package de.ianboy10.firmenregister.managers;

import de.ianboy10.firmenregister.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserManager {

    // Map zur Zuordnung von UUIDs zu Firmen-IDs
    public static final Map<UUID, String> companyPlayers = new HashMap<>();

    // Initialisierungsmethode, um Daten aus der Datenbank zu laden
    public static void initialize() {
        // Verbindung zur Datenbank herstellen und Abfrage ausführen
        try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                .prepareStatement("SELECT * FROM company_users")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // UUID und Firmen-ID aus dem ResultSet auslesen
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String companyId = resultSet.getString("firmaId");
                // Werte in die Map einfügen
                companyPlayers.put(uuid, companyId);
            }
        } catch (SQLException e) {
            // Fehlerausgabe für Debugging
            e.printStackTrace();
        }
    }

    // Überprüfen, ob ein Spieler in einer Firma ist
    public static boolean inCompany(UUID uuid) {
        return companyPlayers.containsKey(uuid);
    }

    // Die Firmen-ID eines Spielers abrufen
    public static String getCompany(UUID uuid) {
        return companyPlayers.get(uuid);
    }

}