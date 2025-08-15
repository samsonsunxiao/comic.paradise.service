package com.mdd.admin.service.system;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.system.JobsCreateValidate;
import com.mdd.admin.validate.system.JobsSearchValidate;
import com.mdd.admin.validate.system.JobsUpdateValidate;
import com.mdd.admin.vo.system.JobsVo;
import com.mdd.common.core.PageResult;

import java.util.List;

/**
 * 系统岗位服务接口类
 */
public interface IJobsService {

    /**
     * 岗位所有
     *
     * @author fzr
     * @return List<SystemPostVo>
     */
    List<JobsVo> all();

    /**
     * 岗位列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<SystemPostVo>
     */
    PageResult<JobsVo> list(PageValidate pageValidate, JobsSearchValidate searchValidate);

    /**
     * 岗位详情
     *
     * @author fzr
     * @param id 主键
     * @return SystemPostVo
     */
    JobsVo detail(Integer id);

    /**
     * 岗位新增
     *
     * @author fzr
     * @param createValidate 参数
     */
    void add(JobsCreateValidate createValidate);

    /**
     * 岗位编辑
     *
     * @author fzr
     * @param updateValidate 参数
     */
    void edit(JobsUpdateValidate updateValidate);

    /**
     * 岗位删除
     *
     * @author fzr
     * @param id 主键
     */
    void del(Integer id);

    /**
     * 返回导出格式
     * @return
     */
    JSONObject getExportData(PageValidate pageValidate, JobsSearchValidate searchValidate);

    /**
     * 导出
     */
    String export(JobsSearchValidate searchValidate);
}
