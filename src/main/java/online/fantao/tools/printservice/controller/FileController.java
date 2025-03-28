package online.fantao.tools.printservice.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.fantao.tools.printservice.common.Result;
import online.fantao.tools.printservice.config.FileUploadConfig;
import online.fantao.tools.printservice.service.FileTypeChecker;
import online.fantao.tools.printservice.service.FileTypeCheckerFactory;

@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "文件上传相关接口")
public class FileController {

    private final FileUploadConfig fileUploadConfig;
    private final FileTypeCheckerFactory fileTypeCheckerFactory;

    @Operation(summary = "上传文件", description = "上传文件到指定目录")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadFile(
            @Parameter(description = "要上传的文件", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 检查文件大小
            if (file.getSize() > fileUploadConfig.getMaxFileSize() * 1024 * 1024) {
                return Result.error("文件大小超过限制");
            }

            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Result.error("文件名不能为空");
            }
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

            // 检查文件类型
            if (!fileTypeCheckerFactory.isSupported(fileExtension)) {
                return Result.error("不支持的文件类型");
            }

            // 获取对应的文件类型检查器
            FileTypeChecker checker = fileTypeCheckerFactory.getChecker(fileExtension);
            if (!checker.check(file.getInputStream(), fileExtension)) {
                return Result.error("文件类型与扩展名不匹配");
            }

            // 确保上传目录存在
            Path uploadPath = Paths.get(fileUploadConfig.getUploadDir());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 检查目录权限
            if (!Files.isWritable(uploadPath)) {
                return Result.error("上传目录没有写入权限");
            }

            // 生成唯一文件名
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            return Result.success(uniqueFilename);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}