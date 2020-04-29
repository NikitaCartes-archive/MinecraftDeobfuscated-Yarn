package net.minecraft.world.level.storage;

import java.io.File;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5219;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;

@Environment(EnvType.CLIENT)
public class LevelSummary implements Comparable<LevelSummary> {
	private final String name;
	private final String displayName;
	private final long lastPlayed;
	private final long getSizeOnDisk;
	private final boolean requiresConversion;
	private final GameMode gameMode;
	private final boolean hardcore;
	private final boolean commandsAllowed;
	private final String versionName;
	private final int versionId;
	private final boolean snapshot;
	private final LevelGeneratorType generatorType;
	private final boolean locked;
	private final File file;
	@Nullable
	private Text field_24191;

	public LevelSummary(class_5219 arg, String name, String displayName, long size, boolean requiresConversion, boolean locked, File file) {
		this.name = name;
		this.displayName = displayName;
		this.locked = locked;
		this.file = file;
		this.lastPlayed = arg.getLastPlayed();
		this.getSizeOnDisk = size;
		this.gameMode = arg.getGameMode();
		this.requiresConversion = requiresConversion;
		this.hardcore = arg.isHardcore();
		this.commandsAllowed = arg.areCommandsAllowed();
		this.versionName = arg.getVersionName();
		this.versionId = arg.getVersionId();
		this.snapshot = arg.isVersionSnapshot();
		this.generatorType = arg.method_27859().getGeneratorType();
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public File getFile() {
		return this.file;
	}

	public boolean requiresConversion() {
		return this.requiresConversion;
	}

	public long getLastPlayed() {
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
		return this.hardcore;
	}

	public boolean hasCheats() {
		return this.commandsAllowed;
	}

	public MutableText getVersion() {
		return (MutableText)(ChatUtil.isEmpty(this.versionName) ? new TranslatableText("selectWorld.versionUnknown") : new LiteralText(this.versionName));
	}

	public boolean isDifferentVersion() {
		return this.isFutureLevel() || !SharedConstants.getGameVersion().isStable() && !this.snapshot || this.isOutdatedLevel() || this.isLegacyCustomizedWorld();
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

	public boolean isLocked() {
		return this.locked;
	}

	public Text method_27429() {
		if (this.field_24191 == null) {
			this.field_24191 = this.method_27430();
		}

		return this.field_24191;
	}

	private Text method_27430() {
		if (this.isLocked()) {
			return new TranslatableText("selectWorld.locked").formatted(Formatting.RED);
		} else if (this.requiresConversion()) {
			return new TranslatableText("selectWorld.conversion");
		} else {
			MutableText mutableText = (MutableText)(this.isHardcore()
				? new LiteralText("").append(new TranslatableText("gameMode.hardcore").formatted(Formatting.DARK_RED))
				: new TranslatableText("gameMode." + this.getGameMode().getName()));
			if (this.hasCheats()) {
				mutableText.append(", ").append(new TranslatableText("selectWorld.cheats"));
			}

			MutableText mutableText2 = this.getVersion();
			MutableText mutableText3 = new LiteralText(", ").append(new TranslatableText("selectWorld.version")).append(" ");
			if (this.isDifferentVersion()) {
				mutableText3.append(mutableText2.formatted(this.isFutureLevel() ? Formatting.RED : Formatting.ITALIC));
			} else {
				mutableText3.append(mutableText2);
			}

			mutableText.append(mutableText3);
			return mutableText;
		}
	}
}
