package com.pl.indexserver.web;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.query.TDialogQuery;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.OperateType;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TDialog;
import com.pl.model.TmUser;
import com.pl.model.UploadFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/busiManagement")
public class FileTransferContorller {

    private static final Logger logger = LoggerFactory.getLogger(FileTransferContorller.class);

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private FileTransferService fileTransferService;
    @Autowired
    private UserOperateRecordService userOperateRecordService;
    @Autowired
    private TDialogService tDialogService;
    @Autowired
    private UploadFileService uploadFileService;
    @Value("${recordings.address}")
    private String recordAddress;
    @Autowired
    private BusinessReportService businessReportService;


    /**
     * @api {POST} /busiManagement/flowConfig/craft/uploadRecord  智能配置相关上传录音文件
     * @apiName uploadRecord
     * @apiGroup FileTransferContorller
     * @apiParam {MultipartFile} file  音频文件
     * @apiParam {String} businessId  智库id
     * @apiSuccess {String} code 0:上传成功 1:上传失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "filaId":文件id,
     * "filePath":"录音播放地址"
     * },
     * "token": null,
     * "errorMsg": null
     * }id
     * @apiVersion 1.0.0
     */
    @PostMapping("/flowConfig/craft/uploadRecord")
    public ReturnMsg uploadRecirdForCommonCraft(HttpServletRequest request, @RequestParam("businessId") Long businessId,
                                                @RequestParam("file") MultipartFile file) {
        ReturnMsg returnMsg = new ReturnMsg();
        Map<String, Object> map = new HashMap<>();
        try {
            if (StringUtils.isEmpty(businessId)) {
                businessId = 0L;
            }
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            String filePath = companyId + "/BUSINESS-" + businessId;
            String fileName = file.getOriginalFilename()+System.nanoTime();
            InputStream is = file.getInputStream();
            String userid = user.getUserid();
            long size = file.getSize();
            //插入录音文件相关信息
            UploadFile uploadFile = new UploadFile();
            uploadFile.setCompanyId(companyId);
            uploadFile.setCreateDate(new Date());
            uploadFile.setFileName(fileName);
            uploadFile.setStatus(0);
            uploadFile.setFileSize(size);
            uploadFile.setType(1);
            uploadFile.setUid(userid);

            Boolean aBoolean = uploadFileService.insertSelective(uploadFile);
            Long id = uploadFile.getId();

            //上传录音文件
            boolean flag = fileTransferService.uploadFileToFTP(filePath, fileName, is);

            logger.info("upload file result={}  filePath={}, fileName={}",flag,filePath ,fileName);
            //返回录音地址
            String ftpPath = recordAddress + "/" + filePath + "/" + fileName;
            map.put("fileId", uploadFile.getId());
            map.put("filePath", ftpPath);
            //插入操作日志
            StringBuilder remark = new StringBuilder(JSONObject.toJSONString(null));
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
            }

            // 操作日志处理
            String opInfo = String.format("文件名称:%s,", file.getOriginalFilename());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId.toString(), "craft/uploadRecord",
                    CommonConstant.UPLOADRECIRD, remark.toString(), OperateType.CREATE, opInfo, null, null);

