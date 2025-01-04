package de.ianboy10.firmenregister.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SkullBuilder {

    private final String base64;
    private String displayName;
    private List<String> lore;
    private Material material;

    public SkullBuilder(String base64) {
        this.base64 = base64;
        this.displayName = null;
        this.lore = null;
        this.material = Material.SKULL_ITEM;
    }

    public SkullBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SkullBuilder setLore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }
    public ItemStack build() {
        ItemStack skull = new ItemStack(material, 1, (short) 3);
        if (this.base64!=null && !this.base64.isEmpty()) {
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

            try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);

                Object profile = Class.forName("com.mojang.authlib.GameProfile")
                        .getConstructor(UUID.class, String.class)
                        .newInstance(UUID.randomUUID(), null);

                Field propertyMapField = profile.getClass().getDeclaredField("properties");
                propertyMapField.setAccessible(true);
                Object propertyMap = propertyMapField.get(profile);

                Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
                Constructor<?> propertyConstructor = propertyClass.getDeclaredConstructor(String.class, String.class);
                Object property = propertyConstructor.newInstance("textures", this.base64);

                propertyMap.getClass().getMethod("put", Object.class, Object.class)
                        .invoke(propertyMap, "textures", property);

                profileField.set(skullMeta, profile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            skull.setItemMeta(skullMeta);

            if (this.displayName!=null) {
                skullMeta.setDisplayName(this.displayName);
            }

            if (this.lore!=null) {
                skullMeta.setLore(this.lore);
            }

            skull.setItemMeta(skullMeta);

            return skull;
        } else {
            return skull;
        }
    }

    public static String getPlayerSkinBase64(Player player) {
        try {
            JSONArray properties = getJsonArray(player);
            JSONObject textureProperty = null;
            for (Object property : properties) {
                JSONObject prop = (JSONObject) property;
                String name = (String) prop.get("name");
                if (name.equals("textures")) {
                    textureProperty = prop;
                    break;
                }
            }
            if (textureProperty != null) {
                return (String) textureProperty.get("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONArray getJsonArray(Player player) throws IOException, ParseException {
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + player.getUniqueId());
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.toString());
        return (JSONArray) json.get("properties");
    }

}
