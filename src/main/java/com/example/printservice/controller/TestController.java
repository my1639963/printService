package com.example.printservice.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.printservice.common.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "测试接口", description = "用于测试服务是否正常运行")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "测试问候接口", description = "返回一个简单的问候消息")
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, Print Service!");
    }

    @Operation(summary = "获取服务信息", description = "返回服务的基本信息，包括版本号和当前时间")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Print Service");
        info.put("version", "1.0.0");
        info.put("time", LocalDateTime.now());
        return Result.success(info);
    }
} 