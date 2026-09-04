/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class OffCyclePayrollAuthorizationService {
    public Assessment assess(Request request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (!request.employeesActive()) blockers.add("批次包含无有效劳动关系的人员");
        if (!request.deductionsValidated()) blockers.add("扣减项目未完成合法性校验");
        if (!request.taxCalculated()) blockers.add("个税与属地规则未完成计算");
        if (!request.bankTokensValid()) blockers.add("工资卡令牌无效或缺失");
        if (!request.duplicateCheckClear()) blockers.add("检测到重复补发风险");
        if (!request.fundingApproved()) blockers.add("批次资金尚未落实");
        if (!request.payrollApproved()) blockers.add("薪酬负责人尚未批准");
        if (!request.makerCheckerSeparated()) blockers.add("制单人与复核人未职责分离");
        if (!request.auditReady()) blockers.add("批次审计资料不完整");
        if (!request.earningsEvidenceComplete()) actions.add("补充补发、奖金或更正依据");
        if (!request.reasonCodesAssigned()) actions.add("为员工明细补充标准原因码");
        if (!request.employeeNoticeReady()) actions.add("生成员工工资差异通知");
        Decision decision = !blockers.isEmpty() ? Decision.BLOCKED : !actions.isEmpty() ? Decision.REVIEW : Decision.RELEASE;
        return new Assessment(request.batchId(), decision, List.copyOf(blockers), List.copyOf(actions));
    }

    public record Request(@NotBlank String batchId, boolean employeesActive,
                          boolean earningsEvidenceComplete, boolean deductionsValidated,
                          boolean taxCalculated, boolean bankTokensValid, boolean duplicateCheckClear,
                          boolean fundingApproved, boolean payrollApproved, boolean reasonCodesAssigned,
                          boolean employeeNoticeReady, boolean makerCheckerSeparated, boolean auditReady) {}
    public record Assessment(String batchId, Decision decision, List<String> blockers, List<String> actions) {}
    public enum Decision { RELEASE, REVIEW, BLOCKED }
}
