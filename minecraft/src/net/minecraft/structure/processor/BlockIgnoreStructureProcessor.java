package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class BlockIgnoreStructureProcessor extends StructureProcessor {
	public static final Codec<BlockIgnoreStructureProcessor> CODEC = BlockState.CODEC
		.xmap(AbstractBlock.AbstractBlockState::getBlock, Block::getDefaultState)
		.listOf()
		.fieldOf("blocks")
		.<BlockIgnoreStructureProcessor>xmap(BlockIgnoreStructureProcessor::new, blockIgnoreStructureProcessor -> blockIgnoreStructureProcessor.blocks)
		.codec();
	public static final BlockIgnoreStructureProcessor IGNORE_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.field_10465));
	public static final BlockIgnoreStructureProcessor IGNORE_AIR = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.field_10124));
	public static final BlockIgnoreStructureProcessor IGNORE_AIR_AND_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor(
		ImmutableList.of(Blocks.field_10124, Blocks.field_10465)
	);
	private final ImmutableList<Block> blocks;

	public BlockIgnoreStructureProcessor(List<Block> blocks) {
		this.blocks = ImmutableList.copyOf(blocks);
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView worldView,
		BlockPos pos,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		return this.blocks.contains(structureBlockInfo2.state.getBlock()) ? null : structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.field_16986;
	}
}
