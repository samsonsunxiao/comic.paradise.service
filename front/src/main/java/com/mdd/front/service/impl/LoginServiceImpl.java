package com.mdd.front.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.mdd.common.cache.ScanLoginCache;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.user.UserAuth;
import com.mdd.common.entity.user.UserSession;
import com.mdd.common.enums.ErrorEnum;
import com.mdd.common.enums.LoginEnum;
import com.mdd.common.enums.NoticeEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.user.UserAuthMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.mapper.user.UserSessionMapper;
import com.mdd.common.plugin.notice.NoticeCheck;
import com.mdd.common.plugin.wechat.WxMnpDriver;
import com.mdd.common.util.*;
import com.mdd.front.cache.TokenLoginCache;
import com.mdd.front.service.ILoginService;
import com.mdd.front.validate.login.LoginPwdValidate;
import com.mdd.front.vo.login.LoginTokenVo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpOAuth2ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 登录服务实现类
 */
@Slf4j
@Service
public class LoginServiceImpl implements ILoginService {

    @Resource
    UserMapper userMapper;

    @Resource
    UserAuthMapper userAuthMapper;
    @Resource
    UserSessionMapper userSessionMapper;

    /**
     * 注册账号
     *
     * @author fzr
     * @param account  账号
     * @param password 密码
     * @param terminal 总端
     */
    @Override
    public void register(String account, String password, Integer terminal) {
        User model = userMapper.selectOne(new QueryWrapper<User>()
                .select("id,sn,account")
                .eq("account", account)
                .isNull("delete_time")
                .last("limit 1"));

        Assert.isNull(model, "账号已存在,换一个吧!");

        Integer sn = this.__generateSn();
        // String salt = ToolUtils.randomString(5);
        String pwd = ToolUtils.makePassword(password);

        User user = new User();
        user.setSn(sn);
        user.setNickname("用户" + sn);
        user.setAccount(account);
        user.setPassword(pwd);

        String defaultAvatar = ConfigUtils.get("default_image", "user_avatar", "/api/static/default_avatar.png");

        user.setAvatar(defaultAvatar);
        user.setChannel(terminal);
        user.setIsNewUser(1);
        user.setCreateTime(System.currentTimeMillis() / 1000);
        user.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.insert(user);
    }

    /**
     * 账号登录
     *
     * @author fzr
     * @return LoginTokenVo
     */
    @Override
    @Transactional
    public LoginTokenVo accountLogin(LoginPwdValidate loginPwdValidate) {
        JSONArray config = JSONArray.parseArray(ConfigUtils.get("login", "login_way", "[]"));
        if (config.contains(loginPwdValidate.getScene().toString()) == false) {
            throw new OperateException("不支持的登录方式");
        }

        String account = loginPwdValidate.getAccount();
        String password = loginPwdValidate.getPassword();
        Integer terminal = loginPwdValidate.getTerminal();
        String code = loginPwdValidate.getCode();
        Integer sceneId = loginPwdValidate.getScene();
        // 账号密码登录
        if (loginPwdValidate.getScene().equals(LoginEnum.ACCOUNT_PASSWORD.getCode())) {
            User user = userMapper.selectOne(new QueryWrapper<User>()
                    .nested(wq -> wq
                            .eq("account", account).or()
                            .eq("mobile", account).or())
                    .isNull("delete_time")
                    .last("limit 1"));

            Assert.notNull(user, "账号不存在!");
            String pwd = ToolUtils.makePassword(password);
            Assert.isFalse(!pwd.equals(user.getPassword()), "账号或密码错误!");
            Assert.isFalse(!user.getIsDisable().equals(0), "账号已被禁用!");

            return this.__loginToken(user.getId(), user.getMobile(), user.getIsNewUser(), terminal);
        } else {
            return mobileLogin(account, code, terminal, sceneId);
        }
    }

