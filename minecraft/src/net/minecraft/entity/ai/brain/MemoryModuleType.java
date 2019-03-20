package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MemoryModuleType<U> {
	public static final MemoryModuleType<Void> field_18437 = register("dummy", Optional.empty());
	public static final MemoryModuleType<GlobalPos> field_18438 = register("home", Optional.of(GlobalPos::method_19444));
	public static final MemoryModuleType<GlobalPos> field_18439 = register("job_site", Optional.of(GlobalPos::method_19444));
	public static final MemoryModuleType<GlobalPos> field_18440 = register("meeting_point", Optional.of(GlobalPos::method_19444));
	public static final MemoryModuleType<List<GlobalPos>> field_18873 = register("secondary_job_site", Optional.empty());
	public static final MemoryModuleType<List<LivingEntity>> field_18441 = register("mobs", Optional.empty());
	public static final MemoryModuleType<List<LivingEntity>> field_18442 = register("visible_mobs", Optional.empty());
	public static final MemoryModuleType<List<PlayerEntity>> field_18443 = register("nearest_players", Optional.empty());
	public static final MemoryModuleType<PlayerEntity> field_18444 = register("nearest_visible_player", Optional.empty());
	public static final MemoryModuleType<WalkTarget> field_18445 = register("walk_target", Optional.empty());
	public static final MemoryModuleType<LookTarget> field_18446 = register("look_target", Optional.empty());
	public static final MemoryModuleType<LivingEntity> field_18447 = register("interaction_target", Optional.empty());
	public static final MemoryModuleType<VillagerEntity> field_18448 = register("breed_target", Optional.empty());
	public static final MemoryModuleType<Path> field_18449 = register("path", Optional.empty());
	public static final MemoryModuleType<List<GlobalPos>> field_18450 = register("interactable_doors", Optional.empty());
	public static final MemoryModuleType<DamageSource> field_18451 = register("hurt_by", Optional.empty());
	public static final MemoryModuleType<LivingEntity> field_18452 = register("hurt_by_entity", Optional.empty());
	public static final MemoryModuleType<LivingEntity> field_18453 = register("nearest_hostile", Optional.empty());
	public static final MemoryModuleType<VillagerEntity.GolemSpawnCondition> field_18874 = register("golem_spawn_conditions", Optional.empty());
	private final Optional<Function<Dynamic<?>, U>> factory;

	private MemoryModuleType(Optional<Function<Dynamic<?>, U>> optional) {
		this.factory = optional;
	}

	public Identifier getId() {
		return Registry.MEMORY_MODULE_TYPE.getId(this);
	}

	public String toString() {
		return this.getId().toString();
	}

	public Optional<Function<Dynamic<?>, U>> getFactory() {
		return this.factory;
	}

	private static <U> MemoryModuleType<U> register(String string, Optional<Function<Dynamic<?>, U>> optional) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(string), new MemoryModuleType<>(optional));
	}
}
