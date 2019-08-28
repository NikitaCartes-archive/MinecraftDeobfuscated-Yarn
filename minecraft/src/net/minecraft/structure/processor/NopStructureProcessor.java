package net.minecraft.structure.processor;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.class_4538;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;

public class NopStructureProcessor extends StructureProcessor {
	public static final NopStructureProcessor INSTANCE = new NopStructureProcessor();

	private NopStructureProcessor() {
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		class_4538 arg,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		return structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType getType() {
		return StructureProcessorType.NOP;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
