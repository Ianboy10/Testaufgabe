package de.ianboy10.firmenregister;

import de.ianboy10.firmenregister.commands.CompanyRegister;
import de.ianboy10.firmenregister.commands.DeleteCompany;
import de.ianboy10.firmenregister.commands.RegisterCompany;
import de.ianboy10.firmenregister.listeners.ChatListener;
import de.ianboy10.firmenregister.listeners.GUIListener;
import de.ianboy10.firmenregister.managers.CompanyManager;
import de.ianboy10.firmenregister.managers.CompanyRegisterManager;
import de.ianboy10.firmenregister.managers.UserManager;
import de.ianboy10.firmenregister.utils.DatabaseManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Getter
    private static DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialisierung des Plugins und der Manager
        init();

        // Registrierung der Commands und Events
        registerCommands();
        registerListener();

        getLogger().info("Firmenregister Plugin aktiviert.");
    }

    @Override
    public void onDisable() {
        // Hier könnte man Datenbankverbindungen schließen oder andere Bereinigungsprozesse hinzufügen
        getLogger().info("Firmenregister Plugin deaktiviert.");
    }

    private void init() {
        // Initialisiere den DatabaseManager mit geeigneten Parametern (evtl. aus einer Konfiguration)
        databaseManager = new DatabaseManager("testaufgabe", "testaufgabe");

        // Initialisierung der Manager
        CompanyManager.initialize();
        CompanyRegisterManager.initialize();

    }

    private void registerCommands() {
        getCommand("createfirma").setExecutor(new RegisterCompany());
        getCommand("firmenregister").setExecutor(new CompanyRegister());
        getCommand("delcompany").setExecutor(new DeleteCompany());
    }

    private void registerListener() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new GUIListener(), this);
        pm.registerEvents(new ChatListener(), this);
    }
}
