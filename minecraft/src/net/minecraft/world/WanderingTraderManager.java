package net.minecraft.world;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_5217;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class WanderingTraderManager {
	private final Random random = new Random();
	private final ServerWorld world;
	private int spawnTimer;
	private int spawnDelay;
	private int spawnChance;

	public WanderingTraderManager(ServerWorld world) {
		this.world = world;
		this.spawnTimer = 1200;
		class_5217 lv = world.getLevelProperties();
		this.spawnDelay = lv.getWanderingTraderSpawnDelay();
		this.spawnChance = lv.getWanderingTraderSpawnChance();
		if (this.spawnDelay == 0 && this.spawnChance == 0) {
			this.spawnDelay = 24000;
			lv.setWanderingTraderSpawnDelay(this.spawnDelay);
			this.spawnChance = 25;
			lv.setWanderingTraderSpawnChance(this.spawnChance);
		}
	}

	public void tick() {
		if (this.world.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING)) {
			if (--this.spawnTimer <= 0) {
				this.spawnTimer = 1200;
				class_5217 lv = this.world.getLevelProperties();
				this.spawnDelay -= 1200;
				lv.setWanderingTraderSpawnDelay(this.spawnDelay);
				if (this.spawnDelay <= 0) {
					this.spawnDelay = 24000;
					if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
						int i = this.spawnChance;
						this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
						lv.setWanderingTraderSpawnChance(this.spawnChance);
						if (this.random.nextInt(100) <= i) {
							if (this.method_18018()) {
								this.spawnChance = 25;
							}
						}
					}
				}
			}
		}
	}

	private boolean method_18018() {
		PlayerEntity playerEntity = this.world.getRandomAlivePlayer();
		if (playerEntity == null) {
			return true;
		} else if (this.random.nextInt(10) != 0) {
			return false;
		} else {
			BlockPos blockPos = playerEntity.getBlockPos();
			int i = 48;
			PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
			Optional<BlockPos> optional = pointOfInterestStorage.getPosition(
				PointOfInterestType.MEETING.getCompletionCondition(), blockPosx -> true, blockPos, 48, PointOfInterestStorage.OccupationStatus.ANY
			);
			BlockPos blockPos2 = (BlockPos)optional.orElse(blockPos);
			BlockPos blockPos3 = this.getNearbySpawnPos(blockPos2, 48);
			if (blockPos3 != null && this.wontSuffocateAt(blockPos3)) {
				if (this.world.getBiome(blockPos3) == Biomes.THE_VOID) {
					return false;
				}

				WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(this.world, null, null, null, blockPos3, SpawnType.EVENT, false, false);
				if (wanderingTraderEntity != null) {
					for (int j = 0; j < 2; j++) {
						this.spawnLlama(wanderingTraderEntity, 4);
					}

					this.world.getLevelProperties().setWanderingTraderId(wanderingTraderEntity.getUuid());
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
		BlockPos blockPos = this.getNearbySpawnPos(wanderingTrader.getBlockPos(), range);
		if (blockPos != null) {
			TraderLlamaEntity traderLlamaEntity = EntityType.TRADER_LLAMA.spawn(this.world, null, null, null, blockPos, SpawnType.EVENT, false, false);
			if (traderLlamaEntity != null) {
				traderLlamaEntity.attachLeash(wanderingTrader, true);
			}
		}
	}

	@Nullable
	private BlockPos getNearbySpawnPos(BlockPos pos, int range) {
		BlockPos blockPos = null;

		for (int i = 0; i < 10; i++) {
			int j = pos.getX() + this.random.nextInt(range * 2) - range;
			int k = pos.getZ() + this.random.nextInt(range * 2) - range;
			int l = this.world.getTopY(Heightmap.Type.WORLD_SURFACE, j, k);
			BlockPos blockPos2 = new BlockPos(j, l, k);
			if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, this.world, blockPos2, EntityType.WANDERING_TRADER)) {
				blockPos = blockPos2;
				break;
			}
		}

		return blockPos;
	}

	private boolean wontSuffocateAt(BlockPos pos) {
		for (BlockPos blockPos : BlockPos.iterate(pos, pos.add(1, 2, 1))) {
			if (!this.world.getBlockState(blockPos).getCollisionShape(this.world, blockPos).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
