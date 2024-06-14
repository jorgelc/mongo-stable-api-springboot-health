package com.example.demo.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoSettingsConfiguration {

	@Bean
	public MongoClientSettings mongoClientSettings() {
		return MongoClientSettings.builder()
			.applicationName("testApp")
			.serverApi(ServerApi.builder()
				.strict(true)
				.version(ServerApiVersion.V1)
				.build())
			.build();
	}
}
