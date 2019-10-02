package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class BlockIgnoreStructureProcessor extends StructureProcessor {
	public static final BlockIgnoreStructureProcessor IGNORE_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK));
	public static final BlockIgnoreStructureProcessor IGNORE_AIR = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.AIR));
	public static final BlockIgnoreStructureProcessor IGNORE_AIR_AND_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor(
		ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK)
	);
	private final ImmutableList<Block> blocks;

	public BlockIgnoreStructureProcessor(List<Block> list) {
		this.blocks = ImmutableList.copyOf(list);
	}

	public BlockIgnoreStructureProcessor(Dynamic<?> dynamic) {
		this(dynamic.get("blocks").asList(dynamicx -> BlockState.deserialize(dynamicx).getBlock()));
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView worldView,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		return this.blocks.contains(structureBlockInfo2.state.getBlock()) ? null : structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType getType() {
		return StructureProcessorType.BLOCK_IGNORE;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("blocks"),
					dynamicOps.createList(this.blocks.stream().map(block -> BlockState.serialize(dynamicOps, block.getDefaultState()).getValue()))
				)
			)
		);
	}
}
