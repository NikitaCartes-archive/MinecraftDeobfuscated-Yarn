/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DynamicLike;
import com.mojang.datafixers.OptionalDynamic;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

public class LevelGeneratorOptions {
    private final LevelGeneratorType type;
    private final Dynamic<?> dynamic;
    private final Function<IWorld, ChunkGenerator<?>> chunkGeneratorFactory;

    public LevelGeneratorOptions(LevelGeneratorType type, Dynamic<?> dynamic, Function<IWorld, ChunkGenerator<?>> chunkGeneratorFactory) {
        this.type = type;
        this.dynamic = dynamic;
        this.chunkGeneratorFactory = chunkGeneratorFactory;
    }

    public LevelGeneratorType getType() {
        return this.type;
    }

    public Dynamic<?> getDynamic() {
        return this.dynamic;
    }

    public ChunkGenerator<?> createChunkGenerator(IWorld world) {
        return this.chunkGeneratorFactory.apply(world);
    }

    public static LevelGeneratorOptions createDefault(LevelGeneratorType generatorType, Dynamic<?> dynamic) {
        OverworldChunkGeneratorConfig overworldChunkGeneratorConfig = ChunkGeneratorType.SURFACE.createConfig();
        return new LevelGeneratorOptions(generatorType, dynamic, iWorld -> {
            VanillaLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig = BiomeSourceType.VANILLA_LAYERED.getConfig(iWorld.getSeed()).setGeneratorType(generatorType).setGeneratorConfig(overworldChunkGeneratorConfig);
            return ChunkGeneratorType.SURFACE.create((IWorld)iWorld, BiomeSourceType.VANILLA_LAYERED.applyConfig(vanillaLayeredBiomeSourceConfig), overworldChunkGeneratorConfig);
        });
    }

    public static LevelGeneratorOptions createFlat(LevelGeneratorType generatorType, Dynamic<?> dynamic) {
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = FlatChunkGeneratorConfig.fromDynamic(dynamic);
        return new LevelGeneratorOptions(generatorType, dynamic, iWorld -> {
            FixedBiomeSourceConfig fixedBiomeSourceConfig = BiomeSourceType.FIXED.getConfig(iWorld.getSeed()).setBiome(flatChunkGeneratorConfig.getBiome());
            return ChunkGeneratorType.FLAT.create((IWorld)iWorld, BiomeSourceType.FIXED.applyConfig(fixedBiomeSourceConfig), flatChunkGeneratorConfig);
        });
    }

    private static <T> T retrieveFromRegistry(DynamicLike<?> dynamic, Registry<T> registry, T fallback) {
        return (T)dynamic.asString().map(Identifier::new).flatMap(registry::getOrEmpty).orElse(fallback);
    }

    private static LongFunction<BiomeSource> loadBiomeSourceFactory(DynamicLike<?> dynamic) {
        BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> biomeSourceType = LevelGeneratorOptions.retrieveFromRegistry(dynamic.get("type"), Registry.BIOME_SOURCE_TYPE, BiomeSourceType.FIXED);
        OptionalDynamic<?> dynamicLike = dynamic.get("options");
        Stream stream2 = ((DynamicLike)dynamicLike).get("biomes").asStreamOpt().map(stream -> stream.map(dynamic -> LevelGeneratorOptions.retrieveFromRegistry(dynamic, Registry.BIOME, Biomes.OCEAN))).orElseGet(Stream::empty);
        if (BiomeSourceType.CHECKERBOARD == biomeSourceType) {
            Biome[] biomeArray;
            int i = ((DynamicLike)dynamicLike).get("size").asInt(2);
            Biome[] biomes = (Biome[])stream2.toArray(Biome[]::new);
            if (biomes.length > 0) {
                biomeArray = biomes;
            } else {
                Biome[] biomeArray2 = new Biome[1];
                biomeArray = biomeArray2;
                biomeArray2[0] = Biomes.OCEAN;
            }
            Biome[] biomes2 = biomeArray;
            return l -> {
                CheckerboardBiomeSourceConfig checkerboardBiomeSourceConfig = BiomeSourceType.CHECKERBOARD.getConfig(l).setBiomes(biomes2).setSize(i);
                return BiomeSourceType.CHECKERBOARD.applyConfig(checkerboardBiomeSourceConfig);
            };
        }
        if (BiomeSourceType.VANILLA_LAYERED == biomeSourceType) {
            return l -> {
                VanillaLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig = BiomeSourceType.VANILLA_LAYERED.getConfig(l);
                return BiomeSourceType.VANILLA_LAYERED.applyConfig(vanillaLayeredBiomeSourceConfig);
            };
        }
        Biome biome = stream2.findFirst().orElse(Biomes.OCEAN);
        return l -> {
            FixedBiomeSourceConfig fixedBiomeSourceConfig = BiomeSourceType.FIXED.getConfig(l).setBiome(biome);
            return BiomeSourceType.FIXED.applyConfig(fixedBiomeSourceConfig);
        };
    }

