package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_3443;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.entity.EntityType;
import net.minecraft.sortme.structures.NetherFortressGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;

public class NetherFortressFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> field_13705 = Lists.<Biome.SpawnEntry>newArrayList(
		new Biome.SpawnEntry(EntityType.BLAZE, 10, 2, 3),
		new Biome.SpawnEntry(EntityType.ZOMBIE_PIGMAN, 5, 4, 4),
		new Biome.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5),
		new Biome.SpawnEntry(EntityType.SKELETON, 2, 5, 5),
		new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)
	);

	public NetherFortressFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public boolean method_14026(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		int k = i >> 4;
		int l = j >> 4;
		random.setSeed((long)(k ^ l << 4) ^ chunkGenerator.getSeed());
		random.nextInt();
		if (random.nextInt(3) != 0) {
			return false;
		} else if (i != (k << 4) + 4 + random.nextInt(8)) {
			return false;
		} else if (j != (l << 4) + 4 + random.nextInt(8)) {
			return false;
		} else {
			Biome biome = chunkGenerator.getBiomeSource().method_8758(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
			return chunkGenerator.hasStructure(biome, Feature.NETHER_BRIDGE);
		}
	}

	@Override
	public StructureFeature.class_3774 method_14016() {
		return NetherFortressFeature.class_3109::new;
	}

	@Override
	public String getName() {
		return "Fortress";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	@Override
	public List<Biome.SpawnEntry> method_13149() {
		return field_13705;
	}

	public static class class_3109 extends class_3449 {
		public class_3109(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome) {
			NetherFortressGenerator.class_3407 lv = new NetherFortressGenerator.class_3407(this.field_16715, (i << 4) + 2, (j << 4) + 2);
			this.children.add(lv);
			lv.method_14918(lv, this.children, this.field_16715);
			List<class_3443> list = lv.field_14505;

			while (!list.isEmpty()) {
				int k = this.field_16715.nextInt(list.size());
				class_3443 lv2 = (class_3443)list.remove(k);
				lv2.method_14918(lv, this.children, this.field_16715);
			}

			this.method_14969();
			this.method_14976(this.field_16715, 48, 70);
		}
	}
}
