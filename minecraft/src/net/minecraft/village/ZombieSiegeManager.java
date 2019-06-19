package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;

public class ZombieSiegeManager {
	private final ServerWorld world;
	private boolean spawned;
	private ZombieSiegeManager.State state = ZombieSiegeManager.State.field_18482;
	private int remaining;
	private int countdown;
	private int startX;
	private int startY;
	private int startZ;

	public ZombieSiegeManager(ServerWorld serverWorld) {
		this.world = serverWorld;
	}

	public void tick() {
		if (this.world.isDaylight()) {
			this.state = ZombieSiegeManager.State.field_18482;
			this.spawned = false;
		} else {
			float f = this.world.getSkyAngle(0.0F);
			if ((double)f == 0.5) {
				this.state = this.world.random.nextInt(10) == 0 ? ZombieSiegeManager.State.field_18481 : ZombieSiegeManager.State.field_18482;
			}

			if (this.state != ZombieSiegeManager.State.field_18482) {
				if (!this.spawned) {
					if (!this.spawn()) {
						return;
					}

					this.spawned = true;
				}

				if (this.countdown > 0) {
					this.countdown--;
				} else {
					this.countdown = 2;
					if (this.remaining > 0) {
						this.trySpawnZombie();
						this.remaining--;
					} else {
						this.state = ZombieSiegeManager.State.field_18482;
					}
				}
			}
		}
	}

	private boolean spawn() {
		for (PlayerEntity playerEntity : this.world.getPlayers()) {
			if (!playerEntity.isSpectator()) {
				BlockPos blockPos = playerEntity.getBlockPos();
				if (this.world.isNearOccupiedPointOfInterest(blockPos)) {
					for (int i = 0; i < 10; i++) {
						float f = this.world.random.nextFloat() * (float) (Math.PI * 2);
						this.startX = blockPos.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0F);
						this.startY = blockPos.getY();
						this.startZ = blockPos.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0F);
						if (this.getSpawnVector(new BlockPos(this.startX, this.startY, this.startZ)) != null) {
							this.countdown = 0;
							this.remaining = 20;
							break;
						}
					}

					return true;
				}
			}
		}

		return false;
	}

	private void trySpawnZombie() {
		Vec3d vec3d = this.getSpawnVector(new BlockPos(this.startX, this.startY, this.startZ));
		if (vec3d != null) {
			ZombieEntity zombieEntity;
			try {
				zombieEntity = new ZombieEntity(this.world);
				zombieEntity.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(zombieEntity)), SpawnType.field_16467, null, null);
			} catch (Exception var4) {
				var4.printStackTrace();
				return;
			}

			zombieEntity.setPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, this.world.random.nextFloat() * 360.0F, 0.0F);
			this.world.spawnEntity(zombieEntity);
		}
	}

	@Nullable
	private Vec3d getSpawnVector(BlockPos blockPos) {
		for (int i = 0; i < 10; i++) {
			int j = blockPos.getX() + this.world.random.nextInt(16) - 8;
			int k = blockPos.getZ() + this.world.random.nextInt(16) - 8;
			int l = this.world.getTop(Heightmap.Type.field_13202, j, k);
			BlockPos blockPos2 = new BlockPos(j, l, k);
			if (this.world.isNearOccupiedPointOfInterest(blockPos2)
				&& SpawnHelper.canSpawn(SpawnRestriction.Location.field_6317, this.world, blockPos2, EntityType.field_6051)) {
				return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
			}
		}

		return null;
	}

	static enum State {
		field_18480,
		field_18481,
		field_18482;
	}
}
