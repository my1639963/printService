package com.example.printservice.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 打印机业务对象
 * 用于接收前端请求数据，包含参数验证
 */
@Data
public class PrinterBO {
    /**
     * 打印机ID
     */
    private Long id;
    
    /**
     * 打印机名称
     * 不能为空，长度在2-50个字符之间
     */
    @NotBlank(message = "打印机名称不能为空")
    @Pattern(regexp = "^.{2,50}$", message = "打印机名称长度必须在2-50个字符之间")
    private String name;
    
    /**
     * 打印机型号
     * 不能为空，长度在2-30个字符之间
     */
    @NotBlank(message = "打印机型号不能为空")
    @Pattern(regexp = "^.{2,30}$", message = "打印机型号长度必须在2-30个字符之间")
    private String model;
    
    /**
     * IP地址
     * 不能为空，必须是有效的IP地址格式
     */
    @NotBlank(message = "IP地址不能为空")
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$", 
            message = "IP地址格式不正确")
    private String ipAddress;
    
    /**
     * 端口号
     * 不能为空，必须是1-65535之间的正整数
     */
    @NotNull(message = "端口号不能为空")
    @Positive(message = "端口号必须大于0")
    private Integer port;
    
    /**
     * 状态
     * 不能为空，必须是ONLINE、OFFLINE或ERROR之一
     */
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "^(ONLINE|OFFLINE|ERROR)$", message = "状态值不正确")
    private String status;
    
    /**
     * 最后在线时间
     */
    private LocalDateTime lastOnlineTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    private Boolean deleted;
} 