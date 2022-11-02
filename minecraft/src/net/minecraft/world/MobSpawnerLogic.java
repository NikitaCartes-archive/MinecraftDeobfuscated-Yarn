package net.minecraft.world;

import com.mojang.logging.LogUtils;
import java.util.Optional;
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
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public abstract class MobSpawnerLogic {
	public static final String SPAWN_DATA_KEY = "SpawnData";
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_30951 = 1;
	private int spawnDelay = 20;
	private DataPool<MobSpawnerEntry> spawnPotentials = DataPool.empty();
	@Nullable
	private MobSpawnerEntry spawnEntry;
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

	public void setEntityId(EntityType<?> type, @Nullable World world, Random random, BlockPos pos) {
		this.getSpawnEntry(world, random, pos).getNbt().putString("id", Registry.ENTITY_TYPE.getId(type).toString());
	}

	private boolean isPlayerInRange(World world, BlockPos pos) {
		return world.isPlayerInRange((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (double)this.requiredPlayerRange);
	}

	public void clientTick(World world, BlockPos pos) {
		if (!this.isPlayerInRange(world, pos)) {
			this.field_9159 = this.field_9161;
		} else if (this.renderedEntity != null) {
			Random random = world.getRandom();
			double d = (double)pos.getX() + random.nextDouble();
			double e = (double)pos.getY() + random.nextDouble();
			double f = (double)pos.getZ() + random.nextDouble();
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
				Random random = world.getRandom();
				MobSpawnerEntry mobSpawnerEntry = this.getSpawnEntry(world, random, pos);

				for (int i = 0; i < this.spawnCount; i++) {
					NbtCompound nbtCompound = mobSpawnerEntry.getNbt();
					Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);
					if (optional.isEmpty()) {
						this.updateSpawns(world, pos);
						return;
					}

					NbtList nbtList = nbtCompound.getList("Pos", NbtElement.DOUBLE_TYPE);
					int j = nbtList.size();
					double d = j >= 1 ? nbtList.getDouble(0) : (double)pos.getX() + (random.nextDouble() - random.nextDouble()) * (double)this.spawnRange + 0.5;
					double e = j >= 2 ? nbtList.getDouble(1) : (double)(pos.getY() + random.nextInt(3) - 1);
					double f = j >= 3 ? nbtList.getDouble(2) : (double)pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double)this.spawnRange + 0.5;
					if (world.isSpaceEmpty(((EntityType)optional.get()).createSimpleBoundingBox(d, e, f))) {
						BlockPos blockPos = new BlockPos(d, e, f);
						if (mobSpawnerEntry.getCustomSpawnRules().isPresent()) {
							if (!((EntityType)optional.get()).getSpawnGroup().isPeaceful() && world.getDifficulty() == Difficulty.PEACEFUL) {
								continue;
							}

							MobSpawnerEntry.CustomSpawnRules customSpawnRules = (MobSpawnerEntry.CustomSpawnRules)mobSpawnerEntry.getCustomSpawnRules().get();
							if (!customSpawnRules.blockLightLimit().contains(world.getLightLevel(LightType.BLOCK, blockPos))
								|| !customSpawnRules.skyLightLimit().contains(world.getLightLevel(LightType.SKY, blockPos))) {
								continue;
							}
						} else if (!SpawnRestriction.canSpawn((EntityType)optional.get(), world, SpawnReason.SPAWNER, blockPos, world.getRandom())) {
							continue;
						}

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

						entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), random.nextFloat() * 360.0F, 0.0F);
						if (entity instanceof MobEntity mobEntity) {
							if (mobSpawnerEntry.getCustomSpawnRules().isEmpty() && !mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) {
								continue;
							}

							if (mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().contains("id", NbtElement.STRING_TYPE)) {
								((MobEntity)entity).initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, null, null);
							}
						}

						if (!world.spawnNewEntityAndPassengers(entity)) {
							this.updateSpawns(world, pos);
							return;
						}

						world.syncWorldEvent(WorldEvents.SPAWNER_SPAWNS_MOB, pos, 0);
						world.emitGameEvent(entity, GameEvent.ENTITY_PLACE, blockPos);
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
		Random random = world.random;
		if (this.maxSpawnDelay <= this.minSpawnDelay) {
			this.spawnDelay = this.minSpawnDelay;
		} else {
			this.spawnDelay = this.minSpawnDelay + random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
		}

		this.spawnPotentials.getOrEmpty(random).ifPresent(spawnPotential -> this.setSpawnEntry(world, pos, (MobSpawnerEntry)spawnPotential.getData()));
		this.sendStatus(world, pos, 1);
	}

	public void readNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {
		this.spawnDelay = nbt.getShort("Delay");
		boolean bl = nbt.contains("SpawnData", NbtElement.COMPOUND_TYPE);
		if (bl) {
			MobSpawnerEntry mobSpawnerEntry = (MobSpawnerEntry)MobSpawnerEntry.CODEC
				.parse(NbtOps.INSTANCE, nbt.getCompound("SpawnData"))
				.resultOrPartial(string -> LOGGER.warn("Invalid SpawnData: {}", string))
				.orElseGet(MobSpawnerEntry::new);
			this.setSpawnEntry(world, pos, mobSpawnerEntry);
		}

		boolean bl2 = nbt.contains("SpawnPotentials", NbtElement.LIST_TYPE);
		if (bl2) {
			NbtList nbtList = nbt.getList("SpawnPotentials", NbtElement.COMPOUND_TYPE);
			this.spawnPotentials = (DataPool<MobSpawnerEntry>)MobSpawnerEntry.DATA_POOL_CODEC
				.parse(NbtOps.INSTANCE, nbtList)
				.resultOrPartial(error -> LOGGER.warn("Invalid SpawnPotentials list: {}", error))
				.orElseGet(DataPool::empty);
		} else {
			this.spawnPotentials = DataPool.of(this.spawnEntry != null ? this.spawnEntry : new MobSpawnerEntry());
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

	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putShort("Delay", (short)this.spawnDelay);
		nbt.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
		nbt.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
		nbt.putShort("SpawnCount", (short)this.spawnCount);
		nbt.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
		nbt.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
		nbt.putShort("SpawnRange", (short)this.spawnRange);
		if (this.spawnEntry != null) {
			nbt.put(
				"SpawnData",
				(NbtElement)MobSpawnerEntry.CODEC.encodeStart(NbtOps.INSTANCE, this.spawnEntry).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))
			);
		}

		nbt.put("SpawnPotentials", (NbtElement)MobSpawnerEntry.DATA_POOL_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());
		return nbt;
	}

	@Nullable
	public Entity getRenderedEntity(World world, Random random, BlockPos pos) {
		if (this.renderedEntity == null) {
			NbtCompound nbtCompound = this.getSpawnEntry(world, random, pos).getNbt();
			if (!nbtCompound.contains("id", NbtElement.STRING_TYPE)) {
				return null;
			}

			this.renderedEntity = EntityType.loadEntityWithPassengers(nbtCompound, world, Function.identity());
			if (nbtCompound.getSize() == 1 && this.renderedEntity instanceof MobEntity) {
			}
		}

		return this.renderedEntity;
	}

	public boolean handleStatus(World world, int status) {
		if (status == 1) {
			if (world.isClient) {
				this.spawnDelay = this.minSpawnDelay;
			}

			return true;
		} else {
			return false;
		}
	}

	protected void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
		this.spawnEntry = spawnEntry;
	}

	private MobSpawnerEntry getSpawnEntry(@Nullable World world, Random random, BlockPos pos) {
		if (this.spawnEntry != null) {
			return this.spawnEntry;
		} else {
			this.setSpawnEntry(world, pos, (MobSpawnerEntry)this.spawnPotentials.getOrEmpty(random).map(Weighted.Present::getData).orElseGet(MobSpawnerEntry::new));
			return this.spawnEntry;
		}
	}

	public abstract void sendStatus(World world, BlockPos pos, int status);

	public double method_8278() {
		return this.field_9161;
	}

	public double method_8279() {
		return this.field_9159;
	}
}
