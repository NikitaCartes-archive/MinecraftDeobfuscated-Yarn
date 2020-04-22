/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5217;
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

    public boolean method_27431();

    public Set<String> method_27432();

    public void addServerBrand(String var1, boolean var2);

    default public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Known server brands", () -> String.join((CharSequence)", ", this.method_27432()));
        crashReportSection.add("Level was modded", () -> Boolean.toString(this.method_27431()));
        crashReportSection.add("Level storage version", () -> {
            int i = this.getVersion();
            return String.format("0x%05X - %s", i, this.method_27440(i));
        });
    }

    default public String method_27440(int i) {
        switch (i) {
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

    public class_5217 method_27437(DimensionType var1);

    public LevelInfo method_27433();

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

    public CompoundTag method_27434(DimensionType var1);

    public void method_27435(DimensionType var1, CompoundTag var2);
}

