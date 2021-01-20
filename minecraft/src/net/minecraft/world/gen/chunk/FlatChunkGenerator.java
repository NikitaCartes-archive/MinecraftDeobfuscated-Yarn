package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;

public class FlatChunkGenerator extends ChunkGenerator {
	public static final Codec<FlatChunkGenerator> CODEC = FlatChunkGeneratorConfig.CODEC
		.fieldOf("settings")
		.<FlatChunkGenerator>xmap(FlatChunkGenerator::new, FlatChunkGenerator::getConfig)
		.codec();
	private final FlatChunkGeneratorConfig config;

	public FlatChunkGenerator(FlatChunkGeneratorConfig config) {
		super(new FixedBiomeSource(config.createBiome()), new FixedBiomeSource(config.getBiome()), config.getStructuresConfig(), 0L);
		this.config = config;
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator withSeed(long seed) {
		return this;
	}

	public FlatChunkGeneratorConfig getConfig() {
		return this.config;
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
	}

	@Override
	public int getSpawnHeight() {
		BlockState[] blockStates = this.config.getLayerBlocks();

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i] == null ? Blocks.AIR.getDefaultState() : blockStates[i];
			if (!Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
				return this.config.getBottomSectionLimit() + i - 1;
			}
		}

		return this.config.getBottomSectionLimit() + blockStates.length;
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
		BlockState[] blockStates = this.config.getLayerBlocks();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i];
			if (blockState != null) {
				int j = world.getBottomSectionLimit() + i;

				for (int k = 0; k < 16; k++) {
					for (int l = 0; l < 16; l++) {
						chunk.setBlockState(mutable.set(k, j, l), blockState, false);
						heightmap.trackUpdate(k, j, l, blockState);
						heightmap2.trackUpdate(k, j, l, blockState);
					}
				}
			}
		}
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		BlockState[] blockStates = this.config.getLayerBlocks();

		for (int i = blockStates.length - 1; i >= 0; i--) {
			BlockState blockState = blockStates[i];
			if (blockState != null && heightmapType.getBlockPredicate().test(blockState)) {
				return this.config.getBottomSectionLimit() + i + 1;
			}
		}

		return 0;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z) {
		return new VerticalBlockSample(
			0, (BlockState[])Arrays.stream(this.config.getLayerBlocks()).map(state -> state == null ? Blocks.AIR.getDefaultState() : state).toArray(BlockState[]::new)
		);
	}
}
