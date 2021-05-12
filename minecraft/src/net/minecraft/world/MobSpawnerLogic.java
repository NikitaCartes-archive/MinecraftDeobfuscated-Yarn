package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MobSpawnerLogic {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_30951 = 1;
	private static Pool<MobSpawnerEntry> field_30952 = Pool.empty();
	private int spawnDelay = 20;
	private Pool<MobSpawnerEntry> spawnPotentials = field_30952;
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
	private final Random random = new Random();

	@Nullable
	private Identifier getEntityId(@Nullable World world, BlockPos pos) {
		String string = this.spawnEntry.getEntityNbt().getString("id");

		try {
			return ChatUtil.isEmpty(string) ? null : new Identifier(string);
		} catch (InvalidIdentifierException var5) {
			LOGGER.warn(
				"Invalid entity id '{}' at spawner {}:[{},{},{}]", string, world != null ? world.getRegistryKey().getValue() : "<null>", pos.getX(), pos.getY(), pos.getZ()
			);
			return null;
		}
	}

	public void setEntityId(EntityType<?> type) {
		this.spawnEntry.getEntityNbt().putString("id", Registry.ENTITY_TYPE.getId(type).toString());
	}

	private boolean isPlayerInRange(World world, BlockPos pos) {
		return world.isPlayerInRange((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (double)this.requiredPlayerRange);
	}

	public void clientTick(World world, BlockPos pos) {
		if (!this.isPlayerInRange(world, pos)) {
			this.field_9159 = this.field_9161;
		} else {
			double d = (double)pos.getX() + world.random.nextDouble();
			double e = (double)pos.getY() + world.random.nextDouble();
			double f = (double)pos.getZ() + world.random.nextDouble();
			world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
			world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
			if (this.spawnDelay > 0) {
				this.spawnDelay--;
			}

			this.field_9159 = this.field_9161;
			this.field_9161 = (this.field_9161 + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0;
		}
	}

	public void serverTick(ServerWorld world, BlockPos pos) {
		if (this.isPlayerInRange(world, pos)) {
			if (this.spawnDelay == -1) {
				this.updateSpawns(world, pos);
			}

			if (this.spawnDelay > 0) {
				this.spawnDelay--;
			} else {
				boolean bl = false;

				for (int i = 0; i < this.spawnCount; i++) {
					NbtCompound nbtCompound = this.spawnEntry.getEntityNbt();
					Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);
					if (!optional.isPresent()) {
						this.updateSpawns(world, pos);
						return;
					}

					NbtList nbtList = nbtCompound.getList("Pos", NbtElement.DOUBLE_TYPE);
					int j = nbtList.size();
					double d = j >= 1 ? nbtList.getDouble(0) : (double)pos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
					double e = j >= 2 ? nbtList.getDouble(1) : (double)(pos.getY() + world.random.nextInt(3) - 1);
					double f = j >= 3 ? nbtList.getDouble(2) : (double)pos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
					if (world.isSpaceEmpty(((EntityType)optional.get()).createSimpleBoundingBox(d, e, f))
						&& SpawnRestriction.canSpawn((EntityType)optional.get(), world, SpawnReason.SPAWNER, new BlockPos(d, e, f), world.getRandom())) {
						Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, world, entityx -> {
							entityx.refreshPositionAndAngles(d, e, f, entityx.getYaw(), entityx.getPitch());
							return entityx;
						});
						if (entity == null) {
							this.updateSpawns(world, pos);
							return;
						}

						int k = world.getNonSpectatingEntities(
								entity.getClass(),
								new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))
									.expand((double)this.spawnRange)
							)
							.size();
						if (k >= this.maxNearbyEntities) {
							this.updateSpawns(world, pos);
							return;
						}

						entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), world.random.nextFloat() * 360.0F, 0.0F);
						if (entity instanceof MobEntity mobEntity) {
							if (!mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) {
								continue;
							}

							if (this.spawnEntry.getEntityNbt().getSize() == 1 && this.spawnEntry.getEntityNbt().contains("id", NbtElement.STRING_TYPE)) {
								((MobEntity)entity).initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, null, null);
							}
						}

						if (!world.shouldCreateNewEntityWithPassenger(entity)) {
							this.updateSpawns(world, pos);
							return;
						}

						world.syncWorldEvent(WorldEvents.SPAWNER_SPAWNS_MOB, pos, 0);
						if (entity instanceof MobEntity) {
							((MobEntity)entity).playSpawnEffects();
						}

						bl = true;
					}
				}

				if (bl) {
					this.updateSpawns(world, pos);
				}
			}
		}
	}

	private void updateSpawns(World world, BlockPos pos) {
		if (this.maxSpawnDelay <= this.minSpawnDelay) {
			this.spawnDelay = this.minSpawnDelay;
		} else {
			this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
		}

		this.spawnPotentials.getOrEmpty(this.random).ifPresent(mobSpawnerEntry -> this.setSpawnEntry(world, pos, mobSpawnerEntry));
		this.sendStatus(world, pos, 1);
	}

	public void readNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {
		this.spawnDelay = nbt.getShort("Delay");
		List<MobSpawnerEntry> list = Lists.<MobSpawnerEntry>newArrayList();
		if (nbt.contains("SpawnPotentials", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("SpawnPotentials", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < nbtList.size(); i++) {
				list.add(new MobSpawnerEntry(nbtList.getCompound(i)));
			}
		}

		this.spawnPotentials = Pool.of(list);
		if (nbt.contains("SpawnData", NbtElement.COMPOUND_TYPE)) {
			this.setSpawnEntry(world, pos, new MobSpawnerEntry(1, nbt.getCompound("SpawnData")));
		} else if (!list.isEmpty()) {
			this.spawnPotentials.getOrEmpty(this.random).ifPresent(mobSpawnerEntry -> this.setSpawnEntry(world, pos, mobSpawnerEntry));
		}

		if (nbt.contains("MinSpawnDelay", NbtElement.NUMBER_TYPE)) {
			this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
			this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
			this.spawnCount = nbt.getShort("SpawnCount");
		}

		if (nbt.contains("MaxNearbyEntities", NbtElement.NUMBER_TYPE)) {
			this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
			this.requiredPlayerRange = nbt.getShort("RequiredPlayerRange");
		}

		if (nbt.contains("SpawnRange", NbtElement.NUMBER_TYPE)) {
			this.spawnRange = nbt.getShort("SpawnRange");
		}

		this.renderedEntity = null;
	}

	public NbtCompound writeNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {
		Identifier identifier = this.getEntityId(world, pos);
		if (identifier == null) {
			return nbt;
		} else {
			nbt.putShort("Delay", (short)this.spawnDelay);
			nbt.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
			nbt.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
			nbt.putShort("SpawnCount", (short)this.spawnCount);
			nbt.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
			nbt.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
			nbt.putShort("SpawnRange", (short)this.spawnRange);
			nbt.put("SpawnData", this.spawnEntry.getEntityNbt().copy());
			NbtList nbtList = new NbtList();
			if (this.spawnPotentials.isEmpty()) {
				nbtList.add(this.spawnEntry.toNbt());
			} else {
				for (MobSpawnerEntry mobSpawnerEntry : this.spawnPotentials.getEntries()) {
					nbtList.add(mobSpawnerEntry.toNbt());
				}
			}

			nbt.put("SpawnPotentials", nbtList);
			return nbt;
		}
	}

	@Nullable
	public Entity getRenderedEntity(World world) {
		if (this.renderedEntity == null) {
			this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getEntityNbt(), world, Function.identity());
			if (this.spawnEntry.getEntityNbt().getSize() == 1
				&& this.spawnEntry.getEntityNbt().contains("id", NbtElement.STRING_TYPE)
				&& this.renderedEntity instanceof MobEntity) {
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

	public void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
		this.spawnEntry = spawnEntry;
	}

	public abstract void sendStatus(World world, BlockPos pos, int i);

	public double method_8278() {
		return this.field_9161;
	}

	public double method_8279() {
		return this.field_9159;
	}
}
