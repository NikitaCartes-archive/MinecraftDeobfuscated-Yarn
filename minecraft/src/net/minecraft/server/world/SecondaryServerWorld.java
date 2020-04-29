package net.minecraft.server.world;

import java.util.concurrent.Executor;
import net.minecraft.class_5268;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;

public class SecondaryServerWorld extends ServerWorld {
	public SecondaryServerWorld(
		ServerWorld serverWorld,
		class_5268 arg,
		MinecraftServer minecraftServer,
		Executor executor,
		LevelStorage.Session session,
		DimensionType dimensionType,
		WorldGenerationProgressListener worldGenerationProgressListener
	) {
		super(
			minecraftServer,
			executor,
			session,
			new UnmodifiableLevelProperties(dimensionType, minecraftServer.method_27728(), arg),
			dimensionType,
			worldGenerationProgressListener
		);
		serverWorld.getWorldBorder().addListener(new WorldBorderListener.WorldBorderSyncer(this.getWorldBorder()));
	}

	@Override
	protected void tickTime() {
	}
}
