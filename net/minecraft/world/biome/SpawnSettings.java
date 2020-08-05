/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SpawnSettings {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final SpawnSettings INSTANCE = new SpawnSettings(0.1f, (Map<SpawnGroup, List<SpawnEntry>>)Stream.of(SpawnGroup.values()).collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> ImmutableList.of())), ImmutableMap.of(), false);
    public static final MapCodec<SpawnSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.FLOAT.optionalFieldOf("creature_spawn_probability", Float.valueOf(0.1f)).forGetter(spawnSettings -> Float.valueOf(spawnSettings.creatureSpawnProbability)), Codec.simpleMap(SpawnGroup.field_24655, SpawnEntry.CODEC.listOf().promotePartial((Consumer)Util.method_29188("Spawn data: ", LOGGER::error)), StringIdentifiable.method_28142(SpawnGroup.values())).fieldOf("spawners").forGetter(spawnSettings -> spawnSettings.spawners), Codec.simpleMap(Registry.ENTITY_TYPE, SpawnDensity.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(spawnSettings -> spawnSettings.spawnCosts), ((MapCodec)Codec.BOOL.fieldOf("player_spawn_friendly")).orElse(false).forGetter(SpawnSettings::method_31082)).apply((Applicative<SpawnSettings, ?>)instance, SpawnSettings::new));
    private final float creatureSpawnProbability;
    private final Map<SpawnGroup, List<SpawnEntry>> spawners;
    private final Map<EntityType<?>, SpawnDensity> spawnCosts;
    private final boolean field_26692;

    private SpawnSettings(float creatureSpawnProbability, Map<SpawnGroup, List<SpawnEntry>> spawners, Map<EntityType<?>, SpawnDensity> spawnCosts, boolean bl) {
        this.creatureSpawnProbability = creatureSpawnProbability;
        this.spawners = spawners;
        this.spawnCosts = spawnCosts;
        this.field_26692 = bl;
    }

    public List<SpawnEntry> getSpawnEntry(SpawnGroup spawnGroup) {
        return this.spawners.get(spawnGroup);
    }

    @Nullable
    public SpawnDensity getSpawnDensity(EntityType<?> entityType) {
        return this.spawnCosts.get(entityType);
    }

    public float getCreatureSpawnProbability() {
        return this.creatureSpawnProbability;
    }

    public boolean method_31082() {
        return this.field_26692;
    }

    public static class Builder {
        private final Map<SpawnGroup, List<SpawnEntry>> spawners = Stream.of(SpawnGroup.values()).collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> Lists.newArrayList()));
        private final Map<EntityType<?>, SpawnDensity> spawnCosts = Maps.newLinkedHashMap();
        private float creatureSpawnProbability = 0.1f;
        private boolean field_26693;

        public Builder spawners(SpawnGroup spawnGroup, SpawnEntry spawnEntry) {
            this.spawners.get(spawnGroup).add(spawnEntry);
            return this;
        }

        public Builder spawnCosts(EntityType<?> entityType, double mass, double gravityLimit) {
            this.spawnCosts.put(entityType, new SpawnDensity(gravityLimit, mass));
            return this;
        }

        public Builder creatureSpawnProbability(float probability) {
            this.creatureSpawnProbability = probability;
            return this;
        }

        public Builder method_31083() {
            this.field_26693 = true;
            return this;
        }

        public SpawnSettings build() {
            return new SpawnSettings(this.creatureSpawnProbability, this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ImmutableList.copyOf((Collection)entry.getValue()))), ImmutableMap.copyOf(this.spawnCosts), this.field_26693);
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
    extends WeightedPicker.Entry {
        public static final Codec<SpawnEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registry.ENTITY_TYPE.fieldOf("type")).forGetter(spawnEntry -> spawnEntry.type), ((MapCodec)Codec.INT.fieldOf("weight")).forGetter(spawnEntry -> spawnEntry.weight), ((MapCodec)Codec.INT.fieldOf("minCount")).forGetter(spawnEntry -> spawnEntry.minGroupSize), ((MapCodec)Codec.INT.fieldOf("maxCount")).forGetter(spawnEntry -> spawnEntry.maxGroupSize)).apply((Applicative<SpawnEntry, ?>)instance, SpawnEntry::new));
        public final EntityType<?> type;
        public final int minGroupSize;
        public final int maxGroupSize;

        public SpawnEntry(EntityType<?> type, int weight, int minGroupSize, int maxGroupSize) {
            super(weight);
            this.type = type.getSpawnGroup() == SpawnGroup.MISC ? EntityType.PIG : type;
            this.minGroupSize = minGroupSize;
            this.maxGroupSize = maxGroupSize;
        }

        public String toString() {
            return EntityType.getId(this.type) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.weight;
        }
    }
}

