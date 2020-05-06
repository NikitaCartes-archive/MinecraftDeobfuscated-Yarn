/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.UUID;
import net.minecraft.class_5269;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.timer.Timer;

public interface class_5268
extends class_5269 {
    public String getLevelName();

    public void setThundering(boolean var1);

    public int getRainTime();

    public void setRainTime(int var1);

    public void setThunderTime(int var1);

    public int getThunderTime();

    @Override
    default public void populateCrashReport(CrashReportSection crashReportSection) {
        class_5269.super.populateCrashReport(crashReportSection);
        crashReportSection.add("Level name", this::getLevelName);
        crashReportSection.add("Level game mode", () -> String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameMode().getName(), this.getGameMode().getId(), this.isHardcore(), this.areCommandsAllowed()));
        crashReportSection.add("Level generator", () -> {
            LevelGeneratorType levelGeneratorType = this.getGeneratorOptions().getType();
            return String.format("ID %02d - %s, ver %d. Features enabled: %b", levelGeneratorType.getId(), levelGeneratorType.getName(), levelGeneratorType.getVersion(), this.hasStructures());
        });
        crashReportSection.add("Level weather", () -> String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering()));
    }

    public int getClearWeatherTime();

    public void setClearWeatherTime(int var1);

    public boolean hasStructures();

    public CompoundTag getWorldData();

    public void setWorldData(CompoundTag var1);

    public int getWanderingTraderSpawnDelay();

    public void setWanderingTraderSpawnDelay(int var1);

    public int getWanderingTraderSpawnChance();

    public void setWanderingTraderSpawnChance(int var1);

    public void setWanderingTraderId(UUID var1);

    public GameMode getGameMode();

    public void method_27415(WorldBorder.class_5200 var1);

    public WorldBorder.class_5200 method_27422();

    public boolean isInitialized();

    public void setInitialized(boolean var1);

    public boolean areCommandsAllowed();

    public void setGameMode(GameMode var1);

    public Timer<MinecraftServer> getScheduledEvents();
}

