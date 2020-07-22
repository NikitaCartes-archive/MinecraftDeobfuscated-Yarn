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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
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
						SimpleRegistry.createCodec(Registry.DIMENSION_OPTIONS, Lifecycle.stable(), DimensionOptions.CODEC)
							.xmap(DimensionOptions::method_29569, Function.identity())
							.fieldOf("dimensions")
							.forGetter(GeneratorOptions::getDimensionMap),
						Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(generatorOptions -> generatorOptions.legacyCustomOptions)
					)
					.apply(instance, instance.stable(GeneratorOptions::new))
		)
		.comapFlatMap(GeneratorOptions::method_28610, Function.identity());
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int DEMO_SEED = "North Carolina".hashCode();
	public static final GeneratorOptions DEMO_CONFIG = new GeneratorOptions(
		(long)DEMO_SEED, true, true, method_28608(DimensionType.method_28517((long)DEMO_SEED), createOverworldGenerator((long)DEMO_SEED))
	);
	private final long seed;
	private final boolean generateStructures;
	private final boolean bonusChest;
	private final SimpleRegistry<DimensionOptions> options;
	private final Optional<String> legacyCustomOptions;

	private DataResult<GeneratorOptions> method_28610() {
		return this.method_28611() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this);
	}

	private boolean method_28611() {
		return DimensionOptions.method_29567(this.seed, this.options);
	}

	public GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<DimensionOptions> simpleRegistry) {
		this(seed, generateStructures, bonusChest, simpleRegistry, Optional.empty());
	}

	private GeneratorOptions(
		long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<DimensionOptions> simpleRegistry, Optional<String> legacyCustomOptions
	) {
		this.seed = seed;
		this.generateStructures = generateStructures;
		this.bonusChest = bonusChest;
		this.options = simpleRegistry;
		this.legacyCustomOptions = legacyCustomOptions;
	}

	public static GeneratorOptions getDefaultOptions() {
		long l = new Random().nextLong();
		return new GeneratorOptions(l, true, false, method_28608(DimensionType.method_28517(l), createOverworldGenerator(l)));
	}

	public static NoiseChunkGenerator createOverworldGenerator(long seed) {
		return new NoiseChunkGenerator(new VanillaLayeredBiomeSource(seed, false, false), seed, () -> ChunkGeneratorType.field_26355);
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

	public static SimpleRegistry<DimensionOptions> method_28608(SimpleRegistry<DimensionOptions> simpleRegistry, ChunkGenerator chunkGenerator) {
		DimensionOptions dimensionOptions = simpleRegistry.get(DimensionOptions.OVERWORLD);
		Supplier<DimensionType> supplier = () -> dimensionOptions == null ? DimensionType.getOverworldDimensionType() : dimensionOptions.getDimensionType();
		return method_29962(simpleRegistry, supplier, chunkGenerator);
	}

	public static SimpleRegistry<DimensionOptions> method_29962(
		SimpleRegistry<DimensionOptions> simpleRegistry, Supplier<DimensionType> supplier, ChunkGenerator chunkGenerator
	) {
		SimpleRegistry<DimensionOptions> simpleRegistry2 = new SimpleRegistry<>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
		simpleRegistry2.add(DimensionOptions.OVERWORLD, new DimensionOptions(supplier, chunkGenerator));
		simpleRegistry2.markLoaded(DimensionOptions.OVERWORLD);

		for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : simpleRegistry.getEntries()) {
			RegistryKey<DimensionOptions> registryKey = (RegistryKey<DimensionOptions>)entry.getKey();
			if (registryKey != DimensionOptions.OVERWORLD) {
				simpleRegistry2.add(registryKey, entry.getValue());
				if (simpleRegistry.isLoaded(registryKey)) {
					simpleRegistry2.markLoaded(registryKey);
				}
			}
		}

		return simpleRegistry2;
	}

	public SimpleRegistry<DimensionOptions> getDimensionMap() {
		return this.options;
	}

	public ChunkGenerator getChunkGenerator() {
		DimensionOptions dimensionOptions = this.options.get(DimensionOptions.OVERWORLD);
		return (ChunkGenerator)(dimensionOptions == null ? createOverworldGenerator(new Random().nextLong()) : dimensionOptions.getChunkGenerator());
	}

	public ImmutableSet<RegistryKey<World>> getWorlds() {
		return (ImmutableSet<RegistryKey<World>>)this.getDimensionMap()
			.getEntries()
			.stream()
			.map(entry -> RegistryKey.of(Registry.DIMENSION, ((RegistryKey)entry.getKey()).getValue()))
			.collect(ImmutableSet.toImmutableSet());
	}

	public boolean isDebugWorld() {
		return this.getChunkGenerator() instanceof DebugChunkGenerator;
	}

	public boolean isFlatWorld() {
		return this.getChunkGenerator() instanceof FlatChunkGenerator;
	}

	@Environment(EnvType.CLIENT)
	public boolean isLegacyCustomizedType() {
		return this.legacyCustomOptions.isPresent();
	}

	public GeneratorOptions withBonusChest() {
		return new GeneratorOptions(this.seed, this.generateStructures, true, this.options, this.legacyCustomOptions);
	}

	@Environment(EnvType.CLIENT)
	public GeneratorOptions toggleGenerateStructures() {
		return new GeneratorOptions(this.seed, !this.generateStructures, this.bonusChest, this.options);
	}

	@Environment(EnvType.CLIENT)
	public GeneratorOptions toggleBonusChest() {
		return new GeneratorOptions(this.seed, this.generateStructures, !this.bonusChest, this.options);
	}

	@Environment(EnvType.CLIENT)
	public GeneratorOptions method_29573(SimpleRegistry<DimensionOptions> simpleRegistry) {
		return new GeneratorOptions(this.seed, this.generateStructures, this.bonusChest, simpleRegistry);
	}

	public static GeneratorOptions fromProperties(Properties properties) {
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
			} catch (NumberFormatException var14) {
				l = (long)string2.hashCode();
			}
		}

		SimpleRegistry<DimensionOptions> simpleRegistry = DimensionType.method_28517(l);
		switch (string5) {
			case "flat":
				JsonObject jsonObject = !string.isEmpty() ? JsonHelper.deserialize(string) : new JsonObject();
				Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, jsonObject);
				return new GeneratorOptions(
					l,
					bl,
					false,
					method_28608(
						simpleRegistry,
						new FlatChunkGenerator(
							(FlatChunkGeneratorConfig)FlatChunkGeneratorConfig.CODEC
								.parse(dynamic)
								.resultOrPartial(LOGGER::error)
								.orElseGet(FlatChunkGeneratorConfig::getDefaultConfig)
						)
					)
				);
			case "debug_all_block_states":
				return new GeneratorOptions(l, bl, false, method_28608(simpleRegistry, DebugChunkGenerator.INSTANCE));
			case "amplified":
				return new GeneratorOptions(
					l,
					bl,
					false,
					method_28608(simpleRegistry, new NoiseChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, () -> ChunkGeneratorType.field_26356))
				);
			case "largebiomes":
				return new GeneratorOptions(
					l,
					bl,
					false,
					method_28608(simpleRegistry, new NoiseChunkGenerator(new VanillaLayeredBiomeSource(l, false, true), l, () -> ChunkGeneratorType.field_26355))
				);
			default:
				return new GeneratorOptions(l, bl, false, method_28608(simpleRegistry, createOverworldGenerator(l)));
		}
	}

	@Environment(EnvType.CLIENT)
	public GeneratorOptions withHardcore(boolean hardcore, OptionalLong seed) {
		long l = seed.orElse(this.seed);
		SimpleRegistry<DimensionOptions> simpleRegistry;
		if (seed.isPresent()) {
			simpleRegistry = new SimpleRegistry<>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
			long m = seed.getAsLong();

			for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : this.options.getEntries()) {
				RegistryKey<DimensionOptions> registryKey = (RegistryKey<DimensionOptions>)entry.getKey();
				simpleRegistry.add(
					registryKey,
					new DimensionOptions(((DimensionOptions)entry.getValue()).getDimensionTypeSupplier(), ((DimensionOptions)entry.getValue()).getChunkGenerator().withSeed(m))
				);
				if (this.options.isLoaded(registryKey)) {
					simpleRegistry.markLoaded(registryKey);
				}
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
