package server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proto.BookServiceGrpc;

public class Connector {
    private ManagedChannel channel;
    private BookServiceGrpc.BookServiceStub stub;

    public Connector() {
        /* Default Values for connection */
        channel = ManagedChannelBuilder.forAddress("localhost", 8999).usePlaintext().build();
        stub = BookServiceGrpc.newStub(channel);
    }

    public Connector(ManagedChannel channel, BookServiceGrpc.BookServiceStub stub) {
        this.channel = channel;
        this.stub = stub;
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    public BookServiceGrpc.BookServiceStub getStub() {
        return stub;
    }

    public void setStub(BookServiceGrpc.BookServiceStub stub) {
        this.stub = stub;
    }

    public void shutDown(){
        this.channel.shutdown();
    }
}
