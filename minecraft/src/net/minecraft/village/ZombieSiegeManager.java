package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;

public class ZombieSiegeManager {
	private boolean spawned;
	private ZombieSiegeManager.State state = ZombieSiegeManager.State.field_18482;
	private int remaining;
	private int countdown;
	private int startX;
	private int startY;
	private int startZ;

	public int tick(ServerWorld serverWorld, boolean bl, boolean bl2) {
		if (!serverWorld.isDaylight() && bl) {
			float f = serverWorld.getSkyAngle(0.0F);
			if ((double)f == 0.5) {
				this.state = serverWorld.random.nextInt(10) == 0 ? ZombieSiegeManager.State.field_18481 : ZombieSiegeManager.State.field_18482;
			}

			if (this.state == ZombieSiegeManager.State.field_18482) {
				return 0;
			} else {
				if (!this.spawned) {
					if (!this.spawn(serverWorld)) {
						return 0;
					}

					this.spawned = true;
				}

				if (this.countdown > 0) {
					this.countdown--;
					return 0;
				} else {
					this.countdown = 2;
					if (this.remaining > 0) {
						this.trySpawnZombie(serverWorld);
						this.remaining--;
					} else {
						this.state = ZombieSiegeManager.State.field_18482;
					}

					return 1;
				}
			}
		} else {
			this.state = ZombieSiegeManager.State.field_18482;
			this.spawned = false;
			return 0;
		}
	}

	private boolean spawn(ServerWorld serverWorld) {
		for (PlayerEntity playerEntity : serverWorld.getPlayers()) {
			if (!playerEntity.isSpectator()) {
				BlockPos blockPos = playerEntity.getBlockPos();
				if (serverWorld.isNearOccupiedPointOfInterest(blockPos) && serverWorld.getBiome(blockPos).getCategory() != Biome.Category.field_9365) {
					for (int i = 0; i < 10; i++) {
						float f = serverWorld.random.nextFloat() * (float) (Math.PI * 2);
						this.startX = blockPos.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0F);
						this.startY = blockPos.getY();
						this.startZ = blockPos.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0F);
						if (this.getSpawnVector(serverWorld, new BlockPos(this.startX, this.startY, this.startZ)) != null) {
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

	private void trySpawnZombie(ServerWorld serverWorld) {
		Vec3d vec3d = this.getSpawnVector(serverWorld, new BlockPos(this.startX, this.startY, this.startZ));
		if (vec3d != null) {
			ZombieEntity zombieEntity;
			try {
				zombieEntity = new ZombieEntity(serverWorld);
				zombieEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(new BlockPos(zombieEntity)), SpawnType.field_16467, null, null);
			} catch (Exception var5) {
				var5.printStackTrace();
				return;
			}

			zombieEntity.setPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, serverWorld.random.nextFloat() * 360.0F, 0.0F);
			serverWorld.spawnEntity(zombieEntity);
		}
	}

	@Nullable
	private Vec3d getSpawnVector(ServerWorld serverWorld, BlockPos blockPos) {
		for (int i = 0; i < 10; i++) {
			int j = blockPos.getX() + serverWorld.random.nextInt(16) - 8;
			int k = blockPos.getZ() + serverWorld.random.nextInt(16) - 8;
			int l = serverWorld.getTop(Heightmap.Type.field_13202, j, k);
			BlockPos blockPos2 = new BlockPos(j, l, k);
			if (serverWorld.isNearOccupiedPointOfInterest(blockPos2)
				&& HostileEntity.method_20680(EntityType.field_6051, serverWorld, SpawnType.field_16467, blockPos2, serverWorld.random)) {
				return new Vec3d((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
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
