package com.mdd.generator.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DbColumnVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("columnName")
    private String columnName;      // 字段名称
    @JsonProperty("columnComment")
    private String columnComment;   // 字段描述
    @JsonProperty("columnType")
    private String columnType;      // 字段类型

}
