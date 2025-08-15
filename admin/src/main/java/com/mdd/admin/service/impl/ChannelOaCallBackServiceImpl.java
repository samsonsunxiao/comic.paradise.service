package com.mdd.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.IChannelOaCallBackService;
import com.mdd.common.cache.ScanLoginCache;
import com.mdd.common.entity.OfficialAccountReply;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.user.UserAuth;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.OfficialAccountReplyMapper;
import com.mdd.common.mapper.user.UserAuthMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.plugin.wechat.WxMnpDriver;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.HttpUtils;
import com.mdd.common.util.IpUtils;
import com.mdd.common.util.MapUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.ToolUtils;
import com.mdd.common.util.UrlUtils;
import com.mdd.common.util.YmlUtils;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.EventType.SUBSCRIBE;
//import static me.chanjar.weixin.common.api.WxConsts.EventType.SCAN;
//import static me.chanjar.weixin.common.api.WxConsts.EventType.UNSUBSCRIBE;

@Slf4j
@Service
public class ChannelOaCallBackServiceImpl implements IChannelOaCallBackService {


    @Resource
    private OfficialAccountReplyMapper officialAccountReplyMapper;

    @Resource
    UserAuthMapper userAuthMapper;

    @Resource
    UserMapper userMapper;
    /**
     * 服务器验证
     *
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @return String
     */
    @Override
    public String checkSignature(String signature, String timestamp, String nonce, String echostr) {
        WxMpService wxMpService = WxMnpDriver.oa();
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "非法请求";
    }


