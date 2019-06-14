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
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MobSpawnerLogic {
	private static final Logger LOGGER = LogManager.getLogger();
	private int spawnDelay = 20;
	private final List<MobSpawnerEntry> spawnPotentials = Lists.<MobSpawnerEntry>newArrayList();
	private MobSpawnerEntry field_9155 = new MobSpawnerEntry();
	private double field_9161;
	private double field_9159;
	private int minSpawnDelay = 200;
	private int maxSpawnDelay = 800;
	private int spawnCount = 4;
	private Entity renderedEntity;
	private int maxNearbyEntities = 6;
	private int requiredPlayerRange = 16;
	private int spawnRange = 4;

	@Nullable
	private Identifier getEntityId() {
		String string = this.field_9155.getEntityTag().getString("id");

		try {
			return ChatUtil.isEmpty(string) ? null : new Identifier(string);
		} catch (InvalidIdentifierException var4) {
			BlockPos blockPos = this.getPos();
			LOGGER.warn(
				"Invalid entity id '{}' at spawner {}:[{},{},{}]", string, this.method_8271().field_9247.method_12460(), blockPos.getX(), blockPos.getY(), blockPos.getZ()
			);
			return null;
		}
	}

	public void setEntityId(EntityType<?> entityType) {
		this.field_9155.getEntityTag().putString("id", Registry.ENTITY_TYPE.getId(entityType).toString());
	}

	private boolean isPlayerInRange() {
		BlockPos blockPos = this.getPos();
		return this.method_8271()
			.isPlayerInRange((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, (double)this.requiredPlayerRange);
	}

	public void update() {
		if (!this.isPlayerInRange()) {
			this.field_9159 = this.field_9161;
		} else {
			World world = this.method_8271();
			BlockPos blockPos = this.getPos();
			if (world.isClient) {
				double d = (double)((float)blockPos.getX() + world.random.nextFloat());
				double e = (double)((float)blockPos.getY() + world.random.nextFloat());
				double f = (double)((float)blockPos.getZ() + world.random.nextFloat());
				world.addParticle(ParticleTypes.field_11251, d, e, f, 0.0, 0.0, 0.0);
				world.addParticle(ParticleTypes.field_11240, d, e, f, 0.0, 0.0, 0.0);
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
					CompoundTag compoundTag = this.field_9155.getEntityTag();
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
					if (world.method_18026(((EntityType)optional.get()).method_17683(g, h, k))
						&& SpawnRestriction.method_20638((EntityType)optional.get(), world.getWorld(), SpawnType.field_16469, new BlockPos(g, h, k), world.getRandom())) {
						Entity entity = EntityType.method_17842(compoundTag, world, entityx -> {
							entityx.setPositionAndAngles(g, h, k, entityx.yaw, entityx.pitch);
							return entityx;
						});
						if (entity == null) {
							this.updateSpawns();
							return;
						}

						int l = world.method_18467(
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

						entity.setPositionAndAngles(entity.x, entity.y, entity.z, world.random.nextFloat() * 360.0F, 0.0F);
						if (entity instanceof MobEntity) {
							MobEntity mobEntity = (MobEntity)entity;
							if (!mobEntity.method_5979(world, SpawnType.field_16469) || !mobEntity.method_5957(world)) {
								continue;
							}

							if (this.field_9155.getEntityTag().getSize() == 1 && this.field_9155.getEntityTag().containsKey("id", 8)) {
								((MobEntity)entity).method_5943(world, world.getLocalDifficulty(new BlockPos(entity)), SpawnType.field_16469, null, null);
							}
						}

						this.spawnEntity(entity);
						world.playLevelEvent(2004, blockPos, 0);
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
		if (this.method_8271().spawnEntity(entity)) {
			for (Entity entity2 : entity.getPassengerList()) {
				this.spawnEntity(entity2);
			}
		}
	}

	private void updateSpawns() {
		if (this.maxSpawnDelay <= this.minSpawnDelay) {
			this.spawnDelay = this.minSpawnDelay;
		} else {
			this.spawnDelay = this.minSpawnDelay + this.method_8271().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
		}

		if (!this.spawnPotentials.isEmpty()) {
			this.method_8277(WeightedPicker.getRandom(this.method_8271().random, this.spawnPotentials));
		}

		this.sendStatus(1);
	}

	public void deserialize(CompoundTag compoundTag) {
		this.spawnDelay = compoundTag.getShort("Delay");
		this.spawnPotentials.clear();
		if (compoundTag.containsKey("SpawnPotentials", 9)) {
			ListTag listTag = compoundTag.getList("SpawnPotentials", 10);

			for (int i = 0; i < listTag.size(); i++) {
				this.spawnPotentials.add(new MobSpawnerEntry(listTag.getCompoundTag(i)));
			}
		}

		if (compoundTag.containsKey("SpawnData", 10)) {
			this.method_8277(new MobSpawnerEntry(1, compoundTag.getCompound("SpawnData")));
		} else if (!this.spawnPotentials.isEmpty()) {
			this.method_8277(WeightedPicker.getRandom(this.method_8271().random, this.spawnPotentials));
		}

		if (compoundTag.containsKey("MinSpawnDelay", 99)) {
			this.minSpawnDelay = compoundTag.getShort("MinSpawnDelay");
			this.maxSpawnDelay = compoundTag.getShort("MaxSpawnDelay");
			this.spawnCount = compoundTag.getShort("SpawnCount");
		}

		if (compoundTag.containsKey("MaxNearbyEntities", 99)) {
			this.maxNearbyEntities = compoundTag.getShort("MaxNearbyEntities");
			this.requiredPlayerRange = compoundTag.getShort("RequiredPlayerRange");
		}

		if (compoundTag.containsKey("SpawnRange", 99)) {
			this.spawnRange = compoundTag.getShort("SpawnRange");
		}

		if (this.method_8271() != null) {
			this.renderedEntity = null;
		}
	}

	public CompoundTag serialize(CompoundTag compoundTag) {
		Identifier identifier = this.getEntityId();
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
			compoundTag.put("SpawnData", this.field_9155.getEntityTag().method_10553());
			ListTag listTag = new ListTag();
			if (this.spawnPotentials.isEmpty()) {
				listTag.add(this.field_9155.serialize());
			} else {
				for (MobSpawnerEntry mobSpawnerEntry : this.spawnPotentials) {
					listTag.add(mobSpawnerEntry.serialize());
				}
			}

			compoundTag.put("SpawnPotentials", listTag);
			return compoundTag;
		}
	}

	@Environment(EnvType.CLIENT)
	public Entity getRenderedEntity() {
		if (this.renderedEntity == null) {
			this.renderedEntity = EntityType.method_17842(this.field_9155.getEntityTag(), this.method_8271(), Function.identity());
			if (this.field_9155.getEntityTag().getSize() == 1 && this.field_9155.getEntityTag().containsKey("id", 8) && this.renderedEntity instanceof MobEntity) {
				((MobEntity)this.renderedEntity)
					.method_5943(this.method_8271(), this.method_8271().getLocalDifficulty(new BlockPos(this.renderedEntity)), SpawnType.field_16469, null, null);
			}
		}

		return this.renderedEntity;
	}

	public boolean method_8275(int i) {
		if (i == 1 && this.method_8271().isClient) {
			this.spawnDelay = this.minSpawnDelay;
			return true;
		} else {
			return false;
		}
	}

	public void method_8277(MobSpawnerEntry mobSpawnerEntry) {
		this.field_9155 = mobSpawnerEntry;
	}

	public abstract void sendStatus(int i);

	public abstract World method_8271();

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
