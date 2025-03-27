package online.fantao.tools.printservice.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import online.fantao.tools.printservice.bo.PrinterBO;
import online.fantao.tools.printservice.entity.Printer;
import online.fantao.tools.printservice.mapper.PrinterMapper;
import online.fantao.tools.printservice.service.PrinterService;
import online.fantao.tools.printservice.vo.PrinterVO;

/**
 * 打印机服务实现类
 * 实现打印机相关的业务逻辑
 */
@Service
public class PrinterServiceImpl implements PrinterService {

    @Autowired
    private PrinterMapper printerMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        return printerMapper.update(printer) > 0;
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
        List<Printer> printers = printerMapper.selectList();
        return printers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updatePrinterStatus(Long id, String status) {
        return printerMapper.updateStatus(id, status) > 0;
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