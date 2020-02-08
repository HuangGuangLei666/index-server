package com.pl.indexserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.*;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.*;
import com.pl.mapper.*;
import com.pl.model.*;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class SpeechcraftServiceImpl implements SpeechcraftService {

    private static final Logger logger = Logger.getLogger(SpeechcraftServiceImpl.class);

    @Autowired
    private CallTaskMapper callTaskMapper;
    @Autowired
    private SpeechcraftMapper speechcraftMapper;
    @Autowired
    private UploadFileMapper uploadFileMapper;
    @Autowired
    private FileTransferService fileTransferService;
    @Autowired
    private TTSService ttsService;
    @Autowired
    private SpeechcraftTagService speechcraftTagService;
    @Autowired
    private UserOperateRecordService userOperateRecordService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TBusinessMapper tBusinessMapper;
    @Autowired
    private TBusinessPropertyMapper tBusinessPropertyMapper;
    @Autowired
    private TBusinessFocusService tBusinessFocusService;

    @Autowired
    private KnowledgeQuestionMapper knowledgeQuestionMapper;
    @Autowired
    private KnowledgeAnswerMapper knowledgeAnswerMapper;
    @Autowired
    private QCommonCraftMapper qCommonCraftMapper;
    @Autowired
    private ACommonCraftMapper aCommonCraftMapper;

    @Value("${redis.recordUpdateForSIP}")
    private String recordUpdateForSIP;
    @Value("${redis.recordSynthesis}")
    private String recordSynthesis;
    @Value("${recordings.address}")
    private String ftpPath;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public ReturnMsg deleteByPrimaryKey(Long id) {
        try {
            int rest = speechcraftMapper.deleteByPrimaryKey(id);
            return new ReturnMsg(0, rest, "", "");
        } catch (Exception e) {
            logger.error("SpeechCraft Delele is Error:" + e.getMessage());
            return new ReturnMsg(-1, "", "", "删除失败！");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int insert(Speechcraft record) {
        return speechcraftMapper.insert(record);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public ReturnMsg insertSelective(Long companyId, Long businessId, String name, String content, Long msgtemplId,
                                     MultipartFile file) {
        Speechcraft sp = new Speechcraft();
        sp.setName(name);
        sp.setBusinessId(businessId);
        sp.setCompanyId(companyId);
        sp.setContent(content);
        sp.setMsgtemplId(msgtemplId);
        sp.setCreateDate(DateUtils.getCurrentDate());
        if (file != null) {
            sp.setIsRecord(1);
            sp.setRecordFile(file.getOriginalFilename());
        } else {
            sp.setIsRecord(0);
            sp.setRecordFile("");
        }
        try {
            int i = speechcraftMapper.insertSelective(sp);
            return new ReturnMsg(0, i, "", "");
        } catch (Exception e) {
            logger.error("SpeechCraftService insertSelective is Error" + e.getMessage());
            return new ReturnMsg(-1, "", "", "新增专用话术失败！");
        }
    }

    @Override
    public Speechcraft selectByPrimaryKey(Long id) {
        return speechcraftMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public ReturnMsg updateByPrimaryKeySelective(String msgBody) {
        try {
            Speechcraft speechcraft = JSONObject.parseObject(msgBody, Speechcraft.class);
            speechcraft.setModifyDate(DateUtils.getCurrentDate());
            int reult = speechcraftMapper.updateByPrimaryKeySelective(speechcraft);
            return new ReturnMsg(0, reult, "", "");
        } catch (Exception e) {
            logger.error("updateByPrimaryKeySelective is Error:" + e.getMessage());
            return new ReturnMsg(-1, "", "", "修改专用话术失败！");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int updateByPrimaryKey(Speechcraft record) {
        return speechcraftMapper.updateByPrimaryKey(record);
    }

    public SpeechcraftServiceImpl() {
        super();
    }

    @Override
    public int selectSpecialtyTalkCount(Long id) {
        return speechcraftMapper.selectSpecialtyTalkCount(id);
    }

    @Override
    public int selectSpecialtyTalkCountByIsRecord(Long id) {
        return speechcraftMapper.selectSpecialtyTalkCountByIsRecord(id);
    }

    @Override
    public int selectTDialogCountByIStatus(Long id) {
        List<Long> list = callTaskMapper.selectTaskIdByBusinessId(id);
        StringBuilder str = new StringBuilder();
        if (list.size() > 0) {
            for (Long callTaskId : list) {
                if (null != callTaskId) {
                    str.append(callTaskId).append(",");
                }
            }
        }
        if (str.length() > 0) {
            str.deleteCharAt(str.length() - 1);
            return speechcraftMapper.selectTDialogCountByIStatus(str.toString());
        } else {
            return 0;
        }
    }

    @Override
    public int selectTDialogCountByIsIntention(Long id) {
        return speechcraftMapper.selectTDialogCountByIsIntention(id.toString());
    }

    @Override
    public List<SpecialtyTalkDto> selectSpeechcraftAllForName(Map<String, Object> map) {
        List<SpecialtyTalkDto> modelList = speechcraftMapper.selectSpeechcraftAllForName(map);
        if (!CollectionUtils.isEmpty(modelList)) {
            for (SpecialtyTalkDto specialtyTalkDto : modelList) {
                String modifyDate = specialtyTalkDto.getModifyDate();
                if (null == modifyDate || "".equals(modifyDate)) {
                    specialtyTalkDto.setModifyDate(specialtyTalkDto.getCreateDate());
                }
            }
        }
        return modelList;
    }

    @Override
    public List<Speechcraft> selectByCraftId(String craftId) throws Exception {
        return speechcraftMapper.selectByCraftId(craftId);
    }

    @Override
    public SpeechcraftModelDto selectDtoByCraftId(String craftId, boolean flag) throws Exception {
        List<Speechcraft> list = speechcraftMapper.selectByCraftId(craftId);
        if (null == list || list.size() == 0) {
            return null;
        }
        // 获取当前公司所有话术标签
        List<SpeechcraftTagDto> speechcraftTagDtos = speechcraftTagService.getSpeechcraftTagDtoList(list.get(0).getCompanyId(), null);

        SpeechcraftModelDto speechcraftModelDto = new SpeechcraftModelDto();
        List<SpeechcraftModelDto> speechcrafts = speechcraftModelDto.getSpeechcrafts();
        for (int i = 0; i < list.size(); i++) {
            Speechcraft speechcraft = list.get(i);
            if (i == 0) {
                speechcraftModelDto.setCraftId(speechcraft.getCraftId());
                speechcraftModelDto.setName(speechcraft.getName());
                speechcraftModelDto.setMsgtemplId(speechcraft.getMsgtemplId());
                speechcraftModelDto.setBusinessId(speechcraft.getBusinessId());
                speechcraftModelDto.setScore(speechcraft.getScore());
                speechcraftModelDto.setFoucs(speechcraft.getFoucs());
            }
            String content = speechcraft.getContent();
            if (null != content) {
                String recordFile = speechcraft.getRecordFile();
                String recordDescribe = speechcraft.getRecordDescribe();
                SpeechcraftModelDto temp = new SpeechcraftModelDto();
                List<Map<String, String>> recordDetail = new ArrayList<>();
                String[] recordFiles = recordFile.split("&");
                String[] contents = content.split("&");
                String[] recordDescribes = recordDescribe.split("&");
                for (int j = 0; j < contents.length; j++) {
                    Map<String, String> map = new HashMap<>();
                    String recordFileItem = recordFiles[j];
                    if (flag) {
                        //处理录音文件
                        if (!TagUtils.isLabel(recordFileItem) && !"*".equals(recordFileItem)) {
                            recordFileItem = ftpPath + "/" + speechcraft.getCompanyId() + "/BUSINESS-" + speechcraft.getBusinessId() + "/" + recordFileItem + "?" + new Date().getTime();
                        }
                    }
                    map.put("content", TagUtils.getLabel(contents[j], speechcraftTagDtos));
                    map.put("recordFile", recordFileItem);
                    map.put("recordDescribe", recordDescribes[j]);
                    recordDetail.add(map);
                }
                temp.setRecordDetail(recordDetail);
                temp.setContent(TagUtils.disposeContent(content, speechcraftTagDtos));
                temp.setRecordState(speechcraft.getRecordState());
                temp.setRecordName(recordFile);
                temp.setId(speechcraft.getId());
//                temp.setContent(null == content ? "" : content);
                speechcrafts.add(temp);
            }
//            if (flag) {
//                //处理录音文件
//                if (null != recordFile && !"".equals(recordFile)) {
//                    recordFile = ftpPath + "/" + speechcraft.getCompanyId() + "/BUSINESS-" + speechcraft.getBusinessId() + "/" + recordFile + "?" + new Date().getTime();
//                }
//            }
        }
        return speechcraftModelDto;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Boolean updateByPrimaryKey(List<Speechcraft> speechcrafts) throws Exception {
        boolean flag = false;
        for (Speechcraft speechcraft : speechcrafts) {
            int i1 = speechcraftMapper.updateByPrimaryKeySelective(speechcraft);
            flag = i1 > 0;
        }
        if (!flag) {
            throw new Exception();
        }
        return flag;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean deleteByCraftId(String craftIid) throws Exception {
        List<Speechcraft> speechcrafts = selectByCraftId(craftIid);
        int i = speechcraftMapper.deleteByCraftId(craftIid);
        if (speechcrafts.size() > 0) {
            tBusinessMapper.updateModifyDateById(speechcrafts.get(0).getBusinessId());
        }
        return i > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Boolean insertBasicByDto(SpeechcraftModelDto speechcraftModelDto) throws Exception {
        if (StringUtils.isEmpty(speechcraftModelDto.getName())) {
            throw new NullPointerException("话术名称不能为空");
        }
        speechcraftModelDto.setCreateDate(new Date());
        int flag = speechcraftMapper.insertSelective(speechcraftModelDto);
        tBusinessMapper.updateModifyDateById(speechcraftModelDto.getBusinessId());
        return flag > 0;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Boolean updateBasicByDto(SpeechcraftModelDto speechcraftModelDto) throws Exception {
        if (!StringUtils.isEmpty(speechcraftModelDto.getContent()) || !StringUtils.isEmpty(speechcraftModelDto.getRecordName())) {
            throw new Exception("数据异常！");
        }
        int flag = speechcraftMapper.updateByCraftIdSelective(speechcraftModelDto);
        tBusinessMapper.updateModifyDateById(speechcraftModelDto.getBusinessId());
        return flag > 0;
    }

    @Override
    public long countFileSizeByCompanyId(Long companyId) throws Exception {
        Long size = speechcraftMapper.countFileSizeByCompanyId(companyId);
        return size == null ? 0 : size;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean updateAndRecardLogToContentRelevant(SpeechcraftModelDto speechcraftModelDto, TmUser user) throws Exception {
        String craftId = speechcraftModelDto.getCraftId();
        Long id = speechcraftModelDto.getId();
        if (StringUtils.isEmpty(craftId) || StringUtils.isEmpty(id)) {
            throw new NullPointerException("主键或者话术标识不能为null!");
        }
        //查询部分基础信息
        Speechcraft speechcraft = selectByPrimaryKey(id);
        speechcraftModelDto.setModifyDate(new Date());
        speechcraftModelDto.setCompanyId(speechcraft.getCompanyId());
        speechcraftModelDto.setBusinessId(speechcraft.getBusinessId());
        speechcraftModelDto.setName(speechcraft.getName());
        speechcraftModelDto.setFileSizeCount(speechcraft.getFileSizeCount());
        List<Integer> list = disposeParam(speechcraftModelDto, user);
        int flag = speechcraftMapper.updateByPrimaryKeySelective(speechcraftModelDto);
        tBusinessMapper.updateModifyDateById(speechcraftModelDto.getBusinessId());
        if (flag > 0 && list.size() > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", speechcraftModelDto.getId());
            jsonObject.put("type", 1);
            jsonObject.put("index", list);
            redisClient.lpush(recordSynthesis, jsonObject.toJSONString());
        }
        return flag > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean insertAndRecardLogToContentRelevant(SpeechcraftModelDto speechcraftModelDto, TmUser user) throws Exception {
        String craftId = speechcraftModelDto.getCraftId();
        if (StringUtils.isEmpty(craftId)) {
            throw new NullPointerException("话术标识不能为null!");
        }
        //查询部分基础信息
        SpeechcraftModelDto temp = speechcraftMapper.selectOneByCraftId(craftId);
        String content_temp = temp.getContent();
        //处理第一条空数据
        if (StringUtils.isEmpty(content_temp)) {
            speechcraftModelDto.setId(temp.getId());
            return updateAndRecardLogToContentRelevant(speechcraftModelDto, user);
        }
        speechcraftModelDto.setCreateDate(new Date());
        speechcraftModelDto.setCompanyId(temp.getCompanyId());
        speechcraftModelDto.setBusinessId(temp.getBusinessId());
        speechcraftModelDto.setName(temp.getName());
        List<Integer> list = disposeParam(speechcraftModelDto, user);
        int flag = speechcraftMapper.insertSelective(speechcraftModelDto);
        tBusinessMapper.updateModifyDateById(speechcraftModelDto.getBusinessId());
        if (flag > 0 && list.size() > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", speechcraftModelDto.getId());
            jsonObject.put("type", 1);
            jsonObject.put("index", list);
            redisClient.lpush(recordSynthesis, jsonObject.toJSONString());
        }
        return flag > 0;
    }

    private List<Integer> disposeParam(SpeechcraftModelDto speechcraftModelDto, TmUser user) throws Exception {
        List<Integer> list = new ArrayList<>();
        //初始化数据
        Long companyId = speechcraftModelDto.getCompanyId();
        Speechcraft speechcraft = speechcraftMapper.selectByCompanyIdAndCraftId(companyId, speechcraftModelDto.getCraftId());
        String title = speechcraft.getName();
        if (StringUtils.isEmpty(companyId)) {
            throw new Exception("无法获取到当前用户的公司编号!");
        }
        Long businessId = speechcraftModelDto.getBusinessId();
        StringBuilder content = new StringBuilder("");
        StringBuilder recordFile = new StringBuilder("");
        StringBuilder recordDescribe = new StringBuilder("");
        StringBuilder record_state = new StringBuilder("");
        StringBuilder fileSizeCount = new StringBuilder("");
        Long fileSize = 0L;
        int notRecordingNum = 0;
        int recordState;

        long recordSize = 0L;

        //如果是小兵秘书的时候，把文件上传路径改为1,2,3,4
        int[] arr = {1,2,3,4};

        String filePath = companyId + "/BUSINESS-" + businessId;
        List<Map<String, String>> recordDetail = speechcraftModelDto.getRecordDetail();
        if (null != recordDetail) {
            Map<String, String> itemMap = null;
            for (int j = 0; j < recordDetail.size(); j++) {
                itemMap = recordDetail.get(j);
                String contentItem = itemMap.get("content").trim().replaceAll("&", "");//分段文本内容
                if (StringUtils.isEmpty(contentItem)) {
                    continue;
                }
                String fileIdItem = itemMap.get("recordFile");//分段文本对应的录音
                String recordDescribeItem = itemMap.get("recordDescribe");//分段文本对应的录音类型
                content.append(contentItem).append("&");
                if (TagUtils.isLabel(contentItem)) {
                    recordFile.append(contentItem).append("&");
                    recordDescribe.append("0&");
                    fileSizeCount.append("0&");
                } else {
                    String logContent = null;
                    String fileName = MD5.MD5_32bit(contentItem ) + ".wav";// 生成录音文件名
                    if (StringUtils.isEmpty(fileIdItem) || "0".equals(recordDescribeItem)) {
                        //tts合成音频并上传到ftp
                        TtsProperty ttsPropertyDto = null;
                        TBusinessProperty tBusinessProperty = tBusinessPropertyMapper.selectByBusinessIdAndType(businessId, BusinessPropertyType.BUSINESS_TTS.getCode());
                        if (null != tBusinessProperty && !StringUtils.isEmpty(tBusinessProperty.getPropertyValue())) {
                            ttsPropertyDto = JSONObject.parseObject(tBusinessProperty.getPropertyValue(), TtsProperty.class);
                        }
                        if (companyId == 2122L){
                            for (int i : arr) {
                                filePath = companyId + "/BUSINESS-" + businessId + "/" +i;
                                fileName = fileName.replaceAll(".wav",".mp3");
                                logger.info("=======filePath={}===========" + filePath);
                                recordSize = ttsService.ssttsToFTP(contentItem, filePath, fileName, ttsPropertyDto);
                            }
                        } else {
                            recordSize = ttsService.createRecordToFTP(contentItem, filePath, fileName, ttsPropertyDto);
                        }
                        if (recordSize > 0) {
                            recordFile.append(fileName).append("&");
                            fileSize += recordSize;
                        } else {
                            recordFile.append("*&");
                            list.add(j);
                        }

                        //数据处理
                        notRecordingNum += 1;
                        recordDescribe.append("0&");
                        record_state.append("0");
                        fileSizeCount.append(recordSize).append("&");
                        //处理相关日志---
                        logContent = "话术标题【" + title + "】【" + contentItem + "】的录音文件更新为TTS合成";
                    } else {
                        if (fileIdItem.contains(".wav")) {//处理未修改
                            //获取录音文件名
                            String[] temp_str = fileIdItem.split("/");
                            String[] temp_str1 = temp_str[temp_str.length - 1].split("\\?");
                            String oldFileSizeCount = speechcraftModelDto.getFileSizeCount();
                            String old_itemFileSize = oldFileSizeCount.split("&")[j];

                            fileSize += Long.valueOf(old_itemFileSize);
                            recordFile.append(temp_str1[0]).append("&");
                            recordDescribe.append(recordDescribeItem).append("&");
                            record_state.append("1");
                            fileSizeCount.append(old_itemFileSize).append("&");
                        } else {
                            // 修改录音文件状态
                            UploadFile uploadFile = uploadFileMapper.selectByPrimaryKey(Long.valueOf(fileIdItem));
                            // 重命名文件
                            Map<String, String> map = new HashMap<>();
                            map.put(uploadFile.getFileName(), fileName);
                            fileTransferService.rnameFilesByFTP(filePath, map);
                            uploadFile.setFileName(fileName);
                            uploadFile.setStatus(1);
                            uploadFile.setModifyDate(new Date());
                            uploadFileMapper.updateByPrimaryKeySelective(uploadFile);
                            recordFile.append(fileName).append("&");
                            //累加文件大小
                            fileSize += uploadFile.getFileSize();
                            recordDescribe.append("1&");
                            record_state.append("1");
                            fileSizeCount.append(uploadFile.getFileSize()).append("&");
                            logContent = "话术标题【" + title + "】【" + contentItem + "】的录音文件重新上传了";
                        }
                    }
                    userOperateRecordService.insertForRecordFile(logContent, businessId, user);
                }
            }
        }
        if (content.length() <= 0) {
            throw new NullPointerException("文本内容为null!");
        }
        //数据处理
        recordFile.deleteCharAt(recordFile.length() - 1);
        content.deleteCharAt(content.length() - 1);
        recordDescribe.deleteCharAt(recordDescribe.length() - 1);
        fileSizeCount.deleteCharAt(fileSizeCount.length() - 1);
        String temp_recordDescribe = recordDescribe.toString();
        String temp_record_state = record_state.toString();
        if (temp_record_state.contains("0")) {
            if (temp_record_state.contains("1")) {
                recordState = 1;
            } else {
                recordState = 0;
            }
        } else {
            if (temp_record_state.contains("1")) {
                recordState = 2;
            } else {
                recordState = 0;
            }
        }

        speechcraftModelDto.setNotRecordingNum(notRecordingNum);
        speechcraftModelDto.setIsRecord(temp_recordDescribe.contains("1") ? 0 : 1);
        speechcraftModelDto.setContent(content.toString());
        speechcraftModelDto.setRecordDescribe(recordDescribe.toString());
        speechcraftModelDto.setRecordFile(recordFile.toString());
        speechcraftModelDto.setFileSize(fileSize);
        speechcraftModelDto.setFileSizeCount(fileSizeCount.toString());
        speechcraftModelDto.setRecordState(recordState);
        speechcraftModelDto.setIsRecord(1);
        return list;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean deleteById(Long id) throws Exception {
        Speechcraft speechcraft = selectByPrimaryKey(id);
        int flag = speechcraftMapper.deleteByPrimaryKey(id);
        tBusinessMapper.updateModifyDateById(speechcraft.getBusinessId());
        return flag > 0;
    }

    @Override
    public Map<String, String> clone(TmUser user, Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId) {
        List<Speechcraft> speechcrafts = speechcraftMapper.selectByCompanyIdAndBusinessId(sourceCompanyId, sourceBusinessId);
        Map<String, String> craftIdMap = new HashMap<>();
        Date date = new Date();
        for (Speechcraft speechcraft : speechcrafts) {
            Speechcraft targetSpeechcraft = new Speechcraft();
            BeanUtils.copyProperties(speechcraft, targetSpeechcraft, "id", "modifyDate");
            String craftId;
            if (craftIdMap.containsKey(speechcraft.getCraftId())) {
                craftId = craftIdMap.get(speechcraft.getCraftId());
            } else {
                craftId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                craftIdMap.put(speechcraft.getCraftId(), craftId);
            }
            if (!StringUtils.isEmpty(speechcraft.getFoucs())) {
                String focusNames = tBusinessFocusService.convertFocusIdsToFocusNames(sourceCompanyId, sourceBusinessId, speechcraft.getFoucs());
                String foucsIds = tBusinessFocusService.convertFocusNamesToFocusIds(user, targetBusinessId, focusNames);
                targetSpeechcraft.setFoucs(foucsIds);
            }
            targetSpeechcraft.setCraftId(craftId);
            targetSpeechcraft.setCompanyId(targetCompanyId);
            targetSpeechcraft.setBusinessId(targetBusinessId);
            targetSpeechcraft.setCreateDate(date);
            targetSpeechcraft.setModifyDate(date);
            speechcraftMapper.insertSelective(targetSpeechcraft);
        }
        return craftIdMap;
    }

    @Override
    public void node(Map<String, Object> map, HttpServletResponse xls) {
        List<Speechcraft> nodes = speechcraftMapper.node(map);
        List<KnowledgeQuestion> qas = knowledgeQuestionMapper.qas(map);
        List<QCommonCraft> qCommonCrafts = qCommonCraftMapper.qcommon(map);
        String fileName = "节点话术";
        String fileName2 = "问答话术";
        String fileName3 = "通用话术";
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(fileName);
        HSSFSheet sheet2 = wb.createSheet(fileName2);
        HSSFSheet sheet3 = wb.createSheet(fileName3);
        CellStyle style = ExportUtil.getCellStyle(wb);
        String[] headers = {"节点名称", "内容"};
        String[] headers2 = {"节点名称", "问题", "关键词", "答案"};
        String[] headers3 = {"节点名称", "问题", "关键词", "内容"};
        ExportUtil.setAllHeader(wb, sheet, headers, style);
        ExportUtil.setAllHeader(wb, sheet2, headers2, style);
        ExportUtil.setAllHeader(wb, sheet3, headers3, style);
        //节点话术
        for (int i = 0; i < nodes.size(); i++) {
            Speechcraft t = nodes.get(i);
            String[] cells = new String[]{
                    t.getName(), t.getContent()
            };
            ExportUtil.setAllCell(wb, sheet, i + 1, cells, style);
        }
        //问答话术
        for (int i = 0; i < qas.size(); i++) {
            KnowledgeQuestion t = qas.get(i);
            List<KnowledgeAnswer> answers = knowledgeAnswerMapper.selectKnowledgeAnswerByKnowledgeId(t.getKnowledgeId());
            if (!CollectionUtils.isEmpty(answers)) {
                for (KnowledgeAnswer a : answers) {
                    String[] cells = new String[]{
                            t.getName(), t.getQuestion(), t.getKeyWord(), a.getAnswer()
                    };
                    ExportUtil.setAllCell(wb, sheet2, i + 1, cells, style);
                }
            }
        }
        //通用话术
        for (int i = 0; i < qCommonCrafts.size(); i++) {
            QCommonCraft q = qCommonCrafts.get(i);
            /*String[] cells = new String[]{
                    q.getName(), q.getQuestion(), q.getKeyWord()
            };
            ExportUtil.setAllCell(wb, sheet3, i + 1, cells, style);*/
            List<ACommonCraft> aCommonCrafts = aCommonCraftMapper.selectByCraftIdAndCompanyIdAndBusinessId(q.getCraftId(), q.getCompanyId(), q.getBusinessId());
            if (!CollectionUtils.isEmpty(aCommonCrafts)) {
                for (ACommonCraft a : aCommonCrafts) {
                    String[] cells = new String[]{
                            q.getName(), q.getQuestion(), q.getKeyWord(),a.getContent()
                    };
                    ExportUtil.setAllCell(wb, sheet3, i + 1, cells, style);
                }
            }else {
                String[] cells = new String[]{
                        q.getName(), q.getQuestion(), q.getKeyWord()
                };
                ExportUtil.setAllCell(wb, sheet3, i + 1, cells, style);
            }
        }

        ExportUtil.exportXls(wb, fileName, xls);
        ExportUtil.exportXls(wb, fileName2, xls);
        ExportUtil.exportXls(wb, fileName3, xls);
    }

}
