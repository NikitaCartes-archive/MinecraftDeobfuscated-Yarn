package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.noise.NoiseConfig;

public class DebugChunkGenerator extends ChunkGenerator {
	public static final Codec<DebugChunkGenerator> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(RegistryOps.getEntryCodec(BiomeKeys.PLAINS)).apply(instance, instance.stable(DebugChunkGenerator::new))
	);
	private static final int field_31467 = 2;
	private static final List<BlockState> BLOCK_STATES = (List<BlockState>)StreamSupport.stream(Registries.BLOCK.spliterator(), false)
		.flatMap(block -> block.getStateManager().getStates().stream())
		.collect(Collectors.toList());
	private static final int X_SIDE_LENGTH = MathHelper.ceil(MathHelper.sqrt((float)BLOCK_STATES.size()));
	private static final int Z_SIDE_LENGTH = MathHelper.ceil((float)BLOCK_STATES.size() / (float)X_SIDE_LENGTH);
	protected static final BlockState AIR = Blocks.AIR.getDefaultState();
	protected static final BlockState BARRIER = Blocks.BARRIER.getDefaultState();
	public static final int field_31465 = 70;
	public static final int field_31466 = 60;

	public DebugChunkGenerator(RegistryEntry.Reference<Biome> biomeEntry) {
		super(new FixedBiomeSource(biomeEntry));
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
	}

	@Override
	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				int m = ChunkSectionPos.getOffsetPos(i, k);
				int n = ChunkSectionPos.getOffsetPos(j, l);
				world.setBlockState(mutable.set(m, 60, n), BARRIER, Block.NOTIFY_LISTENERS);
				BlockState blockState = getBlockState(m, n);
				world.setBlockState(mutable.set(m, 70, n), blockState, Block.NOTIFY_LISTENERS);
			}
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
		return 0;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
		return new VerticalBlockSample(0, new BlockState[0]);
	}

	@Override
	public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
	}

	public static BlockState getBlockState(int x, int z) {
		BlockState blockState = AIR;
		if (x > 0 && z > 0 && x % 2 != 0 && z % 2 != 0) {
			x /= 2;
			z /= 2;
			if (x <= X_SIDE_LENGTH && z <= Z_SIDE_LENGTH) {
				int i = MathHelper.abs(x * X_SIDE_LENGTH + z);
				if (i < BLOCK_STATES.size()) {
					blockState = (BlockState)BLOCK_STATES.get(i);
				}
			}
		}

		return blockState;
	}

	@Override
	public void carve(
		ChunkRegion chunkRegion,
		long seed,
		NoiseConfig noiseConfig,
		BiomeAccess biomeAccess,
		StructureAccessor structureAccessor,
		Chunk chunk,
		GenerationStep.Carver carverStep
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
