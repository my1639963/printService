package online.fantao.tools.printservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import online.fantao.tools.printservice.interceptor.RequestLogInterceptor;

/**
 * Web配置类
 * 配置拦截器等Web相关组件
 */
@Configuration
@RequiredArgsConstructor
@SuppressWarnings("null")
public class WebConfig implements WebMvcConfigurer {

    private final RequestLogInterceptor requestLogInterceptor;

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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源处理
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        
        // 配置favicon.ico
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.ico");
    }
} 