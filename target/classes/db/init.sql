-- 创建打印机表
CREATE TABLE IF NOT EXISTS printer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '打印机名称',
    model VARCHAR(50) NOT NULL COMMENT '打印机型号',
    ip_address VARCHAR(50) NOT NULL COMMENT 'IP地址',
    port INTEGER NOT NULL COMMENT '端口号',
    status VARCHAR(20) NOT NULL COMMENT '状态',
    last_online_time DATETIME COMMENT '最后在线时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN NOT NULL DEFAULT 0 COMMENT '是否删除'
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_printer_ip ON printer(ip_address);
CREATE INDEX IF NOT EXISTS idx_printer_status ON printer(status);
CREATE INDEX IF NOT EXISTS idx_printer_deleted ON printer(deleted);

-- 插入测试数据
INSERT INTO printer (name, model, ip_address, port, status, last_online_time)
VALUES 
('测试打印机1', 'M6200', '192.168.1.100', 9100, 'ONLINE', CURRENT_TIMESTAMP),
('测试打印机2', 'M6200', '192.168.1.101', 9100, 'OFFLINE', NULL);
