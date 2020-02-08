package com.pl.indexserver.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.service.TManualService;
import com.pl.indexserver.untils.StatusMapValue;
import com.pl.model.TManual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/busiManagement/manual")
public class TurnToManualController {

    @Autowired
    private TManualService tManualService;

    @RequestMapping("/queryList")
    public ReturnMsg getQueryManualList(Long companyId, String uid, String phoneNum,
                                        int pageIndex, int pageNum,
                                        @RequestParam(required = false) Integer status,
                                        @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginDate,
                                        @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate){
        Page<TManual> page = tManualService.getTManualListOfPage(pageIndex, pageNum, phoneNum, companyId, uid, status, beginDate, endDate);
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setCode(0);
        returnMsg.setContent(page);
        return returnMsg;
    }

    @RequestMapping("/add")
    public ReturnMsg addManual(TManual manual){
        ReturnMsg returnMsg = new ReturnMsg();
        if (manual.getTaskId() == null || manual.getPhoneNum() == null || manual.getUid() == null || manual.getCompanyId() == null){
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("必要参数不全，无法添加！");
            return returnMsg;
        }
        Integer id = manual.getId();
        int rest;
        if (id != null && id > 0){
            rest = tManualService.updateTmanual(manual);
        } else {
            //转人工成功(人工提交信息)
            manual.setStatus(2);
            manual.setStage(StatusMapValue.getStatusMapValue(manual.getStatus()));
            rest = tManualService.insertTmanual(manual);
        }
        if (rest > 0){
            returnMsg.setCode(0);
            returnMsg.setContent(rest);
        } else {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("操作失败！");
        }
        return returnMsg;
    }

    @RequestMapping("/getManual")
    public ReturnMsg queryManual(@RequestParam Integer id){
        ReturnMsg returnMsg = new ReturnMsg();
        TManual manual = tManualService.getTmanualByPriKey(id);
        if (manual != null){
            returnMsg.setCode(0);
            returnMsg.setContent(manual);
        } else {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("查无数据！");
        }
        return returnMsg;
    }

}
