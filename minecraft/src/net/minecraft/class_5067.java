package net.minecraft;

import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
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

public class class_5067 extends RandomDimension {
	public class_5067(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5067.class_5068(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
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

	public static class class_5068 extends ChunkGenerator<class_5099> {
		private final OctaveSimplexNoiseSampler field_23546;
		private final OctaveSimplexNoiseSampler field_23547;

		public class_5068(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
			ChunkRandom chunkRandom = new ChunkRandom(iWorld.getSeed());
			this.field_23546 = new OctaveSimplexNoiseSampler(chunkRandom, IntStream.rangeClosed(-4, 1));
			this.field_23547 = new OctaveSimplexNoiseSampler(chunkRandom, IntStream.rangeClosed(-5, 2));
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public int getSpawnHeight() {
			return 30;
		}

		@Override
		public void populateNoise(IWorld world, Chunk chunk) {
			ChunkPos chunkPos = chunk.getPos();
			Instrument[] instruments = Instrument.values();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					int k = (int)((this.field_23546.sample((double)(chunkPos.x * 16 + i), (double)(chunkPos.z * 16 + j), false) + 1.0) / 2.0 * 24.0);
					int l = (int)(
						(this.field_23547.sample((double)((float)(chunkPos.x * 16 + i) / 4.0F), (double)((float)(chunkPos.z * 16 + j) / 4.0F), false) + 1.0)
							/ 2.0
							* (double)instruments.length
					);
					chunk.setBlockState(
						mutable.set(i, 0, j), Blocks.NOTE_BLOCK.getDefaultState().with(NoteBlock.INSTRUMENT, instruments[l]).with(NoteBlock.NOTE, Integer.valueOf(k)), false
					);
				}
			}
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
		}

		@Override
		public int getHeight(int x, int z, Heightmap.Type heightmapType) {
			return 0;
		}

		@Override
		public BlockView getColumnSample(int x, int z) {
			return EmptyBlockView.INSTANCE;
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23478;
		}
	}
}
