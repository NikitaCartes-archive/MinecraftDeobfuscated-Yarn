package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
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

	@Nullable
	private Identifier getEntityId() {
		String string = this.spawnEntry.getEntityTag().getString("id");

		try {
			return ChatUtil.isEmpty(string) ? null : new Identifier(string);
		} catch (InvalidIdentifierException var4) {
			BlockPos blockPos = this.getPos();
			LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", string, this.getWorld().method_27983(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
			return null;
		}
	}

	public void setEntityId(EntityType<?> type) {
		this.spawnEntry.getEntityTag().putString("id", Registry.ENTITY_TYPE.getId(type).toString());
	}

	private boolean isPlayerInRange() {
		BlockPos blockPos = this.getPos();
		return this.getWorld()
			.isPlayerInRange((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, (double)this.requiredPlayerRange);
	}

	public void update() {
		if (!this.isPlayerInRange()) {
			this.field_9159 = this.field_9161;
		} else {
			World world = this.getWorld();
			BlockPos blockPos = this.getPos();
			if (world.isClient) {
				double d = (double)blockPos.getX() + (double)world.random.nextFloat();
				double e = (double)blockPos.getY() + (double)world.random.nextFloat();
				double f = (double)blockPos.getZ() + (double)world.random.nextFloat();
				world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
				world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
				if (this.spawnDelay > 0) {
					this.spawnDelay--;
				}

				this.field_9159 = this.field_9161;
				this.field_9161 = (this.field_9161 + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0;
			} else {
				if (this.spawnDelay == -1) {
					this.updateSpawns();
				}

				if (this.spawnDelay > 0) {
					this.spawnDelay--;
					return;
				}

				boolean bl = false;

				for (int i = 0; i < this.spawnCount; i++) {
					CompoundTag compoundTag = this.spawnEntry.getEntityTag();
					Optional<EntityType<?>> optional = EntityType.fromTag(compoundTag);
					if (!optional.isPresent()) {
						this.updateSpawns();
						return;
					}

					ListTag listTag = compoundTag.getList("Pos", 6);
					int j = listTag.size();
					double g = j >= 1
						? listTag.getDouble(0)
						: (double)blockPos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
					double h = j >= 2 ? listTag.getDouble(1) : (double)(blockPos.getY() + world.random.nextInt(3) - 1);
					double k = j >= 3
						? listTag.getDouble(2)
						: (double)blockPos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
					if (world.doesNotCollide(((EntityType)optional.get()).createSimpleBoundingBox(g, h, k))
						&& SpawnRestriction.canSpawn((EntityType)optional.get(), world.getWorld(), SpawnReason.SPAWNER, new BlockPos(g, h, k), world.getRandom())) {
						Entity entity = EntityType.loadEntityWithPassengers(compoundTag, world, entityx -> {
							entityx.refreshPositionAndAngles(g, h, k, entityx.yaw, entityx.pitch);
							return entityx;
						});
						if (entity == null) {
							this.updateSpawns();
							return;
						}

						int l = world.getNonSpectatingEntities(
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
						if (l >= this.maxNearbyEntities) {
							this.updateSpawns();
							return;
						}

						entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), world.random.nextFloat() * 360.0F, 0.0F);
						if (entity instanceof MobEntity) {
							MobEntity mobEntity = (MobEntity)entity;
							if (!mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) {
								continue;
							}

							if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().contains("id", 8)) {
								((MobEntity)entity).initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, null, null);
							}
						}

						this.spawnEntity(entity);
						world.syncWorldEvent(2004, blockPos, 0);
						if (entity instanceof MobEntity) {
							((MobEntity)entity).playSpawnEffects();
						}

						bl = true;
					}
				}

				if (bl) {
					this.updateSpawns();
				}
			}
		}
	}

	private void spawnEntity(Entity entity) {
		if (this.getWorld().spawnEntity(entity)) {
			for (Entity entity2 : entity.getPassengerList()) {
				this.spawnEntity(entity2);
			}
		}
	}

	private void updateSpawns() {
		if (this.maxSpawnDelay <= this.minSpawnDelay) {
			this.spawnDelay = this.minSpawnDelay;
		} else {
			this.spawnDelay = this.minSpawnDelay + this.getWorld().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
		}

		if (!this.spawnPotentials.isEmpty()) {
			this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
		}

		this.sendStatus(1);
	}

	public void fromTag(CompoundTag tag) {
		this.spawnDelay = tag.getShort("Delay");
		this.spawnPotentials.clear();
		if (tag.contains("SpawnPotentials", 9)) {
			ListTag listTag = tag.getList("SpawnPotentials", 10);

			for (int i = 0; i < listTag.size(); i++) {
				this.spawnPotentials.add(new MobSpawnerEntry(listTag.getCompound(i)));
			}
		}

		if (tag.contains("SpawnData", 10)) {
			this.setSpawnEntry(new MobSpawnerEntry(1, tag.getCompound("SpawnData")));
		} else if (!this.spawnPotentials.isEmpty()) {
			this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
		}

		if (tag.contains("MinSpawnDelay", 99)) {
			this.minSpawnDelay = tag.getShort("MinSpawnDelay");
			this.maxSpawnDelay = tag.getShort("MaxSpawnDelay");
			this.spawnCount = tag.getShort("SpawnCount");
		}

		if (tag.contains("MaxNearbyEntities", 99)) {
			this.maxNearbyEntities = tag.getShort("MaxNearbyEntities");
			this.requiredPlayerRange = tag.getShort("RequiredPlayerRange");
		}

		if (tag.contains("SpawnRange", 99)) {
			this.spawnRange = tag.getShort("SpawnRange");
		}

		if (this.getWorld() != null) {
			this.renderedEntity = null;
		}
	}

	public CompoundTag toTag(CompoundTag tag) {
		Identifier identifier = this.getEntityId();
		if (identifier == null) {
			return tag;
		} else {
			tag.putShort("Delay", (short)this.spawnDelay);
			tag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
			tag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
			tag.putShort("SpawnCount", (short)this.spawnCount);
			tag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
			tag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
			tag.putShort("SpawnRange", (short)this.spawnRange);
			tag.put("SpawnData", this.spawnEntry.getEntityTag().copy());
			ListTag listTag = new ListTag();
			if (this.spawnPotentials.isEmpty()) {
				listTag.add(this.spawnEntry.serialize());
			} else {
				for (MobSpawnerEntry mobSpawnerEntry : this.spawnPotentials) {
					listTag.add(mobSpawnerEntry.serialize());
				}
			}

			tag.put("SpawnPotentials", listTag);
			return tag;
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Entity getRenderedEntity() {
		if (this.renderedEntity == null) {
			this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getEntityTag(), this.getWorld(), Function.identity());
			if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().contains("id", 8) && this.renderedEntity instanceof MobEntity) {
				((MobEntity)this.renderedEntity)
					.initialize(this.getWorld(), this.getWorld().getLocalDifficulty(this.renderedEntity.getBlockPos()), SpawnReason.SPAWNER, null, null);
			}
		}

		return this.renderedEntity;
	}

	public boolean method_8275(int i) {
		if (i == 1 && this.getWorld().isClient) {
			this.spawnDelay = this.minSpawnDelay;
			return true;
		} else {
			return false;
		}
	}

	public void setSpawnEntry(MobSpawnerEntry spawnEntry) {
		this.spawnEntry = spawnEntry;
	}

	public abstract void sendStatus(int status);

	public abstract World getWorld();

	public abstract BlockPos getPos();

	@Environment(EnvType.CLIENT)
	public double method_8278() {
		return this.field_9161;
	}

	@Environment(EnvType.CLIENT)
	public double method_8279() {
		return this.field_9159;
	}
}
