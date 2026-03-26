package com.github.yash777.myworld.api.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.github.yash777.myworld.api.JsonReport;
import com.github.yash777.myworld.api.http.config.ApplicationServiceUtil;


@org.springframework.web.bind.annotation.RestController //spring-boot-starter-web
@org.springframework.web.bind.annotation.RequestMapping(value = "/Jackson")
public class JsonReportController {
	
	private ObjectMapper mapper = new ObjectMapper();
	public JsonReportController() {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
	}
	
	@Autowired
	private ApplicationServiceUtil appUtil;
	
	//Specifically, @GetMapping is a composed annotation thatacts as a shortcut for @RequestMapping(method = RequestMethod.GET).
	@GetMapping("/report")
    public JsonReport getJsonReport() {
        JsonReport report = new JsonReport();
        report.setProductLine("Electronics");

        JsonReport.User user1 = new JsonReport.User(1, "Alice", "Brown");
        JsonReport.User user2 = new JsonReport.User(2, "Bob", "Smith");
        JsonReport.User user3 = new JsonReport.User(3, null, "");

        report.setNewComerComissionList(Arrays.asList(user1, user2, user3));
        report.setPersonalBonusList(Arrays.asList(user1, user2));
        report.setBaseCommissionBonusList(Arrays.asList(user1));
        report.setBaseCommissionAccessoriesBonusList(Arrays.asList(user2));
        report.setRecruitmentBonusList(Arrays.asList(user3));
        report.setOtherCommisisonPersonalList(Arrays.asList(user1, user3));
        report.setGroupCommissionList(Arrays.asList(user2));
        report.setResumeList(Arrays.asList(user3));
        report.setTotalGrossList(Arrays.asList(user1, user2));

        return report;
    }
	
	@GetMapping("/report/responseentity")
    public ResponseEntity<? extends Object> getJsonReportResponseEntity() {
		
		appUtil.configureObjectMapper(mapper, null);
		JsonReport report = this.getJsonReport();
		
		//transaction = mapper.readValue(transactionRequest, Transaction.class);
		return new ResponseEntity<>(report, HttpStatus.OK);
	}
}
