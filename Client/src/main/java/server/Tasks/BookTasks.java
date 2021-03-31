package server.Tasks;

import com.google.protobuf.FieldMask;
import com.victorlaerte.asynctask.AsyncTask;
import controllers.MainController;
import io.grpc.stub.StreamObserver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import proto.BookOuterClass;
import proto.BookServiceGrpc;
import server.Connector;

import java.util.ArrayList;
import java.util.Arrays;

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
        /* https://github.com/victorlaerte/jfx-asynctask */
        new AsyncTask<>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public Object doInBackground(Object[] objects) {
                /* This call use field mask to receive only the title from each book */
                connector.getStub().getBooks(BookOuterClass.BooksRequest.newBuilder().setFieldMask(FieldMask.newBuilder().addPaths("title").build()).build(), new StreamObserver<BookOuterClass.BookResponse>() {
                    @Override
                    /* Receive the stream of data from the server */
                    public void onNext(BookOuterClass.BookResponse curentBookResponse) {
                        System.out.println("Current book response : " + curentBookResponse);
                        BookOuterClass.Book currentBook = curentBookResponse.getBook();

                        System.out.println("Current Book " + currentBook.getTitle().toString());
                        publishProgress(currentBook.getTitle().toString(), loadingBooksCounter);
                        loadingBooksCounter++;
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error : " + throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        /* Make the loading bar disappear */
                        controller.getBooksListViewLoadingBar().setVisible(false);
                    }
                });
                return null;
            }

            @Override
            public void onPostExecute(Object o) {
            }

            @Override
            public void progressCallback(Object[] objects) {
                System.out.println("loading in list");
                String title = (String) objects[0];
                Integer progressBarCounter = (Integer) objects[1];
                controller.addListViewBook(title);
                controller.getBooksListViewLoadingBar().setProgress(progressBarCounter);
            }
        }.execute();
    }
}

