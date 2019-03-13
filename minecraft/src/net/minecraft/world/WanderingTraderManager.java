package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.SpawnHelper;
import net.minecraft.sortme.SpawnRestriction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.LevelProperties;

public class WanderingTraderManager {
	private final Random random = new Random();
	private final ServerWorld field_17727;
	private int field_17728;
	private int field_17729;
	private int field_17730;

	public WanderingTraderManager(ServerWorld serverWorld) {
		this.field_17727 = serverWorld;
		this.field_17728 = 1200;
		LevelProperties levelProperties = serverWorld.method_8401();
		this.field_17729 = levelProperties.getWanderingTraderSpawnDelay();
		this.field_17730 = levelProperties.getWanderingTraderSpawnChance();
		if (this.field_17729 == 0 && this.field_17730 == 0) {
			this.field_17729 = 24000;
			levelProperties.setWanderingTraderSpawnDelay(this.field_17729);
			this.field_17730 = 25;
			levelProperties.setWanderingTraderSpawnChance(this.field_17730);
		}
	}

	public void tick() {
		if (--this.field_17728 <= 0) {
			this.field_17728 = 1200;
			LevelProperties levelProperties = this.field_17727.method_8401();
			this.field_17729 -= 1200;
			levelProperties.setWanderingTraderSpawnDelay(this.field_17729);
			if (this.field_17729 <= 0) {
				this.field_17729 = 24000;
				if (this.field_17727.getGameRules().getBoolean("doMobSpawning")) {
					int i = this.field_17730;
					this.field_17730 = MathHelper.clamp(this.field_17730 + 25, 25, 75);
					levelProperties.setWanderingTraderSpawnChance(this.field_17730);
					if (this.random.nextInt(100) <= i) {
						if (this.method_18018()) {
							this.field_17730 = 25;
						}
					}
				}
			}
		}
	}

	private boolean method_18018() {
		PlayerEntity playerEntity = this.field_17727.method_18779();
		if (playerEntity == null) {
			return true;
		} else if (this.random.nextInt(10) != 0) {
			return false;
		} else {
			BlockPos blockPos = playerEntity.method_5704();
			BlockPos blockPos2 = this.method_18017(blockPos, 48);
			if (blockPos2 != null) {
				WanderingTraderEntity wanderingTraderEntity = EntityType.field_17713
					.method_5899(this.field_17727, null, null, null, blockPos2, SpawnType.field_16467, false, false);
				if (wanderingTraderEntity != null) {
					for (int i = 0; i < 2; i++) {
						this.method_18016(wanderingTraderEntity, 8);
					}

					this.field_17727.method_8401().setWanderingTraderId(wanderingTraderEntity.getUuid());
					wanderingTraderEntity.setDespawnDelay(48000);
					wanderingTraderEntity.method_18069(new BlockPos(playerEntity.x, playerEntity.y, playerEntity.z));
					return true;
				}
			}

			return false;
		}
	}

	private void method_18016(WanderingTraderEntity wanderingTraderEntity, int i) {
		BlockPos blockPos = this.method_18017(new BlockPos(wanderingTraderEntity), i);
		if (blockPos != null) {
			TraderLlamaEntity traderLlamaEntity = EntityType.field_17714.method_5899(this.field_17727, null, null, null, blockPos, SpawnType.field_16467, false, false);
			if (traderLlamaEntity != null) {
				traderLlamaEntity.attachLeash(wanderingTraderEntity, true);
				traderLlamaEntity.setDespawnDelay(47999);
			}
		}
	}

	@Nullable
	private BlockPos method_18017(BlockPos blockPos, int i) {
		BlockPos blockPos2 = null;

		for (int j = 0; j < 10; j++) {
			int k = blockPos.getX() + this.random.nextInt(i * 2) - i;
			int l = blockPos.getZ() + this.random.nextInt(i * 2) - i;
			int m = this.field_17727.method_8589(Heightmap.Type.WORLD_SURFACE, k, l);
			BlockPos blockPos3 = new BlockPos(k, m, l);
			if (SpawnHelper.method_8660(SpawnRestriction.Location.field_6317, this.field_17727, blockPos3, EntityType.field_17713)) {
				blockPos2 = blockPos3;
				break;
			}
		}

		return blockPos2;
	}
}
