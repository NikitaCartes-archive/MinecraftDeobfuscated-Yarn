package net.minecraft.world.gen.chunk.placement;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Contains the configuration for placement of each structure type during chunk generation.
 */
public class StructuresConfig {
	public static final Codec<StructuresConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.simpleMap(Registry.STRUCTURE_FEATURE.getCodec(), StructurePlacement.TYPE_CODEC, Registry.STRUCTURE_FEATURE).forGetter(config -> config.placements)
				)
				.apply(instance, StructuresConfig::new)
	);
	/**
	 * Default {@linkplain StructurePlacement structure placements} for each known structure type.
	 * At startup, Minecraft validates that each registered structure has a default
	 * placement in this map. If mods register structures after this class
	 * has been initialized, the check will already have been made and a
	 * bad default configuration will be used instead (see below).
	 */
	public static final ImmutableMap<StructureFeature<?>, StructurePlacement> DEFAULT_PLACEMENTS = ImmutableMap.<StructureFeature<?>, StructurePlacement>builder()
		.put(StructureFeature.VILLAGE, new RandomSpreadStructurePlacement(34, 8, SpreadType.LINEAR, 10387312))
		.put(StructureFeature.DESERT_PYRAMID, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357617))
		.put(StructureFeature.IGLOO, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357618))
		.put(StructureFeature.JUNGLE_PYRAMID, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357619))
		.put(StructureFeature.SWAMP_HUT, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357620))
		.put(StructureFeature.PILLAGER_OUTPOST, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 165745296))
		.put(StructureFeature.MONUMENT, new RandomSpreadStructurePlacement(32, 5, SpreadType.TRIANGULAR, 10387313))
		.put(StructureFeature.END_CITY, new RandomSpreadStructurePlacement(20, 11, SpreadType.TRIANGULAR, 10387313))
		.put(StructureFeature.MANSION, new RandomSpreadStructurePlacement(80, 20, SpreadType.TRIANGULAR, 10387319))
		.put(StructureFeature.BURIED_TREASURE, new RandomSpreadStructurePlacement(1, 0, SpreadType.LINEAR, 0, new Vec3i(9, 0, 9)))
		.put(StructureFeature.MINESHAFT, new RandomSpreadStructurePlacement(1, 0, SpreadType.LINEAR, 0))
		.put(StructureFeature.RUINED_PORTAL, new RandomSpreadStructurePlacement(40, 15, SpreadType.LINEAR, 34222645))
		.put(StructureFeature.SHIPWRECK, new RandomSpreadStructurePlacement(24, 4, SpreadType.LINEAR, 165745295))
		.put(StructureFeature.OCEAN_RUIN, new RandomSpreadStructurePlacement(20, 8, SpreadType.LINEAR, 14357621))
		.put(StructureFeature.BASTION_REMNANT, new RandomSpreadStructurePlacement(27, 4, SpreadType.LINEAR, 30084232))
		.put(StructureFeature.FORTRESS, new RandomSpreadStructurePlacement(27, 4, SpreadType.LINEAR, 30084232))
		.put(StructureFeature.NETHER_FOSSIL, new RandomSpreadStructurePlacement(2, 1, SpreadType.LINEAR, 14357921))
		.build();
	/**
	 * Default structure placement for the stronghold.
	 */
	public static final ConcentricRingsStructurePlacement STRONGHOLD_PLACEMENT = new ConcentricRingsStructurePlacement(32, 3, 128);
	public static final ImmutableMap<StructureFeature<?>, StructurePlacement> DEFAULT_PLACEMENTS_WITH_STRONGHOLD = ImmutableMap.<StructureFeature<?>, StructurePlacement>builder()
		.putAll(DEFAULT_PLACEMENTS)
		.put(StructureFeature.STRONGHOLD, STRONGHOLD_PLACEMENT)
		.build();
	private final Map<StructureFeature<?>, StructurePlacement> placements;

	public StructuresConfig(Map<StructureFeature<?>, StructurePlacement> placements) {
		this.placements = placements;
	}

	/**
	 * Creates a new structure placement configuration with default values.
	 * 
	 * @param withStronghold determines if the default stronghold configuration should be included
	 */
	public StructuresConfig(boolean withStronghold) {
		this(Maps.<StructureFeature<?>, StructurePlacement>newHashMap(withStronghold ? DEFAULT_PLACEMENTS_WITH_STRONGHOLD : DEFAULT_PLACEMENTS));
	}

	@VisibleForTesting
	public Map<StructureFeature<?>, StructurePlacement> getStructures() {
		return this.placements;
	}

	/**
	 * Gets the structure placement for a specific structure type, or
	 * a default placement if placement for the structure was not explicitly configured.
	 */
	@Nullable
	public StructurePlacement getForType(StructureFeature<?> structureType) {
		return (StructurePlacement)this.placements.get(structureType);
	}
}
