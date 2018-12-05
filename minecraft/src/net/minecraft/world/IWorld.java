package net.minecraft.world;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3747;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.Particle;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.command.ManagerCommand;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

public interface IWorld extends ViewableWorld, class_3747, ManagerCommand {
	long getSeed();

	default float method_8391() {
		return Dimension.field_13059[this.getDimension().method_12454(this.getLevelProperties().getTimeOfDay())];
	}

	default float method_8400(float f) {
		return this.getDimension().method_12464(this.getLevelProperties().getTimeOfDay(), f);
	}

	@Environment(EnvType.CLIENT)
	default int method_8394() {
		return this.getDimension().method_12454(this.getLevelProperties().getTimeOfDay());
	}

	TickScheduler<Block> getBlockTickScheduler();

	TickScheduler<Fluid> getFluidTickScheduler();

	default Chunk method_8399(BlockPos blockPos) {
		return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	Chunk getChunk(int i, int j);

	Chunk getChunk(int i, int j, ChunkStatus chunkStatus);

	default <T extends Entity> List<T> getVisibleEntities(Class<? extends T> class_, BoundingBox boundingBox) {
		return this.getEntities(class_, boundingBox, EntityPredicates.EXCEPT_SPECTATOR);
	}

	<T extends Entity> List<T> getEntities(Class<? extends T> class_, BoundingBox boundingBox, @Nullable Predicate<? super T> predicate);

	World method_8410();

	LevelProperties getLevelProperties();

	LocalDifficulty getLocalDifficulty(BlockPos blockPos);

	default Difficulty getDifficulty() {
		return this.getLevelProperties().getDifficulty();
	}

	ChunkManager getChunkManager();

	default boolean method_8393(int i, int j) {
		return this.getChunkManager().method_12123(i, j);
	}

	WorldSaveHandler getSaveHandler();

	Random getRandom();

	void updateNeighbors(BlockPos blockPos, Block block);

	BlockPos method_8395();

	void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g);

	void method_8406(Particle particle, double d, double e, double f, double g, double h, double i);
}
