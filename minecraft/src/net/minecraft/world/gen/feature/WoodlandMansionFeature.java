package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.class_6834;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class WoodlandMansionFeature extends StructureFeature<DefaultFeatureConfig> {
	public WoodlandMansionFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec, WoodlandMansionFeature::addPieces, WoodlandMansionFeature::postPlace);
	}

	@Override
	protected boolean isUniformDistribution() {
		return false;
	}

	private static Optional<StructurePiecesGenerator<DefaultFeatureConfig>> addPieces(class_6834.class_6835<DefaultFeatureConfig> arg) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setCarverSeed(arg.seed(), arg.chunkPos().x, arg.chunkPos().z);
		BlockRotation blockRotation = BlockRotation.random(chunkRandom);
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

		int k = arg.chunkPos().getOffsetX(7);
		int l = arg.chunkPos().getOffsetZ(7);
		int[] is = arg.method_39847(k, i, l, j);
		int m = Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
		if (m < 60) {
			return Optional.empty();
		} else if (!arg.validBiome().test(arg.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(is[0]), BiomeCoords.fromBlock(l)))
			)
		 {
			return Optional.empty();
		} else {
			BlockPos blockPos = new BlockPos(arg.chunkPos().getCenterX(), m + 1, arg.chunkPos().getCenterZ());
			return Optional.of((StructurePiecesGenerator<>)(structurePiecesCollector, context) -> {
				List<WoodlandMansionGenerator.Piece> list = Lists.<WoodlandMansionGenerator.Piece>newLinkedList();
				WoodlandMansionGenerator.addPieces(context.structureManager(), blockPos, blockRotation, list, chunkRandom);
				list.forEach(structurePiecesCollector::addPiece);
			});
		}
	}

	private static void postPlace(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox chunkBox,
		ChunkPos chunkPos,
		StructurePiecesList children
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = world.getBottomY();
		BlockBox blockBox = children.getBoundingBox();
		int j = blockBox.getMinY();

		for (int k = chunkBox.getMinX(); k <= chunkBox.getMaxX(); k++) {
			for (int l = chunkBox.getMinZ(); l <= chunkBox.getMaxZ(); l++) {
				mutable.set(k, j, l);
				if (!world.isAir(mutable) && blockBox.contains(mutable) && children.contains(mutable)) {
					for (int m = j - 1; m > i; m--) {
						mutable.setY(m);
						if (!world.isAir(mutable) && !world.getBlockState(mutable).getMaterial().isLiquid()) {
							break;
						}

						world.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_LISTENERS);
					}
				}
			}
		}
	}
}
