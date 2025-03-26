package com.example.printservice.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        try {
            // 从 URL 中提取数据库文件路径
            String dbPath = dbUrl.replace("jdbc:sqlite:", "");
            Path dbFile = Paths.get(dbPath);
            
            // 如果数据库文件不存在，创建它并执行初始化脚本
            if (!Files.exists(dbFile)) {
                Files.createFile(dbFile);
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("db/init.sql"));
                populator.execute(dataSource);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
} 