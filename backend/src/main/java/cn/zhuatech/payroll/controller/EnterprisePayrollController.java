/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.controller;
import cn.zhuatech.payroll.common.ApiResponse;
import cn.zhuatech.payroll.service.EnterprisePayrollService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/enterprise/payroll")
public class EnterprisePayrollController {
    private final EnterprisePayrollService service;public EnterprisePayrollController(EnterprisePayrollService service){this.service=service;}
    @PostMapping("/calculate-batch") ApiResponse<EnterprisePayrollService.BatchResult> calculate(@Valid @RequestBody EnterprisePayrollService.BatchRequest request){return ApiResponse.ok(service.calculate(request));}
}
