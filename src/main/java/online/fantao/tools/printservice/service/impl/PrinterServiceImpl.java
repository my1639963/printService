package online.fantao.tools.printservice.service.impl;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.PrinterURI;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.fantao.tools.printservice.bo.PrinterBO;
import online.fantao.tools.printservice.entity.Printer;
import online.fantao.tools.printservice.mapper.PrinterMapper;
import online.fantao.tools.printservice.service.PrinterService;
import online.fantao.tools.printservice.vo.PrinterVO;

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
                    Map<String, String> networkInfo = getPrinterNetworkInfo(printerVO.getName());
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
     * 获取打印机IP地址和端口号
     * @param printerName 打印机名称
     * @return 包含IP和端口的信息
     */
    private Map<String, String> getPrinterNetworkInfo(String printerName) {
        Map<String, String> result = new HashMap<>();
        try {
            // 获取所有打印机服务
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            
            // 查找匹配的打印机
            for (PrintService printService : printServices) {
                // 获取打印机名称
                String name = printService.getName();
                if (name.equals(printerName)) {
                    // 获取打印机URI
                    Attribute uriAttr = printService.getAttribute(PrinterURI.class);
                    if (uriAttr != null) {
                        String uri = uriAttr.toString();
                        // 解析URI获取IP地址
                        if (uri.startsWith("socket://")) {
                            String ipPort = uri.substring(9); // 移除 "socket://" 前缀
                            String[] parts = ipPort.split(":");
                            if (parts.length == 2) {
                                result.put("ipAddress", parts[0]);
                                result.put("port", parts[1]);
                                return result;
                            }
                        }
                    }
                    
                    // 如果无法从URI获取，尝试从打印机名称解析
                    if (name.contains("(") && name.contains(")")) {
                        String ipPort = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
                        String[] parts = ipPort.split(":");
                        if (parts.length == 2) {
                            result.put("ipAddress", parts[0]);
                            result.put("port", parts[1]);
                            return result;
                        }
                    }
                    
                    // 如果还是无法获取，尝试从本地网络接口获取
                    List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                    for (NetworkInterface ni : interfaces) {
                        if (ni.isUp() && !ni.isLoopback() && !ni.isVirtual()) {
                            for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                                if (addr instanceof java.net.Inet4Address) {
                                    String ip = addr.getHostAddress();
                                    // 尝试连接打印机
                                    if (isPrinterReachable(ip, 9100)) {
                                        result.put("ipAddress", ip);
                                        result.put("port", "9100");
                                        return result;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取打印机网络信息失败", e);
        }
        return result;
    }

    /**
     * 检查打印机是否可达
     * @param ip IP地址
     * @param port 端口号
     * @return 是否可达
     */
    private boolean isPrinterReachable(String ip, int port) {
        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress(ip, port), 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
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