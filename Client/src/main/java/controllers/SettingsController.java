package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class SettingsController implements Controller {
    @FXML
    private ListView settingsList;

    @FXML
    private AnchorPane rootWindow;

    @FXML
    private AnchorPane rightPane;

    @FXML
    private ListView settingsListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Controllers.INSTANCE.setSettingsController((Controller) this);
        settingsListView.getItems().add("Appearance");

        this.settingsListView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        AnchorPane pane = null;
                        try {
                            pane = FXMLLoader.load(getClass().getResource("../gui/views/appearance.fxml"));
                            rightPane.getChildren().setAll(pane);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                });
    }

    @FXML
    private Label exitButton, minimizeButton, maximizeButton;

    @Override
    public AnchorPane getRoot() {
        return rootWindow;
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

}

