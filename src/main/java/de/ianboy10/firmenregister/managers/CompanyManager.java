package de.ianboy10.firmenregister.managers;

import de.ianboy10.firmenregister.Main;

import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static de.ianboy10.firmenregister.managers.UserManager.companyPlayers;

public class CompanyManager {

    // Maps zur Speicherung von Firmeninformationen
    private static final Map<String, String> companyNames = new HashMap<>();
    private static final Map<String, UUID> companyOwners = new HashMap<>();
    private static final Map<String, String> companyDescriptions = new HashMap<>();
    private static final Map<String, String> companyBankingIds = new HashMap<>();
    private static final Map<String, String> companyBizs = new HashMap<>();
    private static final Map<String, List<UUID>> companyMembers = new HashMap<>();

    // Initialisierung: Lädt alle Firmeninformationen aus der Datenbank
    public static void initialize() {
        try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                .prepareStatement("SELECT * FROM companys")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String companyId = resultSet.getString("firmaId");
                String companyName = resultSet.getString("name");
                UUID companyOwner = UUID.fromString(resultSet.getString("owner"));
                String companyDescription = resultSet.getString("description");
                String companyBankingId = resultSet.getString("banking");
                String companyBiz = resultSet.getString("biz");

                // Initialisierung der Maps
                companyNames.put(companyId, companyName);
                companyOwners.put(companyId, companyOwner);
                companyDescriptions.put(companyId, companyDescription);
                companyBankingIds.put(companyId, companyBankingId);
                companyBizs.put(companyId, companyBiz);
                companyMembers.put(companyId, new ArrayList<>()); // Mitgliederliste wird leer initialisiert
            }
        } catch (SQLException e) {
            throw new RuntimeException(e); // Fehler direkt weiterwerfen
        }
    }

    // Generiert eine zufällige 5-stellige Firmen-ID
    private static String generadeCompanyId() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static void addCompany(String companyName, String companyDescription, UUID playerUUID) {
        String compID = generadeCompanyId();

        try {
            // Füge die Firma in die Tabelle `companys` ein
            try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                    .prepareStatement("INSERT INTO companys (firmaId, name, description, banking, biz, owner) VALUES (?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, compID);
                statement.setString(2, companyName);
                statement.setString(3, companyDescription);
                statement.setString(4, "123");  // Banking-ID, kann angepasst werden
                statement.setString(5, "1");  // Beispielwert für biz
                statement.setString(6, playerUUID.toString());  // Owner ist der Spieler
                statement.executeUpdate();
            }

            // Füge den Spieler in die Tabelle `company_users` ein
            try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                    .prepareStatement("INSERT INTO company_users (firmaId, uuid) VALUES (?, ?)")) {
                statement.setString(1, compID);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }

            // Initialisierung der Maps
            companyNames.put(compID, companyName);
            companyOwners.put(compID, playerUUID);
            companyDescriptions.put(compID, companyDescription);
            companyBankingIds.put(compID, "123");
            companyBizs.put(compID, "1");
            companyMembers.put(compID, new ArrayList<>()); // Mitgliederliste wird leer initialisiert
            UserManager.companyPlayers.put(playerUUID, compID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Entfernt eine Firma aus der Datenbank und den Maps
    public static void removeCompany(String companyId) {
        try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                .prepareStatement("DELETE FROM companys WHERE firmaId=?")) {
            statement.setString(1, companyId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Fehlerausgabe
        }

        try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                .prepareStatement("DELETE FROM company_register WHERE firmaId=?")) {
            statement.setString(1, companyId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Fehlerausgabe
        }

        try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                .prepareStatement("DELETE FROM company_users WHERE firmaId=?")) {
            statement.setString(1, companyId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Fehlerausgabe
        }

        // Entfernen aus den Maps
        UserManager.companyPlayers.remove(getOwner(companyId));

        companyOwners.remove(companyId);
        companyNames.remove(companyId);
        companyDescriptions.remove(companyId);
        companyBankingIds.remove(companyId);
        companyBizs.remove(companyId);
        companyMembers.remove(companyId);

        CompanyRegisterManager.registeredCompanys.remove(companyId);
        CompanyRegisterManager.showedInformations.remove(companyId);
    }

    // Gibt den Namen einer Firma zurück
    public static String getName(String companyId) {
        return companyNames.get(companyId);
    }

    // Gibt die BIZ-ID einer Firma zurück
    public static String getBIZ(String companyId) {
        return companyBizs.get(companyId);
    }

    // Gibt die Beschreibung einer Firma zurück
    public static String getDescription(String companyId) {
        return companyDescriptions.get(companyId);
    }

    // Gibt die Mitgliederliste einer Firma zurück
    public static List<UUID> getMembers(String companyId) {
        return companyMembers.get(companyId);
    }

    // Gibt die Banking-ID einer Firma zurück
    public static String getCompanyBankingId(String companyId) {
        return companyBankingIds.get(companyId);
    }

    // Gibt den Besitzer einer Firma zurück
    public static UUID getOwner(String companyId) {
        return companyOwners.get(companyId);
    }

    public static boolean exists(String companyId) {
        return companyNames.containsKey(companyId);
    }
}
