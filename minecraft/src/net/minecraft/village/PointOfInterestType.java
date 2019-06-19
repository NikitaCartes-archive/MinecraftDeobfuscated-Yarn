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
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PointOfInterestType {
	private static final Predicate<PointOfInterestType> IS_USED_BY_PROFESSION = pointOfInterestType -> ((Set)Registry.VILLAGER_PROFESSION
				.stream()
				.map(VillagerProfession::getWorkStation)
				.collect(Collectors.toSet()))
			.contains(pointOfInterestType);
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
		.flatMap(block -> block.getStateFactory().getStates().stream())
		.filter(blockState -> blockState.get(BedBlock.PART) == BedPart.field_12560)
		.collect(ImmutableSet.toImmutableSet());
	private static final Map<BlockState, PointOfInterestType> BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE = Maps.<BlockState, PointOfInterestType>newHashMap();
	public static final PointOfInterestType field_18502 = register("unemployed", ImmutableSet.of(), 1, null, IS_USED_BY_PROFESSION);
	public static final PointOfInterestType field_18503 = register("armorer", getAllStatesOf(Blocks.field_16333), 1, SoundEvents.field_18826);
	public static final PointOfInterestType field_18504 = register("butcher", getAllStatesOf(Blocks.field_16334), 1, SoundEvents.field_18827);
	public static final PointOfInterestType field_18505 = register("cartographer", getAllStatesOf(Blocks.field_16336), 1, SoundEvents.field_18828);
	public static final PointOfInterestType field_18506 = register("cleric", getAllStatesOf(Blocks.field_10333), 1, SoundEvents.field_18829);
	public static final PointOfInterestType field_18507 = register("farmer", getAllStatesOf(Blocks.field_17563), 1, SoundEvents.field_18817);
	public static final PointOfInterestType field_18508 = register("fisherman", getAllStatesOf(Blocks.field_16328), 1, SoundEvents.field_18818);
	public static final PointOfInterestType field_18509 = register("fletcher", getAllStatesOf(Blocks.field_16331), 1, SoundEvents.field_18819);
	public static final PointOfInterestType field_18510 = register("leatherworker", getAllStatesOf(Blocks.field_10593), 1, SoundEvents.field_18820);
	public static final PointOfInterestType field_18511 = register("librarian", getAllStatesOf(Blocks.field_16330), 1, SoundEvents.field_18821);
	public static final PointOfInterestType field_18512 = register("mason", getAllStatesOf(Blocks.field_16335), 1, SoundEvents.field_18822);
	public static final PointOfInterestType field_18513 = register("nitwit", ImmutableSet.of(), 1, null);
	public static final PointOfInterestType field_18514 = register("shepherd", getAllStatesOf(Blocks.field_10083), 1, SoundEvents.field_18823);
	public static final PointOfInterestType field_18515 = register("toolsmith", getAllStatesOf(Blocks.field_16329), 1, SoundEvents.field_18824);
	public static final PointOfInterestType field_18516 = register("weaponsmith", getAllStatesOf(Blocks.field_16337), 1, SoundEvents.field_18825);
	public static final PointOfInterestType field_18517 = register("home", BED_STATES, 1, null);
	public static final PointOfInterestType field_18518 = register("meeting", getAllStatesOf(Blocks.field_16332), 32, null);
	private final String id;
	private final Set<BlockState> workStationStates;
	private final int ticketCount;
	@Nullable
	private final SoundEvent sound;
	private final Predicate<PointOfInterestType> completionCondition;

	private static Set<BlockState> getAllStatesOf(Block block) {
		return ImmutableSet.copyOf(block.getStateFactory().getStates());
	}

	private PointOfInterestType(String string, Set<BlockState> set, int i, @Nullable SoundEvent soundEvent, Predicate<PointOfInterestType> predicate) {
		this.id = string;
		this.workStationStates = ImmutableSet.copyOf(set);
		this.ticketCount = i;
		this.sound = soundEvent;
		this.completionCondition = predicate;
	}

	private PointOfInterestType(String string, Set<BlockState> set, int i, @Nullable SoundEvent soundEvent) {
		this.id = string;
		this.workStationStates = ImmutableSet.copyOf(set);
		this.ticketCount = i;
		this.sound = soundEvent;
		this.completionCondition = pointOfInterestType -> pointOfInterestType == this;
	}

	public int getTicketCount() {
		return this.ticketCount;
	}

	public Predicate<PointOfInterestType> getCompletionCondition() {
		return this.completionCondition;
	}

	public String toString() {
		return this.id;
	}

	@Nullable
	public SoundEvent getSound() {
		return this.sound;
	}

	private static PointOfInterestType register(String string, Set<BlockState> set, int i, @Nullable SoundEvent soundEvent) {
		return setup(Registry.POINT_OF_INTEREST_TYPE.add(new Identifier(string), new PointOfInterestType(string, set, i, soundEvent)));
	}

	private static PointOfInterestType register(
		String string, Set<BlockState> set, int i, @Nullable SoundEvent soundEvent, Predicate<PointOfInterestType> predicate
	) {
		return setup(Registry.POINT_OF_INTEREST_TYPE.add(new Identifier(string), new PointOfInterestType(string, set, i, soundEvent, predicate)));
	}

	private static PointOfInterestType setup(PointOfInterestType pointOfInterestType) {
		pointOfInterestType.workStationStates.forEach(blockState -> {
			PointOfInterestType pointOfInterestType2 = (PointOfInterestType)BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.put(blockState, pointOfInterestType);
			if (pointOfInterestType2 != null) {
				throw new IllegalStateException(String.format("%s is defined in too many tags", blockState));
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
