package com.mdd.generator.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/***
 * 表实体
 */
@Data
public class DbTableVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("tableName")
    private String tableName;     // 表的名称
    @JsonProperty("tableComment")
    private String tableComment;  // 表的描述
    @JsonProperty("authorName")
    private String authorName;    // 作者名称
    @JsonProperty("createTime")
    private String createTime;    // 创建时间
    @JsonProperty("updateTime")
    private String updateTime;    // 更新时间

}
