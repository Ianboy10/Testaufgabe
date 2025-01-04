package de.ianboy10.firmenregister.utils;

import de.ianboy10.firmenregister.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private final String host = "localhost";
    private final String port = "4310";
    private final String database = "testaufgabe";
    private final String username;
    private final String password;
    private Connection con;

    public DatabaseManager(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    private void connect() { // -- Zur Datenbank verbinden --
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            }
        } catch (SQLException e) { // -- Fehler beim Verbinden zur Datenbank --
            System.out.println(e.getMessage());
            Main.getInstance().getLogger().severe("Fehler beim Verbinden zur Datenbank.");
        }
    }

    public Connection getConnection() {
        try {
            if (con == null || con.isClosed()) connect();
            return con;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
