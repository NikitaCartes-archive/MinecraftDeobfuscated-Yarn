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
		.flatMap(block -> block.getStateFactory().getStates().stream())
		.filter(blockState -> blockState.get(BedBlock.PART) == BedPart.HEAD)
		.collect(ImmutableSet.toImmutableSet());
	private static final Map<BlockState, PointOfInterestType> BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE = Maps.<BlockState, PointOfInterestType>newHashMap();
	public static final PointOfInterestType UNEMPLOYED = register("unemployed", ImmutableSet.of(), 1, null, IS_USED_BY_PROFESSION);
	public static final PointOfInterestType ARMORER = register("armorer", getAllStatesOf(Blocks.BLAST_FURNACE), 1, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);
	public static final PointOfInterestType BUTCHER = register("butcher", getAllStatesOf(Blocks.SMOKER), 1, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER);
	public static final PointOfInterestType CARTOGRAPHER = register(
		"cartographer", getAllStatesOf(Blocks.CARTOGRAPHY_TABLE), 1, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER
	);
	public static final PointOfInterestType CLERIC = register("cleric", getAllStatesOf(Blocks.BREWING_STAND), 1, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
	public static final PointOfInterestType FARMER = register("farmer", getAllStatesOf(Blocks.COMPOSTER), 1, SoundEvents.ENTITY_VILLAGER_WORK_FARMER);
	public static final PointOfInterestType FISHERMAN = register("fisherman", getAllStatesOf(Blocks.BARREL), 1, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);
	public static final PointOfInterestType FLETCHER = register("fletcher", getAllStatesOf(Blocks.FLETCHING_TABLE), 1, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER);
	public static final PointOfInterestType LEATHERWORKER = register(
		"leatherworker", getAllStatesOf(Blocks.CAULDRON), 1, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER
	);
	public static final PointOfInterestType LIBRARIAN = register("librarian", getAllStatesOf(Blocks.LECTERN), 1, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
	public static final PointOfInterestType MASON = register("mason", getAllStatesOf(Blocks.STONECUTTER), 1, SoundEvents.ENTITY_VILLAGER_WORK_MASON);
	public static final PointOfInterestType NITWIT = register("nitwit", ImmutableSet.of(), 1, null);
	public static final PointOfInterestType SHEPHERD = register("shepherd", getAllStatesOf(Blocks.LOOM), 1, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD);
	public static final PointOfInterestType TOOLSMITH = register("toolsmith", getAllStatesOf(Blocks.SMITHING_TABLE), 1, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);
	public static final PointOfInterestType WEAPONSMITH = register(
		"weaponsmith", getAllStatesOf(Blocks.GRINDSTONE), 1, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH
	);
	public static final PointOfInterestType HOME = register("home", BED_STATES, 1, null);
	public static final PointOfInterestType MEETING = register("meeting", getAllStatesOf(Blocks.BELL), 32, null);
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
