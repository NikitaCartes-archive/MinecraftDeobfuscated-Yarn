package net.minecraft.world.gen.chunk;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;

public class ChunkGeneratorType<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> implements ChunkGeneratorFactory<C, T> {
	public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, OverworldChunkGenerator> SURFACE = register(
		"surface", OverworldChunkGenerator::new, OverworldChunkGeneratorConfig::new, true
	);
	public static final ChunkGeneratorType<CavesChunkGeneratorConfig, CavesChunkGenerator> CAVES = register(
		"caves", CavesChunkGenerator::new, CavesChunkGeneratorConfig::new, true
	);
	public static final ChunkGeneratorType<FloatingIslandsChunkGeneratorConfig, FloatingIslandsChunkGenerator> FLOATING_ISLANDS = register(
		"floating_islands", FloatingIslandsChunkGenerator::new, FloatingIslandsChunkGeneratorConfig::new, true
	);
	public static final ChunkGeneratorType<DebugChunkGeneratorConfig, DebugChunkGenerator> DEBUG = register(
		"debug", DebugChunkGenerator::new, DebugChunkGeneratorConfig::new, false
	);
	public static final ChunkGeneratorType<FlatChunkGeneratorConfig, FlatChunkGenerator> FLAT = register(
		"flat", FlatChunkGenerator::new, FlatChunkGeneratorConfig::new, false
	);
	private final ChunkGeneratorFactory<C, T> factory;
	private final boolean buffetScreenOption;
	private final Supplier<C> configSupplier;

	private static <C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> register(
		String id, ChunkGeneratorFactory<C, T> factory, Supplier<C> configSupplier, boolean buffetScreenOption
	) {
		return Registry.register(Registry.CHUNK_GENERATOR_TYPE, id, new ChunkGeneratorType<>(factory, buffetScreenOption, configSupplier));
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
		return (C)this.configSupplier.get();
	}

	@Environment(EnvType.CLIENT)
	public boolean isBuffetScreenOption() {
		return this.buffetScreenOption;
	}
}
