package com.appsdeveloperblog.photoapp.api.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import feign.Logger;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PhotoAppApiUsersApplication {

	@Autowired
	Environment environment ;
	
	public static void main(String[] args) {
		SpringApplication.run(PhotoAppApiUsersApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public HttpExchangeRepository httpTraceRepository() {
		return new InMemoryHttpExchangeRepository();
	}

	/*
	 * @Bean
	 * 
	 * @LoadBalanced public RestTemplate getRestTemplate() { return new
	 * RestTemplate();
	 * 
	 * }
	 */

	@Bean
	Logger.Level feignLoggerLover() {
		return Logger.Level.FULL;
	}

	// If we have declared any class with @Component annotation already,then
	// springboot will perform classpath scan automatically
	// and it is not needed to declare that class with @Bean annotation manually.
//		@Bean
//		public FeignErrorDecoder getFeignErrorDecoder() {
//			return new FeignErrorDecoder();
//	}

	@Bean
	@Profile("production")
	public String createProductionBean() {
		System.out.println("Production Bean Created. myapplication.environment = " + environment.getProperty("myapplication.environment"));
		return "Not Production bean";
	}

	@Bean
	@Profile("!production")
	public String createNotProductionBean() {
		System.out.println("Not Production Bean Created. myapplication.environment = " + environment.getProperty("myapplication.environment"));
		return "Productionbean";
	}

	@Bean
	@Profile("default")
	public String createDevelopmentBean() {
		System.out.println("Development Bean Created. myapplication.environment = " + environment.getProperty("myapplication.environment"));
		return "Development bean";
	}

}
