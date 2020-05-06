/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5268;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import org.jetbrains.annotations.Nullable;

public interface class_5219 {
    @Environment(value=EnvType.CLIENT)
    public int getVersionId();

    @Environment(value=EnvType.CLIENT)
    public boolean isVersionSnapshot();

    @Environment(value=EnvType.CLIENT)
    public String getVersionName();

    public Set<String> getDisabledDataPacks();

    public Set<String> getEnabledDataPacks();

    public boolean isModded();

    public Set<String> getServerBrands();

    public void addServerBrand(String var1, boolean var2);

    default public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Known server brands", () -> String.join((CharSequence)", ", this.getServerBrands()));
        crashReportSection.add("Level was modded", () -> Boolean.toString(this.isModded()));
        crashReportSection.add("Level storage version", () -> {
            int i = this.getVersion();
            return String.format("0x%05X - %s", i, this.getFormatName(i));
        });
    }

    default public String getFormatName(int id) {
        switch (id) {
            case 19133: {
                return "Anvil";
            }
            case 19132: {
                return "McRegion";
            }
        }
        return "Unknown?";
    }

    @Nullable
    public CompoundTag getCustomBossEvents();

    public void setCustomBossEvents(@Nullable CompoundTag var1);

    public class_5268 method_27859();

    public LevelInfo getLevelInfo();

    public CompoundTag cloneWorldTag(@Nullable CompoundTag var1);

    public boolean isHardcore();

    public int getVersion();

    public String getLevelName();

    public GameMode getGameMode();

    public void setGameMode(GameMode var1);

    @Environment(value=EnvType.CLIENT)
    public long getLastPlayed();

    public long getSeed();

    public boolean areCommandsAllowed();

    public Difficulty getDifficulty();

    public void setDifficulty(Difficulty var1);

    public boolean isDifficultyLocked();

    public void setDifficultyLocked(boolean var1);

    public GameRules getGameRules();

    public CompoundTag getPlayerData();

    public CompoundTag getWorldData(DimensionType var1);

    public void setWorldData(DimensionType var1, CompoundTag var2);
}

