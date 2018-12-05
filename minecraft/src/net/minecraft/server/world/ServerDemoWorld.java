package net.minecraft.server.world;

import net.minecraft.class_37;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Profiler;
import net.minecraft.world.GameMode;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

public class ServerDemoWorld extends ServerWorld {
	private static final long seed = (long)"North Carolina".hashCode();
	public static final LevelInfo INFO = new LevelInfo(seed, GameMode.field_9215, true, false, LevelGeneratorType.DEFAULT).setBonusChest();

	public ServerDemoWorld(
		MinecraftServer minecraftServer,
		WorldSaveHandler worldSaveHandler,
		class_37 arg,
		LevelProperties levelProperties,
		DimensionType dimensionType,
		Profiler profiler
	) {
		super(minecraftServer, worldSaveHandler, arg, levelProperties, dimensionType, profiler);
		this.properties.method_140(INFO);
	}
}
