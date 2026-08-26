/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll;
import org.junit.jupiter.api.Test;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;import org.springframework.http.MediaType;import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc class EnterprisePayrollApiTests {
 @Autowired MockMvc mvc;
 private static final String LINE="{\"employeeNo\":\"E001\",\"employeeName\":\"张三\",\"baseSalary\":10000,\"allowance\":1000,\"bonus\":2000,\"overtimePay\":500,\"socialInsurance\":1000,\"housingFund\":800,\"specialDeduction\":1000,\"otherDeduction\":100,\"cumulativeTaxableBefore\":30000,\"taxPaidBefore\":900,\"bankAccountToken\":\"bank-token-001\"}";
 @Test void cumulativeTaxAndNetPayAreCalculated() throws Exception {mvc.perform(post("/api/enterprise/payroll/calculate-batch").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("{\"period\":\"2026-08\",\"lines\":["+LINE+"]}")).andExpect(status().isOk()).andExpect(jsonPath("$.data.employeeCount").value(1)).andExpect(jsonPath("$.data.slips[0].currentTax").isNumber()).andExpect(jsonPath("$.data.slips[0].decision").value("READY"));}
 @Test void duplicateEmployeesAreBlocked() throws Exception {mvc.perform(post("/api/enterprise/payroll/calculate-batch").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("{\"period\":\"2026-08\",\"lines\":["+LINE+","+LINE+"]}")).andExpect(status().isBadRequest());}
}
