package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class WoodlandMansionFeature extends StructureFeature<DefaultFeatureConfig> {
	public WoodlandMansionFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.method_12109().getMansionDistance();
		int n = chunkGenerator.method_12109().getMansionSeparation();
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
	public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.getStart(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			for (Biome biome : chunkGenerator.getBiomeSource().getBiomesInArea(i * 16 + 9, j * 16 + 9, 32)) {
				if (!chunkGenerator.method_12097(biome, Feature.field_13528)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return WoodlandMansionFeature.Start::new;
	}

	@Override
	public String getName() {
		return "Mansion";
	}

	@Override
	public int getRadius() {
		return 8;
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			BlockRotation blockRotation = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
			int k = 5;
			int l = 5;
			if (blockRotation == BlockRotation.field_11463) {
				k = -5;
			} else if (blockRotation == BlockRotation.field_11464) {
				k = -5;
				l = -5;
			} else if (blockRotation == BlockRotation.field_11465) {
				l = -5;
			}

			int m = (i << 4) + 7;
			int n = (j << 4) + 7;
			int o = chunkGenerator.getHeightInGround(m, n, Heightmap.Type.field_13194);
			int p = chunkGenerator.getHeightInGround(m, n + l, Heightmap.Type.field_13194);
			int q = chunkGenerator.getHeightInGround(m + k, n, Heightmap.Type.field_13194);
			int r = chunkGenerator.getHeightInGround(m + k, n + l, Heightmap.Type.field_13194);
			int s = Math.min(Math.min(o, p), Math.min(q, r));
			if (s >= 60) {
				BlockPos blockPos = new BlockPos(i * 16 + 8, s + 1, j * 16 + 8);
				List<WoodlandMansionGenerator.Piece> list = Lists.<WoodlandMansionGenerator.Piece>newLinkedList();
				WoodlandMansionGenerator.method_15029(structureManager, blockPos, blockRotation, list, this.random);
				this.children.addAll(list);
				this.setBoundingBoxFromChildren();
			}
		}

		@Override
		public void generateStructure(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			super.generateStructure(iWorld, random, mutableIntBoundingBox, chunkPos);
			int i = this.boundingBox.minY;

			for (int j = mutableIntBoundingBox.minX; j <= mutableIntBoundingBox.maxX; j++) {
				for (int k = mutableIntBoundingBox.minZ; k <= mutableIntBoundingBox.maxZ; k++) {
					BlockPos blockPos = new BlockPos(j, i, k);
					if (!iWorld.isAir(blockPos) && this.boundingBox.contains(blockPos)) {
						boolean bl = false;

						for (StructurePiece structurePiece : this.children) {
							if (structurePiece.getBoundingBox().contains(blockPos)) {
								bl = true;
								break;
							}
						}

						if (bl) {
							for (int l = i - 1; l > 1; l--) {
								BlockPos blockPos2 = new BlockPos(j, l, k);
								if (!iWorld.isAir(blockPos2) && !iWorld.method_8320(blockPos2).method_11620().isLiquid()) {
									break;
								}

								iWorld.method_8652(blockPos2, Blocks.field_10445.method_9564(), 2);
							}
						}
					}
				}
			}
		}
	}
}
