package net.minecraft.structure.processor;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class BlackstoneReplacementStructureProcessor extends StructureProcessor {
	public static final Codec<BlackstoneReplacementStructureProcessor> CODEC = Codec.unit(
		(Supplier<BlackstoneReplacementStructureProcessor>)(() -> BlackstoneReplacementStructureProcessor.INSTANCE)
	);
	public static final BlackstoneReplacementStructureProcessor INSTANCE = new BlackstoneReplacementStructureProcessor();
	private final Map<Block, Block> replacementMap = Util.make(Maps.<Block, Block>newHashMap(), hashMap -> {
		hashMap.put(Blocks.COBBLESTONE, Blocks.BLACKSTONE);
		hashMap.put(Blocks.MOSSY_COBBLESTONE, Blocks.BLACKSTONE);
		hashMap.put(Blocks.STONE, Blocks.POLISHED_BLACKSTONE);
		hashMap.put(Blocks.STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
		hashMap.put(Blocks.MOSSY_STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
		hashMap.put(Blocks.COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
		hashMap.put(Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
		hashMap.put(Blocks.STONE_STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS);
		hashMap.put(Blocks.STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
		hashMap.put(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
		hashMap.put(Blocks.COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
		hashMap.put(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
		hashMap.put(Blocks.SMOOTH_STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
		hashMap.put(Blocks.STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
		hashMap.put(Blocks.STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
		hashMap.put(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
		hashMap.put(Blocks.STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
		hashMap.put(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
		hashMap.put(Blocks.COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
		hashMap.put(Blocks.MOSSY_COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
		hashMap.put(Blocks.CHISELED_STONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE);
		hashMap.put(Blocks.CRACKED_STONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
		hashMap.put(Blocks.IRON_BARS, Blocks.CHAIN);
	});

	private BlackstoneReplacementStructureProcessor() {
	}

	@Override
	public Structure.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		Block block = (Block)this.replacementMap.get(structureBlockInfo2.state.getBlock());
		if (block == null) {
			return structureBlockInfo2;
		} else {
			BlockState blockState = structureBlockInfo2.state;
			BlockState blockState2 = block.getDefaultState();
			if (blockState.contains(StairsBlock.FACING)) {
				blockState2 = blockState2.with(StairsBlock.FACING, blockState.get(StairsBlock.FACING));
			}

			if (blockState.contains(StairsBlock.HALF)) {
				blockState2 = blockState2.with(StairsBlock.HALF, blockState.get(StairsBlock.HALF));
			}

			if (blockState.contains(SlabBlock.TYPE)) {
				blockState2 = blockState2.with(SlabBlock.TYPE, blockState.get(SlabBlock.TYPE));
			}

			return new Structure.StructureBlockInfo(structureBlockInfo2.pos, blockState2, structureBlockInfo2.nbt);
		}
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.BLACKSTONE_REPLACE;
	}
}
