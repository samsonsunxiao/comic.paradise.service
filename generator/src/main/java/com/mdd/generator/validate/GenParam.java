package com.mdd.generator.validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.common.validator.annotation.IntegerContains;
import com.mdd.common.validator.annotation.StringContains;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生成参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GenParam implements Serializable {

    public interface delete{}

    @IDMust(message = "id参数必传且需大于0")
    private Integer id;

    @NotNull(message = "ids参数缺失", groups = {delete.class})
    private List<Integer> ids;

    @NotNull(message = "tableName参数缺失")
    @NotEmpty(message = "表名称不能为空")
    @Length(min = 1, max = 200, message = "名称不能大于200个字符")
    @JsonProperty("tableName")
    private String tableName;

    @NotNull(message = "entityName参数缺失")
    @NotEmpty(message = "实体类名称不能为空")
    @Length(min = 1, max = 200, message = "实体类名称不能大于200个字符")
    @JsonProperty("entityName")
    private String entityName;

    @NotNull(message = "tableComment参数缺失")
    @NotEmpty(message = "表描述不能为空")
    @Length(min = 1, max = 200, message = "表描述不能大于200个字符")
    @JsonProperty("tableComment")
    private String tableComment;

    @NotNull(message = "authorName参数缺失")
    @NotEmpty(message = "作者名称不能为空")
    @Length(min = 1, max = 100, message = "作者名称不能大于60个字符")
    @JsonProperty("authorName")
    private String authorName;

    @Length(max = 60, message = "备注不能大于200个字符")
    private String remarks;

    @NotNull(message = "genTpl参数缺失")
    @NotEmpty(message = "请选择生成模板")
    @StringContains(values = {"crud", "tree"}, message = "选择的生成模板不符合")
    @JsonProperty("genTpl")
    private String genTpl;

    @NotNull(message = "moduleName参数缺失")
    @NotEmpty(message = "生成模块名不能为空")
    @Length(min = 1, max = 60, message = "生成模块名不能大于60个字符")
    @JsonProperty("moduleName")
    private String moduleName;

    @NotNull(message = "functionName参数缺失")
    @NotEmpty(message = "生成功能名不能为空")
    @Length(min = 1, max = 60, message = "生成功能名不能大于60个字符")
    @JsonProperty("functionName")
    private String functionName;

    @NotNull(message = "genType参数缺失")
    @IntegerContains(values = {0, 1}, message = "选择的生成代码方式不符合")
    @JsonProperty("genType")
    private Integer genType;

    @Length(max = 200, message = "生成代码路径不能大于200个字符")
    @JsonProperty("genPath")
    private String genPath = "/";

    private List<Map<String, String>> column = new ArrayList<>();

    @JsonProperty("treePrimary")
    private String treePrimary = "";
    @JsonProperty("treeParent")
    private String treeParent  = "";
    @JsonProperty("treeName")
    private String treeName  = "";

    @JsonProperty("subTableName")
    private String subTableName = "";
    @JsonProperty("subTableFk")
    private String subTableFk = "";

    @JsonProperty("subTableFr")
    private String subTableFr = "";

    @JsonProperty("menuStatus")
    private Integer menuStatus = 2;
    @JsonProperty("menuPid")
    private Integer menuPid = 0;

    @JsonProperty("menuName")
    private String menuName = "";

}
