package com.example.printservice.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PrinterVO {
    private Long id;
    private String name;
    private String model;
    private String ipAddress;
    private Integer port;
    private String status;
    private LocalDateTime lastOnlineTime;
    private String statusText;
    private String lastOnlineTimeText;
} 