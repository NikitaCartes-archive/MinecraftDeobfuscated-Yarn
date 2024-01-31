package net.minecraft.block.spawner;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class TrialSpawnerData {
	public static final String SPAWN_DATA_KEY = "spawn_data";
	private static final String NEXT_MOB_SPAWNS_AT_KEY = "next_mob_spawns_at";
	public static MapCodec<TrialSpawnerData> codec = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Uuids.SET_CODEC.optionalFieldOf("registered_players", Sets.<UUID>newHashSet()).forGetter(data -> data.players),
					Uuids.SET_CODEC.optionalFieldOf("current_mobs", Sets.<UUID>newHashSet()).forGetter(data -> data.spawnedMobsAlive),
					Codec.LONG.optionalFieldOf("cooldown_ends_at", Long.valueOf(0L)).forGetter(data -> data.cooldownEnd),
					Codec.LONG.optionalFieldOf("next_mob_spawns_at", Long.valueOf(0L)).forGetter(data -> data.nextMobSpawnsAt),
					Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("total_mobs_spawned", 0).forGetter(data -> data.totalSpawnedMobs),
					MobSpawnerEntry.CODEC.optionalFieldOf("spawn_data").forGetter(data -> data.spawnData),
					Identifier.CODEC.optionalFieldOf("ejecting_loot_table").forGetter(data -> data.rewardLootTable)
				)
				.apply(instance, TrialSpawnerData::new)
	);
	public final Set<UUID> players = new HashSet();
	public final Set<UUID> spawnedMobsAlive = new HashSet();
	public long cooldownEnd;
	public long nextMobSpawnsAt;
	public int totalSpawnedMobs;
	public Optional<MobSpawnerEntry> spawnData;
	public Optional<Identifier> rewardLootTable;
	public DataPool<MobSpawnerEntry> spawnDataPool;
	@Nullable
	protected Entity displayEntity;
	protected double displayEntityRotation;
	protected double lastDisplayEntityRotation;

	public TrialSpawnerData() {
		this(Collections.emptySet(), Collections.emptySet(), 0L, 0L, 0, Optional.empty(), Optional.empty());
	}

	public TrialSpawnerData(
		Set<UUID> players,
		Set<UUID> spawnedMobsAlive,
		long cooldownEnd,
		long nextMobSpawnsAt,
		int totalSpawnedMobs,
		Optional<MobSpawnerEntry> spawnData,
		Optional<Identifier> rewardLootTable
	) {
		this.players.addAll(players);
		this.spawnedMobsAlive.addAll(spawnedMobsAlive);
		this.cooldownEnd = cooldownEnd;
		this.nextMobSpawnsAt = nextMobSpawnsAt;
		this.totalSpawnedMobs = totalSpawnedMobs;
		this.spawnData = spawnData;
		this.rewardLootTable = rewardLootTable;
	}

	public void populateSpawnDataPool(TrialSpawnerConfig config) {
		DataPool<MobSpawnerEntry> dataPool = config.spawnPotentialsDefinition();
		if (dataPool.isEmpty()) {
			this.spawnDataPool = DataPool.of((MobSpawnerEntry)this.spawnData.orElseGet(MobSpawnerEntry::new));
		} else {
			this.spawnDataPool = dataPool;
		}
	}

	public void reset() {
		this.players.clear();
		this.totalSpawnedMobs = 0;
		this.nextMobSpawnsAt = 0L;
		this.cooldownEnd = 0L;
		this.spawnedMobsAlive.clear();
	}

	public boolean hasSpawnData() {
		boolean bl = this.spawnData.isPresent() && ((MobSpawnerEntry)this.spawnData.get()).getNbt().contains("id", NbtElement.STRING_TYPE);
		return bl || !this.spawnDataPool.isEmpty();
	}

	public boolean hasSpawnedAllMobs(TrialSpawnerConfig config, int additionalPlayers) {
		return this.totalSpawnedMobs >= config.getTotalMobs(additionalPlayers);
	}

	public boolean areMobsDead() {
		return this.spawnedMobsAlive.isEmpty();
	}

	public boolean canSpawnMore(ServerWorld world, TrialSpawnerConfig config, int additionalPlayers) {
		return world.getTime() >= this.nextMobSpawnsAt && this.spawnedMobsAlive.size() < config.getSimultaneousMobs(additionalPlayers);
	}

	public int getAdditionalPlayers(BlockPos pos) {
		if (this.players.isEmpty()) {
			Util.error("Trial Spawner at " + pos + " has no detected players");
		}

		return Math.max(0, this.players.size() - 1);
	}

	public void updatePlayers(ServerWorld world, BlockPos pos, EntityDetector entityDetector, EntityDetector.Selector entitySelector, int range) {
		List<UUID> list = entityDetector.detect(world, entitySelector, pos, (double)range);
		boolean bl = this.players.addAll(list);
		if (bl) {
			this.nextMobSpawnsAt = Math.max(world.getTime() + 40L, this.nextMobSpawnsAt);
			world.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_DETECTS_PLAYER, pos, this.players.size());
		}
	}

	public boolean isCooldownPast(ServerWorld world, TrialSpawnerConfig config, float position) {
		long l = this.cooldownEnd - (long)config.targetCooldownLength();
		return (float)world.getTime() >= (float)l + position;
	}

	public boolean isCooldownAtRepeating(ServerWorld world, TrialSpawnerConfig config, float position) {
		long l = this.cooldownEnd - (long)config.targetCooldownLength();
		return (float)(world.getTime() - l) % position == 0.0F;
	}

	public boolean isCooldownOver(ServerWorld world) {
		return world.getTime() >= this.cooldownEnd;
	}

	public void setEntityType(TrialSpawnerLogic logic, Random random, EntityType<?> type) {
		this.getSpawnData(logic, random).getNbt().putString("id", Registries.ENTITY_TYPE.getId(type).toString());
	}

	protected MobSpawnerEntry getSpawnData(TrialSpawnerLogic logic, Random random) {
		if (this.spawnData.isPresent()) {
			return (MobSpawnerEntry)this.spawnData.get();
		} else {
			this.spawnData = Optional.of((MobSpawnerEntry)this.spawnDataPool.getOrEmpty(random).map(Weighted.Present::getData).orElseGet(MobSpawnerEntry::new));
			logic.updateListeners();
			return (MobSpawnerEntry)this.spawnData.get();
		}
	}

	@Nullable
	public Entity setDisplayEntity(TrialSpawnerLogic logic, World world, TrialSpawnerState state) {
		if (logic.canActivate(world) && state.doesDisplayRotate()) {
			if (this.displayEntity == null) {
				NbtCompound nbtCompound = this.getSpawnData(logic, world.getRandom()).getNbt();
				if (nbtCompound.contains("id", NbtElement.STRING_TYPE)) {
					this.displayEntity = EntityType.loadEntityWithPassengers(nbtCompound, world, Function.identity());
				}
			}

			return this.displayEntity;
		} else {
			return null;
		}
	}

	public NbtCompound getSpawnDataNbt(TrialSpawnerState state) {
		NbtCompound nbtCompound = new NbtCompound();
		if (state == TrialSpawnerState.ACTIVE) {
			nbtCompound.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
		}

		this.spawnData
			.ifPresent(
				spawnData -> nbtCompound.put(
						"spawn_data",
						(NbtElement)MobSpawnerEntry.CODEC.encodeStart(NbtOps.INSTANCE, spawnData).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))
					)
			);
		return nbtCompound;
	}

	public double getDisplayEntityRotation() {
		return this.displayEntityRotation;
	}

	public double getLastDisplayEntityRotation() {
		return this.lastDisplayEntityRotation;
	}
}
