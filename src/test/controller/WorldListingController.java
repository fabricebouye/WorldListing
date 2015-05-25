package test.controller;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import test.query.WorldsQuery;
import test.scene.renderer.WorldListCell;

/**
 * Contrôleur pour l'IU WorldListing.
 * @author Fabrice Bouyé
 */
public final class WorldListingController implements Initializable {

    @FXML
    private ListView<Integer> worldListView;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private HBox regionBox;
    @FXML
    private ToggleGroup regionSelectionGroup;
    @FXML
    private ToggleButton usToggle;
    @FXML
    private ToggleButton euToggle;
    @FXML
    private HBox languageBox;
    @FXML
    private ToggleGroup languageSelectionGroup;
    @FXML
    private ToggleButton enToggle;
    @FXML
    private ToggleButton frToggle;
    @FXML
    private ToggleButton deToggle;
    @FXML
    private ToggleButton esToggle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        worldListView.setVisible(false);
        //
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(0);
        //
        usToggle.setUserData("us"); // NOI18N.
        euToggle.setUserData("eu"); // NOI18N.        
        regionSelectionGroup.selectedToggleProperty().addListener(regionInvalidationListener);
        //
        enToggle.setUserData("en"); // NOI18N.
        frToggle.setUserData("fr"); // NOI18N.
        deToggle.setUserData("de"); // NOI18N.
        esToggle.setUserData("es"); // NOI18N.
        languageSelectionGroup.selectedToggleProperty().addListener(languageInvalidationListener);
        //
        final String region = (String) regionSelectionGroup.getSelectedToggle().getUserData();
        final String language = (String) languageSelectionGroup.getSelectedToggle().getUserData();
        loadWorldList(region, language);
    }

    private Map<Integer, String> worlds;
    private FilteredList<Integer> filteredWorldIds;

    /**
     * Réaction au changement de language.
     */
    private final InvalidationListener languageInvalidationListener = observable -> {
        final Toggle selectedRegionToggle = regionSelectionGroup.getSelectedToggle();
        final String regionCode = (selectedRegionToggle == null) ? null : (String) selectedRegionToggle.getUserData();
        final Toggle selectedLanguageToggle = languageSelectionGroup.getSelectedToggle();
        final String languageCode = (selectedLanguageToggle == null) ? null : (String) selectedLanguageToggle.getUserData();
        // On réinitialise toute la liste des mondes.
        if (languageCode != null) {
            loadWorldList(regionCode, languageCode);
        }
    };

    /**
     * Réction au changmement de région.
     */
    private final InvalidationListener regionInvalidationListener = observable -> {
        if (filteredWorldIds != null) {
            final Toggle selectedRegionToggle = regionSelectionGroup.getSelectedToggle();
            final String regionCode = (selectedRegionToggle == null) ? null : (String) selectedRegionToggle.getUserData();
            // On se contente de filtrer la liste existante.
            filterWorldList(regionCode);
        }
    };

    /**
     * Chargement de la liste des mondes de manière asynchrone.
     * @param regionCode Le code de la région (pour le filtrage après la
     * requête).
     * @param languageCode Le code de la langue (pour la requête).
     */
    private void loadWorldList(final String regionCode, final String languageCode) {
        regionBox.setDisable(true);
        languageBox.setDisable(true);
        worldListView.setVisible(false);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setVisible(true);
        final Service<Map<Integer, String>> query = new Service<Map<Integer, String>>() {

            @Override
            protected Task<Map<Integer, String>> createTask() {
                return new Task<Map<Integer, String>>() {

                    @Override
                    protected Map<Integer, String> call() throws Exception {
                        return WorldsQuery.worldNames(languageCode);
                    }
                };
            }
        };
        query.setOnSucceeded(workerStateEvent -> {
            worlds = query.getValue();
            final ObservableList<Integer> worldIds = FXCollections.observableArrayList(worlds.keySet());
            final ObservableList<Integer> sortedWorldIds = new SortedList<>(worldIds, Integer::compareTo);
            filteredWorldIds = new FilteredList<>(sortedWorldIds);
            filterWorldList(regionCode);
            worldListView.setItems(filteredWorldIds);
            worldListView.setCellFactory(listView -> new WorldListCell(worlds));
            progressIndicator.setVisible(false);
            progressIndicator.setProgress(0);
            worldListView.setVisible(true);
            regionBox.setDisable(false);
            languageBox.setDisable(false);
        });
        query.setOnCancelled(workerStateEvent -> {
            regionBox.setDisable(false);
            languageBox.setDisable(false);
        });
        query.setOnFailed(workerStateEvent -> {
            regionBox.setDisable(false);
            languageBox.setDisable(false);
            System.out.println(query.getException());
        });
        query.start();
    }

    /**
     * Filtre par défaut : affiche tout.
     */
    private final Predicate<Integer> acceptAllPredicate = worldId -> true;
    /**
     * Filtre qui n'affiche que les mondes US.
     */
    private final Predicate<Integer> usOnlyPredicate = worldId -> worldId < 2000;
    /**
     * Filtre qui n'affiche que les mondes EU.
     */
    private final Predicate<Integer> euOnlyPredicate = worldId -> worldId >= 2000;

    /**
     * Filtre de la liste des monde.
     * @param regionCode Le code de la région. Si {@code null}, tous les mondes
     * sont affichés.
     */
    private void filterWorldList(final String regionCode) {
        Predicate<Integer> predicate = acceptAllPredicate;
        if (regionCode != null) {
            switch (regionCode) {
                case "us": { // NOI18N.
                    predicate = usOnlyPredicate;
                }
                break;
                case "eu": { // NOI18N.
                    predicate = euOnlyPredicate;
                }
                break;
                default:
            }
        }
        filteredWorldIds.setPredicate(predicate);
    }
}
