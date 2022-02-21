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

public record StructureSet(List<StructureSet.class_7060> structures, StructurePlacement placement) {
	public static final Codec<StructureSet> field_37195 = RecordCodecBuilder.create(
		instance -> instance.group(
					StructureSet.class_7060.field_37197.listOf().fieldOf("structures").forGetter(StructureSet::structures),
					StructurePlacement.TYPE_CODEC.fieldOf("placement").forGetter(StructureSet::placement)
				)
				.apply(instance, StructureSet::new)
	);
	public static final Codec<RegistryEntry<StructureSet>> field_37196 = RegistryElementCodec.of(Registry.STRUCTURE_SET_KEY, field_37195);

	public StructureSet(RegistryEntry<ConfiguredStructureFeature<?, ?>> registryEntry, StructurePlacement structurePlacement) {
		this(List.of(new StructureSet.class_7060(registryEntry, 1)), structurePlacement);
	}

	public static StructureSet.class_7060 method_41146(RegistryEntry<ConfiguredStructureFeature<?, ?>> registryEntry, int i) {
		return new StructureSet.class_7060(registryEntry, i);
	}

	public static StructureSet.class_7060 method_41145(RegistryEntry<ConfiguredStructureFeature<?, ?>> registryEntry) {
		return new StructureSet.class_7060(registryEntry, 1);
	}

	public static record class_7060(RegistryEntry<ConfiguredStructureFeature<?, ?>> structure, int weight) {
		public static final Codec<StructureSet.class_7060> field_37197 = RecordCodecBuilder.create(
			instance -> instance.group(
						ConfiguredStructureFeature.REGISTRY_CODEC.fieldOf("structure").forGetter(StructureSet.class_7060::structure),
						Codecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSet.class_7060::weight)
					)
					.apply(instance, StructureSet.class_7060::new)
		);

		public boolean method_41148(Predicate<RegistryEntry<Biome>> predicate) {
			RegistryEntryList<Biome> registryEntryList = this.structure().value().getBiomes();
			return registryEntryList.stream().anyMatch(predicate);
		}
	}
}
