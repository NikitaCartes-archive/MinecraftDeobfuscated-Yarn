/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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
import net.minecraft.util.Timestamp;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class MemoryModuleType<U> {
    public static final MemoryModuleType<Void> DUMMY = MemoryModuleType.register("dummy");
    public static final MemoryModuleType<GlobalPos> HOME = MemoryModuleType.register("home", Optional.of(GlobalPos::deserialize));
    public static final MemoryModuleType<GlobalPos> JOB_SITE = MemoryModuleType.register("job_site", Optional.of(GlobalPos::deserialize));
    public static final MemoryModuleType<GlobalPos> MEETING_POINT = MemoryModuleType.register("meeting_point", Optional.of(GlobalPos::deserialize));
    public static final MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE = MemoryModuleType.register("secondary_job_site");
    public static final MemoryModuleType<List<LivingEntity>> MOBS = MemoryModuleType.register("mobs");
    public static final MemoryModuleType<List<LivingEntity>> VISIBLE_MOBS = MemoryModuleType.register("visible_mobs");
    public static final MemoryModuleType<List<LivingEntity>> VISIBLE_VILLAGER_BABIES = MemoryModuleType.register("visible_villager_babies");
    public static final MemoryModuleType<List<PlayerEntity>> NEAREST_PLAYERS = MemoryModuleType.register("nearest_players");
    public static final MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_PLAYER = MemoryModuleType.register("nearest_visible_player");
    public static final MemoryModuleType<WalkTarget> WALK_TARGET = MemoryModuleType.register("walk_target");
    public static final MemoryModuleType<LookTarget> LOOK_TARGET = MemoryModuleType.register("look_target");
    public static final MemoryModuleType<LivingEntity> INTERACTION_TARGET = MemoryModuleType.register("interaction_target");
    public static final MemoryModuleType<VillagerEntity> BREED_TARGET = MemoryModuleType.register("breed_target");
    public static final MemoryModuleType<Path> PATH = MemoryModuleType.register("path");
    public static final MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS = MemoryModuleType.register("interactable_doors");
    public static final MemoryModuleType<Set<GlobalPos>> OPENED_DOORS = MemoryModuleType.register("opened_doors");
    public static final MemoryModuleType<BlockPos> NEAREST_BED = MemoryModuleType.register("nearest_bed");
    public static final MemoryModuleType<DamageSource> HURT_BY = MemoryModuleType.register("hurt_by");
    public static final MemoryModuleType<LivingEntity> HURT_BY_ENTITY = MemoryModuleType.register("hurt_by_entity");
    public static final MemoryModuleType<LivingEntity> NEAREST_HOSTILE = MemoryModuleType.register("nearest_hostile");
    public static final MemoryModuleType<GlobalPos> HIDING_PLACE = MemoryModuleType.register("hiding_place");
    public static final MemoryModuleType<Long> HEARD_BELL_TIME = MemoryModuleType.register("heard_bell_time");
    public static final MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE = MemoryModuleType.register("cant_reach_walk_target_since");
    public static final MemoryModuleType<Long> GOLEM_LAST_SEEN_TIME = MemoryModuleType.register("golem_last_seen_time");
    public static final MemoryModuleType<Timestamp> LAST_SLEPT = MemoryModuleType.register("last_slept", Optional.of(Timestamp::of));
    public static final MemoryModuleType<Timestamp> LAST_WOKEN = MemoryModuleType.register("last_woken", Optional.of(Timestamp::of));
    public static final MemoryModuleType<Timestamp> LAST_WORKED_AT_POI = MemoryModuleType.register("last_worked_at_poi", Optional.of(Timestamp::of));
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
        return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(id), new MemoryModuleType<U>(factory));
    }

    private static <U> MemoryModuleType<U> register(String id) {
        return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(id), new MemoryModuleType<U>(Optional.empty()));
    }
}

