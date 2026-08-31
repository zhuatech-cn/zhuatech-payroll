/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.payroll.controller;
import cn.zhuatech.payroll.common.ApiResponse;import cn.zhuatech.payroll.service.PayrollRunReleaseGovernanceService;
import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/enterprise/payroll")
public class PayrollRunReleaseGovernanceController{private final PayrollRunReleaseGovernanceService service;public PayrollRunReleaseGovernanceController(PayrollRunReleaseGovernanceService service){this.service=service;}@PostMapping("/run-release")public ApiResponse<PayrollRunReleaseGovernanceService.Assessment> assess(@Valid @RequestBody PayrollRunReleaseGovernanceService.Request request){return ApiResponse.ok(service.assess(request));}}
