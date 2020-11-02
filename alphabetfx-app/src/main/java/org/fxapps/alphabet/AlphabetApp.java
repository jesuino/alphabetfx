package org.fxapps.alphabet;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.scene.Scene;

public class AlphabetApp extends MobileApplication {

    AlphabetFX alphabetFX = new AlphabetFX();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void init() {
        addViewFactory(HOME_VIEW, () -> {

            View view = new View(alphabetFX.buildApp()) {
                @Override
                protected void updateAppBar(AppBar appBar) {
                    appBar.setTitleText("AlphabetFX App");
                }
            };

            return view;
        });
    }

    @Override
    public void postInit(Scene scene) {
        getAppBar().setVisible(false);
    }

}