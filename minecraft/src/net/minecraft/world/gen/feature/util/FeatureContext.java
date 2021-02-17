package net.minecraft.world.gen.feature.util;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;

public class FeatureContext<FC extends FeatureConfig> {
	private final StructureWorldAccess world;
	private final ChunkGenerator generator;
	private final Random random;
	private final BlockPos origin;
	private final FC config;

	public FeatureContext(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos origin, FC config) {
		this.world = world;
		this.generator = generator;
		this.random = random;
		this.origin = origin;
		this.config = config;
	}

	public StructureWorldAccess getWorld() {
		return this.world;
	}

	public ChunkGenerator getGenerator() {
		return this.generator;
	}

	public Random getRandom() {
		return this.random;
	}

	public BlockPos getOrigin() {
		return this.origin;
	}

	public FC getConfig() {
		return this.config;
	}
}
