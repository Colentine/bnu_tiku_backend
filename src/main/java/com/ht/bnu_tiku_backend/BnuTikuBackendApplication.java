package com.ht.bnu_tiku_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ht.bnu_tiku_backend.mapper")
public class BnuTikuBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BnuTikuBackendApplication.class, args);
    }

}
