package stage;

import Theme.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class StageManager {
    public static void makeMainWindow() {
        try {
            /* load main window */
            FXMLLoader fxmlLoader = new FXMLLoader(StageManager.class.getResource("../gui/views/main.fxml"));
            Parent root = (Parent) fxmlLoader.load();

            /* No default white background */
            Scene newScene = new Scene((Parent) root, Color.TRANSPARENT);
            Stage stage = new Stage();

            /* No header */
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(newScene);

            /* set the title and icon of the main window */
            stage.setTitle("Book Finder");
            stage.getIcons().add(new Image(StageManager.class.getResourceAsStream("../gui/icons/logo/logo.png")));

            ResizeHelper.addResizeListener(stage);

            /* Assign current theme */
            ThemeManager.setTheme(ThemeManager.getCurrentTheme());

            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void makeSettingsWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StageManager.class.getResource("../gui/views/settings.fxml"));
            Parent root = (Parent) fxmlLoader.load();

            /* No default white background */
            Scene scene = new Scene(root, Color.TRANSPARENT);
            Stage stage = new Stage();

            /* No header */
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            /* set the title and icon of the main window */
            stage.setTitle("Book Finder");
            stage.getIcons().add(new Image(StageManager.class.getResourceAsStream("../gui/icons/logo/logo.png")));

            ResizeHelper.addResizeListener(stage);

            /* Assign current theme */
            ThemeManager.setTheme(ThemeManager.getCurrentTheme(), stage);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
