package com.mojang.realmsclient.util;

import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.CheckedGson;
import net.minecraft.realms.RealmsSerializable;
import org.apache.commons.io.FileUtils;

@Environment(EnvType.CLIENT)
public class RealmsPersistence {
	private static final CheckedGson CHECKED_GSON = new CheckedGson();

	public static RealmsPersistence.RealmsPersistenceData readFile() {
		File file = getFile();

		try {
			return CHECKED_GSON.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8), RealmsPersistence.RealmsPersistenceData.class);
		} catch (IOException var2) {
			return new RealmsPersistence.RealmsPersistenceData();
		}
	}

	public static void writeFile(RealmsPersistence.RealmsPersistenceData data) {
		File file = getFile();

		try {
			FileUtils.writeStringToFile(file, CHECKED_GSON.toJson(data), StandardCharsets.UTF_8);
		} catch (IOException var3) {
		}
	}

	private static File getFile() {
		return new File(MinecraftClient.getInstance().runDirectory, "realms_persistence.json");
	}

	@Environment(EnvType.CLIENT)
	public static class RealmsPersistenceData implements RealmsSerializable {
		@SerializedName("newsLink")
		public String newsLink;
		@SerializedName("hasUnreadNews")
		public boolean hasUnreadNews;
	}
}
