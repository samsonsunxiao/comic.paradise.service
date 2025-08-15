package com.mdd.common.mapper.system;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.system.SystemMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统菜单Mapper
 */
@Mapper
public interface SystemMenuMapper extends IBaseMapper<SystemMenu> {

    @Select({"<script>",
            " SELECT ",
            " perms ",
            " FROM la_system_menu WHERE perms != '' AND id in ",
            "<foreach item='item' index='index' collection='items' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    List<String> getPerms(@Param("items") List<Integer> menuIds);




}
