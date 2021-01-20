package net.minecraft.world.level.storage;

import com.mojang.bridge.game.GameVersion;
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
	private final SaveVersionInfo versionInfo;
	private final String name;
	private final boolean requiresConversion;
	private final boolean locked;
	private final File file;
	@Nullable
	@Environment(EnvType.CLIENT)
	private Text details;

	public LevelSummary(LevelInfo levelInfo, SaveVersionInfo versionInfo, String name, boolean requiresConversion, boolean locked, File file) {
		this.levelInfo = levelInfo;
		this.versionInfo = versionInfo;
		this.name = name;
		this.locked = locked;
		this.file = file;
		this.requiresConversion = requiresConversion;
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
		return this.versionInfo.getLastPlayed();
	}

	public int compareTo(LevelSummary levelSummary) {
		if (this.versionInfo.getLastPlayed() < levelSummary.versionInfo.getLastPlayed()) {
			return 1;
		} else {
			return this.versionInfo.getLastPlayed() > levelSummary.versionInfo.getLastPlayed() ? -1 : this.name.compareTo(levelSummary.name);
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
		return (MutableText)(ChatUtil.isEmpty(this.versionInfo.getVersionName())
			? new TranslatableText("selectWorld.versionUnknown")
			: new LiteralText(this.versionInfo.getVersionName()));
	}

	public SaveVersionInfo getVersionInfo() {
		return this.versionInfo;
	}

	@Environment(EnvType.CLIENT)
	public boolean isDifferentVersion() {
		return this.isFutureLevel() || !SharedConstants.getGameVersion().isStable() && !this.versionInfo.isStable() || this.method_33405().method_33406();
	}

	@Environment(EnvType.CLIENT)
	public boolean isFutureLevel() {
		return this.versionInfo.getVersionId() > SharedConstants.getGameVersion().getWorldVersion();
	}

	@Environment(EnvType.CLIENT)
	public LevelSummary.class_5781 method_33405() {
		GameVersion gameVersion = SharedConstants.getGameVersion();
		int i = gameVersion.getWorldVersion();
		int j = this.versionInfo.getVersionId();
		if (!gameVersion.isStable() && j < i) {
			return LevelSummary.class_5781.field_28439;
		} else {
			return j > i ? LevelSummary.class_5781.field_28438 : LevelSummary.class_5781.field_28437;
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean isLocked() {
		return this.locked;
	}

	@Environment(EnvType.CLIENT)
	public Text getDetails() {
		if (this.details == null) {
			this.details = this.createDetails();
		}

		return this.details;
	}

	@Environment(EnvType.CLIENT)
	private Text createDetails() {
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

	@Environment(EnvType.CLIENT)
	public static enum class_5781 {
		field_28437(false, false, ""),
		field_28438(true, true, "downgrade"),
		field_28439(true, false, "snapshot");

		private final boolean field_28440;
		private final boolean field_28441;
		private final String field_28442;

		private class_5781(boolean bl, boolean bl2, String string2) {
			this.field_28440 = bl;
			this.field_28441 = bl2;
			this.field_28442 = string2;
		}

		public boolean method_33406() {
			return this.field_28440;
		}

		public boolean method_33407() {
			return this.field_28441;
		}

		public String method_33408() {
			return this.field_28442;
		}
	}
}
