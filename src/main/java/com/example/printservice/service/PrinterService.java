package com.example.printservice.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.printservice.bo.PrinterBO;
import com.example.printservice.entity.Printer;
import com.example.printservice.vo.PrinterVO;

/**
 * 打印机服务接口
 * 提供打印机相关的业务操作
 */
public interface PrinterService extends IService<Printer> {
    
    /**
     * 添加打印机
     * @param printer 打印机信息
     * @return 添加结果
     */
    boolean addPrinter(PrinterBO printer);
    
    /**
     * 更新打印机信息
     * @param printer 打印机信息
     * @return 更新结果
     */
    boolean updatePrinter(PrinterBO printer);
    
    /**
     * 删除打印机
     * @param id 打印机ID
     * @return 删除结果
     */
    boolean deletePrinter(Long id);
    
    /**
     * 获取打印机详情
     * @param id 打印机ID
     * @return 打印机详情
     */
    PrinterVO getPrinterDetail(Long id);
    
    /**
     * 获取打印机列表
     * @return 打印机列表
     */
    List<PrinterVO> getPrinterList();
    
    /**
     * 更新打印机状态
     * @param id 打印机ID
     * @param status 新状态
     * @return 更新结果
     */
    boolean updatePrinterStatus(Long id, String status);
} 