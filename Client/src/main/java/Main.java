import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            /* load main window */
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui/views/main.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Scene newScene = new Scene(root);


            /* set the title and icon of the main window */
            stage.setTitle("Book Finder");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("gui/icons/logo/logo.png")));
            stage.setScene(newScene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

}