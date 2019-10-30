package net.minecraft.world.level.storage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;

@Environment(EnvType.CLIENT)
public class LevelSummary implements Comparable<LevelSummary> {
	private final String name;
	private final String displayName;
	private final long lastPlayed;
	private final long getSizeOnDisk;
	private final boolean requiresConversion;
	private final GameMode gameMode;
	private final boolean isHardcore;
	private final boolean commandsAllowed;
	private final String versionName;
	private final int versionId;
	private final boolean isSnapshot;
	private final LevelGeneratorType generatorType;

	public LevelSummary(LevelProperties properties, String name, String displayName, long size, boolean requiresConversion) {
		this.name = name;
		this.displayName = displayName;
		this.lastPlayed = properties.getLastPlayed();
		this.getSizeOnDisk = size;
		this.gameMode = properties.getGameMode();
		this.requiresConversion = requiresConversion;
		this.isHardcore = properties.isHardcore();
		this.commandsAllowed = properties.areCommandsAllowed();
		this.versionName = properties.getVersionName();
		this.versionId = properties.getVersionId();
		this.isSnapshot = properties.isVersionSnapshot();
		this.generatorType = properties.getGeneratorType();
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public long getSizeOnDisk() {
		return this.getSizeOnDisk;
	}

	public boolean requiresConversion() {
		return this.requiresConversion;
	}

	public long getLastPlayed() {
		return this.lastPlayed;
	}

	public int method_251(LevelSummary levelSummary) {
		if (this.lastPlayed < levelSummary.lastPlayed) {
			return 1;
		} else {
			return this.lastPlayed > levelSummary.lastPlayed ? -1 : this.name.compareTo(levelSummary.name);
		}
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean isHardcore() {
		return this.isHardcore;
	}

	public boolean hasCheats() {
		return this.commandsAllowed;
	}

	public Text getVersion() {
		return (Text)(ChatUtil.isEmpty(this.versionName) ? new TranslatableText("selectWorld.versionUnknown") : new LiteralText(this.versionName));
	}

	public boolean isDifferentVersion() {
		return this.isFutureLevel() || !SharedConstants.getGameVersion().isStable() && !this.isSnapshot || this.isOutdatedLevel() || this.isLegacyCustomizedWorld();
	}

	public boolean isFutureLevel() {
		return this.versionId > SharedConstants.getGameVersion().getWorldVersion();
	}

	public boolean isLegacyCustomizedWorld() {
		return this.generatorType == LevelGeneratorType.CUSTOMIZED && this.versionId < 1466;
	}

	public boolean isOutdatedLevel() {
		return this.versionId < SharedConstants.getGameVersion().getWorldVersion();
	}
}
