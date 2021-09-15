package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class WoodlandMansionFeature extends StructureFeature<DefaultFeatureConfig> {
	public WoodlandMansionFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	protected boolean isUniformDistribution() {
		return false;
	}

	@Override
	public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return WoodlandMansionFeature.Start::new;
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			DefaultFeatureConfig defaultFeatureConfig,
			HeightLimitView heightLimitView,
			Predicate<Biome> predicate
		) {
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

			int k = chunkPos.getOffsetX(7);
			int l = chunkPos.getOffsetZ(7);
			int[] is = StructureFeature.getCornerInGroundHeights(chunkGenerator, k, i, l, j, heightLimitView);
			int m = Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
			if (m >= 60) {
				if (predicate.test(chunkGenerator.getBiomeForNoiseGen(BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(is[0]), BiomeCoords.fromBlock(l)))) {
					BlockPos blockPos = new BlockPos(chunkPos.getCenterX(), m + 1, chunkPos.getCenterZ());
					List<WoodlandMansionGenerator.Piece> list = Lists.<WoodlandMansionGenerator.Piece>newLinkedList();
					WoodlandMansionGenerator.addPieces(structureManager, blockPos, blockRotation, list, this.random);
					list.forEach(this::addPiece);
				}
			}
		}

		@Override
		public void generateStructure(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			Predicate<Biome> predicate,
			BlockBox blockBox,
			ChunkPos chunkPos
		) {
			super.generateStructure(world, structureAccessor, chunkGenerator, random, predicate, blockBox, chunkPos);
			BlockBox blockBox2 = this.setBoundingBoxFromChildren();
			int i = blockBox2.getMinY();

			for (int j = blockBox.getMinX(); j <= blockBox.getMaxX(); j++) {
				for (int k = blockBox.getMinZ(); k <= blockBox.getMaxZ(); k++) {
					BlockPos blockPos = new BlockPos(j, i, k);
					if (!world.isAir(blockPos) && blockBox2.contains(blockPos) && this.contains(blockPos)) {
						for (int l = i - 1; l > 1; l--) {
							BlockPos blockPos2 = new BlockPos(j, l, k);
							if (!world.isAir(blockPos2) && !world.getBlockState(blockPos2).getMaterial().isLiquid()) {
								break;
							}

							world.setBlockState(blockPos2, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_LISTENERS);
						}
					}
				}
			}
		}
	}
}
