-- 创建打印机表
CREATE TABLE IF NOT EXISTS printer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    ip_address VARCHAR(50) NOT NULL,
    port INTEGER NOT NULL,
    status INTEGER DEFAULT 0,
    last_online_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_printer_ip_port ON printer(ip_address, port);
CREATE INDEX IF NOT EXISTS idx_printer_status ON printer(status); 