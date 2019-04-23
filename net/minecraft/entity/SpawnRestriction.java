/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.EntityType;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;

public class SpawnRestriction {
    private static final Map<EntityType<?>, Entry> mapping = Maps.newHashMap();

    private static void register(EntityType<?> entityType, Location location, Heightmap.Type type) {
        mapping.put(entityType, new Entry(type, location));
    }

    @Nullable
    public static Location getLocation(EntityType<?> entityType) {
        Entry entry = mapping.get(entityType);
        return entry == null ? null : entry.location;
    }

    public static Heightmap.Type getHeightMapType(@Nullable EntityType<?> entityType) {
        Entry entry = mapping.get(entityType);
        return entry == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : entry.heightMapType;
    }

    static {
        SpawnRestriction.register(EntityType.COD, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.DOLPHIN, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.DROWNED, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.GUARDIAN, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.PUFFERFISH, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.SALMON, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.SQUID, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.TROPICAL_FISH, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.BAT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.BLAZE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.CAVE_SPIDER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.CHICKEN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.COW, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.CREEPER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.DONKEY, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.ENDERMAN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.ENDERMITE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.ENDER_DRAGON, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.GHAST, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.GIANT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.HORSE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.HUSK, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.IRON_GOLEM, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.LLAMA, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.MAGMA_CUBE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.MOOSHROOM, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.MULE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.OCELOT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING);
        SpawnRestriction.register(EntityType.PARROT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING);
        SpawnRestriction.register(EntityType.PIG, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.PILLAGER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.POLAR_BEAR, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.RABBIT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.SHEEP, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.SILVERFISH, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.SKELETON, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.SKELETON_HORSE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.SLIME, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.SNOW_GOLEM, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.SPIDER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.STRAY, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.TURTLE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.VILLAGER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.WITCH, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.WITHER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.WITHER_SKELETON, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.WOLF, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.ZOMBIE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.ZOMBIE_HORSE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.ZOMBIE_PIGMAN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        SpawnRestriction.register(EntityType.ZOMBIE_VILLAGER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
    }

    public static enum Location {
        ON_GROUND,
        IN_WATER;

    }

    static class Entry {
        private final Heightmap.Type heightMapType;
        private final Location location;

        public Entry(Heightmap.Type type, Location location) {
            this.heightMapType = type;
            this.location = location;
        }
    }
}

