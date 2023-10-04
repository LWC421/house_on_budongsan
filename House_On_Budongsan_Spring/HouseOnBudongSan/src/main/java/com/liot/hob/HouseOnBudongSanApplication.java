package com.liot.hob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling	//이거 넣어야 스케쥴링 할 수 있다
@SpringBootApplication
public class HouseOnBudongSanApplication {

	public static void main(String[] args) {
		SpringApplication.run(HouseOnBudongSanApplication.class, args);
	}

}
