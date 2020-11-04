package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MobSpawnerLogic {
	private static final Logger LOGGER = LogManager.getLogger();
	private int spawnDelay = 20;
	private final List<MobSpawnerEntry> spawnPotentials = Lists.<MobSpawnerEntry>newArrayList();
	private MobSpawnerEntry spawnEntry = new MobSpawnerEntry();
	private double field_9161;
	private double field_9159;
	private int minSpawnDelay = 200;
	private int maxSpawnDelay = 800;
	private int spawnCount = 4;
	@Nullable
	private Entity renderedEntity;
	private int maxNearbyEntities = 6;
	private int requiredPlayerRange = 16;
	private int spawnRange = 4;
	private final Random field_27080 = new Random();

	@Nullable
	private Identifier getEntityId(@Nullable World world, BlockPos blockPos) {
		String string = this.spawnEntry.getEntityTag().getString("id");

		try {
			return ChatUtil.isEmpty(string) ? null : new Identifier(string);
		} catch (InvalidIdentifierException var5) {
			LOGGER.warn(
				"Invalid entity id '{}' at spawner {}:[{},{},{}]",
				string,
				world != null ? world.getRegistryKey().getValue() : "<null>",
				blockPos.getX(),
				blockPos.getY(),
				blockPos.getZ()
			);
			return null;
		}
	}

	public void setEntityId(EntityType<?> type) {
		this.spawnEntry.getEntityTag().putString("id", Registry.ENTITY_TYPE.getId(type).toString());
	}

	private boolean isPlayerInRange(World world, BlockPos blockPos) {
		return world.isPlayerInRange((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, (double)this.requiredPlayerRange);
	}

	public void method_31589(World world, BlockPos blockPos) {
		if (!this.isPlayerInRange(world, blockPos)) {
			this.field_9159 = this.field_9161;
		} else {
			double d = (double)blockPos.getX() + world.random.nextDouble();
			double e = (double)blockPos.getY() + world.random.nextDouble();
			double f = (double)blockPos.getZ() + world.random.nextDouble();
			world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
			world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
			if (this.spawnDelay > 0) {
				this.spawnDelay--;
			}

			this.field_9159 = this.field_9161;
			this.field_9161 = (this.field_9161 + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0;
		}
	}

	public void method_31588(ServerWorld serverWorld, BlockPos blockPos) {
		if (this.isPlayerInRange(serverWorld, blockPos)) {
			if (this.spawnDelay == -1) {
				this.updateSpawns(serverWorld, blockPos);
			}

			if (this.spawnDelay > 0) {
				this.spawnDelay--;
			} else {
				boolean bl = false;

				for (int i = 0; i < this.spawnCount; i++) {
					CompoundTag compoundTag = this.spawnEntry.getEntityTag();
					Optional<EntityType<?>> optional = EntityType.fromTag(compoundTag);
					if (!optional.isPresent()) {
						this.updateSpawns(serverWorld, blockPos);
						return;
					}

					ListTag listTag = compoundTag.getList("Pos", 6);
					int j = listTag.size();
					double d = j >= 1
						? listTag.getDouble(0)
						: (double)blockPos.getX() + (serverWorld.random.nextDouble() - serverWorld.random.nextDouble()) * (double)this.spawnRange + 0.5;
					double e = j >= 2 ? listTag.getDouble(1) : (double)(blockPos.getY() + serverWorld.random.nextInt(3) - 1);
					double f = j >= 3
						? listTag.getDouble(2)
						: (double)blockPos.getZ() + (serverWorld.random.nextDouble() - serverWorld.random.nextDouble()) * (double)this.spawnRange + 0.5;
					if (serverWorld.isSpaceEmpty(((EntityType)optional.get()).createSimpleBoundingBox(d, e, f))
						&& SpawnRestriction.canSpawn((EntityType)optional.get(), serverWorld, SpawnReason.SPAWNER, new BlockPos(d, e, f), serverWorld.getRandom())) {
						Entity entity = EntityType.loadEntityWithPassengers(compoundTag, serverWorld, entityx -> {
							entityx.refreshPositionAndAngles(d, e, f, entityx.yaw, entityx.pitch);
							return entityx;
						});
						if (entity == null) {
							this.updateSpawns(serverWorld, blockPos);
							return;
						}

						int k = serverWorld.getNonSpectatingEntities(
								entity.getClass(),
								new Box(
										(double)blockPos.getX(),
										(double)blockPos.getY(),
										(double)blockPos.getZ(),
										(double)(blockPos.getX() + 1),
										(double)(blockPos.getY() + 1),
										(double)(blockPos.getZ() + 1)
									)
									.expand((double)this.spawnRange)
							)
							.size();
						if (k >= this.maxNearbyEntities) {
							this.updateSpawns(serverWorld, blockPos);
							return;
						}

						entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), serverWorld.random.nextFloat() * 360.0F, 0.0F);
						if (entity instanceof MobEntity) {
							MobEntity mobEntity = (MobEntity)entity;
							if (!mobEntity.canSpawn(serverWorld, SpawnReason.SPAWNER) || !mobEntity.canSpawn(serverWorld)) {
								continue;
							}

							if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().contains("id", 8)) {
								((MobEntity)entity).initialize(serverWorld, serverWorld.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, null, null);
							}
						}

						if (!serverWorld.shouldCreateNewEntityWithPassenger(entity)) {
							this.updateSpawns(serverWorld, blockPos);
							return;
						}

						serverWorld.syncWorldEvent(2004, blockPos, 0);
						if (entity instanceof MobEntity) {
							((MobEntity)entity).playSpawnEffects();
						}

						bl = true;
					}
				}

				if (bl) {
					this.updateSpawns(serverWorld, blockPos);
				}
			}
		}
	}

	private void updateSpawns(World world, BlockPos blockPos) {
		if (this.maxSpawnDelay <= this.minSpawnDelay) {
			this.spawnDelay = this.minSpawnDelay;
		} else {
			this.spawnDelay = this.minSpawnDelay + this.field_27080.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
		}

		if (!this.spawnPotentials.isEmpty()) {
			this.setSpawnEntry(world, blockPos, WeightedPicker.getRandom(this.field_27080, this.spawnPotentials));
		}

		this.sendStatus(world, blockPos, 1);
	}

	public void fromTag(@Nullable World world, BlockPos blockPos, CompoundTag compoundTag) {
		this.spawnDelay = compoundTag.getShort("Delay");
		this.spawnPotentials.clear();
		if (compoundTag.contains("SpawnPotentials", 9)) {
			ListTag listTag = compoundTag.getList("SpawnPotentials", 10);

			for (int i = 0; i < listTag.size(); i++) {
				this.spawnPotentials.add(new MobSpawnerEntry(listTag.getCompound(i)));
			}
		}

		if (compoundTag.contains("SpawnData", 10)) {
			this.setSpawnEntry(world, blockPos, new MobSpawnerEntry(1, compoundTag.getCompound("SpawnData")));
		} else if (!this.spawnPotentials.isEmpty()) {
			this.setSpawnEntry(world, blockPos, WeightedPicker.getRandom(this.field_27080, this.spawnPotentials));
		}

		if (compoundTag.contains("MinSpawnDelay", 99)) {
			this.minSpawnDelay = compoundTag.getShort("MinSpawnDelay");
			this.maxSpawnDelay = compoundTag.getShort("MaxSpawnDelay");
			this.spawnCount = compoundTag.getShort("SpawnCount");
		}

		if (compoundTag.contains("MaxNearbyEntities", 99)) {
			this.maxNearbyEntities = compoundTag.getShort("MaxNearbyEntities");
			this.requiredPlayerRange = compoundTag.getShort("RequiredPlayerRange");
		}

		if (compoundTag.contains("SpawnRange", 99)) {
			this.spawnRange = compoundTag.getShort("SpawnRange");
		}

		this.renderedEntity = null;
	}

	public CompoundTag toTag(@Nullable World world, BlockPos blockPos, CompoundTag compoundTag) {
		Identifier identifier = this.getEntityId(world, blockPos);
		if (identifier == null) {
			return compoundTag;
		} else {
			compoundTag.putShort("Delay", (short)this.spawnDelay);
			compoundTag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
			compoundTag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
			compoundTag.putShort("SpawnCount", (short)this.spawnCount);
			compoundTag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
			compoundTag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
			compoundTag.putShort("SpawnRange", (short)this.spawnRange);
			compoundTag.put("SpawnData", this.spawnEntry.getEntityTag().copy());
			ListTag listTag = new ListTag();
			if (this.spawnPotentials.isEmpty()) {
				listTag.add(this.spawnEntry.serialize());
			} else {
				for (MobSpawnerEntry mobSpawnerEntry : this.spawnPotentials) {
					listTag.add(mobSpawnerEntry.serialize());
				}
			}

			compoundTag.put("SpawnPotentials", listTag);
			return compoundTag;
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Entity getRenderedEntity(World world) {
		if (this.renderedEntity == null) {
			this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getEntityTag(), world, Function.identity());
			if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().contains("id", 8) && this.renderedEntity instanceof MobEntity) {
			}
		}

		return this.renderedEntity;
	}

	public boolean method_8275(World world, int i) {
		if (i == 1) {
			if (world.isClient) {
				this.spawnDelay = this.minSpawnDelay;
			}

			return true;
		} else {
			return false;
		}
	}

	public void setSpawnEntry(@Nullable World world, BlockPos blockPos, MobSpawnerEntry mobSpawnerEntry) {
		this.spawnEntry = mobSpawnerEntry;
	}

	public abstract void sendStatus(World world, BlockPos blockPos, int i);

	@Environment(EnvType.CLIENT)
	public double method_8278() {
		return this.field_9161;
	}

	@Environment(EnvType.CLIENT)
	public double method_8279() {
		return this.field_9159;
	}
}
