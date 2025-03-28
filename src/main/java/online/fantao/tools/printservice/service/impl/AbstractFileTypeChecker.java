package online.fantao.tools.printservice.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import online.fantao.tools.printservice.service.FileTypeChecker;

/**
 * 文件类型检查器抽象基类
 */
public abstract class AbstractFileTypeChecker implements FileTypeChecker {
    
    /**
     * 检查文件头是否匹配
     * @param inputStream 文件输入流
     * @param magicNumbers 魔数
     * @return 是否匹配
     */
    protected boolean checkMagicNumbers(InputStream inputStream, byte[] magicNumbers) throws IOException {
        byte[] header = new byte[magicNumbers.length];
        int bytesRead = inputStream.read(header);
        
        if (bytesRead != magicNumbers.length) {
            return false;
        }
        
        return Arrays.equals(header, magicNumbers);
    }

    /**
     * 检查文件头是否匹配（支持多个魔数）
     * @param inputStream 文件输入流
     * @param magicNumbersList 魔数列表
     * @return 是否匹配
     */
    protected boolean checkMagicNumbers(InputStream inputStream, List<byte[]> magicNumbersList) throws IOException {
        // 读取文件头
        int maxLength = magicNumbersList.stream()
                .mapToInt(arr -> arr.length)
                .max()
                .orElse(0);
        
        byte[] header = new byte[maxLength];
        int bytesRead = inputStream.read(header);
        
        if (bytesRead < maxLength) {
            return false;
        }
        
        // 检查每个魔数
        for (byte[] magicNumbers : magicNumbersList) {
            if (Arrays.equals(Arrays.copyOf(header, magicNumbers.length), magicNumbers)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 检查文件扩展名是否匹配
     * @param extension 文件扩展名
     * @return 是否匹配
     */
    protected boolean checkExtension(String extension) {
        return Arrays.asList(getSupportedExtensions()).contains(extension.toLowerCase());
    }
} 