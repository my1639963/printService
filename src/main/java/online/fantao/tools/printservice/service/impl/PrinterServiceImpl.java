package online.fantao.tools.printservice.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import online.fantao.tools.printservice.bo.PrinterBO;
import online.fantao.tools.printservice.entity.Printer;
import online.fantao.tools.printservice.mapper.PrinterMapper;
import online.fantao.tools.printservice.service.PrinterService;
import online.fantao.tools.printservice.vo.PrinterVO;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import online.fantao.tools.printservice.util.SnmpUtil;

/**
 * 打印机服务实现类
 * 实现打印机相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrinterServiceImpl implements PrinterService {

    private final PrinterMapper printerMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // 定义常用的文档类型
    private static final DocFlavor[] DOC_FLAVORS = {
        DocFlavor.SERVICE_FORMATTED.PAGEABLE,
        DocFlavor.SERVICE_FORMATTED.PRINTABLE,
        DocFlavor.BYTE_ARRAY.AUTOSENSE,
        DocFlavor.INPUT_STREAM.AUTOSENSE,
        DocFlavor.URL.AUTOSENSE
    };

    @Override
    @Transactional
    public boolean addPrinter(PrinterBO printerBO) {
        Printer printer = convertToEntity(printerBO);
        return printerMapper.insert(printer) > 0;
    }

    @Override
    @Transactional
    public boolean updatePrinter(PrinterBO printerBO) {
        Printer printer = convertToEntity(printerBO);
        return printerMapper.updateById(printer) > 0;
        
    }

    @Override
    @Transactional
    public boolean deletePrinter(Long id) {
        return printerMapper.deleteById(id) > 0;
    }

    @Override
    public PrinterVO getPrinterDetail(Long id) {
        Printer printer = printerMapper.selectById(id);
        return convertToVO(printer);
    }

    @Override
    public List<PrinterVO> getPrinterList() {
        List<Printer> printers = printerMapper.selectList(null);
        return printers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updatePrinterStatus(Long id, String status) {
        Printer printer = new Printer();
        printer.setId(id);
        printer.setStatus(status);
        return printerMapper.updateById(printer) > 0;
    }

    @Override
    public List<PrinterVO> getSystemPrinters() {
        List<PrinterVO> printers = new ArrayList<>();
        try {
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
            
            for (PrintService printService : printServices) {
                PrinterVO printerVO = new PrinterVO();
                printerVO.setName(printService.getName());
                
                // 设置是否为默认打印机
                printerVO.setIsDefault(printService.equals(defaultPrintService));
                
                // 设置打印机状态（根据是否支持打印来判断）
                boolean isOnline = false;
                for (DocFlavor flavor : DOC_FLAVORS) {
                    if (printService.isDocFlavorSupported(flavor)) {
                        isOnline = true;
                        break;
                    }
                }
                printerVO.setStatus(isOnline ? "ONLINE" : "OFFLINE");
                
                // 设置打印机属性
                Map<String, String> attributes = new HashMap<>();
                for (Attribute attr : printService.getAttributes().toArray()) {
                    attributes.put(attr.getCategory().getName(), attr.toString());
                }
                printerVO.setAttributes(attributes);
                
                // 设置最后在线时间
                if (isOnline) {
                    printerVO.setLastOnlineTime(LocalDateTime.now());
                }
                
                // 设置状态文本
                printerVO.setStatusText(convertStatusText(printerVO.getStatus()));
                
                // 设置最后在线时间文本
                if (printerVO.getLastOnlineTime() != null) {
                    printerVO.setLastOnlineTimeText(printerVO.getLastOnlineTime().format(DATE_TIME_FORMATTER));
                }

                // 如果是真实打印机，尝试获取IP和端口
                if (!printerVO.getName().contains("PDF") && !printerVO.getName().contains("Microsoft")) {
                    Map<String, String> networkInfo = SnmpUtil.getPrinterNetworkInfo(printerVO.getName());
                    if (!networkInfo.isEmpty()) {
                        printerVO.setIpAddress(networkInfo.get("ipAddress"));
                        printerVO.setPort(Integer.parseInt(networkInfo.get("port")));
                    }
                }
                
                printers.add(printerVO);
            }
        } catch (Exception e) {
            log.error("获取系统打印机列表失败", e);
        }
        return printers;
    }

    /**
     * 将BO对象转换为实体对象
     * @param bo 业务对象
     * @return 实体对象
     */
    private Printer convertToEntity(PrinterBO bo) {
        if (bo == null) {
            return null;
        }
        Printer entity = new Printer();
        BeanUtils.copyProperties(bo, entity);
        return entity;
    }

    /**
     * 将实体对象转换为VO对象
     * @param entity 实体对象
     * @return 视图对象
     */
    private PrinterVO convertToVO(Printer entity) {
        if (entity == null) {
            return null;
        }
        PrinterVO vo = new PrinterVO();
        BeanUtils.copyProperties(entity, vo);
        
        // 设置状态文本
        vo.setStatusText(convertStatusText(entity.getStatus()));
        
        // 设置最后在线时间文本
        if (entity.getLastOnlineTime() != null) {
            vo.setLastOnlineTimeText(entity.getLastOnlineTime().format(DATE_TIME_FORMATTER));
        }
        
        return vo;
    }

    /**
     * 转换状态文本
     * @param status 状态代码
     * @return 状态文本
     */
    private String convertStatusText(String status) {
        switch (status) {
            case "ONLINE":
                return "在线";
            case "OFFLINE":
                return "离线";
            case "ERROR":
                return "错误";
            default:
                return "未知";
        }
    }
} 