/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.service;
import cn.zhuatech.payroll.domain.DomainCatalog;
import cn.zhuatech.payroll.model.*;
import cn.zhuatech.payroll.repository.*;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
@Service
public class PayrollService {
    private final BusinessRecordRepository records; private final AuditLogRepository audits;
    private final SystemSettingRepository settings; private final DomainCatalog catalog;
    public PayrollService(BusinessRecordRepository records,AuditLogRepository audits,SystemSettingRepository settings,DomainCatalog catalog){
        this.records=records;this.audits=audits;this.settings=settings;this.catalog=catalog;}
    public Map<String,Object> about(){return Map.of("product",catalog.systemName(),"company","上海如静知华信息科技有限公司","website","https://www.zhuatech.cn/","license","仅限个人非商业学习交流");}
    public CatalogView catalog(){return new CatalogView(catalog.systemName(),catalog.scene(),catalog.initialStatus(),catalog.partyLabel(),catalog.amountLabel(),catalog.quantityLabel(),catalog.dueLabel(),catalog.modules(),new ArrayList<>(catalog.actions().values()));}
    public Dashboard dashboard(){
        List<BusinessRecord> all=records.findAllByOrderByUpdatedAtDesc(); Map<String,Long> status=new LinkedHashMap<>(),modules=new LinkedHashMap<>();
        all.forEach(item->{status.merge(item.getStatus(),1L,Long::sum);modules.merge(item.getModule(),1L,Long::sum);});
        BigDecimal amount=all.stream().map(BusinessRecord::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        return new Dashboard(all.size(),amount,status,modules,all.stream().limit(6).toList());
    }
    public List<BusinessRecord> list(String module){return module==null||module.isBlank()?records.findAllByOrderByUpdatedAtDesc():records.findByModuleOrderByUpdatedAtDesc(module);}
    @Transactional public BusinessRecord create(RecordRequest request){
        requireModule(request.module()); if(records.findByRecordNo(request.recordNo()).isPresent())throw conflict("业务编号已存在");
        BusinessRecord item=records.save(new BusinessRecord(request.recordNo(),request.module(),request.title(),request.businessParty(),request.owner(),catalog.initialStatus(),request.amount(),request.quantity(),request.dueDate(),request.riskLevel(),request.description()));
        audit(request.module(),"创建",request.recordNo(),request.title()); return item;
    }
    @Transactional public BusinessRecord update(Long id,RecordRequest request){
        BusinessRecord item=get(id); if(!item.getStatus().equals(catalog.initialStatus()))throw conflict("只有初始状态记录允许修改"); requireModule(request.module());
        item.update(request.module(),request.title(),request.businessParty(),request.owner(),request.amount(),request.quantity(),request.dueDate(),request.riskLevel(),request.description());
        audit(request.module(),"修改",item.getRecordNo(),request.title()); return item;
    }
    @Transactional public BusinessRecord action(Long id,ActionRequest request){
        BusinessRecord item=get(id); DomainCatalog.WorkflowAction rule=catalog.actions().get(request.action());
        if(rule==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"不支持的流程动作");
        if(!rule.from().contains(item.getStatus()))throw conflict("当前状态“"+item.getStatus()+"”不能执行“"+rule.label()+"”");
        item.transition(rule.to()); audit(item.getModule(),rule.label(),item.getRecordNo(),request.remark()); return item;
    }
    @Transactional public void delete(Long id){BusinessRecord item=get(id);if(!item.getStatus().equals(catalog.initialStatus()))throw conflict("只有初始状态记录允许删除");records.delete(item);audit(item.getModule(),"删除",item.getRecordNo(),item.getTitle());}
    public List<AuditLog> auditLogs(){return audits.findTop100ByOrderByOccurredAtDesc();}
    public Map<String,String> settings(){Map<String,String> result=new LinkedHashMap<>();settings.findAll().stream().sorted(Comparator.comparing(SystemSetting::getSettingKey)).forEach(s->result.put(s.getSettingKey(),s.getSettingValue()));return result;}
    @Transactional public Map<String,String> updateSettings(Map<String,String> values){values.forEach((key,value)->{if(value!=null&&!value.isBlank()){SystemSetting setting=settings.findById(key).orElseGet(()->new SystemSetting(key,value));setting.change(value);settings.save(setting);}});audit("SYSTEM","保存设置","SYSTEM",values.keySet().toString());return settings();}
    private BusinessRecord get(Long id){return records.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"业务记录不存在"));}
    private void requireModule(String module){if(catalog.modules().stream().noneMatch(item->item.code().equals(module)))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"未知业务模块");}
    private ResponseStatusException conflict(String message){return new ResponseStatusException(HttpStatus.CONFLICT,message);}
    private void audit(String module,String action,String no,String detail){var auth=SecurityContextHolder.getContext().getAuthentication();audits.save(new AuditLog(module,action,no,auth==null?"system":auth.getName(),detail==null?"":detail));}
    public record Dashboard(long totalRecords,BigDecimal totalAmount,Map<String,Long> statusCounts,Map<String,Long> moduleCounts,List<BusinessRecord> recentRecords){}
    public record CatalogView(String systemName,String scene,String initialStatus,String partyLabel,String amountLabel,String quantityLabel,String dueLabel,List<DomainCatalog.ModuleDefinition> modules,List<DomainCatalog.WorkflowAction> actions){}
    public record RecordRequest(@NotBlank @Size(max=40) String recordNo,@NotBlank String module,@NotBlank @Size(max=120) String title,@NotBlank @Size(max=100) String businessParty,@NotBlank @Size(max=50) String owner,@NotNull @PositiveOrZero BigDecimal amount,@PositiveOrZero int quantity,@NotNull LocalDate dueDate,@NotBlank @Size(max=20) String riskLevel,@Size(max=500) String description){}
    public record ActionRequest(@NotBlank String action,@Size(max=300) String remark){}
}
