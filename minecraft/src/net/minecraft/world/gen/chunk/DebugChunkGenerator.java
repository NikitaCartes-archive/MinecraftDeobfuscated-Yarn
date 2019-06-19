package net.minecraft.world.gen.chunk;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;

public class DebugChunkGenerator extends ChunkGenerator<DebugChunkGeneratorConfig> {
	private static final List<BlockState> BLOCK_STATES = (List<BlockState>)StreamSupport.stream(Registry.BLOCK.spliterator(), false)
		.flatMap(block -> block.getStateFactory().getStates().stream())
		.collect(Collectors.toList());
	private static final int X_SIDE_LENGTH = MathHelper.ceil(MathHelper.sqrt((float)BLOCK_STATES.size()));
	private static final int Z_SIDE_LENGTH = MathHelper.ceil((float)BLOCK_STATES.size() / (float)X_SIDE_LENGTH);
	protected static final BlockState AIR = Blocks.field_10124.getDefaultState();
	protected static final BlockState BARRIER = Blocks.field_10499.getDefaultState();

	public DebugChunkGenerator(IWorld iWorld, BiomeSource biomeSource, DebugChunkGeneratorConfig debugChunkGeneratorConfig) {
		super(iWorld, biomeSource, debugChunkGeneratorConfig);
	}

	@Override
	public void buildSurface(Chunk chunk) {
	}

	@Override
	public void carve(Chunk chunk, GenerationStep.Carver carver) {
	}

	@Override
	public int getSpawnHeight() {
		return this.world.getSeaLevel() + 1;
	}

	@Override
	public void generateFeatures(ChunkRegion chunkRegion) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = chunkRegion.getCenterChunkX();
		int j = chunkRegion.getCenterChunkZ();

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				int m = (i << 4) + k;
				int n = (j << 4) + l;
				chunkRegion.setBlockState(mutable.set(m, 60, n), BARRIER, 2);
				BlockState blockState = getBlockState(m, n);
				if (blockState != null) {
					chunkRegion.setBlockState(mutable.set(m, 70, n), blockState, 2);
				}
			}
		}
	}

	@Override
	public void populateNoise(IWorld iWorld, Chunk chunk) {
	}

	@Override
	public int getHeightOnGround(int i, int j, Heightmap.Type type) {
		return 0;
	}

	public static BlockState getBlockState(int i, int j) {
		BlockState blockState = AIR;
		if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
			i /= 2;
			j /= 2;
			if (i <= X_SIDE_LENGTH && j <= Z_SIDE_LENGTH) {
				int k = MathHelper.abs(i * X_SIDE_LENGTH + j);
				if (k < BLOCK_STATES.size()) {
					blockState = (BlockState)BLOCK_STATES.get(k);
				}
			}
		}

		return blockState;
	}
}
