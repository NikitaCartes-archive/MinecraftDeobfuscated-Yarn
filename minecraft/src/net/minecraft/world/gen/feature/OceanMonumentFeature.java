package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_3366;
import net.minecraft.entity.EntityType;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.sortme.structures.StructureStart;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class OceanMonumentFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> field_13716 = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4));

	public OceanMonumentFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected ChunkPos method_14018(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.method_12109().getOceanMonumentSpacing();
		int n = chunkGenerator.method_12109().getOceanMonumentSeparation();
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
	public boolean method_14026(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.method_14018(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			for (Biome biome : chunkGenerator.getBiomeSource().getBiomesInArea(i * 16 + 9, j * 16 + 9, 16)) {
				if (!chunkGenerator.hasStructure(biome, Feature.OCEAN_MONUMENT)) {
					return false;
				}
			}

			for (Biome biome2 : chunkGenerator.getBiomeSource().getBiomesInArea(i * 16 + 9, j * 16 + 9, 29)) {
				if (biome2.getCategory() != Biome.Category.OCEAN && biome2.getCategory() != Biome.Category.RIVER) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.class_3774 method_14016() {
		return OceanMonumentFeature.class_3117::new;
	}

	@Override
	public String getName() {
		return "Monument";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	@Override
	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return field_13716;
	}

	public static class class_3117 extends StructureStart {
		private boolean field_13717;

		public class_3117(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			this.method_16588(i, j);
		}

		private void method_16588(int i, int j) {
			int k = i * 16 - 29;
			int l = j * 16 - 29;
			Direction direction = Direction.class_2353.HORIZONTAL.random(this.field_16715);
			this.children.add(new class_3366.class_3374(this.field_16715, k, l, direction));
			this.method_14969();
			this.field_13717 = true;
		}

		@Override
		public void method_14974(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (!this.field_13717) {
				this.children.clear();
				this.method_16588(this.method_14967(), this.method_14966());
			}

			super.method_14974(iWorld, random, mutableIntBoundingBox, chunkPos);
		}
	}
}
