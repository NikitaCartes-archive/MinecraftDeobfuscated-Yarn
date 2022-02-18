package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.class_6954;
import net.minecraft.class_7056;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;

public record ChunkGeneratorSettings(
	GenerationShapeConfig generationShapeConfig,
	BlockState defaultBlock,
	BlockState defaultFluid,
	class_7056 noiseRouter,
	MaterialRules.MaterialRule surfaceRule,
	int seaLevel,
	@Deprecated boolean mobGenerationDisabled,
	boolean aquifers,
	boolean oreVeins,
	boolean usesLegacyRandom
) {
	public static final Codec<ChunkGeneratorSettings> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					GenerationShapeConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorSettings::generationShapeConfig),
					BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorSettings::defaultBlock),
					BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorSettings::defaultFluid),
					class_7056.field_37137.fieldOf("noise_router").forGetter(ChunkGeneratorSettings::noiseRouter),
					MaterialRules.MaterialRule.CODEC.fieldOf("surface_rule").forGetter(ChunkGeneratorSettings::surfaceRule),
					Codec.INT.fieldOf("sea_level").forGetter(ChunkGeneratorSettings::seaLevel),
					Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorSettings::mobGenerationDisabled),
					Codec.BOOL.fieldOf("aquifers_enabled").forGetter(ChunkGeneratorSettings::hasAquifers),
					Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(ChunkGeneratorSettings::oreVeins),
					Codec.BOOL.fieldOf("legacy_random_source").forGetter(ChunkGeneratorSettings::usesLegacyRandom)
				)
				.apply(instance, ChunkGeneratorSettings::new)
	);
	public static final Codec<RegistryEntry<ChunkGeneratorSettings>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, CODEC);
	public static final RegistryKey<ChunkGeneratorSettings> OVERWORLD = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("overworld"));
	public static final RegistryKey<ChunkGeneratorSettings> LARGE_BIOMES = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("large_biomes"));
	public static final RegistryKey<ChunkGeneratorSettings> AMPLIFIED = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("amplified"));
	public static final RegistryKey<ChunkGeneratorSettings> NETHER = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("nether"));
	public static final RegistryKey<ChunkGeneratorSettings> END = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("end"));
	public static final RegistryKey<ChunkGeneratorSettings> CAVES = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("caves"));
	public static final RegistryKey<ChunkGeneratorSettings> FLOATING_ISLANDS = RegistryKey.of(
		Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("floating_islands")
	);

	public boolean hasAquifers() {
		return this.aquifers;
	}

	public ChunkRandom.RandomProvider getRandomProvider() {
		return this.usesLegacyRandom ? ChunkRandom.RandomProvider.LEGACY : ChunkRandom.RandomProvider.XOROSHIRO;
	}

	public NoiseRouter method_41099(Registry<DoublePerlinNoiseSampler.NoiseParameters> registry, long l) {
		return class_6954.method_40544(this.generationShapeConfig, l, registry, this.getRandomProvider(), this.noiseRouter);
	}

	public boolean equals(RegistryKey<ChunkGeneratorSettings> registryKey) {
		return Objects.equals(
			this, ((DynamicRegistryManager.Immutable)DynamicRegistryManager.BUILTIN.get()).get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).get(registryKey)
		);
	}

	private static void register(RegistryKey<ChunkGeneratorSettings> registryKey, ChunkGeneratorSettings settings) {
		BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, registryKey.getValue(), settings);
	}

	public static RegistryEntry<ChunkGeneratorSettings> getInstance() {
		return (RegistryEntry<ChunkGeneratorSettings>)BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.streamEntries().iterator().next();
	}

	private static ChunkGeneratorSettings createEndSettings() {
		return new ChunkGeneratorSettings(
			GenerationShapeConfig.field_37139,
			Blocks.END_STONE.getDefaultState(),
			Blocks.AIR.getDefaultState(),
			class_6954.method_41120(GenerationShapeConfig.field_37139),
			VanillaSurfaceRules.getEndStoneRule(),
			0,
			true,
			false,
			false,
			true
		);
	}

	private static ChunkGeneratorSettings createNetherSettings() {
		return new ChunkGeneratorSettings(
			GenerationShapeConfig.field_37138,
			Blocks.NETHERRACK.getDefaultState(),
			Blocks.LAVA.getDefaultState(),
			class_6954.method_41118(GenerationShapeConfig.field_37138),
			VanillaSurfaceRules.createNetherSurfaceRule(),
			32,
			false,
			false,
			false,
			true
		);
	}

	private static ChunkGeneratorSettings createSurfaceSettings(boolean amplified, boolean largeBiomes) {
		GenerationShapeConfig generationShapeConfig = GenerationShapeConfig.method_41126(amplified, largeBiomes);
		return new ChunkGeneratorSettings(
			generationShapeConfig,
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			class_6954.method_41103(generationShapeConfig),
			VanillaSurfaceRules.createOverworldSurfaceRule(),
			63,
			false,
			true,
			true,
			false
		);
	}

	private static ChunkGeneratorSettings createCavesSettings() {
		return new ChunkGeneratorSettings(
			GenerationShapeConfig.field_37140,
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			class_6954.method_41114(GenerationShapeConfig.field_37140),
			VanillaSurfaceRules.createDefaultRule(false, true, true),
			32,
			false,
			false,
			false,
			true
		);
	}

	private static ChunkGeneratorSettings createFloatingIslandsSettings() {
		return new ChunkGeneratorSettings(
			GenerationShapeConfig.field_37141,
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			class_6954.method_41114(GenerationShapeConfig.field_37141),
			VanillaSurfaceRules.createDefaultRule(false, false, false),
			-64,
			false,
			false,
			false,
			true
		);
	}

	static {
		register(OVERWORLD, createSurfaceSettings(false, false));
		register(LARGE_BIOMES, createSurfaceSettings(false, true));
		register(AMPLIFIED, createSurfaceSettings(true, false));
		register(NETHER, createNetherSettings());
		register(END, createEndSettings());
		register(CAVES, createCavesSettings());
		register(FLOATING_ISLANDS, createFloatingIslandsSettings());
	}
}
