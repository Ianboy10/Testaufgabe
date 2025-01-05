package de.ianboy10.firmenregister.guis;

import de.ianboy10.firmenregister.managers.*;
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

    // Erstellt das Firmenregister-Inventar mit optionalem Suchbegriff.
    public static Inventory getRegisterGUI(Player player, String message) {
        Inventory inventory = Bukkit.createInventory(null, 4 * 9, "§a§lFirmenregister");

        String playerCompanyId = UserManager.getCompany(player.getUniqueId());
        Company playersCompany = CompanyManager.getCompany(playerCompanyId);

        for (String companyId : CompanyRegisterManager.getAllRegisteredCompnays()) {
            Company company = CompanyManager.getCompany(companyId);
            if (!CompanyManager.exists(company.getCompanyId())) continue;

            boolean matchesFilter = message == null ||
                    company.getName().contains(message) ||
                    company.getDescription().contains(message) ||
                    company.getMembers().contains(message);


            if (matchesFilter) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.ANVIL)
                        .setDisplayName("§a" + company.getName())
                        .setLore(Arrays.asList(
                                        (company.getDescription() != null && CompanyRegisterManager.getAllowing(companyId, RegisterTypes.DESCRIPTION))
                                                ? "§7Beschreibung: " + company.getDescription() : null,

                                        (company.getOwner() != null && CompanyRegisterManager.getAllowing(companyId, RegisterTypes.OWNER))
                                                ? "§7Inhaber: " + Bukkit.getOfflinePlayer(company.getOwner()).getName() : null,

                                        (company.getBankingId() != null && CompanyRegisterManager.getAllowing(companyId, RegisterTypes.BANKINGID))
                                                ? "§7Kontonummer: " + company.getBankingId() : null,

                                        (company.getBiz() != null && CompanyRegisterManager.getAllowing(companyId, RegisterTypes.BIZ))
                                                ? "§7BIZ: " + company.getBiz() : null,

                                        (company.getMembers() != null && CompanyRegisterManager.getAllowing(companyId, RegisterTypes.MEMBERS))
                                                ? "§7Mitglieder: " + company.getMembers().size() : null
                                ).stream()
                                .filter(Objects::nonNull)  // Filter out null values
                                .collect(Collectors.toList()));
                ItemStack itemStack = itemBuilder.build();

                NBT.modify(itemStack, nbt -> {
                    nbt.setString("companyId", companyId);
                });

                inventory.addItem(itemStack);
            }
        }

        // Graue Platzhalter im unteren Bereich des Inventars
        for (int i = 27; i < 36; i++) {
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName("§8").build());
        }

        // Optionen basierend auf der Firmensituation des Spielers
        if (UserManager.inCompany(player.getUniqueId()) && playersCompany.getOwner().equals(player.getUniqueId())) {
            if (!CompanyRegisterManager.isRegistered(playerCompanyId)) {
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


    // Erstellt das Firmenverwaltungs-Inventar für den Spieler.
    public static Inventory getManagementInventory(String companyId, Player player) {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, "§a§lFirmenverwaltung");

        Company company = CompanyManager.getCompany(companyId);
        if(company == null || !CompanyManager.exists(companyId)) return null;


        for (int i = 0; i < 27; i++) { // Graue Glasscheiben als Platzhalter
            if(i >= 11 && i <= 15) continue;
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName("§8").build());
        }

        int startSlot = 11;
        for (RegisterTypes registerTypes : RegisterTypes.values()) {
            boolean activated = CompanyRegisterManager.getAllowing(companyId, registerTypes);
            Material material = (activated) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;

            ItemBuilder itemBuilder = new ItemBuilder(material)
                    .setDisplayName((activated ? "§a" : "§c") + registerTypes.getDisplayName())
                    .setLore(Arrays.asList("§7Klicke hier, um die angegebene Kategorie zu de-/aktivieren."));

            inventory.setItem(startSlot++, itemBuilder.build());
        }

        ItemBuilder itemBuilder = new ItemBuilder(Material.REDSTONE_BLOCK)
                .setDisplayName("§c§lFirma entfernen")
                .setLore(Arrays.asList("§7Klicke hier, um deine Firma aus dem Firmenregister zu entfernen."));
        inventory.setItem(18, itemBuilder.build());

        if(player != null && player.isOp()) {
            SkullBuilder skullBuilder = new SkullBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY0MzlkMmUzMDZiMjI1NTE2YWE5YTZkMDA3YTdlNzVlZGQyZDUwMTVkMTEzYjQyZjQ0YmU2MmE1MTdlNTc0ZiJ9fX0=")
                    .setDisplayName("§c§lFirmeninformationen")
                    .setLore("§7FirmenID: " + companyId, "§7Besitzer: " + Bukkit.getOfflinePlayer(company.getOwner()).getName());
            inventory.setItem(19, skullBuilder.build());
        }

        return inventory;
    }

}
