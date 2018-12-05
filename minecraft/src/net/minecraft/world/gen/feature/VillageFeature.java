package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_2919;
import net.minecraft.class_3443;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.entity.EntityType;
import net.minecraft.sortme.structures.VillageGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.config.feature.VillageFeatureConfig;

public class VillageFeature extends StructureFeature<VillageFeatureConfig> {
	private static final List<Biome.SpawnEntry> field_16436 = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.CAT, 1, 1, 5));

	public VillageFeature(Function<Dynamic<?>, ? extends VillageFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Village";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	@Override
	public List<Biome.SpawnEntry> method_16140() {
		return field_16436;
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
			return chunkGenerator.hasStructure(biome, Feature.VILLAGE);
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.class_3774 method_14016() {
		return VillageFeature.class_3212::new;
	}

	public static class class_3212 extends class_3449 {
		public class_3212(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome) {
			VillageFeatureConfig villageFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.VILLAGE);
			List<VillageGenerator.class_3455> list = VillageGenerator.method_14986(this.field_16715, villageFeatureConfig.sizeModifier);
			VillageGenerator.class_3461 lv = new VillageGenerator.class_3461(this.field_16715, (i << 4) + 2, (j << 4) + 2, list, villageFeatureConfig);
			this.children.add(lv);
			lv.method_14918(lv, this.children, this.field_16715);
			List<class_3443> list2 = lv.field_15349;
			List<class_3443> list3 = lv.field_15346;

			while (!list2.isEmpty() || !list3.isEmpty()) {
				if (list2.isEmpty()) {
					int k = this.field_16715.nextInt(list3.size());
					class_3443 lv2 = (class_3443)list3.remove(k);
					lv2.method_14918(lv, this.children, this.field_16715);
				} else {
					int k = this.field_16715.nextInt(list2.size());
					class_3443 lv2 = (class_3443)list2.remove(k);
					lv2.method_14918(lv, this.children, this.field_16715);
				}
			}

			this.method_14969();
			int k = 0;

			for (class_3443 lv3 : this.children) {
				if (!(lv3 instanceof VillageGenerator.class_3462)) {
					k++;
				}
			}

			if (k <= 2) {
				this.children.clear();
			}
		}
	}
}
