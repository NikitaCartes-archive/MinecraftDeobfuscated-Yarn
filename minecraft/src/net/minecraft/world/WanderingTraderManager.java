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
					} else if (this.method_18018(world)) {
						this.spawnChance = 25;
						return 1;
					} else {
						return 0;
					}
				}
			}
		}
	}

	private boolean method_18018(ServerWorld serverWorld) {
		PlayerEntity playerEntity = serverWorld.getRandomAlivePlayer();
		if (playerEntity == null) {
			return true;
		} else if (this.random.nextInt(10) != 0) {
			return false;
		} else {
			BlockPos blockPos = playerEntity.getBlockPos();
			int i = 48;
			PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
			Optional<BlockPos> optional = pointOfInterestStorage.getPosition(
				PointOfInterestType.MEETING.getCompletionCondition(), blockPosx -> true, blockPos, 48, PointOfInterestStorage.OccupationStatus.ANY
			);
			BlockPos blockPos2 = (BlockPos)optional.orElse(blockPos);
			BlockPos blockPos3 = this.getNearbySpawnPos(serverWorld, blockPos2, 48);
			if (blockPos3 != null && this.doesNotSuffocateAt(serverWorld, blockPos3)) {
				if (serverWorld.getBiomeKey(blockPos3).equals(Optional.of(BiomeKeys.THE_VOID))) {
					return false;
				}

				WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(serverWorld, null, null, null, blockPos3, SpawnReason.EVENT, false, false);
				if (wanderingTraderEntity != null) {
					for (int j = 0; j < 2; j++) {
						this.spawnLlama(serverWorld, wanderingTraderEntity, 4);
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

	private void spawnLlama(ServerWorld serverWorld, WanderingTraderEntity wanderingTraderEntity, int i) {
		BlockPos blockPos = this.getNearbySpawnPos(serverWorld, wanderingTraderEntity.getBlockPos(), i);
		if (blockPos != null) {
			TraderLlamaEntity traderLlamaEntity = EntityType.TRADER_LLAMA.spawn(serverWorld, null, null, null, blockPos, SpawnReason.EVENT, false, false);
			if (traderLlamaEntity != null) {
				traderLlamaEntity.attachLeash(wanderingTraderEntity, true);
			}
		}
	}

	@Nullable
	private BlockPos getNearbySpawnPos(WorldView worldView, BlockPos blockPos, int i) {
		BlockPos blockPos2 = null;

		for (int j = 0; j < 10; j++) {
			int k = blockPos.getX() + this.random.nextInt(i * 2) - i;
			int l = blockPos.getZ() + this.random.nextInt(i * 2) - i;
			int m = worldView.getTopY(Heightmap.Type.WORLD_SURFACE, k, l);
			BlockPos blockPos3 = new BlockPos(k, m, l);
			if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, worldView, blockPos3, EntityType.WANDERING_TRADER)) {
				blockPos2 = blockPos3;
				break;
			}
		}

		return blockPos2;
	}

	private boolean doesNotSuffocateAt(BlockView blockView, BlockPos blockPos) {
		for (BlockPos blockPos2 : BlockPos.iterate(blockPos, blockPos.add(1, 2, 1))) {
			if (!blockView.getBlockState(blockPos2).getCollisionShape(blockView, blockPos2).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
