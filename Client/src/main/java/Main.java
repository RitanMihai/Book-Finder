import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import proto.BookOuterClass;
import proto.BookServiceGrpc;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8999).usePlaintext().build();
        /* Synchronous stub */
        //BookServiceGrpc.BookServiceBlockingStub bookStub = BookServiceGrpc.newBlockingStub(channel);

        /* To be capable to stream with out client we must use a Asynchronous stub */
        BookServiceGrpc.BookServiceStub bookStub = BookServiceGrpc.newStub(channel);

        System.out.println("MENU");
        System.out.println("1. Set a book");
        System.out.println("2. Get a book");
        System.out.println("3. Get all books");
        System.out.println("4. Get all pages of a book");
        System.out.println("5. Get a page from a book");
        System.out.println("6. QUIT");

        boolean isConnected = true;
        while (isConnected) {
            Scanner input = new Scanner(System.in);
            System.out.println("Choose:");
            int option = input.nextInt();

            switch (option) {
                case 1: {
                    Scanner read = new Scanner(System.in).useDelimiter("\n"); /* Read until break line*/
                    System.out.println("The title of the book is: ");
                    String title = read.next();

                    System.out.println("Nr. of pages: ");
                    Integer totalPages = read.nextInt();

                    System.out.println("Publisher: ");
                    String publisher = read.next();

                    System.out.println("Genre: ");
                    /* Print all genre that are defined in book.proto */
                    Arrays.stream(BookOuterClass.Book.Genre.values()).forEach(System.out::println);
                    String genre = read.next();

                    Integer bookId = new Random().nextInt(/* Maximum value */100000 - 1 /* Minimum value + 1*/);

                    bookStub.setBook(
                            BookOuterClass.Book.newBuilder()
                                    .setTitle(title)
                                    .setGenre(BookOuterClass.Book.Genre.valueOf(genre))
                                    .setId(bookId)
                                    .setTotalPages(totalPages).build(),
                            /* On Synchronous stub you do not need the second parameter, do not forget to erase the code
                             * below if you switch to Synchronous stub */
                            new StreamObserver<BookOuterClass.Empty>() {
                                @Override
                                public void onNext(BookOuterClass.Empty empty) {
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    System.out.println("Error : " + throwable.getMessage());
                                }

                                @Override
                                public void onCompleted() {

                                }
                            }

                    );

                    System.out.println("Do you want to add the pages? (Y/N): ");
                    String isAdding = read.next();

                    /* Client stream to the server */
                    if (isAdding.equals("Yes") || isAdding.equals("yes") || isAdding.equals("Y") || isAdding.equals("y")) {
                        StreamObserver<BookOuterClass.Book.Page> pageStreamObserver = bookStub.setPages(new StreamObserver<BookOuterClass.Empty>() {
                            @Override
                            public void onNext(BookOuterClass.Empty empty) {

                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }

                            @Override
                            public void onCompleted() {

                            }
                        });

                        for (int i = 0; i < totalPages; i++) {
                            /* Declare a page that will be send to the server */
                            BookOuterClass.Book.Page.Builder currentPage = BookOuterClass.Book.Page.newBuilder();
                            currentPage.setBookId(bookId);
                            currentPage.setPageNumber(i);

                            System.out.println("Content for page " + i + " : ");
                            String content = read.next();

                            currentPage.setContent(content);
                            pageStreamObserver.onNext(currentPage.build());
                        }
                        pageStreamObserver.onCompleted();
                    }

                    break;
                }
                case 2: {
                    Scanner read = new Scanner(System.in).useDelimiter("\n");
                    System.out.println("Name of the book: ");

                    String bookName = read.next();

                    bookStub.getBook(BookOuterClass.BookRequest.newBuilder().setName(bookName).build(), new StreamObserver<BookOuterClass.BookResponse>() {
                        @Override
                        public void onNext(BookOuterClass.BookResponse bookResponse) {
                            System.out.println(bookResponse);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            System.out.println("Error : " + throwable.getMessage());
                        }

                        @Override
                        public void onCompleted() {

                        }
                    });

                    break;
                }
                case 3: {
                    /* Server stream to the client */
                    /* Version for the Asynchronous Stub */
                    bookStub.getBooks(BookOuterClass.Empty.newBuilder().build(), new StreamObserver<BookOuterClass.Book>() {
                        @Override
                        public void onNext(BookOuterClass.Book book) {
                            System.out.println("Book: " + book.getTitle() + " ( id: " + book.getId() + ", genre" + book.getGenre() + ")");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            System.out.println("Error : " + throwable.getMessage());
                        }

                        @Override
                        public void onCompleted() {

                        }
                    });

                    /* Version for the Synchronous BlockingStub */
                    /*
                    Iterator<BookOuterClass.Book> books = bookStub.getBooks(BookOuterClass.Empty.newBuilder().build());
                    try {
                        System.out.println("All books: ");
                        books.forEachRemaining(bookResponse -> {
                            System.out.println(bookResponse.getId() + " " + bookResponse.getTitle() + " " + bookResponse.getGenre());
                        });
                    } catch (StatusRuntimeException e) {
                        System.out.println("Error : " + e.getMessage());
                    }
                    */
                    break;
                }
                case 4: {
                    Scanner read = new Scanner(System.in);
                    System.out.println("Book id: ");

                    Integer bookId = read.nextInt();
                    bookStub.getPages(BookOuterClass.PageRequest.newBuilder().setBookId(bookId).build(), new StreamObserver<BookOuterClass.PageResponse>() {
                        @Override
                        public void onNext(BookOuterClass.PageResponse pageResponse) {
                            System.out.println(pageResponse);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            System.out.println("Error : " + throwable.getMessage());
                        }

                        @Override
                        public void onCompleted() {

                        }
                    });
                    break;
                }
                case 5: {
                    Scanner read = new Scanner(System.in);
                    System.out.println("Book id: ");
                    Integer bookId = read.nextInt();

                    System.out.println("Page number: ");
                    Integer pageNumber = read.nextInt();

                    bookStub.getPage(BookOuterClass.PageRequest.newBuilder()
                            .setBookId(bookId)
                            .setPageNumber(pageNumber)
                            .build(), new StreamObserver<BookOuterClass.PageResponse>() {
                        @Override
                        public void onNext(BookOuterClass.PageResponse pageResponse) {
                            System.out.println(pageResponse);
                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onCompleted() {

                        }
                    });
                    break;
                }
                case 6: {
                    isConnected = false;
                    break;
                }
                default:
                    System.out.println("Unknown command, insert a valid command!");
            }
        }
        channel.shutdown();
    }
}
