package grpc.client;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.example.echo.EchoOuterClass.Echo;
import com.example.echo.EchoServiceGrpc;
import com.example.echo.EchoServiceGrpc.EchoServiceBlockingStub;

import io.grpc.Attributes;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver;
import io.grpc.NameResolver.Factory;
import io.grpc.ResolvedServerInfo;

@SpringBootApplication
@EnableDiscoveryClient
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	public App(final DiscoveryClient client) {
//		needed for gRPC 1.0.1 to work :S
		client.getServices();

		Channel channel = ManagedChannelBuilder.forTarget("EchoService")
				.nameResolverFactory(new Factory() {
					@Override
					public NameResolver newNameResolver(final URI targetUri, Attributes params) {
						return new NameResolver() {
							private Listener listener;

							@Override
							public void start(Listener listener) {
								this.listener = listener;
								refresh();
							}

							@Override
							public void refresh() {
//								GRPC 1.0.1
								List<List<ResolvedServerInfo>> servers = new ArrayList<>();
								for (ServiceInstance serviceInstance : client.getInstances(targetUri.toString())) {
									System.out.println("Service Instance: " + serviceInstance.getHost() + ":"
											+ serviceInstance.getPort());
									servers.add(Collections.singletonList(new ResolvedServerInfo(InetSocketAddress
											.createUnresolved(serviceInstance.getHost(), serviceInstance.getPort()),
											Attributes.EMPTY)));
								}

								this.listener.onUpdate(servers, Attributes.EMPTY);
								
//								GRPC 0.14.0
//								List<ResolvedServerInfo> servers = new ArrayList<>();
//								for (ServiceInstance serviceInstance : client.getInstances(targetUri.toString())) {
//									System.out.println("Service Instance: " + serviceInstance.getHost() + ":"
//											+ serviceInstance.getPort());
//									servers.add(new ResolvedServerInfo(InetSocketAddress
//											.createUnresolved(serviceInstance.getHost(), serviceInstance.getPort()),
//											Attributes.EMPTY));
//								}
//
//								this.listener.onUpdate(servers, Attributes.EMPTY);
							}

							@Override
							public void shutdown() {

							}

							@Override
							public String getServiceAuthority() {
								return targetUri.toString();
							}
						};
					}

					@Override
					public String getDefaultScheme() {
						return "spring";
					}
				}).usePlaintext(true)
				.build();

		System.out.println(channel);

		EchoServiceBlockingStub stub = EchoServiceGrpc.newBlockingStub(channel);
		for (int i = 0;; ++i) {
			try {
				Echo response = stub.echo(Echo.newBuilder().setMessage("Hello " + i).build());
				System.out.println(response);

				try {
					Thread.sleep(2000L);
				} catch (InterruptedException e) {
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
