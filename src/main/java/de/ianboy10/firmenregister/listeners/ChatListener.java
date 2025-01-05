package de.ianboy10.firmenregister.listeners;

import de.ianboy10.firmenregister.guis.GUIBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        // Prüfen, ob der Spieler in der Suchliste ist
        if (GUIListener.list.contains(player)) {
            e.setCancelled(true); // Chat-Nachricht abbrechen
            GUIListener.list.remove(player); // Spieler aus der Suchliste entfernen

            String search = e.getMessage(); // Spieler eingegebener Begriff
            Inventory inventory = GUIBuilder.getRegisterGUI(player, search);
            int items = 0;

            // Zählen der Items im Inventar (maximal bis Slot 26)
            for (int i = 0; i <= 26; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    items++;
                }
            }

            // Überprüfung, ob gefilterte Items vorhanden sind
            if (items == 0) {
                // Kein Ergebnis gefunden, Standard-GUI öffnen
                player.openInventory(GUIBuilder.getRegisterGUI(player, null));
                player.sendMessage("§aEs wurden keine Firmen mit deinem Filter gefunden!");
            } else {
                // Gefilterte Ergebnisse anzeigen
                player.openInventory(inventory);
            }
        }
    }

}
