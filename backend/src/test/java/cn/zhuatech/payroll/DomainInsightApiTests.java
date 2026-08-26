/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class DomainInsightApiTests {
    @Autowired MockMvc mvc;
    @Test void domainInsightProducesAuditableDecision() throws Exception {
        mvc.perform(post("/api/insights/payroll").with(httpBasic("operator","operator123"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"baseSalary\":10000,\"allowance\":1000,\"bonus\":2000,\"socialInsurance\":1200,\"tax\":600,\"otherDeduction\":200}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.netPay").value(11000));
    }
}
