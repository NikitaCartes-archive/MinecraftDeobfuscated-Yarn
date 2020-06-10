/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.datafixers.kinds.Applicative;
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
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorOptions {
    public static final Codec<GeneratorOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(GeneratorOptions::getSeed), ((MapCodec)Codec.BOOL.fieldOf("generate_features")).withDefault(true).stable().forGetter(GeneratorOptions::shouldGenerateStructures), ((MapCodec)Codec.BOOL.fieldOf("bonus_chest")).withDefault(false).stable().forGetter(GeneratorOptions::hasBonusChest), ((MapCodec)SimpleRegistry.createCodec(Registry.DIMENSION_OPTIONS, Lifecycle.stable(), DimensionOptions.CODEC).xmap(DimensionOptions::method_29569, Function.identity()).fieldOf("dimensions")).forGetter(GeneratorOptions::getDimensionMap), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(generatorOptions -> generatorOptions.legacyCustomOptions)).apply((Applicative<GeneratorOptions, ?>)instance, instance.stable(GeneratorOptions::new))).comapFlatMap(GeneratorOptions::method_28610, Function.identity());
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int DEMO_SEED = "North Carolina".hashCode();
    public static final GeneratorOptions DEMO_CONFIG = new GeneratorOptions(DEMO_SEED, true, true, GeneratorOptions.method_28608(DimensionType.method_28517(DEMO_SEED), GeneratorOptions.createOverworldGenerator(DEMO_SEED)));
    private final long seed;
    private final boolean generateStructures;
    private final boolean bonusChest;
    private final SimpleRegistry<DimensionOptions> field_24827;
    private final Optional<String> legacyCustomOptions;

    private DataResult<GeneratorOptions> method_28610() {
        if (this.method_28611()) {
            return DataResult.success(this, Lifecycle.stable());
        }
        return DataResult.success(this);
    }

    private boolean method_28611() {
        return DimensionOptions.method_29567(this.seed, this.field_24827);
    }

    public GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<DimensionOptions> simpleRegistry) {
        this(seed, generateStructures, bonusChest, simpleRegistry, Optional.empty());
    }

    private GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<DimensionOptions> simpleRegistry, Optional<String> legacyCustomOptions) {
        this.seed = seed;
        this.generateStructures = generateStructures;
        this.bonusChest = bonusChest;
        this.field_24827 = simpleRegistry;
        this.legacyCustomOptions = legacyCustomOptions;
    }

    public static GeneratorOptions getDefaultOptions() {
        long l = new Random().nextLong();
        return new GeneratorOptions(l, true, false, GeneratorOptions.method_28608(DimensionType.method_28517(l), GeneratorOptions.createOverworldGenerator(l)));
    }

    public static SurfaceChunkGenerator createOverworldGenerator(long seed) {
        return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(seed, false, false), seed, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
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

    public static SimpleRegistry<DimensionOptions> method_28608(SimpleRegistry<DimensionOptions> simpleRegistry, ChunkGenerator chunkGenerator) {
        DimensionOptions dimensionOptions = simpleRegistry.get(DimensionOptions.OVERWORLD);
        Supplier<DimensionType> supplier = () -> dimensionOptions == null ? DimensionType.getOverworldDimensionType() : dimensionOptions.getDimensionType();
        return GeneratorOptions.method_29962(simpleRegistry, supplier, chunkGenerator);
    }

    public static SimpleRegistry<DimensionOptions> method_29962(SimpleRegistry<DimensionOptions> simpleRegistry, Supplier<DimensionType> supplier, ChunkGenerator chunkGenerator) {
        SimpleRegistry<DimensionOptions> simpleRegistry2 = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
        simpleRegistry2.add(DimensionOptions.OVERWORLD, new DimensionOptions(supplier, chunkGenerator));
        simpleRegistry2.markLoaded(DimensionOptions.OVERWORLD);
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : simpleRegistry.getEntries()) {
            RegistryKey<DimensionOptions> registryKey = entry.getKey();
            if (registryKey == DimensionOptions.OVERWORLD) continue;
            simpleRegistry2.add(registryKey, entry.getValue());
            if (!simpleRegistry.isLoaded(registryKey)) continue;
            simpleRegistry2.markLoaded(registryKey);
        }
        return simpleRegistry2;
    }

    public SimpleRegistry<DimensionOptions> getDimensionMap() {
        return this.field_24827;
    }

    public ChunkGenerator getChunkGenerator() {
        DimensionOptions dimensionOptions = this.field_24827.get(DimensionOptions.OVERWORLD);
        if (dimensionOptions == null) {
            return GeneratorOptions.createOverworldGenerator(new Random().nextLong());
        }
        return dimensionOptions.getChunkGenerator();
    }

    public ImmutableSet<RegistryKey<World>> method_29575() {
        return this.getDimensionMap().getEntries().stream().map(entry -> RegistryKey.of(Registry.DIMENSION, ((RegistryKey)entry.getKey()).getValue())).collect(ImmutableSet.toImmutableSet());
    }

    public boolean isDebugWorld() {
        return this.getChunkGenerator() instanceof DebugChunkGenerator;
    }

    public boolean isFlatWorld() {
        return this.getChunkGenerator() instanceof FlatChunkGenerator;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLegacyCustomizedType() {
        return this.legacyCustomOptions.isPresent();
    }

    public GeneratorOptions withBonusChest() {
        return new GeneratorOptions(this.seed, this.generateStructures, true, this.field_24827, this.legacyCustomOptions);
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions toggleGenerateStructures() {
        return new GeneratorOptions(this.seed, !this.generateStructures, this.bonusChest, this.field_24827);
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions toggleBonusChest() {
        return new GeneratorOptions(this.seed, this.generateStructures, !this.bonusChest, this.field_24827);
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions method_29573(SimpleRegistry<DimensionOptions> simpleRegistry) {
        return new GeneratorOptions(this.seed, this.generateStructures, this.bonusChest, simpleRegistry);
    }

    public static GeneratorOptions fromProperties(Properties properties) {
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
        long l = new Random().nextLong();
        if (!string22.isEmpty()) {
            try {
                long m = Long.parseLong(string22);
                if (m != 0L) {
                    l = m;
                }
            } catch (NumberFormatException numberFormatException) {
                l = string22.hashCode();
            }
        }
        SimpleRegistry<DimensionOptions> simpleRegistry = DimensionType.method_28517(l);
        switch (string5) {
            case "flat": {
                JsonObject jsonObject = !string2.isEmpty() ? JsonHelper.deserialize(string2) : new JsonObject();
                Dynamic<JsonObject> dynamic = new Dynamic<JsonObject>(JsonOps.INSTANCE, jsonObject);
                return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(simpleRegistry, new FlatChunkGenerator(FlatChunkGeneratorConfig.CODEC.parse(dynamic).resultOrPartial(LOGGER::error).orElseGet(FlatChunkGeneratorConfig::getDefaultConfig))));
            }
            case "debug_all_block_states": {
                return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(simpleRegistry, DebugChunkGenerator.INSTANCE));
            }
        }
        return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(simpleRegistry, GeneratorOptions.createOverworldGenerator(l)));
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions withHardcore(boolean hardcore, OptionalLong seed) {
        SimpleRegistry<DimensionOptions> simpleRegistry;
        long l = seed.orElse(this.seed);
        if (seed.isPresent()) {
            simpleRegistry = new SimpleRegistry(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
            long m = seed.getAsLong();
            for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : this.field_24827.getEntries()) {
                RegistryKey<DimensionOptions> registryKey = entry.getKey();
                simpleRegistry.add(registryKey, new DimensionOptions(entry.getValue().getDimensionTypeSupplier(), entry.getValue().getChunkGenerator().withSeed(m)));
                if (!this.field_24827.isLoaded(registryKey)) continue;
                simpleRegistry.markLoaded(registryKey);
            }
        } else {
            simpleRegistry = this.field_24827;
        }
        GeneratorOptions generatorOptions = this.isDebugWorld() ? new GeneratorOptions(l, false, false, simpleRegistry) : new GeneratorOptions(l, this.shouldGenerateStructures(), this.hasBonusChest() && !hardcore, simpleRegistry);
        return generatorOptions;
    }
}

