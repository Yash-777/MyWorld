package com.github.yash777.myworld.api;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({
    "productLine",
    "newComerComissionList",
    "personalBonusList",
    "baseCommissionBonusList",
    "baseCommissionAccessoriesBonusList",
    "recruitmentBonusList",
    "otherCommisisonPersonalList",
    "groupCommissionList",
    "resumeList",
    "totalGrossList"
})
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class JsonReport implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String productLine;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Timestamp commissionDate;
    
    private List<User> newComerComissionList;
    private List<User> personalBonusList;
    private List<User> baseCommissionBonusList;
    private List<User> baseCommissionAccessoriesBonusList;
    private List<User> recruitmentBonusList;
    private List<User> otherCommisisonPersonalList;
    private List<User> groupCommissionList;
    private List<User> resumeList;
    private List<User> totalGrossList;

// Override toString to return JSON in the specified order
@Override
public String toString() {
    try {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
        return "Error generating JSON: " + e.getMessage();
    }
}
    
    // Static nested class
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    // Applies filtering to nested User objects
    //@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)
    public static class User {
        public int id;
        public String name;
        public String name2;
    }

    public static void main(String[] args) throws JsonProcessingException {
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

        System.out.println("ToString Mapper:" + report);
    }
}
