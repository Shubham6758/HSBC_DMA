package com.coforge.hsbcdma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication

@EnableAspectJAutoProxy
@EnableScheduling

public class HsbcDmaApplication {

    private static final Logger logger = LoggerFactory.getLogger(HsbcDmaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(HsbcDmaApplication.class, args);
 	}
}
