package com.gymapp.ms_gamificacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class MsGamificacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGamificacionApplication.class, args);
	}

}
