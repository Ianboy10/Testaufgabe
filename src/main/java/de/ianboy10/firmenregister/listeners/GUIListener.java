package de.ianboy10.firmenregister.listeners;

import de.ianboy10.firmenregister.guis.GUIBuilder;
import de.ianboy10.firmenregister.managers.*;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GUIListener implements Listener {

    // Liste für Spieler, die nach einer Firma suchen
    public static final List<Player> list = new ArrayList<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Company company = CompanyManager.getCompany(UserManager.getCompany(player.getUniqueId()));

        // Sicherheitsprüfungen, um Fehler bei null-Werten zu vermeiden
        if (event.getView() == null || event.getView().getTitle() == null) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null
                || event.getCurrentItem().getItemMeta().getDisplayName() == null) return;

        // Verarbeitung für das Firmenregister-Inventar
        if (event.getView().getTitle().equalsIgnoreCase("§a§lFirmenregister")) {
            event.setCancelled(true); // Verhindert das Entfernen von Items aus dem GUI

            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

            if (displayName.equalsIgnoreCase("§a§lFirma hinzufügen")) {
                // Firma registrieren
                CompanyRegisterManager.registerCompany(company.getCompanyId());
                player.openInventory(GUIBuilder.getRegisterGUI(player, null));
                player.sendMessage("§aDeine Firma wurde erfolgreich registriert.");
            } else if (displayName.equalsIgnoreCase("§a§lFirma verwalten")) {
                // Verwaltung öffnen
                player.openInventory(GUIBuilder.getManagementInventory(company.getCompanyId(), player));
            } else if (displayName.equalsIgnoreCase("§aSuchen")) {
                // Spieler in Suchmodus versetzen
                list.add(player);
                player.sendMessage("§bWonach suchst du genau?");
                player.closeInventory();
            }
        }
        // Verarbeitung für das Firmenverwaltungs-Inventar
        else if (event.getView().getTitle().equalsIgnoreCase("§a§lFirmenverwaltung")) {
            event.setCancelled(true); // Verhindert das Entfernen von Items aus dem GUI

            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

            if (displayName.equalsIgnoreCase("§c§lFirma entfernen")) {
                // Firma aus dem Register entfernen
                CompanyRegisterManager.unregisterCompany(company.getCompanyId());
                player.closeInventory();
                player.sendMessage("§aDu hast deine Firma erfolgreich aus dem Firmenregister entfernt.");
            } else if (event.getCurrentItem().getType() == Material.REDSTONE_BLOCK || event.getCurrentItem().getType() == Material.EMERALD_BLOCK) {
                // Verarbeiten von Sichtbarkeitseinstellungen
                RegisterTypes type = null;
                boolean bool;

                switch (event.getCurrentItem().getType()) {
                    case REDSTONE_BLOCK: // Deaktivieren
                        bool = true;
                        break;
                    case EMERALD_BLOCK: // Aktivieren
                        bool = false;
                        break;
                    default: // Standardmäßig aktivieren
                        bool = true;
                }

                // Bestimmung des Typs anhand des Item-Namens
                ItemStack itemStack = event.getCurrentItem();
                String itemName = itemStack.getItemMeta().getDisplayName();

                if (itemName.endsWith("Kontonummer")) {
                    type = RegisterTypes.BANKINGID;
                } else if (itemName.endsWith("Besitzer")) {
                    type = RegisterTypes.OWNER;
                } else if (itemName.endsWith("Firmenbeschreibung")) {
                    type = RegisterTypes.DESCRIPTION;
                } else if (itemName.endsWith("BIZ")) {
                    type = RegisterTypes.BIZ;
                } else if (itemName.endsWith("Mitglieder")) {
                    type = RegisterTypes.MEMBERS;
                }

                // Sichtbarkeitsstatus setzen
                if (type != null) {
                    CompanyRegisterManager.setAllowing(company.getCompanyId(), type, bool);
                }

                player.openInventory(GUIBuilder.getManagementInventory(company.getCompanyId(), player));
            }
        }
    }
}
