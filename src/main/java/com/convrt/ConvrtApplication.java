package com.convrt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConvrtApplication {

	public static void main(String[] args) {
		System.getProperties().put( "server.port", 8080 );
		System.getProperties().put( "spring.http.multipart.max-file-size", "-1" );
		System.getProperties().put( "spring.http.multipart.max-request-size", "-1" );
		SpringApplication.run(ConvrtApplication.class, args);
	}
}
