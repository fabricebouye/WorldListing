package test.scene.renderer;

import java.util.Map;
import javafx.scene.control.ListCell;

/**
 * Cellule permettant d'afficher le nom d'un monde à partir de son identifiant.
 * @author Fabrice Bouyé
 */
public final class WorldListCell extends ListCell<Integer> {

    /**
     * Dictionnaire des noms des mondes.
     */
    private final Map<Integer, String> worldNamesMap;

    /**
     * Crée une nouvelle instance.
     * @param worldNamesMap Dictionnaire des noms des mondes.
     */
    public WorldListCell(final Map<Integer, String> worldNamesMap) {
        this.worldNamesMap = worldNamesMap;
    }

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setText(worldNamesMap.get(item));
        }
    }
}
