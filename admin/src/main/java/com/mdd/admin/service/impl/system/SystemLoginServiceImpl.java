package com.mdd.admin.service.impl.system;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.cache.CaptchaCache;
import com.mdd.admin.service.system.ISystemLoginService;
import com.mdd.admin.service.system.ISystemRoleService;
import com.mdd.admin.validate.system.SystemAdminLoginsValidate;
import com.mdd.admin.vo.system.SystemCaptchaVo;
import com.mdd.admin.vo.system.SystemLoginVo;
import com.mdd.common.entity.admin.Admin;
import com.mdd.common.entity.system.SystemLogLogin;
import com.mdd.common.enums.ErrorEnum;
import com.mdd.common.exception.LoginException;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.admin.AdminMapper;
import com.mdd.common.mapper.system.SystemLogLoginMapper;
import com.mdd.common.util.*;
import com.google.code.kaptcha.Producer;
import nl.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * 系统登录服务实现类
 */
@Service
public class SystemLoginServiceImpl implements ISystemLoginService {

    @Resource
    ISystemRoleService iSystemRoleService;
    @Resource
    Producer captchaProducer;

    @Resource
    SystemLogLoginMapper systemLogLoginMapper;

    @Resource
    AdminMapper adminMapper;

    private static final Logger log = LoggerFactory.getLogger(SystemLoginServiceImpl.class);

    /**
     * 验证码
     *
     * @author fzr
     * @return SystemCaptchaVo
     */
    @Override
    public SystemCaptchaVo captcha() {
        // 验证码信息
        String capStr, code;
        BufferedImage image;
        String uuid = ToolUtils.makeUUID();

        // 生成验证码
        capStr = code = captchaProducer.createText();
        image = captchaProducer.createImage(capStr);
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            log.error("verifyCode Error:" + e.getMessage());
            throw new OperateException(e.getMessage());
        }

        // 缓存验证码
        CaptchaCache.set(code, uuid);

        // 返回验证码
        String base64 = "data:image/jpeg;base64,"+ Base64Util.encode(os.toByteArray());
        SystemCaptchaVo vo =  new SystemCaptchaVo();
        vo.setUuid(uuid);
        vo.setImg(base64);
        return vo;
    }

    /**
     * 登录
     *
     * @author fzr
     * @param loginsValidate 登录参数
     * @return SystemLoginVo
     */
    @Override
    public SystemLoginVo login(SystemAdminLoginsValidate loginsValidate) {
        String username = loginsValidate.getAccount();
        String password = loginsValidate.getPassword();

        String captchaStatus = YmlUtils.get("app.captcha.status");
        if (StringUtils.isNotNull(captchaStatus) && captchaStatus.equals("true")) {
            Assert.notNull(loginsValidate.getCode(), "code参数缺失");
            Assert.notNull(loginsValidate.getUuid(), "uuid参数缺失");
            String code = CaptchaCache.get(loginsValidate.getUuid());
            log.info("login captcha verify: " + loginsValidate.getCode() + ", uuid:" + loginsValidate.getUuid() + ", code:" + code);
            if (!loginsValidate.getCode().equals(code)) {
                 log.error("login captcha verify failed");
                throw new LoginException(ErrorEnum.CAPTCHA_ERROR.getCode(), ErrorEnum.CAPTCHA_ERROR.getMsg());
            }
        }

        Admin sysAdmin = adminMapper.selectOne(new QueryWrapper<Admin>()
                .eq("account", username)
                .last("limit 1"));

        if (StringUtils.isNull(sysAdmin)) {
            throw new OperateException("账号不存在");
        }

        if (sysAdmin.getDisable().equals(1)) {
            this.recordLoginLog(sysAdmin.getId(), loginsValidate.getAccount(), ErrorEnum.FAILED.getMsg());
            throw new LoginException(ErrorEnum.FAILED.getCode(), ErrorEnum.LOGIN_DISABLE_ERROR.getMsg());
        }
        String md5Pwd = ToolUtils.makePassword(password);
        if (!md5Pwd.equals(sysAdmin.getPassword())) {
            this.recordLoginLog(sysAdmin.getId(), loginsValidate.getAccount(), ErrorEnum.FAILED.getMsg());
            throw new LoginException(ErrorEnum.FAILED.getCode(), ErrorEnum.LOGIN_ACCOUNT_ERROR.getMsg());
        }

        try {
            // 禁止多处登录
            if (sysAdmin.getMultipointLogin().equals(0)) {
                StpUtil.logout(sysAdmin.getId());
            }

            // 实现账号登录
            StpUtil.login(sysAdmin.getId());

            // 更新登录信息
            sysAdmin.setLoginIp(IpUtils.getIpAddress());
            sysAdmin.setLoginTime(System.currentTimeMillis() / 1000);
            adminMapper.updateById(sysAdmin);

            // 记录登录日志
//            this.recordLoginLog(sysAdmin.getId(), loginsValidate.getAccount(), "");

            // 响应登录信息
            String defaultAvatar = "/api/static/backend_avatar.png";
            String avatar = StringUtils.isNotEmpty(sysAdmin.getAvatar()) ? UrlUtils.toRelativeUrl(sysAdmin.getAvatar()) : defaultAvatar;
            SystemLoginVo vo = new SystemLoginVo();
            vo.setId(sysAdmin.getId());
            vo.setName(sysAdmin.getName());
            vo.setAvatar(UrlUtils.toAdminAbsoluteUrl(avatar));
            vo.setToken(StpUtil.getTokenValue());
            vo.setRoleName(ListUtils.listToStringByStr(iSystemRoleService.getRoleNameByAdminId(sysAdmin.getId()), "/"));
            return vo;
        } catch (Exception e) {
            Integer adminId = StringUtils.isNotNull(sysAdmin.getId()) ? sysAdmin.getId() : 0;
            String error = StringUtils.isEmpty(e.getMessage()) ? "未知错误" : e.getMessage();
//            this.recordLoginLog(adminId, loginsValidate.getAccount(), error);
            throw new OperateException(e.getMessage());
        }
    }

    /**
     * 退出
     *
     * @author fzr
     * @param token 令牌
     */
    @Override
    public void logout(String token) {
//        RedisUtil.del(AdminConfig.backstageTokenKey + token);
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(Integer adminId, String username, String error) {
        try {
            HttpServletRequest request = Objects.requireNonNull(RequestUtils.handler());
            final UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

            SystemLogLogin model = new SystemLogLogin();
            model.setAdminId(adminId);
            model.setUsername(username);
            model.setIp(IpUtils.getIpAddress());
            model.setOs(userAgent.getOperatingSystem().getName());
            model.setBrowser(userAgent.getBrowser().getName());
            model.setStatus(StringUtils.isEmpty(error) ? 1 : 0);
            model.setCreateTime(System.currentTimeMillis() / 1000);
            systemLogLoginMapper.insert(model);
        } catch (Exception e) {
            log.error("记录登录日志异常 {}" + e.getMessage());
        }
    }

}
