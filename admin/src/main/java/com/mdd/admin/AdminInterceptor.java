package com.mdd.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.admin.IAdminRoleService;
import com.mdd.common.aop.NotPower;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.entity.admin.Admin;
import com.mdd.common.enums.ErrorEnum;
import com.mdd.common.exception.LoginException;
import com.mdd.common.mapper.admin.AdminMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.YmlUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 拦截器
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Resource
    AdminMapper systemAuthAdminMapper;

    @Resource
    IAdminRoleService iAdminRoleService;

    /**
     * 前置处理器
     *
     * @param request 请求
     * @param response 响应
     * @param handler 处理
     * @return boolean
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {

        // 请求方法类型
        String reqUri = request.getRequestURI();
        if (!(handler instanceof HandlerMethod) || ( !reqUri.startsWith("/api") &&  !reqUri.startsWith("/adminapi"))) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        // 登录权限校验
        try {
            response.setContentType("application/json;charset=utf-8");
            Method method = this.obtainAop(handler);
            this.checkLogin(method, reqUri);
        } catch (LoginException e) {
            AjaxResult<Object> result = AjaxResult.failed(e.getCode(), e.getMsg());
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        // 演示环境拦截
        String env = YmlUtils.get("like.production");
        if (StringUtils.isNotNull(env) && env.equals("true")) {
            String prefix = "/adminapi/";
            String route = request.getRequestURI().replaceFirst(prefix, "");
//            String auths = route.replace("/", ":");
            String auths = route;
            List<String> ignoreUrl = Arrays.asList("login/account", "login/logout");
            if (request.getMethod().equals("POST") && !ignoreUrl.contains(auths)) {
                String message = "演示环境不支持修改数据，请下载源码本地部署体验";
                AjaxResult<Object> result = AjaxResult.failed(ErrorEnum.NO_PERMISSION.getCode(), message);
                response.getWriter().print(JSON.toJSONString(result));
                return false;
            }
        }

        // 验证通过继续
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 后置处理器
     *
     * @param request 请求
     * @param response 响应
     * @param handler 处理
     * @param ex 异常
     * @throws Exception 异常
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) throws Exception {
        AdminThreadLocal.remove();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    /**
     * 提取注解
     *
     * @param handler 处理器
     * @return Method
     * @throws Exception 异常
     */
    private Method obtainAop(@NotNull Object handler) throws Exception {
        String[] objArr = handler.toString().split("#");
        String methodStr = objArr[1].split("\\(")[0];
        String classStr = objArr[0];
        Class<?> clazz = Class.forName(classStr);

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodStr)) {
                return method;
            }
        }

        return null;
    }

    /**
     * 登录验证
     *
     * @param method 方法类
     * @author fzr
     */
    private void checkLogin(Method method, String reqUri) {
        for (int i=0; i<=0; i++) {
            // 免登校验
            if (StringUtils.isNotNull(method) && method.isAnnotationPresent(NotLogin.class)) {
                break;
            }

            // 令牌校验
            String token = StpUtil.getTokenValue();
            if (StringUtils.isNull(token) || StringUtils.isBlank(token)) {
                Integer errCode = ErrorEnum.TOKEN_EMPTY.getCode();
                String errMsg = ErrorEnum.TOKEN_EMPTY.getMsg();
                throw new LoginException(errCode, errMsg);
            }

            // 登录校验
            Object id = StpUtil.getLoginId();
            if (StringUtils.isNull(id)) {
                Integer errCode = ErrorEnum.TOKEN_INVALID.getCode();
                String errMsg = ErrorEnum.TOKEN_INVALID.getMsg();
                throw new LoginException(errCode, errMsg);
            }

            // 用户校验
            Admin adminUser = systemAuthAdminMapper.selectOne(
                    new QueryWrapper<Admin>()
                        .select("id,name,disable")
                        .eq("id", Integer.parseInt(id.toString()))
                            .isNull("delete_time")
                        .last("limit 1"));

            // 删除校验
            if (StringUtils.isNull(adminUser)) {
                Integer errCode = ErrorEnum.TOKEN_INVALID.getCode();
                String errMsg = ErrorEnum.TOKEN_INVALID.getMsg();
                throw new LoginException(errCode, errMsg);
            }

            // 禁用校验
            if (adminUser.getDisable().equals(1)) {
                Integer errCode = ErrorEnum.LOGIN_DISABLE_ERROR.getCode();
                String errMsg = ErrorEnum.LOGIN_DISABLE_ERROR.getMsg();
                throw new LoginException(errCode, errMsg);
            }

            // 写入线程
            AdminThreadLocal.put("adminId", id);
            AdminThreadLocal.put("username", adminUser.getName());
            String roleIds = StringUtils.join(iAdminRoleService.getRoleIdAttr(Integer.parseInt(String.valueOf(id))), ",");
            AdminThreadLocal.put("roleIds", roleIds);

            // 权限校验
            if (!adminUser.getId().equals(1)) {
                this.checkAuth(method, reqUri);
            }
        }
    }

    /**
     * 权限验证
     *
     * @param method 方法类
     * @param uri 请求路由
     * @author fzr
     */
    private void checkAuth(Method method, String uri) {
        for (int i=0; i<=0; i++) {
            // 免权限校验
            if (StringUtils.isNotNull(method) && method.isAnnotationPresent(NotPower.class)) {
                break;
            }

            // 路由转权限
            String prefix = "/adminapi/";
            String route = uri.replaceFirst(prefix, "");
//            String auths = route.replace("/", ":");
            String auths = route;
            // 校验权限
            StpUtil.checkPermission(auths);
        }
    }
}
