package test;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** 
 * Application WorldListing.
 * @author Fabrice Bouyé
 */
public final class WorldListing extends Application {

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // chargement de l'interface graphique.
        final ResourceBundle bundle = ResourceBundle.getBundle("test.WorldListing"); // NOI18N.
        final URL fxmlURL = getClass().getResource("WorldListing.fxml"); // NOI18N.
        final FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL, bundle);
        final Parent root = fxmlLoader.load();
        // Préparation de la scène.
        final Scene scene = new Scene(root);
        final URL cssURL = getClass().getResource("WorldListing.css"); // NOI18N.
        scene.getStylesheets().add(cssURL.toExternalForm());
        primaryStage.setTitle(bundle.getString("app.title")); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
