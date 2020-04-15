package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;

public class LegacySinglePoolElement extends SinglePoolElement {
	@Deprecated
	public LegacySinglePoolElement(String string, List<StructureProcessor> list) {
		super(string, list, StructurePool.Projection.RIGID);
	}

	@Deprecated
	public LegacySinglePoolElement(String string) {
		super(string, ImmutableList.of());
	}

	public LegacySinglePoolElement(Dynamic<?> dynamic) {
		super(dynamic);
	}

	@Override
	protected StructurePlacementData createPlacementData(BlockRotation blockRotation, BlockBox blockBox, boolean bl) {
		StructurePlacementData structurePlacementData = super.createPlacementData(blockRotation, blockBox, bl);
		structurePlacementData.removeProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		structurePlacementData.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		return structurePlacementData;
	}

	@Override
	public StructurePoolElementType getType() {
		return StructurePoolElementType.LEGACY_SINGLE_POOL_ELEMENT;
	}

	@Override
	public String toString() {
		return "LegacySingle[" + this.field_24015 + "]";
	}
}
