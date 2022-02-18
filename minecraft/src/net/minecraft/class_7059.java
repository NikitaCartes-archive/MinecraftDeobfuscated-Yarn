package net.minecraft;

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

public record class_7059(List<class_7059.class_7060> structures, StructurePlacement placement) {
	public static final Codec<class_7059> field_37195 = RecordCodecBuilder.create(
		instance -> instance.group(
					class_7059.class_7060.field_37197.listOf().fieldOf("structures").forGetter(class_7059::structures),
					StructurePlacement.TYPE_CODEC.fieldOf("placement").forGetter(class_7059::placement)
				)
				.apply(instance, class_7059::new)
	);
	public static final Codec<RegistryEntry<class_7059>> field_37196 = RegistryElementCodec.of(Registry.STRUCTURE_SET_WORLDGEN, field_37195);

	public class_7059(RegistryEntry<ConfiguredStructureFeature<?, ?>> registryEntry, StructurePlacement structurePlacement) {
		this(List.of(new class_7059.class_7060(registryEntry, 1)), structurePlacement);
	}

	public static class_7059.class_7060 method_41146(RegistryEntry<ConfiguredStructureFeature<?, ?>> registryEntry, int i) {
		return new class_7059.class_7060(registryEntry, i);
	}

	public static class_7059.class_7060 method_41145(RegistryEntry<ConfiguredStructureFeature<?, ?>> registryEntry) {
		return new class_7059.class_7060(registryEntry, 1);
	}

	public static record class_7060(RegistryEntry<ConfiguredStructureFeature<?, ?>> structure, int weight) {
		public static final Codec<class_7059.class_7060> field_37197 = RecordCodecBuilder.create(
			instance -> instance.group(
						ConfiguredStructureFeature.REGISTRY_CODEC.fieldOf("structure").forGetter(class_7059.class_7060::structure),
						Codecs.POSITIVE_INT.fieldOf("weight").forGetter(class_7059.class_7060::weight)
					)
					.apply(instance, class_7059.class_7060::new)
		);

		public boolean method_41148(Predicate<RegistryEntry<Biome>> predicate) {
			RegistryEntryList<Biome> registryEntryList = this.structure().value().getBiomes();
			return registryEntryList.stream().anyMatch(predicate);
		}
	}
}
