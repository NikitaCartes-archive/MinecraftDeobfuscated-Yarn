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
	private static final Supplier<Set<PointOfInterestType>> field_25163 = Suppliers.memoize(
		() -> (Set<PointOfInterestType>)Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getWorkStation).collect(Collectors.toSet())
	);
	public static final Predicate<PointOfInterestType> IS_USED_BY_PROFESSION = pointOfInterestType -> ((Set)field_25163.get()).contains(pointOfInterestType);
	public static final Predicate<PointOfInterestType> ALWAYS_TRUE = pointOfInterestType -> true;
	private static final Set<BlockState> BED_STATES = (Set<BlockState>)ImmutableList.of(
			Blocks.field_10069,
			Blocks.field_10461,
			Blocks.field_10527,
			Blocks.field_10288,
			Blocks.field_10109,
			Blocks.field_10141,
			Blocks.field_10561,
			Blocks.field_10621,
			Blocks.field_10326,
			Blocks.field_10180,
			Blocks.field_10230,
			Blocks.field_10410,
			Blocks.field_10610,
			Blocks.field_10019,
			Blocks.field_10120,
			Blocks.field_10356
		)
		.stream()
		.flatMap(block -> block.getStateManager().getStates().stream())
		.filter(blockState -> blockState.get(BedBlock.PART) == BedPart.field_12560)
		.collect(ImmutableSet.toImmutableSet());
	private static final Map<BlockState, PointOfInterestType> BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE = Maps.<BlockState, PointOfInterestType>newHashMap();
	public static final PointOfInterestType field_18502 = register("unemployed", ImmutableSet.of(), 1, IS_USED_BY_PROFESSION, 1);
	public static final PointOfInterestType field_18503 = register("armorer", getAllStatesOf(Blocks.field_16333), 1, 1);
	public static final PointOfInterestType field_18504 = register("butcher", getAllStatesOf(Blocks.field_16334), 1, 1);
	public static final PointOfInterestType field_18505 = register("cartographer", getAllStatesOf(Blocks.field_16336), 1, 1);
	public static final PointOfInterestType field_18506 = register("cleric", getAllStatesOf(Blocks.field_10333), 1, 1);
	public static final PointOfInterestType field_18507 = register("farmer", getAllStatesOf(Blocks.field_17563), 1, 1);
	public static final PointOfInterestType field_18508 = register("fisherman", getAllStatesOf(Blocks.field_16328), 1, 1);
	public static final PointOfInterestType field_18509 = register("fletcher", getAllStatesOf(Blocks.field_16331), 1, 1);
	public static final PointOfInterestType field_18510 = register("leatherworker", getAllStatesOf(Blocks.field_10593), 1, 1);
	public static final PointOfInterestType field_18511 = register("librarian", getAllStatesOf(Blocks.field_16330), 1, 1);
	public static final PointOfInterestType field_18512 = register("mason", getAllStatesOf(Blocks.field_16335), 1, 1);
	public static final PointOfInterestType field_18513 = register("nitwit", ImmutableSet.of(), 1, 1);
	public static final PointOfInterestType field_18514 = register("shepherd", getAllStatesOf(Blocks.field_10083), 1, 1);
	public static final PointOfInterestType field_18515 = register("toolsmith", getAllStatesOf(Blocks.field_16329), 1, 1);
	public static final PointOfInterestType field_18516 = register("weaponsmith", getAllStatesOf(Blocks.field_16337), 1, 1);
	public static final PointOfInterestType field_18517 = register("home", BED_STATES, 1, 1);
	public static final PointOfInterestType field_18518 = register("meeting", getAllStatesOf(Blocks.field_16332), 32, 6);
	public static final PointOfInterestType field_20351 = register("beehive", getAllStatesOf(Blocks.field_20422), 0, 1);
	public static final PointOfInterestType field_20352 = register("bee_nest", getAllStatesOf(Blocks.field_20421), 0, 1);
	public static final PointOfInterestType field_20632 = register("nether_portal", getAllStatesOf(Blocks.field_10316), 0, 1);
	public static final PointOfInterestType field_23229 = register("lodestone", getAllStatesOf(Blocks.field_23261), 0, 1);
	protected static final Set<BlockState> field_25162 = new ObjectOpenHashSet<>(BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.keySet());
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
