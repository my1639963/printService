package online.fantao.tools.printservice.service;

import java.util.List;
import java.util.Map;

import online.fantao.tools.printservice.bo.PrinterBO;
import online.fantao.tools.printservice.vo.PrinterVO;

/**
 * 打印机服务接口
 * 提供打印机相关的业务操作
 */
public interface PrinterService {
    
    /**
     * 添加打印机
     * @param printerBO 打印机信息
     * @return 是否添加成功
     */
    boolean addPrinter(PrinterBO printerBO);
    
    /**
     * 更新打印机信息
     * @param printerBO 打印机信息
     * @return 是否更新成功
     */
    boolean updatePrinter(PrinterBO printerBO);
    
    /**
     * 删除打印机
     * @param id 打印机ID
     * @return 是否删除成功
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
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updatePrinterStatus(Long id, String status);

    /**
     * 获取系统中已安装的打印机列表
     * @return 打印机列表
     */
    List<PrinterVO> getSystemPrinters();
} 