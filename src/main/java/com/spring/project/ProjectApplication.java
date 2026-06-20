package com.spring.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ProjectApplication.class, args);

		String port = context.getEnvironment().getProperty("local.server.port");
		String sslEnabled = context.getEnvironment().getProperty("server.ssl.enabled", "false");
		String protocol = sslEnabled.equalsIgnoreCase("true") ? "https" : "http";

		System.out.println("\n---------------------------------------------------------");
		System.out.println("\tApplication is running! Access URL:");
		System.out.println("\tLocal: \t\t" + protocol + "://localhost:" + port);
		System.out.println("---------------------------------------------------------\n");
	}
}