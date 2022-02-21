package net.minecraft.world.gen;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class GeneratorOptions {
	public static final Codec<GeneratorOptions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorOptions::getSeed),
						Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(GeneratorOptions::shouldGenerateStructures),
						Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(GeneratorOptions::hasBonusChest),
						RegistryCodecs.dynamicRegistry(Registry.DIMENSION_KEY, Lifecycle.stable(), DimensionOptions.CODEC)
							.xmap(DimensionOptions::method_29569, Function.identity())
							.fieldOf("dimensions")
							.forGetter(GeneratorOptions::getDimensions),
						Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(generatorOptions -> generatorOptions.legacyCustomOptions)
					)
					.apply(instance, instance.stable(GeneratorOptions::new))
		)
		.comapFlatMap(GeneratorOptions::validate, Function.identity());
	private static final Logger LOGGER = LogUtils.getLogger();
	private final long seed;
	private final boolean generateStructures;
	private final boolean bonusChest;
	private final Registry<DimensionOptions> options;
	private final Optional<String> legacyCustomOptions;

	private DataResult<GeneratorOptions> validate() {
		DimensionOptions dimensionOptions = this.options.get(DimensionOptions.OVERWORLD);
		if (dimensionOptions == null) {
			return DataResult.error("Overworld settings missing");
		} else {
			return this.isStable() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this);
		}
	}

	private boolean isStable() {
		return DimensionOptions.hasDefaultSettings(this.seed, this.options);
	}

	public GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, Registry<DimensionOptions> options) {
		this(seed, generateStructures, bonusChest, options, Optional.empty());
		DimensionOptions dimensionOptions = options.get(DimensionOptions.OVERWORLD);
		if (dimensionOptions == null) {
			throw new IllegalStateException("Overworld settings missing");
		}
	}

	private GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, Registry<DimensionOptions> options, Optional<String> legacyCustomOptions) {
		this.seed = seed;
		this.generateStructures = generateStructures;
		this.bonusChest = bonusChest;
		this.options = options;
		this.legacyCustomOptions = legacyCustomOptions;
	}

	public static GeneratorOptions createDemo(DynamicRegistryManager registryManager) {
		int i = "North Carolina".hashCode();
		return new GeneratorOptions(
			(long)i,
			true,
			true,
			getRegistryWithReplacedOverworldGenerator(
				registryManager.get(Registry.DIMENSION_TYPE_KEY),
				DimensionType.createDefaultDimensionOptions(registryManager, (long)i),
				createOverworldGenerator(registryManager, (long)i)
			)
		);
	}

	public static GeneratorOptions getDefaultOptions(DynamicRegistryManager registryManager) {
		long l = new Random().nextLong();
		return new GeneratorOptions(
			l,
			true,
			false,
			getRegistryWithReplacedOverworldGenerator(
				registryManager.get(Registry.DIMENSION_TYPE_KEY),
				DimensionType.createDefaultDimensionOptions(registryManager, l),
				createOverworldGenerator(registryManager, l)
			)
		);
	}

	public static NoiseChunkGenerator createOverworldGenerator(DynamicRegistryManager registryManager, long seed) {
		return createOverworldGenerator(registryManager, seed, true);
	}

	public static NoiseChunkGenerator createOverworldGenerator(DynamicRegistryManager registryManager, long seed, boolean bl) {
		return createGenerator(registryManager, seed, ChunkGeneratorSettings.OVERWORLD, bl);
	}

	public static NoiseChunkGenerator createGenerator(DynamicRegistryManager registryManager, long seed, RegistryKey<ChunkGeneratorSettings> settings) {
		return createGenerator(registryManager, seed, settings, true);
	}

	public static NoiseChunkGenerator createGenerator(DynamicRegistryManager registryManager, long seed, RegistryKey<ChunkGeneratorSettings> settings, boolean bl) {
		Registry<Biome> registry = registryManager.get(Registry.BIOME_KEY);
		Registry<StructureSet> registry2 = registryManager.get(Registry.STRUCTURE_SET_KEY);
		Registry<ChunkGeneratorSettings> registry3 = registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry4 = registryManager.get(Registry.NOISE_WORLDGEN);
		return new NoiseChunkGenerator(
			registry2, registry4, MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(registry, bl), seed, registry3.getOrCreateEntry(settings)
		);
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

	public static Registry<DimensionOptions> getRegistryWithReplacedOverworldGenerator(
		Registry<DimensionType> dimensionTypeRegistry, Registry<DimensionOptions> options, ChunkGenerator overworldGenerator
	) {
		DimensionOptions dimensionOptions = options.get(DimensionOptions.OVERWORLD);
		RegistryEntry<DimensionType> registryEntry = dimensionOptions == null
			? dimensionTypeRegistry.getOrCreateEntry(DimensionType.OVERWORLD_REGISTRY_KEY)
			: dimensionOptions.getDimensionTypeSupplier();
		return getRegistryWithReplacedOverworld(options, registryEntry, overworldGenerator);
	}

	public static Registry<DimensionOptions> getRegistryWithReplacedOverworld(
		Registry<DimensionOptions> options, RegistryEntry<DimensionType> dimensionType, ChunkGenerator overworldGenerator
	) {
		MutableRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
		mutableRegistry.add(DimensionOptions.OVERWORLD, new DimensionOptions(dimensionType, overworldGenerator), Lifecycle.stable());

		for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : options.getEntrySet()) {
			RegistryKey<DimensionOptions> registryKey = (RegistryKey<DimensionOptions>)entry.getKey();
			if (registryKey != DimensionOptions.OVERWORLD) {
				mutableRegistry.add(registryKey, (DimensionOptions)entry.getValue(), options.getEntryLifecycle((DimensionOptions)entry.getValue()));
			}
		}

		return mutableRegistry;
	}

	public Registry<DimensionOptions> getDimensions() {
		return this.options;
	}

	public ChunkGenerator getChunkGenerator() {
		DimensionOptions dimensionOptions = this.options.get(DimensionOptions.OVERWORLD);
		if (dimensionOptions == null) {
			throw new IllegalStateException("Overworld settings missing");
		} else {
			return dimensionOptions.getChunkGenerator();
		}
	}

	public ImmutableSet<RegistryKey<World>> getWorlds() {
		return (ImmutableSet<RegistryKey<World>>)this.getDimensions()
			.getEntrySet()
			.stream()
			.map(Entry::getKey)
			.map(GeneratorOptions::toWorldKey)
			.collect(ImmutableSet.toImmutableSet());
	}

	public static RegistryKey<World> toWorldKey(RegistryKey<DimensionOptions> dimensionOptionsKey) {
		return RegistryKey.of(Registry.WORLD_KEY, dimensionOptionsKey.getValue());
	}

	public static RegistryKey<DimensionOptions> toDimensionOptionsKey(RegistryKey<World> worldKey) {
		return RegistryKey.of(Registry.DIMENSION_KEY, worldKey.getValue());
	}

	public boolean isDebugWorld() {
		return this.getChunkGenerator() instanceof DebugChunkGenerator;
	}

	public boolean isFlatWorld() {
		return this.getChunkGenerator() instanceof FlatChunkGenerator;
	}

	public boolean isLegacyCustomizedType() {
		return this.legacyCustomOptions.isPresent();
	}

	public GeneratorOptions withBonusChest() {
		return new GeneratorOptions(this.seed, this.generateStructures, true, this.options, this.legacyCustomOptions);
	}

	public GeneratorOptions toggleGenerateStructures() {
		return new GeneratorOptions(this.seed, !this.generateStructures, this.bonusChest, this.options);
	}

	public GeneratorOptions toggleBonusChest() {
		return new GeneratorOptions(this.seed, this.generateStructures, !this.bonusChest, this.options);
	}

	public static GeneratorOptions fromProperties(DynamicRegistryManager registryManager, ServerPropertiesHandler.WorldGenProperties worldGenProperties) {
		long l = parseSeed(worldGenProperties.levelSeed()).orElse(new Random().nextLong());
		Registry<DimensionType> registry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
		Registry<Biome> registry2 = registryManager.get(Registry.BIOME_KEY);
		Registry<StructureSet> registry3 = registryManager.get(Registry.STRUCTURE_SET_KEY);
		Registry<DimensionOptions> registry4 = DimensionType.createDefaultDimensionOptions(registryManager, l);
		String var8 = worldGenProperties.levelType();
		switch (var8) {
			case "flat":
				Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, worldGenProperties.generatorSettings());
				return new GeneratorOptions(
					l,
					worldGenProperties.generateStructures(),
					false,
					getRegistryWithReplacedOverworldGenerator(
						registry,
						registry4,
						new FlatChunkGenerator(
							registry3,
							(FlatChunkGeneratorConfig)FlatChunkGeneratorConfig.CODEC
								.parse(dynamic)
								.resultOrPartial(LOGGER::error)
								.orElseGet(() -> FlatChunkGeneratorConfig.getDefaultConfig(registry2, registry3))
						)
					)
				);
			case "debug_all_block_states":
				return new GeneratorOptions(
					l,
					worldGenProperties.generateStructures(),
					false,
					getRegistryWithReplacedOverworldGenerator(registry, registry4, new DebugChunkGenerator(registry3, registry2))
				);
			case "amplified":
				return new GeneratorOptions(
					l,
					worldGenProperties.generateStructures(),
					false,
					getRegistryWithReplacedOverworldGenerator(registry, registry4, createGenerator(registryManager, l, ChunkGeneratorSettings.AMPLIFIED))
				);
			case "largebiomes":
				return new GeneratorOptions(
					l,
					worldGenProperties.generateStructures(),
					false,
					getRegistryWithReplacedOverworldGenerator(registry, registry4, createGenerator(registryManager, l, ChunkGeneratorSettings.LARGE_BIOMES))
				);
			default:
				return new GeneratorOptions(
					l,
					worldGenProperties.generateStructures(),
					false,
					getRegistryWithReplacedOverworldGenerator(registry, registry4, createOverworldGenerator(registryManager, l))
				);
		}
	}

	public GeneratorOptions withHardcore(boolean hardcore, OptionalLong seed) {
		long l = seed.orElse(this.seed);
		Registry<DimensionOptions> registry;
		if (seed.isPresent()) {
			MutableRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
			long m = seed.getAsLong();

			for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : this.options.getEntrySet()) {
				RegistryKey<DimensionOptions> registryKey = (RegistryKey<DimensionOptions>)entry.getKey();
				mutableRegistry.add(
					registryKey,
					new DimensionOptions(((DimensionOptions)entry.getValue()).getDimensionTypeSupplier(), ((DimensionOptions)entry.getValue()).getChunkGenerator().withSeed(m)),
					this.options.getEntryLifecycle((DimensionOptions)entry.getValue())
				);
			}

			registry = mutableRegistry;
		} else {
			registry = this.options;
		}

		GeneratorOptions generatorOptions;
		if (this.isDebugWorld()) {
			generatorOptions = new GeneratorOptions(l, false, false, registry);
		} else {
			generatorOptions = new GeneratorOptions(l, this.shouldGenerateStructures(), this.hasBonusChest() && !hardcore, registry);
		}

		return generatorOptions;
	}

	public static OptionalLong parseSeed(String seed) {
		seed = seed.trim();
		if (StringUtils.isEmpty(seed)) {
			return OptionalLong.empty();
		} else {
			try {
				return OptionalLong.of(Long.parseLong(seed));
			} catch (NumberFormatException var2) {
				return OptionalLong.of((long)seed.hashCode());
			}
		}
	}
}
