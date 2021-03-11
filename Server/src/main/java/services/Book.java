package services;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import proto.BookOuterClass;
import proto.BookOuterClass.*;
import proto.BookServiceGrpc;

import java.util.ArrayList;
import java.util.List;

public class Book extends BookServiceGrpc.BookServiceImplBase {
    /* Simulate a database at runtime */
    List<BookOuterClass.Book> books = new ArrayList<>();

    /***
     * @param request is the request got from the client
     * @param responseObserver is a response observer, which is a special interface for the server to call
     *                        with its response
     */
    @Override
    public void getBook(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        System.out.println("Get book accessed");

        BookResponse.Builder response = BookResponse.newBuilder();
        BookOuterClass.Book.Builder book = null;

        /* Search a book in our list by using lambda expresion, if there is no book null will be return */
        BookOuterClass.Book search = books.stream().filter(element -> element.getTitle().equals(request.getName()))
                .findFirst().orElse(null);

        /* Error handling */
        if (search == null) {
            /* gRPC status codes */
            Status status = Status.NOT_FOUND.withDescription("Book by this name not found");
            responseObserver.onError(status.asRuntimeException());
        } else
            book = search.toBuilder();
        response.setBook(book);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void setBook(BookOuterClass.Book request, StreamObserver<Empty> responseObserver) {
        System.out.println("Set book accessed");
        books.add(request);

        System.out.println(request);
        System.out.println("all array: " + this.books);

        Empty.Builder response = Empty.newBuilder();
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getBooks(Empty request, StreamObserver<BookOuterClass.Book> responseObserver) {
        if (this.books.size() != 0) {
            for (BookOuterClass.Book book : this.books) {
                responseObserver.onNext(book);
            }
            responseObserver.onCompleted();
        } else {
            /* gRPC status codes */
            Status status = Status.NOT_FOUND.withDescription("No books");
            responseObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getPage(PageRequest request, StreamObserver<PageResponse> responseObserver) {
        /* Search a book in our list by using lambda expresion, if there is no book null will be return */
        BookOuterClass.Book.Builder bookSearch = books.stream().filter(element -> element.getId() == (request.getBookId()))
                .findFirst().orElse(null).toBuilder();

        /* Error handling */
        if (bookSearch == null) {
            /* gRPC status codes */
            Status status = Status.NOT_FOUND.withDescription("Book by this name not found");
            responseObserver.onError(status.asRuntimeException());
        } else {
            BookOuterClass.Book.Page pageSearch = bookSearch.getPagesList().stream().filter(element -> element.getPageNumber() == request.getPageNumber())
                    .findFirst().orElse(null);

            /* Error handling */
            if (pageSearch == null) {
                /* gRPC status codes */
                Status status = Status.NOT_FOUND.withDescription("Book do not have this page number");
                responseObserver.onError(status.asRuntimeException());
            } else {
                responseObserver.onNext(PageResponse.newBuilder().setPage(pageSearch).build());
                responseObserver.onCompleted();
            }
        }
    }

    @Override
    public void getPages(PageRequest request, StreamObserver<PageResponse> responseObserver) {
        /* Search a book in our list by using lambda expresion, if there is no book null will be return */
        BookOuterClass.Book search = books.stream().filter(element -> element.getId() == (request.getBookId()))
                .findFirst().orElse(null);

        /* Error handling */
        if (search == null) {
            /* gRPC status codes */
            Status status = Status.NOT_FOUND.withDescription("Book by this name not found");
            responseObserver.onError(status.asRuntimeException());
        } else {
            /* Iterate the pages of a book and send them individually */
            for (BookOuterClass.Book.Page page : search.getPagesList()) {
                responseObserver.onNext(BookOuterClass.PageResponse.newBuilder()
                        .setPage(page)
                        .build());
            }
        }

        /* We use the response observer's onCompleted method to specify that we've finished dealing with the RPC */
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<BookOuterClass.Book.Page> setPages(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<BookOuterClass.Book.Page>() {
            List<BookOuterClass.Book.Page> pages = new ArrayList<>();

            @Override
            public void onNext(BookOuterClass.Book.Page page) {
                System.out.println("Enter " + page.getBookId());
                pages.add(page);
            }

            @Override
            public void onError(Throwable throwable) {
                /* EMPTY */
            }

            @Override
            public void onCompleted() {
                System.out.println(this.pages);
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
                        System.out.println("PAGE AT " + i + pages.get(i));
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
