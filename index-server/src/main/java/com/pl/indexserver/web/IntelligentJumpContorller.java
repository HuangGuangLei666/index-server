package com.pl.indexserver.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.IntelligentJumpDto;
import com.pl.indexserver.model.KnowledgeQuestionDto;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.service.IntelligentJumpService;
import com.pl.indexserver.service.UserOperateRecordService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.OperateType;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TmUser;

@RestController
@RequestMapping(value = "/busiManagement")
public class IntelligentJumpContorller {

	private static final Logger logger = LoggerFactory
			.getLogger(IntelligentJumpContorller.class);

	@Autowired
	private IntelligentJumpService intelligentJumpService;

	@Autowired
	private RedisClient redisClient;
	@Autowired
	private UserOperateRecordService userOperateRecordService;

	@GetMapping(value = "/flowConfig/intelligentjump/query")
	public ReturnMsg queryIntelligentJump(HttpServletRequest request,
			@RequestParam("businessId") Long businessId) {
		ReturnMsg returnMsg = new ReturnMsg();
		TmUser user = GetUid.getUID(request, redisClient);
		if (StringUtils.isEmpty(user)) {
			returnMsg.setCode(1);
			returnMsg.setErrorMsg("登录过期，请重新登录！");
			return returnMsg;
		}
		try {

			List<IntelligentJumpDto> intelligentJumpDtos = intelligentJumpService
					.selectByBusinessId(businessId);
			returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
			returnMsg.setContent(null == intelligentJumpDtos ? new Object[0]
					: intelligentJumpDtos);
		} catch (Exception e) {
			returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
			logger.error("获取智能跳转列表页异常:", e);
		}
		return returnMsg;
	}

	@RequestMapping(value = "/flowConfig/intelligentjump/queryDetail")
	public ReturnMsg queryDetail(HttpServletRequest request,
			@RequestParam("id") Long id,
			@RequestParam("businessId") Long businessId) {
		ReturnMsg returnMsg = new ReturnMsg();
		TmUser user = GetUid.getUID(request, redisClient);
		if (StringUtils.isEmpty(user)) {
			returnMsg.setCode(1);
			returnMsg.setErrorMsg("登录过期，请重新登录！");
			return returnMsg;
		}
		try {
			returnMsg.setCode(CommonConstant.RETURN_SUCCEED);

			IntelligentJumpDto intelligentJumpDto = intelligentJumpService
					.selectByPrimaryKey(id, user.getCompanyId(), businessId);
			returnMsg.setContent(intelligentJumpDto);
		} catch (Exception e) {
			logger.error("获取知识库问答详情失败:", e);
			returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
		}
		return returnMsg;
	}

	@PostMapping(value = "/flowConfig/intelligentjump/delete")
	public ReturnMsg delete(HttpServletRequest request,
			@RequestParam("id") Long id,
			@RequestParam("businessId") String businessId) {
		ReturnMsg returnMsg = new ReturnMsg();

		TmUser user = GetUid.getUID(request, redisClient);
		if (StringUtils.isEmpty(user)) {
			returnMsg.setCode(1);
			returnMsg.setErrorMsg("登录已过期，请重新登录！");
			return returnMsg;
		}
		intelligentJumpService.deleteById(id);
		returnMsg.setCode(0);
		returnMsg.setErrorMsg("success");
		return returnMsg;
	}
	
	
	@PostMapping(value = "/flowConfig/intelligentjump/insertMap")
	public ReturnMsg insertMap(HttpServletRequest request,
			@RequestParam("id") String id,
			@RequestParam("type") Long type,
			@RequestParam("mapId") String mapId) {
		ReturnMsg returnMsg = new ReturnMsg();

		TmUser user = GetUid.getUID(request, redisClient);
		if (StringUtils.isEmpty(user)) {
			returnMsg.setCode(1);
			returnMsg.setErrorMsg("登录已过期，请重新登录！");
			return returnMsg;
		}
		intelligentJumpService.insertMap(id, type, mapId) ;
		return returnMsg;
	}

	@PostMapping(value = "/flowConfig/intelligentjump/deleteMap")
	public ReturnMsg deleteMap(HttpServletRequest request,
			@RequestParam("id") String id,
			@RequestParam("type") Long type,
			@RequestParam("mapId") String mapId) {
		ReturnMsg returnMsg = new ReturnMsg();

		TmUser user = GetUid.getUID(request, redisClient);
		if (StringUtils.isEmpty(user)) {
			returnMsg.setCode(1);
			returnMsg.setErrorMsg("登录已过期，请重新登录！");
			return returnMsg;
		}
		intelligentJumpService.deleteMap(id, type, mapId) ;
		return returnMsg;
	}	

	@PostMapping(value = "/flowConfig/intelligentjump/update")
	public ReturnMsg update(HttpServletRequest request,
			@RequestBody IntelligentJumpDto intelligentJumpDto) {
		ReturnMsg returnMsg = new ReturnMsg();
		TmUser user = GetUid.getUID(request, redisClient);
		if (StringUtils.isEmpty(user)) {
			returnMsg.setCode(1);
			returnMsg.setErrorMsg("登录已过期，请重新登录！");
			return returnMsg;
		}

		try {

			Long id = intelligentJumpDto.getId();
			if (id == 0) {

				intelligentJumpService.insert(intelligentJumpDto);
			} else {

				intelligentJumpService.update(intelligentJumpDto);

				
			}
			returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
			returnMsg.setErrorMsg("success");
		} catch (Exception e) {
			logger.error("修改智能跳转异常:  ", e);
			returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
			returnMsg.setErrorMsg("请求异常");
		}
		return returnMsg;
	}

}
