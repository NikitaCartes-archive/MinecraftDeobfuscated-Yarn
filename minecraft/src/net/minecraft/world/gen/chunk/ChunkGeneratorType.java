package net.minecraft.world.gen.chunk;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
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
	private final Supplier<C> settingsSupplier;

	private static <C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> register(
		String string, ChunkGeneratorFactory<C, T> chunkGeneratorFactory, Supplier<C> supplier, boolean bl
	) {
		return Registry.register(Registry.CHUNK_GENERATOR_TYPE, string, new ChunkGeneratorType<>(chunkGeneratorFactory, bl, supplier));
	}

	public ChunkGeneratorType(ChunkGeneratorFactory<C, T> factory, boolean buffetScreenOption, Supplier<C> settingsSupplier) {
		this.factory = factory;
		this.buffetScreenOption = buffetScreenOption;
		this.settingsSupplier = settingsSupplier;
	}

	@Override
	public T create(World world, BiomeSource biomeSource, C chunkGeneratorConfig) {
		return this.factory.create(world, biomeSource, chunkGeneratorConfig);
	}

	public C createSettings() {
		return (C)this.settingsSupplier.get();
	}

	@Environment(EnvType.CLIENT)
	public boolean isBuffetScreenOption() {
		return this.buffetScreenOption;
	}
}
