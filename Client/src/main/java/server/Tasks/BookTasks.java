package server.Tasks;

import com.victorlaerte.asynctask.AsyncTask;
import controllers.MainController;
import io.grpc.stub.StreamObserver;
import model.Book;
import proto.BookOuterClass;
import server.Connector;

public class BookTasks {
    private Connector connector;
    private MainController controller;

    private static int loadingBooksCounter = 0;

    public BookTasks(MainController controller) {
        /* Use default values: localhost:8999 */
        this.connector = new Connector();
        this.controller = controller;
    }

    public BookTasks(Connector connector, MainController controller) {
        this.connector = connector;
        this.controller = controller;
    }

    public void getBooks() {
        getBooks(0, 50);
    }

    public void getBooks(int firstBook, int lastBook) {
        /* https://github.com/victorlaerte/jfx-asynctask */
        new AsyncTask<>() {
            @Override
            public void onPreExecute() {
                /* EMPTY */
            }

            @Override
            public Object doInBackground(Object[] objects) {
                /* This call use field mask to receive only the title from each book */
                connector.getStub().getBooks(BookOuterClass.BooksRequest.newBuilder().setFirstBook(firstBook).setLastBook(lastBook).build(),
                        new StreamObserver<BookOuterClass.BookResponse>() {
                            @Override
                            /* Receive the stream of data from the server */
                            public void onNext(BookOuterClass.BookResponse curentBookResponse) {
                                BookOuterClass.Book currentBook = curentBookResponse.getBook();
                                publishProgress(new Book(currentBook), loadingBooksCounter);
                                loadingBooksCounter++;
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                System.out.println("Error : " + throwable.getMessage());
                            }

                            @Override
                            public void onCompleted() {
                                loadingBooksCounter = 0; /* Reset counter */
                                controller.getBooksListViewLoadingBar().setProgress(1); /* Set as completed */
                                controller.getBooksListViewLoadingBar().setVisible(false); /* Make the loading bar disappear */

                            }
                        });
                return null;
            }

            @Override
            public void onPostExecute(Object o) {
                /* EMPTY */
            }

            @Override
            public void progressCallback(Object[] objects) {
                Book book = (Book) objects[0];
                Double progressBarCounter = (int) objects[1] / (double) (lastBook - firstBook);
                controller.addTableViewBook(book);
                controller.getBooksListViewLoadingBar().setProgress(progressBarCounter);
            }
        }.execute();
    }

    public void getPage(int bookId, int pageNumber) {
        new AsyncTask<>() {
            String content;

            @Override
            public void onPreExecute() {
                /* EMPTY */
            }

            @Override
            public Object doInBackground(Object... objects) {
                connector.getStub().getPage(BookOuterClass.PageRequest.newBuilder().setBookId(bookId).setPageNumber(pageNumber).build(),
                        new StreamObserver<BookOuterClass.PageResponse>() {
                            @Override
                            public void onNext(BookOuterClass.PageResponse pageResponse) {
                                content = pageResponse.getPage().getContent();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                /* EMPTY */
                            }

                            @Override
                            public void onCompleted() {
                                controller.showPage(content);
                            }
                        });

                return null;
            }

            @Override
            public void onPostExecute(Object o) {
                /* EMPTY */
            }

            @Override
            public void progressCallback(Object... objects) {
                /* EMPTY */
            }
        }.execute();
    }
}

