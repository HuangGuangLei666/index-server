package com.pl.index;

import java.util.ArrayList;
import java.util.List;

import com.pl.indexserver.IndexServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pl.indexserver.service.TCallAgentService;
import com.pl.model.TCallAgent;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = IndexServerApplication.class)
public class TCallAgentServiceTest {
//	@Autowired
	TCallAgentService tCallAgentService;

//	@Test
	public void testBatchUpdateCallAgentUsed() {
//		List<TCallAgent> callAgentList=new ArrayList<TCallAgent>();
//		TCallAgent callAgent=new TCallAgent();
//		 callAgent.setIsUsed(1);
//         callAgent.setUsedTaskid(2039L);
//         callAgent.setId(2039l);
//         callAgentList.add(callAgent);
//
//         TCallAgent callAgent1=new TCallAgent();
//         callAgent1.setIsUsed(1);
//         callAgent1.setUsedTaskid(2038L);
//         callAgent1.setId(2038L);
//         callAgentList.add(callAgent1);
//
//		tCallAgentService.batchUpdateCallAgentUsed(callAgentList);
		System.out.println("========================success=============");
	}

}
