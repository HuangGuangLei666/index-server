package com.pl.mapper.xbms;


import com.pl.model.xbms.Mechanism;

public interface MechanismMapper {

    Mechanism selectByUserId(Integer userId);
}