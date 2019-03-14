package net.minecraft.village;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

public class PointOfInterestType {
	public static final Predicate<PointOfInterestType> IS_USED_BY_PROFESSION = pointOfInterestType -> ((Set)Registry.VILLAGER_PROFESSION
				.stream()
				.map(VillagerProfession::getWorkStation)
				.collect(Collectors.toSet()))
			.contains(pointOfInterestType);
	public static final Predicate<PointOfInterestType> ALWAYS_TRUE = pointOfInterestType -> true;
	public static final PointOfInterestType field_18502 = register("unemployed", BlockTags.field_18830, 1, null, IS_USED_BY_PROFESSION);
	public static final PointOfInterestType field_18503 = register("armorer", BlockTags.field_18831, 1, SoundEvents.field_18826);
	public static final PointOfInterestType field_18504 = register("butcher", BlockTags.field_18832, 1, SoundEvents.field_18827);
	public static final PointOfInterestType field_18505 = register("cartographer", BlockTags.field_18833, 1, SoundEvents.field_18828);
	public static final PointOfInterestType field_18506 = register("cleric", BlockTags.field_18834, 1, SoundEvents.field_18829);
	public static final PointOfInterestType field_18507 = register("farmer", BlockTags.field_18835, 1, SoundEvents.field_18817);
	public static final PointOfInterestType field_18508 = register("fisherman", BlockTags.field_18836, 1, SoundEvents.field_18818);
	public static final PointOfInterestType field_18509 = register("fletcher", BlockTags.field_18837, 1, SoundEvents.field_18819);
	public static final PointOfInterestType field_18510 = register("leatherworker", BlockTags.field_18838, 1, SoundEvents.field_18820);
	public static final PointOfInterestType field_18511 = register("librarian", BlockTags.field_18839, 1, SoundEvents.field_18821);
	public static final PointOfInterestType field_18512 = register("mason", BlockTags.field_18840, 1, SoundEvents.field_18822);
	public static final PointOfInterestType field_18513 = register("nitwit", BlockTags.field_18841, 1, null);
	public static final PointOfInterestType field_18514 = register("shepherd", BlockTags.field_18842, 1, SoundEvents.field_18823);
	public static final PointOfInterestType field_18515 = register("toolsmith", BlockTags.field_18843, 1, SoundEvents.field_18824);
	public static final PointOfInterestType field_18516 = register("weaponsmith", BlockTags.field_18844, 1, SoundEvents.field_18825);
	public static final PointOfInterestType field_18517 = register("home", BlockTags.field_16443, 1, null);
	public static final PointOfInterestType field_18518 = register("meeting", BlockTags.field_18846, 32, null);
	private static final Map<BlockState, PointOfInterestType> field_18849 = Maps.<BlockState, PointOfInterestType>newHashMap();
	private final String id;
	private final Tag<Block> blockTag;
	private final Set<BlockState> field_18850 = Sets.<BlockState>newHashSet();
	private final int ticketCount;
	@Nullable
	private final SoundEvent sound;
	private final Predicate<PointOfInterestType> completedCondition;

	private PointOfInterestType(String string, Tag<Block> tag, int i, @Nullable SoundEvent soundEvent, Predicate<PointOfInterestType> predicate) {
		this.id = string;
		this.blockTag = tag;
		this.ticketCount = i;
		this.sound = soundEvent;
		this.completedCondition = predicate;
	}

	private PointOfInterestType(String string, Tag<Block> tag, int i, @Nullable SoundEvent soundEvent) {
		this.id = string;
		this.blockTag = tag;
		this.ticketCount = i;
		this.sound = soundEvent;
		this.completedCondition = pointOfInterestType -> pointOfInterestType == this;
	}

	@Environment(EnvType.CLIENT)
	public String register() {
		return this.id;
	}

	public int getTicketCount() {
		return this.ticketCount;
	}

	public Predicate<PointOfInterestType> getCompletedCondition() {
		return this.completedCondition;
	}

	public String toString() {
		return this.id;
	}

	@Nullable
	public SoundEvent getSound() {
		return this.sound;
	}

	private static PointOfInterestType register(String string, Tag<Block> tag, int i, @Nullable SoundEvent soundEvent) {
		return Registry.POINT_OF_INTEREST_TYPE.add(new Identifier(string), new PointOfInterestType(string, tag, i, soundEvent));
	}

	private static PointOfInterestType register(String string, Tag<Block> tag, int i, @Nullable SoundEvent soundEvent, Predicate<PointOfInterestType> predicate) {
		return Registry.POINT_OF_INTEREST_TYPE.add(new Identifier(string), new PointOfInterestType(string, tag, i, soundEvent, predicate));
	}

	public static Optional<PointOfInterestType> method_19516(BlockState blockState) {
		return Optional.ofNullable(field_18849.get(blockState));
	}

	private static boolean method_19517(BlockState blockState) {
		return !blockState.matches(BlockTags.field_16443) || blockState.get(BedBlock.PART) != BedPart.field_12557;
	}

	public static Stream<BlockState> method_19518() {
		return field_18849.keySet().stream();
	}

	public static CompletableFuture<Void> method_19515(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	) {
		return helper.waitForAll(net.minecraft.util.Void.INSTANCE)
			.thenRunAsync(
				() -> {
					field_18849.clear();
					Registry.POINT_OF_INTEREST_TYPE.forEach(pointOfInterestType -> pointOfInterestType.field_18850.clear());
					Registry.BLOCK
						.stream()
						.filter(block -> block.matches(BlockTags.field_18847))
						.forEach(
							block -> {
								List<PointOfInterestType> list = (List<PointOfInterestType>)Registry.POINT_OF_INTEREST_TYPE
									.stream()
									.filter(pointOfInterestTypex -> pointOfInterestTypex.blockTag.contains(block))
									.collect(Collectors.toList());
								if (list.size() > 1) {
									throw new IllegalStateException(String.format("%s is defined in too many tags", block));
								} else {
									PointOfInterestType pointOfInterestType = (PointOfInterestType)list.get(0);
									block.getStateFactory().getStates().stream().filter(PointOfInterestType::method_19517).forEach(blockState -> {
										pointOfInterestType.field_18850.add(blockState);
										field_18849.put(blockState, pointOfInterestType);
									});
								}
							}
						);
				},
				executor2
			);
	}
}
