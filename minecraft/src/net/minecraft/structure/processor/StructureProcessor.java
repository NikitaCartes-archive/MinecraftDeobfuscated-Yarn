package net.minecraft.structure.processor;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;

public abstract class StructureProcessor {
	@Nullable
	public abstract Structure.StructureBlockInfo process(
		WorldView worldView,
		BlockPos pos,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	);

	protected abstract StructureProcessorType getType();

	protected abstract <T> Dynamic<T> rawToDynamic(DynamicOps<T> dynamicOps);

	public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.mergeInto(
				this.rawToDynamic(dynamicOps).getValue(),
				dynamicOps.createString("processor_type"),
				dynamicOps.createString(Registry.STRUCTURE_PROCESSOR.getId(this.getType()).toString())
			)
		);
	}
}
