package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.FileTransferService;
import com.pl.indexserver.service.TSetService;
import com.pl.mapper.TSetMapper;
import com.pl.model.wx.TSet;
import com.pl.model.xbms.PrologueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/26
 */
@Service
public class TSetServiceImpl implements TSetService {

    private static final Logger logger = LoggerFactory.getLogger(TSetServiceImpl.class);

    @Autowired
    private TSetMapper tSetMapper;
    @Autowired
    private FileTransferService fileTransferService;

    @Override
    public int updateFriendSet(Integer id, Integer userId, String type, Integer voiceId, String phone) {
        return tSetMapper.updateFriendSet(id,userId,type,voiceId,phone);
    }

    @Override
    public int updateGroupSet(Integer id, Integer userId, String type, Integer voiceId, Integer groupId) {
        return tSetMapper.updateGroupSet(id, userId, type, voiceId, groupId);
    }

    @Override
    public TSet selectBusinessVoiceByUserIdAndValue(Integer userId, String phone) {
        return tSetMapper.selectBusinessVoiceByUserIdAndValue(userId,phone);
    }

    @Override
    public TSet selectBusinessVoiceByUserIdOrValue(Integer userId, Integer groupId) {
        return tSetMapper.selectBusinessVoiceByUserIdOrValue(userId,groupId);
    }

    @Override
    public TSet selectTSetByIdUserIdAndType(Integer id, Integer userId, String type) {
        return tSetMapper.selectTSetByIdUserIdAndType(id,userId,type);
    }

    @Override
    public void addTSetList(List<TSet> tSetList) {
        tSetMapper.addTSetList(tSetList);
    }

    @Override
    public List<TSet> selectByUserIdAndValue(Integer userId, String value) {
        return tSetMapper.selectByUserIdAndValue(userId,value);
    }

    @Override
    public List<TSet> selectByUserId(Integer userId) {
        return tSetMapper.selectByUserId(userId);
    }

    @Override
    public List<TSet> selectByOperationId(Integer aLong) {
        return tSetMapper.selectByOperationId(aLong);
    }

    @Override
    public void delOperationRecordByOperationId(Integer operationId) {
        tSetMapper.delOperationRecordByOperationId(operationId);
    }

    @Override
    public List<TSet> selectContentByUserId(Integer userId) {
        return tSetMapper.selectContentByUserId(userId);
    }

    @Override
    public int addContentSet(TSet set) {
        return tSetMapper.addContentSet(set);
    }

    @Override
    public List<TSet> selectprologueByUserId(Integer userId) {
        return tSetMapper.selectprologueByUserId(userId);
    }

    @Override
    public CheckSmsCodeResp insertSet(TSet set) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tSetMapper.addMyContentSet(set);
        logger.info("=========i={}========", i);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("开场白设置失败");
            return resp;
        }

        resp.setRetCode(0);
        resp.setRetDesc("开场白设置成功");
        return resp;
    }


    @Override
    public PrologueDto prologueTypeQry(Integer userId) {
        //拼接上传到服务器自己录音的url
        String url = "https://ai.yousayido.net/recordManagement/2122/BUSINESS-134680578/own_recordings/"+userId+"/";
        PrologueDto prologueDto = new PrologueDto();
        //查询最近的一次设置开场白的内容和类型
        TSet set = tSetMapper.selectCurrentSetContentByUserId(userId);
        if (!StringUtils.isEmpty(set)){
            String type = set.getType();
            prologueDto.setId(set.getId());
            prologueDto.setType(type);
            prologueDto.setCreateTime(set.getCreateTime());
            prologueDto.setContent(set.getContent());
            prologueDto.setRecordingUrl(("1".equals(type))?(url+set.getFileName()):null);
            logger.info("======url+set.getFileName()========",url+set.getFileName());
            return prologueDto;
        }
        return prologueDto;
    }

    @Override
    public CheckSmsCodeResp prologueDel(Integer id, Integer userId, String fileName) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        String[] fileNames = new String[2];
        fileNames[0] = fileName;
        //服务器录音路径
        String recordingPath = "2122/BUSINESS-134680578/own_recordings/" + userId;
        int delete = tSetMapper.deleteByPrimaryKey(id);
        if (delete < 1){
            resp.setRetCode(1);
            resp.setRetDesc("删除失败~~");
            return resp;
        }
        try {
            boolean deleteFiles = fileTransferService.deleteFilesByFTP(recordingPath, fileNames);
            if (deleteFiles){
                resp.setRetCode(0);
                resp.setRetDesc("删除成功~~");
                return resp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.setRetCode(1);
        resp.setRetDesc("删除失败~~");
        return resp;
    }

    @Override
    public TSet selectById(Integer id) {
        return tSetMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer prologueIdQry(Integer userId) {
        Integer id = null;
        //查询最近的一次设置开场白的内容和类型
        TSet set = tSetMapper.selectCurrentSetContentByUserId(userId);
        if (!StringUtils.isEmpty(set)){
            id = set.getId();
            return id;
        }
        return id;
    }

}
