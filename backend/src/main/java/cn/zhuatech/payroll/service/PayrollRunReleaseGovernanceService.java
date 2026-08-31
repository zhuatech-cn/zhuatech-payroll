/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.service;
import jakarta.validation.constraints.*; import org.springframework.stereotype.Service;
import java.util.ArrayList; import java.util.List;
@Service
public class PayrollRunReleaseGovernanceService{
 public Assessment assess(Request r){List<String>b=new ArrayList<>();List<String>a=new ArrayList<>();
  if(!r.employeeCoverageComplete())b.add("在册员工与薪资明细未完全对账"); if(!r.bankTokensValid())b.add("存在无效或缺失的银行账户令牌");
  if(!r.taxAndSocialInsuranceCalculated())b.add("个税或社保计算未完成"); if(r.varianceBps()>r.allowedVarianceBps())b.add("本期薪资总额波动超过授权阈值");
  if(!r.makerCheckerSeparated())b.add("核算与复核未实现职责分离"); if(!r.hrApproved())a.add("取得人力资源负责人批准");
  if(!r.financeApproved())a.add("取得财务负责人批准"); if(!r.paymentFileSigned())a.add("签名并校验银行发放文件");
  Decision d=!b.isEmpty()?Decision.HOLD:!a.isEmpty()?Decision.APPROVAL_REQUIRED:Decision.RELEASE;
  return new Assessment(r.payrollPeriod(),r.employeeCount(),d,List.copyOf(b),List.copyOf(a));}
 public record Request(@NotBlank String payrollPeriod,@Min(1)int employeeCount,boolean employeeCoverageComplete,
                       boolean bankTokensValid,boolean taxAndSocialInsuranceCalculated,@Min(0)int varianceBps,
                       @Min(0)int allowedVarianceBps,boolean makerCheckerSeparated,boolean hrApproved,
                       boolean financeApproved,boolean paymentFileSigned){}
 public record Assessment(String payrollPeriod,int employeeCount,Decision decision,List<String> blockers,List<String> actions){}
 public enum Decision{RELEASE,APPROVAL_REQUIRED,HOLD}
}
