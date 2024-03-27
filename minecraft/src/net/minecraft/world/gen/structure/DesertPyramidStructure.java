package net.minecraft.world.gen.structure;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.loot.LootTables;
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
	public static final MapCodec<DesertPyramidStructure> CODEC = createCodec(DesertPyramidStructure::new);

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
		Set<BlockPos> set = SortedArraySet.<BlockPos>create(Vec3i::compareTo);

		for (StructurePiece structurePiece : pieces.pieces()) {
			if (structurePiece instanceof DesertTempleGenerator desertTempleGenerator) {
				set.addAll(desertTempleGenerator.getPotentialSuspiciousSandPositions());
				placeSuspiciousSand(box, world, desertTempleGenerator.getBasementMarkerPos());
			}
		}

		ObjectArrayList<BlockPos> objectArrayList = new ObjectArrayList<>(set.stream().toList());
		Random random2 = Random.create(world.getSeed()).nextSplitter().split(pieces.getBoundingBox().getCenter());
		Util.shuffle(objectArrayList, random2);
		int i = Math.min(set.size(), random2.nextBetweenExclusive(5, 8));

		for (BlockPos blockPos : objectArrayList) {
			if (i > 0) {
				i--;
				placeSuspiciousSand(box, world, blockPos);
			} else if (box.contains(blockPos)) {
				world.setBlockState(blockPos, Blocks.SAND.getDefaultState(), Block.NOTIFY_LISTENERS);
			}
		}
	}

	private static void placeSuspiciousSand(BlockBox box, StructureWorldAccess world, BlockPos pos) {
		if (box.contains(pos)) {
			world.setBlockState(pos, Blocks.SUSPICIOUS_SAND.getDefaultState(), Block.NOTIFY_LISTENERS);
			world.getBlockEntity(pos, BlockEntityType.BRUSHABLE_BLOCK)
				.ifPresent(blockEntity -> blockEntity.setLootTable(LootTables.DESERT_PYRAMID_ARCHAEOLOGY, pos.asLong()));
		}
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.DESERT_PYRAMID;
	}
}
