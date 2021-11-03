package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import net.minecraft.class_6748;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
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

	@Override
	public ChunkGenerator withSeed(long seed) {
		return this;
	}

	public FlatChunkGeneratorConfig getConfig() {
		return this.config;
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
	}

	@Override
	public int getSpawnHeight(HeightLimitView world) {
		return world.getBottomY() + Math.min(world.getHeight(), this.config.getLayerBlocks().size());
	}

	@Override
	protected boolean method_38274(Registry<Biome> registry, Predicate<RegistryKey<Biome>> predicate, Biome biome) {
		return registry.getKey(this.config.getBiome()).filter(predicate).isPresent();
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, class_6748 arg, StructureAccessor structureAccessor, Chunk chunk) {
		List<BlockState> list = this.config.getLayerBlocks();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

		for (int i = 0; i < Math.min(chunk.getHeight(), list.size()); i++) {
			BlockState blockState = (BlockState)list.get(i);
			if (blockState != null) {
				int j = chunk.getBottomY() + i;

				for (int k = 0; k < 16; k++) {
					for (int l = 0; l < 16; l++) {
						chunk.setBlockState(mutable.set(k, j, l), blockState, false);
						heightmap.trackUpdate(k, j, l, blockState);
						heightmap2.trackUpdate(k, j, l, blockState);
					}
				}
			}
		}

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		List<BlockState> list = this.config.getLayerBlocks();

		for (int i = Math.min(list.size(), world.getTopY()) - 1; i >= 0; i--) {
			BlockState blockState = (BlockState)list.get(i);
			if (blockState != null && heightmap.getBlockPredicate().test(blockState)) {
				return world.getBottomY() + i + 1;
			}
		}

		return world.getBottomY();
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		return new VerticalBlockSample(
			world.getBottomY(),
			(BlockState[])this.config
				.getLayerBlocks()
				.stream()
				.limit((long)world.getHeight())
				.map(state -> state == null ? Blocks.AIR.getDefaultState() : state)
				.toArray(BlockState[]::new)
		);
	}

	@Override
	public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
		return (i, j, k) -> MultiNoiseUtil.createNoiseValuePoint(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void carve(
		ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep
	) {
	}

	@Override
	public void populateEntities(ChunkRegion region) {
	}

	@Override
	public int getMinimumY() {
		return 0;
	}

	@Override
	public int getWorldHeight() {
		return 384;
	}

	@Override
	public int getSeaLevel() {
		return 63;
	}
}
