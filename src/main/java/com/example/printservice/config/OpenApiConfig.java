package online.fantao.tools.printservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("打印服务 API 文档")
                        .description("用于控制奔图打印机的 Web 服务")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Print Service")
                                .email("5926172@qq.com")));
    }
} 