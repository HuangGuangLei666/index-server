package com.pl.indexserver.web;

import com.pl.indexserver.service.CheckBusinessService;
import com.pl.model.TBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智库完整性检查接口
 */
@RestController
@RequestMapping("/busiManagement/flowConfig")
public class CheckBusinessController {

    private CheckBusinessService checkBusinessService;

    @Autowired
    public CheckBusinessController(CheckBusinessService checkBusinessService){
        this.checkBusinessService = checkBusinessService;
    }

    /**
     * @api {GET} /busiManagement/flowConfig/checkBusiness 智库检测-检测智库配置是否完整;
     * @apiName CheckBusiness
     * @apiGroup WorkFlowContorller
     * @apiParam{Long} businessId 智库ID
     * @apiParam{Long} companyId 公司ID
     * @apiSuccess {String} 检测报告文本。
     * @apiSuccessExample {String}  成功返回示例
     *  小兵智库检测系统
     * 智库【深圳盛泰】配置检查结果：
     * #####【智库流程节点检测】#####
     *
     * #####【智库智能问答检测】#####
     *
     * #####【系统通用话术检测】#####
     * 【通用话术】没有配置，智库名称：深圳盛泰
     * @apiVersion 1.0.0
     */
    @GetMapping("/checkBusiness")
    public String checkBusiness(@RequestParam("companyId") Long companyId, @RequestParam("businessId")Long businessId){
        StringBuilder result = new StringBuilder();
        if (null == companyId || null == businessId){
            result.append("检查智库时，传入参数异常，请检查参数。");
            return result.toString();
        }
        TBusiness tBusiness = checkBusinessService.getCheckBusiness(businessId, companyId);
        if (null == tBusiness){
            result.append("要检查的智库不存在，请核查后再来！");
            return result.toString();
        }
        //智库节点检查
        String wnString = checkBusinessService.workNodeCheck(tBusiness);
        result.append(wnString).append("\r\n");

        //智能问答检查
        String knowQAString = checkBusinessService.knowledgeQACheck(tBusiness);
        result.append(knowQAString).append("\r\n");

        //系统通用检查
        String sccString = checkBusinessService.systemCommonCheck(tBusiness);
        result.append(sccString).append("\r\n");
        return result.toString();

    }

}
