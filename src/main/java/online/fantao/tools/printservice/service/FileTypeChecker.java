package online.fantao.tools.printservice.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件类型检查器接口
 */
public interface FileTypeChecker {
    /**
     * 检查文件类型是否匹配
     * @param inputStream 文件输入流
     * @param extension 文件扩展名
     * @return 是否匹配
     */
    boolean check(InputStream inputStream, String extension) throws IOException;

    /**
     * 获取支持的文件扩展名
     * @return 支持的文件扩展名列表
     */
    String[] getSupportedExtensions();
} 