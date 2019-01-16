package net.minecraft.world.gen.chunk;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;

public class ChunkGeneratorType<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> implements ChunkGeneratorFactory<C, T> {
	public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, OverworldChunkGenerator> field_12769 = register(
		"surface", OverworldChunkGenerator::new, OverworldChunkGeneratorConfig::new, true
	);
	public static final ChunkGeneratorType<CavesChunkGeneratorConfig, CavesChunkGenerator> field_12765 = register(
		"caves", CavesChunkGenerator::new, CavesChunkGeneratorConfig::new, true
	);
	public static final ChunkGeneratorType<FloatingIslandsChunkGeneratorConfig, FloatingIslandsChunkGenerator> field_12770 = register(
		"floating_islands", FloatingIslandsChunkGenerator::new, FloatingIslandsChunkGeneratorConfig::new, true
	);
	public static final ChunkGeneratorType<DebugChunkGeneratorConfig, DebugChunkGenerator> field_12768 = register(
		"debug", DebugChunkGenerator::new, DebugChunkGeneratorConfig::new, false
	);
	public static final ChunkGeneratorType<FlatChunkGeneratorConfig, FlatChunkGenerator> field_12766 = register(
		"flat", FlatChunkGenerator::new, FlatChunkGeneratorConfig::new, false
	);
	private final ChunkGeneratorFactory<C, T> factory;
	private final boolean field_12767;
	private final Supplier<C> settingsSupplier;

	private static <C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> register(
		String string, ChunkGeneratorFactory<C, T> chunkGeneratorFactory, Supplier<C> supplier, boolean bl
	) {
		return Registry.register(Registry.CHUNK_GENERATOR_TYPE, string, new ChunkGeneratorType<>(chunkGeneratorFactory, bl, supplier));
	}

	public ChunkGeneratorType(ChunkGeneratorFactory<C, T> chunkGeneratorFactory, boolean bl, Supplier<C> supplier) {
		this.factory = chunkGeneratorFactory;
		this.field_12767 = bl;
		this.settingsSupplier = supplier;
	}

	@Override
	public T create(World world, BiomeSource biomeSource, C chunkGeneratorConfig) {
		return this.factory.create(world, biomeSource, chunkGeneratorConfig);
	}

	public C createSettings() {
		return (C)this.settingsSupplier.get();
	}

	@Environment(EnvType.CLIENT)
	public boolean method_12118() {
		return this.field_12767;
	}
}
