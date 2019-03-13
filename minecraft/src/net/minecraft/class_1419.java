package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.SpawnHelper;
import net.minecraft.sortme.SpawnRestriction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_1419 {
	private final ServerWorld field_6727;
	private boolean field_6725;
	private class_1419.class_4152 field_18479 = class_1419.class_4152.field_18482;
	private int field_6723;
	private int field_6722;
	private int field_6721;
	private int field_6720;
	private int field_6719;

	public class_1419(ServerWorld serverWorld) {
		this.field_6727 = serverWorld;
	}

	public void method_6445() {
		if (this.field_6727.isDaylight()) {
			this.field_18479 = class_1419.class_4152.field_18482;
			this.field_6725 = false;
		} else {
			float f = this.field_6727.getSkyAngle(0.0F);
			if ((double)f == 0.5) {
				this.field_18479 = this.field_6727.random.nextInt(10) == 0 ? class_1419.class_4152.field_18481 : class_1419.class_4152.field_18482;
			}

			if (this.field_18479 != class_1419.class_4152.field_18482) {
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
						this.field_18479 = class_1419.class_4152.field_18482;
					}
				}
			}
		}
	}

	private boolean method_6446() {
		for (PlayerEntity playerEntity : this.field_6727.getPlayers()) {
			if (!playerEntity.isSpectator()) {
				BlockPos blockPos = new BlockPos(playerEntity);
				if (this.field_6727.method_19500(blockPos)) {
					for (int i = 0; i < 10; i++) {
						float f = this.field_6727.random.nextFloat() * (float) (Math.PI * 2);
						this.field_6721 = blockPos.getX() + (int)(MathHelper.cos(f) * 32.0F);
						this.field_6720 = blockPos.getY();
						this.field_6719 = blockPos.getZ() + (int)(MathHelper.sin(f) * 32.0F);
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

	private void method_6447() {
		Vec3d vec3d = this.method_6448(new BlockPos(this.field_6721, this.field_6720, this.field_6719));
		if (vec3d != null) {
			ZombieEntity zombieEntity;
			try {
				zombieEntity = new ZombieEntity(this.field_6727);
				zombieEntity.method_5943(this.field_6727, this.field_6727.method_8404(new BlockPos(zombieEntity)), SpawnType.field_16467, null, null);
			} catch (Exception var4) {
				var4.printStackTrace();
				return;
			}

			zombieEntity.setPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, this.field_6727.random.nextFloat() * 360.0F, 0.0F);
			this.field_6727.spawnEntity(zombieEntity);
		}
	}

	@Nullable
	private Vec3d method_6448(BlockPos blockPos) {
		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(this.field_6727.random.nextInt(16) - 8, this.field_6727.random.nextInt(6) - 3, this.field_6727.random.nextInt(16) - 8);
			if (this.field_6727.method_19500(blockPos2) && SpawnHelper.method_8660(SpawnRestriction.Location.field_6317, this.field_6727, blockPos2, null)) {
				return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
			}
		}

		return null;
	}

	static enum class_4152 {
		field_18480,
		field_18481,
		field_18482;
	}
}
