package net.minecraft.client.realms.util;

import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import org.apache.commons.io.FileUtils;

@Environment(EnvType.CLIENT)
public class RealmsPersistence {
	private static final String FILE_NAME = "realms_persistence.json";
	private static final CheckedGson CHECKED_GSON = new CheckedGson();

	public RealmsPersistence.RealmsPersistenceData load() {
		return readFile();
	}

	public void save(RealmsPersistence.RealmsPersistenceData data) {
		writeFile(data);
	}

	public static RealmsPersistence.RealmsPersistenceData readFile() {
		File file = getFile();

		try {
			String string = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			RealmsPersistence.RealmsPersistenceData realmsPersistenceData = CHECKED_GSON.fromJson(string, RealmsPersistence.RealmsPersistenceData.class);
			return realmsPersistenceData != null ? realmsPersistenceData : new RealmsPersistence.RealmsPersistenceData();
		} catch (IOException var3) {
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
