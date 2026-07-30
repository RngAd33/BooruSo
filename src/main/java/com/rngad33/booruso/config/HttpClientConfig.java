package com.rngad33.booruso.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * HTTP 客户端配置
 * 提供带连接池和超时控制的 OkHttpClient
 */
@Configuration
public class HttpClientConfig {

    /** 连接超时时间（秒） */
    private static final int CONNECT_TIMEOUT = 10;
    /** 读取超时时间（秒） */
    private static final int READ_TIMEOUT = 15;
    /** 写入超时时间（秒） */
    private static final int WRITE_TIMEOUT = 10;
    /** 连接池最大空闲连接数 */
    private static final int MAX_IDLE_CONNECTIONS = 10;
    /** 连接保活时长（分钟） */
    private static final long KEEP_ALIVE_MINUTES = 5;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                // 超时配置
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                // 连接池配置：复用连接，减少 TCP 握手开销
                .connectionPool(new ConnectionPool(
                        MAX_IDLE_CONNECTIONS,
                        KEEP_ALIVE_MINUTES, TimeUnit.MINUTES))
                // 失败自动重试（仅对幂等请求）
                .retryOnConnectionFailure(true)
                .build();
    }

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(
                MAX_IDLE_CONNECTIONS,
                KEEP_ALIVE_MINUTES, TimeUnit.MINUTES);
    }
}
