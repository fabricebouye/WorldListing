package test.query;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.util.Pair;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;

import static test.query.QueryUtils.*;

/**
 * Permet de faire des requêtes sur l'endpoint Worlds.
 * @author Fabrice Bouyé
 */
public enum WorldsQuery {

    INSTANCE;

    /**
     * L'URL de base de cet endpoint.
     */
    private static final String basecode = "https://api.guildwars2.com/v2/worlds"; // NOI18N.

    /*
     * Récupère la liste de tous les identifiants des mondes.
     * @return Une instance de {@code List<Integer>}.
     * @throws IOException En cas d'erreur.
     */
    public static List<Integer> list() throws IOException {
        final JsonArray array = queryArray(basecode);
        final List<Integer> result = array.getValuesAs(JsonNumber.class)
                .stream()
                .map(value -> value.intValue())
                .collect(Collectors.toList());
        return Collections.unmodifiableList(result);
    }

    /**
     * Récupère le nom d'un seul serveur.
     * @param languageCode Le code de la langue.
     * @param id L'identifiant du monde.
     * @return Une instance de {@code Pair<Integer, String>} :
     * <ul>
     * <li>Clé : identifiant du monde.</li>
     * <li>Valeur : nom localisé du monde.</li>
     * </ul>
     * @throws IOException En cas d'erreur.
     */
    public static Pair<Integer, String> worldName(final String languageCode, final int id) throws IOException {
        final JsonObject value = queryObject(String.format("%s?id=%d&lang=%s", basecode, id, languageCode)); // NOI18N.
        final String name = value.getString("name"); // NOI18N.
        final Pair<Integer, String> result = new Pair<>(id, name);
        return result;
    }

    /**
     * Récupère le nom d'un seul serveur.
     * @param languageCode Le code de la langue.
     * @param ids Les identifiants des mondes. Si aucun indentifiant est fourni,
     * les noms de tous les mondes sont retournés.
     * @return Une instance de {@code Map<Integer, String>} :
     * <ul>
     * <li>Clé : identifiant du monde.</li>
     * <li>Valeur : nom localisé du monde.</li>
     * </ul>
     * @throws IOException En cas d'erreur.
     */
    public static Map<Integer, String> worldNames(final String languageCode, final int... ids) throws IOException {
        final String idsCode = idsToString(ids);
        final JsonArray array = queryArray(String.format("%s?ids=%s&lang=%s", basecode, idsCode, languageCode)); // NOI18N.
        final Map<Integer, String> result = new HashMap();
        array.getValuesAs(JsonObject.class)
                .stream()
                .forEach((value) -> {
                    final JsonNumber id = value.getJsonNumber("id"); // NOI18N.
                    final JsonString name = value.getJsonString("name"); // NOI18N.
                    result.put(id.intValue(), name.getString());
                });
        return result;
    }
}
