package controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.database.Book;
import server.Tasks.BookTasks;

import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    /* Every FXML must be named as it is in its view, in this case main.fxml */
    @FXML
    private TableView<Book> booksTableView;

    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> genreColumn;
    @FXML
    private TableColumn<Book, String> publisherColumn;

    @FXML
    private AnchorPane pageControlContainer;

    @FXML
    private TextField searchBookField;

    @FXML
    private TextField currentPageNumberField;

    @FXML
    private Label totalPagesNumberLabel;

    @FXML
    private WebView pageView;

    @FXML
    private ProgressIndicator booksListViewLoadingBar;

    BookTasks tasks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Create Table columns */
        /* Manual way to create columns in our table - if we do not define TableColumn in .fxml*/
        /*
        TableColumn titleColumn = new TableColumn("Title");
        TableColumn authorColumn = new TableColumn("Author");
        TableColumn genreColumn = new TableColumn("Genre");
        TableColumn publisherColumn = new TableColumn("Publisher");

        booksTableView.getColumns().addAll(titleColumn, authorColumn, genreColumn, publisherColumn);
        */

        /* Initially get all books - asynchronously */
        tasks = new BookTasks(this);

        /* Bind data */
        this.titleColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        this.authorColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        this.genreColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("genre"));
        this.publisherColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("publisher"));

        /* Load the book list */
        tasks.getBooks();

        /* Wrap the ObservableList in a FilteredList */
        FilteredList<Book> filteredData = new FilteredList<>(this.booksTableView.getItems(), p -> true);

        /* Set the filter Predicate whenever the filter changes */
        searchBookField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                /* If filter text is empty, display all persons */
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                /* Compare title of every book with filter text */
                String lowerCaseFilter = newValue.toLowerCase();

                if (book.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; /* Does not match */
            });

            this.booksTableView.setItems(filteredData);
        });

        /* When a list item is pressed a page will be request */
        this.booksTableView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Book>() {
                    @Override
                    public void changed(ObservableValue<? extends Book> observable, Book oldValue, Book newValue) {
                        tasks.getPage(newValue.getId(), 1);
                        currentPageNumberField.setText("1");
                        pageControlContainer.setVisible(true);
                        totalPagesNumberLabel.setText(newValue.getTotalPages().toString());
                    }
                }
        );

        /* Load a welcome html page in the right panel */
        WebEngine webEngine = pageView.getEngine();
        URL welcomePageURL = getClass().getResource("/welcome_page.html");
        webEngine.load(welcomePageURL.toString());
    }

    @FXML
    public void onPreviousButtonPressed() {
        Integer selectedBookId = booksTableView.getSelectionModel().selectedItemProperty().get().getId();
        Integer currentPageNumber = Integer.parseInt(currentPageNumberField.getText());

        if (currentPageNumber != 1 && selectedBookId != null) {
            currentPageNumber--;

            if (selectedBookId != null) {
                tasks.getPage(selectedBookId, currentPageNumber);
            }

            currentPageNumberField.setText(currentPageNumber.toString());
        }
    }

    @FXML
    public void onNextButtonPressed() {
        Integer selectedBookId = booksTableView.getSelectionModel().selectedItemProperty().get().getId();
        Integer currentPageNumber = Integer.parseInt(currentPageNumberField.getText());
        Integer totalPagesNumber = Integer.parseInt(totalPagesNumberLabel.getText());

        if (currentPageNumber != totalPagesNumber) {
            currentPageNumber++;
            /* Request */
            if (selectedBookId != null) {
                tasks.getPage(selectedBookId, currentPageNumber);
            }

            currentPageNumberField.setText(currentPageNumber.toString());
        }
    }

    @FXML
    private void onEnter(ActionEvent ae) {
        Integer selectedBookId = booksTableView.getSelectionModel().selectedItemProperty().get().getId();
        Integer currentPageNumber = Integer.parseInt(currentPageNumberField.getText());
        Integer totalPagesNumber = Integer.parseInt(totalPagesNumberLabel.getText());

        if (currentPageNumber < totalPagesNumber && currentPageNumber > 0)
            tasks.getPage(selectedBookId, currentPageNumber);
        else {
            currentPageNumberField.setText("1");
            tasks.getPage(selectedBookId, 1);
        }
    }

    public void showPage(String content) {
        /* To make sure the page will be load on a FX thread */
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pageView.getEngine().loadContent(content);
            }
        });
    }

    public void addTableViewBook(Book book) {
        this.booksTableView.getItems().add(book);
    }

    /* Getters and setter */
    public TableView<Book> getBooksTableView() {
        return booksTableView;
    }

    public void setBooksTableView(ObservableList<Book> booksTableView) {
        this.booksTableView.setItems(booksTableView);
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

    public void setBooksTableViewLoadingBar(ProgressIndicator booksListTableLoadingBar) {
        this.booksListViewLoadingBar = booksListViewLoadingBar;
    }
}
