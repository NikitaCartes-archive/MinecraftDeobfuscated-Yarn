package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_2919;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.sortme.structures.EndCityGenerator;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;

public class EndCityFeature extends StructureFeature<DefaultFeatureConfig> {
	public EndCityFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected ChunkPos method_14018(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.getSettings().method_12554();
		int n = chunkGenerator.getSettings().method_12557();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_2919)random).method_12665(chunkGenerator.getSeed(), s, t, 10387313);
		s *= m;
		t *= m;
		s += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		t += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		return new ChunkPos(s, t);
	}

	@Override
	public boolean method_14026(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.method_14018(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			Biome biome = chunkGenerator.getBiomeSource().method_8758(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
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
	public StructureFeature.class_3774 method_14016() {
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

		int m = chunkGenerator.produceHeight(i + 7, j + 7, Heightmap.Type.WORLD_SURFACE_WG);
		int n = chunkGenerator.produceHeight(i + 7, j + 7 + l, Heightmap.Type.WORLD_SURFACE_WG);
		int o = chunkGenerator.produceHeight(i + 7 + k, j + 7, Heightmap.Type.WORLD_SURFACE_WG);
		int p = chunkGenerator.produceHeight(i + 7 + k, j + 7 + l, Heightmap.Type.WORLD_SURFACE_WG);
		return Math.min(Math.min(m, n), Math.min(o, p));
	}

	public static class class_3022 extends class_3449 {
		public class_3022(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome) {
			Rotation rotation = Rotation.values()[this.field_16715.nextInt(Rotation.values().length)];
			int k = EndCityFeature.method_13085(i, j, chunkGenerator);
			if (k >= 60) {
				BlockPos blockPos = new BlockPos(i * 16 + 8, k, j * 16 + 8);
				EndCityGenerator.method_14679(arg, blockPos, rotation, this.children, this.field_16715);
				this.method_14969();
			}
		}
	}
}
