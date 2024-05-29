package net.minecraft.structure.pool;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;

public class LegacySinglePoolElement extends SinglePoolElement {
	public static final MapCodec<LegacySinglePoolElement> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(locationGetter(), processorsGetter(), projectionGetter(), overrideLiquidSettingsGetter())
				.apply(instance, LegacySinglePoolElement::new)
	);

	protected LegacySinglePoolElement(
		Either<Identifier, StructureTemplate> either,
		RegistryEntry<StructureProcessorList> registryEntry,
		StructurePool.Projection projection,
		Optional<StructureLiquidSettings> optional
	) {
		super(either, registryEntry, projection, optional);
	}

	@Override
	protected StructurePlacementData createPlacementData(BlockRotation rotation, BlockBox box, StructureLiquidSettings liquidSettings, boolean keepJigsaws) {
		StructurePlacementData structurePlacementData = super.createPlacementData(rotation, box, liquidSettings, keepJigsaws);
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
