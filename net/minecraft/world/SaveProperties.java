/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.mojang.serialization.Lifecycle;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.Nullable;

public interface SaveProperties {
    public DataPackSettings method_29589();

    public void method_29590(DataPackSettings var1);

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

    public ServerWorldProperties getMainWorldProperties();

    @Environment(value=EnvType.CLIENT)
    public LevelInfo getLevelInfo();

    public CompoundTag cloneWorldTag(RegistryTracker var1, @Nullable CompoundTag var2);

    public boolean isHardcore();

    public int getVersion();

    public String getLevelName();

    public GameMode getGameMode();

    public void setGameMode(GameMode var1);

    public boolean areCommandsAllowed();

    public Difficulty getDifficulty();

    public void setDifficulty(Difficulty var1);

    public boolean isDifficultyLocked();

    public void setDifficultyLocked(boolean var1);

    public GameRules getGameRules();

    public CompoundTag getPlayerData();

    public CompoundTag method_29036();

    public void method_29037(CompoundTag var1);

    public GeneratorOptions getGeneratorOptions();

    @Environment(value=EnvType.CLIENT)
    public Lifecycle method_29588();
}

