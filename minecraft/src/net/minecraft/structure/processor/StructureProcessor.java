package net.minecraft.structure.processor;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class StructureProcessor {
	@Nullable
	public abstract StructureTemplate.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos pivot,
		StructureTemplate.StructureBlockInfo originalBlockInfo,
		StructureTemplate.StructureBlockInfo currentBlockInfo,
		StructurePlacementData data
	);

	protected abstract StructureProcessorType<?> getType();

	public void reprocess(
		WorldAccess world, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, List<StructureTemplate.StructureBlockInfo> processedOnFirstRound
	) {
	}
}
