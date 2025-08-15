package com.mdd.admin.service.impl;

import com.mdd.admin.service.IChannelH5ConfigService;
import com.mdd.admin.validate.channel.ChannelH5Validate;
import com.mdd.admin.vo.channel.ChannelH5Vo;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.YmlUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * H5渠道设置服务实现类
 */
@Service
public class ChannelH5ConfigServiceImpl implements IChannelH5ConfigService {

    /**
     * H5渠道配置详情
     *
     * @author fzr
     * @return ChannelH5Vo
     */
    @Override
    public ChannelH5Vo getConfig() {
        Map<String, String> config = ConfigUtils.get("web_page");
        ChannelH5Vo vo = new ChannelH5Vo();
        vo.setStatus(Integer.parseInt(config.getOrDefault("status", "1")));
        vo.setPageStatus(Integer.parseInt(config.getOrDefault("page_status", "1")));

        String url = YmlUtils.get("like.front-url");

        url = StringUtils.isNotEmpty(url) && url.endsWith("/") ? url + "/moblie" : url + "moblie";

        vo.setUrl(config.getOrDefault("url", url));
        vo.setPageUrl(config.getOrDefault("page_url", ""));
        return vo;
    }

    /**
     * H5渠道配置保存
     *
     * @author fzr
     * @param channelH5Validate 参数
     */
    @Override
    public void setConfig(ChannelH5Validate channelH5Validate) {
        ConfigUtils.set("web_page", "status", String.valueOf(channelH5Validate.getStatus()));
        ConfigUtils.set("web_page", "page_status", String.valueOf(channelH5Validate.getPageStatus()));
        ConfigUtils.set("web_page", "url", channelH5Validate.getUrl());
        ConfigUtils.set("web_page", "page_url", channelH5Validate.getPageUrl());
    }

}
