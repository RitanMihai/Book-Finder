import io.grpc.Server;
import io.grpc.ServerBuilder;
import service.GreeterImpl;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            /* *
             * Do not forget to install maven. The grpc stub classes are generated when you run the protoc compiler
             * and it finds a service declaration in your proto file.
             * Do not forget the client must use the same port in order to connect to this server.
             * */
            Server server = ServerBuilder.forPort(8999).addService(new GreeterImpl()).build();

            server.start();
            System.out.println("Server started at " + server.getPort());
            server.awaitTermination();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e);
        }
    }
}
