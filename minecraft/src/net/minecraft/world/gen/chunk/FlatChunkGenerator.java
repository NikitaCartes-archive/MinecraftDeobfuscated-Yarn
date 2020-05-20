package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;

public class FlatChunkGenerator extends ChunkGenerator {
	public static final Codec<FlatChunkGenerator> field_24769 = FlatChunkGeneratorConfig.CODEC
		.fieldOf("settings")
		.<FlatChunkGenerator>xmap(FlatChunkGenerator::new, FlatChunkGenerator::method_28545)
		.codec();
	private final FlatChunkGeneratorConfig generatorConfig;

	public FlatChunkGenerator(FlatChunkGeneratorConfig config) {
		super(new FixedBiomeSource(config.method_28917()), new FixedBiomeSource(config.getBiome()), config.getConfig(), 0L);
		this.generatorConfig = config;
	}

	@Override
	protected Codec<? extends ChunkGenerator> method_28506() {
		return field_24769;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator withSeed(long seed) {
		return this;
	}

	public FlatChunkGeneratorConfig method_28545() {
		return this.generatorConfig;
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
	}

	@Override
	public int getSpawnHeight() {
		BlockState[] blockStates = this.generatorConfig.getLayerBlocks();

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i] == null ? Blocks.AIR.getDefaultState() : blockStates[i];
			if (!Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
				return i - 1;
			}
		}

		return blockStates.length;
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
		BlockState[] blockStates = this.generatorConfig.getLayerBlocks();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i];
			if (blockState != null) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						chunk.setBlockState(mutable.set(j, i, k), blockState, false);
						heightmap.trackUpdate(j, i, k, blockState);
						heightmap2.trackUpdate(j, i, k, blockState);
					}
				}
			}
		}
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		BlockState[] blockStates = this.generatorConfig.getLayerBlocks();

		for (int i = blockStates.length - 1; i >= 0; i--) {
			BlockState blockState = blockStates[i];
			if (blockState != null && heightmapType.getBlockPredicate().test(blockState)) {
				return i + 1;
			}
		}

		return 0;
	}

	@Override
	public BlockView getColumnSample(int x, int z) {
		return new VerticalBlockSample(
			(BlockState[])Arrays.stream(this.generatorConfig.getLayerBlocks())
				.map(state -> state == null ? Blocks.AIR.getDefaultState() : state)
				.toArray(BlockState[]::new)
		);
	}
}
