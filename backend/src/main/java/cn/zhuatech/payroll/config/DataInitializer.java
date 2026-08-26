/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.config;
import cn.zhuatech.payroll.model.*;
import cn.zhuatech.payroll.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Configuration public class DataInitializer {
    @Bean CommandLineRunner seed(BusinessRecordRepository records,SystemSettingRepository settings){return args->{
        if(records.count()>0)return;
            settings.save(new SystemSetting("payDay","每月10日"));
    settings.save(new SystemSetting("currency","CNY"));
    settings.save(new SystemSetting("taxMode","累计预扣法"));
    settings.save(new SystemSetting("payslipChannel","员工工作台"));
            records.save(new BusinessRecord("PAY-20260826-001","EMPLOYEE","研发中心八月薪资组","研发中心","薪酬专员","已审批",new BigDecimal("486000"),28,LocalDate.now().plusDays(5),"正常","计薪人员及银行账户已复核"));
    records.save(new BusinessRecord("PAY-20260826-002","PAYROLL","2026年8月月度工资批次","全体员工","薪酬主管","待复核",new BigDecimal("1268000"),86,LocalDate.now().plusDays(5),"关注","存在3条考勤补录待确认"));
    records.save(new BusinessRecord("PAY-20260826-003","TAX","八月个税与专项扣除申报","上海主体","税务专员","草稿",new BigDecimal("138000"),86,LocalDate.now().plusDays(8),"正常","等待员工专项附加扣除确认"));
    records.save(new BusinessRecord("PAY-20260826-004","PAYMENT","八月工资发放与工资条","上海总部","财务出纳","草稿",new BigDecimal("1130000"),86,LocalDate.now().plusDays(10),"正常","审批后生成银行代发文件"));
    };}
}
