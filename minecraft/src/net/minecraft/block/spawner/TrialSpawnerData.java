package net.minecraft.block.spawner;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class TrialSpawnerData {
	public static final String SPAWN_DATA_KEY = "spawn_data";
	private static final String NEXT_MOB_SPAWNS_AT_KEY = "next_mob_spawns_at";
	private static final int field_50190 = 20;
	private static final int field_50191 = 18000;
	public static MapCodec<TrialSpawnerData> codec = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Uuids.SET_CODEC.lenientOptionalFieldOf("registered_players", Sets.<UUID>newHashSet()).forGetter(data -> data.players),
					Uuids.SET_CODEC.lenientOptionalFieldOf("current_mobs", Sets.<UUID>newHashSet()).forGetter(data -> data.spawnedMobsAlive),
					Codec.LONG.lenientOptionalFieldOf("cooldown_ends_at", Long.valueOf(0L)).forGetter(data -> data.cooldownEnd),
					Codec.LONG.lenientOptionalFieldOf("next_mob_spawns_at", Long.valueOf(0L)).forGetter(data -> data.nextMobSpawnsAt),
					Codec.intRange(0, Integer.MAX_VALUE).lenientOptionalFieldOf("total_mobs_spawned", 0).forGetter(data -> data.totalSpawnedMobs),
					MobSpawnerEntry.CODEC.lenientOptionalFieldOf("spawn_data").forGetter(data -> data.spawnData),
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).lenientOptionalFieldOf("ejecting_loot_table").forGetter(data -> data.rewardLootTable)
				)
				.apply(instance, TrialSpawnerData::new)
	);
	public final Set<UUID> players = new HashSet();
	public final Set<UUID> spawnedMobsAlive = new HashSet();
	public long cooldownEnd;
	public long nextMobSpawnsAt;
	public int totalSpawnedMobs;
	public Optional<MobSpawnerEntry> spawnData;
	public Optional<RegistryKey<LootTable>> rewardLootTable;
	@Nullable
	protected Entity displayEntity;
	@Nullable
	private DataPool<ItemStack> itemsToDropWhenOminous;
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
		Optional<RegistryKey<LootTable>> rewardLootTable
	) {
		this.players.addAll(players);
		this.spawnedMobsAlive.addAll(spawnedMobsAlive);
		this.cooldownEnd = cooldownEnd;
		this.nextMobSpawnsAt = nextMobSpawnsAt;
		this.totalSpawnedMobs = totalSpawnedMobs;
		this.spawnData = spawnData;
		this.rewardLootTable = rewardLootTable;
	}

	public void reset() {
		this.players.clear();
		this.totalSpawnedMobs = 0;
		this.nextMobSpawnsAt = 0L;
		this.cooldownEnd = 0L;
		this.spawnedMobsAlive.clear();
	}

	public boolean hasSpawnData(TrialSpawnerLogic logic, Random random) {
		boolean bl = this.getSpawnData(logic, random).getNbt().contains("id", NbtElement.STRING_TYPE);
		return bl || !logic.getConfig().spawnPotentialsDefinition().isEmpty();
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

	public void updatePlayers(ServerWorld world, BlockPos pos, TrialSpawnerLogic logic) {
		boolean bl = (pos.asLong() + world.getTime()) % 20L != 0L;
		if (!bl) {
			if (!logic.getSpawnerState().equals(TrialSpawnerState.COOLDOWN) || !logic.isOminous()) {
				List<UUID> list = logic.getEntityDetector().detect(world, logic.getEntitySelector(), pos, (double)logic.getDetectionRadius(), true);
				boolean bl2;
				if (!logic.isOminous() && !list.isEmpty()) {
					Optional<Pair<PlayerEntity, RegistryEntry<StatusEffect>>> optional = findPlayerWithOmen(world, list);
					optional.ifPresent(pair -> {
						PlayerEntity playerEntity = (PlayerEntity)pair.getFirst();
						if (pair.getSecond() == StatusEffects.BAD_OMEN) {
							applyTrialOmen(playerEntity);
						}

						world.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_TURNS_OMINOUS, BlockPos.ofFloored(playerEntity.getEyePos()), 0);
						logic.setOminous(world, pos);
					});
					bl2 = optional.isPresent();
				} else {
					bl2 = false;
				}

				if (!logic.getSpawnerState().equals(TrialSpawnerState.COOLDOWN) || bl2) {
					boolean bl3 = logic.getData().players.isEmpty();
					List<UUID> list2 = bl3 ? list : logic.getEntityDetector().detect(world, logic.getEntitySelector(), pos, (double)logic.getDetectionRadius(), false);
					if (this.players.addAll(list2)) {
						this.nextMobSpawnsAt = Math.max(world.getTime() + 40L, this.nextMobSpawnsAt);
						if (!bl2) {
							int i = logic.isOminous() ? WorldEvents.OMINOUS_TRIAL_SPAWNER_DETECTS_PLAYER : WorldEvents.TRIAL_SPAWNER_DETECTS_PLAYER;
							world.syncWorldEvent(i, pos, this.players.size());
						}
					}
				}
			}
		}
	}

	private static Optional<Pair<PlayerEntity, RegistryEntry<StatusEffect>>> findPlayerWithOmen(ServerWorld world, List<UUID> players) {
		PlayerEntity playerEntity = null;

		for (UUID uUID : players) {
			PlayerEntity playerEntity2 = world.getPlayerByUuid(uUID);
			if (playerEntity2 != null) {
				RegistryEntry<StatusEffect> registryEntry = StatusEffects.TRIAL_OMEN;
				if (playerEntity2.hasStatusEffect(registryEntry)) {
					return Optional.of(Pair.of(playerEntity2, registryEntry));
				}

				if (playerEntity2.hasStatusEffect(StatusEffects.BAD_OMEN)) {
					playerEntity = playerEntity2;
				}
			}
		}

		return Optional.ofNullable(playerEntity).map(player -> Pair.of(player, StatusEffects.BAD_OMEN));
	}

	public void resetAndClearMobs(TrialSpawnerLogic logic, ServerWorld world) {
		this.spawnedMobsAlive.stream().map(world::getEntity).forEach(entity -> {
			if (entity != null) {
				world.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_SPAWNS_MOB_AT_SPAWN_POS, entity.getBlockPos(), TrialSpawnerLogic.Type.NORMAL.getIndex());
				entity.remove(Entity.RemovalReason.DISCARDED);
			}
		});
		if (!logic.getOminousConfig().spawnPotentialsDefinition().isEmpty()) {
			this.spawnData = Optional.empty();
		}

		this.totalSpawnedMobs = 0;
		this.spawnedMobsAlive.clear();
		this.nextMobSpawnsAt = world.getTime() + (long)logic.getOminousConfig().ticksBetweenSpawn();
		logic.updateListeners();
		this.cooldownEnd = world.getTime() + logic.getOminousConfig().getCooldownLength();
	}

	private static void applyTrialOmen(PlayerEntity player) {
		StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.BAD_OMEN);
		if (statusEffectInstance != null) {
			int i = statusEffectInstance.getAmplifier() + 1;
			int j = 18000 * i;
			player.removeStatusEffect(StatusEffects.BAD_OMEN);
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.TRIAL_OMEN, j, 0));
		}
	}

	public boolean isCooldownPast(ServerWorld world, float f, int i) {
		long l = this.cooldownEnd - (long)i;
		return (float)world.getTime() >= (float)l + f;
	}

	public boolean isCooldownAtRepeating(ServerWorld world, float f, int i) {
		long l = this.cooldownEnd - (long)i;
		return (float)(world.getTime() - l) % f == 0.0F;
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
			DataPool<MobSpawnerEntry> dataPool = logic.getConfig().spawnPotentialsDefinition();
			Optional<MobSpawnerEntry> optional = dataPool.isEmpty() ? this.spawnData : dataPool.getOrEmpty(random).map(Weighted.Present::data);
			this.spawnData = Optional.of((MobSpawnerEntry)optional.orElseGet(MobSpawnerEntry::new));
			logic.updateListeners();
			return (MobSpawnerEntry)this.spawnData.get();
		}
	}

	@Nullable
	public Entity setDisplayEntity(TrialSpawnerLogic logic, World world, TrialSpawnerState state) {
		if (!state.doesDisplayRotate()) {
			return null;
		} else {
			if (this.displayEntity == null) {
				NbtCompound nbtCompound = this.getSpawnData(logic, world.getRandom()).getNbt();
				if (nbtCompound.contains("id", NbtElement.STRING_TYPE)) {
					this.displayEntity = EntityType.loadEntityWithPassengers(nbtCompound, world, Function.identity());
				}
			}

			return this.displayEntity;
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

	public DataPool<ItemStack> getItemsToDropWhenOminous(ServerWorld world, TrialSpawnerConfig config, BlockPos pos) {
		if (this.itemsToDropWhenOminous != null) {
			return this.itemsToDropWhenOminous;
		} else {
			LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(config.itemsToDropWhenOminous());
			LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world).build(LootContextTypes.EMPTY);
			long l = getLootSeed(world, pos);
			ObjectArrayList<ItemStack> objectArrayList = lootTable.generateLoot(lootContextParameterSet, l);
			if (objectArrayList.isEmpty()) {
				return DataPool.empty();
			} else {
				DataPool.Builder<ItemStack> builder = new DataPool.Builder<>();

				for (ItemStack itemStack : objectArrayList) {
					builder.add(itemStack.copyWithCount(1), itemStack.getCount());
				}

				this.itemsToDropWhenOminous = builder.build();
				return this.itemsToDropWhenOminous;
			}
		}
	}

	private static long getLootSeed(ServerWorld world, BlockPos pos) {
		BlockPos blockPos = new BlockPos(
			MathHelper.floor((float)pos.getX() / 30.0F), MathHelper.floor((float)pos.getY() / 20.0F), MathHelper.floor((float)pos.getZ() / 30.0F)
		);
		return world.getSeed() + blockPos.asLong();
	}
}
