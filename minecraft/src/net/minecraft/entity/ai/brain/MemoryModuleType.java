package net.minecraft.entity.ai.brain;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class MemoryModuleType<U> {
	public static final MemoryModuleType<Void> field_18437 = register("dummy");
	public static final MemoryModuleType<GlobalPos> field_18438 = register("home", GlobalPos.CODEC);
	public static final MemoryModuleType<GlobalPos> field_18439 = register("job_site", GlobalPos.CODEC);
	public static final MemoryModuleType<GlobalPos> field_25160 = register("potential_job_site", GlobalPos.CODEC);
	public static final MemoryModuleType<GlobalPos> field_18440 = register("meeting_point", GlobalPos.CODEC);
	public static final MemoryModuleType<List<GlobalPos>> field_18873 = register("secondary_job_site");
	public static final MemoryModuleType<List<LivingEntity>> field_18441 = register("mobs");
	public static final MemoryModuleType<List<LivingEntity>> field_18442 = register("visible_mobs");
	public static final MemoryModuleType<List<LivingEntity>> field_19006 = register("visible_villager_babies");
	public static final MemoryModuleType<List<PlayerEntity>> field_18443 = register("nearest_players");
	public static final MemoryModuleType<PlayerEntity> field_18444 = register("nearest_visible_player");
	public static final MemoryModuleType<PlayerEntity> field_22354 = register("nearest_visible_targetable_player");
	public static final MemoryModuleType<WalkTarget> field_18445 = register("walk_target");
	public static final MemoryModuleType<LookTarget> field_18446 = register("look_target");
	public static final MemoryModuleType<LivingEntity> field_22355 = register("attack_target");
	public static final MemoryModuleType<Boolean> field_22475 = register("attack_cooling_down");
	public static final MemoryModuleType<LivingEntity> field_18447 = register("interaction_target");
	public static final MemoryModuleType<PassiveEntity> field_18448 = register("breed_target");
	public static final MemoryModuleType<Entity> field_22356 = register("ride_target");
	public static final MemoryModuleType<Path> field_18449 = register("path");
	public static final MemoryModuleType<List<GlobalPos>> field_18450 = register("interactable_doors");
	public static final MemoryModuleType<Set<GlobalPos>> field_26389 = register("doors_to_close");
	public static final MemoryModuleType<BlockPos> field_19007 = register("nearest_bed");
	public static final MemoryModuleType<DamageSource> field_18451 = register("hurt_by");
	public static final MemoryModuleType<LivingEntity> field_18452 = register("hurt_by_entity");
	public static final MemoryModuleType<LivingEntity> field_22357 = register("avoid_target");
	public static final MemoryModuleType<LivingEntity> field_18453 = register("nearest_hostile");
	public static final MemoryModuleType<GlobalPos> field_19008 = register("hiding_place");
	public static final MemoryModuleType<Long> field_19009 = register("heard_bell_time");
	public static final MemoryModuleType<Long> field_19293 = register("cant_reach_walk_target_since");
	public static final MemoryModuleType<Boolean> field_25754 = register("golem_detected_recently", Codec.BOOL);
	public static final MemoryModuleType<Long> field_19385 = register("last_slept", Codec.LONG);
	public static final MemoryModuleType<Long> field_20616 = register("last_woken", Codec.LONG);
	public static final MemoryModuleType<Long> field_19386 = register("last_worked_at_poi", Codec.LONG);
	public static final MemoryModuleType<PassiveEntity> field_25359 = register("nearest_visible_adult");
	public static final MemoryModuleType<ItemEntity> field_22332 = register("nearest_visible_wanted_item");
	public static final MemoryModuleType<MobEntity> field_25360 = register("nearest_visible_nemesis");
	public static final MemoryModuleType<UUID> field_22333 = register("angry_at", DynamicSerializableUuid.field_25122);
	public static final MemoryModuleType<Boolean> field_25361 = register("universal_anger", Codec.BOOL);
	public static final MemoryModuleType<Boolean> field_22334 = register("admiring_item", Codec.BOOL);
	public static final MemoryModuleType<Integer> field_25813 = register("time_trying_to_reach_admire_item");
	public static final MemoryModuleType<Boolean> field_25814 = register("disable_walk_to_admire_item");
	public static final MemoryModuleType<Boolean> field_22473 = register("admiring_disabled", Codec.BOOL);
	public static final MemoryModuleType<Boolean> field_22336 = register("hunted_recently", Codec.BOOL);
	public static final MemoryModuleType<BlockPos> field_22337 = register("celebrate_location");
	public static final MemoryModuleType<Boolean> field_25159 = register("dancing");
	public static final MemoryModuleType<HoglinEntity> field_22339 = register("nearest_visible_huntable_hoglin");
	public static final MemoryModuleType<HoglinEntity> field_22340 = register("nearest_visible_baby_hoglin");
	public static final MemoryModuleType<PlayerEntity> field_22342 = register("nearest_targetable_player_not_wearing_gold");
	public static final MemoryModuleType<List<AbstractPiglinEntity>> field_25755 = register("nearby_adult_piglins");
	public static final MemoryModuleType<List<AbstractPiglinEntity>> field_22343 = register("nearest_visible_adult_piglins");
	public static final MemoryModuleType<List<HoglinEntity>> field_22344 = register("nearest_visible_adult_hoglins");
	public static final MemoryModuleType<AbstractPiglinEntity> field_22345 = register("nearest_visible_adult_piglin");
	public static final MemoryModuleType<LivingEntity> field_22346 = register("nearest_visible_zombified");
	public static final MemoryModuleType<Integer> field_22347 = register("visible_adult_piglin_count");
	public static final MemoryModuleType<Integer> field_22348 = register("visible_adult_hoglin_count");
	public static final MemoryModuleType<PlayerEntity> field_22349 = register("nearest_player_holding_wanted_item");
	public static final MemoryModuleType<Boolean> field_22350 = register("ate_recently");
	public static final MemoryModuleType<BlockPos> field_22474 = register("nearest_repellent");
	public static final MemoryModuleType<Boolean> field_22353 = register("pacified");
	private final Optional<Codec<Memory<U>>> codec;

	private MemoryModuleType(Optional<Codec<U>> factory) {
		this.codec = factory.map(Memory::method_28353);
	}

	public String toString() {
		return Registry.MEMORY_MODULE_TYPE.getId(this).toString();
	}

	public Optional<Codec<Memory<U>>> getCodec() {
		return this.codec;
	}

	private static <U> MemoryModuleType<U> register(String id, Codec<U> codec) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(id), new MemoryModuleType<>(Optional.of(codec)));
	}

	private static <U> MemoryModuleType<U> register(String id) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(id), new MemoryModuleType<>(Optional.empty()));
	}
}
