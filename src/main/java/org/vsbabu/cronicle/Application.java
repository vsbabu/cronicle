package org.vsbabu.cronicle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import reactor.bus.EventBus;
import reactor.Environment;


@SpringBootApplication
@EnableJpaRepositories
@EnableAutoConfiguration
@EnableScheduling
public class Application {


	@Bean
	Environment env() {
		return Environment.initializeIfEmpty().assignErrorJournal();
	}

	@Bean
	EventBus createEventBus(Environment env) {
		return EventBus.create(env, Environment.THREAD_POOL);
	}

	public static void main(String[] args) throws Exception {
		ApplicationContext app = SpringApplication.run(Application.class, args);
	}

}
