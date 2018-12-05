package net.minecraft.world.gen.chunk;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.class_3233;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;

public class DebugChunkGenerator extends ChunkGenerator<DebugChunkGeneratorSettings> {
	private static final List<BlockState> field_13163 = (List<BlockState>)StreamSupport.stream(Registry.BLOCK.spliterator(), false)
		.flatMap(block -> block.getStateFactory().getStates().stream())
		.collect(Collectors.toList());
	private static final int field_13161 = MathHelper.ceil(MathHelper.sqrt((float)field_13163.size()));
	private static final int field_13160 = MathHelper.ceil((float)field_13163.size() / (float)field_13161);
	protected static final BlockState field_13162 = Blocks.field_10124.getDefaultState();
	protected static final BlockState field_13164 = Blocks.field_10499.getDefaultState();

	public DebugChunkGenerator(IWorld iWorld, BiomeSource biomeSource, DebugChunkGeneratorSettings debugChunkGeneratorSettings) {
		super(iWorld, biomeSource, debugChunkGeneratorSettings);
	}

	@Override
	public void buildSurface(Chunk chunk) {
	}

	@Override
	public void carve(Chunk chunk, GenerationStep.Carver carver) {
	}

	@Override
	public int method_12100() {
		return this.world.getSeaLevel() + 1;
	}

	@Override
	public void generateFeatures(class_3233 arg) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = arg.method_14336();
		int j = arg.method_14339();

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				int m = (i << 4) + k;
				int n = (j << 4) + l;
				arg.setBlockState(mutable.set(m, 60, n), field_13164, 2);
				BlockState blockState = method_12578(m, n);
				if (blockState != null) {
					arg.setBlockState(mutable.set(m, 70, n), blockState, 2);
				}
			}
		}
	}

	@Override
	public void populateNoise(IWorld iWorld, Chunk chunk) {
	}

	@Override
	public int produceHeight(int i, int j, Heightmap.Type type) {
		return 0;
	}

	public static BlockState method_12578(int i, int j) {
		BlockState blockState = field_13162;
		if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
			i /= 2;
			j /= 2;
			if (i <= field_13161 && j <= field_13160) {
				int k = MathHelper.abs(i * field_13161 + j);
				if (k < field_13163.size()) {
					blockState = (BlockState)field_13163.get(k);
				}
			}
		}

		return blockState;
	}
}
