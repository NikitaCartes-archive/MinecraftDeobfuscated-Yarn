/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

@Environment(value=EnvType.CLIENT)
public abstract class GeneratorType {
    public static final GeneratorType DEFAULT = new GeneratorType("default"){

        @Override
        protected ChunkGenerator getChunkGenerator(DynamicRegistryManager dynamicRegistryManager, long l) {
            return GeneratorOptions.createOverworldGenerator(dynamicRegistryManager, l);
        }
    };
    private static final GeneratorType FLAT = new GeneratorType("flat"){

        @Override
        protected ChunkGenerator getChunkGenerator(DynamicRegistryManager dynamicRegistryManager, long l) {
            return new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig(dynamicRegistryManager.get(Registry.BIOME_KEY)));
        }
    };
    private static final GeneratorType SINGLE_BIOME_SURFACE = new GeneratorType("single_biome_surface"){

        @Override
        protected ChunkGenerator getChunkGenerator(DynamicRegistryManager dynamicRegistryManager, long l) {
            return GeneratorType.method_39194(dynamicRegistryManager, l, ChunkGeneratorSettings.OVERWORLD);
        }
    };
    private static final GeneratorType SINGLE_BIOME_CAVES = new GeneratorType("single_biome_caves"){

        @Override
        public GeneratorOptions createDefaultOptions(DynamicRegistryManager.Impl registryManager, long seed, boolean generateStructures, boolean bonusChest) {
            Registry<DimensionType> registry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
            return new GeneratorOptions(seed, generateStructures, bonusChest, GeneratorOptions.getRegistryWithReplacedOverworld(DimensionType.createDefaultDimensionOptions(registryManager, seed), () -> registry.getOrThrow(DimensionType.OVERWORLD_CAVES_REGISTRY_KEY), this.getChunkGenerator(registryManager, seed)));
        }

        @Override
        protected ChunkGenerator getChunkGenerator(DynamicRegistryManager dynamicRegistryManager, long l) {
            return GeneratorType.method_39194(dynamicRegistryManager, l, ChunkGeneratorSettings.CAVES);
        }
    };
    private static final GeneratorType SINGLE_BIOME_FLOATING_ISLANDS = new GeneratorType("single_biome_floating_islands"){

        @Override
        protected ChunkGenerator getChunkGenerator(DynamicRegistryManager dynamicRegistryManager, long l) {
            return GeneratorType.method_39194(dynamicRegistryManager, l, ChunkGeneratorSettings.FLOATING_ISLANDS);
        }
    };
    private static final GeneratorType DEBUG_ALL_BLOCK_STATES = new GeneratorType("debug_all_block_states"){

        @Override
        protected ChunkGenerator getChunkGenerator(DynamicRegistryManager dynamicRegistryManager, long l) {
            return new DebugChunkGenerator(dynamicRegistryManager.get(Registry.BIOME_KEY));
        }
    };
    protected static final List<GeneratorType> VALUES = Lists.newArrayList(DEFAULT, FLAT, SINGLE_BIOME_SURFACE, SINGLE_BIOME_CAVES, SINGLE_BIOME_FLOATING_ISLANDS, DEBUG_ALL_BLOCK_STATES);
    protected static final Map<Optional<GeneratorType>, ScreenProvider> SCREEN_PROVIDERS = ImmutableMap.of(Optional.of(FLAT), (screen, generatorOptions) -> {
        ChunkGenerator chunkGenerator = generatorOptions.getChunkGenerator();
        return new CustomizeFlatLevelScreen(screen, config -> createWorldScreen.moreOptionsDialog.setGeneratorOptions(new GeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest(), GeneratorOptions.getRegistryWithReplacedOverworldGenerator(createWorldScreen.moreOptionsDialog.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY), generatorOptions.getDimensions(), new FlatChunkGenerator((FlatChunkGeneratorConfig)config)))), chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkGenerator).getConfig() : FlatChunkGeneratorConfig.getDefaultConfig(screen.moreOptionsDialog.getRegistryManager().get(Registry.BIOME_KEY)));
    }, Optional.of(SINGLE_BIOME_SURFACE), (screen, generatorOptions) -> new CustomizeBuffetLevelScreen(screen, screen.moreOptionsDialog.getRegistryManager(), biome -> createWorldScreen.moreOptionsDialog.setGeneratorOptions(GeneratorType.createFixedBiomeOptions(createWorldScreen.moreOptionsDialog.getRegistryManager(), generatorOptions, SINGLE_BIOME_SURFACE, biome)), GeneratorType.getFirstBiome(screen.moreOptionsDialog.getRegistryManager(), generatorOptions)), Optional.of(SINGLE_BIOME_CAVES), (screen, generatorOptions) -> new CustomizeBuffetLevelScreen(screen, screen.moreOptionsDialog.getRegistryManager(), biome -> createWorldScreen.moreOptionsDialog.setGeneratorOptions(GeneratorType.createFixedBiomeOptions(createWorldScreen.moreOptionsDialog.getRegistryManager(), generatorOptions, SINGLE_BIOME_CAVES, biome)), GeneratorType.getFirstBiome(screen.moreOptionsDialog.getRegistryManager(), generatorOptions)), Optional.of(SINGLE_BIOME_FLOATING_ISLANDS), (screen, generatorOptions) -> new CustomizeBuffetLevelScreen(screen, screen.moreOptionsDialog.getRegistryManager(), biome -> createWorldScreen.moreOptionsDialog.setGeneratorOptions(GeneratorType.createFixedBiomeOptions(createWorldScreen.moreOptionsDialog.getRegistryManager(), generatorOptions, SINGLE_BIOME_FLOATING_ISLANDS, biome)), GeneratorType.getFirstBiome(screen.moreOptionsDialog.getRegistryManager(), generatorOptions)));
    private final Text displayName;

    static NoiseChunkGenerator method_39194(DynamicRegistryManager dynamicRegistryManager, long l, RegistryKey<ChunkGeneratorSettings> registryKey) {
        return new NoiseChunkGenerator(dynamicRegistryManager.get(Registry.NOISE_WORLDGEN), (BiomeSource)new FixedBiomeSource(dynamicRegistryManager.get(Registry.BIOME_KEY).getOrThrow(BiomeKeys.PLAINS)), l, () -> dynamicRegistryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getOrThrow(registryKey));
    }

    GeneratorType(String translationKeySuffix) {
        this.displayName = new TranslatableText("generator." + translationKeySuffix);
    }

    private static GeneratorOptions createFixedBiomeOptions(DynamicRegistryManager registryManager, GeneratorOptions generatorOptions, GeneratorType type, Biome biome) {
        FixedBiomeSource biomeSource = new FixedBiomeSource(biome);
        Registry<DimensionType> registry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
        Registry<ChunkGeneratorSettings> registry2 = registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
        Supplier<ChunkGeneratorSettings> supplier = type == SINGLE_BIOME_CAVES ? () -> registry2.getOrThrow(ChunkGeneratorSettings.CAVES) : (type == SINGLE_BIOME_FLOATING_ISLANDS ? () -> registry2.getOrThrow(ChunkGeneratorSettings.FLOATING_ISLANDS) : () -> registry2.getOrThrow(ChunkGeneratorSettings.OVERWORLD));
        return new GeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest(), GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry, generatorOptions.getDimensions(), new NoiseChunkGenerator(registryManager.get(Registry.NOISE_WORLDGEN), (BiomeSource)biomeSource, generatorOptions.getSeed(), supplier)));
    }

    private static Biome getFirstBiome(DynamicRegistryManager registryManager, GeneratorOptions options) {
        return options.getChunkGenerator().getBiomeSource().getBiomes().stream().findFirst().orElse(registryManager.get(Registry.BIOME_KEY).getOrThrow(BiomeKeys.PLAINS));
    }

    public static Optional<GeneratorType> fromGeneratorOptions(GeneratorOptions generatorOptions) {
        ChunkGenerator chunkGenerator = generatorOptions.getChunkGenerator();
        if (chunkGenerator instanceof FlatChunkGenerator) {
            return Optional.of(FLAT);
        }
        if (chunkGenerator instanceof DebugChunkGenerator) {
            return Optional.of(DEBUG_ALL_BLOCK_STATES);
        }
        return Optional.empty();
    }

    public Text getDisplayName() {
        return this.displayName;
    }

    public GeneratorOptions createDefaultOptions(DynamicRegistryManager.Impl registryManager, long seed, boolean generateStructures, boolean bonusChest) {
        return new GeneratorOptions(seed, generateStructures, bonusChest, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registryManager.get(Registry.DIMENSION_TYPE_KEY), DimensionType.createDefaultDimensionOptions(registryManager, seed), this.getChunkGenerator(registryManager, seed)));
    }

    protected abstract ChunkGenerator getChunkGenerator(DynamicRegistryManager var1, long var2);

    public static boolean isNotDebug(GeneratorType generatorType) {
        return generatorType != DEBUG_ALL_BLOCK_STATES;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface ScreenProvider {
        public Screen createEditScreen(CreateWorldScreen var1, GeneratorOptions var2);
    }
}

