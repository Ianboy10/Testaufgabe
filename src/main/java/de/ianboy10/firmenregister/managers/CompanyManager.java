package de.ianboy10.firmenregister.managers;

import de.ianboy10.firmenregister.Main;

import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CompanyManager {

    // Map zur Speicherung von Firmen (Firma-ID zu Company-Objekt)
    private static final Map<String, Company> companies = new HashMap<>();


    // Initialisiert den Manager, indem alle Firmen und deren Mitglieder aus der Datenbank geladen werden.
    public static void initialize() {
        try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                .prepareStatement("SELECT * FROM companys")) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String companyId = resultSet.getString("firmaId");
                String name = resultSet.getString("name");
                UUID owner = UUID.fromString(resultSet.getString("owner"));
                String description = resultSet.getString("description");
                String bankingId = resultSet.getString("banking");
                String biz = resultSet.getString("biz");

                // Erstelle Company-Objekt
                Company company = new Company(companyId, name, owner, description, bankingId, biz, new ArrayList<>());

                // Lade Mitglieder der Firma
                loadMembers(company);

                // Speichere die Firma in der Map
                companies.put(companyId, company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  Läd die Mitglieder einer Firma aus der Datenbank.
    private static void loadMembers(Company company) {
        try (PreparedStatement memberStatement = Main.getDatabaseManager().getConnection()
                .prepareStatement("SELECT uuid FROM company_users WHERE firmaId = ?")) {
            memberStatement.setString(1, company.getCompanyId());
            ResultSet resultSet = memberStatement.executeQuery();

            while (resultSet.next()) {
                UUID memberId = UUID.fromString(resultSet.getString("uuid"));
                company.addMember(memberId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Erstellt eine neue Firma und speichert sie in der Datenbank.
    public static void addCompany(String name, String description, UUID owner) {
        String companyId = generateCompanyId();

        try {
            // Firma in die Tabelle `companys` einfügen
            try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                    .prepareStatement("INSERT INTO companys (firmaId, name, description, banking, biz, owner) VALUES (?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, companyId);
                statement.setString(2, name);
                statement.setString(3, description);
                statement.setString(4, "123"); // Beispielwert für Banking-ID
                statement.setString(5, "1");   // Beispielwert für Biz
                statement.setString(6, owner.toString());
                statement.executeUpdate();
            }

            // Eigentümer als erstes Mitglied hinzufügen
            try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                    .prepareStatement("INSERT INTO company_users (firmaId, uuid) VALUES (?, ?)")) {
                statement.setString(1, companyId);
                statement.setString(2, owner.toString());
                statement.executeUpdate();
            }

            // Company-Objekt erstellen und registrieren
            Company company = new Company(companyId, name, owner, description, "123", "1", new ArrayList<>());
            company.addMember(owner);
            companies.put(companyId, company);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Entfernt eine Firma aus der Datenbank und dem Speicher.
    public static void removeCompany(String companyId) {
        try {
            // Firma aus der Tabelle `companys` entfernen
            try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                    .prepareStatement("DELETE FROM companys WHERE firmaId = ?")) {
                statement.setString(1, companyId);
                statement.executeUpdate();
            }

            // Alle Mitglieder der Firma aus der Tabelle `company_users` entfernen
            try (PreparedStatement statement = Main.getDatabaseManager().getConnection()
                    .prepareStatement("DELETE FROM company_users WHERE firmaId = ?")) {
                statement.setString(1, companyId);
                statement.executeUpdate();
            }

            // Firma aus dem Speicher entfernen
            companies.remove(companyId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Überprüft, ob eine Firma existiert.
    public static boolean exists(String companyId) {
        return companies.containsKey(companyId);
    }

    // Gibt eine Firma basierend auf ihrer ID zurück.
    public static Company getCompany(String companyId) {
        return companies.get(companyId);
    }

    // Gibt eine 6 Stellige ID zurück.
    private static String generateCompanyId() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Alle Companies
    public static Collection<Company> getAllCompanies() {
        return companies.values();
    }
}
