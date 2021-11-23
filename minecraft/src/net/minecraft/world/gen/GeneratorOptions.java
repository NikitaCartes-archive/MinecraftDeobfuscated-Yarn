package net.minecraft.world.gen;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorOptions {
	public static final Codec<GeneratorOptions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorOptions::getSeed),
						Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(GeneratorOptions::shouldGenerateStructures),
						Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(GeneratorOptions::hasBonusChest),
						SimpleRegistry.createRegistryCodec(Registry.DIMENSION_KEY, Lifecycle.stable(), DimensionOptions.CODEC)
							.xmap(DimensionOptions::method_29569, Function.identity())
							.fieldOf("dimensions")
							.forGetter(GeneratorOptions::getDimensions),
						Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(generatorOptions -> generatorOptions.legacyCustomOptions)
					)
					.apply(instance, instance.stable(GeneratorOptions::new))
		)
		.comapFlatMap(GeneratorOptions::validate, Function.identity());
	private static final Logger LOGGER = LogManager.getLogger();
	private final long seed;
	private final boolean generateStructures;
	private final boolean bonusChest;
	private final SimpleRegistry<DimensionOptions> options;
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

	public GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<DimensionOptions> options) {
		this(seed, generateStructures, bonusChest, options, Optional.empty());
		DimensionOptions dimensionOptions = options.get(DimensionOptions.OVERWORLD);
		if (dimensionOptions == null) {
			throw new IllegalStateException("Overworld settings missing");
		}
	}

	private GeneratorOptions(
		long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<DimensionOptions> options, Optional<String> legacyCustomOptions
	) {
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
		return new NoiseChunkGenerator(
			registryManager.get(Registry.NOISE_WORLDGEN),
			MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(registryManager.get(Registry.BIOME_KEY), bl),
			seed,
			() -> registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getOrThrow(settings)
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

	public static SimpleRegistry<DimensionOptions> getRegistryWithReplacedOverworldGenerator(
		Registry<DimensionType> dimensionTypeRegistry, SimpleRegistry<DimensionOptions> optionsRegistry, ChunkGenerator overworldGenerator
	) {
		DimensionOptions dimensionOptions = optionsRegistry.get(DimensionOptions.OVERWORLD);
		Supplier<DimensionType> supplier = () -> dimensionOptions == null
				? dimensionTypeRegistry.getOrThrow(DimensionType.OVERWORLD_REGISTRY_KEY)
				: dimensionOptions.getDimensionType();
		return getRegistryWithReplacedOverworld(optionsRegistry, supplier, overworldGenerator);
	}

	public static SimpleRegistry<DimensionOptions> getRegistryWithReplacedOverworld(
		SimpleRegistry<DimensionOptions> optionsRegistry, Supplier<DimensionType> overworldDimensionType, ChunkGenerator overworldGenerator
	) {
		SimpleRegistry<DimensionOptions> simpleRegistry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental());
		simpleRegistry.add(DimensionOptions.OVERWORLD, new DimensionOptions(overworldDimensionType, overworldGenerator), Lifecycle.stable());

		for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : optionsRegistry.getEntries()) {
			RegistryKey<DimensionOptions> registryKey = (RegistryKey<DimensionOptions>)entry.getKey();
			if (registryKey != DimensionOptions.OVERWORLD) {
				simpleRegistry.add(registryKey, (DimensionOptions)entry.getValue(), optionsRegistry.getEntryLifecycle((DimensionOptions)entry.getValue()));
			}
		}

		return simpleRegistry;
	}

	public SimpleRegistry<DimensionOptions> getDimensions() {
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
			.getEntries()
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

	public static GeneratorOptions fromProperties(DynamicRegistryManager registryManager, Properties properties) {
		String string = MoreObjects.firstNonNull((String)properties.get("generator-settings"), "");
		properties.put("generator-settings", string);
		String string2 = MoreObjects.firstNonNull((String)properties.get("level-seed"), "");
		properties.put("level-seed", string2);
		String string3 = (String)properties.get("generate-structures");
		boolean bl = string3 == null || Boolean.parseBoolean(string3);
		properties.put("generate-structures", Objects.toString(bl));
		String string4 = (String)properties.get("level-type");
		String string5 = (String)Optional.ofNullable(string4).map(stringx -> stringx.toLowerCase(Locale.ROOT)).orElse("default");
		properties.put("level-type", string5);
		long l = new Random().nextLong();
		if (!string2.isEmpty()) {
			try {
				long m = Long.parseLong(string2);
				if (m != 0L) {
					l = m;
				}
			} catch (NumberFormatException var17) {
				l = (long)string2.hashCode();
			}
		}

		Registry<DimensionType> registry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
		Registry<Biome> registry2 = registryManager.get(Registry.BIOME_KEY);
		SimpleRegistry<DimensionOptions> simpleRegistry = DimensionType.createDefaultDimensionOptions(registryManager, l);
		switch (string5) {
			case "flat":
				JsonObject jsonObject = !string.isEmpty() ? JsonHelper.deserialize(string) : new JsonObject();
				Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, jsonObject);
				return new GeneratorOptions(
					l,
					bl,
					false,
					getRegistryWithReplacedOverworldGenerator(
						registry,
						simpleRegistry,
						new FlatChunkGenerator(
							(FlatChunkGeneratorConfig)FlatChunkGeneratorConfig.CODEC
								.parse(dynamic)
								.resultOrPartial(LOGGER::error)
								.orElseGet(() -> FlatChunkGeneratorConfig.getDefaultConfig(registry2))
						)
					)
				);
			case "debug_all_block_states":
				return new GeneratorOptions(l, bl, false, getRegistryWithReplacedOverworldGenerator(registry, simpleRegistry, new DebugChunkGenerator(registry2)));
			case "amplified":
				return new GeneratorOptions(
					l, bl, false, getRegistryWithReplacedOverworldGenerator(registry, simpleRegistry, createGenerator(registryManager, l, ChunkGeneratorSettings.AMPLIFIED))
				);
			case "largebiomes":
				return new GeneratorOptions(
					l,
					bl,
					false,
					getRegistryWithReplacedOverworldGenerator(registry, simpleRegistry, createGenerator(registryManager, l, ChunkGeneratorSettings.LARGE_BIOMES))
				);
			default:
				return new GeneratorOptions(l, bl, false, getRegistryWithReplacedOverworldGenerator(registry, simpleRegistry, createOverworldGenerator(registryManager, l)));
		}
	}

	public GeneratorOptions withHardcore(boolean hardcore, OptionalLong seed) {
		long l = seed.orElse(this.seed);
		SimpleRegistry<DimensionOptions> simpleRegistry;
		if (seed.isPresent()) {
			simpleRegistry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental());
			long m = seed.getAsLong();

			for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : this.options.getEntries()) {
				RegistryKey<DimensionOptions> registryKey = (RegistryKey<DimensionOptions>)entry.getKey();
				simpleRegistry.add(
					registryKey,
					new DimensionOptions(((DimensionOptions)entry.getValue()).getDimensionTypeSupplier(), ((DimensionOptions)entry.getValue()).getChunkGenerator().withSeed(m)),
					this.options.getEntryLifecycle((DimensionOptions)entry.getValue())
				);
			}
		} else {
			simpleRegistry = this.options;
		}

		GeneratorOptions generatorOptions;
		if (this.isDebugWorld()) {
			generatorOptions = new GeneratorOptions(l, false, false, simpleRegistry);
		} else {
			generatorOptions = new GeneratorOptions(l, this.shouldGenerateStructures(), this.hasBonusChest() && !hardcore, simpleRegistry);
		}

		return generatorOptions;
	}
}