    /**
     * 消息回复
     *
     * @param requestBody  请求数据
     * @param signature    微信加密签名
     * @param timestamp    时间戳
     * @param nonce        随机数
     * @param encType      加密类型
     * @param msgSignature 加密签名
     * @return String
     */
    @Override
    public String post(String requestBody, String signature, String timestamp, String nonce, String encType, String msgSignature) {

        WxMpService wxMpService = WxMnpDriver.oa();
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String outMsg = null;
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.msgHandler(inMessage);
            if (outMessage == null) {
                return "";
            }
            outMsg = outMessage.toXml();

        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(
                    requestBody,
                    wxMpService.getWxMpConfigStorage(),
                    timestamp,
                    nonce,
                    msgSignature);

            WxMpXmlOutMessage outMessage = this.msgHandler(inMessage);
            if (outMessage == null) {
                return "";
            }
            outMsg = outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage());
        }

        return outMsg;
    }

    /**
     * 消息处理
     *
     * @param wxMessage 微信回调信息
     * @return WxMpXmlOutMessage
     */
    private WxMpXmlOutMessage msgHandler(WxMpXmlMessage wxMessage) {
        try {
            // 文本消息
            if (wxMessage.getMsgType().equals(WxConsts.XmlMsgType.TEXT)) {
                String msg = keyMsg(wxMessage);
                return textBuild(msg, wxMessage);
            }

            // 事件消息
            if (wxMessage.getMsgType().equals(WxConsts.XmlMsgType.EVENT)) {
                handleScanComplete(wxMessage.getEvent(), wxMessage.getEventKey(), wxMessage.getFromUser());
                if (wxMessage.getEvent().equals(SUBSCRIBE)) {
                    // 关注回复
                    String msg = subMsg();
                    return textBuild(msg, wxMessage);
                } 
            }
        } catch (Exception e) {
            throw new OperateException("公众号消息回调错误" + e.getMessage());
        }
        return null;
    }

    /**
     * 返回文本消息
     *
     * @param content   文本内容
     * @param wxMessage 微信回调信息
     * @return WxMpXmlOutMessage
     */
    private WxMpXmlOutMessage textBuild(String content, WxMpXmlMessage wxMessage) {
        return WxMpXmlOutMessage.TEXT().content(content)
                .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                .build();
    }

    /**
     * 关键词回复
     *
     * @param wxMessage 微信回调信息
     * @return String
     */
    private String keyMsg(WxMpXmlMessage wxMessage) {
        List<OfficialAccountReply> oaReplyList = officialAccountReplyMapper.selectList(
                new QueryWrapper<OfficialAccountReply>()
                        .eq("reply_type", 2)
                        .isNull("delete_time")
                        .eq("status", 1)
                        .orderByAsc("id"));

        String msg = null;
        for (OfficialAccountReply oaReply : oaReplyList) {
            // 全匹配
            if (oaReply.getMatchingType() == 1 && oaReply.getKeyword().equals(wxMessage.getContent())) {
                msg = oaReply.getContent();
            }
            // 模糊匹配
            if (oaReply.getMatchingType() == 2 && wxMessage.getContent().contains(oaReply.getKeyword())) {
                msg = oaReply.getContent();
            }
        }

        return msg == null ? defaultMsg() : msg;
    }

    /**
     * 默认回复
     *
     * @return String
     */
    private String defaultMsg() {
        OfficialAccountReply officialAccountReply = officialAccountReplyMapper.selectOne(new QueryWrapper<OfficialAccountReply>()
                .eq("reply_type", 3)
                .isNull("delete_time")
                .eq("status", 1)
                .last("limit 1"));
        if (officialAccountReply == null) {
            return null;
        }
        return officialAccountReply.getContent();
    }

    /**
     * 关注回复内容
     *
     * @return String
     */
    private String subMsg() {
        OfficialAccountReply officialAccountReply = officialAccountReplyMapper.selectOne(new QueryWrapper<OfficialAccountReply>()
                .eq("reply_type", 1)
                .isNull("delete_time")
                .eq("status", 1)
                .last("limit 1"));
        if (officialAccountReply == null) {
            return defaultMsg();
        }
        return officialAccountReply.getContent();
    }

    private void handleScanComplete(String event, String eventKey, String openid) {
        String uniqueId = eventKey;
        Boolean isFirstSubscribe = false;
        if (eventKey.startsWith("qrscene_")){
            isFirstSubscribe = true;//首次关注
            uniqueId = eventKey.replace("qrscene_", "");
        }
        log.info("handleScanComplete Event Key: " + eventKey + " uniqueId:" + uniqueId + " isFirstSubscribe:{}",isFirstSubscribe);
        Map<String, Object> userMap = getOaUserInfo(openid);
        if (userMap == null) {
            return;
        }
        //扫码完成，如果是关注完成注册账号
        if (!completeOaSubscribe(userMap)){
            return;
        } 
        Boolean bSubscribe = userMap.get("subscribe") != null && (Integer) userMap.get("subscribe") == 1;
        //写入扫码完成状态，
        String scanCache = ScanLoginCache.get(uniqueId, false);
        if(scanCache.isEmpty()){
            return;
        }
        Map<String, Object> cacheMap = MapUtils.jsonToMapAsObj(scanCache);
        if (cacheMap.isEmpty()) {
            return;
        }
        String openId = (String) userMap.get("openid");
        cacheMap.put("status", bSubscribe ? 2 : 3);
        cacheMap.put("openid", openId);
        ScanLoginCache.set(uniqueId, JSON.toJSONString(cacheMap));
        
    }

    private Map<String, Object> getOaUserInfo(String openid) {
        try { 
            String accessToken = WxMnpDriver.getAccessTokens();
            if (accessToken.isEmpty()){
                return null;
            }
            String url = UrlUtils.getWeixinApiUrl() + "/cgi-bin/user/info?access_token=" + accessToken + "&openid=" + openid;
            String result = HttpUtils.sendPost(url, null);
            Map<String, Object> resultMap = MapUtils.jsonToMapAsObj(result);
            return resultMap;
        } catch (Exception ignored) {
        }
        return null;
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

    private Boolean completeOaSubscribe(Map<String, Object> oaUserMap) {
        log.info("completeOaSubscribe oaUserMap: {}", oaUserMap);
        if (oaUserMap == null) {
            return false;
        }
        Boolean bSubscribe = oaUserMap.get("subscribe") != null && (Integer) oaUserMap.get("subscribe") == 1;
        String openId = (String) oaUserMap.get("openid");
        if(openId == null){
            return false;
        }
        
        String nickname = (String) oaUserMap.get("nickname");
        String avatar = (String) oaUserMap.get("headimgurl");
        // 查询授权
        QueryWrapper<UserAuth> queryWrapper = new QueryWrapper<UserAuth>();
        queryWrapper = new QueryWrapper<UserAuth>()
            .nested(wq -> wq.eq("openid", openId))
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
        if (!bSubscribe){//取消关注
            //取消授权
            // if (StringUtils.isNotNull(userAuth)) {//没有授权
            //     userAuthMapper.deleteById(userAuth);
            // }
            log.info("completeOaSubscribe 取消关注 {}",userAuth);
            return false;
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
            model.setChannel(2);
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
                        .last("limit 1"));

        // 创建授权
        if (user != null){
            if (StringUtils.isNull(auth)) {
                UserAuth authModel = new UserAuth();
                authModel.setUserId(user.getId());
                authModel.setUnionid("0");
                authModel.setOpenid(openId);
                authModel.setTerminal(2);
                authModel.setCreateTime(System.currentTimeMillis() / 1000);
                authModel.setUpdateTime(System.currentTimeMillis() / 1000);
                userAuthMapper.insert(authModel);
            } else if (StringUtils.isEmpty(auth.getUnionid())) {
                auth.setUnionid("0");
                auth.setUpdateTime(System.currentTimeMillis() / 1000);
                userAuthMapper.updateById(auth);
            }
        }
        return true;
    }

}
