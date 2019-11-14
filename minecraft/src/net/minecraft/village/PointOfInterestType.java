package net.minecraft.village;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class PointOfInterestType {
	private static final Predicate<PointOfInterestType> IS_USED_BY_PROFESSION = pointOfInterestType -> ((Set)Registry.VILLAGER_PROFESSION
				.stream()
				.map(VillagerProfession::getWorkStation)
				.collect(Collectors.toSet()))
			.contains(pointOfInterestType);
	public static final Predicate<PointOfInterestType> ALWAYS_TRUE = pointOfInterestType -> true;
	private static final Set<BlockState> BED_STATES = (Set<BlockState>)ImmutableList.of(
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
	private static final Map<BlockState, PointOfInterestType> BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE = Maps.<BlockState, PointOfInterestType>newHashMap();
	public static final PointOfInterestType UNEMPLOYED = register("unemployed", ImmutableSet.of(), 1, IS_USED_BY_PROFESSION, 1);
	public static final PointOfInterestType ARMORER = register("armorer", getAllStatesOf(Blocks.BLAST_FURNACE), 1, 1);
	public static final PointOfInterestType BUTCHER = register("butcher", getAllStatesOf(Blocks.SMOKER), 1, 1);
	public static final PointOfInterestType CARTOGRAPHER = register("cartographer", getAllStatesOf(Blocks.CARTOGRAPHY_TABLE), 1, 1);
	public static final PointOfInterestType CLERIC = register("cleric", getAllStatesOf(Blocks.BREWING_STAND), 1, 1);
	public static final PointOfInterestType FARMER = register("farmer", getAllStatesOf(Blocks.COMPOSTER), 1, 1);
	public static final PointOfInterestType FISHERMAN = register("fisherman", getAllStatesOf(Blocks.BARREL), 1, 1);
	public static final PointOfInterestType FLETCHER = register("fletcher", getAllStatesOf(Blocks.FLETCHING_TABLE), 1, 1);
	public static final PointOfInterestType LEATHERWORKER = register("leatherworker", getAllStatesOf(Blocks.CAULDRON), 1, 1);
	public static final PointOfInterestType LIBRARIAN = register("librarian", getAllStatesOf(Blocks.LECTERN), 1, 1);
	public static final PointOfInterestType MASON = register("mason", getAllStatesOf(Blocks.STONECUTTER), 1, 1);
	public static final PointOfInterestType NITWIT = register("nitwit", ImmutableSet.of(), 1, 1);
	public static final PointOfInterestType SHEPHERD = register("shepherd", getAllStatesOf(Blocks.LOOM), 1, 1);
	public static final PointOfInterestType TOOLSMITH = register("toolsmith", getAllStatesOf(Blocks.SMITHING_TABLE), 1, 1);
	public static final PointOfInterestType WEAPONSMITH = register("weaponsmith", getAllStatesOf(Blocks.GRINDSTONE), 1, 1);
	public static final PointOfInterestType HOME = register("home", BED_STATES, 1, 1);
	public static final PointOfInterestType MEETING = register("meeting", getAllStatesOf(Blocks.BELL), 32, 6);
	public static final PointOfInterestType BEEHIVE = register("beehive", getAllStatesOf(Blocks.BEEHIVE), 0, 1);
	public static final PointOfInterestType BEE_NEST = register("bee_nest", getAllStatesOf(Blocks.BEE_NEST), 0, 1);
	public static final PointOfInterestType NETHER_PORTAL = register("nether_portal", getAllStatesOf(Blocks.NETHER_PORTAL), 0, 1);
	private final String id;
	private final Set<BlockState> workStationStates;
	private final int ticketCount;
	private final Predicate<PointOfInterestType> completionCondition;
	private final int field_20298;

	private static Set<BlockState> getAllStatesOf(Block block) {
		return ImmutableSet.copyOf(block.getStateManager().getStates());
	}

	private PointOfInterestType(String string, Set<BlockState> set, int i, Predicate<PointOfInterestType> predicate, int j) {
		this.id = string;
		this.workStationStates = ImmutableSet.copyOf(set);
		this.ticketCount = i;
		this.completionCondition = predicate;
		this.field_20298 = j;
	}

	private PointOfInterestType(String string, Set<BlockState> set, int i, int j) {
		this.id = string;
		this.workStationStates = ImmutableSet.copyOf(set);
		this.ticketCount = i;
		this.completionCondition = pointOfInterestType -> pointOfInterestType == this;
		this.field_20298 = j;
	}

	public int getTicketCount() {
		return this.ticketCount;
	}

	public Predicate<PointOfInterestType> getCompletionCondition() {
		return this.completionCondition;
	}

	public int method_21648() {
		return this.field_20298;
	}

	public String toString() {
		return this.id;
	}

	private static PointOfInterestType register(String id, Set<BlockState> set, int i, int j) {
		return setup(Registry.POINT_OF_INTEREST_TYPE.add(new Identifier(id), new PointOfInterestType(id, set, i, j)));
	}

	private static PointOfInterestType register(String id, Set<BlockState> set, int i, Predicate<PointOfInterestType> predicate, int j) {
		return setup(Registry.POINT_OF_INTEREST_TYPE.add(new Identifier(id), new PointOfInterestType(id, set, i, predicate, j)));
	}

	private static PointOfInterestType setup(PointOfInterestType pointOfInterestType) {
		pointOfInterestType.workStationStates.forEach(blockState -> {
			PointOfInterestType pointOfInterestType2 = (PointOfInterestType)BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.put(blockState, pointOfInterestType);
			if (pointOfInterestType2 != null) {
				throw (IllegalStateException)Util.throwOrPause(new IllegalStateException(String.format("%s is defined in too many tags", blockState)));
			}
		});
		return pointOfInterestType;
	}

	public static Optional<PointOfInterestType> from(BlockState blockState) {
		return Optional.ofNullable(BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.get(blockState));
	}

	public static Stream<BlockState> getAllAssociatedStates() {
		return BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.keySet().stream();
	}
}
