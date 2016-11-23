package grpc.server;

import com.example.echo.EchoOuterClass.Echo;
import com.example.echo.EchoServiceGrpc.EchoServiceImplBase;

import io.grpc.stub.StreamObserver;

//GRPC 1.0.1
public class EchoServiceImpl extends EchoServiceImplBase {

//GRPC 0.14.0
//public class EchoServiceImpl implements EchoService {

	@Override
	public void echo(Echo request, StreamObserver<Echo> responseObserver) {
		System.out.println(request);
	    responseObserver.onNext(request);
	    responseObserver.onCompleted();
	}
	
}
