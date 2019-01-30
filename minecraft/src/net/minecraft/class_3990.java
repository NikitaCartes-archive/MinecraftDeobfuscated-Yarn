package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sortme.SpawnHelper;
import net.minecraft.sortme.SpawnRestriction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.LevelProperties;

public class class_3990 {
	private final Random field_17726 = new Random();
	private final World field_17727;
	private int field_17728;
	private int field_17729;
	private int field_17730;

	public class_3990(World world) {
		this.field_17727 = world;
		this.field_17728 = 1200;
		LevelProperties levelProperties = world.getLevelProperties();
		this.field_17729 = levelProperties.method_18038();
		this.field_17730 = levelProperties.method_18039();
		if (this.field_17729 == 0 && this.field_17730 == 0) {
			this.field_17729 = 24000;
			levelProperties.method_18041(this.field_17729);
			this.field_17730 = 25;
			levelProperties.method_18042(this.field_17730);
		}
	}

	public void method_18015() {
		if (--this.field_17728 <= 0) {
			this.field_17728 = 1200;
			LevelProperties levelProperties = this.field_17727.getLevelProperties();
			this.field_17729 -= 1200;
			levelProperties.method_18041(this.field_17729);
			if (this.field_17729 <= 0) {
				this.field_17729 = 24000;
				int i = this.field_17730;
				this.field_17730 = MathHelper.clamp(this.field_17730 + 25, 25, 75);
				levelProperties.method_18042(this.field_17730);
				if (this.field_17726.nextInt(100) <= i) {
					if (this.method_18018()) {
						this.field_17730 = 25;
					}
				}
			}
		}
	}

	private boolean method_18018() {
		PlayerEntity playerEntity = this.field_17727.method_18022();
		if (playerEntity == null) {
			return true;
		} else if (this.field_17726.nextInt(10) != 0) {
			return false;
		} else {
			BlockPos blockPos = playerEntity.getPos();
			BlockPos blockPos2 = this.method_18017(blockPos, 48);
			if (blockPos2 != null) {
				WanderingTraderEntity wanderingTraderEntity = EntityType.field_17713
					.spawn(this.field_17727, null, null, null, blockPos2, SpawnType.field_16467, false, false);
				if (wanderingTraderEntity != null) {
					for (int i = 0; i < 2; i++) {
						this.method_18016(wanderingTraderEntity, 8);
					}

					this.field_17727.getLevelProperties().method_18040(wanderingTraderEntity.getUuid());
					wanderingTraderEntity.method_18013(48000);
					return true;
				}
			}

			return false;
		}
	}

	private void method_18016(WanderingTraderEntity wanderingTraderEntity, int i) {
		BlockPos blockPos = this.method_18017(new BlockPos(wanderingTraderEntity), i);
		if (blockPos != null) {
			class_3986 lv = EntityType.field_17714.spawn(this.field_17727, null, null, null, blockPos, SpawnType.field_16467, false, false);
			if (lv != null) {
				lv.attachLeash(wanderingTraderEntity, true);
				lv.method_18005(47999);
			}
		}
	}

	@Nullable
	private BlockPos method_18017(BlockPos blockPos, int i) {
		BlockPos blockPos2 = null;

		for (int j = 0; j < 10; j++) {
			int k = blockPos.getX() + this.field_17726.nextInt(i * 2) - i;
			int l = blockPos.getZ() + this.field_17726.nextInt(i * 2) - i;
			int m = this.field_17727.getTop(Heightmap.Type.WORLD_SURFACE, k, l);
			BlockPos blockPos3 = new BlockPos(k, m, l);
			if (SpawnHelper.canSpawn(SpawnRestriction.Location.field_6317, this.field_17727, blockPos3, EntityType.field_17713)) {
				blockPos2 = blockPos3;
				break;
			}
		}

		return blockPos2;
	}
}
