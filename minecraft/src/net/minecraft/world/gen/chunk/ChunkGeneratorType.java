package net.minecraft.world.gen.chunk;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;

public class ChunkGeneratorType<C extends ChunkGeneratorSettings, T extends ChunkGenerator<C>> implements ChunkGeneratorFactory<C, T> {
	public static final ChunkGeneratorType<OverworldChunkGeneratorSettings, OverworldChunkGenerator> field_12769 = register(
		"surface", OverworldChunkGenerator::new, OverworldChunkGeneratorSettings::new, true
	);
	public static final ChunkGeneratorType<CavesChunkGeneratorSettings, CavesChunkGenerator> field_12765 = register(
		"caves", CavesChunkGenerator::new, CavesChunkGeneratorSettings::new, true
	);
	public static final ChunkGeneratorType<FloatingIslandsChunkGeneratorSettings, FloatingIslandsChunkGenerator> field_12770 = register(
		"floating_islands", FloatingIslandsChunkGenerator::new, FloatingIslandsChunkGeneratorSettings::new, true
	);
	public static final ChunkGeneratorType<DebugChunkGeneratorSettings, DebugChunkGenerator> field_12768 = register(
		"debug", DebugChunkGenerator::new, DebugChunkGeneratorSettings::new, false
	);
	public static final ChunkGeneratorType<FlatChunkGeneratorSettings, FlatChunkGenerator> field_12766 = register(
		"flat", FlatChunkGenerator::new, FlatChunkGeneratorSettings::new, false
	);
	private final ChunkGeneratorFactory<C, T> factory;
	private final boolean field_12767;
	private final Supplier<C> settingsSupplier;

	private static <C extends ChunkGeneratorSettings, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> register(
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
	public T create(World world, BiomeSource biomeSource, C chunkGeneratorSettings) {
		return this.factory.create(world, biomeSource, chunkGeneratorSettings);
	}

	public C createSettings() {
		return (C)this.settingsSupplier.get();
	}

	@Environment(EnvType.CLIENT)
	public boolean method_12118() {
		return this.field_12767;
	}
}
