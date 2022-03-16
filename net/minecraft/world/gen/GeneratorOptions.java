/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import org.apache.commons.lang3.StringUtils;

public class GeneratorOptions {
    public static final Codec<GeneratorOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(GeneratorOptions::getSeed), ((MapCodec)Codec.BOOL.fieldOf("generate_features")).orElse(true).stable().forGetter(GeneratorOptions::shouldGenerateStructures), ((MapCodec)Codec.BOOL.fieldOf("bonus_chest")).orElse(false).stable().forGetter(GeneratorOptions::hasBonusChest), ((MapCodec)RegistryCodecs.dynamicRegistry(Registry.DIMENSION_KEY, Lifecycle.stable(), DimensionOptions.CODEC).xmap(DimensionOptions::createRegistry, Function.identity()).fieldOf("dimensions")).forGetter(GeneratorOptions::getDimensions), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(generatorOptions -> generatorOptions.legacyCustomOptions)).apply((Applicative<GeneratorOptions, ?>)instance, instance.stable(GeneratorOptions::new))).comapFlatMap(GeneratorOptions::validate, Function.identity());
    private final long seed;
    private final boolean generateStructures;
    private final boolean bonusChest;
    private final Registry<DimensionOptions> options;
    private final Optional<String> legacyCustomOptions;

    private DataResult<GeneratorOptions> validate() {
        DimensionOptions dimensionOptions = this.options.get(DimensionOptions.OVERWORLD);
        if (dimensionOptions == null) {
            return DataResult.error("Overworld settings missing");
        }
        if (this.isStable()) {
            return DataResult.success(this, Lifecycle.stable());
        }
        return DataResult.success(this);
    }

    private boolean isStable() {
        return DimensionOptions.hasDefaultSettings(this.options);
    }

    public GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, Registry<DimensionOptions> options) {
        this(seed, generateStructures, bonusChest, options, Optional.empty());
        DimensionOptions dimensionOptions = options.get(DimensionOptions.OVERWORLD);
        if (dimensionOptions == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
    }

    private GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, Registry<DimensionOptions> options, Optional<String> legacyCustomOptions) {
        this.seed = seed;
        this.generateStructures = generateStructures;
        this.bonusChest = bonusChest;
        this.options = options;
        this.legacyCustomOptions = legacyCustomOptions;
    }

    public long getSeed() {
        return this.seed;
    }

    public boolean shouldGenerateStructures() {
        return this.generateStructures;
    }

    public boolean hasBonusChest() {
        return this.bonusChest;
    }

    public static GeneratorOptions create(DynamicRegistryManager dynamicRegistryManager, GeneratorOptions generatorOptions, ChunkGenerator chunkGenerator) {
        Registry<DimensionType> registry = dynamicRegistryManager.get(Registry.DIMENSION_TYPE_KEY);
        Registry<DimensionOptions> registry2 = GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, generatorOptions.getDimensions(), chunkGenerator);
        return new GeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest(), registry2);
    }

    public static Registry<DimensionOptions> getRegistryWithReplacedOverworldGenerator(Registry<DimensionType> dimensionTypeRegistry, Registry<DimensionOptions> options, ChunkGenerator overworldGenerator) {
        DimensionOptions dimensionOptions = options.get(DimensionOptions.OVERWORLD);
        RegistryEntry<DimensionType> registryEntry = dimensionOptions == null ? dimensionTypeRegistry.getOrCreateEntry(DimensionTypes.OVERWORLD) : dimensionOptions.getDimensionTypeEntry();
        return GeneratorOptions.getRegistryWithReplacedOverworld(options, registryEntry, overworldGenerator);
    }

    public static Registry<DimensionOptions> getRegistryWithReplacedOverworld(Registry<DimensionOptions> options, RegistryEntry<DimensionType> dimensionType, ChunkGenerator overworldGenerator) {
        SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
        ((MutableRegistry)mutableRegistry).add(DimensionOptions.OVERWORLD, new DimensionOptions(dimensionType, overworldGenerator), Lifecycle.stable());
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : options.getEntrySet()) {
            RegistryKey<DimensionOptions> registryKey = entry.getKey();
            if (registryKey == DimensionOptions.OVERWORLD) continue;
            ((MutableRegistry)mutableRegistry).add(registryKey, entry.getValue(), options.getEntryLifecycle(entry.getValue()));
        }
        return mutableRegistry;
    }

    public Registry<DimensionOptions> getDimensions() {
        return this.options;
    }

    public ChunkGenerator getChunkGenerator() {
        DimensionOptions dimensionOptions = this.options.get(DimensionOptions.OVERWORLD);
        if (dimensionOptions == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
        return dimensionOptions.getChunkGenerator();
    }

    public ImmutableSet<RegistryKey<World>> getWorlds() {
        return this.getDimensions().getEntrySet().stream().map(Map.Entry::getKey).map(GeneratorOptions::toWorldKey).collect(ImmutableSet.toImmutableSet());
    }

    public static RegistryKey<World> toWorldKey(RegistryKey<DimensionOptions> dimensionOptionsKey) {
        return RegistryKey.of(Registry.WORLD_KEY, dimensionOptionsKey.getValue());
    }

    public static RegistryKey<DimensionOptions> toDimensionOptionsKey(RegistryKey<World> worldKey) {
        return RegistryKey.of(Registry.DIMENSION_KEY, worldKey.getValue());
    }

    public boolean isDebugWorld() {
        return this.getChunkGenerator() instanceof DebugChunkGenerator;
    }

    public boolean isFlatWorld() {
        return this.getChunkGenerator() instanceof FlatChunkGenerator;
    }

    public boolean isLegacyCustomizedType() {
        return this.legacyCustomOptions.isPresent();
    }

    public GeneratorOptions withBonusChest() {
        return new GeneratorOptions(this.seed, this.generateStructures, true, this.options, this.legacyCustomOptions);
    }

    public GeneratorOptions toggleGenerateStructures() {
        return new GeneratorOptions(this.seed, !this.generateStructures, this.bonusChest, this.options);
    }

    public GeneratorOptions toggleBonusChest() {
        return new GeneratorOptions(this.seed, this.generateStructures, !this.bonusChest, this.options);
    }

    public GeneratorOptions withHardcore(boolean hardcore, OptionalLong seed) {
        Registry<DimensionOptions> registry;
        long l = seed.orElse(this.seed);
        if (seed.isPresent()) {
            SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
            for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : this.options.getEntrySet()) {
                RegistryKey<DimensionOptions> registryKey = entry.getKey();
                ((MutableRegistry)mutableRegistry).add(registryKey, new DimensionOptions(entry.getValue().getDimensionTypeEntry(), entry.getValue().getChunkGenerator()), this.options.getEntryLifecycle(entry.getValue()));
            }
            registry = mutableRegistry;
        } else {
            registry = this.options;
        }
        GeneratorOptions generatorOptions = this.isDebugWorld() ? new GeneratorOptions(l, false, false, registry) : new GeneratorOptions(l, this.shouldGenerateStructures(), this.hasBonusChest() && !hardcore, registry);
        return generatorOptions;
    }

    public static OptionalLong parseSeed(String seed) {
        if (StringUtils.isEmpty(seed = seed.trim())) {
            return OptionalLong.empty();
        }
        try {
            return OptionalLong.of(Long.parseLong(seed));
        } catch (NumberFormatException numberFormatException) {
            return OptionalLong.of(seed.hashCode());
        }
    }
}

