package net.minecraft.world.level.storage;

import java.io.File;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import org.apache.commons.lang3.StringUtils;

public class LevelSummary implements Comparable<LevelSummary> {
	private final LevelInfo levelInfo;
	private final SaveVersionInfo field_25023;
	private final String name;
	private final boolean requiresConversion;
	private final boolean locked;
	private final File file;
	@Nullable
	@Environment(EnvType.CLIENT)
	private Text field_24191;

	public LevelSummary(LevelInfo levelInfo, SaveVersionInfo saveVersionInfo, String string, boolean bl, boolean bl2, File file) {
		this.levelInfo = levelInfo;
		this.field_25023 = saveVersionInfo;
		this.name = string;
		this.locked = bl2;
		this.file = file;
		this.requiresConversion = bl;
	}

	@Environment(EnvType.CLIENT)
	public String getName() {
		return this.name;
	}

	@Environment(EnvType.CLIENT)
	public String getDisplayName() {
		return StringUtils.isEmpty(this.levelInfo.getLevelName()) ? this.name : this.levelInfo.getLevelName();
	}

	@Environment(EnvType.CLIENT)
	public File getFile() {
		return this.file;
	}

	@Environment(EnvType.CLIENT)
	public boolean requiresConversion() {
		return this.requiresConversion;
	}

	@Environment(EnvType.CLIENT)
	public long getLastPlayed() {
		return this.field_25023.getLastPlayed();
	}

	public int method_251(LevelSummary levelSummary) {
		if (this.field_25023.getLastPlayed() < levelSummary.field_25023.getLastPlayed()) {
			return 1;
		} else {
			return this.field_25023.getLastPlayed() > levelSummary.field_25023.getLastPlayed() ? -1 : this.name.compareTo(levelSummary.name);
		}
	}

	@Environment(EnvType.CLIENT)
	public GameMode getGameMode() {
		return this.levelInfo.getGameMode();
	}

	@Environment(EnvType.CLIENT)
	public boolean isHardcore() {
		return this.levelInfo.isHardcore();
	}

	@Environment(EnvType.CLIENT)
	public boolean hasCheats() {
		return this.levelInfo.areCommandsAllowed();
	}

	@Environment(EnvType.CLIENT)
	public MutableText getVersion() {
		return (MutableText)(ChatUtil.isEmpty(this.field_25023.getVersionName())
			? new TranslatableText("selectWorld.versionUnknown")
			: new LiteralText(this.field_25023.getVersionName()));
	}

	public SaveVersionInfo method_29586() {
		return this.field_25023;
	}

	@Environment(EnvType.CLIENT)
	public boolean isDifferentVersion() {
		return this.isFutureLevel() || !SharedConstants.getGameVersion().isStable() && !this.field_25023.isStable() || this.isOutdatedLevel();
	}

	@Environment(EnvType.CLIENT)
	public boolean isFutureLevel() {
		return this.field_25023.getVersionId() > SharedConstants.getGameVersion().getWorldVersion();
	}

	@Environment(EnvType.CLIENT)
	public boolean isOutdatedLevel() {
		return this.field_25023.getVersionId() < SharedConstants.getGameVersion().getWorldVersion();
	}

	@Environment(EnvType.CLIENT)
	public boolean isLocked() {
		return this.locked;
	}

	@Environment(EnvType.CLIENT)
	public Text method_27429() {
		if (this.field_24191 == null) {
			this.field_24191 = this.method_27430();
		}

		return this.field_24191;
	}

	@Environment(EnvType.CLIENT)
	private Text method_27430() {
		if (this.isLocked()) {
			return new TranslatableText("selectWorld.locked").formatted(Formatting.field_1061);
		} else if (this.requiresConversion()) {
			return new TranslatableText("selectWorld.conversion");
		} else {
			MutableText mutableText = (MutableText)(this.isHardcore()
				? new LiteralText("").append(new TranslatableText("gameMode.hardcore").formatted(Formatting.field_1079))
				: new TranslatableText("gameMode." + this.getGameMode().getName()));
			if (this.hasCheats()) {
				mutableText.append(", ").append(new TranslatableText("selectWorld.cheats"));
			}

			MutableText mutableText2 = this.getVersion();
			MutableText mutableText3 = new LiteralText(", ").append(new TranslatableText("selectWorld.version")).append(" ");
			if (this.isDifferentVersion()) {
				mutableText3.append(mutableText2.formatted(this.isFutureLevel() ? Formatting.field_1061 : Formatting.field_1056));
			} else {
				mutableText3.append(mutableText2);
			}

			mutableText.append(mutableText3);
			return mutableText;
		}
	}
}
