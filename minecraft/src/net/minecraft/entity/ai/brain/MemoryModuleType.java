package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.dynamic.DynamicSerializableBoolean;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.dynamic.Timestamp;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class MemoryModuleType<U> {
	public static final MemoryModuleType<Void> DUMMY = register("dummy");
	public static final MemoryModuleType<GlobalPos> HOME = register("home", Optional.of(GlobalPos::deserialize));
	public static final MemoryModuleType<GlobalPos> JOB_SITE = register("job_site", Optional.of(GlobalPos::deserialize));
	public static final MemoryModuleType<GlobalPos> MEETING_POINT = register("meeting_point", Optional.of(GlobalPos::deserialize));
	public static final MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE = register("secondary_job_site");
	public static final MemoryModuleType<List<LivingEntity>> MOBS = register("mobs");
	public static final MemoryModuleType<List<LivingEntity>> VISIBLE_MOBS = register("visible_mobs");
	public static final MemoryModuleType<List<LivingEntity>> VISIBLE_VILLAGER_BABIES = register("visible_villager_babies");
	public static final MemoryModuleType<List<PlayerEntity>> NEAREST_PLAYERS = register("nearest_players");
	public static final MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_PLAYER = register("nearest_visible_player");
	public static final MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_TARGETABLE_PLAYER = register("nearest_visible_targetable_player");
	public static final MemoryModuleType<WalkTarget> WALK_TARGET = register("walk_target");
	public static final MemoryModuleType<LookTarget> LOOK_TARGET = register("look_target");
	public static final MemoryModuleType<LivingEntity> ATTACK_TARGET = register("attack_target");
	public static final MemoryModuleType<Boolean> ATTACK_COOLING_DOWN = register("attack_cooling_down");
	public static final MemoryModuleType<LivingEntity> INTERACTION_TARGET = register("interaction_target");
	public static final MemoryModuleType<PassiveEntity> BREED_TARGET = register("breed_target");
	public static final MemoryModuleType<Entity> RIDE_TARGET = register("ride_target");
	public static final MemoryModuleType<Path> PATH = register("path");
	public static final MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS = register("interactable_doors");
	public static final MemoryModuleType<Set<GlobalPos>> OPENED_DOORS = register("opened_doors");
	public static final MemoryModuleType<BlockPos> NEAREST_BED = register("nearest_bed");
	public static final MemoryModuleType<DamageSource> HURT_BY = register("hurt_by");
	public static final MemoryModuleType<LivingEntity> HURT_BY_ENTITY = register("hurt_by_entity");
	public static final MemoryModuleType<LivingEntity> AVOID_TARGET = register("avoid_target");
	public static final MemoryModuleType<LivingEntity> NEAREST_HOSTILE = register("nearest_hostile");
	public static final MemoryModuleType<GlobalPos> HIDING_PLACE = register("hiding_place");
	public static final MemoryModuleType<Long> HEARD_BELL_TIME = register("heard_bell_time");
	public static final MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE = register("cant_reach_walk_target_since");
	public static final MemoryModuleType<Long> GOLEM_LAST_SEEN_TIME = register("golem_last_seen_time");
	public static final MemoryModuleType<Timestamp> LAST_SLEPT = register("last_slept", Optional.of(Timestamp::of));
	public static final MemoryModuleType<Timestamp> LAST_WOKEN = register("last_woken", Optional.of(Timestamp::of));
	public static final MemoryModuleType<Timestamp> LAST_WORKED_AT_POI = register("last_worked_at_poi", Optional.of(Timestamp::of));
	public static final MemoryModuleType<ItemEntity> NEAREST_VISIBLE_WANTED_ITEM = register("nearest_visible_wanted_item");
	public static final MemoryModuleType<DynamicSerializableUuid> ANGRY_AT = register("angry_at", Optional.of(DynamicSerializableUuid::of));
	public static final MemoryModuleType<DynamicSerializableBoolean> ADMIRING_ITEM = register("admiring_item", Optional.of(DynamicSerializableBoolean::of));
	public static final MemoryModuleType<DynamicSerializableBoolean> ADMIRING_DISABLED = register("admiring_disabled", Optional.of(DynamicSerializableBoolean::of));
	public static final MemoryModuleType<DynamicSerializableBoolean> HUNTED_RECENTLY = register("hunted_recently", Optional.of(DynamicSerializableBoolean::of));
	public static final MemoryModuleType<BlockPos> CELEBRATE_LOCATION = register("celebrate_location");
	public static final MemoryModuleType<WitherSkeletonEntity> NEAREST_VISIBLE_WITHER_SKELETON = register("nearest_visible_wither_skeleton");
	public static final MemoryModuleType<HoglinEntity> NEAREST_VISIBLE_ADULT_HOGLIN = register("nearest_visible_adult_hoglin");
	public static final MemoryModuleType<HoglinEntity> NEAREST_VISIBLE_BABY_HOGLIN = register("nearest_visible_baby_hoglin");
	public static final MemoryModuleType<PiglinEntity> NEAREST_VISIBLE_BABY_PIGLIN = register("nearest_visible_baby_piglin");
	public static final MemoryModuleType<PlayerEntity> NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD = register("nearest_targetable_player_not_wearing_gold");
	public static final MemoryModuleType<List<PiglinEntity>> NEAREST_ADULT_PIGLINS = register("nearest_adult_piglins");
	public static final MemoryModuleType<List<PiglinEntity>> NEAREST_VISIBLE_ADULT_PIGLINS = register("nearest_visible_adult_piglins");
	public static final MemoryModuleType<List<HoglinEntity>> NEAREST_VISIBLE_ADULT_HOGLINS = register("nearest_visible_adult_hoglins");
	public static final MemoryModuleType<PiglinEntity> NEAREST_VISIBLE_ADULT_PIGLIN = register("nearest_visible_adult_piglin");
	public static final MemoryModuleType<ZombifiedPiglinEntity> NEAREST_VISIBLE_ZOMBIFIED_PIGLIN = register("nearest_visible_zombified_piglin");
	public static final MemoryModuleType<Integer> VISIBLE_ADULT_PIGLIN_COUNT = register("visible_adult_piglin_count");
	public static final MemoryModuleType<Integer> VISIBLE_ADULT_HOGLIN_COUNT = register("visible_adult_hoglin_count");
	public static final MemoryModuleType<PlayerEntity> NEAREST_PLAYER_HOLDING_WANTED_ITEM = register("nearest_player_holding_wanted_item");
	public static final MemoryModuleType<Boolean> ATE_RECENTLY = register("ate_recently");
	public static final MemoryModuleType<BlockPos> NEAREST_REPELLENT = register("nearest_repellent");
	public static final MemoryModuleType<Boolean> PACIFIED = register("pacified");
	private final Optional<Function<Dynamic<?>, U>> factory;

	private MemoryModuleType(Optional<Function<Dynamic<?>, U>> factory) {
		this.factory = factory;
	}

	public String toString() {
		return Registry.MEMORY_MODULE_TYPE.getId(this).toString();
	}

	public Optional<Function<Dynamic<?>, U>> getFactory() {
		return this.factory;
	}

	private static <U extends DynamicSerializable> MemoryModuleType<U> register(String id, Optional<Function<Dynamic<?>, U>> factory) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(id), new MemoryModuleType<>(factory));
	}

	private static <U> MemoryModuleType<U> register(String id) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(id), new MemoryModuleType<>(Optional.empty()));
	}
}
