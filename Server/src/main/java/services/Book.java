package services;

import com.google.protobuf.Descriptors;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import proto.BookOuterClass;
import proto.BookOuterClass.*;
import proto.BookServiceGrpc;

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
            Integer startBook = 0;
            Integer endBook = books.size();

            List<BookOuterClass.Book> filteredBooks = this.books;
            Boolean haveFilters = request.hasBookFilters();
            Boolean haveFieldMask = request.hasFieldMask();

            /* Apply filters if there are any */
            if (haveFilters) {
                System.out.println("Requested filters are : ");
                Map<Descriptors.FieldDescriptor, Object> allFilters = request.getBookFilters().getAllFields();

                /* *
                 * As you can see we got the field names, we know that for each of this field gRPC framework generate
                 * getters and setters following this pattern: getFieldName(), setFieldName().
                 * Ex. field title: getTitle(), setTitle.
                 * That being said we will use java Reflection to dynamically filter our list
                 * */

                Map<String, Object> methods = new HashMap<>();
                allFilters.forEach((fieldDescriptor, o) ->
                        System.out.println(" ( " + o.getClass() + " ) " + fieldDescriptor.getName() + " : " + o));

                System.out.println("Create a Map of Methods and their values ... ");
                allFilters.forEach(((fieldDescriptor, o) -> {
                    /* Construct methods name */
                    final String getter = "get";
                    String fieldName = fieldDescriptor.getName();
                    String methodName = getter + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                    methods.put(methodName, o);
                }));

                System.out.println(methods);

                filteredBooks = filteredBooks.stream().filter(book -> {
                    boolean isValid = true;

                    for (Map.Entry<String, Object> entry : methods.entrySet()) {
                        String s = entry.getKey();
                        Object o = entry.getValue();

                        try {
                            isValid = book.getClass().getMethod(s, null).invoke(book).equals(o);
                            if (!isValid)
                                break;
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }

                    return isValid;
                }).collect(Collectors.toList());
            }

            for (BookOuterClass.Book book : filteredBooks) {
                /* *
                 * An example with an instance of FieldMask class
                 * We do not need the FieldMask below it 'cause we use the one from the request
                 * */
                /*
                com.google.protobuf.FieldMask fieldMask = com.google.protobuf.FieldMask.newBuilder().
                        addPaths("title")
                        .build();
                */

                /* Apply field mask if was requested */
                if (haveFieldMask) {
                    BookOuterClass.Book.Builder filteredBook = BookOuterClass.Book.newBuilder();
                    com.google.protobuf.util.FieldMaskUtil.merge(request.getFieldMask(), book, filteredBook);
                    responseObserver.onNext(BookOuterClass.BookResponse.newBuilder().setBook(filteredBook).build());
                } else {
                    BookResponse bookResponse = BookOuterClass.BookResponse.newBuilder().setBook(book).build();
                    responseObserver.onNext(bookResponse);
                }
            }

            responseObserver.onCompleted();
        } else {
            /* gRPC status codes */
            Status status = Status.NOT_FOUND.withDescription("No books");
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
