package de.ianboy10.firmenregister.guis;

import de.ianboy10.firmenregister.managers.CompanyManager;
import de.ianboy10.firmenregister.managers.CompanyRegisterManager;
import de.ianboy10.firmenregister.managers.RegisterTypes;
import de.ianboy10.firmenregister.managers.UserManager;
import de.ianboy10.firmenregister.utils.ItemBuilder;
import de.ianboy10.firmenregister.utils.SkullBuilder;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class GUIBuilder {

    /**
     * Erstellt das Firmenregister-Inventar mit optionalem Suchbegriff.
     */
    public static Inventory getRegisterGUI(Player player, String message) {
        Inventory inventory = Bukkit.createInventory(null, 4 * 9, "§a§lFirmenregister");

        for (String firmaId : CompanyRegisterManager.getAllRegisteredCompnays()) {
            if (firmaId == null || CompanyManager.getName(firmaId) == null) continue;
            boolean matchesFilter = message == null ||
                    CompanyManager.getName(firmaId).contains(message) ||
                    CompanyManager.getDescription(firmaId).contains(message) ||
                    CompanyManager.getMembers(firmaId).contains(message);

            if (matchesFilter) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.ANVIL)
                        .setDisplayName("§a" + CompanyManager.getName(firmaId))
                        .setLore(Arrays.asList(
                                        (CompanyManager.getDescription(firmaId) != null && CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.DESCRIPTION))
                                                ? "§7Beschreibung: " + CompanyManager.getDescription(firmaId) : null,

                                        (CompanyManager.getCompanyBankingId(firmaId) != null && CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.BANKINGID))
                                                ? "§7Kontonummer: " + CompanyManager.getCompanyBankingId(firmaId) : null,

                                        (CompanyManager.getOwner(firmaId) != null && CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.OWNER))
                                                ? "§7Inhaber: " + Bukkit.getOfflinePlayer(CompanyManager.getOwner(firmaId)).getName() : null,

                                        (CompanyManager.getBIZ(firmaId) != null && CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.BIZ))
                                                ? "§7BIZ: " + CompanyManager.getBIZ(firmaId) : null,

                                        (CompanyManager.getMembers(firmaId) != null && CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.MEMBERS))
                                                ? "§7Mitglieder: " + CompanyManager.getMembers(firmaId).size() : null
                                ).stream()
                                .filter(Objects::nonNull)  // Filter out null values
                                .collect(Collectors.toList()));
                ItemStack itemStack = itemBuilder.build();

                NBT.modify(itemStack, nbt -> {
                    nbt.setString("companyId", firmaId);
                });

                inventory.addItem(itemStack);
            }
        }

        // Graue Platzhalter im unteren Bereich des Inventars
        for (int i = 27; i < 36; i++) {
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName("§8").build());
        }

        // Optionen basierend auf der Firmensituation des Spielers
        if (UserManager.inCompany(player.getUniqueId()) && CompanyManager.getOwner(UserManager.getCompany(player.getUniqueId())).equals(player.getUniqueId())) {
            String firmaId = UserManager.getCompany(player.getUniqueId());

            if (!CompanyRegisterManager.isRegistered(firmaId)) {
                inventory.setItem(27, new SkullBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19")
                        .setDisplayName("§a§lFirma hinzufügen")
                        .setLore("§7Deine Firma wird dabei im Firmenregister hinzugefügt.")
                        .build());
            } else {
                inventory.setItem(27, new SkullBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQxOWM2ODQ2MTY2NmFhY2Q3NjI4ZTM0YTFlMmFkMzlmZTRmMmJkZTMyZTIzMTk2M2VmM2IzNTUzMyJ9fX0=")
                        .setDisplayName("§a§lFirma verwalten")
                        .setLore("§7Verwalte deine Firma im Firmenregister.")
                        .build());
            }
        }

        // Suchoption
        inventory.setItem(31, new SkullBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFiNzM5ZjRjYjAyOGVmMmFjZjM0YTZkYzNiNGZmODVlYWM1Y2E5ODdiNTgzMmJmZGQwZjNjMzM1MWFhNDQzIn19fQ==")
                .setDisplayName("§aSuchen")
                .setLore("§7Nach einem Begriff suchen...")
                .build());

        return inventory;
    }

    /**
     * Erstellt das Firmenverwaltungs-Inventar für den Spieler.
     */
    public static Inventory getManagementInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, "§a§lFirmenverwaltung");

        if (UserManager.getCompany(player.getUniqueId()) != null && CompanyRegisterManager.isRegistered(UserManager.getCompany(player.getUniqueId()))) {
            String firmaId = UserManager.getCompany(player.getUniqueId()); // Firmen ID vom Spieler

            ItemBuilder itemBuilder = new ItemBuilder(Material.REDSTONE_BLOCK)
                    .setDisplayName("§c§lFirma entfernen")
                    .setLore(Arrays.asList("§7Klicke hier, um deine Firma aus dem Firmenregister zu entfernen."));

            ItemBuilder bankingid;
            if (CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.BANKINGID)) {
                bankingid = new ItemBuilder(Material.EMERALD_BLOCK)
                        .setDisplayName("§aKontonummer")
                        .setLore(Arrays.asList("§7Klicke, um die Kontonummer zu verstecken."));
            } else {
                bankingid = new ItemBuilder(Material.REDSTONE_BLOCK)
                        .setDisplayName("§cKontonummer")
                        .setLore(Arrays.asList("§7Klicke, um die Kontonummer zu zeigen."));
            }

            ItemBuilder owner;
            if (CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.OWNER)) {
                owner = new ItemBuilder(Material.EMERALD_BLOCK)
                        .setDisplayName("§aBesitzer")
                        .setLore(Arrays.asList("§7Klicke, um den Besitzer zu verstecken."));
            } else {
                owner = new ItemBuilder(Material.REDSTONE_BLOCK)
                        .setDisplayName("§cBesitzer")
                        .setLore(Arrays.asList("§7Klicke, um den Besitzer zu zeigen."));
            }

            ItemBuilder description;
            if (CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.DESCRIPTION)) {
                description = new ItemBuilder(Material.EMERALD_BLOCK)
                        .setDisplayName("§aFirmenbeschreibung")
                        .setLore(Arrays.asList("§7Klicke, um die Beschreibung zu verstecken."));
            } else {
                description = new ItemBuilder(Material.REDSTONE_BLOCK)
                        .setDisplayName("§cFirmenbeschreibung")
                        .setLore(Arrays.asList("§7Klicke, um die Beschreibung zu zeigen."));
            }

            ItemBuilder biz;
            if (CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.BIZ)) {
                biz = new ItemBuilder(Material.EMERALD_BLOCK)
                        .setDisplayName("§aBIZ")
                        .setLore(Arrays.asList("§7Klicke, um die BIZ zu verstecken."));
            } else {
                biz = new ItemBuilder(Material.REDSTONE_BLOCK)
                        .setDisplayName("§cBIZ")
                        .setLore(Arrays.asList("§7Klicke, um die BIZ zu zeigen."));
            }

            ItemBuilder members;
            if (CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.MEMBERS)) {
                members = new ItemBuilder(Material.EMERALD_BLOCK)
                        .setDisplayName("§aMitglieder")
                        .setLore(Arrays.asList("§7Klicke, um die Mitglieder zu verstecken."));
            } else {
                members = new ItemBuilder(Material.REDSTONE_BLOCK)
                        .setDisplayName("§cMitglieder")
                        .setLore(Arrays.asList("§7Klicke, um die Mitglieder zu zeigen."));
            }

            for (int i = 0; i < 27; i++) { // Graue Glasscheiben als Platzhalter
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName("§8").build());
            }

            inventory.setItem(11, bankingid.build());
            inventory.setItem(12, owner.build());
            inventory.setItem(13, description.build());
            inventory.setItem(14, biz.build());
            inventory.setItem(15, members.build());
            inventory.setItem(18, itemBuilder.build());

        }
        return inventory;
    }

    public static Inventory getManagementInventory(String firmaId) {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, "§a§lFirmenverwaltung");

        ItemBuilder itemBuilder = new ItemBuilder(Material.REDSTONE_BLOCK)
                .setDisplayName("§c§lFirma entfernen")
                .setLore(Arrays.asList("§7Klicke hier, um deine Firma aus dem Firmenregister zu entfernen."));

        ItemBuilder bankingid;
        if (CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.BANKINGID)) {
            bankingid = new ItemBuilder(Material.EMERALD_BLOCK)
                    .setDisplayName("§aKontonummer")
                    .setLore(Arrays.asList("§7Klicke, um die Kontonummer zu verstecken."));
        } else {
            bankingid = new ItemBuilder(Material.REDSTONE_BLOCK)
                    .setDisplayName("§cKontonummer")
                    .setLore(Arrays.asList("§7Klicke, um die Kontonummer zu zeigen."));
        }

        ItemBuilder owner;
        if (CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.OWNER)) {
            owner = new ItemBuilder(Material.EMERALD_BLOCK)
                    .setDisplayName("§aBesitzer")
                    .setLore(Arrays.asList("§7Klicke, um den Besitzer zu verstecken."));
        } else {
            owner = new ItemBuilder(Material.REDSTONE_BLOCK)
                    .setDisplayName("§cBesitzer")
                    .setLore(Arrays.asList("§7Klicke, um den Besitzer zu zeigen."));
        }

        ItemBuilder description;
        if (CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.DESCRIPTION)) {
            description = new ItemBuilder(Material.EMERALD_BLOCK)
                    .setDisplayName("§aFirmenbeschreibung")
                    .setLore(Arrays.asList("§7Klicke, um die Beschreibung zu verstecken."));
        } else {
            description = new ItemBuilder(Material.REDSTONE_BLOCK)
                    .setDisplayName("§cFirmenbeschreibung")
                    .setLore(Arrays.asList("§7Klicke, um die Beschreibung zu zeigen."));
        }

        ItemBuilder biz;
        if (CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.BIZ)) {
            biz = new ItemBuilder(Material.EMERALD_BLOCK)
                    .setDisplayName("§aBIZ")
                    .setLore(Arrays.asList("§7Klicke, um die BIZ zu verstecken."));
        } else {
            biz = new ItemBuilder(Material.REDSTONE_BLOCK)
                    .setDisplayName("§cBIZ")
                    .setLore(Arrays.asList("§7Klicke, um die BIZ zu zeigen."));
        }

        ItemBuilder members;
        if (CompanyRegisterManager.getAllowing(firmaId, RegisterTypes.MEMBERS)) {
            members = new ItemBuilder(Material.EMERALD_BLOCK)
                    .setDisplayName("§aMitglieder")
                    .setLore(Arrays.asList("§7Klicke, um die Mitglieder zu verstecken."));
        } else {
            members = new ItemBuilder(Material.REDSTONE_BLOCK)
                    .setDisplayName("§cMitglieder")
                    .setLore(Arrays.asList("§7Klicke, um die Mitglieder zu zeigen."));
        }

        for (int i = 0; i < 27; i++) { // Graue Glasscheiben als Platzhalter
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName("§8").build());
        }

        inventory.setItem(11, bankingid.build());
        inventory.setItem(12, owner.build());
        inventory.setItem(13, description.build());
        inventory.setItem(14, biz.build());
        inventory.setItem(15, members.build());
        inventory.setItem(18, itemBuilder.build());

        return inventory;
    }

}
