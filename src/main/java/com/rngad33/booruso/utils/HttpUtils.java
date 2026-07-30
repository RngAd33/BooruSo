package com.rngad33.booruso.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 基于 OkHttp 的统一 HTTP 工具类
 * 支持连接池复用、超时控制、自动重试
 */
@Component
@Slf4j
public class HttpUtils {

    @Resource
    private ConnectionPool connectionPool;

    private OkHttpClient client;

    @PostConstruct
    public void init() {
        this.client = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                // 自定义请求头，模拟浏览器
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                            .header("Accept", "text/html,application/xhtml+xml,application/json,*/*")
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    @PreDestroy
    public void cleanup() {
        // 关闭连接池，释放资源
        connectionPool.evictAll();
        log.info("HTTP 连接池已清理");
    }

    /**
     * 同步 GET 请求
     *
     * @param url 请求地址
     * @return 响应体字符串，失败返回 null
     */
    public String get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warn("HTTP 请求失败 - URL: {}, Code: {}", url, response.code());
                return null;
            }
            ResponseBody body = response.body();
            return body != null ? body.string() : null;
        } catch (IOException e) {
            log.error("HTTP 请求异常 - URL: {}, 错误: {}", url, e.getMessage());
            return null;
        }
    }

    /**
     * 同步 GET 请求（带自定义超时）
     *
     * @param url     请求地址
     * @param timeout 超时时间（秒）
     * @return 响应体字符串，失败返回 null
     */
    public String get(String url, int timeout) {
        // 为单次请求创建带自定义超时的客户端
        OkHttpClient customClient = client.newBuilder()
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = customClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warn("HTTP 请求失败 - URL: {}, Code: {}", url, response.code());
                return null;
            }
            ResponseBody body = response.body();
            return body != null ? body.string() : null;
        } catch (IOException e) {
            log.error("HTTP 请求异常 - URL: {}, 错误: {}", url, e.getMessage());
            return null;
        }
    }
}
