/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.service;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.*;
import java.util.*;
@Service
public class EnterprisePayrollService {
    public BatchResult calculate(@Valid BatchRequest request){
        Set<String> employees=new HashSet<>();List<PaySlip> slips=new ArrayList<>();BigDecimal batchNet=BigDecimal.ZERO;
        for(var line:request.lines()){
            if(!employees.add(line.employeeNo()))throw bad("员工编号重复: "+line.employeeNo());
            BigDecimal gross=money(line.baseSalary().add(line.allowance()).add(line.bonus()).add(line.overtimePay()));
            BigDecimal taxableCurrent=gross.subtract(line.socialInsurance()).subtract(line.housingFund())
                .subtract(line.specialDeduction()).max(BigDecimal.ZERO);
            BigDecimal cumulativeTaxable=line.cumulativeTaxableBefore().add(taxableCurrent);
            TaxBracket bracket=bracket(cumulativeTaxable);
            BigDecimal cumulativeTax=money(cumulativeTaxable.multiply(bracket.rate()).subtract(bracket.quickDeduction())).max(BigDecimal.ZERO);
            BigDecimal currentTax=money(cumulativeTax.subtract(line.taxPaidBefore())).max(BigDecimal.ZERO);
            BigDecimal deductions=line.socialInsurance().add(line.housingFund()).add(line.otherDeduction()).add(currentTax);
            BigDecimal net=money(gross.subtract(deductions));
            List<String> warnings=new ArrayList<>();
            if(line.bankAccountToken().isBlank())warnings.add("缺少银行账户令牌");
            if(net.signum()<0)warnings.add("实发工资为负");
            if(gross.signum()==0)warnings.add("应发工资为零");
            slips.add(new PaySlip(line.employeeNo(),line.employeeName(),gross,taxableCurrent,currentTax,money(deductions),net,
                bracket.rate(),warnings,warnings.isEmpty()?"READY":"REVIEW"));
            batchNet=batchNet.add(net.max(BigDecimal.ZERO));
        }
        return new BatchResult(request.period(),slips.size(),money(batchNet),slips,
            slips.stream().allMatch(s->"READY".equals(s.decision()))?"READY_FOR_APPROVAL":"REVIEW_REQUIRED");
    }
    private TaxBracket bracket(BigDecimal value){
        if(value.compareTo(new BigDecimal("36000"))<=0)return new TaxBracket(new BigDecimal(".03"),BigDecimal.ZERO);
        if(value.compareTo(new BigDecimal("144000"))<=0)return new TaxBracket(new BigDecimal(".10"),new BigDecimal("2520"));
        if(value.compareTo(new BigDecimal("300000"))<=0)return new TaxBracket(new BigDecimal(".20"),new BigDecimal("16920"));
        if(value.compareTo(new BigDecimal("420000"))<=0)return new TaxBracket(new BigDecimal(".25"),new BigDecimal("31920"));
        if(value.compareTo(new BigDecimal("660000"))<=0)return new TaxBracket(new BigDecimal(".30"),new BigDecimal("52920"));
        if(value.compareTo(new BigDecimal("960000"))<=0)return new TaxBracket(new BigDecimal(".35"),new BigDecimal("85920"));
        return new TaxBracket(new BigDecimal(".45"),new BigDecimal("181920"));
    }
    private BigDecimal money(BigDecimal value){return value.setScale(2,RoundingMode.HALF_UP);}
    private ResponseStatusException bad(String msg){return new ResponseStatusException(HttpStatus.BAD_REQUEST,msg);}
    private record TaxBracket(BigDecimal rate,BigDecimal quickDeduction){}
    public record BatchRequest(@NotBlank @Pattern(regexp="\\d{4}-(0[1-9]|1[0-2])") String period,
        @NotEmpty List<@Valid PayLine> lines){}
    public record PayLine(@NotBlank String employeeNo,@NotBlank String employeeName,@NotNull @DecimalMin("0") BigDecimal baseSalary,
        @NotNull @DecimalMin("0") BigDecimal allowance,@NotNull @DecimalMin("0") BigDecimal bonus,
        @NotNull @DecimalMin("0") BigDecimal overtimePay,@NotNull @DecimalMin("0") BigDecimal socialInsurance,
        @NotNull @DecimalMin("0") BigDecimal housingFund,@NotNull @DecimalMin("0") BigDecimal specialDeduction,
        @NotNull @DecimalMin("0") BigDecimal otherDeduction,@NotNull @DecimalMin("0") BigDecimal cumulativeTaxableBefore,
        @NotNull @DecimalMin("0") BigDecimal taxPaidBefore,@NotNull String bankAccountToken){}
    public record PaySlip(String employeeNo,String employeeName,BigDecimal grossPay,BigDecimal taxableCurrent,
        BigDecimal currentTax,BigDecimal deductions,BigDecimal netPay,BigDecimal taxRate,List<String> warnings,String decision){}
    public record BatchResult(String period,int employeeCount,BigDecimal batchNetPay,List<PaySlip> slips,String decision){}
}
