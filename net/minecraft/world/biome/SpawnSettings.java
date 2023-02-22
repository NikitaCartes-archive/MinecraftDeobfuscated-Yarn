/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weight;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SpawnSettings {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float field_30983 = 0.1f;
    public static final Pool<SpawnEntry> EMPTY_ENTRY_POOL = Pool.empty();
    public static final SpawnSettings INSTANCE = new Builder().build();
    public static final MapCodec<SpawnSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.floatRange(0.0f, 0.9999999f).optionalFieldOf("creature_spawn_probability", Float.valueOf(0.1f)).forGetter(spawnSettings -> Float.valueOf(spawnSettings.creatureSpawnProbability)), Codec.simpleMap(SpawnGroup.CODEC, Pool.createCodec(SpawnEntry.CODEC).promotePartial((Consumer)Util.addPrefix("Spawn data: ", LOGGER::error)), StringIdentifiable.toKeyable(SpawnGroup.values())).fieldOf("spawners").forGetter(spawnSettings -> spawnSettings.spawners), Codec.simpleMap(Registries.ENTITY_TYPE.getCodec(), SpawnDensity.CODEC, Registries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(spawnSettings -> spawnSettings.spawnCosts)).apply((Applicative<SpawnSettings, ?>)instance, SpawnSettings::new));
    private final float creatureSpawnProbability;
    private final Map<SpawnGroup, Pool<SpawnEntry>> spawners;
    private final Map<EntityType<?>, SpawnDensity> spawnCosts;

    SpawnSettings(float creatureSpawnProbability, Map<SpawnGroup, Pool<SpawnEntry>> spawners, Map<EntityType<?>, SpawnDensity> spawnCosts) {
        this.creatureSpawnProbability = creatureSpawnProbability;
        this.spawners = ImmutableMap.copyOf(spawners);
        this.spawnCosts = ImmutableMap.copyOf(spawnCosts);
    }

    public Pool<SpawnEntry> getSpawnEntries(SpawnGroup spawnGroup) {
        return this.spawners.getOrDefault(spawnGroup, EMPTY_ENTRY_POOL);
    }

    @Nullable
    public SpawnDensity getSpawnDensity(EntityType<?> entityType) {
        return this.spawnCosts.get(entityType);
    }

    public float getCreatureSpawnProbability() {
        return this.creatureSpawnProbability;
    }

    public record SpawnDensity(double gravityLimit, double mass) {
        public static final Codec<SpawnDensity> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.DOUBLE.fieldOf("energy_budget")).forGetter(spawnDensity -> spawnDensity.gravityLimit), ((MapCodec)Codec.DOUBLE.fieldOf("charge")).forGetter(spawnDensity -> spawnDensity.mass)).apply((Applicative<SpawnDensity, ?>)instance, SpawnDensity::new));
    }

    public static class SpawnEntry
    extends Weighted.Absent {
        public static final Codec<SpawnEntry> CODEC = Codecs.validate(RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registries.ENTITY_TYPE.getCodec().fieldOf("type")).forGetter(spawnEntry -> spawnEntry.type), ((MapCodec)Weight.CODEC.fieldOf("weight")).forGetter(Weighted.Absent::getWeight), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("minCount")).forGetter(spawnEntry -> spawnEntry.minGroupSize), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("maxCount")).forGetter(spawnEntry -> spawnEntry.maxGroupSize)).apply((Applicative<SpawnEntry, ?>)instance, SpawnEntry::new)), spawnEntry -> {
            if (spawnEntry.minGroupSize > spawnEntry.maxGroupSize) {
                return DataResult.error(() -> "minCount needs to be smaller or equal to maxCount");
            }
            return DataResult.success(spawnEntry);
        });
        public final EntityType<?> type;
        public final int minGroupSize;
        public final int maxGroupSize;

        public SpawnEntry(EntityType<?> type, int weight, int minGroupSize, int maxGroupSize) {
            this(type, Weight.of(weight), minGroupSize, maxGroupSize);
        }

        public SpawnEntry(EntityType<?> type, Weight weight, int minGroupSize, int maxGroupSize) {
            super(weight);
            this.type = type.getSpawnGroup() == SpawnGroup.MISC ? EntityType.PIG : type;
            this.minGroupSize = minGroupSize;
            this.maxGroupSize = maxGroupSize;
        }

        public String toString() {
            return EntityType.getId(this.type) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.getWeight();
        }
    }

    public static class Builder {
        private final Map<SpawnGroup, List<SpawnEntry>> spawners = Stream.of(SpawnGroup.values()).collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> Lists.newArrayList()));
        private final Map<EntityType<?>, SpawnDensity> spawnCosts = Maps.newLinkedHashMap();
        private float creatureSpawnProbability = 0.1f;

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

        public SpawnSettings build() {
            return new SpawnSettings(this.creatureSpawnProbability, (Map<SpawnGroup, Pool<SpawnEntry>>)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> Pool.of((List)entry.getValue()))), ImmutableMap.copyOf(this.spawnCosts));
        }
    }
}

