package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Spawner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZombieSiegeManager implements Spawner {
	private static final Logger LOGGER = LogManager.getLogger();
	private boolean spawned;
	private ZombieSiegeManager.State state = ZombieSiegeManager.State.SIEGE_DONE;
	private int remaining;
	private int countdown;
	private int startX;
	private int startY;
	private int startZ;

	@Override
	public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		if (!world.isDay() && spawnMonsters) {
			float f = world.getSkyAngle(0.0F);
			if ((double)f == 0.5) {
				this.state = world.random.nextInt(10) == 0 ? ZombieSiegeManager.State.SIEGE_TONIGHT : ZombieSiegeManager.State.SIEGE_DONE;
			}

			if (this.state == ZombieSiegeManager.State.SIEGE_DONE) {
				return 0;
			} else {
				if (!this.spawned) {
					if (!this.spawn(world)) {
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
						this.trySpawnZombie(world);
						this.remaining--;
					} else {
						this.state = ZombieSiegeManager.State.SIEGE_DONE;
					}

					return 1;
				}
			}
		} else {
			this.state = ZombieSiegeManager.State.SIEGE_DONE;
			this.spawned = false;
			return 0;
		}
	}

	private boolean spawn(ServerWorld world) {
		for (PlayerEntity playerEntity : world.getPlayers()) {
			if (!playerEntity.isSpectator()) {
				BlockPos blockPos = playerEntity.getBlockPos();
				if (world.isNearOccupiedPointOfInterest(blockPos) && world.getBiome(blockPos).getCategory() != Biome.Category.MUSHROOM) {
					for (int i = 0; i < 10; i++) {
						float f = world.random.nextFloat() * (float) (Math.PI * 2);
						this.startX = blockPos.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0F);
						this.startY = blockPos.getY();
						this.startZ = blockPos.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0F);
						if (this.getSpawnVector(world, new BlockPos(this.startX, this.startY, this.startZ)) != null) {
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

	private void trySpawnZombie(ServerWorld world) {
		Vec3d vec3d = this.getSpawnVector(world, new BlockPos(this.startX, this.startY, this.startZ));
		if (vec3d != null) {
			ZombieEntity zombieEntity;
			try {
				zombieEntity = new ZombieEntity(world);
				zombieEntity.initialize(world, world.getLocalDifficulty(zombieEntity.getBlockPos()), SpawnReason.EVENT, null, null);
			} catch (Exception var5) {
				LOGGER.warn("Failed to create zombie for village siege at {}", vec3d, var5);
				return;
			}

			zombieEntity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, world.random.nextFloat() * 360.0F, 0.0F);
			world.spawnEntityAndPassengers(zombieEntity);
		}
	}

	@Nullable
	private Vec3d getSpawnVector(ServerWorld world, BlockPos pos) {
		for (int i = 0; i < 10; i++) {
			int j = pos.getX() + world.random.nextInt(16) - 8;
			int k = pos.getZ() + world.random.nextInt(16) - 8;
			int l = world.getTopY(Heightmap.Type.WORLD_SURFACE, j, k);
			BlockPos blockPos = new BlockPos(j, l, k);
			if (world.isNearOccupiedPointOfInterest(blockPos) && HostileEntity.canSpawnInDark(EntityType.ZOMBIE, world, SpawnReason.EVENT, blockPos, world.random)) {
				return Vec3d.ofBottomCenter(blockPos);
			}
		}

		return null;
	}

	static enum State {
		SIEGE_CAN_ACTIVATE,
		SIEGE_TONIGHT,
		SIEGE_DONE;
	}
}
