package net.minecraft.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public record StructureSet(List<StructureSet.WeightedEntry> structures, StructurePlacement placement) {
	public static final Codec<StructureSet> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StructureSet.WeightedEntry.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures),
					StructurePlacement.TYPE_CODEC.fieldOf("placement").forGetter(StructureSet::placement)
				)
				.apply(instance, StructureSet::new)
	);
	public static final Codec<RegistryEntry<StructureSet>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.STRUCTURE_SET_KEY, CODEC);

	public StructureSet(RegistryEntry<ConfiguredStructureFeature<?, ?>> structure, StructurePlacement placement) {
		this(List.of(new StructureSet.WeightedEntry(structure, 1)), placement);
	}

	public static StructureSet.WeightedEntry createEntry(RegistryEntry<ConfiguredStructureFeature<?, ?>> structure, int weight) {
		return new StructureSet.WeightedEntry(structure, weight);
	}

	public static StructureSet.WeightedEntry createEntry(RegistryEntry<ConfiguredStructureFeature<?, ?>> structure) {
		return new StructureSet.WeightedEntry(structure, 1);
	}

	public static record WeightedEntry(RegistryEntry<ConfiguredStructureFeature<?, ?>> structure, int weight) {
		public static final Codec<StructureSet.WeightedEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						ConfiguredStructureFeature.REGISTRY_CODEC.fieldOf("structure").forGetter(StructureSet.WeightedEntry::structure),
						Codecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSet.WeightedEntry::weight)
					)
					.apply(instance, StructureSet.WeightedEntry::new)
		);

		public boolean matches(Predicate<RegistryEntry<Biome>> predicate) {
			RegistryEntryList<Biome> registryEntryList = this.structure().value().getBiomes();
			return registryEntryList.stream().anyMatch(predicate);
		}
	}
}
