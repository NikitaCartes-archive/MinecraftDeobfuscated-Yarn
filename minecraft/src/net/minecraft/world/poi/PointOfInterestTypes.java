package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class PointOfInterestTypes {
	public static final RegistryKey<PointOfInterestType> ARMORER = of("armorer");
	public static final RegistryKey<PointOfInterestType> BUTCHER = of("butcher");
	public static final RegistryKey<PointOfInterestType> CARTOGRAPHER = of("cartographer");
	public static final RegistryKey<PointOfInterestType> CLERIC = of("cleric");
	public static final RegistryKey<PointOfInterestType> FARMER = of("farmer");
	public static final RegistryKey<PointOfInterestType> FISHERMAN = of("fisherman");
	public static final RegistryKey<PointOfInterestType> FLETCHER = of("fletcher");
	public static final RegistryKey<PointOfInterestType> LEATHERWORKER = of("leatherworker");
	public static final RegistryKey<PointOfInterestType> LIBRARIAN = of("librarian");
	public static final RegistryKey<PointOfInterestType> MASON = of("mason");
	public static final RegistryKey<PointOfInterestType> SHEPHERD = of("shepherd");
	public static final RegistryKey<PointOfInterestType> TOOLSMITH = of("toolsmith");
	public static final RegistryKey<PointOfInterestType> WEAPONSMITH = of("weaponsmith");
	public static final RegistryKey<PointOfInterestType> HOME = of("home");
	public static final RegistryKey<PointOfInterestType> MEETING = of("meeting");
	public static final RegistryKey<PointOfInterestType> BEEHIVE = of("beehive");
	public static final RegistryKey<PointOfInterestType> BEE_NEST = of("bee_nest");
	public static final RegistryKey<PointOfInterestType> NETHER_PORTAL = of("nether_portal");
	public static final RegistryKey<PointOfInterestType> LODESTONE = of("lodestone");
	public static final RegistryKey<PointOfInterestType> LIGHTNING_ROD = of("lightning_rod");
	private static final Set<BlockState> BED_HEADS = (Set<BlockState>)ImmutableList.of(
			Blocks.RED_BED,
			Blocks.BLACK_BED,
			Blocks.BLUE_BED,
			Blocks.BROWN_BED,
			Blocks.CYAN_BED,
			Blocks.GRAY_BED,
			Blocks.GREEN_BED,
			Blocks.LIGHT_BLUE_BED,
			Blocks.LIGHT_GRAY_BED,
			Blocks.LIME_BED,
			Blocks.MAGENTA_BED,
			Blocks.ORANGE_BED,
			Blocks.PINK_BED,
			Blocks.PURPLE_BED,
			Blocks.WHITE_BED,
			Blocks.YELLOW_BED
		)
		.stream()
		.flatMap(block -> block.getStateManager().getStates().stream())
		.filter(blockState -> blockState.get(BedBlock.PART) == BedPart.HEAD)
		.collect(ImmutableSet.toImmutableSet());
	private static final Set<BlockState> CAULDRONS = (Set<BlockState>)ImmutableList.of(
			Blocks.CAULDRON, Blocks.LAVA_CAULDRON, Blocks.WATER_CAULDRON, Blocks.POWDER_SNOW_CAULDRON
		)
		.stream()
		.flatMap(block -> block.getStateManager().getStates().stream())
		.collect(ImmutableSet.toImmutableSet());
	private static final Map<BlockState, RegistryEntry<PointOfInterestType>> POI_STATES_TO_TYPE = Maps.<BlockState, RegistryEntry<PointOfInterestType>>newHashMap();

	private static Set<BlockState> getStatesOfBlock(Block block) {
		return ImmutableSet.copyOf(block.getStateManager().getStates());
	}

	private static RegistryKey<PointOfInterestType> of(String id) {
		return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(id));
	}

	private static PointOfInterestType register(
		Registry<PointOfInterestType> registry, RegistryKey<PointOfInterestType> key, Set<BlockState> states, int ticketCount, int searchDistance
	) {
		PointOfInterestType pointOfInterestType = new PointOfInterestType(states, ticketCount, searchDistance);
		Registry.register(registry, key, pointOfInterestType);
		registerStates(registry.entryOf(key), states);
		return pointOfInterestType;
	}

	private static void registerStates(RegistryEntry<PointOfInterestType> poiTypeEntry, Set<BlockState> states) {
		states.forEach(state -> {
			RegistryEntry<PointOfInterestType> registryEntry2 = (RegistryEntry<PointOfInterestType>)POI_STATES_TO_TYPE.put(state, poiTypeEntry);
			if (registryEntry2 != null) {
				throw (IllegalStateException)Util.throwOrPause(new IllegalStateException(String.format(Locale.ROOT, "%s is defined in more than one PoI type", state)));
			}
		});
	}

	public static Optional<RegistryEntry<PointOfInterestType>> getTypeForState(BlockState state) {
		return Optional.ofNullable((RegistryEntry)POI_STATES_TO_TYPE.get(state));
	}

	public static boolean isPointOfInterest(BlockState state) {
		return POI_STATES_TO_TYPE.containsKey(state);
	}

	public static PointOfInterestType registerAndGetDefault(Registry<PointOfInterestType> registry) {
		register(registry, ARMORER, getStatesOfBlock(Blocks.BLAST_FURNACE), 1, 1);
		register(registry, BUTCHER, getStatesOfBlock(Blocks.SMOKER), 1, 1);
		register(registry, CARTOGRAPHER, getStatesOfBlock(Blocks.CARTOGRAPHY_TABLE), 1, 1);
		register(registry, CLERIC, getStatesOfBlock(Blocks.BREWING_STAND), 1, 1);
		register(registry, FARMER, getStatesOfBlock(Blocks.COMPOSTER), 1, 1);
		register(registry, FISHERMAN, getStatesOfBlock(Blocks.BARREL), 1, 1);
		register(registry, FLETCHER, getStatesOfBlock(Blocks.FLETCHING_TABLE), 1, 1);
		register(registry, LEATHERWORKER, CAULDRONS, 1, 1);
		register(registry, LIBRARIAN, getStatesOfBlock(Blocks.LECTERN), 1, 1);
		register(registry, MASON, getStatesOfBlock(Blocks.STONECUTTER), 1, 1);
		register(registry, SHEPHERD, getStatesOfBlock(Blocks.LOOM), 1, 1);
		register(registry, TOOLSMITH, getStatesOfBlock(Blocks.SMITHING_TABLE), 1, 1);
		register(registry, WEAPONSMITH, getStatesOfBlock(Blocks.GRINDSTONE), 1, 1);
		register(registry, HOME, BED_HEADS, 1, 1);
		register(registry, MEETING, getStatesOfBlock(Blocks.BELL), 32, 6);
		register(registry, BEEHIVE, getStatesOfBlock(Blocks.BEEHIVE), 0, 1);
		register(registry, BEE_NEST, getStatesOfBlock(Blocks.BEE_NEST), 0, 1);
		register(registry, NETHER_PORTAL, getStatesOfBlock(Blocks.NETHER_PORTAL), 0, 1);
		register(registry, LODESTONE, getStatesOfBlock(Blocks.LODESTONE), 0, 1);
		return register(registry, LIGHTNING_ROD, getStatesOfBlock(Blocks.LIGHTNING_ROD), 0, 1);
	}
}
