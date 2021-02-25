package service;

import io.grpc.stub.StreamObserver;
import proto.GreeterGrpc;
import proto.Helloworld;

public class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    /*
    * We observe here that some words have an "@", this are Annotations. Annotations are used to provide supplement
    * information about a program. We can autogenerate this functions, in Intellij we can use the shortcut ctrl + O to
    * do this.
    * */
    @Override
    public void sayHello(Helloworld.HelloRequest request, StreamObserver<Helloworld.HelloReply> responseObserver) {
        Helloworld.HelloReply reply = Helloworld.HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
        /* We can call multiple times onNext function if we have multiple replies, ex. in next commits */
        responseObserver.onNext(reply);
        /* We use the response observer's onCompleted method to specify that we've finished dealing with the RPC */
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloAgain(Helloworld.HelloRequest request, StreamObserver<Helloworld.HelloReply> responseObserver) {
        Helloworld.HelloReply reply = Helloworld.HelloReply.newBuilder().setMessage("Hello again " + request.getName()).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
