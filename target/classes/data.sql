-- 插入测试打印机数据
INSERT INTO printer (name, model, ip_address, port, status, last_online_time) VALUES
('测试打印机1', 'Pantum P2500', '192.168.1.100', 9100, 1, CURRENT_TIMESTAMP),
('测试打印机2', 'Pantum P3300', '192.168.1.101', 9100, 1, CURRENT_TIMESTAMP),
('测试打印机3', 'Pantum P3500', '192.168.1.102', 9100, 0, NULL); 