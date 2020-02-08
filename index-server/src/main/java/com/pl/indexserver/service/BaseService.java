package com.pl.indexserver.service;

import com.pl.indexserver.query.BaseQuery;
import com.pl.indexserver.query.Page;

import java.util.List;

public interface BaseService<T> {

    /**
     * 根据主键删除对应数据
     *
     * @param primaryKey 主键id
     * @return 返回操作结果
     * @throws Exception
     */
    boolean deleteByPrimaryKey(String primaryKey) throws Exception;

    /**
     * 新增一条数据(完整数据)
     *
     * @param t 对应模型数据
     * @return 返回操作结果
     * @throws Exception
     */
    boolean insert(T t) throws Exception;

    /**
     * 新增一条数据(只新增有值属性)
     *
     * @param t 对应模型数据
     * @return 返回操作结果
     * @throws Exception
     */
    boolean insertSelective(T t) throws Exception;

    /**
     * 根据主键查找对应数据
     *
     * @param primaryKey 主键id
     * @return 返回查询到的结果
     * @throws Exception
     */
    T selectByPrimaryKey(String primaryKey) throws Exception;

    /**
     * 查询所有属性
     *
     * @return 返回查询到的结果集
     * @throws Exception
     */
    List<T> selectAll() throws Exception;

    /**
     * 修改一条数据(完整数据)
     *
     * @param t 对应模型数据
     * @return 返回操作结果
     * @throws Exception
     */
    boolean updateByPrimaryKey(T t) throws Exception;

    /**
     * 修改一条数据(只修改有值属性)
     *
     * @param t 对应模型数据
     * @return 返回操作结果
     * @throws Exception
     */
    boolean updateByPrimaryKeySelective(T t) throws Exception;

    /**
     * 根据查询条件统计总数
     *
     * @param baseQuery 查询对象
     * @return 返回统计结果
     * @throws Exception
     */
    Long countByQuery(BaseQuery baseQuery) throws Exception;

    /**
     * 根据查询的条件查询对应数据(用于分页)
     *
     * @param baseQuery 查询对象
     * @return 返回查询到的结果集
     * @throws Exception
     */
    Page<T> selectByQuery(BaseQuery baseQuery) throws Exception;

}
