package de.ianboy10.firmenregister.commands;

import de.ianboy10.firmenregister.managers.CompanyManager;
import de.ianboy10.firmenregister.managers.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCompany implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Überprüfen, ob der Spieler einer Firma zugeordnet ist
            String companyId = UserManager.getCompany(player.getUniqueId());
            if (companyId != null) {
                // Firma entfernen
                CompanyManager.removeCompany(companyId);
                player.sendMessage("§aDie Firma wurde erfolgreich gelöscht!");
            } else {
                player.sendMessage("§cDu hast keine Firma, die gelöscht werden könnte.");
            }
        } else {
            sender.sendMessage("§cDieser Befehl kann nur von einem Spieler ausgeführt werden.");
        }
        return true;
    }
}