            if (flag) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(map);
            } else {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("上传文件失败");
            }
            
            logger.info("upload file  return={}",returnMsg);
            
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("上传文件失败");
            logger.error("上传录音文件失败:", e);
        }
        return returnMsg;
    }


    /**
     *
     * @api {GET} /busiManagement/callTask/dialog/downloadRecordFiles 根据条件下载对应通话录音
     * @apiName 任务明细信息
     * @apiGroup FileTransferContorller
     * @apiParam {Long} taskId  任务ID
     * @apiParam {String} beginDate  开始时间
     * @apiParam {String} endDate  结束时间
     * @apiParam {String} intentionLevel  意向等级
     * @apiParam {String} telephone  联系号码
     * @apiParam {String} status  工作状态
     * @apiParam {String} type
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": null,
     * "token": null,
     * "errorMsg": null
     * }
     */
    @GetMapping(value = "/callTask/dialog/downloadRecordFiles")
    public ReturnMsg selectAllTDialogTDialogModelDto(HttpServletRequest request, HttpServletResponse response,
                                                     @RequestParam(value = "id") Long taskId,
                                                     @RequestParam(value = "beginDate", required = false) String beginDate,
                                                     @RequestParam(value = "endDate", required = false) String endDate,
                                                     @RequestParam(value = "intentionLevel", required = false) String intentionLevel,
                                                     @RequestParam(value = "status", required = false) String status,
                                                     @RequestParam(value = "type", required = false, defaultValue = "all") String type,
                                                     @RequestParam(value = "telephone", required = false) String telephone,
                                                     @RequestParam(value = "isLocal", required = false) Boolean isLocal) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            TDialogQuery dialogQuery = new TDialogQuery();
            dialogQuery.setTaskId(taskId);
            dialogQuery.setType(type);
            dialogQuery.setIntentionLevel(intentionLevel);
            if (!StringUtils.isEmpty(beginDate)) {
                dialogQuery.setBeginDate(beginDate + " 00:00:00");
            }
            if (!StringUtils.isEmpty(endDate)) {
                dialogQuery.setEndDate(endDate + " 23:59:59");
            }
            if (!StringUtils.isEmpty(telephone)) {
                telephone=telephone.replaceAll("\\D", "");
                dialogQuery.setTelephone(telephone);
            }
            if (status != null && !"".equals(status) && Integer.valueOf(status) == 50) {
                dialogQuery.setTableName(businessReportService.getReportTableName(dialogQuery.getTaskId()));
            } else {
                if (!StringUtils.isEmpty(status) && Integer.valueOf(status) < 20) {
                    dialogQuery.setStatus(status);
                } else if (!StringUtils.isEmpty(status) && Integer.valueOf(status) > 20) {
                    dialogQuery.setIsIntention(status);
                } else if (!StringUtils.isEmpty(status) && Integer.valueOf(status) == 20) {
                    dialogQuery.setAllIsIntention(true);
                }
            }
            List<TDialog> tDialogs = tDialogService.selectByQuery(dialogQuery);
            String filePath = null;
            if(null != isLocal && isLocal){
                filePath = fileTransferService.downloadResourcesToLocalCompressFile(taskId, user.getCompanyId(), user.getUsername(), tDialogs);
            }else {
                if (tDialogs.size() > 10000) {
                    returnMsg.setCode(1);
                    returnMsg.setErrorMsg("文件过大，请联系客服~");
                    return returnMsg;
                }else
                if(tDialogs.size() <=0){
                    returnMsg.setCode(1);
                    returnMsg.setErrorMsg("没有符合条件的通话~");
                    return returnMsg;
                }
                response.setHeader("Content-disposition", "attachment;filename=" + new String("录音文件.zip".getBytes("GBK"), "ISO-8859-1"));
                response.setContentType("application/x-msdownload;charset=UTF-8");
                ServletOutputStream outputStream = response.getOutputStream();
                fileTransferService.downloadResourcesToCompressFile(outputStream,taskId,user.getCompanyId(),tDialogs);
            }
            returnMsg.setCode(0);
            returnMsg.setContent(filePath);
        } catch (Exception e) {
            logger.info("批量下载录音文件出现异常:  ", e);
        }
        return returnMsg;
    }


    /**
     *
     * @api {GET} /busiManagement/callTask/dialog/downloadRecordFiles 下载对应呼入通话录音
     * @apiName 下载对应呼入通话录音
     * @apiGroup FileTransferContorller
     * @apiParam {Long} taskId  任务ID
     * @apiParam {String} beginDate  开始时间
     * @apiParam {String} endDate  结束时间
     * @apiParam {String} intentionLevel  意向等级
     * @apiParam {String} telephone  联系号码
     * @apiParam {String} status  工作状态
     * @apiParam {String} type
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": null,
     * "token": null,
     * "errorMsg": null
     * }
     */
    @GetMapping(value = "/callIn/dialog/downloadRecordFiles")
    public ReturnMsg selectAllTDialogInTDialogModelDto(HttpServletRequest request, HttpServletResponse response,
                                                     @RequestParam(value = "id") Long taskId,
                                                     @RequestParam(value = "beginDate", required = false) String beginDate,
                                                     @RequestParam(value = "endDate", required = false) String endDate,
                                                     @RequestParam(value = "intentionLevel", required = false) String intentionLevel,
                                                     @RequestParam(value = "status", required = false) String status,
                                                     @RequestParam(value = "type", required = false, defaultValue = "all") String type,
                                                     @RequestParam(value = "telephone", required = false) String telephone,
                                                     @RequestParam(value = "isLocal", required = false) Boolean isLocal) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            TDialogQuery dialogQuery = new TDialogQuery();
            dialogQuery.setTaskId(taskId);
            dialogQuery.setType(type);
            dialogQuery.setIntentionLevel(intentionLevel);
            if (!StringUtils.isEmpty(beginDate)) {
                dialogQuery.setBeginDate(beginDate + " 00:00:00");
            }
            if (!StringUtils.isEmpty(endDate)) {
                dialogQuery.setEndDate(endDate + " 23:59:59");
            }
            if (!StringUtils.isEmpty(telephone)) {
                telephone=telephone.replaceAll("\\D", "");
                dialogQuery.setTelephone(telephone);
            }
            if (status != null && !"".equals(status) && Integer.valueOf(status) == 50) {
                dialogQuery.setTableName(businessReportService.getReportTableName(dialogQuery.getTaskId()));
            } else {
                if (!StringUtils.isEmpty(status) && Integer.valueOf(status) < 20) {
                    dialogQuery.setStatus(status);
                } else if (!StringUtils.isEmpty(status) && Integer.valueOf(status) > 20) {
                    dialogQuery.setIsIntention(status);
                } else if (!StringUtils.isEmpty(status) && Integer.valueOf(status) == 20) {
                    dialogQuery.setAllIsIntention(true);
                }
            }
            List<TDialog> tDialogs = tDialogService.selectCallInByQuery(dialogQuery);
            String filePath = null;
            if(null != isLocal && isLocal){
                filePath = fileTransferService.downloadResourcesToLocalCompressFile(taskId, user.getCompanyId(), user.getUsername(), tDialogs);
            }else {
                if (tDialogs.size() > 1000) {
                    returnMsg.setCode(1);
                    returnMsg.setErrorMsg("文件过大，请联系客服~");
                    return returnMsg;
                }else
                if(tDialogs.size() <=0){
                    returnMsg.setCode(1);
                    returnMsg.setErrorMsg("没有符合条件的通话~");
                    return returnMsg;
                }
                response.setHeader("Content-disposition", "attachment;filename=" + new String("录音文件.zip".getBytes("GBK"), "ISO-8859-1"));
                response.setContentType("application/x-msdownload;charset=UTF-8");
                ServletOutputStream outputStream = response.getOutputStream();
                fileTransferService.downloadResourcesToCompressFile(outputStream,taskId,user.getCompanyId(),tDialogs);
            }
            returnMsg.setCode(0);
            returnMsg.setContent(filePath);
        } catch (Exception e) {
            logger.info("批量下载录音文件出现异常:  ", e);
        }
        return returnMsg;
    }


}




