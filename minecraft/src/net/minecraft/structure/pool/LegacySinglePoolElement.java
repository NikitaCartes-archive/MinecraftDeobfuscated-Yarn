package net.minecraft.structure.pool;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;

public class LegacySinglePoolElement extends SinglePoolElement {
	public static final Codec<LegacySinglePoolElement> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(locationGetter(), processorsGetter(), projectionGetter()).apply(instance, LegacySinglePoolElement::new)
	);

	protected LegacySinglePoolElement(
		Either<Identifier, StructureTemplate> either, RegistryEntry<StructureProcessorList> registryEntry, StructurePool.Projection projection
	) {
		super(either, registryEntry, projection);
	}

	@Override
	protected StructurePlacementData createPlacementData(BlockRotation rotation, BlockBox box, boolean keepJigsaws) {
		StructurePlacementData structurePlacementData = super.createPlacementData(rotation, box, keepJigsaws);
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
		return "LegacySingle[" + this.location + "]";
	}
}
