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

public class StructuresConfig {
	public static final Codec<StructuresConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StrongholdConfig.CODEC.optionalFieldOf("stronghold").forGetter(structuresConfig -> Optional.ofNullable(structuresConfig.stronghold)),
					Codec.simpleMap(Registry.STRUCTURE_FEATURE, StructureConfig.CODEC, Registry.STRUCTURE_FEATURE)
						.fieldOf("structures")
						.forGetter(structuresConfig -> structuresConfig.structures)
				)
				.apply(instance, StructuresConfig::new)
	);
	public static final ImmutableMap<StructureFeature<?>, StructureConfig> DEFAULT_STRUCTURES = ImmutableMap.<StructureFeature<?>, StructureConfig>builder()
		.put(StructureFeature.VILLAGE, new StructureConfig(32, 8, 10387312))
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
		.put(StructureFeature.BASTION_REMNANT, new StructureConfig(30, 4, 30084232))
		.put(StructureFeature.FORTRESS, new StructureConfig(30, 4, 30084232))
		.put(StructureFeature.NETHER_FOSSIL, new StructureConfig(2, 1, 14357921))
		.build();
	public static final StrongholdConfig DEFAULT_STRONGHOLD;
	private final Map<StructureFeature<?>, StructureConfig> structures;
	@Nullable
	private final StrongholdConfig stronghold;

	public StructuresConfig(Optional<StrongholdConfig> stronghold, Map<StructureFeature<?>, StructureConfig> structures) {
		this.stronghold = (StrongholdConfig)stronghold.orElse(null);
		this.structures = structures;
	}

	public StructuresConfig(boolean bl) {
		this.structures = Maps.<StructureFeature<?>, StructureConfig>newHashMap(DEFAULT_STRUCTURES);
		this.stronghold = bl ? DEFAULT_STRONGHOLD : null;
	}

	public Map<StructureFeature<?>, StructureConfig> getStructures() {
		return this.structures;
	}

	public StructureConfig method_28600(StructureFeature<?> structureFeature) {
		return (StructureConfig)this.structures.getOrDefault(structureFeature, new StructureConfig(1, 0, 0));
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
