package com.pl.mapper;


import com.pl.model.wx.TSuggestion;

public interface TSuggestionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TSuggestion record);

    int insertSelective(TSuggestion record);

    TSuggestion selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TSuggestion record);

    int updateByPrimaryKey(TSuggestion record);

    int insertSuggestion(TSuggestion suggestion);
}