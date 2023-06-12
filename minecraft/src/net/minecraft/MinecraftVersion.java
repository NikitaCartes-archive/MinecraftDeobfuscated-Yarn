package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

public class MinecraftVersion implements GameVersion {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final GameVersion CURRENT = new MinecraftVersion();
	private final String id;
	private final String name;
	private final boolean stable;
	private final SaveVersion saveVersion;
	private final int protocolVersion;
	private final int resourcePackVersion;
	private final int dataPackVersion;
	private final Date buildTime;

	private MinecraftVersion() {
		this.id = UUID.randomUUID().toString().replaceAll("-", "");
		this.name = "1.20.1";
		this.stable = true;
		this.saveVersion = new SaveVersion(3465, "main");
		this.protocolVersion = SharedConstants.getProtocolVersion();
		this.resourcePackVersion = 15;
		this.dataPackVersion = 15;
		this.buildTime = new Date();
	}

	private MinecraftVersion(JsonObject json) {
		this.id = JsonHelper.getString(json, "id");
		this.name = JsonHelper.getString(json, "name");
		this.stable = JsonHelper.getBoolean(json, "stable");
		this.saveVersion = new SaveVersion(JsonHelper.getInt(json, "world_version"), JsonHelper.getString(json, "series_id", SaveVersion.MAIN_SERIES));
		this.protocolVersion = JsonHelper.getInt(json, "protocol_version");
		JsonObject jsonObject = JsonHelper.getObject(json, "pack_version");
		this.resourcePackVersion = JsonHelper.getInt(jsonObject, "resource");
		this.dataPackVersion = JsonHelper.getInt(jsonObject, "data");
		this.buildTime = Date.from(ZonedDateTime.parse(JsonHelper.getString(json, "build_time")).toInstant());
	}

	public static GameVersion create() {
		try {
			InputStream inputStream = MinecraftVersion.class.getResourceAsStream("/version.json");

			GameVersion var9;
			label63: {
				MinecraftVersion var2;
				try {
					if (inputStream == null) {
						LOGGER.warn("Missing version information!");
						var9 = CURRENT;
						break label63;
					}

					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

					try {
						var2 = new MinecraftVersion(JsonHelper.deserialize(inputStreamReader));
					} catch (Throwable var6) {
						try {
							inputStreamReader.close();
						} catch (Throwable var5) {
							var6.addSuppressed(var5);
						}

						throw var6;
					}

					inputStreamReader.close();
				} catch (Throwable var7) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var4) {
							var7.addSuppressed(var4);
						}
					}

					throw var7;
				}

				if (inputStream != null) {
					inputStream.close();
				}

				return var2;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var9;
		} catch (JsonParseException | IOException var8) {
			throw new IllegalStateException("Game version information is corrupt", var8);
		}
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public SaveVersion getSaveVersion() {
		return this.saveVersion;
	}

	@Override
	public int getProtocolVersion() {
		return this.protocolVersion;
	}

	@Override
	public int getResourceVersion(ResourceType type) {
		return type == ResourceType.SERVER_DATA ? this.dataPackVersion : this.resourcePackVersion;
	}

	@Override
	public Date getBuildTime() {
		return this.buildTime;
	}

	@Override
	public boolean isStable() {
		return this.stable;
	}
}
