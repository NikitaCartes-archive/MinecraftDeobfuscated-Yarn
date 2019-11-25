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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class WoodlandMansionFeature extends StructureFeature<DefaultFeatureConfig> {
	public WoodlandMansionFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.getConfig().getMansionDistance();
		int n = chunkGenerator.getConfig().getMansionSeparation();
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
	public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int chunkZ, int i, Biome biome) {
		ChunkPos chunkPos = this.getStart(chunkGenerator, random, chunkZ, i, 0, 0);
		if (chunkZ == chunkPos.x && i == chunkPos.z) {
			for (Biome biome2 : chunkGenerator.getBiomeSource().getBiomesInArea(chunkZ * 16 + 9, chunkGenerator.getSeaLevel(), i * 16 + 9, 32)) {
				if (!chunkGenerator.hasStructure(biome2, this)) {
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
		public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, BlockBox blockBox, int i, long l) {
			super(structureFeature, chunkX, chunkZ, blockBox, i, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			BlockRotation blockRotation = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
			int i = 5;
			int j = 5;
			if (blockRotation == BlockRotation.CLOCKWISE_90) {
				i = -5;
			} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
				i = -5;
				j = -5;
			} else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
				j = -5;
			}

			int k = (x << 4) + 7;
			int l = (z << 4) + 7;
			int m = chunkGenerator.getHeightInGround(k, l, Heightmap.Type.WORLD_SURFACE_WG);
			int n = chunkGenerator.getHeightInGround(k, l + j, Heightmap.Type.WORLD_SURFACE_WG);
			int o = chunkGenerator.getHeightInGround(k + i, l, Heightmap.Type.WORLD_SURFACE_WG);
			int p = chunkGenerator.getHeightInGround(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG);
			int q = Math.min(Math.min(m, n), Math.min(o, p));
			if (q >= 60) {
				BlockPos blockPos = new BlockPos(x * 16 + 8, q + 1, z * 16 + 8);
				List<WoodlandMansionGenerator.Piece> list = Lists.<WoodlandMansionGenerator.Piece>newLinkedList();
				WoodlandMansionGenerator.addPieces(structureManager, blockPos, blockRotation, list, this.random);
				this.children.addAll(list);
				this.setBoundingBoxFromChildren();
			}
		}

		@Override
		public void generateStructure(IWorld world, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			super.generateStructure(world, chunkGenerator, random, blockBox, chunkPos);
			int i = this.boundingBox.minY;

			for (int j = blockBox.minX; j <= blockBox.maxX; j++) {
				for (int k = blockBox.minZ; k <= blockBox.maxZ; k++) {
					BlockPos blockPos = new BlockPos(j, i, k);
					if (!world.isAir(blockPos) && this.boundingBox.contains(blockPos)) {
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
								if (!world.isAir(blockPos2) && !world.getBlockState(blockPos2).getMaterial().isLiquid()) {
									break;
								}

								world.setBlockState(blockPos2, Blocks.COBBLESTONE.getDefaultState(), 2);
							}
						}
					}
				}
			}
		}
	}
}
