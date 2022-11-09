package net.minecraft.world.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weight;
import net.minecraft.util.collection.Weighted;
import org.slf4j.Logger;

public class SpawnSettings {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final float field_30983 = 0.1F;
	public static final Pool<SpawnSettings.SpawnEntry> EMPTY_ENTRY_POOL = Pool.empty();
	public static final SpawnSettings INSTANCE = new SpawnSettings.Builder().build();
	public static final MapCodec<SpawnSettings> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.floatRange(0.0F, 0.9999999F).optionalFieldOf("creature_spawn_probability", 0.1F).forGetter(spawnSettings -> spawnSettings.creatureSpawnProbability),
					Codec.simpleMap(
							SpawnGroup.CODEC,
							Pool.createCodec(SpawnSettings.SpawnEntry.CODEC).promotePartial(Util.addPrefix("Spawn data: ", LOGGER::error)),
							StringIdentifiable.toKeyable(SpawnGroup.values())
						)
						.fieldOf("spawners")
						.forGetter(spawnSettings -> spawnSettings.spawners),
					Codec.simpleMap(Registries.ENTITY_TYPE.getCodec(), SpawnSettings.SpawnDensity.CODEC, Registries.ENTITY_TYPE)
						.fieldOf("spawn_costs")
						.forGetter(spawnSettings -> spawnSettings.spawnCosts)
				)
				.apply(instance, SpawnSettings::new)
	);
	private final float creatureSpawnProbability;
	private final Map<SpawnGroup, Pool<SpawnSettings.SpawnEntry>> spawners;
	private final Map<EntityType<?>, SpawnSettings.SpawnDensity> spawnCosts;

	SpawnSettings(
		float creatureSpawnProbability, Map<SpawnGroup, Pool<SpawnSettings.SpawnEntry>> spawners, Map<EntityType<?>, SpawnSettings.SpawnDensity> spawnCosts
	) {
		this.creatureSpawnProbability = creatureSpawnProbability;
		this.spawners = ImmutableMap.copyOf(spawners);
		this.spawnCosts = ImmutableMap.copyOf(spawnCosts);
	}

	public Pool<SpawnSettings.SpawnEntry> getSpawnEntries(SpawnGroup spawnGroup) {
		return (Pool<SpawnSettings.SpawnEntry>)this.spawners.getOrDefault(spawnGroup, EMPTY_ENTRY_POOL);
	}

	@Nullable
	public SpawnSettings.SpawnDensity getSpawnDensity(EntityType<?> entityType) {
		return (SpawnSettings.SpawnDensity)this.spawnCosts.get(entityType);
	}

	public float getCreatureSpawnProbability() {
		return this.creatureSpawnProbability;
	}

	public static class Builder {
		private final Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> spawners = (Map<SpawnGroup, List<SpawnSettings.SpawnEntry>>)Stream.of(SpawnGroup.values())
			.collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> Lists.newArrayList()));
		private final Map<EntityType<?>, SpawnSettings.SpawnDensity> spawnCosts = Maps.<EntityType<?>, SpawnSettings.SpawnDensity>newLinkedHashMap();
		private float creatureSpawnProbability = 0.1F;

		public SpawnSettings.Builder spawn(SpawnGroup spawnGroup, SpawnSettings.SpawnEntry spawnEntry) {
			((List)this.spawners.get(spawnGroup)).add(spawnEntry);
			return this;
		}

		public SpawnSettings.Builder spawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
			this.spawnCosts.put(entityType, new SpawnSettings.SpawnDensity(gravityLimit, mass));
			return this;
		}

		public SpawnSettings.Builder creatureSpawnProbability(float probability) {
			this.creatureSpawnProbability = probability;
			return this;
		}

		public SpawnSettings build() {
			return new SpawnSettings(
				this.creatureSpawnProbability,
				(Map<SpawnGroup, Pool<SpawnSettings.SpawnEntry>>)this.spawners
					.entrySet()
					.stream()
					.collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> Pool.of((List)entry.getValue()))),
				ImmutableMap.copyOf(this.spawnCosts)
			);
		}
	}

	/**
	 * Embodies the density limit information of a type of entity in entity
	 * spawning logic. The density field is generated for all entities spawned
	 * than a specific type of entity.
	 */
	public static class SpawnDensity {
		public static final Codec<SpawnSettings.SpawnDensity> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.DOUBLE.fieldOf("energy_budget").forGetter(spawnDensity -> spawnDensity.gravityLimit),
						Codec.DOUBLE.fieldOf("charge").forGetter(spawnDensity -> spawnDensity.mass)
					)
					.apply(instance, SpawnSettings.SpawnDensity::new)
		);
		private final double gravityLimit;
		private final double mass;

		SpawnDensity(double gravityLimit, double mass) {
			this.gravityLimit = gravityLimit;
			this.mass = mass;
		}

		/**
		 * Represents the cap of gravity as in {@link
		 * net.minecraft.util.math.GravityField#calculate(BlockPos, double)} for
		 * entity spawning. If the cap is exceeded, the entity spawning attempt
		 * will skip.
		 */
		public double getGravityLimit() {
			return this.gravityLimit;
		}

		/**
		 * Represents the mass of each entity spawned. Will affect gravity
		 * calculation.
		 */
		public double getMass() {
			return this.mass;
		}
	}

	public static class SpawnEntry extends Weighted.Absent {
		public static final Codec<SpawnSettings.SpawnEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Registries.ENTITY_TYPE.getCodec().fieldOf("type").forGetter(spawnEntry -> spawnEntry.type),
						Weight.CODEC.fieldOf("weight").forGetter(Weighted.Absent::getWeight),
						Codec.INT.fieldOf("minCount").forGetter(spawnEntry -> spawnEntry.minGroupSize),
						Codec.INT.fieldOf("maxCount").forGetter(spawnEntry -> spawnEntry.maxGroupSize)
					)
					.apply(instance, SpawnSettings.SpawnEntry::new)
		);
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
}
