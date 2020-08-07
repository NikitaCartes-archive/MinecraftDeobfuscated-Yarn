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
		hashMap.put(Blocks.field_10445, Blocks.field_23869);
		hashMap.put(Blocks.field_9989, Blocks.field_23869);
		hashMap.put(Blocks.field_10340, Blocks.field_23873);
		hashMap.put(Blocks.field_10056, Blocks.field_23874);
		hashMap.put(Blocks.field_10065, Blocks.field_23874);
		hashMap.put(Blocks.field_10596, Blocks.field_23870);
		hashMap.put(Blocks.field_10207, Blocks.field_23870);
		hashMap.put(Blocks.field_10440, Blocks.field_23861);
		hashMap.put(Blocks.field_10392, Blocks.field_23878);
		hashMap.put(Blocks.field_10173, Blocks.field_23878);
		hashMap.put(Blocks.field_10351, Blocks.field_23872);
		hashMap.put(Blocks.field_10405, Blocks.field_23872);
		hashMap.put(Blocks.field_10136, Blocks.field_23862);
		hashMap.put(Blocks.field_10454, Blocks.field_23862);
		hashMap.put(Blocks.field_10131, Blocks.field_23877);
		hashMap.put(Blocks.field_10024, Blocks.field_23877);
		hashMap.put(Blocks.field_10252, Blocks.field_23879);
		hashMap.put(Blocks.field_10059, Blocks.field_23879);
		hashMap.put(Blocks.field_10625, Blocks.field_23871);
		hashMap.put(Blocks.field_9990, Blocks.field_23871);
		hashMap.put(Blocks.field_10552, Blocks.field_23876);
		hashMap.put(Blocks.field_10416, Blocks.field_23875);
		hashMap.put(Blocks.field_10576, Blocks.field_23985);
	});

	private BlackstoneReplacementStructureProcessor() {
	}

	@Override
	public Structure.StructureBlockInfo process(
		WorldView worldView,
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

			return new Structure.StructureBlockInfo(structureBlockInfo2.pos, blockState2, structureBlockInfo2.tag);
		}
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.field_24045;
	}
}