    /**
     * 手机号登录
     *
     * @author fzr
     * @param mobile 手机号
     * @param code   验证码
     * @return LoginTokenVo
     */
    @Override
    public LoginTokenVo mobileLogin(String mobile, String code, Integer terminal, Integer sceneId) {
        // 校验验证码
        int sceneCode = NoticeEnum.LOGIN_CAPTCHA.getCode();
        if (!NoticeCheck.verify(sceneCode, code, mobile)) {
            throw new OperateException("验证码错误!");
        }

        // 查询手机号
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .select("id,account,mobile,is_disable,is_new_user")
                .eq("mobile", mobile)
                .isNull("delete_time")
                .last("limit 1"));

        Assert.notNull(user, "账号不存在!");
        Assert.isFalse(user.getIsDisable() != 0, "账号已禁用!");

        return this.__loginToken(user.getId(), user.getMobile(), user.getIsNewUser(), terminal);
    }

    /**
     * 微信小程序登录
     *
     * @author fzr
     * @param code     编码
     * @param terminal 终端
     * @return LoginTokenVo
     */
    @Override
    @Transactional
    public LoginTokenVo mnpLogin(String code, Integer terminal) {
        try {
            WxMaService wxMaService = WxMnpDriver.mnp();
            WxMaJscode2SessionResult sessionResult = wxMaService.getUserService().getSessionInfo(code);
            String openId = sessionResult.getOpenid();
            String uniId = sessionResult.getUnionid();
            String unionId = uniId == null ? "0" : uniId;

            return this.__wxLoginHandle(openId, unionId, "", "", terminal);
        } catch (WxErrorException e) {
            throw new OperateException(e.getError().getErrorCode() + ", " + e.getError().getErrorMsg());
        }
    }

    /**
     * 公众号登录
     *
     * @author fzr
     * @param code     编码
     * @param terminal 终端
     * @return LoginTokenVo
     */
    @Override
    public LoginTokenVo officeLogin(String code, Integer terminal) {
        try {
            WxMpService wxMpService = WxMnpDriver.oa();
            WxOAuth2AccessToken wxOAuth2AccessToken = wxMpService.getOAuth2Service().getAccessToken(code);
            String uniId = wxOAuth2AccessToken.getUnionId();
            String openId = wxOAuth2AccessToken.getOpenId();
            String unionId = uniId == null ? "0" : uniId;

            String avatar = "";
            String nickname = "";
            try {
                String accessToken = wxOAuth2AccessToken.getAccessToken();
                String userInfoUri = UrlUtils.getWeixinApiUrl() + "/sns/userinfo?access_token=%s&openid=%s";
                String userInfoUrl = String.format(userInfoUri, accessToken, openId);
                String resultInfo = HttpUtils.sendGet(userInfoUrl);
                Map<String, String> resultMap = MapUtils.jsonToMap(resultInfo);
                avatar = resultMap.get("headimgurl");
                nickname = resultMap.get("nickname");
            } catch (Exception ignored) {
            }

            return this.__wxLoginHandle(openId, unionId, avatar, nickname, terminal);
        } catch (WxErrorException e) {
            throw new OperateException(e.getError().getErrorCode() + ", " + e.getError().getErrorMsg());
        }
    }

    /**
     * 公众号跳转url
     *
     * @author fzr
     * @param url 连接
     * @return String
     */
    @Override
    public String oaCodeUrl(String url) {
        WxMpService wxMpService = WxMnpDriver.oa();
        WxMpOAuth2ServiceImpl wxMpOAuth2Service = new WxMpOAuth2ServiceImpl(wxMpService);
        String state = ToolUtils.makeMd5(ToolUtils.makeToken());
        return wxMpOAuth2Service.buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, state);
    }

    @Override
    public Map<String, Object> oaQrcode(String uniqueId) {
        String scanCache = ScanLoginCache.get(uniqueId, false);
        if (scanCache.isEmpty()) {
            return generalQrcodeUrl();
        } else {
            Map<String, Object> scanMap = MapUtils.jsonToMapAsObj(scanCache);
            scanMap.remove("openid");
            return scanMap;
        }
    }

    private Map<String, Object> generalQrcodeUrl() {
        try {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            String accessToken = WxMnpDriver.getAccessTokens();
            if (accessToken.isEmpty()) {
                return resultMap;
            }
            String uniqueId = ToolUtils.makeToken();
            String qrcodeUrl = UrlUtils.getWeixinApiUrl() + "/cgi-bin/qrcode/create?access_token=" + accessToken;
            Map<String, Object> param = new HashMap<>();
            param.put("expire_seconds", 604800);
            param.put("action_name", "QR_STR_SCENE");
            Map<String, Object> scene = new HashMap<>();
            scene.put("scene_str", uniqueId);
            Map<String, Object> actionInfo = new HashMap<>();
            actionInfo.put("scene", scene);
            param.put("action_info", actionInfo);
            String result = HttpUtils.sendPost(qrcodeUrl, JSONObject.toJSONString(param));
            Map<String, String> ticketMap = MapUtils.jsonToMap(result);
            String ticket = ticketMap.get("ticket");
            String qrcode = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
            // 写入缓存
            resultMap.put("status", 1);// 1 表示生成公众号临时二维码ticket成功
            resultMap.put("qrcode", qrcode);
            resultMap.put("uniqueid", uniqueId);
            ScanLoginCache.set(uniqueId, JSON.toJSONString(resultMap));
            return resultMap;
        } catch (Exception e) {
            throw new OperateException(e.getMessage(), ErrorEnum.FAILED.getCode(), -1);
        }
    }

    @Override
    public LoginTokenVo oaQrLogin(String uniqueId, Integer terminal) {
        String scanCache = ScanLoginCache.get(uniqueId);
        Map<String, Object> scanMap = MapUtils.jsonToMapAsObj(scanCache);
        if (scanMap == null || scanMap.isEmpty()) {
            throw new OperateException("二维码已失效或不存在,请重新操作");
        }
        QueryWrapper<UserAuth> queryWrapper = new QueryWrapper<UserAuth>();
        queryWrapper = new QueryWrapper<UserAuth>()
                .nested(wq -> wq
                        .eq("openid", scanMap.get("openid").toString()))
                .last("limit 1");
        UserAuth userAuth = userAuthMapper.selectOne(queryWrapper);

        // 查询用户
        User user = null;
        if (StringUtils.isNotNull(userAuth)) {
            user = userMapper.selectOne(new QueryWrapper<User>()
                    .isNull("delete_time")
                    .eq("id", userAuth.getUserId())
                    .last("limit 1"));
        }
        if (user == null) {
            throw new OperateException("找不到用户信息");
        }
        return this.__loginToken(user.getId(), user.getMobile(), user.getIsNewUser(), terminal);
    }

    /**
     * 扫码链接
     *
     * @author fzr
     * @param session session
     * @return String
     */
    @Override
    public String scanCodeUrl(String url, HttpSession session) {
        // 获取AppId
        String appId = ConfigUtils.get("open_platform", "app_id", "");

        // 微信开放平台授权
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        // 回调地址
        String redirectUrl = url;
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new OperateException(e.getMessage());
        }

        // 防止csrf攻击
        String state = ToolUtils.makeUUID().replaceAll("-", "");
        ScanLoginCache.set(session.getId(), state);
        System.out.println(baseUrl);
        System.out.println(appId);
        System.out.println(redirectUrl);
        System.out.println(state);
        System.out.println(String.format(baseUrl, appId, redirectUrl, state));
        // 生成QrcodeUrl
        return String.format(baseUrl, appId, redirectUrl, state);
    }

    @Override
    public void qrcodeState(String state, HttpSession session) {
        ScanLoginCache.set(session.getId(), state);
    }

    /**
     * 扫码登录
     *
     * @author fzr
     * @param code     编码
     * @param state    标识
     * @param terminal 终端
     * @param session  会话
     */
    @Override
    public LoginTokenVo scanLogin(String code, String state, Integer terminal, HttpSession session) {
        if (!ScanLoginCache.get(session.getId()).equals(state)) {
            throw new OperateException("二维码已失效或不存在,请重新操作");
        }

        // 得到配置和授权临时票据code
        String appId = ConfigUtils.get("open_platform", "app_id", "");
        String appSecret = ConfigUtils.get("open_platform", "app_secret", "");

        // 向认证服务器发送请求换取access_token
        String baseAccessTokenUrl = UrlUtils.getWeixinApiUrl() + "/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        Map<String, String> resultMap;
        try {
            String accessTokenUrl = String.format(baseAccessTokenUrl, appId, appSecret, code);
            String result = HttpUtils.sendGet(accessTokenUrl);
            resultMap = MapUtils.jsonToMap(result);
        } catch (Exception e) {
            throw new OperateException("获取access_token失败:" + e.getMessage());
        }

        // 访问微信获取用户信息 (openId,unionId,昵称,头像等)
        String accessToken = resultMap.get("access_token");
        String openid = resultMap.get("openid");
        String baseUserInfoUrl = UrlUtils.getWeixinApiUrl() + "/sns/userinfo?access_token=%s&openid=%s";
        String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
        Map<String, String> userinfoMap;
        try {
            String resultUserInfo = HttpUtils.sendGet(userInfoUrl);
            userinfoMap = MapUtils.jsonToMap(resultUserInfo);
        } catch (Exception e) {
            throw new OperateException("获取用户信息失败:" + e.getMessage());
        }

        String openId = userinfoMap.get("openid");
        String uniId = userinfoMap.get("unionid");
        String unionId = uniId == null ? "0" : uniId;
        String avatar = userinfoMap.getOrDefault("headimgurl", "");
        String nickname = userinfoMap.getOrDefault("nickname", "");

        return this.__wxLoginHandle(openId, unionId, avatar, nickname, terminal);
    }

    @Override
    public void logout() {
        TokenLoginCache.del();
    }

    /**
     * 处理微信登录
     *
     * @param openId   (openId)
     * @param unionId  (unionId)
     * @param terminal (terminal)
     * @param avatar   (用户头像)
     * @param nickname (用户昵称)
     * @return LoginTokenVo
     */
    private LoginTokenVo __wxLoginHandle(String openId, String unionId, String avatar, String nickname,
            Integer terminal) {

        // 查询授权
        QueryWrapper<UserAuth> queryWrapper = new QueryWrapper<UserAuth>();

        if (StringUtils.equals(unionId, "0") == false) {
            queryWrapper = new QueryWrapper<UserAuth>()
                    .nested(wq -> wq
                            .eq("unionid", unionId).or()
                            .eq("openid", openId))
                    .last("limit 1");
        } else {
            queryWrapper = new QueryWrapper<UserAuth>()
                    .nested(wq -> wq
                            .eq("openid", openId))
                    .last("limit 1");
        }
        UserAuth userAuth = userAuthMapper.selectOne(queryWrapper);

        // 查询用户
        User user = null;
        if (StringUtils.isNotNull(userAuth)) {
            user = userMapper.selectOne(new QueryWrapper<User>()
                    .isNull("delete_time")
                    .eq("id", userAuth.getUserId())
                    .last("limit 1"));
        }

        String defaultAvatar = ConfigUtils.get("default_image", "user_avatar", "/api/static/default_avatar.png");
        // 创建用户
        if (StringUtils.isNull(user)) {
            Integer sn = this.__generateSn();
            // user.setAvatar(defaultAvatar);
            String defaultNickname = "用户" + sn;
            if (StringUtils.isNotEmpty(avatar)) {
                try {
                    Long time = System.currentTimeMillis();
                    String date = TimeUtils.millisecondToDate(time, "yyyyMMdd");
                    String name = ToolUtils.makeMd5(ToolUtils.makeUUID() + time) + ".jpg";
                    String path = "avatar" + date + "/" + name;
                    String savePath = YmlUtils.get("app.upload-directory") + path;
                    ToolUtils.download(avatar, savePath);
                    defaultAvatar = path;
                } catch (IOException ignored) {
                }
            }

            if (StringUtils.isNotEmpty(nickname)) {
                defaultNickname = nickname;
            }

            User model = new User();
            model.setSn(sn);
            model.setAvatar(defaultAvatar);
            model.setNickname(defaultNickname);
            model.setAccount("u" + sn);
            model.setChannel(terminal);
            model.setSex(0);
            model.setLoginIp(IpUtils.getHostIp());
            model.setLoginTime(System.currentTimeMillis() / 1000);
            model.setUpdateTime(System.currentTimeMillis() / 1000);
            model.setCreateTime(System.currentTimeMillis() / 1000);
            model.setIsNewUser(1);
            userMapper.insert(model);
            user = model;
        }

        // 终端授权
        UserAuth auth = userAuthMapper.selectOne(
                new QueryWrapper<UserAuth>()
                        .eq("openid", openId)
                        // .eq("terminal", terminal)
                        .last("limit 1"));

        // 创建授权
        if (StringUtils.isNull(auth)) {
            UserAuth authModel = new UserAuth();
            authModel.setUserId(user.getId());
            authModel.setUnionid(unionId);
            authModel.setOpenid(openId);
            authModel.setTerminal(terminal);
            authModel.setCreateTime(System.currentTimeMillis() / 1000);
            authModel.setUpdateTime(System.currentTimeMillis() / 1000);
            userAuthMapper.insert(authModel);
        } else if (StringUtils.isEmpty(auth.getUnionid())) {
            auth.setUnionid(unionId);
            auth.setUpdateTime(System.currentTimeMillis() / 1000);
            userAuthMapper.updateById(auth);
        }

        return this.__loginToken(user.getId(), user.getMobile(), user.getIsNewUser(), terminal);
    }

    /**
     * 处理录令牌
     *
     * @author fzr
     * @param userId   用户ID
     * @param mobile   用户手机
     * @param terminal 终端
     * @return LoginTokenVo
     */
    private LoginTokenVo __loginToken(Integer userId, String mobile, Integer isNew, Integer terminal) {
        // 实现账号登录
        // StpUtil.login(userId);

        String token = ToolUtils.makeToken();
        TokenLoginCache.set(token, userId, String.valueOf(terminal));

        // 更新登录信息
        User user = new User();
        user.setLoginIp(IpUtils.getHostIp());
        user.setLoginTime(System.currentTimeMillis() / 1000);
        userMapper.update(user, new QueryWrapper<User>().eq("id", userId));

        // 返回登录信息
        LoginTokenVo vo = new LoginTokenVo();
        vo.setId(userId);
        vo.setIsBindMobile(!StringUtils.isEmpty(mobile));
        vo.setToken(token);
        vo.setIsNew(isNew);
        vo.setMobile(mobile);

        // 保存登录信息到session
        userSessionMapper.delete(new QueryWrapper<UserSession>().eq("user_id", userId).eq("terminal", terminal));

        UserSession userSession = new UserSession();
        userSession.setUserId(userId);
        userSession.setToken(token);
        userSession.setTerminal(terminal);
        userSession.setUpdateTime(System.currentTimeMillis() / 1000);
        userSession.setExpireTime(System.currentTimeMillis() / 1000 + Long.valueOf(YmlUtils.get("sa-token.timeout")));
        userSessionMapper.insert(userSession);

        return vo;
    }

    /**
     * 生成用户编号
     *
     * @author fzr
     * @return Integer
     */
    private Integer __generateSn() {
        Integer sn;
        while (true) {
            sn = Integer.parseInt(ToolUtils.randomInt(8));
            User snModel = userMapper.selectOne(new QueryWrapper<User>()
                    .select("id,sn")
                    .eq("sn", sn)
                    .last("limit 1"));
            if (snModel == null) {
                break;
            }
        }
        return sn;
    }

}
