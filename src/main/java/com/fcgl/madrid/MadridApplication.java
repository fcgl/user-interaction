package com.fcgl.madrid;

import com.fcgl.madrid.user.configuration.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

//@EnableEurekaClient
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class MadridApplication {

	public static void main(String[] args) {
		SpringApplication.run(MadridApplication.class, args);
	}

}
