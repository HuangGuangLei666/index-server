package com.pl.indexserver.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.pl.autodialer.api.AutodialerService;
import com.pl.autodialer.dto.AutodialerDTO;
import com.pl.autodialer.dto.AutodialerTimeRangeDTO;
import com.pl.base.SingleResult;
import com.pl.indexserver.model.CallTaskIndexDto;
import com.pl.indexserver.model.CallTaskMsg;
import com.pl.indexserver.service.CallTaskService;
import com.pl.indexserver.service.TCallAgentService;
import com.pl.indexserver.service.TDialogService;
import com.pl.indexserver.untils.BusinessPropertyType;
import com.pl.indexserver.untils.CallTaskCate;
import com.pl.indexserver.untils.StampToDates;
import com.pl.mapper.CallTaskMapper;
import com.pl.mapper.TBusinessPropertyMapper;
import com.pl.mapper.TCallAgentMapper;
import com.pl.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CallTaskServiceImpl implements CallTaskService {
	private static final Logger logger = LoggerFactory.getLogger(CallTaskServiceImpl.class);

	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static final SimpleDateFormat fmt = new SimpleDateFormat(
			"yyyy-MM-dd");

	@Autowired
	private CallTaskMapper callTaskMapper;

	@Autowired
	private TDialogService tDialogService;
	@Autowired
	private TCallAgentService tCallAgentService;

	@Autowired
	private TCallAgentMapper tCallAgentMapper;

	@Autowired
	private TBusinessPropertyMapper tBusinessPropertyMapper;

	private boolean autodialerEnableStats = true;

	@Reference(version = "${autodialer.service.version}", application = "${dubbo.application.id}", timeout = 300000)
	private AutodialerService autodialerService;

	@Override
	public Page<CallTaskMsg> getIndex(String uid, Long company_id,
			Integer taskState, String taskName, int pageIndex, int pageSize) {
		List<CallTaskMsg> callTaskMsgs = new ArrayList<>();
		String now = fmt.format(new Date());
		Date nowTime = new Date();
		// 创建一个数值格式化对象
		NumberFormat numberFormat = NumberFormat.getInstance();
		// 设置精确到小数点后2位
		numberFormat.setMaximumFractionDigits(2);
		// 状态 task_state
		// 任务名称 taskid 模糊查询
		Page<CallTask> pages = new Page<>(pageIndex, pageSize);
		Page<CallTaskMsg> page = new Page<>(pageIndex, pageSize);
		List<CallTask> callTaskList = callTaskMapper.getCallTaskListByMap(
				pages, uid, company_id, taskState, taskName);
		if (CollectionUtils.isEmpty(callTaskList)) {
			page.setTotal(0);
			page.setCurrent(pages.getCurrent());
			page.setSize(0);
			return page;
		}
		List<TDialog> dialogList;
		CallTaskMsg callTaskMsg;
		for (CallTask callTask : callTaskList) {
			String en_time = "";

			int totalNumber = tDialogService.countDialogByTaskId(
					callTask.getCompanyId(), callTask.getId());
			// 今日完成
			int finishToday = tDialogService.countFinishTodayDialogByTaskId(
					callTask.getCompanyId(), callTask.getId());
			// 已拨打的数量
			int finishAll = tDialogService.countFinishAllDialogByTaskId(
					callTask.getCompanyId(), callTask.getId());

			// 还需拨打
			int still = totalNumber - finishAll;

			logger.info(callTask.getId() + "==totalNumber=" + totalNumber + "==finishToday=" + finishToday + "==finishAll="
					+ finishAll + "==still=" + still);
			// 一天可以拨打的数量；坐席数*分钟数=一天预计拨打量
			int finishADay;
			String msg = "";
			TDialogCount tDialogCount = tDialogService.selectAllIsIntentionByCompanyIdAndTaskId(company_id, callTask.getId(), "all");
			// 意向客户数
			int intentional = tDialogCount.getCount(); // 意向客户数
			// 未开始或运行中的任务才显示预计时间

			// 任务截止
			try {
				en_time = simpleDateFormat.format(callTask.getEndDate());
			} catch (Exception e) {
				en_time = "";
			}
			// 拨打百分比
			String stillConnect = "0";

			if (StringUtils.isEmpty(callTask.getTimeQuantum())) {

			} else {
				// 当前任务的坐席数
				int agent_num = tCallAgentMapper
						.countByTaskId(callTask.getId());
				// 统计一天可拨打的时间数
				int SurplusOneDay = reckonNumber(callTask);
				finishADay = agent_num * SurplusOneDay;
				int days = (int) (callTask.getEndDate().getTime() - callTask.getBeginDate().getTime()) / (1000 * 60 * 60 * 24) + 1;
				// 预估差额
				int Surplus = totalNumber - (finishADay * days);
				if (Surplus <= 0) {
					if (-Surplus <= finishADay) {
						msg = "预计可准时完成";
					} else {
						msg = "预计提前" + ((-Surplus) / finishADay) + "天";
					}
				} else if (finishADay == 0) {
					msg = "已释放所有坐席";
				} else {
					msg = "预计多需" + (int) Math.ceil(((double) Surplus) / finishADay) + "天";
				}

				// 拨打百分比
				stillConnect = numberFormat.format((float) finishAll / (float) totalNumber * 100);

			}
			callTaskMsg = new CallTaskMsg(callTask.getId(),
					callTask.getStatus(), callTask.getTaskName(), en_time,
					totalNumber, finishToday, intentional, still, stillConnect
							+ "%");
			callTaskMsg.setExpectMsg(msg);

			callTaskMsgs.add(callTaskMsg);
		}
		page.setTotal(pages.getTotal());
		page.setCurrent(pages.getCurrent());
		page.setSize(pages.getSize());
		page.setRecords(callTaskMsgs);
		return page;
	}

	/**
	 * 计算当前任务的一天可用时间
	 *
	 * @param callTask
	 * @return
	 */
	private int reckonNumber(CallTask callTask) {
		Calendar calendar = Calendar.getInstance();
		int count = 0;
		calendar.set(2018, 6, 1, 0, 0, 0);
		Date beginDate = calendar.getTime();
		calendar.set(2018, 6, 1, 23, 59, 59);
		Date endDate = calendar.getTime();
		List<String> times = StampToDates.getDatesBetweenTwoDate(beginDate,
				endDate, callTask.getTimeQuantum());
		for (String time : times) {
			String[] timesolt = time.split("\\|");
			String beginTime = timesolt[0];
			String endTime = timesolt[1];
			count += StampToDates.stampToDateReduce(beginTime, endTime) / 60;
		}
		return count;
	}

	@Override
	public int deleteByPrimaryKey(Long id) {
		return callTaskMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(CallTask record) {
		return callTaskMapper.insert(record);
	}

	@Override
	public int insertSelective(CallTask record) {
		return callTaskMapper.insertSelective(record);
	}

	@Override
	public CallTask selectByPrimaryKey(Long id) {
		return callTaskMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(CallTask record) {
		return callTaskMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateCallTaskStatus(CallTask record,
			List<TCallAgent> callAgentList) {
		// 判断是否开启全局自动外呼
		if (autodialerEnableStats) {
			String gateWay = callAgentList.get(0).getExtraInfo();
			if (StringUtils.isEmpty(gateWay)) {
				gateWay = record.getCompanyId() + "";
			}
			initAutodialer(record, callAgentList.size(), gateWay);
			record.setTaskCate(CallTaskCate.DINGDING.getCode());
		} else {
			// 判断智库自动外呼条件配置
			TBusinessProperty tBusinessProperty = tBusinessPropertyMapper
					.selectByBusinessIdAndType(record.getBusinessId(),
							BusinessPropertyType.BUSINESS_AUTODIALER.getCode());
			if (null != tBusinessProperty
					&& "autodialer".equalsIgnoreCase(tBusinessProperty
							.getPropertyValue())) {
				logger.info("TBusinessProperty  BusinessId={}, Values={}",
						record.getBusinessId(), tBusinessProperty);
				String gateWay = callAgentList.get(0).getExtraInfo();
				if (StringUtils.isEmpty(gateWay)) {
					gateWay = record.getCompanyId() + "";
				}
				initAutodialer(record, callAgentList.size(), gateWay);
				record.setTaskCate(CallTaskCate.DINGDING.getCode());
			} else {
				record.setTaskCate(CallTaskCate.NORMAL.getCode());
			}
		}
		// 锁定座席
		for (TCallAgent callAgent : callAgentList) {
			tCallAgentService.updateCallAgentUsed(callAgent);
		}
		return callTaskMapper.updateByPrimaryKeySelective(record);
	}

	/**
	 * 自动外呼
	 *
	 * @param callTask
	 *            外呼任务
	 * @param agentNum
	 *            坐席数
	 * @param gateWay
	 *            网关名称（一个任务只有一个网关）
	 */
	private void initAutodialer(CallTask callTask, Integer agentNum,
			String gateWay) {
		try {
			AutodialerDTO autodialer = new AutodialerDTO();
			autodialer.setTaskId(callTask.getId() + "");
			autodialer.setTaskName(callTask.getTaskName());
			autodialer.setCompanyId(callTask.getCompanyId() + "");
			autodialer.setAgentNum(agentNum);
			autodialer.setSipGateWay(Collections.singletonList(gateWay));
			List<AutodialerTimeRangeDTO> timeRangeList = new ArrayList<>();
			setCallTaskNotWorkTime(timeRangeList);
			autodialer.setTimeRangeList(timeRangeList);
			SingleResult result = autodialerService.initAutodialer(autodialer);
			if (result.isSuccess()) {
				logger.info("=====autodialer init is success! taskId={}",
						autodialer.getTaskId());
			} else {
				logger.error("=====autodialer init is fail! taskId={},msg={}",
						autodialer.getTaskId(), result.getErrMsg());
			}
		} catch (Exception e) {
			logger.error("initAutodialer is error,taskId=" + callTask.getId()
					+ ",error msg", e);
		}
	}

	// 设置顶顶禁止呼出时间段。用户设置的外呼时段会在任务调度里面生效，这里不做任何处理。
	private void setCallTaskNotWorkTime(
			List<AutodialerTimeRangeDTO> timeRangeList) {
		// 默认晚上8点到早上8点禁呼
		AutodialerTimeRangeDTO timeRangeDTO = new AutodialerTimeRangeDTO();
		timeRangeDTO.setBeginTime("20:00:00");
		timeRangeDTO.setEndTime("08:00:00");
		timeRangeList.add(timeRangeDTO);
		// 默认晚上12点到早上14点禁呼
		AutodialerTimeRangeDTO timeRangeDTO1 = new AutodialerTimeRangeDTO();
		timeRangeDTO1.setBeginTime("12:00:00");
		timeRangeDTO1.setEndTime("14:00:00");
		timeRangeList.add(timeRangeDTO1);
	}

	@Override
	public int updateOrInsertByPrimaryKeySelective(CallTask record) {
		return callTaskMapper.updateOrInsertByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKeyWithBLOBs(CallTask record) {
		return callTaskMapper.updateByPrimaryKeyWithBLOBs(record);
	}

	@Override
	public int updateByPrimaryKey(CallTask record) {
		return callTaskMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<CallTask> getCallTaskListByCompany_id(Long company_id,
			String taskName) {
		return callTaskMapper.getCallTaskListByCompany_id(company_id, taskName);
	}

	@Override
	public List<CallTask> getCallTaskListByCompany_id2(Long company_id,
			String taskName, Long task_id) {
		return callTaskMapper.getCallTaskListByCompany_idOutTaskid(company_id,
				taskName, task_id);
	}

	@Override
	public List<CallTask> getCallTaskListByMap(String uid, Long company_id,
			Integer taskState, String taskid) {
		return callTaskMapper.getCallTaskListByMap(null, uid, company_id,
				taskState, taskid);
	}

	@Override
	public CallTask getCallTaskByid(Long id, Long company_id) {
		return callTaskMapper.getCallTaskByid(id, company_id);
	}

	@Override
	public int updateTask_state(Integer pause, Long id, Date modify_date,
			Date begindate, Date enddate, String timeQuantum,
			Integer totalNumber, String additionalInfo) {
		return callTaskMapper.updateTask_state(pause, id, modify_date,
				begindate, enddate, timeQuantum, totalNumber, additionalInfo);
	}

	@Override
	public int updateEnd(Long id, Date modify_date) {
		return callTaskMapper.updateEnd(id, modify_date);
	}

	@Override
	public int updateAgentStatus(Long id) {
		return callTaskMapper.updateAgentStatus(id);
	}

	@Override
	public int selectNotDialing(Long company_id, Long taskid) {
		return callTaskMapper.selectNotDialing(company_id, taskid);
	}

	@Override
	public String getCar_Numbers(Long task_id, String ct_phone) {
		return callTaskMapper.getCar_Numbers(task_id, ct_phone);
	}

	@Override
	public Map<String, Integer> getSum_CallTaskAndCalls(Long companyId,
			String uid, Date endTime) {

		return callTaskMapper.getIndexCallData(companyId, uid, endTime);
	}

	@Override
	public List<CallTaskIndexDto> getCallTaskCount(Long companyId,
			Date starTime, Date endTime) {

		return callTaskMapper.getCallTaskSum(companyId, starTime, endTime);
	}

	@Override
	public List<CallTask> getCallTaskBystatus(String userId, int status) {
		return callTaskMapper.getCallTaskBystatus(userId, status);
	}

	@Override
	public boolean stopCallTask(Long taskId) throws Exception {
		CallTask callTask = new CallTask();
		callTask.setId(taskId);
		callTask.setStatus(2);
		callTask.setTaskState("暂停");
		callTask.setModifyDate(new Date());
		int i = updateByPrimaryKeySelective(callTask);
		return i > 0;
	}

	@Override
	public List<CallTask> getCallTaskByCompanyId(Long companyId) {
		return callTaskMapper.getCallTaskByCompanyId(companyId);
	}

	// （顶顶禁用外呼时段+用户设置呼叫时段组合运算出禁呼时段 弃用）
	/*
	 * private void setCallBarringTime(String timeQuantum,
	 * List<AutodialerTimeRangeDTO> timeRangeList) { if (null == timeRangeList)
	 * { timeRangeList = new ArrayList<>(); } if
	 * (!StringUtils.isEmpty(timeQuantum)) { try { String temp; String[] split =
	 * timeQuantum.split("\\|"); for (int i = 0; i < split.length; i++) { for
	 * (int j = 0; j < split.length - i - 1; j++) { Date date1 =
	 * DateUtils.getDate(split[j].split("-")[0], DateUtils.DATEFORMAT_7); Date
	 * date2 = DateUtils.getDate(split[j + 1].split("-")[0],
	 * DateUtils.DATEFORMAT_7); int a = date1.compareTo(date2); if (a > 0) {
	 * temp = split[j]; split[j] = split[j + 1]; split[j + 1] = temp; } } } for
	 * (int i = 0; i < split.length - 1; i++) { String[] split1 =
	 * split[i].split("-"); String[] split2 = split[i + 1].split("-"); Date
	 * date1 = DateUtils.getDate(split1[split1.length - 1],
	 * DateUtils.DATEFORMAT_7); Date date2 = DateUtils.getDate(split2[0],
	 * DateUtils.DATEFORMAT_7); if (i == 0) { Date defaultDate1 =
	 * DateUtils.getDate("08:00", DateUtils.DATEFORMAT_7); Date date1_1 =
	 * DateUtils.getDate(split1[0], DateUtils.DATEFORMAT_7); if
	 * (defaultDate1.compareTo(date1_1) < 0) { AutodialerTimeRangeDTO
	 * timeRangeDTO = new AutodialerTimeRangeDTO();
	 * timeRangeDTO.setBeginTime(DateUtils.getStringForDate(defaultDate1,
	 * DateUtils.DATEFORMAT_4));
	 * timeRangeDTO.setEndTime(DateUtils.getStringForDate(date1_1,
	 * DateUtils.DATEFORMAT_4)); timeRangeList.add(timeRangeDTO); } } if
	 * (date1.compareTo(date2) < 0) { AutodialerTimeRangeDTO timeRangeDTO = new
	 * AutodialerTimeRangeDTO();
	 * timeRangeDTO.setBeginTime(DateUtils.getStringForDate(date1,
	 * DateUtils.DATEFORMAT_4));
	 * timeRangeDTO.setEndTime(DateUtils.getStringForDate(date2,
	 * DateUtils.DATEFORMAT_4)); timeRangeList.add(timeRangeDTO); } else { Date
	 * date3 = DateUtils.getDate(split2[split2.length - 1],
	 * DateUtils.DATEFORMAT_7); int b = date1.compareTo(date3); if (b > 0) {
	 * split[i + 1] = split2[0] + "-" + split1[split1.length - 1]; } } if (i ==
	 * split.length - 2) { Date defaultDate2 = DateUtils.getDate("20:00",
	 * DateUtils.DATEFORMAT_7); Date date2_2 =
	 * DateUtils.getDate(split2[split2.length - 1], DateUtils.DATEFORMAT_7); if
	 * (date2_2.compareTo(defaultDate2) < 0) { AutodialerTimeRangeDTO
	 * timeRangeDTO = new AutodialerTimeRangeDTO();
	 * timeRangeDTO.setBeginTime(DateUtils.getStringForDate(date2_2,
	 * DateUtils.DATEFORMAT_4));
	 * timeRangeDTO.setEndTime(DateUtils.getStringForDate(defaultDate2,
	 * DateUtils.DATEFORMAT_4)); timeRangeList.add(timeRangeDTO); } } } } catch
	 * (Exception e) { timeRangeList.clear(); logger.error("自动设置禁呼时间段出现异常:  ",
	 * e); } } //默认晚上8点到早上8点禁呼 AutodialerTimeRangeDTO timeRangeDTO = new
	 * AutodialerTimeRangeDTO(); timeRangeDTO.setBeginTime("20:00:00");
	 * timeRangeDTO.setEndTime("08:00:00"); timeRangeList.add(timeRangeDTO);
	 * //默认晚上12点到早上14点禁呼 AutodialerTimeRangeDTO timeRangeDTO1 = new
	 * AutodialerTimeRangeDTO(); timeRangeDTO1.setBeginTime("12:00:00");
	 * timeRangeDTO1.setEndTime("14:00:00"); timeRangeList.add(timeRangeDTO1); }
	 */
}
