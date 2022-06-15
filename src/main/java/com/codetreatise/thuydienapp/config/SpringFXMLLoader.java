package com.codetreatise.thuydienapp.config;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

/**
 * Will load the FXML hierarchy as specified in the load method and register
 * Spring as the FXML Controller Factory. Allows Spring and Java FX to coexist
 * once the Spring Application context has been bootstrapped.
 */
@Getter
public class SpringFXMLLoader {


    private static SpringFXMLLoader instance;

    private  ResourceBundle resourceBundle;

    private SpringFXMLLoader() {
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public static SpringFXMLLoader getInstance() {
        if(instance == null) {
            return  new SpringFXMLLoader();
        }
        return  instance;
    }

    public Parent load(String fxmlPath) throws IOException {      
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resourceBundle);
        loader.setLocation(getClass().getResource(fxmlPath));
        return loader.load();
    }
}
