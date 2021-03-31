package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebView;
import server.Tasks.BookTasks;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    /* Every FXML must be named as it is in its view, in this case main.fxml */
    @FXML
    private ListView<String> booksListView;
    @FXML
    private WebView pageView;

    @FXML
    private ProgressIndicator booksListViewLoadingBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        BookTasks tasks = new BookTasks(this);
        tasks.getBooks();
    }

    public ListView<String> getBooksListView() {
        return booksListView;
    }

    public void setBooksListView(ObservableList<String> booksListView) {
        this.booksListView.setItems(booksListView);
    }

    public WebView getPageView() {
        return pageView;
    }

    public void setPageView(WebView pageView) {
        this.pageView = pageView;
    }

    public ProgressIndicator getBooksListViewLoadingBar() {
        return booksListViewLoadingBar;
    }

    public void setBooksListViewLoadingBar(ProgressIndicator booksListViewLoadingBar) {
        this.booksListViewLoadingBar = booksListViewLoadingBar;
    }

    public void addListViewBook(String title){
        this.booksListView.getItems().add(title);
    }
}
