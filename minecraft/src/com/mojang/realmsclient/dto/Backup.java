package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Backup extends class_4352 {
	private static final Logger LOGGER = LogManager.getLogger();
	public String backupId;
	public Date lastModifiedDate;
	public long size;
	private boolean uploadedVersion;
	public Map<String, String> metadata = new HashMap();
	public Map<String, String> changeList = new HashMap();

	public static Backup parse(JsonElement jsonElement) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		Backup backup = new Backup();

		try {
			backup.backupId = class_4431.method_21547("backupId", jsonObject, "");
			backup.lastModifiedDate = class_4431.method_21544("lastModifiedDate", jsonObject);
			backup.size = class_4431.method_21546("size", jsonObject, 0L);
			if (jsonObject.has("metadata")) {
				JsonObject jsonObject2 = jsonObject.getAsJsonObject("metadata");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					if (!((JsonElement)entry.getValue()).isJsonNull()) {
						backup.metadata.put(format((String)entry.getKey()), ((JsonElement)entry.getValue()).getAsString());
					}
				}
			}
		} catch (Exception var7) {
			LOGGER.error("Could not parse Backup: " + var7.getMessage());
		}

		return backup;
	}

	private static String format(String string) {
		String[] strings = string.split("_");
		StringBuilder stringBuilder = new StringBuilder();

		for (String string2 : strings) {
			if (string2 != null && string2.length() >= 1) {
				if ("of".equals(string2)) {
					stringBuilder.append(string2).append(" ");
				} else {
					char c = Character.toUpperCase(string2.charAt(0));
					stringBuilder.append(c).append(string2.substring(1, string2.length())).append(" ");
				}
			}
		}

		return stringBuilder.toString();
	}

	public boolean isUploadedVersion() {
		return this.uploadedVersion;
	}

	public void setUploadedVersion(boolean bl) {
		this.uploadedVersion = bl;
	}
}
