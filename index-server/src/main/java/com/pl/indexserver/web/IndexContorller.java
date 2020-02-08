package com.pl.indexserver.web;

import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.service.CallTaskService;
import com.pl.indexserver.service.IndexService;
import com.pl.indexserver.untils.DateUtils;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TmUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

//呼叫系统首页页面接口类
@RestController
@RequestMapping("/busiManagement/page")
public class IndexContorller {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private IndexService indexService;

    @RequestMapping("/index")
    public ReturnMsg getIndexPageData(HttpServletRequest request){
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            returnMsg = indexService.getIndexPageData(user.getCompanyId(),user.getUserid());
        }catch (Exception e){
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("查询出错！");
        }
        return returnMsg;
    }

    @RequestMapping("/index2")
    public ReturnMsg getIndexPageData2(HttpServletRequest request, @RequestParam int type){
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            returnMsg = indexService.getIndexPageData2(type,user.getCompanyId(),user.getUserid());
        }catch (Exception e){
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("查询出错！");
        }
        return  returnMsg;
    }

}
