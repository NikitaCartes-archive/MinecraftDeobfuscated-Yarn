package net.minecraft.world;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.level.LevelProperties;

public class WanderingTraderManager {
	private final Random random = new Random();
	private final ServerWorld world;
	private int field_17728;
	private int spawnDelay;
	private int spawnChance;

	public WanderingTraderManager(ServerWorld world) {
		this.world = world;
		this.field_17728 = 1200;
		LevelProperties levelProperties = world.getLevelProperties();
		this.spawnDelay = levelProperties.getWanderingTraderSpawnDelay();
		this.spawnChance = levelProperties.getWanderingTraderSpawnChance();
		if (this.spawnDelay == 0 && this.spawnChance == 0) {
			this.spawnDelay = 24000;
			levelProperties.setWanderingTraderSpawnDelay(this.spawnDelay);
			this.spawnChance = 25;
			levelProperties.setWanderingTraderSpawnChance(this.spawnChance);
		}
	}

	public void tick() {
		if (--this.field_17728 <= 0) {
			this.field_17728 = 1200;
			LevelProperties levelProperties = this.world.getLevelProperties();
			this.spawnDelay -= 1200;
			levelProperties.setWanderingTraderSpawnDelay(this.spawnDelay);
			if (this.spawnDelay <= 0) {
				this.spawnDelay = 24000;
				if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
					int i = this.spawnChance;
					this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
					levelProperties.setWanderingTraderSpawnChance(this.spawnChance);
					if (this.random.nextInt(100) <= i) {
						if (this.method_18018()) {
							this.spawnChance = 25;
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
			BlockPos blockPos3 = this.method_18017(blockPos2, 48);
			if (blockPos3 != null && this.method_23279(blockPos3)) {
				if (this.world.getBiome(blockPos3) == Biomes.THE_VOID) {
					return false;
				}

				WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(this.world, null, null, null, blockPos3, SpawnType.EVENT, false, false);
				if (wanderingTraderEntity != null) {
					for (int j = 0; j < 2; j++) {
						this.method_18016(wanderingTraderEntity, 4);
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

	private void method_18016(WanderingTraderEntity wanderingTraderEntity, int i) {
		BlockPos blockPos = this.method_18017(new BlockPos(wanderingTraderEntity), i);
		if (blockPos != null) {
			TraderLlamaEntity traderLlamaEntity = EntityType.TRADER_LLAMA.spawn(this.world, null, null, null, blockPos, SpawnType.EVENT, false, false);
			if (traderLlamaEntity != null) {
				traderLlamaEntity.attachLeash(wanderingTraderEntity, true);
			}
		}
	}

	@Nullable
	private BlockPos method_18017(BlockPos blockPos, int i) {
		BlockPos blockPos2 = null;

		for (int j = 0; j < 10; j++) {
			int k = blockPos.getX() + this.random.nextInt(i * 2) - i;
			int l = blockPos.getZ() + this.random.nextInt(i * 2) - i;
			int m = this.world.getTopY(Heightmap.Type.WORLD_SURFACE, k, l);
			BlockPos blockPos3 = new BlockPos(k, m, l);
			if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, this.world, blockPos3, EntityType.WANDERING_TRADER)) {
				blockPos2 = blockPos3;
				break;
			}
		}

		return blockPos2;
	}

	private boolean method_23279(BlockPos blockPos) {
		for (BlockPos blockPos2 : BlockPos.iterate(blockPos, blockPos.add(1, 2, 1))) {
			if (!this.world.getBlockState(blockPos2).getCollisionShape(this.world, blockPos2).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
