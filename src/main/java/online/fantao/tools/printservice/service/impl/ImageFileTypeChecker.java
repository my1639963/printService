package online.fantao.tools.printservice.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * 图片文件类型检查器
 */
public class ImageFileTypeChecker extends AbstractFileTypeChecker {
    
    private static final byte[] JPEG_MAGIC = {(byte)0xFF, (byte)0xD8, (byte)0xFF};
    private static final byte[] PNG_MAGIC = {(byte)0x89, 0x50, 0x4E, 0x47};
    private static final byte[] GIF_MAGIC = {'G', 'I', 'F'};
    private static final byte[] BMP_MAGIC = {'B', 'M'};
    
    private static final List<byte[]> IMAGE_MAGIC_NUMBERS = Arrays.asList(
        JPEG_MAGIC, PNG_MAGIC, GIF_MAGIC, BMP_MAGIC
    );
    
    @Override
    public boolean check(InputStream inputStream, String extension) throws IOException {
        // 首先检查扩展名
        if (!checkExtension(extension)) {
            return false;
        }
        
        // 然后检查文件头
        return checkMagicNumbers(inputStream, IMAGE_MAGIC_NUMBERS);
    }
    
    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"jpg", "jpeg", "png", "gif", "bmp"};
    }
} 