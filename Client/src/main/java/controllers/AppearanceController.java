package controllers;

import Theme.ThemeManager;
import Theme.Themes;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AppearanceController implements Controller {

    @FXML
    private AnchorPane appearanceWindowRoot;

    @FXML
    private ComboBox themesComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Controllers.INSTANCE.setAppearanceController((Controller)this);

        /* Add all themes to the combo box */
        for (Themes theme : Themes.values()) {
            themesComboBox.getItems().add(theme.toString().toLowerCase().substring(0, 1).toUpperCase() + theme.toString().substring(1));
        }

        this.themesComboBox.setPromptText(ThemeManager.getCurrentTheme().toString());

        this.themesComboBox.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (newValue != null && ThemeManager.getCurrentTheme().toString() != newValue) {
                            ThemeManager.setTheme(newValue);
                        }
                    }
                });
    }

    public AnchorPane getRoot() {
        return appearanceWindowRoot;
    }
}
