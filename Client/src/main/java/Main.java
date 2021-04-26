import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    private double xOffset;
    private double yOffset;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            /* load main window */
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui/views/main.fxml"));
            Parent root = (Parent) fxmlLoader.load();

            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
                event.consume();
            });

            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
                event.consume();
            });

            Scene newScene = new Scene(root);
            //newScene.getRoot().setEffect(new DropShadow(10, Color.rgb(100, 100, 100)));
            newScene.setFill(Color.TRANSPARENT);

            /* set the title and icon of the main window */
            stage.setTitle("Book Finder");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("gui/icons/logo/logo.png")));

            stage.setScene(newScene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

}