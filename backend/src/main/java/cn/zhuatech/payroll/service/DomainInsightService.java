/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.service;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.*;
import java.util.*;
@Service
public class DomainInsightService {
    public Map<String,Object> analyze(InsightRequest req){
        Map<String,Object> result=new LinkedHashMap<>();
        BigDecimal gross=req.baseSalary().add(req.allowance()).add(req.bonus());
BigDecimal deductions=req.socialInsurance().add(req.tax()).add(req.otherDeduction());
BigDecimal net=gross.subtract(deductions).setScale(2,RoundingMode.HALF_UP);
result.put("grossPay",gross);result.put("deductions",deductions);result.put("netPay",net);result.put("decision",net.signum()>=0?"READY":"REVIEW");
        return result;
    }
    private BigDecimal rate(long numerator,long denominator){return denominator==0?BigDecimal.ZERO:BigDecimal.valueOf(numerator).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(denominator),2,RoundingMode.HALF_UP);}
    public record InsightRequest(@DecimalMin("0.0") BigDecimal baseSalary, @DecimalMin("0.0") BigDecimal allowance, @DecimalMin("0.0") BigDecimal bonus, @DecimalMin("0.0") BigDecimal socialInsurance, @DecimalMin("0.0") BigDecimal tax, @DecimalMin("0.0") BigDecimal otherDeduction){}
}
