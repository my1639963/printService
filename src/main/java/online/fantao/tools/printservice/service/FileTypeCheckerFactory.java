package online.fantao.tools.printservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import online.fantao.tools.printservice.service.impl.DocumentFileTypeChecker;
import online.fantao.tools.printservice.service.impl.ImageFileTypeChecker;

/**
 * 文件类型检查器工厂类
 */
@Component
public class FileTypeCheckerFactory {
    
    private final Map<String, FileTypeChecker> checkers = new HashMap<>();
    
    public FileTypeCheckerFactory() {
        // 注册所有检查器
        registerChecker(new ImageFileTypeChecker());
        registerChecker(new DocumentFileTypeChecker());
    }
    
    /**
     * 注册文件类型检查器
     * @param checker 检查器实例
     */
    private void registerChecker(FileTypeChecker checker) {
        for (String extension : checker.getSupportedExtensions()) {
            checkers.put(extension.toLowerCase(), checker);
        }
    }
    
    /**
     * 获取文件类型检查器
     * @param extension 文件扩展名
     * @return 检查器实例
     */
    public FileTypeChecker getChecker(String extension) {
        return checkers.get(extension.toLowerCase());
    }
    
    /**
     * 检查是否支持该文件类型
     * @param extension 文件扩展名
     * @return 是否支持
     */
    public boolean isSupported(String extension) {
        return checkers.containsKey(extension.toLowerCase());
    }
} 