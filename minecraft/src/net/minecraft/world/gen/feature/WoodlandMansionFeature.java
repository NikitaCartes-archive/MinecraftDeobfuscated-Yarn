package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.class_6622;
import net.minecraft.class_6624;
import net.minecraft.class_6626;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class WoodlandMansionFeature extends StructureFeature<DefaultFeatureConfig> {
	public WoodlandMansionFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec, WoodlandMansionFeature::method_38695, WoodlandMansionFeature::method_38694);
	}

	@Override
	protected boolean isUniformDistribution() {
		return false;
	}

	private static void method_38695(class_6626 arg, DefaultFeatureConfig defaultFeatureConfig, class_6622.class_6623 arg2) {
		BlockRotation blockRotation = BlockRotation.random(arg2.random());
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

		int k = arg2.chunkPos().getOffsetX(7);
		int l = arg2.chunkPos().getOffsetZ(7);
		int[] is = arg2.method_38706(k, i, l, j);
		int m = Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
		if (m >= 60) {
			if (arg2.validBiome().test(arg2.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(is[0]), BiomeCoords.fromBlock(l)))) {
				BlockPos blockPos = new BlockPos(arg2.chunkPos().getCenterX(), m + 1, arg2.chunkPos().getCenterZ());
				List<WoodlandMansionGenerator.Piece> list = Lists.<WoodlandMansionGenerator.Piece>newLinkedList();
				WoodlandMansionGenerator.addPieces(arg2.structureManager(), blockPos, blockRotation, list, arg2.random());
				list.forEach(arg::addPiece);
			}
		}
	}

	private static void method_38694(
		StructureWorldAccess structureWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox blockBox,
		ChunkPos chunkPos,
		class_6624 arg
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = structureWorldAccess.getBottomY();
		BlockBox blockBox2 = arg.method_38712();
		int j = blockBox2.getMinY();

		for (int k = blockBox.getMinX(); k <= blockBox.getMaxX(); k++) {
			for (int l = blockBox.getMinZ(); l <= blockBox.getMaxZ(); l++) {
				mutable.set(k, j, l);
				if (!structureWorldAccess.isAir(mutable) && blockBox2.contains(mutable) && arg.method_38710(mutable)) {
					for (int m = j - 1; m > i; m--) {
						mutable.setY(m);
						if (!structureWorldAccess.isAir(mutable) && !structureWorldAccess.getBlockState(mutable).getMaterial().isLiquid()) {
							break;
						}

						structureWorldAccess.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_LISTENERS);
					}
				}
			}
		}
	}
}
