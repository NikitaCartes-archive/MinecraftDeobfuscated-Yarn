/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.HeightLimitView;

public interface WorldProperties {
    public int getSpawnX();

    public int getSpawnY();

    public int getSpawnZ();

    public float getSpawnAngle();

    public long getTime();

    public long getTimeOfDay();

    public boolean isThundering();

    public boolean isRaining();

    public void setRaining(boolean var1);

    public boolean isHardcore();

    public GameRules getGameRules();

    public Difficulty getDifficulty();

    public boolean isDifficultyLocked();

    default public void populateCrashReport(CrashReportSection reportSection, HeightLimitView heightLimitView) {
        reportSection.add("Level spawn location", () -> CrashReportSection.createPositionString(heightLimitView, this.getSpawnX(), this.getSpawnY(), this.getSpawnZ()));
        reportSection.add("Level time", () -> String.format("%d game time, %d day time", this.getTime(), this.getTimeOfDay()));
    }
}

