/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class OffCyclePayrollAuthorizationServiceTest {
    private final OffCyclePayrollAuthorizationService service = new OffCyclePayrollAuthorizationService();

    @Test void releasesControlledOffCycleBatch() {
        var result = service.assess(request(true, true, true));
        assertThat(result.decision()).isEqualTo(OffCyclePayrollAuthorizationService.Decision.RELEASE);
        assertThat(result.blockers()).isEmpty();
    }

    @Test void reviewsBatchWithOutstandingActions() {
        var result = service.assess(request(false, false, false));
        assertThat(result.decision()).isEqualTo(OffCyclePayrollAuthorizationService.Decision.REVIEW);
        assertThat(result.actions()).hasSize(3);
    }

    @Test void blocksUnsafeOffCycleBatch() {
        var result = service.assess(new OffCyclePayrollAuthorizationService.Request("OFF-003", false, true,
                false, false, false, false, false, false, true, true, false, false));
        assertThat(result.decision()).isEqualTo(OffCyclePayrollAuthorizationService.Decision.BLOCKED);
        assertThat(result.blockers()).hasSize(9);
    }

    private OffCyclePayrollAuthorizationService.Request request(boolean evidence, boolean reason, boolean notice) {
        return new OffCyclePayrollAuthorizationService.Request("OFF-001", true, evidence, true, true, true,
                true, true, true, reason, notice, true, true);
    }
}
