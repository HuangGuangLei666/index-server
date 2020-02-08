package com.pl.mapper;

import com.pl.indexserver.query.BaseQuery;

import java.util.List;

public interface BaseMapper<T> {

    /**
     * 根据主键删除对应数据
     *
     * @param id 主键id
     * @return
     */
    int deleteByPrimaryKey(String id);

    /**
     * 新增一条数据(完整数据)
     *
     * @param t 对应模型数据
     * @return
     */
    int insert(T t);

    /**
     * 新增一条数据(只新增有值属性)
     *
     * @param t 对应模型数据
     * @return
     */
    int insertSelective(T t);

    /**
     * 根据主键查找对应数据
     *
     * @param id 主键id
     * @return 返回查询到的结果
     */
    T selectByPrimaryKey(String id);

    /**
     * 查询所有数据
     *
     * @return 返回查询到的结果集
     */
    List<T> selectAll();

    /**
     * 修改一条数据(完整数据)
     *
     * @param t 对应模型数据
     * @return
     */
     int updateByPrimaryKey(T t);

    /**
     * 修改一条数据(只修改有值属性)
     *
     * @param t 对应模型数据
     * @return
     */
    int updateByPrimaryKeySelective(T t);

    /**
     * 根据查询条件统计总数
     *
     * @param baseQuery 查询对象
     * @return 返回统计结果
     */
    Long countByQuery(BaseQuery baseQuery);

    /**
     * 根据查询的条件查询对应数据
     *
     * @param baseQuery 查询对象
     * @return 返回查询到的结果集
     */
    List<T> selectByQuery(BaseQuery baseQuery);
}
