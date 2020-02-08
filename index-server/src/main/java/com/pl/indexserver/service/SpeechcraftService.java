package com.pl.indexserver.service;

import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.SpecialtyTalkDto;
import com.pl.indexserver.model.SpeechcraftModelDto;
import com.pl.model.Speechcraft;
import com.pl.model.TmUser;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface SpeechcraftService {

    ReturnMsg deleteByPrimaryKey(Long id);

    int insert(Speechcraft record);

    ReturnMsg insertSelective(Long companyId, Long businessId, String name, String content, Long msgtemplId, MultipartFile file);

    Speechcraft selectByPrimaryKey(Long id);

    ReturnMsg updateByPrimaryKeySelective(String msgBody);

    int updateByPrimaryKey(Speechcraft record);

    int selectSpecialtyTalkCount(Long id);

    int selectSpecialtyTalkCountByIsRecord(Long id);

    int selectTDialogCountByIStatus(Long id);

    int selectTDialogCountByIsIntention(Long id);

    List<SpecialtyTalkDto> selectSpeechcraftAllForName(Map<String, Object> map);

    /**
     * 根据话术标识获取相应的专有话术
     *
     * @param craftId 话术标识
     * @return 返回获取到的结果集
     * @throws Exception
     */
    List<Speechcraft> selectByCraftId(String craftId) throws Exception;

    /**
     * 批量更新话术数据
     *
     * @param speechcrafts 话术对象集合
     * @return 返回操作结果
     * @throws Exception
     */
    Boolean updateByPrimaryKey(List<Speechcraft> speechcrafts) throws Exception;

    /**
     * 根据话术表示查询专用话术详情
     *
     * @param craftId 话术标识
     * @param flag    是否处理录音文件
     * @return
     * @throws Exception
     */
    SpeechcraftModelDto selectDtoByCraftId(String craftId, boolean flag) throws Exception;

    /**
     * 根据话术标识删除删除相关数据
     *
     * @param craftIid 话术标识
     * @return 返回操作结果
     * @throws Exception
     */
    boolean deleteByCraftId(String craftIid) throws Exception;

    /**
     * 根据话术Dto数据转化为话术模型数据并保存
     *
     * @param speechcraftModelDto
     * @return
     * @throws Exception
     */
    Boolean insertBasicByDto(SpeechcraftModelDto speechcraftModelDto) throws Exception;

    /**
     * 根据话术Dto数据转化为话术模型数据保存
     *
     * @param speechcraftModelDto
     * @return
     * @throws Exception
     */
    Boolean updateBasicByDto(SpeechcraftModelDto speechcraftModelDto) throws Exception;

    /**
     * 根据公司id统计其专用话术录音文件所占用的空间大小
     *
     * @param companyId 公司id
     * @return 返回统计结果
     * @throws Exception
     */
    long countFileSizeByCompanyId(Long companyId) throws Exception;

    /**
     * 专用话术修改(仅用于单条数据)
     * @param speechcraftModelDto   专用话术模型对象
     * @return  返回操作结果
     * @throws Exception
     */
    boolean updateAndRecardLogToContentRelevant(SpeechcraftModelDto speechcraftModelDto, TmUser user) throws Exception;
    /**
     * 专用话术新增(仅用于单条数据)
     * @param speechcraftModelDto   专用话术模型对象
     * @return  返回操作结果
     * @throws Exception
     */
    boolean insertAndRecardLogToContentRelevant(SpeechcraftModelDto speechcraftModelDto, TmUser user) throws Exception;
    /**
     * 专用话术删除(仅用于单条数据)
     * @param id   主键id
     * @return  返回操作结果
     * @throws Exception
     */
    boolean deleteById(Long id) throws Exception;

    /**
     * 克隆智库下所有话术
     *
     * @param user
     * @param sourceCompanyId
     * @param sourceBusinessId
     * @param targetCompanyId
     * @param targetBusinessId
     * @return
     */
    Map<String, String> clone(TmUser user, Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId);

    /**
     * hgl
     * @param map
     * @param response
     */
    void node(Map<String, Object> map, HttpServletResponse response);
}
