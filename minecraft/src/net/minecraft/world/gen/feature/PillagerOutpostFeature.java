package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_3145;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.class_3791;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.config.feature.PillagerOutpostFeatureConfig;

public class PillagerOutpostFeature extends class_3145<PillagerOutpostFeatureConfig> {
	private static final List<Biome.SpawnEntry> field_16656 = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

	public PillagerOutpostFeature(Function<Dynamic<?>, ? extends PillagerOutpostFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Pillager_Outpost";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	public List<Biome.SpawnEntry> method_13149() {
		return field_16656;
	}

	@Override
	public boolean method_14026(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.method_14018(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			int k = i >> 4;
			int l = j >> 4;
			random.setSeed((long)(k ^ l << 4) ^ chunkGenerator.getSeed());
			random.nextInt();
			if (random.nextInt(5) != 0) {
				return false;
			} else {
				Biome biome = chunkGenerator.getBiomeSource().method_8758(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
				return chunkGenerator.hasStructure(biome, Feature.PILLAGER_OUTPOST);
			}
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.class_3774 method_14016() {
		return PillagerOutpostFeature.class_3771::new;
	}

	@Override
	protected int method_13774() {
		return 165745296;
	}

	public static class class_3771 extends class_3449 {
		public class_3771(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome) {
			BlockPos blockPos = new BlockPos(i * 16, 90, j * 16);
			class_3791.method_16650(chunkGenerator, arg, blockPos, this.children, this.field_16715);
			this.method_14969();
		}
	}
}
