package online.fantao.tools.printservice.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * 文档文件类型检查器
 */
public class DocumentFileTypeChecker extends AbstractFileTypeChecker {
    
    private static final byte[] PDF_MAGIC = {'%', 'P', 'D', 'F', '-'};
    private static final byte[] DOC_MAGIC = {(byte)0xD0, (byte)0xCF, 0x11, (byte)0xE0, (byte)0xA1, (byte)0xB1, 0x1A, (byte)0xE1};
    private static final byte[] DOCX_MAGIC = {'P', 'K', 0x03, 0x04};
    private static final byte[] XLS_MAGIC = {(byte)0xD0, (byte)0xCF, 0x11, (byte)0xE0, (byte)0xA1, (byte)0xB1, 0x1A, (byte)0xE1};
    private static final byte[] XLSX_MAGIC = {'P', 'K', 0x03, 0x04};
    private static final byte[] TXT_MAGIC = {'T', 'e', 'x', 't'};
    
    private static final List<byte[]> DOCUMENT_MAGIC_NUMBERS = Arrays.asList(
        PDF_MAGIC, DOC_MAGIC, DOCX_MAGIC, XLS_MAGIC, XLSX_MAGIC, TXT_MAGIC
    );
    
    @Override
    public boolean check(InputStream inputStream, String extension) throws IOException {
        // 首先检查扩展名
        if (!checkExtension(extension)) {
            return false;
        }
        
        // 然后检查文件头
        return checkMagicNumbers(inputStream, DOCUMENT_MAGIC_NUMBERS);
    }
    
    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"pdf", "doc", "docx", "xls", "xlsx", "txt"};
    }
} 