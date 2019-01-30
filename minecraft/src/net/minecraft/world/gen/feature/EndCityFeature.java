package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.generator.EndCityGenerator;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class EndCityFeature extends StructureFeature<DefaultFeatureConfig> {
	public EndCityFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected ChunkPos method_14018(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.getSettings().getEndCityDistance();
		int n = chunkGenerator.getSettings().method_12557();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), s, t, 10387313);
		s *= m;
		t *= m;
		s += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		t += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		return new ChunkPos(s, t);
	}

	@Override
	public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.method_14018(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
			if (!chunkGenerator.hasStructure(biome, Feature.END_CITY)) {
				return false;
			} else {
				int k = method_13085(i, j, chunkGenerator);
				return k >= 60;
			}
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return EndCityFeature.class_3022::new;
	}

	@Override
	public String getName() {
		return "EndCity";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	private static int method_13085(int i, int j, ChunkGenerator<?> chunkGenerator) {
		Random random = new Random((long)(i + j * 10387313));
		Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
		int k = 5;
		int l = 5;
		if (rotation == Rotation.ROT_90) {
			k = -5;
		} else if (rotation == Rotation.ROT_180) {
			k = -5;
			l = -5;
		} else if (rotation == Rotation.ROT_270) {
			l = -5;
		}

		int m = (i << 4) + 7;
		int n = (j << 4) + 7;
		int o = chunkGenerator.method_18028(m, n, Heightmap.Type.WORLD_SURFACE_WG);
		int p = chunkGenerator.method_18028(m, n + l, Heightmap.Type.WORLD_SURFACE_WG);
		int q = chunkGenerator.method_18028(m + k, n, Heightmap.Type.WORLD_SURFACE_WG);
		int r = chunkGenerator.method_18028(m + k, n + l, Heightmap.Type.WORLD_SURFACE_WG);
		return Math.min(Math.min(o, p), Math.min(q, r));
	}

	public static class class_3022 extends StructureStart {
		public class_3022(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
			int k = EndCityFeature.method_13085(i, j, chunkGenerator);
			if (k >= 60) {
				BlockPos blockPos = new BlockPos(i * 16 + 8, k, j * 16 + 8);
				EndCityGenerator.addPieces(structureManager, blockPos, rotation, this.children, this.random);
				this.setBoundingBoxFromChildren();
			}
		}
	}
}
