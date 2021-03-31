package net.minecraft.world.level.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.SharedConstants;

public class SaveVersionInfo {
	private final int levelFormatVersion;
	private final long lastPlayed;
	private final String versionName;
	private final int versionId;
	private final boolean stable;

	public SaveVersionInfo(int levelFormatVersion, long lastPlayed, String versionName, int versionId, boolean stable) {
		this.levelFormatVersion = levelFormatVersion;
		this.lastPlayed = lastPlayed;
		this.versionName = versionName;
		this.versionId = versionId;
		this.stable = stable;
	}

	public static SaveVersionInfo fromDynamic(Dynamic<?> dynamic) {
		int i = dynamic.get("version").asInt(0);
		long l = dynamic.get("LastPlayed").asLong(0L);
		OptionalDynamic<?> optionalDynamic = dynamic.get("Version");
		return optionalDynamic.result().isPresent()
			? new SaveVersionInfo(
				i,
				l,
				optionalDynamic.get("Name").asString(SharedConstants.getGameVersion().getName()),
				optionalDynamic.get("Id").asInt(SharedConstants.getGameVersion().getWorldVersion()),
				optionalDynamic.get("Snapshot").asBoolean(!SharedConstants.getGameVersion().isStable())
			)
			: new SaveVersionInfo(i, l, "", 0, false);
	}

	public int getLevelFormatVersion() {
		return this.levelFormatVersion;
	}

	public long getLastPlayed() {
		return this.lastPlayed;
	}

	public String getVersionName() {
		return this.versionName;
	}

	public int getVersionId() {
		return this.versionId;
	}

	public boolean isStable() {
		return this.stable;
	}
}
