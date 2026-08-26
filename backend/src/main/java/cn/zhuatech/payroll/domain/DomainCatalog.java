/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.domain;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class DomainCatalog {
    private final Map<String,WorkflowAction> actions=new LinkedHashMap<>();
    public DomainCatalog(){
        actions.put("CALCULATE", new WorkflowAction("CALCULATE", "完成核算", List.of("草稿"), "待复核"));
actions.put("APPROVE", new WorkflowAction("APPROVE", "审批通过", List.of("待复核"), "已审批"));
actions.put("PAY", new WorkflowAction("PAY", "确认发放", List.of("已审批"), "已发放"));
    }
    public String systemName(){return "知华科技薪酬管理系统";}
    public String scene(){return "员工薪资、津贴扣款、个税社保、工资条与发放批次管理";}
    public String initialStatus(){return "草稿";}
    public String partyLabel(){return "员工/组织";} public String amountLabel(){return "应发金额";}
    public String quantityLabel(){return "人数";} public String dueLabel(){return "发薪日";}
    public List<ModuleDefinition> modules(){return List.of(
        new ModuleDefinition("EMPLOYEE","员工薪资档案","维护薪资组、计薪状态与发放账户"),
    new ModuleDefinition("PAYROLL","薪资核算","生成月度工资批次并完成应发实发计算"),
    new ModuleDefinition("TAX","个税与社保","维护专项扣除、个税和社保公积金结果"),
    new ModuleDefinition("PAYMENT","发放与工资条","审批发放批次并向员工发布工资条")
    );}
    public Map<String,WorkflowAction> actions(){return Collections.unmodifiableMap(actions);}
    public record ModuleDefinition(String code,String name,String description){}
    public record WorkflowAction(String code,String label,List<String> from,String to){}
}
