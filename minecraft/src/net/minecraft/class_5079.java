package net.minecraft;

import java.util.BitSet;
import java.util.function.IntPredicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class class_5079 extends RandomDimension {
	private static final BitSet field_23548 = new BitSet();
	private static final BitSet field_23549 = new BitSet();
	private static final BitSet field_23550 = new BitSet();
	private static final BitSet field_23551 = new BitSet();
	private static final BitSet field_23552 = new BitSet();
	private static final BitSet field_23553 = new BitSet();
	private static final BitSet field_23554 = new BitSet();

	private static int method_26555(int i, int j, int k) {
		return i << 8 | j << 4 | k;
	}

	public class_5079(World world, DimensionType dimensionType) {
		super(world, dimensionType, 0.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5079.class_5080(this.field_23566, method_26572(Biomes.THE_END), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 12000.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return vec3d;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
		return false;
	}

	static {
		for (int i = 6; i < 10; i++) {
			for (int j = 6; j < 10; j++) {
				for (int k = 0; k <= 8; k++) {
					field_23548.set(method_26555(i, j, k));
					field_23549.set(method_26555(i, j, 15 - k));
					field_23550.set(method_26555(k, j, i));
					field_23551.set(method_26555(15 - k, j, i));
					field_23552.set(method_26555(i, 15 - k, j));
					field_23553.set(method_26555(i, k, j));
				}
			}
		}

		for (int i = 5; i < 11; i++) {
			for (int j = 5; j < 11; j++) {
				for (int k = 5; k < 11; k++) {
					field_23554.set(method_26555(i, j, k));
				}
			}
		}
	}

	public static class class_5080 extends ChunkGenerator<class_5099> {
		public class_5080(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public int getSpawnHeight() {
			return 100;
		}

		private static IntPredicate method_26563(ChunkRandom chunkRandom, int i, int j, int k, IntPredicate intPredicate, BitSet bitSet) {
			if (j < 0) {
				return intPredicate;
			} else {
				chunkRandom.setTerrainSeed(i, k);
				chunkRandom.setTerrainSeed(chunkRandom.nextInt(), j);
				return chunkRandom.nextBoolean() ? intPredicate.or(bitSet::get) : intPredicate;
			}
		}

		@Override
		public void populateNoise(IWorld world, Chunk chunk) {
			ChunkPos chunkPos = chunk.getPos();

			for (int i = 0; i < 16; i++) {
				ChunkRandom chunkRandom = new ChunkRandom();
				int j = 2 * chunkPos.x;
				int k = 2 * i;
				int l = 2 * chunkPos.z;
				IntPredicate intPredicate = ix -> false;
				intPredicate = method_26563(chunkRandom, j + 1, k, l, intPredicate, class_5079.field_23551);
				intPredicate = method_26563(chunkRandom, j - 1, k, l, intPredicate, class_5079.field_23550);
				intPredicate = method_26563(chunkRandom, j, k, l + 1, intPredicate, class_5079.field_23549);
				intPredicate = method_26563(chunkRandom, j, k, l - 1, intPredicate, class_5079.field_23548);
				intPredicate = method_26563(chunkRandom, j, k + 1, l, intPredicate, class_5079.field_23552);
				intPredicate = method_26563(chunkRandom, j, k - 1, l, intPredicate, class_5079.field_23553);
				if (intPredicate.test(class_5079.method_26555(8, 8, 8))) {
					intPredicate = intPredicate.or(class_5079.field_23554::get);
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int m = 0; m < 16; m++) {
					for (int n = 0; n < 16; n++) {
						for (int o = 0; o < 16; o++) {
							if (!intPredicate.test(class_5079.method_26555(m, n, o))) {
								int p = 16 * i + n;
								chunk.setBlockState(mutable.set(m, p, o), Blocks.SEA_LANTERN.getDefaultState(), false);
							}
						}
					}
				}
			}
		}

		@Override
		public int getHeight(int x, int z, Heightmap.Type heightmapType) {
			return 100;
		}

		@Override
		public BlockView getColumnSample(int x, int z) {
			return EmptyBlockView.INSTANCE;
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23450;
		}
	}
}
