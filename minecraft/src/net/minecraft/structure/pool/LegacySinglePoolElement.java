package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;

public class LegacySinglePoolElement extends SinglePoolElement {
	public static final Codec<LegacySinglePoolElement> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(method_28882(), method_28880(), method_28883()).apply(instance, LegacySinglePoolElement::new)
	);

	@Deprecated
	public LegacySinglePoolElement(String string, List<StructureProcessor> list) {
		super(string, list);
	}

	private LegacySinglePoolElement(Either<Identifier, Structure> either, List<StructureProcessor> list, StructurePool.Projection projection) {
		super(either, list, projection);
	}

	@Deprecated
	public LegacySinglePoolElement(String string) {
		super(string, ImmutableList.of());
	}

	@Override
	protected StructurePlacementData createPlacementData(BlockRotation blockRotation, BlockBox blockBox, boolean bl) {
		StructurePlacementData structurePlacementData = super.createPlacementData(blockRotation, blockBox, bl);
		structurePlacementData.removeProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		structurePlacementData.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		return structurePlacementData;
	}

	@Override
	public StructurePoolElementType<?> getType() {
		return StructurePoolElementType.LEGACY_SINGLE_POOL_ELEMENT;
	}

	@Override
	public String toString() {
		return "LegacySingle[" + this.field_24015 + "]";
	}
}
