package online.fantao.tools.printservice.interceptor;

import java.util.Collections;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 请求日志拦截器
 * 记录请求和响应的详细信息
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String headers = Collections.list(request.getHeaderNames())
                .stream()
                .map(headerName -> headerName + ": " + request.getHeader(headerName))
                .collect(Collectors.joining(", "));

        // 记录请求信息
        logger.info("收到请求 - URI: {}, Method: {}, Query: {}, Headers: {}", 
                requestURI, method, queryString, headers);

        // 包装请求，以便多次读取请求体
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        request.setAttribute("requestWrapper", requestWrapper);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request.getAttribute("requestWrapper");
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

            // 获取请求体
            String requestBody = new String(requestWrapper.getContentAsByteArray());
            if (!requestBody.isEmpty()) {
                logger.info("请求体: {}", requestBody);
            }

            // 获取响应体
            String responseBody = new String(responseWrapper.getContentAsByteArray());
            if (!responseBody.isEmpty()) {
                logger.info("响应体: {}", responseBody);
            }

            // 复制响应体到原始响应
            responseWrapper.copyBodyToResponse();
        } catch (Exception e) {
            logger.error("记录请求日志时发生错误", e);
        }
    }
} 