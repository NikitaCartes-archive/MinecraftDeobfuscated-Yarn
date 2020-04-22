/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.hash.Hashing;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.timer.Timer;

public interface class_5217 {
    public long getSeed();

    public static long method_27418(long l) {
        return Hashing.sha256().hashLong(l).asLong();
    }

    public int getSpawnX();

    public void method_27416(int var1);

    public int getSpawnY();

    public void method_27417(int var1);

    public int getSpawnZ();

    public void method_27419(int var1);

    default public void setSpawnPos(BlockPos blockPos) {
        this.method_27416(blockPos.getX());
        this.method_27417(blockPos.getY());
        this.method_27419(blockPos.getZ());
    }

    public long getTime();

    public void setTime(long var1);

    public long getTimeOfDay();

    public void setTimeOfDay(long var1);

    public String getLevelName();

    public int getClearWeatherTime();

    public void setClearWeatherTime(int var1);

    public boolean isThundering();

    public void setThundering(boolean var1);

    public int getThunderTime();

    public void setThunderTime(int var1);

    public boolean isRaining();

    public void setRaining(boolean var1);

    public int getRainTime();

    public void setRainTime(int var1);

    public GameMode getGameMode();

    public boolean method_27420();

    public void setGameMode(GameMode var1);

    public boolean isHardcore();

    public LevelGeneratorType getGeneratorType();

    public LevelGeneratorOptions method_27421();

    public boolean areCommandsAllowed();

    public boolean isInitialized();

    public void setInitialized(boolean var1);

    public GameRules getGameRules();

    public WorldBorder.class_5200 method_27422();

    public void method_27415(WorldBorder.class_5200 var1);

    public Difficulty getDifficulty();

    public boolean isDifficultyLocked();

    public Timer<MinecraftServer> getScheduledEvents();

    default public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Level name", this::getLevelName);
        crashReportSection.add("Level seed", () -> String.valueOf(this.getSeed()));
        crashReportSection.add("Level generator", () -> {
            LevelGeneratorType levelGeneratorType = this.method_27421().getType();
            return String.format("ID %02d - %s, ver %d. Features enabled: %b", levelGeneratorType.getId(), levelGeneratorType.getName(), levelGeneratorType.getVersion(), this.method_27420());
        });
        crashReportSection.add("Level generator options", () -> this.method_27421().getDynamic().toString());
        crashReportSection.add("Level spawn location", () -> CrashReportSection.createPositionString(this.getSpawnX(), this.getSpawnY(), this.getSpawnZ()));
        crashReportSection.add("Level time", () -> String.format("%d game time, %d day time", this.getTime(), this.getTimeOfDay()));
        crashReportSection.add("Level weather", () -> String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering()));
        crashReportSection.add("Level game mode", () -> String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameMode().getName(), this.getGameMode().getId(), this.isHardcore(), this.areCommandsAllowed()));
    }

    public CompoundTag getWorldData();

    public void setWorldData(CompoundTag var1);

    public int getWanderingTraderSpawnDelay();

    public void setWanderingTraderSpawnDelay(int var1);

    public int getWanderingTraderSpawnChance();

    public void setWanderingTraderSpawnChance(int var1);

    public void setWanderingTraderId(UUID var1);
}

