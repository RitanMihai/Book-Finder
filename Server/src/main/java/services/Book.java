package services;

import com.google.protobuf.Descriptors;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import proto.BookOuterClass;
import proto.BookOuterClass.*;
import proto.BookServiceGrpc;
import services.util.BookUtil;

import java.util.*;

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

        Empty.Builder response = Empty.newBuilder();
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPage(PageRequest request, StreamObserver<PageResponse> responseObserver) {
        /* Get Book */
        BookOuterClass.Book book = books.stream().filter(element -> element.getId() == (request.getBookId()))
                .findFirst().orElse(null);

        /* Error handling */
        if (book == null) {
            /* gRPC status codes */
            Status status = Status.NOT_FOUND.withDescription("Book by this id not found");
            responseObserver.onError(status.asRuntimeException());
        } else {
            BookOuterClass.Page page = book.getPagesList().stream().filter(element -> element.getPageNumber() == (request.getPageNumber()))
                    .findFirst().orElse(null);
            if (page == null) {
                Status status = Status.NOT_FOUND.withDescription("Page not found");
                responseObserver.onError(status.asRuntimeException());
            } else {
                responseObserver.onNext(PageResponse.newBuilder().setPage(page).build());
                responseObserver.onCompleted();
            }
        }
    }

    @Override
    public void setPage(Page request, StreamObserver<Empty> responseObserver) {
        /* Get Book */
        BookOuterClass.Book searchedBook = books.stream().filter(element -> element.getId() == (request.getBookId()))
                .findFirst().orElse(null);

        /* Error handling */
        if (searchedBook == null) {
            Status status = Status.NOT_FOUND.withDescription("Book not found");
            responseObserver.onError(status.asRuntimeException());
        } else {
            Integer bookIndex = 0;

            boolean found = false;
            for (int i = 0; i < books.size(); i++) {
                if (books.get(i).getId() == request.getBookId()) {
                    bookIndex = i;
                    found = true;
                }
            }

            if (found == true) {
                BookOuterClass.Book.Builder book = books.get(bookIndex).toBuilder();
                Page page = Page.newBuilder()
                        .setBookId(request.getBookId())
                        .setId(request.getId())
                        .setPageNumber(request.getPageNumber())
                        .setContent(request.getContent()).build();

                book.addPages(page);

                books.set(bookIndex, book.build());

                responseObserver.onNext(Empty.newBuilder().build());
                responseObserver.onCompleted();
            }
        }
    }

    @Override
    public StreamObserver<BookOuterClass.Page> setPages(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<BookOuterClass.Page>() {
            List<BookOuterClass.Page> pages = new ArrayList<>();

            @Override
            public void onNext(BookOuterClass.Page page) {
                pages.add(page);
            }

            @Override
            public void onError(Throwable throwable) {
                /* EMPTY */
            }

            @Override
            public void onCompleted() {
                Integer pageId = pages.get(0).getBookId();
                Integer bookIndex = 0;

                boolean found = false;
                for (int i = 0; i < books.size(); i++) {
                    if (books.get(i).getId() == pageId) {
                        bookIndex = i;
                        found = true;
                    }
                }

                if (found == true) {
                    BookOuterClass.Book.Builder book = books.get(bookIndex).toBuilder();

                    for (int i = 0; i < pages.size(); i++) {
                        book.addPages(pages.get(i));
                    }

                    books.set(bookIndex, book.build());

                    responseObserver.onNext(null);
                    responseObserver.onCompleted();
                } else {
                    /* gRPC status codes */
                    Status status = Status.NOT_FOUND.withDescription("Book by this name not found");
                    responseObserver.onError(status.asRuntimeException());
                }
            }
        };
    }
}


