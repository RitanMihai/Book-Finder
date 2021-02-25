import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proto.GreeterGrpc;
import proto.Helloworld;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        /* *
         * Establish a connection to the server using the class ManagedChannelBuilder and the function usePlaintext().
         * usePlainText() should only be used for testing or for APIs where the use of such API or the data
         * exchanged is not sensitive.
         * */
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8999).usePlaintext().build();

        /* *
         * A blocking-style stub instance of Greeter service. We can have two types of stubs: blocking and async.
         * Blocking stubs are synchronous. Non-blocking stubs are asynchronous.
         * Take care if you want to call a rpc function on a blocking stub from UI thread
         * (cause an unresponsive/laggy UI).
         * */
        GreeterGrpc.GreeterBlockingStub bookStub = GreeterGrpc.newBlockingStub(channel);

        /* *
         * Asynchronous instance of the above declaration.
         * GreeterGrpc.GreeterStub bookStub = GreeterGrpc.newStub(channel);
         * */

        /* *
         * We can now use gRPC function via our instance of GreeterBlockingStub bookStub.
         * Below we call the function sayHello(Helloworld.HelloRequest request) with name field value set to "gRPC".
         * This function return a value of type  Helloworld.HelloReply that is saved in our instance reply.
         * We can get via generated functions every field from our message, in this case we have just one field.
         * */
        Helloworld.HelloReply reply = bookStub.sayHello(Helloworld.HelloRequest.newBuilder().setName("gRPC").build());
        System.out.println(reply.getMessage());

        channel.shutdown();
    }
}
