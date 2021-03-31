/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weight;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SpawnSettings {
    public static final Logger LOGGER = LogManager.getLogger();
    private static final float field_30983 = 0.1f;
    public static final Pool<SpawnEntry> field_30982 = Pool.empty();
    public static final SpawnSettings INSTANCE = new SpawnSettings(0.1f, (Map<SpawnGroup, Pool<SpawnEntry>>)Stream.of(SpawnGroup.values()).collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> field_30982)), ImmutableMap.of(), false);
    public static final MapCodec<SpawnSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.floatRange(0.0f, 0.9999999f).optionalFieldOf("creature_spawn_probability", Float.valueOf(0.1f)).forGetter(spawnSettings -> Float.valueOf(spawnSettings.creatureSpawnProbability)), Codec.simpleMap(SpawnGroup.CODEC, Pool.createCodec(SpawnEntry.CODEC).promotePartial((Consumer)Util.addPrefix("Spawn data: ", LOGGER::error)), StringIdentifiable.toKeyable(SpawnGroup.values())).fieldOf("spawners").forGetter(spawnSettings -> spawnSettings.spawners), Codec.simpleMap(Registry.ENTITY_TYPE, SpawnDensity.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(spawnSettings -> spawnSettings.spawnCosts), ((MapCodec)Codec.BOOL.fieldOf("player_spawn_friendly")).orElse(false).forGetter(SpawnSettings::isPlayerSpawnFriendly)).apply((Applicative<SpawnSettings, ?>)instance, SpawnSettings::new));
    private final float creatureSpawnProbability;
    private final Map<SpawnGroup, Pool<SpawnEntry>> spawners;
    private final Map<EntityType<?>, SpawnDensity> spawnCosts;
    private final boolean playerSpawnFriendly;

    private SpawnSettings(float creatureSpawnProbability, Map<SpawnGroup, Pool<SpawnEntry>> spawners, Map<EntityType<?>, SpawnDensity> spawnCosts, boolean playerSpawnFriendly) {
        this.creatureSpawnProbability = creatureSpawnProbability;
        this.spawners = ImmutableMap.copyOf(spawners);
        this.spawnCosts = ImmutableMap.copyOf(spawnCosts);
        this.playerSpawnFriendly = playerSpawnFriendly;
    }

    public Pool<SpawnEntry> getSpawnEntries(SpawnGroup spawnGroup) {
        return this.spawners.getOrDefault(spawnGroup, field_30982);
    }

    @Nullable
    public SpawnDensity getSpawnDensity(EntityType<?> entityType) {
        return this.spawnCosts.get(entityType);
    }

    public float getCreatureSpawnProbability() {
        return this.creatureSpawnProbability;
    }

    public boolean isPlayerSpawnFriendly() {
        return this.playerSpawnFriendly;
    }

    public static class Builder {
        private final Map<SpawnGroup, List<SpawnEntry>> spawners = Stream.of(SpawnGroup.values()).collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> Lists.newArrayList()));
        private final Map<EntityType<?>, SpawnDensity> spawnCosts = Maps.newLinkedHashMap();
        private float creatureSpawnProbability = 0.1f;
        private boolean playerSpawnFriendly;

        public Builder spawn(SpawnGroup spawnGroup, SpawnEntry spawnEntry) {
            this.spawners.get(spawnGroup).add(spawnEntry);
            return this;
        }

        public Builder spawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
            this.spawnCosts.put(entityType, new SpawnDensity(gravityLimit, mass));
            return this;
        }

        public Builder creatureSpawnProbability(float probability) {
            this.creatureSpawnProbability = probability;
            return this;
        }

        public Builder playerSpawnFriendly() {
            this.playerSpawnFriendly = true;
            return this;
        }

        public SpawnSettings build() {
            return new SpawnSettings(this.creatureSpawnProbability, this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> Pool.of((List)entry.getValue()))), ImmutableMap.copyOf(this.spawnCosts), this.playerSpawnFriendly);
        }
    }

    public static class SpawnDensity {
        public static final Codec<SpawnDensity> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.DOUBLE.fieldOf("energy_budget")).forGetter(spawnDensity -> spawnDensity.gravityLimit), ((MapCodec)Codec.DOUBLE.fieldOf("charge")).forGetter(spawnDensity -> spawnDensity.mass)).apply((Applicative<SpawnDensity, ?>)instance, SpawnDensity::new));
        private final double gravityLimit;
        private final double mass;

        private SpawnDensity(double gravityLimit, double mass) {
            this.gravityLimit = gravityLimit;
            this.mass = mass;
        }

        public double getGravityLimit() {
            return this.gravityLimit;
        }

        public double getMass() {
            return this.mass;
        }
    }

    public static class SpawnEntry
    extends Weighted.Absent {
        public static final Codec<SpawnEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registry.ENTITY_TYPE.fieldOf("type")).forGetter(spawnEntry -> spawnEntry.type), ((MapCodec)Weight.CODEC.fieldOf("weight")).forGetter(Weighted.Absent::getWeight), ((MapCodec)Codec.INT.fieldOf("minCount")).forGetter(spawnEntry -> spawnEntry.minGroupSize), ((MapCodec)Codec.INT.fieldOf("maxCount")).forGetter(spawnEntry -> spawnEntry.maxGroupSize)).apply((Applicative<SpawnEntry, ?>)instance, SpawnEntry::new));
        public final EntityType<?> type;
        public final int minGroupSize;
        public final int maxGroupSize;

        public SpawnEntry(EntityType<?> type, int weight, int minGroupSize, int maxGroupSize) {
            this(type, Weight.of(weight), minGroupSize, maxGroupSize);
        }

        public SpawnEntry(EntityType<?> entityType, Weight weight, int i, int j) {
            super(weight);
            this.type = entityType.getSpawnGroup() == SpawnGroup.MISC ? EntityType.PIG : entityType;
            this.minGroupSize = i;
            this.maxGroupSize = j;
        }

        public String toString() {
            return EntityType.getId(this.type) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.getWeight();
        }
    }
}

