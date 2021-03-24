package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    /* Every FXML must be named as it is in its view, in this case main.fxml */
    @FXML private ListView<String> booksListView;
    @FXML private WebView pageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Load some mock books in booksView, in the next commits this will be changed with our book model */
        ObservableList<String> booksList = FXCollections.observableArrayList(Arrays.asList(
                "Mock book 1",
                "Mock book 2",
                "Mock book 3",
                "Mock book 4"
        ));

        /* Load a local html page in the right panel */
        WebEngine webEngine = pageView.getEngine();

        /* Engine called */
        URL source = getClass().getResource("/mock_book.html");
        webEngine.load(source.toString());
        booksListView.setItems(booksList);
        booksListView.refresh();
    }

}
