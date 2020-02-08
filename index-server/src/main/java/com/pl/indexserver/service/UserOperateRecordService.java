package com.pl.indexserver.service;

import com.pl.indexserver.model.CallTaskReturnDto;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.untils.OperateType;
import com.pl.model.TmUser;
import com.pl.model.UserOperateRecord;

public interface UserOperateRecordService {
    int deleteByPrimaryKey(Long id);

    int insert(UserOperateRecord record);

    int insertSelective(UserOperateRecord record);

    UserOperateRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserOperateRecord record);

    int updateByPrimaryKey(UserOperateRecord record);

    Page getLogger(CallTaskReturnDto callTask,int type);

    /**
     * 分页获取日志信息
     *
     * @param userId
     * @param objectType
     * @param object
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page queryUserOperateRecordByPage(String userId, Integer objectType, String object, Integer pageIndex, Integer pageSize);

    /**
     * 插入操作日志，记录对象修改日志
     *
     * @param tmUser
     * @param objectType
     * @param object
     * @param operateId
     * @param operateName
     * @param remark
     * @param opType
     * @param opInfo
     * @param oldObject
     * @param newObject
     * @return
     */
    int saveUserOperateRecord(TmUser tmUser, Integer objectType, String object, String operateId,
                              String operateName, String remark, OperateType opType, String opInfo,
                              Object oldObject, Object newObject);

    int saveUserOperateRecord(TmUser tmUser, Integer objectType,String operateId,
                              String operateName, String remark, OperateType opType, String opInfo,
                              Object oldObject, Object newObject);

    /**
     * 插入修改录音文件的详细数据
     * @param content  日志内容
     * @param businessId    智库标识
     * @param user  用户信息
     * @return
     */
    void insertForRecordFile(String content,Long businessId,TmUser user);

}
