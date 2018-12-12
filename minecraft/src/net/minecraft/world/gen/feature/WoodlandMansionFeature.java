package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_3443;
import net.minecraft.block.Blocks;
import net.minecraft.sortme.structures.MansionGenerator;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.sortme.structures.StructureStart;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class WoodlandMansionFeature extends StructureFeature<DefaultFeatureConfig> {
	public WoodlandMansionFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected ChunkPos method_14018(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.method_12109().getMansionDistance();
		int n = chunkGenerator.method_12109().method_12552();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), s, t, 10387319);
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
			for (Biome biome : chunkGenerator.getBiomeSource().getBiomesInArea(i * 16 + 9, j * 16 + 9, 32)) {
				if (!chunkGenerator.hasStructure(biome, Feature.WOODLAND_MANSION)) {
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
		return WoodlandMansionFeature.class_3224::new;
	}

	@Override
	public String getName() {
		return "Mansion";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	public static class class_3224 extends StructureStart {
		public class_3224(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			Rotation rotation = Rotation.values()[this.field_16715.nextInt(Rotation.values().length)];
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
			int q = Math.min(Math.min(m, n), Math.min(o, p));
			if (q >= 60) {
				BlockPos blockPos = new BlockPos(i * 16 + 8, q + 1, j * 16 + 8);
				List<MansionGenerator.class_3480> list = Lists.<MansionGenerator.class_3480>newLinkedList();
				MansionGenerator.method_15029(structureManager, blockPos, rotation, list, this.field_16715);
				this.children.addAll(list);
				this.method_14969();
			}
		}

		@Override
		public void method_14974(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			super.method_14974(iWorld, random, mutableIntBoundingBox, chunkPos);
			int i = this.boundingBox.minY;

			for (int j = mutableIntBoundingBox.minX; j <= mutableIntBoundingBox.maxX; j++) {
				for (int k = mutableIntBoundingBox.minZ; k <= mutableIntBoundingBox.maxZ; k++) {
					BlockPos blockPos = new BlockPos(j, i, k);
					if (!iWorld.isAir(blockPos) && this.boundingBox.contains(blockPos)) {
						boolean bl = false;

						for (class_3443 lv : this.children) {
							if (lv.method_14935().contains(blockPos)) {
								bl = true;
								break;
							}
						}

						if (bl) {
							for (int l = i - 1; l > 1; l--) {
								BlockPos blockPos2 = new BlockPos(j, l, k);
								if (!iWorld.isAir(blockPos2) && !iWorld.getBlockState(blockPos2).getMaterial().isLiquid()) {
									break;
								}

								iWorld.setBlockState(blockPos2, Blocks.field_10445.getDefaultState(), 2);
							}
						}
					}
				}
			}
		}
	}
}
