package net.minecraft.world;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class WanderingTraderManager implements Spawner {
	private static final int DEFAULT_SPAWN_TIMER = 1200;
	public static final int DEFAULT_SPAWN_DELAY = 24000;
	private static final int MIN_SPAWN_CHANCE = 25;
	private static final int MAX_SPAWN_CHANCE = 75;
	private static final int DEFAULT_SPAWN_CHANCE = 25;
	private static final int field_30635 = 10;
	private static final int field_30636 = 10;
	private final Random random = new Random();
	private final ServerWorldProperties properties;
	private int spawnTimer;
	private int spawnDelay;
	private int spawnChance;

	public WanderingTraderManager(ServerWorldProperties properties) {
		this.properties = properties;
		this.spawnTimer = 1200;
		this.spawnDelay = properties.getWanderingTraderSpawnDelay();
		this.spawnChance = properties.getWanderingTraderSpawnChance();
		if (this.spawnDelay == 0 && this.spawnChance == 0) {
			this.spawnDelay = 24000;
			properties.setWanderingTraderSpawnDelay(this.spawnDelay);
			this.spawnChance = 25;
			properties.setWanderingTraderSpawnChance(this.spawnChance);
		}
	}

	@Override
	public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		if (!world.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING)) {
			return 0;
		} else if (--this.spawnTimer > 0) {
			return 0;
		} else {
			this.spawnTimer = 1200;
			this.spawnDelay -= 1200;
			this.properties.setWanderingTraderSpawnDelay(this.spawnDelay);
			if (this.spawnDelay > 0) {
				return 0;
			} else {
				this.spawnDelay = 24000;
				if (!world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
					return 0;
				} else {
					int i = this.spawnChance;
					this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
					this.properties.setWanderingTraderSpawnChance(this.spawnChance);
					if (this.random.nextInt(100) > i) {
						return 0;
					} else if (this.trySpawn(world)) {
						this.spawnChance = 25;
						return 1;
					} else {
						return 0;
					}
				}
			}
		}
	}

	private boolean trySpawn(ServerWorld world) {
		PlayerEntity playerEntity = world.getRandomAlivePlayer();
		if (playerEntity == null) {
			return true;
		} else if (this.random.nextInt(10) != 0) {
			return false;
		} else {
			BlockPos blockPos = playerEntity.getBlockPos();
			int i = 48;
			PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
			Optional<BlockPos> optional = pointOfInterestStorage.getPosition(
				PointOfInterestType.MEETING.getCompletionCondition(), pos -> true, blockPos, 48, PointOfInterestStorage.OccupationStatus.ANY
			);
			BlockPos blockPos2 = (BlockPos)optional.orElse(blockPos);
			BlockPos blockPos3 = this.getNearbySpawnPos(world, blockPos2, 48);
			if (blockPos3 != null && this.doesNotSuffocateAt(world, blockPos3)) {
				if (world.getBiomeKey(blockPos3).equals(Optional.of(BiomeKeys.THE_VOID))) {
					return false;
				}

				WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(world, null, null, null, blockPos3, SpawnReason.EVENT, false, false);
				if (wanderingTraderEntity != null) {
					for (int j = 0; j < 2; j++) {
						this.spawnLlama(world, wanderingTraderEntity, 4);
					}

					this.properties.setWanderingTraderId(wanderingTraderEntity.getUuid());
					wanderingTraderEntity.setDespawnDelay(48000);
					wanderingTraderEntity.setWanderTarget(blockPos2);
					wanderingTraderEntity.setPositionTarget(blockPos2, 16);
					return true;
				}
			}

			return false;
		}
	}

	private void spawnLlama(ServerWorld world, WanderingTraderEntity wanderingTrader, int range) {
		BlockPos blockPos = this.getNearbySpawnPos(world, wanderingTrader.getBlockPos(), range);
		if (blockPos != null) {
			TraderLlamaEntity traderLlamaEntity = EntityType.TRADER_LLAMA.spawn(world, null, null, null, blockPos, SpawnReason.EVENT, false, false);
			if (traderLlamaEntity != null) {
				traderLlamaEntity.attachLeash(wanderingTrader, true);
			}
		}
	}

	@Nullable
	private BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range) {
		BlockPos blockPos = null;

		for (int i = 0; i < 10; i++) {
			int j = pos.getX() + this.random.nextInt(range * 2) - range;
			int k = pos.getZ() + this.random.nextInt(range * 2) - range;
			int l = world.getTopY(Heightmap.Type.WORLD_SURFACE, j, k);
			BlockPos blockPos2 = new BlockPos(j, l, k);
			if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, blockPos2, EntityType.WANDERING_TRADER)) {
				blockPos = blockPos2;
				break;
			}
		}

		return blockPos;
	}

	private boolean doesNotSuffocateAt(BlockView world, BlockPos pos) {
		for (BlockPos blockPos : BlockPos.iterate(pos, pos.add(1, 2, 1))) {
			if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
