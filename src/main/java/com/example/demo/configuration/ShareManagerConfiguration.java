package com.example.demo.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

import com.example.demo.service.SharedTableManager;

import io.delta.sharing.server.config.SchemaConfig;
import io.delta.sharing.server.config.ServerConfig;
import io.delta.sharing.server.config.ShareConfig;
import io.delta.sharing.server.config.TableConfig;

@Configuration
public class ShareManagerConfiguration {

	@Bean
	public SharedTableManager sharedTableManager() {
		List<ShareConfig> scl = Arrays.asList(new ShareConfig("share1",
				Arrays.asList(new SchemaConfig("schema1", Arrays.asList(new TableConfig("table1", "s31"))))));
		ServerConfig sc = new ServerConfig(null, scl, null,null,
				"localhost",80,
				"/delta-sharing", 3600, 10, false, false);

		return new SharedTableManager(sc);
	}
	


	@Bean
	ProtobufHttpMessageConverter protobufHttpMessageConverter() {
		return new ProtobufHttpMessageConverter();
	}
}
