package Theme;

import controllers.Controller;
import controllers.Controllers;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public enum ThemeManager {
    INSTANCE;

    private static Themes currentTheme = Themes.DRACULA;

    public static ThemeManager getInstance() {
        return INSTANCE;
    }

    public static Themes getCurrentTheme() {
        return currentTheme;
    }

    public static void getAllThemes() {

    }

    public static void saveSettings(File file) {

    }

    public static void loadSettings(File file) {

    }

    /* Below functions applies to all windows */
    public static void setTheme(Themes theme) {
        if (ThemeManager.currentTheme != null && ThemeManager.currentTheme != theme) {
            removeAllThemes();
            addTheme(theme);
            ThemeManager.currentTheme = theme;
        }
    }

    public static void setTheme(String theme) {
        Themes currentTheme = Themes.valueOf(theme.toUpperCase());
        if (ThemeManager.currentTheme != null && ThemeManager.currentTheme != currentTheme) {
            removeAllThemes();
            addTheme(currentTheme);
            ThemeManager.currentTheme = currentTheme;
        }
    }

    private static void addTheme(Themes theme) {
        try {
            for (Controller controller : Controllers.INSTANCE.getAllControllers()) {
                String currentTheme = theme.toString().toLowerCase();
                URL resource = ThemeManager.class.getResource("../gui/theme/" + currentTheme + "/main.css");

                controller.getRoot().getStylesheets().add(resource.toString());
            }
        } catch (
                Exception e) {
            System.out.println(e);
        }
    }

    /* Remove and add themes in theme vector */
    private static void removeAllThemes() {
        for (Controller controller : Controllers.INSTANCE.getAllControllers()) {
            for (Themes theme : Themes.values()) {
                String currentTheme = theme.toString().toLowerCase();
                URL resource = ThemeManager.class.getResource("../gui/theme/" + currentTheme + "/main.css");
                controller.getRoot().getStylesheets().remove(resource.toString());
            }
        }
    }

    /* Below functions applies to specific windows */
    public static void setTheme(Themes theme, Stage stage) {
        removeAllThemes(stage);
        addTheme(theme, stage);
    }

    public static void addTheme(Themes theme, Stage stage) {
        String currentTheme = theme.toString().toLowerCase();
        URL resource = ThemeManager.class.getResource("../gui/theme/" + currentTheme + "/main.css");
        stage.getScene().getRoot().getStylesheets().add(resource.toString());
    }

    public static void removeTheme(Themes theme, Stage stage) {
        String currentTheme = theme.toString().toLowerCase();
        URL resource = ThemeManager.class.getResource("../gui/theme/" + currentTheme + "/main.css");
        stage.getScene().getRoot().getStylesheets().remove(resource.toString());
    }

    private static void removeAllThemes(Stage stage) {
        for (Themes theme : Themes.values()) {
            String currentTheme = theme.toString().toLowerCase();
            URL resource = ThemeManager.class.getResource("../gui/theme/" + currentTheme + "/main.css");
            stage.getScene().getRoot().getStylesheets().remove(resource.toString());
        }
    }
}
