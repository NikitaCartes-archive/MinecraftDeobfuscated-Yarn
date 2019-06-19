package net.minecraft.server.world;

import java.util.concurrent.Executor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.UnmodifiableLevelProperties;

public class SecondaryServerWorld extends ServerWorld {
	public SecondaryServerWorld(
		ServerWorld serverWorld,
		MinecraftServer minecraftServer,
		Executor executor,
		WorldSaveHandler worldSaveHandler,
		DimensionType dimensionType,
		Profiler profiler,
		WorldGenerationProgressListener worldGenerationProgressListener
	) {
		super(
			minecraftServer,
			executor,
			worldSaveHandler,
			new UnmodifiableLevelProperties(serverWorld.getLevelProperties()),
			dimensionType,
			profiler,
			worldGenerationProgressListener
		);
		serverWorld.getWorldBorder().addListener(new WorldBorderListener.WorldBorderSyncer(this.getWorldBorder()));
	}

	@Override
	protected void tickTime() {
	}
}
