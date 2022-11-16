package net.minecraft.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.structure.Structure;

public record StructureSet(List<StructureSet.WeightedEntry> structures, StructurePlacement placement) {
	public static final Codec<StructureSet> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StructureSet.WeightedEntry.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures),
					StructurePlacement.TYPE_CODEC.fieldOf("placement").forGetter(StructureSet::placement)
				)
				.apply(instance, StructureSet::new)
	);
	public static final Codec<RegistryEntry<StructureSet>> REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.STRUCTURE_SET, CODEC);

	public StructureSet(RegistryEntry<Structure> structure, StructurePlacement placement) {
		this(List.of(new StructureSet.WeightedEntry(structure, 1)), placement);
	}

	public static StructureSet.WeightedEntry createEntry(RegistryEntry<Structure> structure, int weight) {
		return new StructureSet.WeightedEntry(structure, weight);
	}

	public static StructureSet.WeightedEntry createEntry(RegistryEntry<Structure> structure) {
		return new StructureSet.WeightedEntry(structure, 1);
	}

	public static record WeightedEntry(RegistryEntry<Structure> structure, int weight) {
		public static final Codec<StructureSet.WeightedEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Structure.ENTRY_CODEC.fieldOf("structure").forGetter(StructureSet.WeightedEntry::structure),
						Codecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSet.WeightedEntry::weight)
					)
					.apply(instance, StructureSet.WeightedEntry::new)
		);
	}
}
