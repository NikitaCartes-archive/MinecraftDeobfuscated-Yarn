package net.minecraft.world.poi;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class PointOfInterestType {
	private static final Supplier<Set<PointOfInterestType>> VILLAGER_WORKSTATIONS = Suppliers.memoize(
		() -> (Set<PointOfInterestType>)Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getWorkStation).collect(Collectors.toSet())
	);
	public static final Predicate<PointOfInterestType> IS_USED_BY_PROFESSION = pointOfInterestType -> ((Set)VILLAGER_WORKSTATIONS.get())
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
	public static final PointOfInterestType LODESTONE = register("lodestone", getAllStatesOf(Blocks.LODESTONE), 0, 1);
	public static final PointOfInterestType LIGHTNING_ROD = register("lightning_rod", getAllStatesOf(Blocks.LIGHTNING_ROD), 0, 1);
	protected static final Set<BlockState> REGISTERED_STATES = new ObjectOpenHashSet<>(BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.keySet());
	private final String id;
	private final Set<BlockState> blockStates;
	private final int ticketCount;
	private final Predicate<PointOfInterestType> completionCondition;
	private final int searchDistance;

	private static Set<BlockState> getAllStatesOf(Block block) {
		return ImmutableSet.copyOf(block.getStateManager().getStates());
	}

	private PointOfInterestType(String id, Set<BlockState> blockStates, int ticketCount, Predicate<PointOfInterestType> completionCondition, int searchDistance) {
		this.id = id;
		this.blockStates = ImmutableSet.copyOf(blockStates);
		this.ticketCount = ticketCount;
		this.completionCondition = completionCondition;
		this.searchDistance = searchDistance;
	}

	private PointOfInterestType(String id, Set<BlockState> blockStates, int ticketCount, int searchDistance) {
		this.id = id;
		this.blockStates = ImmutableSet.copyOf(blockStates);
		this.ticketCount = ticketCount;
		this.completionCondition = pointOfInterestType -> pointOfInterestType == this;
		this.searchDistance = searchDistance;
	}

	public int getTicketCount() {
		return this.ticketCount;
	}

	public Predicate<PointOfInterestType> getCompletionCondition() {
		return this.completionCondition;
	}

	public int getSearchDistance() {
		return this.searchDistance;
	}

	public String toString() {
		return this.id;
	}

	private static PointOfInterestType register(String id, Set<BlockState> workStationStates, int ticketCount, int searchDistance) {
		return setup(
			Registry.register(Registry.POINT_OF_INTEREST_TYPE, new Identifier(id), new PointOfInterestType(id, workStationStates, ticketCount, searchDistance))
		);
	}

	private static PointOfInterestType register(
		String id, Set<BlockState> workStationStates, int ticketCount, Predicate<PointOfInterestType> completionCondition, int searchDistance
	) {
		return setup(
			Registry.register(
				Registry.POINT_OF_INTEREST_TYPE, new Identifier(id), new PointOfInterestType(id, workStationStates, ticketCount, completionCondition, searchDistance)
			)
		);
	}

	private static PointOfInterestType setup(PointOfInterestType pointOfInterestType) {
		pointOfInterestType.blockStates.forEach(blockState -> {
			PointOfInterestType pointOfInterestType2 = (PointOfInterestType)BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.put(blockState, pointOfInterestType);
			if (pointOfInterestType2 != null) {
				throw (IllegalStateException)Util.throwOrPause(new IllegalStateException(String.format("%s is defined in too many tags", blockState)));
			}
		});
		return pointOfInterestType;
	}

	public static Optional<PointOfInterestType> from(BlockState state) {
		return Optional.ofNullable(BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.get(state));
	}
}
