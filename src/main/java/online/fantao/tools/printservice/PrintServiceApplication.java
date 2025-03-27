package online.fantao.tools.printservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 打印服务启动类
 */
@SpringBootApplication
@MapperScan("online.fantao.tools.printservice.mapper")
public class PrintServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrintServiceApplication.class, args);
    }
}