package controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import stage.StageManager;

import Theme.ThemeManager;
import Theme.Themes;

import model.Book;
import server.Tasks.BookTasks;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Controller {

    /* Every FXML must be named as it is in its view, in this case main.fxml */
    @FXML
    private AnchorPane rootWindow;

    @FXML
    private TableView<Book> booksTableView;

    @FXML
    private TableColumn<Book, String> titleColumn, authorColumn, genreColumn, publisherColumn;

    @FXML
    private AnchorPane pageControlContainer;

    @FXML
    private TextField searchBookField, currentPageNumberField;

    @FXML
    private Label totalPagesNumberLabel, exitButton, maximizeButton, minimizeButton;

    @FXML
    private WebView pageView;

    @FXML
    private ProgressIndicator booksListViewLoadingBar;

    @FXML
    private Menu menu;

    @FXML
    private MenuItem settings;

    BookTasks tasks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Controllers.INSTANCE.setMainController((Controller) this);

        tasks = new BookTasks(this);

        /* Bind data */
        bindData();

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

    /* FXML functions */
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

    @FXML
    private void closeButtonAction() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void minimizeButtonAction() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        if (stage.isIconified())
            stage.setIconified(false);
        else
            stage.setIconified(true);
    }

    @FXML
    private void maximizeButtonAction() {
        Stage stage = (Stage) maximizeButton.getScene().getWindow();

        if (stage.isFullScreen())
            stage.setFullScreen(false);
        else
            stage.setFullScreen(true);
    }

    @FXML
    private void onSettingsPressed(ActionEvent ae) {
        StageManager.makeSettingsWindow();
    }

    private void bindData() {
        this.titleColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        this.authorColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        this.genreColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("genre"));
        this.publisherColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("publisher"));
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

    public ProgressIndicator getBooksListViewLoadingBar() {
        return booksListViewLoadingBar;
    }

    public AnchorPane getRoot() {
        return rootWindow;
    }
}
