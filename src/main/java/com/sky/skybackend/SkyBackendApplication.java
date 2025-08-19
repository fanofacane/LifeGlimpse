package com.sky.skybackend;

import com.sky.skybackend.ws.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class SkyBackendApplication implements CommandLineRunner {
    @Autowired
    private WebSocketServer webSocketServer;

    public static void main(String[] args) {
        SpringApplication.run(SkyBackendApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        // 应用启动完成后再启动Netty服务
        webSocketServer.setPort(8082).start();
    }
}
