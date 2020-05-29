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
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class WanderingTraderManager implements Spawner {
	private final Random random = new Random();
	private final ServerWorldProperties field_24387;
	private int spawnTimer;
	private int spawnDelay;
	private int spawnChance;

	public WanderingTraderManager(ServerWorldProperties serverWorldProperties) {
		this.field_24387 = serverWorldProperties;
		this.spawnTimer = 1200;
		this.spawnDelay = serverWorldProperties.getWanderingTraderSpawnDelay();
		this.spawnChance = serverWorldProperties.getWanderingTraderSpawnChance();
		if (this.spawnDelay == 0 && this.spawnChance == 0) {
			this.spawnDelay = 24000;
			serverWorldProperties.setWanderingTraderSpawnDelay(this.spawnDelay);
			this.spawnChance = 25;
			serverWorldProperties.setWanderingTraderSpawnChance(this.spawnChance);
		}
	}

	@Override
	public int spawn(ServerWorld serverWorld, boolean bl, boolean bl2) {
		if (!serverWorld.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING)) {
			return 0;
		} else if (--this.spawnTimer > 0) {
			return 0;
		} else {
			this.spawnTimer = 1200;
			this.spawnDelay -= 1200;
			this.field_24387.setWanderingTraderSpawnDelay(this.spawnDelay);
			if (this.spawnDelay > 0) {
				return 0;
			} else {
				this.spawnDelay = 24000;
				if (!serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
					return 0;
				} else {
					int i = this.spawnChance;
					this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
					this.field_24387.setWanderingTraderSpawnChance(this.spawnChance);
					if (this.random.nextInt(100) > i) {
						return 0;
					} else if (this.method_18018(serverWorld)) {
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
			if (blockPos3 != null && this.wontSuffocateAt(serverWorld, blockPos3)) {
				if (serverWorld.getBiome(blockPos3) == Biomes.THE_VOID) {
					return false;
				}

				WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(serverWorld, null, null, null, blockPos3, SpawnReason.EVENT, false, false);
				if (wanderingTraderEntity != null) {
					for (int j = 0; j < 2; j++) {
						this.spawnLlama(wanderingTraderEntity, 4);
					}

					this.field_24387.setWanderingTraderId(wanderingTraderEntity.getUuid());
					wanderingTraderEntity.setDespawnDelay(48000);
					wanderingTraderEntity.setWanderTarget(blockPos2);
					wanderingTraderEntity.setPositionTarget(blockPos2, 16);
					return true;
				}
			}

			return false;
		}
	}

	private void spawnLlama(WanderingTraderEntity wanderingTrader, int range) {
		BlockPos blockPos = this.getNearbySpawnPos(wanderingTrader.world, wanderingTrader.getBlockPos(), range);
		if (blockPos != null) {
			TraderLlamaEntity traderLlamaEntity = EntityType.TRADER_LLAMA.spawn(wanderingTrader.world, null, null, null, blockPos, SpawnReason.EVENT, false, false);
			if (traderLlamaEntity != null) {
				traderLlamaEntity.attachLeash(wanderingTrader, true);
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

	private boolean wontSuffocateAt(BlockView blockView, BlockPos blockPos) {
		for (BlockPos blockPos2 : BlockPos.iterate(blockPos, blockPos.add(1, 2, 1))) {
			if (!blockView.getBlockState(blockPos2).getCollisionShape(blockView, blockPos2).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
