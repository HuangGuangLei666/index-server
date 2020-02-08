package com.pl.indexserver.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.service.WorkFlowService;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/busiManagement/flowConfig")
public class WorkFlowContorller {

    private static final Logger logger = LoggerFactory.getLogger(WorkFlowContorller.class);

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private WorkFlowService workFlowService;

    /**
     * @api {POST} /busiManagement/flowConfig/flow/create  【20181106】创建流程
     * @apiName createFlow
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} flowId 流程标识
     * @apiParam {Long} parentId 父流程标识
     * @apiParam {Long} businessId AI方案(业务标识)
     * @apiParam {String} name 流程名字
     * @apiParam {String} triggerOrder 触发指令
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  新创建流程的ID
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     *      "code": 0,
     *      "content":120,
     *      "token": null,
     *      "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping(value = "/flow/create")
    public ReturnMsg createFlow(HttpServletRequest request,
                                    @RequestParam (value = "flowId",required = false) Long flowId,
                                    @RequestParam(value = "parentId", defaultValue = "0", required = false) Long parentId,
                                    @RequestParam (value = "paramter",required = false) String paramter,
                                    @RequestParam(value = "businessId") Long businessId,
                                    @RequestParam(value = "name") String name,
                                    @RequestParam(value = "triggerOrder",required = false) String triggerOrder) {
        TmUser user = GetUid.getUID(request, redisClient);
        if (!StringUtils.isEmpty(user)){
            return workFlowService.workFlowCreate(user, user.getUserid(), flowId, parentId,paramter, user.getCompanyId(), businessId, name, triggerOrder);
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }

    /**
     * @api {POST} /busiManagement/flowConfig/flow/delete  【新】删除流程图,主流程不可删除
     * @apiName deleteFlow
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} flowId  流程标识
     * @apiParam {Long} businessId  业务标识
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  无
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     *      "code": 0,
     *      "content": "",
     *      "token": null,
     *      "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping(value = "/flow/delete")
    public ReturnMsg deleteFlow(HttpServletRequest request,
                                    @RequestParam(value = "flowId") long flowId,
                                    @RequestParam(value = "businessId") long businessId) {
        TmUser user = GetUid.getUID(request,redisClient);
        if (!StringUtils.isEmpty(user)){
            return workFlowService.deleteWorkFlow(user,flowId,businessId);
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }

    /**
     * @api {POST} /busiManagement/flowConfig/node/create   【新】创建流程节点(没有就创建，有就更新)
     * @apiName createFlowNode
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} flowId 流程ID
     * @apiParam {Long} businessId 业务标识ID
     * @apiParam {String} name 节点名字
     * @apiParam {Int} flag 开始节点（10）结束节点（11），非必需
     * @apiParam {String} paramter 界面参数(非必需)
     * @apiParam {Int} interupt 是否打断，1打断0不打断（默认是1）
     * @apiParam {Long} nextWorkNode 下跳的下一个流程节点id
     * @apiParam {Long} speechcraftId 专用话术标识
     * @apiParam {Int} score 意向分值
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  节点标识
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     *      "code": 0,
     *      "content": 210,
     *      "token": null,
     *      "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping(value = "/node/create")
    public ReturnMsg createFlowNode(HttpServletRequest request,
                                   @RequestParam(value = "name")String name,
                                   @RequestParam(value = "flowId")Long flowId,
                                    @RequestParam(value = "businessId")Long businessId,
                                   @RequestParam(value = "paramter",required =false)String paramter,
                                   @RequestParam(value = "flag",required = false)Integer flag,
                                   @RequestParam(value = "speechcraftId",required = false)String speechcraftId,
                                   @RequestParam(value = "interupt",required =false)Integer interupt,
                                   @RequestParam(value = "jump",required =false)Long jump,
                                   @RequestParam(value = "score",required =false)Integer score) {
        TmUser user = GetUid.getUID(request, redisClient);
        if (!StringUtils.isEmpty(user)){
            return workFlowService.createFlowNode(user, user.getUserid(),name,flowId,user.getCompanyId(),businessId,paramter,interupt,flag,jump,speechcraftId,score);
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }


    /**
     * @api {GET} /busiManagement/flowConfig/node/delete  【新】删除节点，有连接不允许删除
     * @apiName deleteFlowNode
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} flowNodeId 当前流程节点id
     * @apiParam {Long} flowId  流程标识
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  无
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     *      "code": 0,
     *      "content": "",
     *      "token": null,
     *      "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/node/delete")
    public ReturnMsg deleteFlowNode(HttpServletRequest request,
                                    @RequestParam(value = "flowNodeId") String paramter,
                                    @RequestParam(value = "flowId") long flowId) {
        TmUser user = GetUid.getUID(request,redisClient);
        if (!StringUtils.isEmpty(user)){
            return workFlowService.deleteWorkFlowNode(user,paramter,flowId);
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }

    /**
     * @api {POST} /busiManagement/flowConfig/link/create   【新】创建连接（节点与节点之间的链接线，没有创建，有就更新）
     * @apiName deleteFlowLink
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} flowId 流程标识ID
     * @apiParam {Long} fromNodeId 起始节点
     * @apiParam {Long} nextNodeId 终止节点
     * @apiParam {Long} businessId 业务标识ID
     * @apiParam {Long} paramter 界面链接线生成的ID，不能为空
     * @apiParam {Integer}  byAlgorithm 是否由算法来决策（非必须，0：否，1：是）非必须
     * @apiParam {Long} responseModeId 响应方式
     * @apiParam {Integer} state 状况(非必需，1：拒绝，2：同意，3：考虑)
     * @apiParam {Integer} priority 起点相同节点链路的优先级（1，2，3，4...优先级从高到底，升序排列）非必须
     * @apiSuccess {Integer} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  连接标识
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     *      "code": 0,
     *      "content": 123
     *      "token": null,
     *      "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping(value = "/link/create")
    public ReturnMsg createFlowLink(HttpServletRequest request,
                                            @RequestParam(value = "flowId") long flowId,
                                            @RequestParam(value = "fromNodeId") long fromNodeId,
                                            @RequestParam(value = "nextNodeId") long nextNodeId,
                                            @RequestParam(value = "businessId")Long businessId,
                                            @RequestParam(value = "paramter")Long ruleId,
                                            @RequestParam(value = "byAlgorithm",required = false)Integer by_algorithm,
                                            @RequestParam(value = "responseModeId") Long responseModeId,
                                            @RequestParam(value = "state",required = false)Integer state,
                                            @RequestParam(value = "priority",required = false)Integer priority) {
        TmUser user = GetUid.getUID(request,redisClient);
        if (!StringUtils.isEmpty(user)){
            return workFlowService.createWorkFlowLink(user,user.getUserid(),fromNodeId,nextNodeId,flowId,
                    user.getCompanyId(),businessId,ruleId,priority,by_algorithm,state, responseModeId);
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }

    /**
     * @api {POST} /busiManagement/flowConfig/link/delete   【新】删除连接
     * @apiName deleteFlowLink
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} flowId 流程标识
     * @apiParam {Long} flowLinkId 流程连接标识
     * @apiSuccess {Integer} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  连接标识
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     *      "code": 0,
     *      "content": "",
     *      "token": null,
     *      "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/link/delete")
    public ReturnMsg deleteFlowLink(HttpServletRequest request,@RequestParam(value = "flowId") Long flowId,
                                    @RequestParam(value = "flowLinkId") Long flowLinkId) {
        TmUser user = GetUid.getUID(request,redisClient);
        if (!StringUtils.isEmpty(user)){
            return workFlowService.deleteWorkFlowLink(user,flowId,flowLinkId);
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }

    /**
     * @api {POST} /busiManagement/flowConfig/link/creawteSpecial    【新】创建响应方式及连接
     * @apiName createSpecialFlowLink
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} flowId 流程标识
     * @apiParam {Long} businessId 业务标识
     * @apiParam {String} name 响应方式名称
     * @apiParam {String} craft 触发话术
     * @apiParam {String} keyword 关键字 & 分隔
     * @apiSuccess {Integer} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  连接标识
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     *      "code": 0,
     *      "content": 234,
     *      "token": null,
     *      "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping(value = "/link/createSpecial")
    public ReturnMsg createSpecialFlowLink(HttpServletRequest request,
                                    @RequestParam(value = "flowId") Long flowId,
                                    @RequestParam(value = "name") String name,
                                    @RequestParam(value = "craft") String craft,
                                    @RequestParam(value = "keyword") String keyword,
                                    @RequestParam(value = "fromNodeId") long fromNodeId,
                                    @RequestParam(value = "nextNodeId") long nextNodeId,
                                    @RequestParam(value = "businessId") Long businessId,
                                    @RequestParam(value = "paramter")Long ruleId,
                                    @RequestParam(value = "byAlgorithm",required = false)Integer by_algorithm,
                                    @RequestParam(value = "state",required = false)Integer state,
                                    @RequestParam(value = "priority",required = false)Integer priority) {
        TmUser user = GetUid.getUID(request,redisClient);
        if (!StringUtils.isEmpty(user)){
            ReturnMsg returnMsg = workFlowService.createSpecialResponseModel(user,user.getCompanyId(),flowId,name,craft,user.getUserid(),keyword);
            if (returnMsg.getCode() ==0){
                Long responseModeId = (Long) returnMsg.getContent();
                returnMsg = workFlowService.createWorkFlowLink(user,user.getUserid(),fromNodeId,nextNodeId,flowId, user.getCompanyId(), businessId,ruleId,priority,by_algorithm,state, responseModeId);
            }else {
                returnMsg.setCode(-1);
                returnMsg.setErrorMsg("自定义应答方式创建失败！");
            }
            return returnMsg;
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }

    /**
     * @api {POST} /busiManagement/flowConfig/flow/saveParam   【新】保存流程图的坐标参数
     * @apiName createSpecialFlowLink
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} flowId 流程标识（非必需）
     * @apiParam {Long} businessId 业务标识
     * @apiParam {String} paramter 参数
     * @apiSuccess {Integer} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  无
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     *      "code": 0,
     *      "content": 234,
     *      "token": null,
     *      "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping(value = "/flow/saveParam")
    public ReturnMsg saveFlowUIParam(HttpServletRequest request,
                                           @RequestParam(value = "flowId",required = false) Long flowId,
                                           @RequestParam(value = "businessId") Long businessId,
                                           @RequestParam(value = "paramter") String paramter) {
        TmUser user = GetUid.getUID(request,redisClient);
        if (!StringUtils.isEmpty(user)){
            return workFlowService.saveFlowUIParam(user,flowId, businessId,paramter);
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }

    /**
     * @api {GET} /busiManagement/flowConfig/flow/list   【新】查询流程列表
     * @apiName listDialogFlow
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} businessId 业务标识,为0 表示查全部
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  流程列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     *{
     *  "code": 0,
     *  "content": [
     *    {
     *      "id": 11,
     *      "companyId": 18,
     *      "businessId": 456795,
     *      "name": null,
     *      "stateRule": 1,
     *      "enableInterupt": 1,
     *      "status": 0,
     *      "paramter": null,
     *      "sort": 9999,
     *      "modifyDate": null
     *    },
     *    {
     *      "id": 15,
     *      "companyId": 18,
     *      "businessId": 456795,
     *      "name": "接入开场白",
     *      "stateRule": 1,
     *      "enableInterupt": 1,
     *      "status": 0,
     *      "paramter": null,
     *      "sort": 9999,
     *      "modifyDate": "2018-07-17 10:21:56"
     *    }
     *  ],
     *  "token": "",
     *  "errorMsg": ""
     *}
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/flow/list")
    public ReturnMsg listDialogFlow(HttpServletRequest request,
                                    @RequestParam(value = "businessId", required = false) Long businessId) {
        TmUser user = GetUid.getUID(request,redisClient);
        if (!StringUtils.isEmpty(user)){
            return workFlowService.getWorkFlowList(user, businessId);
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }

    /**
     * @api {GET} /busiManagement/flowConfig/flow/tree 【20181106】查询流程列表树
     * @apiName dialogFlowTree
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} businessId 业务标识,为0 表示查全部
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  流程列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     *   "code": 0,
     *   "content": [
     *     {
     *       "id": 306,
     *       "companyId": 2000,
     *       "businessId": 134679966,
     *       "parentId": 0,
     *       "name": "主流程",
     *       "stateRule": 1,
     *       "enableInterupt": 1,
     *       "triggerMode": 0,
     *       "triggerOrder": null,
     *       "level": 1,
     *       "status": 0,
     *       "paramter": null,
     *       "sort": 0,
     *       "modifyDate": "2018-11-05 12:26:45",
     *       "children": [
     *         {
     *           "id": 309,
     *           "companyId": 2000,
     *           "businessId": 134679966,
     *           "parentId": 306,
     *           "name": "1.1 流程",
     *           "stateRule": 1,
     *           "enableInterupt": 1,
     *           "triggerMode": 0,
     *           "triggerOrder": "",
     *           "level": 2,
     *           "status": 0,
     *           "paramter": null,
     *           "sort": 0,
     *           "modifyDate": "2018-11-05 14:14:55",
     *           "children": []
     *         }
     *       ]
     *     }
     *   ],
     *   "token": "",
     *   "errorMsg": ""
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/flow/tree")
    public ReturnMsg dialogFlowTree(HttpServletRequest request,
                                    @RequestParam(value = "businessId", required = false) Long businessId) {
        TmUser user = GetUid.getUID(request,redisClient);
        if (!StringUtils.isEmpty(user)){
            return workFlowService.getWorkFlowTree(user, businessId);
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }

    /**
     * @api {GET} /busiManagement/flowConfig/flow/queryFlowUIParam   【新】查询流程图
     * @apiName queryFlowDetail
     * @apiGroup WorkFlowContorller
     * @apiParam {Long} flowId 流程标识
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  流程详情
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     *      "code": 0,
     *      "content": {Object},
     *      "token": null,
     *      "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/flow/queryFlowUIParam")
    public ReturnMsg queryFlowDetail(HttpServletRequest request, @RequestParam(value = "flowId") long flowId){
        TmUser user = GetUid.getUID(request,redisClient);
        if (!StringUtils.isEmpty(user)){
            return workFlowService.getWorkFlowUIParam(user,flowId);
        }
        return new ReturnMsg(-1,"","","抱歉，权限不足！");
    }

    /**
     * 查询测试话术
     * @param telephone 电话号码
     * @return
     */
    @RequestMapping("/dialog/test")
    public ReturnMsg querCallTaskTest(@RequestParam(value = "telephone") String telephone){
        String localKey = "tm_chat_record_test:"+telephone.trim();
        ReturnMsg returnMsg =new ReturnMsg();
        try {
            List<String> list = redisClient.lrange(14,localKey);
            if (list.size()>0){
                JSONObject jsonObject = JSON.parseObject(list.get(0));
                JSONArray arrayList = jsonObject.getJSONArray("chatRecord");
                returnMsg.setCode(0);
                returnMsg.setContent(arrayList);
            }
        }catch (Exception e){
            returnMsg.setCode(0);
            returnMsg.setContent(new ArrayList<>());
            returnMsg.setErrorMsg("查无数据！");
        }
        return returnMsg;
    }

}
