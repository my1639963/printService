package online.fantao.tools.printservice.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import online.fantao.tools.printservice.bo.PrinterBO;
import online.fantao.tools.printservice.common.Result;
import online.fantao.tools.printservice.service.PrinterService;
import online.fantao.tools.printservice.vo.PrinterVO;

/**
 * 打印机管理控制器
 * 提供打印机相关的REST API接口
 */
@Tag(name = "打印机管理", description = "打印机相关的接口")
@RestController
@RequestMapping("/api/printer")
@Validated
@RequiredArgsConstructor
public class PrinterController {

    private final PrinterService printerService;

    /**
     * 添加打印机
     * @param printer 打印机信息
     * @return 添加结果
     */
    @Operation(summary = "添加打印机", description = "添加新的打印机到系统中")
    @PostMapping
    public Result<Boolean> addPrinter(@Validated @RequestBody PrinterBO printer) {
        return Result.success(printerService.addPrinter(printer));
    }

    /**
     * 更新打印机信息
     * @param printer 打印机信息
     * @return 更新结果
     */
    @Operation(summary = "更新打印机", description = "更新打印机信息")
    @PutMapping
    public Result<Boolean> updatePrinter(@Validated @RequestBody PrinterBO printer) {
        return Result.success(printerService.updatePrinter(printer));
    }

    /**
     * 删除打印机
     * @param id 打印机ID
     * @return 删除结果
     */
    @Operation(summary = "删除打印机", description = "根据ID删除打印机")
    @DeleteMapping("/{id}")
    public Result<Boolean> deletePrinter(
            @Parameter(description = "打印机ID", required = true)
            @PathVariable Long id) {
        return Result.success(printerService.deletePrinter(id));
    }

    /**
     * 获取打印机详情
     * @param id 打印机ID
     * @return 打印机详情
     */
    @Operation(summary = "获取打印机详情", description = "根据ID获取打印机详细信息")
    @GetMapping("/{id}")
    public Result<PrinterVO> getPrinterDetail(
            @Parameter(description = "打印机ID", required = true)
            @PathVariable Long id) {
        return Result.success(printerService.getPrinterDetail(id));
    }

    /**
     * 获取打印机列表
     * @return 打印机列表
     */
    @Operation(summary = "获取打印机列表", description = "获取所有打印机列表")
    @GetMapping("/list")
    public Result<List<PrinterVO>> getPrinterList() {
        return Result.success(printerService.getPrinterList());
    }

    /**
     * 更新打印机状态
     * @param id 打印机ID
     * @param status 新状态
     * @return 更新结果
     */
    @Operation(summary = "更新打印机状态", description = "更新打印机的在线状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updatePrinterStatus(
            @Parameter(description = "打印机ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "新状态", required = true)
            @RequestParam String status) {
        return Result.success(printerService.updatePrinterStatus(id, status));
    }

    @Operation(summary = "扫描在线设备", description = "扫描在线设备")
    @GetMapping("/scan")
    public Result<List<PrinterVO>> getSystemPrinters() {
        return Result.success(printerService.getSystemPrinters());
    }
} 