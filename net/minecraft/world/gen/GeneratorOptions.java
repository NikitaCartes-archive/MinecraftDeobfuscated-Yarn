/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class GeneratorOptions {
    public static final Codec<GeneratorOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(GeneratorOptions::getSeed), ((MapCodec)Codec.BOOL.fieldOf("generate_features")).orElse(true).stable().forGetter(GeneratorOptions::shouldGenerateStructures), ((MapCodec)Codec.BOOL.fieldOf("bonus_chest")).orElse(false).stable().forGetter(GeneratorOptions::hasBonusChest), ((MapCodec)RegistryCodecs.dynamicRegistry(Registry.DIMENSION_KEY, Lifecycle.stable(), DimensionOptions.CODEC).xmap(DimensionOptions::method_29569, Function.identity()).fieldOf("dimensions")).forGetter(GeneratorOptions::getDimensions), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(generatorOptions -> generatorOptions.legacyCustomOptions)).apply((Applicative<GeneratorOptions, ?>)instance, instance.stable(GeneratorOptions::new))).comapFlatMap(GeneratorOptions::validate, Function.identity());
    private static final Logger LOGGER = LogUtils.getLogger();
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
        return DimensionOptions.hasDefaultSettings(this.seed, this.options);
    }

    public GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, Registry<DimensionOptions> registry) {
        this(seed, generateStructures, bonusChest, registry, Optional.empty());
        DimensionOptions dimensionOptions = registry.get(DimensionOptions.OVERWORLD);
        if (dimensionOptions == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
    }

    private GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, Registry<DimensionOptions> registry, Optional<String> legacyCustomOptions) {
        this.seed = seed;
        this.generateStructures = generateStructures;
        this.bonusChest = bonusChest;
        this.options = registry;
        this.legacyCustomOptions = legacyCustomOptions;
    }

    public static GeneratorOptions createDemo(DynamicRegistryManager registryManager) {
        int i = "North Carolina".hashCode();
        return new GeneratorOptions(i, true, true, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registryManager.get(Registry.DIMENSION_TYPE_KEY), DimensionType.createDefaultDimensionOptions(registryManager, i), GeneratorOptions.createOverworldGenerator(registryManager, i)));
    }

    public static GeneratorOptions getDefaultOptions(DynamicRegistryManager registryManager) {
        long l = new Random().nextLong();
        return new GeneratorOptions(l, true, false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registryManager.get(Registry.DIMENSION_TYPE_KEY), DimensionType.createDefaultDimensionOptions(registryManager, l), GeneratorOptions.createOverworldGenerator(registryManager, l)));
    }

    public static NoiseChunkGenerator createOverworldGenerator(DynamicRegistryManager registryManager, long seed) {
        return GeneratorOptions.createOverworldGenerator(registryManager, seed, true);
    }

    public static NoiseChunkGenerator createOverworldGenerator(DynamicRegistryManager registryManager, long seed, boolean bl) {
        return GeneratorOptions.createGenerator(registryManager, seed, ChunkGeneratorSettings.OVERWORLD, bl);
    }

    public static NoiseChunkGenerator createGenerator(DynamicRegistryManager registryManager, long seed, RegistryKey<ChunkGeneratorSettings> settings) {
        return GeneratorOptions.createGenerator(registryManager, seed, settings, true);
    }

    public static NoiseChunkGenerator createGenerator(DynamicRegistryManager registryManager, long seed, RegistryKey<ChunkGeneratorSettings> settings, boolean bl) {
        return new NoiseChunkGenerator(registryManager.get(Registry.NOISE_WORLDGEN), (BiomeSource)MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(registryManager.get(Registry.BIOME_KEY), bl), seed, registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getOrCreateEntry(settings));
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

    public static Registry<DimensionOptions> getRegistryWithReplacedOverworldGenerator(Registry<DimensionType> dimensionTypeRegistry, Registry<DimensionOptions> registry, ChunkGenerator overworldGenerator) {
        DimensionOptions dimensionOptions = registry.get(DimensionOptions.OVERWORLD);
        RegistryEntry<DimensionType> registryEntry = dimensionOptions == null ? dimensionTypeRegistry.getOrCreateEntry(DimensionType.OVERWORLD_REGISTRY_KEY) : dimensionOptions.getDimensionTypeSupplier();
        return GeneratorOptions.getRegistryWithReplacedOverworld(registry, registryEntry, overworldGenerator);
    }

    public static Registry<DimensionOptions> getRegistryWithReplacedOverworld(Registry<DimensionOptions> registry, RegistryEntry<DimensionType> registryEntry, ChunkGenerator overworldGenerator) {
        SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
        ((MutableRegistry)mutableRegistry).add(DimensionOptions.OVERWORLD, new DimensionOptions(registryEntry, overworldGenerator), Lifecycle.stable());
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : registry.getEntries()) {
            RegistryKey<DimensionOptions> registryKey = entry.getKey();
            if (registryKey == DimensionOptions.OVERWORLD) continue;
            ((MutableRegistry)mutableRegistry).add(registryKey, entry.getValue(), registry.getEntryLifecycle(entry.getValue()));
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
        return this.getDimensions().getEntries().stream().map(Map.Entry::getKey).map(GeneratorOptions::toWorldKey).collect(ImmutableSet.toImmutableSet());
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

    public static GeneratorOptions fromProperties(DynamicRegistryManager registryManager, Properties properties) {
        String string2 = MoreObjects.firstNonNull((String)properties.get("generator-settings"), "");
        properties.put("generator-settings", string2);
        String string22 = MoreObjects.firstNonNull((String)properties.get("level-seed"), "");
        properties.put("level-seed", string22);
        String string3 = (String)properties.get("generate-structures");
        boolean bl = string3 == null || Boolean.parseBoolean(string3);
        properties.put("generate-structures", Objects.toString(bl));
        String string4 = (String)properties.get("level-type");
        String string5 = Optional.ofNullable(string4).map(string -> string.toLowerCase(Locale.ROOT)).orElse("default");
        properties.put("level-type", string5);
        long l = GeneratorOptions.parseSeed(string22).orElse(new Random().nextLong());
        Registry<DimensionType> registry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
        Registry<Biome> registry2 = registryManager.get(Registry.BIOME_KEY);
        Registry<DimensionOptions> registry3 = DimensionType.createDefaultDimensionOptions(registryManager, l);
        switch (string5) {
            case "flat": {
                JsonObject jsonObject = !string2.isEmpty() ? JsonHelper.deserialize(string2) : new JsonObject();
                Dynamic<JsonObject> dynamic = new Dynamic<JsonObject>(JsonOps.INSTANCE, jsonObject);
                return new GeneratorOptions(l, bl, false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, registry3, new FlatChunkGenerator(FlatChunkGeneratorConfig.CODEC.parse(dynamic).resultOrPartial(LOGGER::error).orElseGet(() -> FlatChunkGeneratorConfig.getDefaultConfig(registry2)))));
            }
            case "debug_all_block_states": {
                return new GeneratorOptions(l, bl, false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, registry3, new DebugChunkGenerator(registry2)));
            }
            case "amplified": {
                return new GeneratorOptions(l, bl, false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, registry3, GeneratorOptions.createGenerator(registryManager, l, ChunkGeneratorSettings.AMPLIFIED)));
            }
            case "largebiomes": {
                return new GeneratorOptions(l, bl, false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, registry3, GeneratorOptions.createGenerator(registryManager, l, ChunkGeneratorSettings.LARGE_BIOMES)));
            }
        }
        return new GeneratorOptions(l, bl, false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, registry3, GeneratorOptions.createOverworldGenerator(registryManager, l)));
    }

    public GeneratorOptions withHardcore(boolean hardcore, OptionalLong seed) {
        Registry<DimensionOptions> registry;
        long l = seed.orElse(this.seed);
        if (seed.isPresent()) {
            SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
            long m = seed.getAsLong();
            for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : this.options.getEntries()) {
                RegistryKey<DimensionOptions> registryKey = entry.getKey();
                ((MutableRegistry)mutableRegistry).add(registryKey, new DimensionOptions(entry.getValue().getDimensionTypeSupplier(), entry.getValue().getChunkGenerator().withSeed(m)), this.options.getEntryLifecycle(entry.getValue()));
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

