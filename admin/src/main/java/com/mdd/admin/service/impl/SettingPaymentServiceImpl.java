package com.mdd.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.ISettingPaymentService;
import com.mdd.admin.validate.setting.SettingPayConfigValidate;
import com.mdd.admin.validate.setting.SettingPayMethodValidate;
import com.mdd.admin.vo.setting.SettingPaymentMethodVo;
import com.mdd.common.entity.setting.DevPayConfig;
import com.mdd.common.entity.setting.DevPayWay;
import com.mdd.common.enums.PaymentEnum;
import com.mdd.common.mapper.setting.DevPayConfigMapper;
import com.mdd.common.mapper.setting.DevPayWayMapper;
import com.mdd.common.util.MapUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * 支付配置服务实现类
 */
@Service
public class SettingPaymentServiceImpl implements ISettingPaymentService {

    @Resource
    DevPayConfigMapper devPayConfigMapper;

    @Resource
    DevPayWayMapper devPayWayMapper;

    /**
     * 支付配置方式
     *
     * @author fzr
     * @return List<List<SettingPaymentMethodVo>>
     */
    public HashMap getPayWay() {
        List<DevPayWay> lists = devPayWayMapper.selectList(null);

        //List<List<SettingPaymentMethodVo>> result = new LinkedList<>();
        HashMap result = new LinkedHashMap<Integer, String>();
        List<SettingPaymentMethodVo> mnp = new LinkedList<>();
        List<SettingPaymentMethodVo> oa = new LinkedList<>();
        List<SettingPaymentMethodVo> h5 = new LinkedList<>();
        for (DevPayWay devPayWay : lists) {
            DevPayConfig devPayConfig = devPayConfigMapper.selectOne(
                    new QueryWrapper<DevPayConfig>()
                        .eq("id", devPayWay.getPayConfigId())
                        .last("limit 1"));

            SettingPaymentMethodVo vo = new SettingPaymentMethodVo();
            BeanUtils.copyProperties(devPayWay, vo);
            vo.setPayWayName(PaymentEnum.getPayWayMsg(devPayConfig.getPayWay()));
            vo.setIcon(UrlUtils.toAdminAbsoluteUrl(devPayConfig.getIcon()));

            switch (devPayWay.getScene()) {
                case 1: // 微信小程序
                    mnp.add(vo);
                    break;
                case 2: // 微信公众号
                    oa.add(vo);
                    break;
                case 3:
                    h5.add(vo);
                    break;
            }
        }

        result.put(1, mnp);
        result.put(2, oa);
        result.put(3, h5);
        return result;
    }

    /**
     * 支付配置列表
     *
     * @author fzr
     * @return List<DevPayConfig>
     */
    @Override
    public JSONObject list() {
        List<DevPayConfig> devPayConfigs = devPayConfigMapper.selectList(
                new QueryWrapper<DevPayConfig>()
                    .orderByDesc(Arrays.asList("sort", "id")));

        for (DevPayConfig dev : devPayConfigs) {
            dev.setPayWayName(PaymentEnum.getPayWayMsg(dev.getPayWay()));
            dev.setName(dev.getName());
            dev.setConfig(JSONObject.parse("{}"));
            dev.setIcon(UrlUtils.toAdminAbsoluteUrl(dev.getIcon()));
        }

        JSONObject result = new JSONObject() {{
            put("lists", devPayConfigs);
        }};
        return result;
    }

    /**
     * 支付配置详情
     *
     * @author fzr
     * @param id 主键
     * @return DevPayConfig
     */
    @Override
    public DevPayConfig getConfig(Integer id) {
        DevPayConfig devPayConfig = devPayConfigMapper.selectOne(
                new QueryWrapper<DevPayConfig>()
                        .eq("id", id)
                        .last("limit 1"));

        devPayConfig.setName(PaymentEnum.getPayWayMsg(devPayConfig.getPayWay()));
        devPayConfig.setPayWayName(devPayConfig.getName());
        devPayConfig.setConfig(JSONObject.parse(devPayConfig.getConfig().toString()));
        devPayConfig.setIcon(UrlUtils.toAdminAbsoluteUrl(devPayConfig.getIcon()));
        return devPayConfig;
    }

    /**
     * 支付配置编辑
     *
     * @author fzr
     * @param configValidate 参数
     */
    @Override
    public void setConfig(SettingPayConfigValidate configValidate) {
        DevPayConfig devPayConfig = devPayConfigMapper.selectOne(
                new QueryWrapper<DevPayConfig>()
                        .eq("id", configValidate.getId())
                        .last("limit 1"));

        Assert.notNull(devPayConfig, "数据不存在!");

        devPayConfig.setName(configValidate.getName());
        devPayConfig.setIcon(UrlUtils.toRelativeUrl(configValidate.getIcon()));
        devPayConfig.setSort(configValidate.getSort());
        devPayConfig.setRemark(configValidate.getRemark());
        if (devPayConfig.getPayWay().equals(1)) {
            devPayConfig.setConfig("{}");
        } else {
            devPayConfig.setConfig(configValidate.getConfig().toJSONString());
        }
        devPayConfigMapper.updateById(devPayConfig);
    }

    /**
     * 支付方式编辑
     *
     * @author fzr
     * @param data 参数
     */
    @Override
    @Transactional
    public void setPayWay(HashMap<Integer, List<SettingPaymentMethodVo>> data) {
        for (Integer key : data.keySet()) {
            List<SettingPaymentMethodVo> list = data.get(key);
            for (SettingPaymentMethodVo vo : list) {
                DevPayWay way = new DevPayWay();
                way.setIsDefault(vo.getIsDefault());
                way.setStatus(vo.getStatus());
                devPayWayMapper.update(way, new QueryWrapper<DevPayWay>().eq("id", vo.getId()));
            }
        }
    }

}
