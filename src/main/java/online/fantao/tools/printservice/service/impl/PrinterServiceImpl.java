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
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReasons;

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
            // 获取所有打印机服务
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

            // 获取所有网络接口信息
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            List<String> localIps = interfaces.stream()
                .filter(ni -> {
                    try {
                        return ni.isUp() && !ni.isLoopback() && !ni.isVirtual();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .flatMap(ni -> Collections.list(ni.getInetAddresses()).stream())
                .filter(addr -> addr instanceof java.net.Inet4Address)
                .map(addr -> addr.getHostAddress())
                .collect(Collectors.toList());

            for (PrintService printService : printServices) {
                PrinterVO printerVO = new PrinterVO();
                printerVO.setName(printService.getName());

                // 设置是否为默认打印机
                printerVO.setIsDefault(printService.equals(defaultPrintService));

                // 设置打印机属性
                Map<String, String> attributes = new HashMap<>();
                for (Attribute attr : printService.getAttributes().toArray()) {
                    attributes.put(attr.getCategory().getName(), attr.toString());
                }
                printerVO.setAttributes(attributes);

                // 判断打印机状态
                boolean isOnline = false;
                try {
                    // 检查打印机是否接受任务
                    PrinterIsAcceptingJobs acceptingJobs = printService.getAttribute(PrinterIsAcceptingJobs.class);
                    if (acceptingJobs != null) {
                        isOnline = acceptingJobs.getValue() == PrinterIsAcceptingJobs.ACCEPTING_JOBS.getValue();
                    }

                    // 检查打印机状态
                    PrinterState printerState = printService.getAttribute(PrinterState.class);
                    if (printerState != null) {
                        // 如果打印机状态不是空闲，则设置为离线
                        if (printerState.getValue() != PrinterState.IDLE.getValue()) {
                            isOnline = false;
                        }
                    }

                    // 检查打印机状态原因
                    PrinterStateReasons stateReasons = printService.getAttribute(PrinterStateReasons.class);
                    if (stateReasons != null && !stateReasons.isEmpty()) {
                        // 如果有错误状态，则设置为离线
                        isOnline = false;
                    }
                } catch (Exception e) {
                    log.warn("获取打印机状态失败: {}", e.getMessage());
                }
                printerVO.setStatus(isOnline ? "ONLINE" : "OFFLINE");

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
                    // 首先尝试从打印机URI获取
                    Attribute uriAttr = printService.getAttribute(PrinterURI.class);
                    if (uriAttr != null) {
                        String uri = uriAttr.toString();
                        if (uri.startsWith("socket://")) {
                            String ipPort = uri.substring(9);
                            String[] parts = ipPort.split(":");
                            if (parts.length == 2) {
                                printerVO.setIpAddress(parts[0]);
                                printerVO.setPort(Integer.parseInt(parts[1]));
                            }
                        }
                    }

                    // 如果无法从URI获取，尝试从本地网络扫描
                    if (printerVO.getIpAddress() == null) {
                        for (String localIp : localIps) {
                            String ipPrefix = localIp.substring(0, localIp.lastIndexOf(".") + 1);
                            for (int i = 1; i < 255; i++) {
                                String ip = ipPrefix + i;
                                if (isPrinterReachable(ip, 9100)) {
                                    printerVO.setIpAddress(ip);
                                    printerVO.setPort(9100);
                                    break;
                                }
                            }
                            if (printerVO.getIpAddress() != null) {
                                break;
                            }
                        }
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
     * 检查打印机是否可达
     * 
     * @param ip   IP地址
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
     * 
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
     * 
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
     * 
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