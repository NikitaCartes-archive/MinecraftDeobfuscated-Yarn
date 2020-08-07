package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Contains the configuration for placement of each structure type during chunk generation.
 */
public class StructuresConfig {
	public static final Codec<StructuresConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StrongholdConfig.CODEC.optionalFieldOf("stronghold").forGetter(config -> Optional.ofNullable(config.stronghold)),
					Codec.simpleMap(Registry.STRUCTURE_FEATURE, StructureConfig.CODEC, Registry.STRUCTURE_FEATURE)
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
		.put(StructureFeature.field_24858, new StructureConfig(32, 8, 10387312))
		.put(StructureFeature.field_24847, new StructureConfig(32, 8, 14357617))
		.put(StructureFeature.field_24848, new StructureConfig(32, 8, 14357618))
		.put(StructureFeature.field_24846, new StructureConfig(32, 8, 14357619))
		.put(StructureFeature.SWAMP_HUT, new StructureConfig(32, 8, 14357620))
		.put(StructureFeature.field_24843, new StructureConfig(32, 8, 165745296))
		.put(StructureFeature.field_24852, new StructureConfig(1, 0, 0))
		.put(StructureFeature.field_24853, new StructureConfig(32, 5, 10387313))
		.put(StructureFeature.field_24856, new StructureConfig(20, 11, 10387313))
		.put(StructureFeature.field_24845, new StructureConfig(80, 20, 10387319))
		.put(StructureFeature.field_24857, new StructureConfig(1, 0, 0))
		.put(StructureFeature.field_24844, new StructureConfig(1, 0, 0))
		.put(StructureFeature.field_24849, new StructureConfig(40, 15, 34222645))
		.put(StructureFeature.field_24850, new StructureConfig(24, 4, 165745295))
		.put(StructureFeature.field_24854, new StructureConfig(20, 8, 14357621))
		.put(StructureFeature.field_24860, new StructureConfig(27, 4, 30084232))
		.put(StructureFeature.field_24855, new StructureConfig(27, 4, 30084232))
		.put(StructureFeature.field_24859, new StructureConfig(2, 1, 14357921))
		.build();
	/**
	 * Default placement settings for the stronghold.
	 */
	public static final StrongholdConfig DEFAULT_STRONGHOLD;
	private final Map<StructureFeature<?>, StructureConfig> structures;
	/**
	 * Placement settings for the stronghold for this particular combination of settings,
	 * may be null to disable placement of strongholds.
	 */
	@Nullable
	private final StrongholdConfig stronghold;

	public StructuresConfig(Optional<StrongholdConfig> stronghold, Map<StructureFeature<?>, StructureConfig> structures) {
		this.stronghold = (StrongholdConfig)stronghold.orElse(null);
		this.structures = structures;
	}

	/**
	 * Creates a new structure placement configuration with default values.
	 * 
	 * @param withStronghold determines if the default stronghold configuration should be included
	 */
	public StructuresConfig(boolean withStronghold) {
		this.structures = Maps.<StructureFeature<?>, StructureConfig>newHashMap(DEFAULT_STRUCTURES);
		this.stronghold = withStronghold ? DEFAULT_STRONGHOLD : null;
	}

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

	static {
		for (StructureFeature<?> structureFeature : Registry.STRUCTURE_FEATURE) {
			if (!DEFAULT_STRUCTURES.containsKey(structureFeature)) {
				throw new IllegalStateException("Structure feature without default settings: " + Registry.STRUCTURE_FEATURE.getId(structureFeature));
			}
		}

		DEFAULT_STRONGHOLD = new StrongholdConfig(32, 3, 128);
	}
}
