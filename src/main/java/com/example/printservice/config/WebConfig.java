package online.fantao.tools.printservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import online.fantao.tools.printservice.interceptor.RequestLogInterceptor;

/**
 * Web配置类
 * 配置拦截器等Web相关组件
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RequestLogInterceptor requestLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(     // 排除不需要拦截的路径
                    "/error",            // 错误页面
                    "/swagger-ui/**",    // Swagger UI
                    "/api-docs/**",      // OpenAPI 文档
                    "/favicon.ico"       // 网站图标
                );
    }
} 