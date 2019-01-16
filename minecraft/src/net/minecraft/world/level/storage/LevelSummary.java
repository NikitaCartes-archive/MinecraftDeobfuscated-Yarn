package net.minecraft.world.level.storage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
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

	public LevelSummary(LevelProperties levelProperties, String string, String string2, long l, boolean bl) {
		this.name = string;
		this.displayName = string2;
		this.lastPlayed = levelProperties.getLastPlayed();
		this.getSizeOnDisk = l;
		this.gameMode = levelProperties.getGameMode();
		this.requiresConversion = bl;
		this.isHardcore = levelProperties.isHardcore();
		this.commandsAllowed = levelProperties.areCommandsAllowed();
		this.versionName = levelProperties.getVersionName();
		this.versionId = levelProperties.getVersionId();
		this.isSnapshot = levelProperties.isVersionSnapshot();
		this.generatorType = levelProperties.getGeneratorType();
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

	public long lastPlayed() {
		return this.lastPlayed;
	}

	public int compareTo(LevelSummary levelSummary) {
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

	public boolean areCommandsAllowed() {
		return this.commandsAllowed;
	}

	public TextComponent getVersionTextComponent() {
		return (TextComponent)(ChatUtil.isEmpty(this.versionName)
			? new TranslatableTextComponent("selectWorld.versionUnknown")
			: new StringTextComponent(this.versionName));
	}

	public boolean method_256() {
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
