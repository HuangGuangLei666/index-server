package com.pl.indexserver.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pl.indexserver.model.MenuDto;
import com.pl.indexserver.model.PermissionDto;
import com.pl.indexserver.service.AuthService;
import com.pl.indexserver.service.UserService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.mapper.TAuhPermissionMapper;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private TAuhPermissionMapper auhPermissionMapper;
    @Autowired
    private UserService userService;

    @Override
    public String getUserAuth(String userid) throws Exception {
        // 准备一个String集合用于存储到redis
        StringBuilder str = new StringBuilder();
        // 查询用户权限
        List<PermissionDto> userPermissions = auhPermissionMapper.selectByUserIdOrPositionId(userid, null);
        int size = userPermissions.size();
        // 优先按用户独立的权限数据执行，若用户无独立权限数据则按用户岗位的通用权限数据执行
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                PermissionDto permissionDto = userPermissions.get(i);
                int detailId = permissionDto.getDetailId();
                if (0 == detailId) {
                    str.append(permissionDto.getMenuId() + ",");
                } else {
                    str.append(detailId + ",");
                }
            }
        } else {
            // 获取岗位的权限信息
            List<Integer> positionList = userService.selectPositionIdByUserId(userid);
            for (int j = 0; j < positionList.size(); j++) {
                Integer integer = positionList.get(j);
                if (null != integer) {
                    List<PermissionDto> positionPermissions = auhPermissionMapper.selectByUserIdOrPositionId(null,
                            integer.toString());
                    for (int x = 0; x < positionPermissions.size(); x++) {
                        PermissionDto permissionDto = positionPermissions.get(x);
                        if (null != permissionDto) {
                            Integer detailId = permissionDto.getDetailId();
                            if (null == detailId || 0 == detailId) {
                                str.append(permissionDto.getMenuId() + ",");
                            } else {
                                str.append(detailId + ",");
                            }
                        }
                    }
                }
            }
        }
        return str.toString();
    }

    @Override
    public List<MenuDto> selectByIsValidate(int isValidate) throws Exception {
        return auhPermissionMapper.selectByIsValidate(isValidate);
    }

    @Override
    public Map<String, Integer> selectIsValidate() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        List<MenuDto> list = selectByIsValidate(CommonConstant.IS_VALIDATE);
        for (int i = 0; i < list.size(); i++) {
            MenuDto menuDto = list.get(i);
            map.put(menuDto.getUrl(), menuDto.getId());
        }
        return map;
    }

}
