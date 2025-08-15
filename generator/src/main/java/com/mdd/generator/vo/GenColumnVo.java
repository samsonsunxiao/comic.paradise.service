package com.mdd.generator.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/***
 * 列实体
 */
@Data
public class GenColumnVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;             // 字段主键
    @JsonProperty("columnName")
    private String columnName;      // 字段名称
    @JsonProperty("columnComment")
    private String columnComment;   // 字段描述
    @JsonProperty("columnLength")
    private Integer columnLength;   // 字段长度
    @JsonProperty("columnType")
    private String columnType;      // 字段类型
    @JsonProperty("javaType")
    private String javaType;        // JAVA类型
    @JsonProperty("javaField")
    private String javaField;       // JAVA字段
    @JsonProperty("isRequired")
    private Integer isRequired;     // 是否必填
    @JsonProperty("isInsert")
    private Integer isInsert;       // 是否插入字段
    @JsonProperty("isEdit")
    private Integer isEdit;         // 是否编辑字段
    @JsonProperty("isList")
    private Integer isList;         // 是否列表字段
    @JsonProperty("isQuery")
    private Integer isQuery;        // 是否查询字段
    @JsonProperty("queryType")
    private String queryType;       // 查询方式: [等于、不等于、大于、小于、范围]
    @JsonProperty("htmlType")
    private String htmlType;        // 显示类型: [文本框、文本域、下拉框、复选框、单选框、日期控件]
    @JsonProperty("dictType")
    private String dictType;        // 字典类型
    @JsonProperty("createTime")
    private String createTime;      // 创建时间
    @JsonProperty("updateTime")
    private String updateTime;      // 更新时间

}
