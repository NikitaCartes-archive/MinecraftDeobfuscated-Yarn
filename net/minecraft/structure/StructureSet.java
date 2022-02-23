/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

public record StructureSet(List<WeightedEntry> structures, StructurePlacement placement) {
    public static final Codec<StructureSet> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)WeightedEntry.CODEC.listOf().fieldOf("structures")).forGetter(StructureSet::structures), ((MapCodec)StructurePlacement.TYPE_CODEC.fieldOf("placement")).forGetter(StructureSet::placement)).apply((Applicative<StructureSet, ?>)instance, StructureSet::new));
    public static final Codec<RegistryEntry<StructureSet>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.STRUCTURE_SET_KEY, CODEC);

    public StructureSet(RegistryEntry<ConfiguredStructureFeature<?, ?>> structure, StructurePlacement placement) {
        this(List.of(new WeightedEntry(structure, 1)), placement);
    }

    public static WeightedEntry createEntry(RegistryEntry<ConfiguredStructureFeature<?, ?>> structure, int weight) {
        return new WeightedEntry(structure, weight);
    }

    public static WeightedEntry createEntry(RegistryEntry<ConfiguredStructureFeature<?, ?>> structure) {
        return new WeightedEntry(structure, 1);
    }

    public record WeightedEntry(RegistryEntry<ConfiguredStructureFeature<?, ?>> structure, int weight) {
        public static final Codec<WeightedEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ConfiguredStructureFeature.REGISTRY_CODEC.fieldOf("structure")).forGetter(WeightedEntry::structure), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("weight")).forGetter(WeightedEntry::weight)).apply((Applicative<WeightedEntry, ?>)instance, WeightedEntry::new));

        public boolean matches(Predicate<RegistryEntry<Biome>> predicate) {
            RegistryEntryList<Biome> registryEntryList = this.structure().value().getBiomes();
            return registryEntryList.stream().anyMatch(predicate);
        }
    }
}

