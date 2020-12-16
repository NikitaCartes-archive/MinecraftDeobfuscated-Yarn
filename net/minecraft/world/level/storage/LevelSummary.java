/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level.storage;

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

    public LevelSummary(LevelInfo levelInfo, SaveVersionInfo saveVersionInfo, String string, boolean bl, boolean bl2, File file) {
        this.levelInfo = levelInfo;
        this.versionInfo = saveVersionInfo;
        this.name = string;
        this.locked = bl2;
        this.file = file;
        this.requiresConversion = bl;
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

    public SaveVersionInfo method_29586() {
        return this.versionInfo;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isDifferentVersion() {
        return this.isFutureLevel() || !SharedConstants.getGameVersion().isStable() && !this.versionInfo.isStable() || this.isOutdatedLevel();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFutureLevel() {
        return this.versionInfo.getVersionId() > SharedConstants.getGameVersion().getWorldVersion();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isOutdatedLevel() {
        return this.versionInfo.getVersionId() < SharedConstants.getGameVersion().getWorldVersion();
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
}

