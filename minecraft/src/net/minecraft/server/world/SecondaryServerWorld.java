package net.minecraft.server.world;

import java.util.concurrent.Executor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;

public class SecondaryServerWorld extends ServerWorld {
	public SecondaryServerWorld(
		ServerWorld world,
		MinecraftServer server,
		Executor workerExecutor,
		LevelStorage.Session session,
		DimensionType dimensionType,
		WorldGenerationProgressListener worldGenerationProgressListener
	) {
		super(
			server,
			workerExecutor,
			session,
			new UnmodifiableLevelProperties(dimensionType, server.method_27728(), world.getLevelProperties()),
			dimensionType,
			worldGenerationProgressListener
		);
		world.getWorldBorder().addListener(new WorldBorderListener.WorldBorderSyncer(this.getWorldBorder()));
	}

	@Override
	protected void tickTime() {
	}
}
