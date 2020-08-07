package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpawnSettings {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final SpawnSettings INSTANCE = new SpawnSettings(
		0.1F,
		(Map<SpawnGroup, List<SpawnSettings.SpawnEntry>>)Stream.of(SpawnGroup.values())
			.collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> ImmutableList.of())),
		ImmutableMap.of(),
		false
	);
	public static final MapCodec<SpawnSettings> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.FLOAT.optionalFieldOf("creature_spawn_probability", Float.valueOf(0.1F)).forGetter(spawnSettings -> spawnSettings.creatureSpawnProbability),
					Codec.simpleMap(
							SpawnGroup.field_24655,
							SpawnSettings.SpawnEntry.CODEC.listOf().promotePartial(Util.method_29188("Spawn data: ", LOGGER::error)),
							StringIdentifiable.method_28142(SpawnGroup.values())
						)
						.fieldOf("spawners")
						.forGetter(spawnSettings -> spawnSettings.spawners),
					Codec.simpleMap(Registry.ENTITY_TYPE, SpawnSettings.SpawnDensity.CODEC, Registry.ENTITY_TYPE)
						.fieldOf("spawn_costs")
						.forGetter(spawnSettings -> spawnSettings.spawnCosts),
					Codec.BOOL.fieldOf("player_spawn_friendly").orElse(false).forGetter(SpawnSettings::isPlayerSpawnFriendly)
				)
				.apply(instance, SpawnSettings::new)
	);
	private final float creatureSpawnProbability;
	private final Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> spawners;
	private final Map<EntityType<?>, SpawnSettings.SpawnDensity> spawnCosts;
	private final boolean playerSpawnFriendly;

	private SpawnSettings(
		float creatureSpawnProbability,
		Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> spawners,
		Map<EntityType<?>, SpawnSettings.SpawnDensity> spawnCosts,
		boolean playerSpawnFriendly
	) {
		this.creatureSpawnProbability = creatureSpawnProbability;
		this.spawners = spawners;
		this.spawnCosts = spawnCosts;
		this.playerSpawnFriendly = playerSpawnFriendly;
	}

	public List<SpawnSettings.SpawnEntry> getSpawnEntry(SpawnGroup spawnGroup) {
		return (List<SpawnSettings.SpawnEntry>)this.spawners.getOrDefault(spawnGroup, ImmutableList.of());
	}

	@Nullable
	public SpawnSettings.SpawnDensity getSpawnDensity(EntityType<?> entityType) {
		return (SpawnSettings.SpawnDensity)this.spawnCosts.get(entityType);
	}

	public float getCreatureSpawnProbability() {
		return this.creatureSpawnProbability;
	}

	public boolean isPlayerSpawnFriendly() {
		return this.playerSpawnFriendly;
	}

	public static class Builder {
		private final Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> spawners = (Map<SpawnGroup, List<SpawnSettings.SpawnEntry>>)Stream.of(SpawnGroup.values())
			.collect(ImmutableMap.toImmutableMap(spawnGroup -> spawnGroup, spawnGroup -> Lists.newArrayList()));
		private final Map<EntityType<?>, SpawnSettings.SpawnDensity> spawnCosts = Maps.<EntityType<?>, SpawnSettings.SpawnDensity>newLinkedHashMap();
		private float creatureSpawnProbability = 0.1F;
		private boolean playerSpawnFriendly;

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

		public SpawnSettings.Builder playerSpawnFriendly() {
			this.playerSpawnFriendly = true;
			return this;
		}

		public SpawnSettings build() {
			return new SpawnSettings(
				this.creatureSpawnProbability,
				(Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ImmutableList.copyOf((Collection)entry.getValue()))),
				ImmutableMap.copyOf(this.spawnCosts),
				this.playerSpawnFriendly
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

		private SpawnDensity(double gravityLimit, double mass) {
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

	public static class SpawnEntry extends WeightedPicker.Entry {
		public static final Codec<SpawnSettings.SpawnEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Registry.ENTITY_TYPE.fieldOf("type").forGetter(spawnEntry -> spawnEntry.type),
						Codec.INT.fieldOf("weight").forGetter(spawnEntry -> spawnEntry.weight),
						Codec.INT.fieldOf("minCount").forGetter(spawnEntry -> spawnEntry.minGroupSize),
						Codec.INT.fieldOf("maxCount").forGetter(spawnEntry -> spawnEntry.maxGroupSize)
					)
					.apply(instance, SpawnSettings.SpawnEntry::new)
		);
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
