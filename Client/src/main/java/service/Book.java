package service;

import io.grpc.stub.StreamObserver;
import proto.BookOuterClass;
import proto.BookServiceGrpc;

public class Book extends BookServiceGrpc.BookServiceImplBase{
    @Override
    public StreamObserver<BookOuterClass.Book.Page> setPages(StreamObserver<BookOuterClass.Empty> responseObserver) {
        return super.setPages(responseObserver);
    }
}
