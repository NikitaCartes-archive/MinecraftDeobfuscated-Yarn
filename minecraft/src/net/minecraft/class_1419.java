package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sortme.SpawnHelper;
import net.minecraft.sortme.SpawnRestriction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.World;

public class class_1419 {
	private final World field_6727;
	private boolean field_6725;
	private int field_6724 = -1;
	private int field_6723;
	private int field_6722;
	private VillageProperties field_6726;
	private int field_6721;
	private int field_6720;
	private int field_6719;

	public class_1419(World world) {
		this.field_6727 = world;
	}

	public void method_6445() {
		if (this.field_6727.isDaylight()) {
			this.field_6724 = 0;
		} else if (this.field_6724 != 2) {
			if (this.field_6724 == 0) {
				float f = this.field_6727.getSkyAngle(0.0F);
				if ((double)f < 0.5 || (double)f > 0.501) {
					return;
				}

				this.field_6724 = this.field_6727.random.nextInt(10) == 0 ? 1 : 2;
				this.field_6725 = false;
				if (this.field_6724 == 2) {
					return;
				}
			}

			if (this.field_6724 != -1) {
				if (!this.field_6725) {
					if (!this.method_6446()) {
						return;
					}

					this.field_6725 = true;
				}

				if (this.field_6722 > 0) {
					this.field_6722--;
				} else {
					this.field_6722 = 2;
					if (this.field_6723 > 0) {
						this.method_6447();
						this.field_6723--;
					} else {
						this.field_6724 = 2;
					}
				}
			}
		}
	}

	private boolean method_6446() {
		for (PlayerEntity playerEntity : this.field_6727.method_18456()) {
			if (!playerEntity.isSpectator()) {
				this.field_6726 = this.field_6727.getVillageManager().getNearestVillage(new BlockPos(playerEntity), 1);
				if (this.field_6726 != null && this.field_6726.getDoorCount() >= 10 && this.field_6726.getStableTicks() >= 20 && this.field_6726.getPopulationSize() >= 20) {
					BlockPos blockPos = this.field_6726.getCenter();
					float f = (float)this.field_6726.getRadius();
					boolean bl = false;

					for (int i = 0; i < 10; i++) {
						float g = this.field_6727.random.nextFloat() * (float) (Math.PI * 2);
						this.field_6721 = blockPos.getX() + (int)((double)(MathHelper.cos(g) * f) * 0.9);
						this.field_6720 = blockPos.getY();
						this.field_6719 = blockPos.getZ() + (int)((double)(MathHelper.sin(g) * f) * 0.9);
						bl = false;

						for (VillageProperties villageProperties : this.field_6727.getVillageManager().getVillages()) {
							if (villageProperties != this.field_6726 && villageProperties.isInRadius(new BlockPos(this.field_6721, this.field_6720, this.field_6719))) {
								bl = true;
								break;
							}
						}

						if (!bl) {
							break;
						}
					}

					if (bl) {
						return false;
					}

					Vec3d vec3d = this.method_6448(new BlockPos(this.field_6721, this.field_6720, this.field_6719));
					if (vec3d != null) {
						this.field_6722 = 0;
						this.field_6723 = 20;
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean method_6447() {
		Vec3d vec3d = this.method_6448(new BlockPos(this.field_6721, this.field_6720, this.field_6719));
		if (vec3d == null) {
			return false;
		} else {
			ZombieEntity zombieEntity;
			try {
				zombieEntity = new ZombieEntity(this.field_6727);
				zombieEntity.prepareEntityData(this.field_6727, this.field_6727.getLocalDifficulty(new BlockPos(zombieEntity)), SpawnType.field_16467, null, null);
			} catch (Exception var4) {
				var4.printStackTrace();
				return false;
			}

			zombieEntity.setPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, this.field_6727.random.nextFloat() * 360.0F, 0.0F);
			this.field_6727.spawnEntity(zombieEntity);
			BlockPos blockPos = this.field_6726.getCenter();
			zombieEntity.method_18408(blockPos, this.field_6726.getRadius());
			return true;
		}
	}

	@Nullable
	private Vec3d method_6448(BlockPos blockPos) {
		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(this.field_6727.random.nextInt(16) - 8, this.field_6727.random.nextInt(6) - 3, this.field_6727.random.nextInt(16) - 8);
			if (this.field_6726.isInRadius(blockPos2) && SpawnHelper.canSpawn(SpawnRestriction.Location.field_6317, this.field_6727, blockPos2, null)) {
				return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
			}
		}

		return null;
	}
}
