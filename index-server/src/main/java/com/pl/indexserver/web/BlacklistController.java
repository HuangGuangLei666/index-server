package com.pl.indexserver.web;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.pl.indexserver.model.*;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.*;
import com.pl.model.CallTask;
import com.pl.model.TDialog;
import com.pl.model.TmCustomer;
import com.pl.model.TmUser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author HGL
 * @Date 2018/12/28
 */
@RestController
@RequestMapping(value = "/busiManagement")
public class BlacklistController {

    private static final Logger logger = LoggerFactory.getLogger(BlacklistController.class);
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Value("${recordings.ftpPath}")
    private String ftpFilePath;

    @Value("${custom.excel-path}")
    private String excelFilePath;

    @Value("${recordings.address}")
    private String recordAddress;

    @Autowired  //黑名单表
    private BlacklistService blacklistService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private SpeechcraftTagService speechcraftTagService;
    @Autowired //对话详情
    private TDialogService tDialogService;
    @Autowired
    private UserOperateRecordService userOperateRecordService;
    @Autowired
    private TmCustomerService tmCustomerService;
    @Autowired
    private FileTransferService fileTransferService;

    /**
     * @api {POST} /busiManagement//blacklist/queryBlacklistList 查询黑名单列表
     * @apiName 查询黑名单列表
     * @apiGroup BlacklistController
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": 3,
     * "ctId": "pulan_yanshi",
     * "ctName": "pulan",
     * "companyId": 1030,
     * "modifyDate": "2018-12-29 12:56:10",
     * "ctPhone": "444444444",
     * "ctQq": "777777777",
     * "ctWechat": "555555555",
     * "ctEmail": "333333333"
     * },
     * {
     * "id": 7,
     * "ctId": "lei",
     * "ctName": "磊",
     * "companyId": 1030,
     * "modifyDate": "2019-01-02 12:09:50",
     * "ctPhone": "18750031121",
     * "ctQq": "1144716956",
     * "ctWechat": "frfe",
     * "ctEmail": "54g5re4ferf"
     * },
     * {
     * "id": 8,
     * "ctId": null,
     * "ctName": null,
     * "companyId": 1030,
     * "modifyDate": "2019-01-02 14:14:39",
     * "ctPhone": "15078142323",
     * "ctQq": null,
     * "ctWechat": null,
     * "ctEmail": null
     * },
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     */
    @RequestMapping(value = "/blacklist/queryBlacklistList")
    public ReturnMsg queryBlacklistPage(HttpServletRequest request, int pageIndex, int pageNum, String ctPhone) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        Page<BlacklistDto> blacklistDtoPage = blacklistService.queryBlacklistPage(pageIndex, pageNum, user.getCompanyId(),ctPhone);

