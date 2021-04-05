package services;

import com.google.protobuf.Any;
import com.google.protobuf.Descriptors;
import com.google.rpc.Code;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import io.grpc.util.TransmitStatusRuntimeExceptionInterceptor;
import proto.BookOuterClass;
import proto.BookOuterClass.*;
import proto.BookServiceGrpc;
import services.util.BookUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Book extends BookServiceGrpc.BookServiceImplBase {
    /* Simulate a database at runtime */
    List<BookOuterClass.Book> books = new ArrayList<>();

    @Override
    public void getBooks(BookOuterClass.BooksRequest request, StreamObserver<BookOuterClass.BookResponse> responseObserver) {
        /* Firstly check if we have any books */
        if (this.books.size() != 0) {
            List<BookOuterClass.Book> filteredBooks = this.books;
            Boolean haveFilters = request.hasBookFilters();
            Boolean haveFieldMask = request.hasFieldMask();

            /* Apply filters if there are any */
            if (haveFilters) {
                Map<Descriptors.FieldDescriptor, Object> filters = request.getBookFilters().getAllFields();
                filteredBooks = BookUtil.filterList(this.books, filters);
            }

            /* Apply field mask if was requested */
            if (haveFieldMask) {
                /* *
                 * An example with an instance of FieldMask class
                 * We do not need the FieldMask below it 'cause we use the one from the request
                 * */
                /*
                com.google.protobuf.FieldMask fieldMask = com.google.protobuf.FieldMask.newBuilder().
                        addPaths("title")
                        .build();
                */
                for (int i = 0; i < filteredBooks.size(); i++) {
                    BookOuterClass.Book.Builder filteredBook = BookOuterClass.Book.newBuilder();
                    com.google.protobuf.util.FieldMaskUtil.merge(request.getFieldMask(), filteredBooks.get(i), filteredBook);
                    filteredBooks.set(i, filteredBook.build());
                }

            }

            /* Make a map of all request fields and their values */
            Map<Descriptors.FieldDescriptor, Object> requestFields = request.getAllFields();
            /* *
             * Check if pagination was requested
             * If startBook/endBook exist a value will be returned otherwise null
             * */

            Descriptors.FieldDescriptor firstBookDescriptor = request.getDescriptorForType().findFieldByName("first_book");
            Descriptors.FieldDescriptor lastBookDescriptor = request.getDescriptorForType().findFieldByName("last_book");

            Object firstBookIndex = requestFields.get(firstBookDescriptor);
            Object lastBookIndex = requestFields.get(lastBookDescriptor);

            filteredBooks = BookUtil.pagination(filteredBooks, firstBookIndex, lastBookIndex);

            if (filteredBooks.isEmpty())
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Empty list requested").asRuntimeException());

            else {
                filteredBooks.forEach(book -> responseObserver.onNext(BookOuterClass.BookResponse.newBuilder().setBook(book).build()));
                responseObserver.onCompleted();
            }

        } else {
            /* gRPC status codes */
            Status status = Status.NOT_FOUND.withDescription("There are no books");
            responseObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void setBook(BookOuterClass.Book request, StreamObserver<Empty> responseObserver) {
        books.add(request);

        System.out.println(request);

        Empty.Builder response = Empty.newBuilder();
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
