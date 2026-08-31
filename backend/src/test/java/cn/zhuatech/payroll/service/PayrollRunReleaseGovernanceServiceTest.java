/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.service;
import org.junit.jupiter.api.Test;import static org.assertj.core.api.Assertions.assertThat;
class PayrollRunReleaseGovernanceServiceTest{private final PayrollRunReleaseGovernanceService service=new PayrollRunReleaseGovernanceService();
 @Test void releasesControlledPayroll(){var r=service.assess(new PayrollRunReleaseGovernanceService.Request("2026-08",120,true,true,true,180,300,true,true,true,true));assertThat(r.decision()).isEqualTo(PayrollRunReleaseGovernanceService.Decision.RELEASE);}
 @Test void holdsUnreconciledPayroll(){var r=service.assess(new PayrollRunReleaseGovernanceService.Request("2026-09",120,false,false,false,900,300,false,false,false,false));assertThat(r.decision()).isEqualTo(PayrollRunReleaseGovernanceService.Decision.HOLD);assertThat(r.blockers()).hasSize(5);assertThat(r.actions()).hasSize(3);}}
