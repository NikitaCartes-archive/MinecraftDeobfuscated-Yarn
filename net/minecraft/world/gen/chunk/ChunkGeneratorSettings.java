/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_6954;
import net.minecraft.class_7056;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;

public record ChunkGeneratorSettings(GenerationShapeConfig generationShapeConfig, BlockState defaultBlock, BlockState defaultFluid, class_7056 noiseRouter, MaterialRules.MaterialRule surfaceRule, int seaLevel, boolean mobGenerationDisabled, boolean aquifers, boolean oreVeins, boolean usesLegacyRandom) {
    private final boolean oreVeins;
    public static final Codec<ChunkGeneratorSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)GenerationShapeConfig.CODEC.fieldOf("noise")).forGetter(ChunkGeneratorSettings::generationShapeConfig), ((MapCodec)BlockState.CODEC.fieldOf("default_block")).forGetter(ChunkGeneratorSettings::defaultBlock), ((MapCodec)BlockState.CODEC.fieldOf("default_fluid")).forGetter(ChunkGeneratorSettings::defaultFluid), ((MapCodec)class_7056.field_37137.fieldOf("noise_router")).forGetter(ChunkGeneratorSettings::noiseRouter), ((MapCodec)MaterialRules.MaterialRule.CODEC.fieldOf("surface_rule")).forGetter(ChunkGeneratorSettings::surfaceRule), ((MapCodec)Codec.INT.fieldOf("sea_level")).forGetter(ChunkGeneratorSettings::seaLevel), ((MapCodec)Codec.BOOL.fieldOf("disable_mob_generation")).forGetter(ChunkGeneratorSettings::mobGenerationDisabled), ((MapCodec)Codec.BOOL.fieldOf("aquifers_enabled")).forGetter(ChunkGeneratorSettings::hasAquifers), ((MapCodec)Codec.BOOL.fieldOf("ore_veins_enabled")).forGetter(ChunkGeneratorSettings::oreVeins), ((MapCodec)Codec.BOOL.fieldOf("legacy_random_source")).forGetter(ChunkGeneratorSettings::usesLegacyRandom)).apply((Applicative<ChunkGeneratorSettings, ?>)instance, ChunkGeneratorSettings::new));
    public static final Codec<RegistryEntry<ChunkGeneratorSettings>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, CODEC);
    public static final RegistryKey<ChunkGeneratorSettings> OVERWORLD = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("overworld"));
    public static final RegistryKey<ChunkGeneratorSettings> LARGE_BIOMES = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("large_biomes"));
    public static final RegistryKey<ChunkGeneratorSettings> AMPLIFIED = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("amplified"));
    public static final RegistryKey<ChunkGeneratorSettings> NETHER = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("nether"));
    public static final RegistryKey<ChunkGeneratorSettings> END = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("end"));
    public static final RegistryKey<ChunkGeneratorSettings> CAVES = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("caves"));
    public static final RegistryKey<ChunkGeneratorSettings> FLOATING_ISLANDS = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("floating_islands"));

    /**
     * Whether entities will be generated during chunk population.
     * 
     * <p>It does not control whether spawns will occur during gameplay.
     */
    @Deprecated
    public boolean mobGenerationDisabled() {
        return this.mobGenerationDisabled;
    }

    public boolean hasAquifers() {
        return this.aquifers;
    }

    public boolean oreVeins() {
        return this.oreVeins;
    }

    public ChunkRandom.RandomProvider getRandomProvider() {
        return this.usesLegacyRandom ? ChunkRandom.RandomProvider.LEGACY : ChunkRandom.RandomProvider.XOROSHIRO;
    }

    public NoiseRouter method_41099(Registry<DoublePerlinNoiseSampler.NoiseParameters> registry, long l) {
        return class_6954.method_40544(this.generationShapeConfig, l, registry, this.getRandomProvider(), this.noiseRouter);
    }

    public boolean equals(RegistryKey<ChunkGeneratorSettings> registryKey) {
        return Objects.equals(this, DynamicRegistryManager.BUILTIN.get().get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).get(registryKey));
    }

    private static void register(RegistryKey<ChunkGeneratorSettings> registryKey, ChunkGeneratorSettings settings) {
        BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, registryKey.getValue(), settings);
    }

    public static RegistryEntry<ChunkGeneratorSettings> getInstance() {
        return (RegistryEntry)BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.streamEntries().iterator().next();
    }

    private static ChunkGeneratorSettings createEndSettings() {
        return new ChunkGeneratorSettings(GenerationShapeConfig.field_37139, Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), class_6954.method_41120(GenerationShapeConfig.field_37139), VanillaSurfaceRules.getEndStoneRule(), 0, true, false, false, true);
    }

    private static ChunkGeneratorSettings createNetherSettings() {
        return new ChunkGeneratorSettings(GenerationShapeConfig.field_37138, Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState(), class_6954.method_41118(GenerationShapeConfig.field_37138), VanillaSurfaceRules.createNetherSurfaceRule(), 32, false, false, false, true);
    }

    private static ChunkGeneratorSettings createSurfaceSettings(boolean amplified, boolean largeBiomes) {
        GenerationShapeConfig generationShapeConfig = GenerationShapeConfig.method_41126(amplified, largeBiomes);
        return new ChunkGeneratorSettings(generationShapeConfig, Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), class_6954.method_41103(generationShapeConfig), VanillaSurfaceRules.createOverworldSurfaceRule(), 63, false, true, true, false);
    }

    private static ChunkGeneratorSettings createCavesSettings() {
        return new ChunkGeneratorSettings(GenerationShapeConfig.field_37140, Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), class_6954.method_41114(GenerationShapeConfig.field_37140), VanillaSurfaceRules.createDefaultRule(false, true, true), 32, false, false, false, true);
    }

    private static ChunkGeneratorSettings createFloatingIslandsSettings() {
        return new ChunkGeneratorSettings(GenerationShapeConfig.field_37141, Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), class_6954.method_41114(GenerationShapeConfig.field_37141), VanillaSurfaceRules.createDefaultRule(false, false, false), -64, false, false, false, true);
    }

    static {
        ChunkGeneratorSettings.register(OVERWORLD, ChunkGeneratorSettings.createSurfaceSettings(false, false));
        ChunkGeneratorSettings.register(LARGE_BIOMES, ChunkGeneratorSettings.createSurfaceSettings(false, true));
        ChunkGeneratorSettings.register(AMPLIFIED, ChunkGeneratorSettings.createSurfaceSettings(true, false));
        ChunkGeneratorSettings.register(NETHER, ChunkGeneratorSettings.createNetherSettings());
        ChunkGeneratorSettings.register(END, ChunkGeneratorSettings.createEndSettings());
        ChunkGeneratorSettings.register(CAVES, ChunkGeneratorSettings.createCavesSettings());
        ChunkGeneratorSettings.register(FLOATING_ISLANDS, ChunkGeneratorSettings.createFloatingIslandsSettings());
    }
}

