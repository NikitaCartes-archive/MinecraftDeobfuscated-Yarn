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
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	);

	protected abstract StructureProcessorType getType();

	protected abstract <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps);

	public <T> Dynamic<T> method_16771(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.mergeInto(
				this.method_16666(dynamicOps).getValue(),
				dynamicOps.createString("processor_type"),
				dynamicOps.createString(Registry.STRUCTURE_PROCESSOR.getId(this.getType()).toString())
			)
		);
	}
}
