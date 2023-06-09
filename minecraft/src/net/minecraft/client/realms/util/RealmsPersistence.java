package net.minecraft.client.realms.util;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsPersistence {
	private static final String FILE_NAME = "realms_persistence.json";
	private static final CheckedGson CHECKED_GSON = new CheckedGson();
	private static final Logger LOGGER = LogUtils.getLogger();

	public RealmsPersistence.RealmsPersistenceData load() {
		return readFile();
	}

	public void save(RealmsPersistence.RealmsPersistenceData data) {
		writeFile(data);
	}

	public static RealmsPersistence.RealmsPersistenceData readFile() {
		Path path = getFile();

		try {
			String string = Files.readString(path, StandardCharsets.UTF_8);
			RealmsPersistence.RealmsPersistenceData realmsPersistenceData = CHECKED_GSON.fromJson(string, RealmsPersistence.RealmsPersistenceData.class);
			if (realmsPersistenceData != null) {
				return realmsPersistenceData;
			}
		} catch (NoSuchFileException var3) {
		} catch (Exception var4) {
			LOGGER.warn("Failed to read Realms storage {}", path, var4);
		}

		return new RealmsPersistence.RealmsPersistenceData();
	}

	public static void writeFile(RealmsPersistence.RealmsPersistenceData data) {
		Path path = getFile();

		try {
			Files.writeString(path, CHECKED_GSON.toJson(data), StandardCharsets.UTF_8);
		} catch (Exception var3) {
		}
	}

	private static Path getFile() {
		return MinecraftClient.getInstance().runDirectory.toPath().resolve("realms_persistence.json");
	}

	@Environment(EnvType.CLIENT)
	public static class RealmsPersistenceData implements RealmsSerializable {
		@SerializedName("newsLink")
		public String newsLink;
		@SerializedName("hasUnreadNews")
		public boolean hasUnreadNews;
	}
}
