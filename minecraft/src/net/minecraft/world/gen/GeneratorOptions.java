package net.minecraft.world.gen;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashMap;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
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
						Codec.unboundedMap(
								Identifier.field_25139.xmap(RegistryKey.createKeyFactory(Registry.DIMENSION), RegistryKey::getValue),
								Codec.mapPair(DimensionType.field_24756.fieldOf("type"), ChunkGenerator.field_24746.fieldOf("generator")).codec()
							)
							.xmap(DimensionType::method_28524, Function.identity())
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
	public static final GeneratorOptions FLAT_CONFIG = new GeneratorOptions(
		0L, false, false, method_28608(DimensionType.method_28517(0L), new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig()))
	);
	private final long seed;
	private final boolean generateStructures;
	private final boolean bonusChest;
	private final LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> field_24827;
	private final Optional<String> legacyCustomOptions;

	private DataResult<GeneratorOptions> method_28610() {
		return this.method_28611() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this);
	}

	private boolean method_28611() {
		return DimensionType.method_28518(this.seed, this.field_24827);
	}

	public GeneratorOptions(
		long seed, boolean generateStructures, boolean bonusChest, LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> linkedHashMap
	) {
		this(seed, generateStructures, bonusChest, linkedHashMap, Optional.empty());
	}

	private GeneratorOptions(
		long seed,
		boolean generateStructures,
		boolean bonusChest,
		LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> linkedHashMap,
		Optional<String> legacyCustomOptions
	) {
		this.seed = seed;
		this.generateStructures = generateStructures;
		this.bonusChest = bonusChest;
		this.field_24827 = linkedHashMap;
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

	public static LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> method_28608(
		LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> linkedHashMap, ChunkGenerator chunkGenerator
	) {
		LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> linkedHashMap2 = Maps.newLinkedHashMap();
		Pair<DimensionType, ChunkGenerator> pair = (Pair<DimensionType, ChunkGenerator>)linkedHashMap.get(DimensionType.OVERWORLD_REGISTRY_KEY);
		DimensionType dimensionType = pair == null ? DimensionType.method_29294() : pair.getFirst();
		linkedHashMap2.put(World.OVERWORLD, Pair.of(dimensionType, chunkGenerator));

		for (Entry<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> entry : linkedHashMap.entrySet()) {
			if (entry.getKey() != World.OVERWORLD) {
				linkedHashMap2.put(entry.getKey(), entry.getValue());
			}
		}

		return linkedHashMap2;
	}

	public LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> getDimensionMap() {
		return this.field_24827;
	}

	public ChunkGenerator getChunkGenerator() {
		Pair<DimensionType, ChunkGenerator> pair = (Pair<DimensionType, ChunkGenerator>)this.field_24827.get(DimensionType.OVERWORLD_REGISTRY_KEY);
		return (ChunkGenerator)(pair == null ? method_28604(new Random().nextLong()) : pair.getSecond());
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

		LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> linkedHashMap = DimensionType.method_28517(l);
		switch (string5) {
			case "flat":
				JsonObject jsonObject = !string.isEmpty() ? JsonHelper.deserialize(string) : new JsonObject();
				Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, jsonObject);
				return new GeneratorOptions(
					l,
					bl,
					false,
					method_28608(
						linkedHashMap,
						new FlatChunkGenerator(
							(FlatChunkGeneratorConfig)FlatChunkGeneratorConfig.CODEC
								.parse(dynamic)
								.resultOrPartial(LOGGER::error)
								.orElseGet(FlatChunkGeneratorConfig::getDefaultConfig)
						)
					)
				);
			case "debug_all_block_states":
				return new GeneratorOptions(l, bl, false, method_28608(linkedHashMap, DebugChunkGenerator.INSTANCE));
			default:
				return new GeneratorOptions(l, bl, false, method_28608(linkedHashMap, method_28604(l)));
		}
	}

	@Environment(EnvType.CLIENT)
	public GeneratorOptions withHardcore(boolean hardcore, OptionalLong seed) {
		long l = seed.orElse(this.seed);
		LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> linkedHashMap;
		if (seed.isPresent()) {
			linkedHashMap = Maps.newLinkedHashMap();
			long m = seed.getAsLong();

			for (Entry<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> entry : this.field_24827.entrySet()) {
				linkedHashMap.put(entry.getKey(), Pair.of(((Pair)entry.getValue()).getFirst(), ((ChunkGenerator)((Pair)entry.getValue()).getSecond()).withSeed(m)));
			}
		} else {
			linkedHashMap = this.field_24827;
		}

		GeneratorOptions generatorOptions;
		if (this.isDebugWorld()) {
			generatorOptions = new GeneratorOptions(l, false, false, linkedHashMap);
		} else {
			generatorOptions = new GeneratorOptions(l, this.shouldGenerateStructures(), this.hasBonusChest() && !hardcore, linkedHashMap);
		}

		return generatorOptions;
	}
}
