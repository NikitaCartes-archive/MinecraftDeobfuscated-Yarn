/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import org.jetbrains.annotations.Nullable;

public class SpawnRestriction {
    private static final Map<EntityType<?>, Entry> mapping = Maps.newHashMap();

    private static <T extends MobEntity> void setRestrictions(EntityType<T> entityType, Location location, Heightmap.Type type, class_4306<T> arg) {
        Entry entry = mapping.put(entityType, new Entry(type, location, arg));
        if (entry != null) {
            throw new IllegalStateException("Duplicate registration for type " + Registry.ENTITY_TYPE.getId(entityType));
        }
    }

    public static Location getLocation(EntityType<?> entityType) {
        Entry entry = mapping.get(entityType);
        return entry == null ? Location.NO_RESTRICTIONS : entry.location;
    }

    public static Heightmap.Type getHeightMapType(@Nullable EntityType<?> entityType) {
        Entry entry = mapping.get(entityType);
        return entry == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : entry.heightMapType;
    }

    public static <T extends Entity> boolean method_20638(EntityType<T> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        Entry entry = mapping.get(entityType);
        return entry == null || entry.field_19349.test(entityType, iWorld, spawnType, blockPos, random);
    }

    static {
        SpawnRestriction.setRestrictions(EntityType.COD, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::canSpawn);
        SpawnRestriction.setRestrictions(EntityType.DOLPHIN, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DolphinEntity::method_20664);
        SpawnRestriction.setRestrictions(EntityType.DROWNED, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DrownedEntity::method_20673);
        SpawnRestriction.setRestrictions(EntityType.GUARDIAN, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GuardianEntity::method_20676);
        SpawnRestriction.setRestrictions(EntityType.PUFFERFISH, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::canSpawn);
        SpawnRestriction.setRestrictions(EntityType.SALMON, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::canSpawn);
        SpawnRestriction.setRestrictions(EntityType.SQUID, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SquidEntity::method_20670);
        SpawnRestriction.setRestrictions(EntityType.TROPICAL_FISH, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::canSpawn);
        SpawnRestriction.setRestrictions(EntityType.BAT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BatEntity::method_20661);
        SpawnRestriction.setRestrictions(EntityType.BLAZE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20681);
        SpawnRestriction.setRestrictions(EntityType.CAVE_SPIDER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.CHICKEN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.COW, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.CREEPER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.DONKEY, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.ENDERMAN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.ENDERMITE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndermiteEntity::method_20674);
        SpawnRestriction.setRestrictions(EntityType.ENDER_DRAGON, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
        SpawnRestriction.setRestrictions(EntityType.GHAST, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GhastEntity::method_20675);
        SpawnRestriction.setRestrictions(EntityType.GIANT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.HORSE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.HUSK, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HuskEntity::method_20677);
        SpawnRestriction.setRestrictions(EntityType.IRON_GOLEM, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
        SpawnRestriction.setRestrictions(EntityType.LLAMA, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.MAGMA_CUBE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MagmaCubeEntity::method_20678);
        SpawnRestriction.setRestrictions(EntityType.MOOSHROOM, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MooshroomEntity::method_20665);
        SpawnRestriction.setRestrictions(EntityType.MULE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.OCELOT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, OcelotEntity::method_20666);
        SpawnRestriction.setRestrictions(EntityType.PARROT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, ParrotEntity::method_20667);
        SpawnRestriction.setRestrictions(EntityType.PIG, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.PILLAGER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PatrolEntity::method_20739);
        SpawnRestriction.setRestrictions(EntityType.POLAR_BEAR, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PolarBearEntity::method_20668);
        SpawnRestriction.setRestrictions(EntityType.RABBIT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RabbitEntity::method_20669);
        SpawnRestriction.setRestrictions(EntityType.SHEEP, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.SILVERFISH, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SilverfishEntity::method_20684);
        SpawnRestriction.setRestrictions(EntityType.SKELETON, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.SKELETON_HORSE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.SLIME, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SlimeEntity::method_20685);
        SpawnRestriction.setRestrictions(EntityType.SNOW_GOLEM, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
        SpawnRestriction.setRestrictions(EntityType.SPIDER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.STRAY, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, StrayEntity::method_20686);
        SpawnRestriction.setRestrictions(EntityType.TURTLE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TurtleEntity::method_20671);
        SpawnRestriction.setRestrictions(EntityType.VILLAGER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
        SpawnRestriction.setRestrictions(EntityType.WITCH, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.WITHER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.WITHER_SKELETON, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.WOLF, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.ZOMBIE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.ZOMBIE_HORSE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.ZOMBIE_PIGMAN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ZombiePigmanEntity::method_20682);
        SpawnRestriction.setRestrictions(EntityType.ZOMBIE_VILLAGER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.CAT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.ELDER_GUARDIAN, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GuardianEntity::method_20676);
        SpawnRestriction.setRestrictions(EntityType.EVOKER, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.FOX, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.ILLUSIONER, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.PANDA, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.PHANTOM, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
        SpawnRestriction.setRestrictions(EntityType.RAVAGER, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.SHULKER, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
        SpawnRestriction.setRestrictions(EntityType.TRADER_LLAMA, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.setRestrictions(EntityType.VEX, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.VINDICATOR, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
        SpawnRestriction.setRestrictions(EntityType.WANDERING_TRADER, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
    }

    public static enum Location {
        ON_GROUND,
        IN_WATER,
        NO_RESTRICTIONS;

    }

    static class Entry {
        private final Heightmap.Type heightMapType;
        private final Location location;
        private final class_4306<?> field_19349;

        public Entry(Heightmap.Type type, Location location, class_4306<?> arg) {
            this.heightMapType = type;
            this.location = location;
            this.field_19349 = arg;
        }
    }

    @FunctionalInterface
    public static interface class_4306<T extends Entity> {
        public boolean test(EntityType<T> var1, IWorld var2, SpawnType var3, BlockPos var4, Random var5);
    }
}

