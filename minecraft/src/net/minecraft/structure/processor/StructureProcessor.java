package net.minecraft.structure.processor;

import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public abstract class StructureProcessor {
	@Nullable
	public abstract Structure.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos pivot,
		Structure.StructureBlockInfo originalBlockInfo,
		Structure.StructureBlockInfo currentBlockInfo,
		StructurePlacementData data
	);

	protected abstract StructureProcessorType<?> getType();
}
