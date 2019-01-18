package net.minecraft.sortme.structures.processor;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.sortme.Structure;
import net.minecraft.sortme.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class NopStructureProcessor extends AbstractStructureProcessor {
	public static final NopStructureProcessor INSTANCE = new NopStructureProcessor();

	private NopStructureProcessor() {
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		ViewableWorld viewableWorld,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		return structureBlockInfo2;
	}

	@Override
	protected StructureProcessor getStructureProcessor() {
		return StructureProcessor.field_16987;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
