package de.ianboy10.firmenregister.commands;

import de.ianboy10.firmenregister.managers.CompanyManager;
import de.ianboy10.firmenregister.managers.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCompany implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Überprüfen, ob der Spieler bereits in einer Firma ist
            if (!UserManager.inCompany(player.getUniqueId())) {
                if (args.length >= 2) {
                    String companyName = args[0];
                    String companyDescription = "";
                    for (int i = 0; i < args.length; i++) {
                        companyDescription += args[i] + " ";
                    }

                    CompanyManager.addCompany(companyName, companyDescription, player.getUniqueId());
                    player.sendMessage("§aDu hast eine Firma erstellt, die BIZ-ID lautet 123!");
                }
            } else {
                player.sendMessage("§cLösche deine Firma erst mit /delcompany");
            }

        } else {
            sender.sendMessage("§cDieser Befehl kann nur von einem Spieler ausgeführt werden.");
        }
        return true;
    }
}
