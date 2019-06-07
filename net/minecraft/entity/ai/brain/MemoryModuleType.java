/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.class_4316;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class MemoryModuleType<U> {
    public static final MemoryModuleType<Void> DUMMY = MemoryModuleType.method_20738("dummy");
    public static final MemoryModuleType<GlobalPos> HOME = MemoryModuleType.register("home", Optional.of(GlobalPos::deserialize));
    public static final MemoryModuleType<GlobalPos> JOB_SITE = MemoryModuleType.register("job_site", Optional.of(GlobalPos::deserialize));
    public static final MemoryModuleType<GlobalPos> MEETING_POINT = MemoryModuleType.register("meeting_point", Optional.of(GlobalPos::deserialize));
    public static final MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE = MemoryModuleType.method_20738("secondary_job_site");
    public static final MemoryModuleType<List<LivingEntity>> MOBS = MemoryModuleType.method_20738("mobs");
    public static final MemoryModuleType<List<LivingEntity>> VISIBLE_MOBS = MemoryModuleType.method_20738("visible_mobs");
    public static final MemoryModuleType<List<LivingEntity>> VISIBLE_VILLAGER_BABIES = MemoryModuleType.method_20738("visible_villager_babies");
    public static final MemoryModuleType<List<PlayerEntity>> NEAREST_PLAYERS = MemoryModuleType.method_20738("nearest_players");
    public static final MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_PLAYER = MemoryModuleType.method_20738("nearest_visible_player");
    public static final MemoryModuleType<WalkTarget> WALK_TARGET = MemoryModuleType.method_20738("walk_target");
    public static final MemoryModuleType<LookTarget> LOOK_TARGET = MemoryModuleType.method_20738("look_target");
    public static final MemoryModuleType<LivingEntity> INTERACTION_TARGET = MemoryModuleType.method_20738("interaction_target");
    public static final MemoryModuleType<VillagerEntity> BREED_TARGET = MemoryModuleType.method_20738("breed_target");
    public static final MemoryModuleType<Path> PATH = MemoryModuleType.method_20738("path");
    public static final MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS = MemoryModuleType.method_20738("interactable_doors");
    public static final MemoryModuleType<BlockPos> NEAREST_BED = MemoryModuleType.method_20738("nearest_bed");
    public static final MemoryModuleType<DamageSource> HURT_BY = MemoryModuleType.method_20738("hurt_by");
    public static final MemoryModuleType<LivingEntity> HURT_BY_ENTITY = MemoryModuleType.method_20738("hurt_by_entity");
    public static final MemoryModuleType<LivingEntity> NEAREST_HOSTILE = MemoryModuleType.method_20738("nearest_hostile");
    public static final MemoryModuleType<GlobalPos> HIDING_PLACE = MemoryModuleType.method_20738("hiding_place");
    public static final MemoryModuleType<Long> HEARD_BELL_TIME = MemoryModuleType.method_20738("heard_bell_time");
    public static final MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE = MemoryModuleType.method_20738("cant_reach_walk_target_since");
    public static final MemoryModuleType<Long> GOLEM_LAST_SEEN_TIME = MemoryModuleType.method_20738("golem_last_seen_time");
    public static final MemoryModuleType<class_4316> LAST_SLEPT = MemoryModuleType.register("last_slept", Optional.of(class_4316::method_20792));
    public static final MemoryModuleType<class_4316> LAST_WORKED_AT_POI = MemoryModuleType.register("last_worked_at_poi", Optional.of(class_4316::method_20792));
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
        return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(string), new MemoryModuleType<U>(optional));
    }

    private static <U> MemoryModuleType<U> method_20738(String string) {
        return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(string), new MemoryModuleType<U>(Optional.empty()));
    }
}

