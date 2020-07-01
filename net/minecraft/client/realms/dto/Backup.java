/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.dto;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class Backup
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public String backupId;
    public Date lastModifiedDate;
    public long size;
    private boolean uploadedVersion;
    public Map<String, String> metadata = Maps.newHashMap();
    public Map<String, String> changeList = Maps.newHashMap();

    public static Backup parse(JsonElement node) {
        JsonObject jsonObject = node.getAsJsonObject();
        Backup backup = new Backup();
        try {
            backup.backupId = JsonUtils.getStringOr("backupId", jsonObject, "");
            backup.lastModifiedDate = JsonUtils.getDateOr("lastModifiedDate", jsonObject);
            backup.size = JsonUtils.getLongOr("size", jsonObject, 0L);
            if (jsonObject.has("metadata")) {
                JsonObject jsonObject2 = jsonObject.getAsJsonObject("metadata");
                Set<Map.Entry<String, JsonElement>> set = jsonObject2.entrySet();
                for (Map.Entry<String, JsonElement> entry : set) {
                    if (entry.getValue().isJsonNull()) continue;
                    backup.metadata.put(Backup.format(entry.getKey()), entry.getValue().getAsString());
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Could not parse Backup: " + exception.getMessage());
        }
        return backup;
    }

    private static String format(String key) {
        String[] strings = key.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            if (string == null || string.length() < 1) continue;
            if ("of".equals(string)) {
                stringBuilder.append(string).append(" ");
                continue;
            }
            char c = Character.toUpperCase(string.charAt(0));
            stringBuilder.append(c).append(string.substring(1)).append(" ");
        }
        return stringBuilder.toString();
    }

    public boolean isUploadedVersion() {
        return this.uploadedVersion;
    }

    public void setUploadedVersion(boolean uploadedVersion) {
        this.uploadedVersion = uploadedVersion;
    }
}

