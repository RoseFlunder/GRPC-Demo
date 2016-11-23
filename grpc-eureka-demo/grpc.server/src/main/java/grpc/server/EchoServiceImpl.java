package grpc.server;

import com.example.echo.EchoOuterClass.Echo;
import com.example.echo.EchoServiceGrpc.EchoServiceImplBase;

import io.grpc.stub.StreamObserver;

public class EchoServiceImpl extends EchoServiceImplBase {

	@Override
	public void echo(Echo request, StreamObserver<Echo> responseObserver) {
		System.out.println(request);
	    responseObserver.onNext(request);
	    responseObserver.onCompleted();
	}
	
}
