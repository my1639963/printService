package online.fantao.tools.printservice.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("printer")
public class Printer {
    @TableId("id")
    private Long id;
    @TableField("name")
    private String name;
    @TableField("model")
    private String model;
    @TableField("ip_address")
    private String ipAddress;
    @TableField("port")
    private Integer port;
    @TableField("status")
    private String status;
    @TableField("last_online_time")
    private LocalDateTime lastOnlineTime;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("deleted")
    private Boolean deleted;
}