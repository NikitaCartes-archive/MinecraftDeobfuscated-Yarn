/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class MemoryModuleType<U> {
    public static final MemoryModuleType<Void> DUMMY = MemoryModuleType.register("dummy", Optional.empty());
    public static final MemoryModuleType<GlobalPos> HOME = MemoryModuleType.register("home", Optional.of(GlobalPos::deserialize));
    public static final MemoryModuleType<GlobalPos> JOB_SITE = MemoryModuleType.register("job_site", Optional.of(GlobalPos::deserialize));
    public static final MemoryModuleType<GlobalPos> MEETING_POINT = MemoryModuleType.register("meeting_point", Optional.of(GlobalPos::deserialize));
    public static final MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE = MemoryModuleType.register("secondary_job_site", Optional.empty());
    public static final MemoryModuleType<List<LivingEntity>> MOBS = MemoryModuleType.register("mobs", Optional.empty());
    public static final MemoryModuleType<List<LivingEntity>> VISIBLE_MOBS = MemoryModuleType.register("visible_mobs", Optional.empty());
    public static final MemoryModuleType<List<LivingEntity>> VISIBLE_VILLAGER_BABIES = MemoryModuleType.register("visible_villager_babies", Optional.empty());
    public static final MemoryModuleType<List<PlayerEntity>> NEAREST_PLAYERS = MemoryModuleType.register("nearest_players", Optional.empty());
    public static final MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_PLAYER = MemoryModuleType.register("nearest_visible_player", Optional.empty());
    public static final MemoryModuleType<WalkTarget> WALK_TARGET = MemoryModuleType.register("walk_target", Optional.empty());
    public static final MemoryModuleType<LookTarget> LOOK_TARGET = MemoryModuleType.register("look_target", Optional.empty());
    public static final MemoryModuleType<LivingEntity> INTERACTION_TARGET = MemoryModuleType.register("interaction_target", Optional.empty());
    public static final MemoryModuleType<VillagerEntity> BREED_TARGET = MemoryModuleType.register("breed_target", Optional.empty());
    public static final MemoryModuleType<Path> PATH = MemoryModuleType.register("path", Optional.empty());
    public static final MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS = MemoryModuleType.register("interactable_doors", Optional.empty());
    public static final MemoryModuleType<BlockPos> NEAREST_BED = MemoryModuleType.register("nearest_bed", Optional.empty());
    public static final MemoryModuleType<DamageSource> HURT_BY = MemoryModuleType.register("hurt_by", Optional.empty());
    public static final MemoryModuleType<LivingEntity> HURT_BY_ENTITY = MemoryModuleType.register("hurt_by_entity", Optional.empty());
    public static final MemoryModuleType<LivingEntity> NEAREST_HOSTILE = MemoryModuleType.register("nearest_hostile", Optional.empty());
    public static final MemoryModuleType<VillagerEntity.GolemSpawnCondition> GOLEM_SPAWN_CONDITIONS = MemoryModuleType.register("golem_spawn_conditions", Optional.empty());
    public static final MemoryModuleType<GlobalPos> HIDING_PLACE = MemoryModuleType.register("hiding_place", Optional.empty());
    public static final MemoryModuleType<Long> HEARD_BELL_TIME = MemoryModuleType.register("heard_bell_time", Optional.empty());
    public static final MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE = MemoryModuleType.register("cant_reach_walk_target_since", Optional.empty());
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

    private static <U> MemoryModuleType<U> register(String string, Optional<Function<Dynamic<?>, U>> optional) {
        return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(string), new MemoryModuleType<U>(optional));
    }
}

