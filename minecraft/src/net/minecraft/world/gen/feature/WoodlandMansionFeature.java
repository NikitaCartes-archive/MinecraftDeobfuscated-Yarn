package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureType;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class WoodlandMansionFeature extends StructureFeature {
	public static final Codec<WoodlandMansionFeature> CODEC = RecordCodecBuilder.create(
		instance -> method_41608(instance).apply(instance, WoodlandMansionFeature::new)
	);

	public WoodlandMansionFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl) {
		super(registryEntryList, map, feature, bl);
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		BlockRotation blockRotation = BlockRotation.random(arg.random());
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
		int[] is = method_41611(arg, k, i, l, j);
		int m = Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
		if (m < 60) {
			return Optional.empty();
		} else {
			BlockPos blockPos = new BlockPos(k, m, l);
			return Optional.of(
				new StructureFeature.class_7150(blockPos, structurePiecesCollector -> this.method_41696(structurePiecesCollector, arg, blockPos, blockRotation))
			);
		}
	}

	private void method_41696(StructurePiecesCollector structurePiecesCollector, StructureFeature.class_7149 arg, BlockPos blockPos, BlockRotation blockRotation) {
		ChunkPos chunkPos = arg.chunkPos();
		BlockPos blockPos2 = new BlockPos(chunkPos.getCenterX(), blockPos.getY() + 1, chunkPos.getCenterZ());
		List<WoodlandMansionGenerator.Piece> list = Lists.<WoodlandMansionGenerator.Piece>newLinkedList();
		WoodlandMansionGenerator.addPieces(arg.structureTemplateManager(), blockPos2, blockRotation, list, arg.random());
		list.forEach(structurePiecesCollector::addPiece);
	}

	@Override
	public void postPlace(
		StructureWorldAccess structureWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox blockBox,
		ChunkPos chunkPos,
		StructurePiecesList structurePiecesList
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = structureWorldAccess.getBottomY();
		BlockBox blockBox2 = structurePiecesList.getBoundingBox();
		int j = blockBox2.getMinY();

		for (int k = blockBox.getMinX(); k <= blockBox.getMaxX(); k++) {
			for (int l = blockBox.getMinZ(); l <= blockBox.getMaxZ(); l++) {
				mutable.set(k, j, l);
				if (!structureWorldAccess.isAir(mutable) && blockBox2.contains(mutable) && structurePiecesList.contains(mutable)) {
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

	@Override
	public StructureType<?> getType() {
		return StructureType.WOODLAND_MANSION;
	}
}
