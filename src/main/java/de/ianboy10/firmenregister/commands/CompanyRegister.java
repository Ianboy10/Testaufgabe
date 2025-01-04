package de.ianboy10.firmenregister.commands;

import de.ianboy10.firmenregister.guis.GUIBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompanyRegister implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Überprüfen, ob der Befehl von einem Spieler ausgeführt wurde
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Öffnen des Firmenregistrierungs-GUIs für den Spieler
            player.openInventory(GUIBuilder.getRegisterGUI(player, null));
            return true; // Erfolgreiche Ausführung
        } else {
            // Fehlernachricht, wenn der Befehl nicht von einem Spieler ausgeführt wird
            sender.sendMessage("§cDieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false; // Fehlgeschlagene Ausführung
        }
    }

}
