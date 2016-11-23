package grpc.server;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import io.grpc.Server;
import io.grpc.ServerBuilder;

@SpringBootApplication
@EnableEurekaClient
public class GrpcServer {

	
	public static void main(String[] args) {
		SpringApplication.run(GrpcServer.class, args);
	}
	
	public GrpcServer() {
		Server server = ServerBuilder.forPort(9090).addService(new EchoServiceImpl()).build();
		try {
			server.start();
			new Thread() {

				@Override
				public void run() {
					try {
						server.awaitTermination();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
