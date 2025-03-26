package com.example.printservice.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Printer {
    private Long id;
    private String name;
    private String model;
    private String ipAddress;
    private Integer port;
    private String status;
    private LocalDateTime lastOnlineTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
} 