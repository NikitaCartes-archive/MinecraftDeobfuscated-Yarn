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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5363;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorOptions {
	public static final Codec<GeneratorOptions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorOptions::getSeed),
						Codec.BOOL.fieldOf("generate_features").withDefault(true).stable().forGetter(GeneratorOptions::shouldGenerateStructures),
						Codec.BOOL.fieldOf("bonus_chest").withDefault(false).stable().forGetter(GeneratorOptions::hasBonusChest),
						SimpleRegistry.method_29721(Registry.field_25490, Lifecycle.stable(), class_5363.field_25411)
							.xmap(class_5363::method_29569, Function.identity())
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
		(long)DEMO_SEED, true, true, method_28608(DimensionType.method_28517((long)DEMO_SEED), method_28604((long)DEMO_SEED))
	);
	private final long seed;
	private final boolean generateStructures;
	private final boolean bonusChest;
	private final SimpleRegistry<class_5363> field_24827;
	private final Optional<String> legacyCustomOptions;

	private DataResult<GeneratorOptions> method_28610() {
		return this.method_28611() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this);
	}

	private boolean method_28611() {
		return class_5363.method_29567(this.seed, this.field_24827);
	}

	public GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<class_5363> simpleRegistry) {
		this(seed, generateStructures, bonusChest, simpleRegistry, Optional.empty());
	}

	private GeneratorOptions(
		long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<class_5363> simpleRegistry, Optional<String> legacyCustomOptions
	) {
		this.seed = seed;
		this.generateStructures = generateStructures;
		this.bonusChest = bonusChest;
		this.field_24827 = simpleRegistry;
		this.legacyCustomOptions = legacyCustomOptions;
	}

	public static GeneratorOptions getDefaultOptions() {
		long l = new Random().nextLong();
		return new GeneratorOptions(l, true, false, method_28608(DimensionType.method_28517(l), method_28604(l)));
	}

	public static SurfaceChunkGenerator method_28604(long l) {
		return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
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

	public static SimpleRegistry<class_5363> method_28608(SimpleRegistry<class_5363> simpleRegistry, ChunkGenerator chunkGenerator) {
		SimpleRegistry<class_5363> simpleRegistry2 = new SimpleRegistry<>(Registry.field_25490, Lifecycle.experimental());
		class_5363 lv = simpleRegistry.get(class_5363.field_25412);
		DimensionType dimensionType = lv == null ? DimensionType.method_29563() : lv.method_29570();
		simpleRegistry2.add(class_5363.field_25412, new class_5363(() -> dimensionType, chunkGenerator));

		for (Entry<RegistryKey<class_5363>, class_5363> entry : simpleRegistry.method_29722()) {
			RegistryKey<class_5363> registryKey = (RegistryKey<class_5363>)entry.getKey();
			if (registryKey != class_5363.field_25412) {
				simpleRegistry2.add(registryKey, entry.getValue());
				if (simpleRegistry.method_29723(registryKey)) {
					simpleRegistry2.method_29725(registryKey);
				}
			}
		}

		return simpleRegistry2;
	}

	public SimpleRegistry<class_5363> getDimensionMap() {
		return this.field_24827;
	}

	public ChunkGenerator getChunkGenerator() {
		class_5363 lv = this.field_24827.get(class_5363.field_25412);
		return (ChunkGenerator)(lv == null ? method_28604(new Random().nextLong()) : lv.method_29571());
	}

	public ImmutableSet<RegistryKey<World>> method_29575() {
		return (ImmutableSet<RegistryKey<World>>)this.getDimensionMap()
			.method_29722()
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
		return new GeneratorOptions(this.seed, this.generateStructures, true, this.field_24827, this.legacyCustomOptions);
	}

	@Environment(EnvType.CLIENT)
	public GeneratorOptions toggleGenerateStructures() {
		return new GeneratorOptions(this.seed, !this.generateStructures, this.bonusChest, this.field_24827);
	}

	@Environment(EnvType.CLIENT)
	public GeneratorOptions toggleBonusChest() {
		return new GeneratorOptions(this.seed, this.generateStructures, !this.bonusChest, this.field_24827);
	}

	@Environment(EnvType.CLIENT)
	public GeneratorOptions method_29573(SimpleRegistry<class_5363> simpleRegistry) {
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

		SimpleRegistry<class_5363> simpleRegistry = DimensionType.method_28517(l);
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
			default:
				return new GeneratorOptions(l, bl, false, method_28608(simpleRegistry, method_28604(l)));
		}
	}

	@Environment(EnvType.CLIENT)
	public GeneratorOptions withHardcore(boolean hardcore, OptionalLong seed) {
		long l = seed.orElse(this.seed);
		SimpleRegistry<class_5363> simpleRegistry;
		if (seed.isPresent()) {
			simpleRegistry = new SimpleRegistry<>(Registry.field_25490, Lifecycle.experimental());
			long m = seed.getAsLong();

			for (Entry<RegistryKey<class_5363>, class_5363> entry : this.field_24827.method_29722()) {
				RegistryKey<class_5363> registryKey = (RegistryKey<class_5363>)entry.getKey();
				simpleRegistry.add(registryKey, new class_5363(((class_5363)entry.getValue()).method_29566(), ((class_5363)entry.getValue()).method_29571().withSeed(m)));
				if (this.field_24827.method_29723(registryKey)) {
					simpleRegistry.method_29725(registryKey);
				}
			}
		} else {
			simpleRegistry = this.field_24827;
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
