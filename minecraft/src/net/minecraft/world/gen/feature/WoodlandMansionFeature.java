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
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class WoodlandMansionFeature extends StructureFeature<DefaultFeatureConfig> {
	public WoodlandMansionFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected int getSpacing(DimensionType dimensionType, ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getMansionDistance();
	}

	@Override
	protected int getSeparation(DimensionType dimensionType, ChunkGeneratorConfig chunkGenerationConfig) {
		return chunkGenerationConfig.getMansionSeparation();
	}

	@Override
	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
		return 10387319;
	}

	@Override
	protected boolean method_27219() {
		return false;
	}

	@Override
	protected boolean shouldStartAt(
		BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, ChunkRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos
	) {
		for (Biome biome2 : chunkGenerator.getBiomeSource().getBiomesInArea(chunkX * 16 + 9, chunkGenerator.getSeaLevel(), chunkZ * 16 + 9, 32)) {
			if (!chunkGenerator.hasStructure(biome2, this)) {
				return false;
			}
		}

		return true;
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
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void init(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			BlockRotation blockRotation = BlockRotation.random(this.random);
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
		public void generateStructure(
			IWorld world, StructureAccessor structureAccessor, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos
		) {
			super.generateStructure(world, structureAccessor, chunkGenerator, random, blockBox, chunkPos);
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
