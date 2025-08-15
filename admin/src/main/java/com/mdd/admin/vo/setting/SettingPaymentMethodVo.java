package com.mdd.admin.vo.setting;

import lombok.Data;

import java.io.Serializable;

@Data
public class SettingPaymentMethodVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String payWayName;

    private Integer payConfigId;

    private String icon;

    private Integer scene;

    private Integer status;

    private Integer isDefault;

}