        returnMsg.setCode(0);
        returnMsg.setErrorMsg("返回正确！");
        returnMsg.setContent(blacklistDtoPage);
        return returnMsg;
    }


    /**
     * @api {POST} /busiManagement/blacklist/deleteBlacklist 刪除黑名單
     * @apiName 刪除黑名單
     * @apiGroup BlacklistController
     * @apiParam {int} id 黑名單id
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": null,
     * "token": null,
     * "errorMsg": "恭喜你！删除成功!"
     * }
     */
    @RequestMapping(value = "/blacklist/deleteBlacklist")
    public ReturnMsg deleteBlacklist(HttpServletRequest request,
                                     @RequestParam(value = "id", required = false) Long id) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        BlacklistDto blacklistDto = blacklistService.selectByPrimaryKey(id);
        if (null == blacklistDto) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("很遗憾！此黑名单不存在!");
            return returnMsg;
        }
        if (!user.getCompanyId().equals(blacklistDto.getCompanyId())) {
            returnMsg.setCode(2);
            returnMsg.setErrorMsg("很遗憾！此黑名单不在你的列表中!");
            /*logger.warn("");*/
            return returnMsg;
        }

        int i = blacklistService.deleteById(id);
        returnMsg.setCode(0);
        returnMsg.setErrorMsg("恭喜你！删除成功!");
        return returnMsg;
    }


    /**
     * @api {GET} /busiManagement/blacklist/downloadTemplate 下载黑名单号码模板
     * @apiName downloadExcelTemplate
     * @apiGroup BlacklistController
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/blacklist/downloadTemplate")
    public void downloadExcelTemplate(HttpServletRequest request, HttpServletResponse response) {
        TmUser user = GetUid.getUID(request, redisClient);
        try {
            List<SpeechcraftTagDto> speechcraftTags = speechcraftTagService.getSpeechcraftTagDtoList(user.getCompanyId(), "TTS");
            String titles = "";
            if (!CollectionUtils.isEmpty(speechcraftTags)) {
                for (SpeechcraftTagDto speeTagDto : speechcraftTags) {
                    String tagName = speeTagDto.getTagName();
                    if (!"客户姓名".equals(tagName) && !"号码后四位".equals(tagName)) {
                        titles += tagName + ",";
                    }
                }
            }
            HSSFWorkbook wb;
            if (user.getCompanyId() == 1005) {
                wb = ExcelDataUtil.exportFileModelWithCarnumber();
            } else {
                wb = ExcelDataUtil.exportFileModel(titles);
            }
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + new String("黑名單号码".getBytes("gbk"), "iso-8859-1") + ".xls");
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @api {GET} /busiManagement/blacklist/addBatch   添加黑名单号码
     * @apiName addBatch
     * @apiGroup BlacklistController
     * @apiParam {String} phones 手机号码
     * @apiParam {int} priority 是否优先
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回导入时间
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "pulan2019-01-02 14:19:50",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/blacklist/addBatch")
    public ReturnMsg addBatch(HttpServletRequest request,
                              @RequestParam("phones") String phones,
                              @RequestParam(value = "priority", required = false) Integer priority) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            String[] str = phones.split("\\|");
            //添加的号码为空。
            if (str.length <= 0) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("添加号码为空，请添加正确号码！");
                return returnMsg;
            }

            String regEx = "(^(0\\d{2,3})(-)?\\d{7,8})$|(1[0-9]{10})|(\\d{7,8})";
            List<String> phoneList = new ArrayList<>();
            List<BlacklistDto> blacklistDtoList = new ArrayList<>();
            // 记录错误号码
            List<String> errorPhones = new ArrayList<>();
            Set<BlacklistDto> newList = new HashSet<>();
            BlacklistDto blacklistDto;
            for (String sts : str) {
                // 校验手机号的格式
                if (!sts.matches(regEx)) {
                    errorPhones.add(sts);
                } else {
                    blacklistDto = new BlacklistDto(sts);
                    newList.add(blacklistDto);
                }
            }
            // 手机号格式有误，提前结束导入号码，把错误手机号返回
            if (errorPhones.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String tel : errorPhones) {
                    sb.append(tel).append("; ");
                }
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("您输入的号码格式有误，请输入正确的号码,错误号码有：" + sb.toString());
                return returnMsg;
            }
            //获取当前黑名单是否有号码！
            List<BlacklistDto> blacklistDtos = blacklistService.queryBlacklistListByCompanyId(user.getCompanyId());
            if (!CollectionUtils.isEmpty(blacklistDtos)) {
                //如果数据库中有数据,对比去除相同的数据
                String temp;
                Iterator<BlacklistDto> it = newList.iterator();
                for (BlacklistDto blacklistDto1 : blacklistDtos) {
                    temp = blacklistDto1.getCtPhone();
                    while (it.hasNext()) {
                        if (temp.equals(it.next().getCtPhone())) {
                            it.remove();
                        }
                    }
                }
            }
            boolean addFlag = false;
            for (BlacklistDto strs : newList) {
                phoneList.add(strs.getCtPhone());
                //还要添加一张表
                blacklistDto = new BlacklistDto();
                blacklistDto.setCtPhone(strs.getCtPhone());
                blacklistDto.setModifyDate(new Date());
                blacklistDto.setCompanyId(user.getCompanyId());
                blacklistDto.setCtId(user.getUserid());
                blacklistDto.setCtName(user.getUsername());
                blacklistDtoList.add(blacklistDto);
                if (blacklistDtoList.size() == 99) {
                    //99个添加一次
                    blacklistService.addBlacklist(blacklistDtoList);
                    blacklistDtoList.clear();
                    addFlag = true;
                }
            }
            //添加剩余的
            if (blacklistDtoList.size() > 0) {
                blacklistService.addBlacklist(blacklistDtoList);
                addFlag = true;
            }
            if (addFlag) {
                addTaskPhone(user.getCompanyId(), phoneList, redisClient);
                returnMsg.setCode(0);
                returnMsg.setContent(user.getUsername() + simpleDateFormat.format(new Date()));
            } else {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("号码保存失败！");
            }
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.STATUS_1, "/blacklist/addBatch",
                    CommonConstant.ADD_PHONE, phones, OperateType.CREATE, "号码：" + phones, null, null);
        } catch (Exception e) {
            logger.error("添加号码异常：", e);
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("添加号码失败");
            return returnMsg;
        }
        return returnMsg;
    }


    /**
     * @api {POST} /busiManagement/blacklist/import 导入Excel黑名单号码
     * @apiName import
     * @apiGroup BlacklistController
     * @apiParam {MultipartFile} file  excel 文件
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "2019-01-02 15:40:11黑名单号码.xls",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/blacklist/import")
    public ReturnMsg importExcel(HttpServletRequest request,
                                 @RequestParam("file") MultipartFile file) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            String ct_type = user.getCompanyId() + "-" + user.getUserid() + "-" + user.getUsername();
            String filename = file.getOriginalFilename();
            List<PersonDto> list = ExcelDataUtil.getExcelDatas(file, user.getCompanyId());
            Set<TmCustomer> tmCustomerSet = new HashSet<>();
            ArrayList<BlacklistDto> blacklistDtos = new ArrayList<>();
            TmCustomer tmCustomer;
            BlacklistDto blacklistDto;
            InputStream is = file.getInputStream();
            String fileName = System.currentTimeMillis() + file.getOriginalFilename();
            Set<PersonDto> setList = new HashSet<>();
            Set<PersonDto> set1 = new HashSet<>();
            // 记录错误号码
            List<String> errorPhones = new ArrayList<>();
            String regEx = "(^(0\\d{2,3})(-)?\\d{7,8})$|(1[0-9]{10})|(\\d{7,8})";
            //去除Excel相同的
            list.forEach(
                    p -> {
                        if (!StringUtils.isEmpty(p.getPhone())) {
                            // 校验手机号的格式
                            if (!p.getPhone().matches(regEx)) {//错误的手机号
                                errorPhones.add(p.getPhone());
                            } else { // 添加正确的手机号
                                // 重写了hashCode方法，根据号码去重
                                setList.add(p);
                            }
                        }
                    }
            );
            set1.addAll(setList);
            // 把客户信息添加到tm_customer表中，不存在就添加，存在就执行修改
            int p = 0;
            List<TmCustomer> list1 = tmCustomerService.getTelephonesFromPrivatCustomer(user.getCompanyId());
            set1.removeAll(list1);
            Iterator<PersonDto> iterator = set1.iterator();
            while (iterator.hasNext()) {
                PersonDto next = iterator.next();
                tmCustomer = new TmCustomer(user.getUserid(), next.getName(),
                        user.getCompanyId(), "", "", "", next.getPhone(), ct_type, 2);
                if (1005 == user.getCompanyId()) {
                    tmCustomer.setCar_numbers(next.getCar_numbers());
                }
                tmCustomerSet.add(tmCustomer);

                if (tmCustomerSet.size() == 900) {
                    p++;
                    System.out.println(p);
                    //900个添加一次
                    if (tmCustomerSet.size() != 0) {
                        tmCustomerService.addCustonerBatch(tmCustomerSet);
                        tmCustomerSet.clear();
                    }
                }
            }
            if (tmCustomerSet.size() > 0) { //添加剩余的
                tmCustomerService.addCustonerBatch(tmCustomerSet);
            }
            //先获取数据库中是否有数据
            List<BlacklistDto> oldList = blacklistService.queryBlacklistListByCompanyId(user.getCompanyId());
            setList.removeAll(oldList);
            int q = 0;
            if (1005 == user.getCompanyId()) {
                Iterator<PersonDto> iterator2 = setList.iterator();
                while (iterator2.hasNext()) {
                    PersonDto next = iterator2.next();
                    blacklistDto = new BlacklistDto();
                    blacklistDto.setCompanyId(user.getCompanyId());
                    blacklistDto.setCtPhone(next.getPhone());
                    blacklistDto.setModifyDate(new Date());
                    blacklistDto.setCtId(user.getUserid());
                    blacklistDto.setCtName(user.getUsername());
                    blacklistDtos.add(blacklistDto);
                    if (tmCustomerSet.size() == 900 || blacklistDtos.size() == 900) {
                        q++;
                        System.out.println(q);
                        //99个添加一次
                        blacklistService.addBlacklist(blacklistDtos);
                        blacklistDtos.clear();
                    }
                }
            } else {
                Iterator<PersonDto> iterator2 = setList.iterator();
                while (iterator2.hasNext()) {
                    PersonDto next = iterator2.next();
                    blacklistDto = new BlacklistDto();
                    blacklistDto.setCompanyId(user.getCompanyId());
                    blacklistDto.setCtPhone(next.getPhone());
                    blacklistDto.setModifyDate(new Date());
                    blacklistDto.setCtId(user.getUserid());
                    blacklistDto.setCtName(user.getUsername());
                    blacklistDtos.add(blacklistDto);
                    if (blacklistDtos.size() == 900 || tmCustomerSet.size() == 900) {
                        q++;
                        System.out.println(q);
                        //99个添加一次
                        blacklistService.addBlacklist(blacklistDtos);
                        blacklistDtos.clear();
                    }
                }
            }
            if (blacklistDtos.size() > 0) { //添加剩余的
                blacklistService.addBlacklist(blacklistDtos);
            }
            // 上传excel文件
            Long filePath = user.getCompanyId();
            System.out.println(recordAddress + "/" + excelFilePath + "/" + filePath);
            boolean re = fileTransferService.uploadFileToFTP(excelFilePath + "/" + filePath, fileName, is);
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.STATUS_1, "/blacklist/import",
                    CommonConstant.IMPORT_FILE, filename, OperateType.CREATE, "Ecxel批量导入号码，" + filename + "<a style='color:blue;' href='" + recordAddress + "/" + excelFilePath + "/" + filePath + "/" + fileName + "'>下载</a>",
                    null, null);
            returnMsg.setCode(0);
            // 提示错误的号码
            if (errorPhones.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String str : errorPhones) {
                    sb.append(str).append("; ");
                }
                returnMsg.setErrorMsg("错误号码未上传，错误号码有：" + sb.toString());
            }
            returnMsg.setContent(simpleDateFormat.format(new Date()) + filename);
            logger.info("导入的号码数量为：" + setList.size());
        } catch (Exception e) {
            logger.info("上传号码出错：", e);
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("上传号码失败");
            return returnMsg;
        }
        return returnMsg;
    }

    private void addTaskPhone(Long companyId, List<String> phoneList, RedisClient redisClient) {
        try {
            TaskManageDto taskManageDto = new TaskManageDto();
            taskManageDto.companyId = companyId;
            taskManageDto.operateType = 2;
            taskManageDto.telephoneList = phoneList;
            redisClient.lpush("TaskPreprocessQuene", JSONObject.toJSONString(taskManageDto));
            logger.info("发送添加任务号码消息【{}】", taskManageDto.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
