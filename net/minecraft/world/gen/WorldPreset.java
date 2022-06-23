/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;

public class WorldPreset {
    public static final Codec<WorldPreset> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.unboundedMap(RegistryKey.createCodec(Registry.DIMENSION_KEY), DimensionOptions.CODEC).fieldOf("dimensions")).forGetter(preset -> preset.dimensions)).apply((Applicative<WorldPreset, ?>)instance, WorldPreset::new)).flatXmap(WorldPreset::validate, WorldPreset::validate);
    public static final Codec<RegistryEntry<WorldPreset>> ENTRY_CODEC = RegistryElementCodec.of(Registry.WORLD_PRESET_KEY, CODEC);
    private final Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions;

    public WorldPreset(Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions) {
        this.dimensions = dimensions;
    }

    private Registry<DimensionOptions> createDimensionOptionsRegistry() {
        SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
        DimensionOptions.streamRegistry(this.dimensions.keySet().stream()).forEach(registryKey -> {
            DimensionOptions dimensionOptions = this.dimensions.get(registryKey);
            if (dimensionOptions != null) {
                mutableRegistry.add((RegistryKey<DimensionOptions>)registryKey, dimensionOptions, Lifecycle.stable());
            }
        });
        return ((Registry)mutableRegistry).freeze();
    }

    public GeneratorOptions createGeneratorOptions(long seed, boolean generateStructures, boolean bonusChest) {
        return new GeneratorOptions(seed, generateStructures, bonusChest, this.createDimensionOptionsRegistry());
    }

    public GeneratorOptions createGeneratorOptions(GeneratorOptions generatorOptions) {
        return this.createGeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest());
    }

    public Optional<DimensionOptions> getOverworld() {
        return Optional.ofNullable(this.dimensions.get(DimensionOptions.OVERWORLD));
    }

    public DimensionOptions getOverworldOrElseThrow() {
        return this.getOverworld().orElseThrow(() -> new IllegalStateException("Can't find overworld in this preset"));
    }

    private static DataResult<WorldPreset> validate(WorldPreset preset) {
        if (preset.getOverworld().isEmpty()) {
            return DataResult.error("Missing overworld dimension");
        }
        return DataResult.success(preset, Lifecycle.stable());
    }
}

