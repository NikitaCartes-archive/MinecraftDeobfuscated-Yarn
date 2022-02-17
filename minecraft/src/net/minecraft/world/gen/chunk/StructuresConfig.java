package net.minecraft.world.gen.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Contains the configuration for placement of each structure type during chunk generation.
 */
public class StructuresConfig {
	public static final Codec<StructuresConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StrongholdConfig.CODEC.optionalFieldOf("stronghold").forGetter(config -> Optional.ofNullable(config.stronghold)),
					Codec.simpleMap(Registry.STRUCTURE_FEATURE.getCodec(), StructureConfig.CODEC, Registry.STRUCTURE_FEATURE)
						.fieldOf("structures")
						.forGetter(config -> config.structures)
				)
				.apply(instance, StructuresConfig::new)
	);
	/**
	 * Default placement settings for each known structure type.
	 * At startup, Minecraft validates that each registered structure has a default
	 * configuration in this map. If mods register structures after this class
	 * has been initialized, the check will already have been made and a
	 * bad default configuration will be used instead (see below).
	 */
	public static final ImmutableMap<StructureFeature<?>, StructureConfig> DEFAULT_STRUCTURES = ImmutableMap.<StructureFeature<?>, StructureConfig>builder()
		.put(StructureFeature.VILLAGE, new StructureConfig(34, 8, 10387312))
		.put(StructureFeature.DESERT_PYRAMID, new StructureConfig(32, 8, 14357617))
		.put(StructureFeature.IGLOO, new StructureConfig(32, 8, 14357618))
		.put(StructureFeature.JUNGLE_PYRAMID, new StructureConfig(32, 8, 14357619))
		.put(StructureFeature.SWAMP_HUT, new StructureConfig(32, 8, 14357620))
		.put(StructureFeature.PILLAGER_OUTPOST, new StructureConfig(32, 8, 165745296))
		.put(StructureFeature.STRONGHOLD, new StructureConfig(1, 0, 0))
		.put(StructureFeature.MONUMENT, new StructureConfig(32, 5, 10387313))
		.put(StructureFeature.END_CITY, new StructureConfig(20, 11, 10387313))
		.put(StructureFeature.MANSION, new StructureConfig(80, 20, 10387319))
		.put(StructureFeature.BURIED_TREASURE, new StructureConfig(1, 0, 0))
		.put(StructureFeature.MINESHAFT, new StructureConfig(1, 0, 0))
		.put(StructureFeature.RUINED_PORTAL, new StructureConfig(40, 15, 34222645))
		.put(StructureFeature.SHIPWRECK, new StructureConfig(24, 4, 165745295))
		.put(StructureFeature.OCEAN_RUIN, new StructureConfig(20, 8, 14357621))
		.put(StructureFeature.BASTION_REMNANT, new StructureConfig(27, 4, 30084232))
		.put(StructureFeature.FORTRESS, new StructureConfig(27, 4, 30084232))
		.put(StructureFeature.NETHER_FOSSIL, new StructureConfig(2, 1, 14357921))
		.put(StructureFeature.ANCIENT_CITY, new StructureConfig(24, 8, 20083232))
		.build();
	/**
	 * Default placement settings for the stronghold.
	 */
	public static final StrongholdConfig DEFAULT_STRONGHOLD;
	private final Map<StructureFeature<?>, StructureConfig> structures;
	private final ImmutableMap<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> configuredStructures;
	/**
	 * Placement settings for the stronghold for this particular combination of settings,
	 * may be null to disable placement of strongholds.
	 */
	@Nullable
	private final StrongholdConfig stronghold;

	private StructuresConfig(Map<StructureFeature<?>, StructureConfig> structures, @Nullable StrongholdConfig stronghold) {
		this.stronghold = stronghold;
		this.structures = structures;
		HashMap<StructureFeature<?>, Builder<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> hashMap = new HashMap();
		ConfiguredStructureFeatures.registerAll(
			(feature, biome) -> ((Builder)hashMap.computeIfAbsent(feature.feature, featurex -> ImmutableMultimap.builder())).put(feature, biome)
		);
		this.configuredStructures = (ImmutableMap<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>>)hashMap.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ((Builder)entry.getValue()).build()));
	}

	public StructuresConfig(Optional<StrongholdConfig> stronghold, Map<StructureFeature<?>, StructureConfig> structures) {
		this(structures, (StrongholdConfig)stronghold.orElse(null));
	}

	/**
	 * Creates a new structure placement configuration with default values.
	 * 
	 * @param withStronghold determines if the default stronghold configuration should be included
	 */
	public StructuresConfig(boolean withStronghold) {
		this(Maps.<StructureFeature<?>, StructureConfig>newHashMap(DEFAULT_STRUCTURES), withStronghold ? DEFAULT_STRONGHOLD : null);
	}

	@VisibleForTesting
	public Map<StructureFeature<?>, StructureConfig> getStructures() {
		return this.structures;
	}

	/**
	 * Gets the placement configuration for a specific structure type, or
	 * a default placement if placement for the structure was not explicitly configured.
	 */
	@Nullable
	public StructureConfig getForType(StructureFeature<?> structureType) {
		return (StructureConfig)this.structures.get(structureType);
	}

	@Nullable
	public StrongholdConfig getStronghold() {
		return this.stronghold;
	}

	public ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> getConfiguredStructureFeature(StructureFeature<?> feature) {
		return this.configuredStructures.getOrDefault(feature, ImmutableMultimap.of());
	}

	static {
		for (StructureFeature<?> structureFeature : Registry.STRUCTURE_FEATURE) {
			if (!DEFAULT_STRUCTURES.containsKey(structureFeature)) {
				throw new IllegalStateException("Structure feature without default settings: " + Registry.STRUCTURE_FEATURE.getId(structureFeature));
			}
		}

		DEFAULT_STRONGHOLD = new StrongholdConfig(32, 3, 128);
	}
}
