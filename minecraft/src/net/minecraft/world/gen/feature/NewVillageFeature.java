package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_2919;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.class_3813;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.config.feature.NewVillageFeatureConfig;

public class NewVillageFeature extends StructureFeature<NewVillageFeatureConfig> {
	public NewVillageFeature(Function<Dynamic<?>, ? extends NewVillageFeatureConfig> function) {
		super(function);
	}

	@Override
	protected ChunkPos method_14018(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.getSettings().method_12558();
		int n = chunkGenerator.getSettings().method_12559();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_2919)random).method_12665(chunkGenerator.getSeed(), s, t, 10387312);
		s *= m;
		t *= m;
		s += random.nextInt(m - n);
		t += random.nextInt(m - n);
		return new ChunkPos(s, t);
	}

	@Override
	public boolean method_14026(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.method_14018(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			Biome biome = chunkGenerator.getBiomeSource().method_8758(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
			return chunkGenerator.hasStructure(biome, Feature.NEW_VILLAGE);
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.class_3774 method_14016() {
		return NewVillageFeature.class_3811::new;
	}

	@Override
	public String getName() {
		return "New_Village";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	public static class class_3811 extends class_3449 {
		public class_3811(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome) {
			NewVillageFeatureConfig newVillageFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.NEW_VILLAGE);
			BlockPos blockPos = new BlockPos(i * 16, 0, j * 16);
			class_3813.method_16753(chunkGenerator, arg, blockPos, this.children, this.field_16715, newVillageFeatureConfig);
			this.method_14969();
		}
	}
}