    private static void loadOptions(ChunkGeneratorConfig config, DynamicLike<?> dynamic) {
        BlockState blockState = LevelGeneratorOptions.retrieveFromRegistry(dynamic.get("default_block"), Registry.BLOCK, Blocks.STONE).getDefaultState();
        config.setDefaultBlock(blockState);
        BlockState blockState2 = LevelGeneratorOptions.retrieveFromRegistry(dynamic.get("default_fluid"), Registry.BLOCK, Blocks.WATER).getDefaultState();
        config.setDefaultFluid(blockState2);
    }

    private static Function<IWorld, ChunkGenerator<?>> loadChunkGeneratorFactory(DynamicLike<?> dynamic, LongFunction<BiomeSource> biomeSourceFactory) {
        ChunkGeneratorType<OverworldChunkGeneratorConfig, OverworldChunkGenerator> chunkGeneratorType = LevelGeneratorOptions.retrieveFromRegistry(dynamic.get("type"), Registry.CHUNK_GENERATOR_TYPE, ChunkGeneratorType.SURFACE);
        return LevelGeneratorOptions.loadChunkGeneratorFactory(dynamic, chunkGeneratorType, biomeSourceFactory);
    }

    private static <C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> Function<IWorld, ChunkGenerator<?>> loadChunkGeneratorFactory(DynamicLike<?> dynamic, ChunkGeneratorType<C, T> type, LongFunction<BiomeSource> biomeSourceFactory) {
        Object chunkGeneratorConfig = type.createConfig();
        if (type == ChunkGeneratorType.FLOATING_ISLANDS) {
            FloatingIslandsChunkGeneratorConfig floatingIslandsChunkGeneratorConfig = (FloatingIslandsChunkGeneratorConfig)chunkGeneratorConfig;
            floatingIslandsChunkGeneratorConfig.withCenter(new BlockPos(0, 64, 0));
        }
        LevelGeneratorOptions.loadOptions(chunkGeneratorConfig, dynamic.get("options"));
        return iWorld -> type.create((IWorld)iWorld, (BiomeSource)biomeSourceFactory.apply(iWorld.getSeed()), chunkGeneratorConfig);
    }

    public static LevelGeneratorOptions createBuffet(LevelGeneratorType type, Dynamic<?> dynamic) {
        LongFunction<BiomeSource> longFunction = LevelGeneratorOptions.loadBiomeSourceFactory(dynamic.get("biome_source"));
        Function<IWorld, ChunkGenerator<?>> function = LevelGeneratorOptions.loadChunkGeneratorFactory(dynamic.get("chunk_generator"), longFunction);
        return new LevelGeneratorOptions(type, dynamic, function);
    }

    public static LevelGeneratorOptions createDebug(LevelGeneratorType type, Dynamic<?> dynamic) {
        return new LevelGeneratorOptions(type, dynamic, iWorld -> {
            FixedBiomeSourceConfig fixedBiomeSourceConfig = BiomeSourceType.FIXED.getConfig(iWorld.getSeed()).setBiome(Biomes.PLAINS);
            return ChunkGeneratorType.DEBUG.create((IWorld)iWorld, BiomeSourceType.FIXED.applyConfig(fixedBiomeSourceConfig), ChunkGeneratorType.DEBUG.createConfig());
        });
    }
}

