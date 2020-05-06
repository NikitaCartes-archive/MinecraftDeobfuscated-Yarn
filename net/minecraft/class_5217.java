/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.hash.Hashing;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;

public interface class_5217 {
    public long getSeed();

    public static long method_27418(long l) {
        return Hashing.sha256().hashLong(l).asLong();
    }

    public int getSpawnX();

    public int getSpawnY();

    public int getSpawnZ();

    public long getTime();

    public long getTimeOfDay();

    public boolean isThundering();

    public boolean isRaining();

    public void setRaining(boolean var1);

    public boolean isHardcore();

    public LevelGeneratorType getGeneratorType();

    public LevelGeneratorOptions getGeneratorOptions();

    public GameRules getGameRules();

    public Difficulty getDifficulty();

    public boolean isDifficultyLocked();

    default public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Level seed", () -> String.valueOf(this.getSeed()));
        crashReportSection.add("Level generator options", () -> this.getGeneratorOptions().getDynamic().toString());
        crashReportSection.add("Level spawn location", () -> CrashReportSection.createPositionString(this.getSpawnX(), this.getSpawnY(), this.getSpawnZ()));
        crashReportSection.add("Level time", () -> String.format("%d game time, %d day time", this.getTime(), this.getTimeOfDay()));
    }
}

