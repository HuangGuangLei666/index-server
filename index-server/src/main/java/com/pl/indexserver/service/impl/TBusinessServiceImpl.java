package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.TBusinessModelDto;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.FileUtil;
import com.pl.mapper.*;
import com.pl.model.DialogWorkflow;
import com.pl.model.SpeechcraftTag;
import com.pl.model.TBusiness;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class TBusinessServiceImpl implements TBusinessService {

    private static final Logger logger = LoggerFactory.getLogger(TBusinessServiceImpl.class);


    @Autowired
    private TBusinessMapper tBusinessMapperl;

    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private SpeechcraftService speechcraftService;
    @Autowired
    private TBusinessFocusService tBusinessFocusService;
    @Autowired
    private TBusinessConfigService tBusinessConfigService;
    @Autowired
    private ResponseModeService responseModeService;
    @Autowired
    private KnowledgeQuestionService knowledgeQuestionService;
    @Autowired
    private QCommonCraftService qCommonCraftService;
    @Autowired
    private FileTransferService fileTransferService;
    @Autowired
    private DialogWorkflowMapper dialogWorkflowMapper;
    @Autowired
    private ACommonCraftMapper aCommonCraftMapper;
    @Autowired
    private SpeechcraftMapper speechcraftMapper;
    @Autowired
    private KnowledgeAnswerMapper knowledgeAnswerMapper;
    @Autowired
    private SpeechcraftTagMapper speechcraftTagMapper;
    @Autowired
    private ResponseModeMapper responseModeMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Long id) {
        return tBusinessMapperl.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int insert(TBusiness record) {
        return tBusinessMapperl.insert(record);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int insertSelective(TBusiness record) {
        int ll = 0;
        //插入智库数据。
        int tb = tBusinessMapperl.insertSelective(record);
        int rm = responseModeMapper.getSysResponseModeSize(record.getCompanyId());
        if (rm == 0) {
            //插入系统响应方式
            ll = responseModeMapper.insertSysResponseMode(record.getCompanyId());
        }
        return tb + ll;
    }

    @Override
    public TBusiness selectByPrimaryKey(Long id) {
        return tBusinessMapperl.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int updateByPrimaryKeySelective(TBusiness record) {
        return tBusinessMapperl.updateByPrimaryKeySelective(record);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int updateByPrimaryKey(TBusiness record) {
        return tBusinessMapperl.updateByPrimaryKey(record);
    }

    @Override
    public List<TBusiness> getTBusinessListByCompany_id(Long company_id) throws Exception {
        return tBusinessMapperl.getTBusinessListByCompany_id(company_id);
    }

    @Override
    public List<TBusinessModelDto> getTBusinessDescribeListByCompanyId(String companyId) throws Exception {
        List<TBusinessModelDto> tBusinessDtos = tBusinessMapperl.getTBusinessDescribeListByCompanyId(companyId);
        if (null != tBusinessDtos) {
            for (TBusinessModelDto tBusinessModelDto : tBusinessDtos) {
                String modifyDate = tBusinessModelDto.getModifyDate();
                if (StringUtils.isEmpty(modifyDate)) {
                    tBusinessModelDto.setModifyDate(tBusinessModelDto.getCreateDate());
                }
            }
        }
        return tBusinessDtos;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Boolean insertByTBusinessModelDto(TBusinessModelDto tBusinessModelDto) throws Exception {
        TBusiness tBusiness = new TBusiness();
        tBusiness.setName(tBusinessModelDto.getName());
        tBusiness.setRemark(tBusinessModelDto.getRemark());
        tBusiness.setStatus(CommonConstant.STATUS_0);
        tBusiness.setCompanyId(tBusinessModelDto.getCompanyId());
        tBusiness.setTemplateName("");
        tBusiness.setAlgorithmAddr("");
        tBusiness.setControllAddr("");
        int i = insertSelective(tBusiness);
        return i > 0;
    }

    @Override
    public TBusinessModelDto selectById(Long id) {
        TBusinessModelDto tBusinessModelDto = new TBusinessModelDto();
        TBusiness tBusiness = selectByPrimaryKey(id);
        tBusinessModelDto.setId(tBusiness.getId());
        tBusinessModelDto.setName(tBusiness.getName());
        tBusinessModelDto.setCompanyId(tBusiness.getCompanyId());
        tBusinessModelDto.setRemark(tBusiness.getRemark());
        return tBusinessModelDto;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int clone(TmUser user, Long companyId, Long businessId, Long targetCompanyId) throws Exception {
        // 复制智库信息

        long t = System.currentTimeMillis();
        TBusiness oldTBusiness = tBusinessMapperl.selectByPrimaryKey(businessId);
        TBusiness targetTBusiness = new TBusiness();
        BeanUtils.copyProperties(oldTBusiness, targetTBusiness, "id", "createDate", "modifyDate");
        targetTBusiness.setCompanyId(targetCompanyId);
        targetTBusiness.setCreateDate(new Date());
        tBusinessMapperl.insertSelective(targetTBusiness);
        // 复制智库下所有关注点

        t = System.currentTimeMillis();
        tBusinessFocusService.clone(user, companyId, businessId, targetCompanyId, targetTBusiness.getId());
        logger.info("====costime=====111===" + (System.currentTimeMillis() -t) );
        // 复制智库配置
        t = System.currentTimeMillis();
        tBusinessConfigService.clone(user, companyId, businessId, targetCompanyId, targetTBusiness.getId());
        logger.info("=====costime====222===" + (System.currentTimeMillis() -t) );
        t = System.currentTimeMillis();
        // 复制智库下所有话术
        Map<String, String> craftIdMap = speechcraftService.clone(user, companyId, businessId, targetCompanyId, targetTBusiness.getId());
        // 复制智库下所有响应方式
        Map<Long, Long> responseModeId = new HashMap<>();
        if (!companyId.equals(targetCompanyId)) {
            responseModeId = responseModeService.clone(companyId, businessId, targetCompanyId, targetTBusiness.getId());
            logger.info("=====costime====555===" + (System.currentTimeMillis() -t) );
            t = System.currentTimeMillis();
        }
        // 复制智库下所有流程
        Map<Long, Long> flowIdsMap = workFlowService.clone(companyId, businessId, targetCompanyId, targetTBusiness.getId(), craftIdMap, responseModeId);
        if (flowIdsMap.size() == 0) {
            DialogWorkflow dialogWorkflow = new DialogWorkflow(targetCompanyId, targetTBusiness.getId(), "主流程", "{\"title\":\"newFlow\"}");
            dialogWorkflow.setModifyDate(new Date());
            dialogWorkflowMapper.insertSelective(dialogWorkflow);
        }
        // 复制智库下所有问答
        knowledgeQuestionService.clone(user, companyId, businessId, targetCompanyId, targetTBusiness.getId(), flowIdsMap);
        logger.info("====costime=====333===" + (System.currentTimeMillis() -t) );
        t = System.currentTimeMillis();
        // 复制智库下所有系统识别
        qCommonCraftService.clone(user, companyId, businessId, targetCompanyId, targetTBusiness.getId(), flowIdsMap);
        logger.info("====costime=====444===" + (System.currentTimeMillis() -t) );
        //复制相关标签
        if (!companyId.equals(targetCompanyId)) {
            cloneSpeechcraftTags(companyId, businessId, targetCompanyId, user);
        }
        String oldFilePath = "/mnt/tm/"+ companyId + "/BUSINESS-" + businessId;
        String newFilePath = "/mnt/tm/"+ targetCompanyId + "/BUSINESS-" + targetTBusiness.getId();
        t = System.currentTimeMillis();

        //另起一个线程拷贝智库文件
        new Thread(){
            @Override
            public void run() {
                FileUtil.copyDir(oldFilePath,newFilePath);
            }
        }.start();

//        FileUtil.copyDir(oldFilePath,newFilePath);
        logger.info("====costime=====666===" + (System.currentTimeMillis() - t ) );
        return 1;
    }

    @Override
    public int countSmsProbably(Long bussiness) {
        return tBusinessMapperl.countSmsProbably(bussiness);
    }

    private void cloneSpeechcraftTags(Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, TmUser user) throws Exception {
        List<SpeechcraftTag> speechcraftTags = getTagFromBusiness(sourceCompanyId, sourceBusinessId);
        Date date = new Date();
        for (SpeechcraftTag speechcraftTag : speechcraftTags) {
            speechcraftTag.setId(null);
            speechcraftTag.setCompanyId(targetCompanyId);
            speechcraftTag.setCreateBy(user.getUsername());
            speechcraftTag.setCreateDate(date);
            speechcraftTag.setUpdateDate(date);
            speechcraftTagMapper.insertSelective(speechcraftTag);
        }
    }

    /**
     * 获取指定智库下所使用到的标签
     *
     * @param companyId  公司标识
     * @param businessId 智库标识
     * @return 话术标签列表
     * @throws Exception
     */
    private List<SpeechcraftTag> getTagFromBusiness(Long companyId, Long businessId) throws Exception {
        List<SpeechcraftTag> tags = new ArrayList<>();
        List<SpeechcraftTag> allTags = speechcraftTagMapper.selectByCompanyId(companyId);
        for (SpeechcraftTag speechcraftTag : allTags) {
            String tagKey = speechcraftTag.getTagKey();
            if (speechcraftMapper.countLabelData(businessId, tagKey) > 0) {
                tags.add(speechcraftTag);
                continue;
            }
            if (aCommonCraftMapper.countLabelData(businessId, tagKey) > 0) {
                tags.add(speechcraftTag);
                continue;
            }
            if (knowledgeAnswerMapper.countLabelData(businessId, tagKey) > 0) {
                tags.add(speechcraftTag);
                continue;
            }
            if (responseModeMapper.countLabelData(companyId, tagKey) > 0) {
                tags.add(speechcraftTag);
            }
        }
        return tags;
    }
}
