package online.fantao.tools.printservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {
    /**
     * 文件上传目录
     */
    private String uploadDir;
    
    /**
     * 允许的文件类型，多个类型用逗号分隔
     */
    private String allowedTypes;
    
    /**
     * 最大文件大小（MB）
     */
    private Long maxFileSize;
} 