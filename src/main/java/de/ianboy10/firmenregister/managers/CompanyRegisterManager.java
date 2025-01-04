package de.ianboy10.firmenregister.managers;

import de.ianboy10.firmenregister.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyRegisterManager {

    // Liste der registrierten Firmen-IDs
    public static final ArrayList<String> registeredCompanys = new ArrayList<>();

    // Map zur Zuordnung von Firmen-IDs zu ihren Sichtbarkeitsinformationen
    public static final Map<String, Map<RegisterTypes, Boolean>> showedInformations = new HashMap<>();

    // Initialisierungsmethode zum Laden der Daten aus der Datenbank
    public static void initialize() {
        try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                .prepareStatement("SELECT * FROM company_register")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String firmaId = rs.getString("firmaId");

                // Sichtbarkeitsinformationen aus der Datenbank abrufen
                Map<RegisterTypes, Boolean> types = new HashMap<>();
                types.put(RegisterTypes.BANKINGID, rs.getBoolean("bankingId"));
                types.put(RegisterTypes.OWNER, rs.getBoolean("owner"));
                types.put(RegisterTypes.DESCRIPTION, rs.getBoolean("description"));
                types.put(RegisterTypes.BIZ, rs.getBoolean("biz"));
                types.put(RegisterTypes.MEMBERS, rs.getBoolean("members"));

                // Daten in Maps und Listen speichern
                showedInformations.put(firmaId, types);
                registeredCompanys.add(firmaId);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Fehlerprotokollierung
        }
    }

    // Überprüfen, ob eine Firma registriert ist
    public static boolean isRegistered(String firmaId) {
        return registeredCompanys.contains(firmaId);
    }

    // Eine neue Firma registrieren
    public static void registerCompany(String firmaId) {
        // Standardwerte für Sichtbarkeitsinformationen
        Map<RegisterTypes, Boolean> types = new HashMap<>();
        for (RegisterTypes registerType : RegisterTypes.values()) {
            types.put(registerType, true); // Standardmäßig auf 'true'
        }
        showedInformations.put(firmaId, types);

        try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                .prepareStatement("INSERT INTO company_register(firmaId, bankingId, owner, description, biz, members) VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, firmaId);
            statement.setBoolean(2, true);
            statement.setBoolean(3, true);
            statement.setBoolean(4, true);
            statement.setBoolean(5, true);
            statement.setBoolean(6, true);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Fehlerprotokollierung
        }

        registeredCompanys.add(firmaId); // Firma zur Liste hinzufügen
    }

    // Eine Firma abmelden (aus Datenbank und Speicher entfernen)
    public static void unregisterCompany(String firmaId) {
        registeredCompanys.remove(firmaId);
        showedInformations.remove(firmaId);

        try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                .prepareStatement("DELETE FROM company_register WHERE firmaId=?")) {
            statement.setString(1, firmaId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Fehlerprotokollierung
        }
    }

    public static void setAllowing(String firmaId, RegisterTypes types, boolean bool) {

        // Bestimmen, welche Spalte geändert werden muss basierend auf dem RegisterType
        String columnName = null;
        switch (types) {
            case BANKINGID:
                columnName = "bankingId";
                break;
            case OWNER:
                columnName = "owner";
                break;
            case DESCRIPTION:
                columnName = "description";
                break;
            case BIZ:
                columnName = "biz";
                break;
            case MEMBERS:
                columnName = "members";
                break;
            default:
                throw new IllegalArgumentException("Unbekannter RegisterType: " + types);
        }

        // SQL-Abfrage zum Aktualisieren der Sichtbarkeit der jeweiligen Spalte
        String sql = "UPDATE company_register SET " + columnName + " = ? WHERE firmaId = ?";

        try (PreparedStatement stmt = Main.getDatabaseManager().getConnection().prepareStatement(sql)) {
            // Setzen der Parameter (true = 1, false = 0)
            stmt.setBoolean(1, bool);
            stmt.setString(2, firmaId);

            // Ausführen der SQL-Abfrage und Fehlerbehandlung
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Bestehende Sichtbarkeitsinformationen abrufen
        showedInformations.put(firmaId, loadVisibilityFromDatabase(firmaId));

        // Debug-Ausgabe: Überprüfen, ob die Map korrekt aktualisiert wurde
    }

    private static Map<RegisterTypes, Boolean> loadVisibilityFromDatabase(String firmaId) {
        Map<RegisterTypes, Boolean> visibilityMap = new HashMap<>();
        String sql = "SELECT * FROM company_register WHERE firmaId = ?";
        try (PreparedStatement stmt = Main.getDatabaseManager().getConnection().prepareStatement(sql)) {
            stmt.setString(1, firmaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    visibilityMap.put(RegisterTypes.BANKINGID, rs.getBoolean("bankingId"));
                    visibilityMap.put(RegisterTypes.OWNER, rs.getBoolean("owner"));
                    visibilityMap.put(RegisterTypes.DESCRIPTION, rs.getBoolean("description"));
                    visibilityMap.put(RegisterTypes.BIZ, rs.getBoolean("biz"));
                    visibilityMap.put(RegisterTypes.MEMBERS, rs.getBoolean("members"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return visibilityMap;
    }


    // Sichtbarkeitsstatus für einen bestimmten Typ abrufen
    public static boolean getAllowing(String firmaId, RegisterTypes type) {
        return showedInformations.get(firmaId) != null
                && showedInformations.get(firmaId).getOrDefault(type, false);
    }

    // Liste aller registrierten Firmen abrufen
    public static List<String> getAllRegisteredCompnays() {
        List<String> companyList = new ArrayList<>();
        // Hole alle Firmen aus der Datenbank oder einer anderen Quelle
        try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                .prepareStatement("SELECT firmaId FROM company_register")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet != null && resultSet.next()) {
                companyList.add(resultSet.getString("firmaId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companyList;  // Sollte niemals null sein
    }

}
