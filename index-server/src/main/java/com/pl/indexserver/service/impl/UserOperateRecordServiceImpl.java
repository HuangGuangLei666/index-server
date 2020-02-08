package com.pl.indexserver.service.impl;

import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.pl.indexserver.model.CallTaskReturnDto;
import com.pl.indexserver.model.UserOperateRecordDto;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.service.UserOperateRecordService;
import com.pl.indexserver.untils.OperateType;
import com.pl.indexserver.untils.ReflectionUtils;
import com.pl.mapper.UserOperateRecordMapper;
import com.pl.model.TmUser;
import com.pl.model.UserOperateRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserOperateRecordServiceImpl implements UserOperateRecordService {

    private static Logger logger = LoggerFactory.getLogger(UserOperateRecordServiceImpl.class);

    @Autowired
    private UserOperateRecordMapper userOperateRecordMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return userOperateRecordMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(UserOperateRecord record) {
        int i = 0;
        try {
            i = userOperateRecordMapper.insert(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public int insertSelective(UserOperateRecord record) {
        int i = 0;
        try {
            i = userOperateRecordMapper.insertSelective(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public UserOperateRecord selectByPrimaryKey(Long id) {
        return userOperateRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(UserOperateRecord record) {
        return userOperateRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(UserOperateRecord record) {
        return userOperateRecordMapper.updateByPrimaryKey(record);
    }

    @Override
    public Page getLogger(CallTaskReturnDto callTask, int type) {
        PageHelper.startPage(callTask.getPageIndex(), callTask.getPageNum());
        List<UserOperateRecordDto> list = userOperateRecordMapper.getLogger(callTask, type);
        return new Page<UserOperateRecordDto>(PageHelper.freeTotal(), list);
    }

    @Override
    public Page queryUserOperateRecordByPage(String userId, Integer objectType, String object, Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex, pageSize);
        List<UserOperateRecordDto> userOperateRecordDtos = userOperateRecordMapper.selectByObject(userId, objectType, object);
        return new Page<UserOperateRecordDto>(PageHelper.freeTotal(), userOperateRecordDtos);
    }


    @Override
    public int saveUserOperateRecord(TmUser user, Integer objectType, String object, String operateId,
                                     String operateName, String remark, OperateType opType, String opInfo,
                                     Object oldObject, Object newObject) {
        int flag = 0;
        try {
            UserOperateRecord userOperateRecord = new UserOperateRecord();
            userOperateRecord.setUserId(user.getUserid());
            userOperateRecord.setUserName(user.getUsername());
            userOperateRecord.setCompanyId(user.getCompanyId());
            userOperateRecord.setObjectType(objectType);
            userOperateRecord.setObject(object);
            userOperateRecord.setOperateId(operateId);
            userOperateRecord.setOperateName(operateName);
            userOperateRecord.setRemark(remark);
            switch (opType) {
                case CREATE:
                    StringBuilder detailSb = new StringBuilder();
                    detailSb.append(operateName);
                    if (!StringUtils.isEmpty(opInfo)) {
                        detailSb.append(",").append(opInfo);
                    }
                    userOperateRecord.setDetail(detailSb.toString());
                    break;
                case DELETE:
                    detailSb = new StringBuilder();
                    detailSb.append(operateName);
                    if (!StringUtils.isEmpty(opInfo)) {
                        detailSb.append(",").append(opInfo);
                    }
                    userOperateRecord.setDetail(detailSb.toString());
                    break;
                default:
                    try {
                        List<Map<String, Object>> changelist = ReflectionUtils.compareTwoClass(oldObject, newObject);
                        StringBuilder updateSb = new StringBuilder();
                        updateSb.append(operateName);
                        if (!StringUtils.isEmpty(opInfo)) {
                            updateSb.append(",").append(opInfo).append(",");
                        }
                        for (Map<String, Object> map : changelist) {
                            if ("keyword".equals(map.get("type"))) {
                                if (StringUtils.isEmpty(map.get("old"))) {
                                    updateSb.append(String.format("将[%s]新增[%s];", map.get("name"), map.get("new")));
                                } else if (StringUtils.isEmpty(map.get("new"))) {
                                    updateSb.append(String.format("将[%s]删除[%s];", map.get("name"), map.get("old")));
                                } else {
                                    updateSb.append(String.format("将[%s]删除[%s],新增[%s];", map.get("name"), map.get("old"), map.get("new")));
                                }
                            } else {
                                if (StringUtils.isEmpty(map.get("old"))) {
                                    updateSb.append(String.format("将[%s]修改为[%s];", map.get("name"), map.get("new")));
                                } else {
                                    updateSb.append(String.format("将[%s]从[%s]修改为[%s];", map.get("name"), map.get("old"), map.get("new")));
                                }
                            }
                        }
                        String updateStr = updateSb.toString();
                        if (updateStr.length() > 0 && (updateStr.lastIndexOf(";") == updateStr.length() - 1 ||
                                updateStr.lastIndexOf(",") == updateStr.length() - 1)) {
                            updateStr = updateStr.substring(0, updateStr.length() - 1);
                        }
                        userOperateRecord.setDetail(updateStr);
                    } catch (Exception ex) {
                        logger.error("生成用户日志，对比用户操作记录异常", ex);
                    }
                    break;
            }
            userOperateRecord.setCreateDate(new Date());
            flag = userOperateRecordMapper.insert(userOperateRecord);
        } catch (Exception e) {
            logger.info("插入操作日志出现异常:  ", e);
        }
        return flag;
    }

    @Override
    public int saveUserOperateRecord(TmUser tmUser, Integer objectType, String operateId, String operateName, String remark, OperateType opType, String opInfo, Object oldObject, Object newObject) {
        int flag = 0;
        try {
            UserOperateRecord userOperateRecord = new UserOperateRecord();
            userOperateRecord.setUserId(tmUser.getUserid());
            userOperateRecord.setUserName(tmUser.getUsername());
            userOperateRecord.setCompanyId(tmUser.getCompanyId());
            userOperateRecord.setObjectType(objectType);
            userOperateRecord.setOperateId(operateId);
            userOperateRecord.setOperateName(operateName);
            userOperateRecord.setRemark(remark);
            switch (opType) {
                case CREATE:
                    StringBuilder detailSb = new StringBuilder();
                    detailSb.append(operateName);
                    if (!StringUtils.isEmpty(opInfo)) {
                        detailSb.append(",").append(opInfo);
                    }
                    userOperateRecord.setDetail(detailSb.toString());
                    break;
                case DELETE:
                    detailSb = new StringBuilder();
                    detailSb.append(operateName);
                    if (!StringUtils.isEmpty(opInfo)) {
                        detailSb.append(",").append(opInfo);
                    }
                    userOperateRecord.setDetail(detailSb.toString());
                    break;
                default:
                    try {
                        List<Map<String, Object>> changelist = ReflectionUtils.compareTwoClass(oldObject, newObject);
                        StringBuilder updateSb = new StringBuilder();
                        updateSb.append(operateName);
                        if (!StringUtils.isEmpty(opInfo)) {
                            updateSb.append(",").append(opInfo).append(",");
                        }
                        for (Map<String, Object> map : changelist) {
                            if ("keyword".equals(map.get("type"))) {
                                if (StringUtils.isEmpty(map.get("old"))) {
                                    updateSb.append(String.format("将[%s]新增[%s];", map.get("name"), map.get("new")));
                                } else if (StringUtils.isEmpty(map.get("new"))) {
                                    updateSb.append(String.format("将[%s]删除[%s];", map.get("name"), map.get("old")));
                                } else {
                                    updateSb.append(String.format("将[%s]删除[%s],新增[%s];", map.get("name"), map.get("old"), map.get("new")));
                                }
                            } else {
                                if (StringUtils.isEmpty(map.get("old"))) {
                                    updateSb.append(String.format("将[%s]修改为[%s];", map.get("name"), map.get("new")));
                                } else {
                                    updateSb.append(String.format("将[%s]从[%s]修改为[%s];", map.get("name"), map.get("old"), map.get("new")));
                                }
                            }
                        }
                        String updateStr = updateSb.toString();
                        if (updateStr.length() > 0 && (updateStr.lastIndexOf(";") == updateStr.length() - 1 ||
                                updateStr.lastIndexOf(",") == updateStr.length() - 1)) {
                            updateStr = updateStr.substring(0, updateStr.length() - 1);
                        }
                        userOperateRecord.setDetail(updateStr);
                    } catch (Exception ex) {
                        logger.error("生成用户日志，对比用户操作记录异常", ex);
                    }
                    break;
            }
            userOperateRecord.setCreateDate(new Date());
            flag = userOperateRecordMapper.insert(userOperateRecord);
        } catch (Exception e) {
            logger.info("插入操作日志出现异常:  ", e);
        }
        return flag;
    }

    @Override
    public void insertForRecordFile(String content, Long id, TmUser user) {
        try {
            UserOperateRecord userOperateRecord = new UserOperateRecord();
            userOperateRecord.setUserId(user.getUserid());
            userOperateRecord.setUserName(user.getUsername());
            userOperateRecord.setCompanyId(user.getCompanyId());
            userOperateRecord.setObjectType(2);
            userOperateRecord.setObject(id.toString());
            userOperateRecord.setOperateId("");
            userOperateRecord.setOperateName("上传录音文件");
            userOperateRecord.setRemark(content);
            userOperateRecord.setDetail(content);
            userOperateRecord.setCreateDate(new Date());
            userOperateRecordMapper.insertSelective(userOperateRecord);
        } catch (Exception e) {
            logger.info("插入录音文件相关日志异常:  ", e);
        }
    }
}
