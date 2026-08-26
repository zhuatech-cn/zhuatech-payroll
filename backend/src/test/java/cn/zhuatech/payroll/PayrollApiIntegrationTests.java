/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.regex.Pattern;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class PayrollApiIntegrationTests {
    @Autowired MockMvc mvc;
    @Test void publicMetadataAndSecurityAreCorrect() throws Exception {
        mvc.perform(get("/api/public/about")).andExpect(status().isOk()).andExpect(jsonPath("$.data.company").value("上海如静知华信息科技有限公司"));
        mvc.perform(get("/api/dashboard")).andExpect(status().isUnauthorized());
    }
    @Test void catalogDashboardAndAllModulesAreAvailable() throws Exception {
        mvc.perform(get("/api/catalog").with(httpBasic("operator","operator123"))).andExpect(status().isOk()).andExpect(jsonPath("$.data.modules.length()").value(4));
        mvc.perform(get("/api/dashboard").with(httpBasic("operator","operator123"))).andExpect(status().isOk()).andExpect(jsonPath("$.data.totalRecords").isNumber());
        mvc.perform(get("/api/records?module=EMPLOYEE").with(httpBasic("operator","operator123"))).andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());
    }
    @Test void recordCanFollowPrimaryWorkflowAndWritesAudit() throws Exception {
        var result=mvc.perform(post("/api/records").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("""
            {"recordNo":"UT-PAY-001","module":"EMPLOYEE","title":"自动化流程验收记录","businessParty":"知华测试组织","owner":"测试专员","amount":12800,"quantity":3,"dueDate":"2026-09-30","riskLevel":"正常","description":"主要流程自动化测试"}
            """)).andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("草稿")).andReturn();
        var matcher=Pattern.compile("\\\"id\\\":(\\d+)").matcher(result.getResponse().getContentAsString()); Assertions.assertTrue(matcher.find()); long id=Long.parseLong(matcher.group(1));
        mvc.perform(post("/api/records/{id}/actions",id).with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"APPROVE\",\"remark\":\"越级操作\"}"))
            .andExpect(status().isConflict());
        mvc.perform(post("/api/records/{id}/actions",id).with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"CALCULATE\",\"remark\":\"第一步完成\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("待复核"));
        mvc.perform(post("/api/records/{id}/actions",id).with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"APPROVE\",\"remark\":\"第二步完成\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已审批"));
        mvc.perform(get("/api/admin/audit-logs").with(httpBasic("admin","admin123"))).andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").isNotEmpty());
    }
    @Test void draftRecordCanBeUpdatedAndDeleted() throws Exception {
        var result=mvc.perform(post("/api/records").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("""
            {"recordNo":"UT-PAY-002","module":"EMPLOYEE","title":"待修改记录","businessParty":"测试部门","owner":"测试员","amount":1000,"quantity":1,"dueDate":"2026-10-10","riskLevel":"正常","description":"用于验证增删改"}
            """)).andExpect(status().isOk()).andReturn();
        var matcher=Pattern.compile("\\\"id\\\":(\\d+)").matcher(result.getResponse().getContentAsString()); Assertions.assertTrue(matcher.find()); long id=Long.parseLong(matcher.group(1));
        mvc.perform(put("/api/records/{id}",id).with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("""
            {"recordNo":"UT-PAY-002","module":"EMPLOYEE","title":"已修改记录","businessParty":"测试部门","owner":"测试员","amount":1600,"quantity":2,"dueDate":"2026-10-12","riskLevel":"关注","description":"修改成功"}
            """)).andExpect(status().isOk()).andExpect(jsonPath("$.data.title").value("已修改记录"));
        mvc.perform(delete("/api/records/{id}",id).with(httpBasic("operator","operator123"))).andExpect(status().isOk());
    }
    @Test void onlyAdminCanMaintainPersistentSettings() throws Exception {
        mvc.perform(get("/api/admin/settings").with(httpBasic("operator","operator123"))).andExpect(status().isForbidden());
        mvc.perform(put("/api/admin/settings").with(httpBasic("admin","admin123")).contentType(MediaType.APPLICATION_JSON).content("{\"acceptanceMode\":\"自动化测试\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.acceptanceMode").value("自动化测试"));
    }
}
