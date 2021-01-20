/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level.storage;

import com.mojang.bridge.game.GameVersion;
import java.io.File;
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
import net.minecraft.world.level.storage.SaveVersionInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class LevelSummary
implements Comparable<LevelSummary> {
    private final LevelInfo levelInfo;
    private final SaveVersionInfo versionInfo;
    private final String name;
    private final boolean requiresConversion;
    private final boolean locked;
    private final File file;
    @Nullable
    @Environment(value=EnvType.CLIENT)
    private Text details;

    public LevelSummary(LevelInfo levelInfo, SaveVersionInfo versionInfo, String name, boolean requiresConversion, boolean locked, File file) {
        this.levelInfo = levelInfo;
        this.versionInfo = versionInfo;
        this.name = name;
        this.locked = locked;
        this.file = file;
        this.requiresConversion = requiresConversion;
    }

    @Environment(value=EnvType.CLIENT)
    public String getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public String getDisplayName() {
        return StringUtils.isEmpty(this.levelInfo.getLevelName()) ? this.name : this.levelInfo.getLevelName();
    }

    @Environment(value=EnvType.CLIENT)
    public File getFile() {
        return this.file;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean requiresConversion() {
        return this.requiresConversion;
    }

    @Environment(value=EnvType.CLIENT)
    public long getLastPlayed() {
        return this.versionInfo.getLastPlayed();
    }

    @Override
    public int compareTo(LevelSummary levelSummary) {
        if (this.versionInfo.getLastPlayed() < levelSummary.versionInfo.getLastPlayed()) {
            return 1;
        }
        if (this.versionInfo.getLastPlayed() > levelSummary.versionInfo.getLastPlayed()) {
            return -1;
        }
        return this.name.compareTo(levelSummary.name);
    }

    @Environment(value=EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.levelInfo.getGameMode();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isHardcore() {
        return this.levelInfo.isHardcore();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasCheats() {
        return this.levelInfo.areCommandsAllowed();
    }

    @Environment(value=EnvType.CLIENT)
    public MutableText getVersion() {
        if (ChatUtil.isEmpty(this.versionInfo.getVersionName())) {
            return new TranslatableText("selectWorld.versionUnknown");
        }
        return new LiteralText(this.versionInfo.getVersionName());
    }

    public SaveVersionInfo getVersionInfo() {
        return this.versionInfo;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isDifferentVersion() {
        return this.isFutureLevel() || !SharedConstants.getGameVersion().isStable() && !this.versionInfo.isStable() || this.method_33405().method_33406();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFutureLevel() {
        return this.versionInfo.getVersionId() > SharedConstants.getGameVersion().getWorldVersion();
    }

    @Environment(value=EnvType.CLIENT)
    public class_5781 method_33405() {
        GameVersion gameVersion = SharedConstants.getGameVersion();
        int i = gameVersion.getWorldVersion();
        int j = this.versionInfo.getVersionId();
        if (!gameVersion.isStable() && j < i) {
            return class_5781.field_28439;
        }
        if (j > i) {
            return class_5781.field_28438;
        }
        return class_5781.field_28437;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLocked() {
        return this.locked;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDetails() {
        if (this.details == null) {
            this.details = this.createDetails();
        }
        return this.details;
    }

    @Environment(value=EnvType.CLIENT)
    private Text createDetails() {
        MutableText mutableText;
        if (this.isLocked()) {
            return new TranslatableText("selectWorld.locked").formatted(Formatting.RED);
        }
        if (this.requiresConversion()) {
            return new TranslatableText("selectWorld.conversion");
        }
        MutableText mutableText2 = mutableText = this.isHardcore() ? new LiteralText("").append(new TranslatableText("gameMode.hardcore").formatted(Formatting.DARK_RED)) : new TranslatableText("gameMode." + this.getGameMode().getName());
        if (this.hasCheats()) {
            mutableText.append(", ").append(new TranslatableText("selectWorld.cheats"));
        }
        MutableText mutableText22 = this.getVersion();
        MutableText mutableText3 = new LiteralText(", ").append(new TranslatableText("selectWorld.version")).append(" ");
        if (this.isDifferentVersion()) {
            mutableText3.append(mutableText22.formatted(this.isFutureLevel() ? Formatting.RED : Formatting.ITALIC));
        } else {
            mutableText3.append(mutableText22);
        }
        mutableText.append(mutableText3);
        return mutableText;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((LevelSummary)object);
    }

    @Environment(value=EnvType.CLIENT)
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

