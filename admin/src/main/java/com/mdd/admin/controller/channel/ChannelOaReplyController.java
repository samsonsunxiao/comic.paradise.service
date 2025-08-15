package com.mdd.admin.controller.channel;

import com.mdd.admin.service.IChannelOaReplyService;
import com.mdd.admin.validate.channel.ChannelRpValidate;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.channel.ChannelRpVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/adminapi/channel.official_account_reply")
@Api(tags = "公众号回复")
public class ChannelOaReplyController {

    @Resource
    IChannelOaReplyService iChannelOaReplyService;

    @GetMapping("/lists")
    @ApiOperation(value="默认回复列表")
    public AjaxResult<PageResult<ChannelRpVo>> list(@Validated PageValidate pageValidate, @RequestParam("reply_type") Integer replyType) {
        PageResult<ChannelRpVo> list = iChannelOaReplyService.list(pageValidate, replyType);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value="默认回复详情")
    public AjaxResult<ChannelRpVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        ChannelRpVo vo = iChannelOaReplyService.detail(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/add")
    @ApiOperation(value="默认回复新增")
    public AjaxResult<Object> add(@Validated @RequestBody ChannelRpValidate defaultValidate) {
        iChannelOaReplyService.add(defaultValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation(value="默认回复编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody ChannelRpValidate defaultValidate) {
        iChannelOaReplyService.edit(defaultValidate);
        return AjaxResult.success();
    }

    @PostMapping("/delete")
    @ApiOperation(value="默认回复删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iChannelOaReplyService.del(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/status")
    @ApiOperation(value="默认回复状态")
    public AjaxResult<Object> status(@Validated @RequestBody IdValidate idValidate) {
        iChannelOaReplyService.status(idValidate.getId());
        return AjaxResult.success();
    }

}
