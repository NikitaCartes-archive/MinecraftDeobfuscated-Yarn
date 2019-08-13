package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Identifier;
import net.minecraft.util.Timestamp;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class MemoryModuleType<U> {
	public static final MemoryModuleType<Void> field_18437 = method_20738("dummy");
	public static final MemoryModuleType<GlobalPos> field_18438 = register("home", Optional.of(GlobalPos::deserialize));
	public static final MemoryModuleType<GlobalPos> field_18439 = register("job_site", Optional.of(GlobalPos::deserialize));
	public static final MemoryModuleType<GlobalPos> field_18440 = register("meeting_point", Optional.of(GlobalPos::deserialize));
	public static final MemoryModuleType<List<GlobalPos>> field_18873 = method_20738("secondary_job_site");
	public static final MemoryModuleType<List<LivingEntity>> field_18441 = method_20738("mobs");
	public static final MemoryModuleType<List<LivingEntity>> field_18442 = method_20738("visible_mobs");
	public static final MemoryModuleType<List<LivingEntity>> field_19006 = method_20738("visible_villager_babies");
	public static final MemoryModuleType<List<PlayerEntity>> field_18443 = method_20738("nearest_players");
	public static final MemoryModuleType<PlayerEntity> field_18444 = method_20738("nearest_visible_player");
	public static final MemoryModuleType<WalkTarget> field_18445 = method_20738("walk_target");
	public static final MemoryModuleType<LookTarget> field_18446 = method_20738("look_target");
	public static final MemoryModuleType<LivingEntity> field_18447 = method_20738("interaction_target");
	public static final MemoryModuleType<VillagerEntity> field_18448 = method_20738("breed_target");
	public static final MemoryModuleType<Path> field_18449 = method_20738("path");
	public static final MemoryModuleType<List<GlobalPos>> field_18450 = method_20738("interactable_doors");
	public static final MemoryModuleType<Set<GlobalPos>> field_20312 = method_20738("opened_doors");
	public static final MemoryModuleType<BlockPos> field_19007 = method_20738("nearest_bed");
	public static final MemoryModuleType<DamageSource> field_18451 = method_20738("hurt_by");
	public static final MemoryModuleType<LivingEntity> field_18452 = method_20738("hurt_by_entity");
	public static final MemoryModuleType<LivingEntity> field_18453 = method_20738("nearest_hostile");
	public static final MemoryModuleType<GlobalPos> field_19008 = method_20738("hiding_place");
	public static final MemoryModuleType<Long> field_19009 = method_20738("heard_bell_time");
	public static final MemoryModuleType<Long> field_19293 = method_20738("cant_reach_walk_target_since");
	public static final MemoryModuleType<Long> field_19355 = method_20738("golem_last_seen_time");
	public static final MemoryModuleType<Timestamp> field_19385 = register("last_slept", Optional.of(Timestamp::of));
	public static final MemoryModuleType<Timestamp> field_19386 = register("last_worked_at_poi", Optional.of(Timestamp::of));
	private final Optional<Function<Dynamic<?>, U>> factory;

	private MemoryModuleType(Optional<Function<Dynamic<?>, U>> optional) {
		this.factory = optional;
	}

	public String toString() {
		return Registry.MEMORY_MODULE_TYPE.getId(this).toString();
	}

	public Optional<Function<Dynamic<?>, U>> getFactory() {
		return this.factory;
	}

	private static <U extends DynamicSerializable> MemoryModuleType<U> register(String string, Optional<Function<Dynamic<?>, U>> optional) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(string), new MemoryModuleType<>(optional));
	}

	private static <U> MemoryModuleType<U> method_20738(String string) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(string), new MemoryModuleType<>(Optional.empty()));
	}
}
