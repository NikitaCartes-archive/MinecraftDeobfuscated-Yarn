package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.loot.OneTwentyLootTables;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.util.Util;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DesertPyramidStructure extends BasicTempleStructure {
	public static final Codec<DesertPyramidStructure> CODEC = createCodec(DesertPyramidStructure::new);

	public DesertPyramidStructure(Structure.Config config) {
		super(DesertTempleGenerator::new, 21, 21, config);
	}

	@Override
	public void postPlace(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox box,
		ChunkPos chunkPos,
		StructurePiecesList pieces
	) {
		if (world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20)) {
			Set<BlockPos> set = SortedArraySet.<BlockPos>create(Vec3i::compareTo);

			for (StructurePiece structurePiece : pieces.pieces()) {
				if (structurePiece instanceof DesertTempleGenerator desertTempleGenerator) {
					set.addAll(desertTempleGenerator.getPotentialSuspiciousSandPositions());
				}
			}

			ObjectArrayList<BlockPos> objectArrayList = new ObjectArrayList<>(set.stream().toList());
			Util.shuffle(objectArrayList, random);
			int i = Math.min(set.size(), random.nextBetweenExclusive(5, 8));

			for (BlockPos blockPos : objectArrayList) {
				if (i > 0) {
					i--;
					world.setBlockState(blockPos, Blocks.SUSPICIOUS_SAND.getDefaultState(), Block.NOTIFY_LISTENERS);
					world.getBlockEntity(blockPos, BlockEntityType.SUSPICIOUS_SAND)
						.ifPresent(blockEntity -> blockEntity.setLootTable(OneTwentyLootTables.DESERT_PYRAMID_ARCHAEOLOGY, blockPos.asLong()));
				} else {
					world.setBlockState(blockPos, Blocks.SAND.getDefaultState(), Block.NOTIFY_LISTENERS);
				}
			}
		}
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.DESERT_PYRAMID;
	}
}
