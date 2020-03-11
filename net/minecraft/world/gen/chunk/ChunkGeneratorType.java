/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.CavesChunkGenerator;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorFactory;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGenerator;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class ChunkGeneratorType<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>>
implements ChunkGeneratorFactory<C, T> {
    public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, OverworldChunkGenerator> SURFACE = ChunkGeneratorType.register("surface", OverworldChunkGenerator::new, OverworldChunkGeneratorConfig::new, true);
    public static final ChunkGeneratorType<CavesChunkGeneratorConfig, CavesChunkGenerator> CAVES = ChunkGeneratorType.register("caves", CavesChunkGenerator::new, CavesChunkGeneratorConfig::new, true);
    public static final ChunkGeneratorType<FloatingIslandsChunkGeneratorConfig, FloatingIslandsChunkGenerator> FLOATING_ISLANDS = ChunkGeneratorType.register("floating_islands", FloatingIslandsChunkGenerator::new, FloatingIslandsChunkGeneratorConfig::new, true);
    public static final ChunkGeneratorType<DebugChunkGeneratorConfig, DebugChunkGenerator> DEBUG = ChunkGeneratorType.register("debug", DebugChunkGenerator::new, DebugChunkGeneratorConfig::new, false);
    public static final ChunkGeneratorType<FlatChunkGeneratorConfig, FlatChunkGenerator> FLAT = ChunkGeneratorType.register("flat", FlatChunkGenerator::new, FlatChunkGeneratorConfig::new, false);
    private final ChunkGeneratorFactory<C, T> factory;
    private final boolean buffetScreenOption;
    private final Supplier<C> configSupplier;

    private static <C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> register(String id, ChunkGeneratorFactory<C, T> factory, Supplier<C> configSupplier, boolean buffetScreenOption) {
        return Registry.register(Registry.CHUNK_GENERATOR_TYPE, id, new ChunkGeneratorType<C, T>(factory, buffetScreenOption, configSupplier));
    }

    public ChunkGeneratorType(ChunkGeneratorFactory<C, T> factory, boolean buffetScreenOption, Supplier<C> configSupplier) {
        this.factory = factory;
        this.buffetScreenOption = buffetScreenOption;
        this.configSupplier = configSupplier;
    }

    @Override
    public T create(IWorld iWorld, BiomeSource biomeSource, C chunkGeneratorConfig) {
        return this.factory.create(iWorld, biomeSource, chunkGeneratorConfig);
    }

    public C createConfig() {
        return (C)((ChunkGeneratorConfig)this.configSupplier.get());
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isBuffetScreenOption() {
        return this.buffetScreenOption;
    }
}

