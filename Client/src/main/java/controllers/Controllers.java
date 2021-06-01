package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Controllers {
    INSTANCE;

    private Controller mainController, settingsController, appearanceController;

    public List<Controller> getAllControllers() {
        return new ArrayList<>(Arrays.asList(mainController, settingsController, appearanceController));
    }

    /*  Main Controller */
    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    /* Settings Controller */
    public void setSettingsController(Controller settingsController) {
        this.settingsController = settingsController;
    }

    /* Appearance Controller */
    public void setAppearanceController(Controller appearanceController) {
        this.appearanceController = appearanceController;
    }
}
