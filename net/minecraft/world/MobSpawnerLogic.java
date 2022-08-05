/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.function.Function;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class MobSpawnerLogic {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_30951 = 1;
    private int spawnDelay = 20;
    private DataPool<MobSpawnerEntry> spawnPotentials = DataPool.empty();
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

    public void setEntityId(EntityType<?> type) {
        this.spawnEntry.getNbt().putString("id", Registry.ENTITY_TYPE.getId(type).toString());
    }

    private boolean isPlayerInRange(World world, BlockPos pos) {
        return world.isPlayerInRange((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, this.requiredPlayerRange);
    }

    public void clientTick(World world, BlockPos pos) {
        if (!this.isPlayerInRange(world, pos)) {
            this.field_9159 = this.field_9161;
        } else {
            Random random = world.getRandom();
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() + random.nextDouble();
            double f = (double)pos.getZ() + random.nextDouble();
            world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
            world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }
            this.field_9159 = this.field_9161;
            this.field_9161 = (this.field_9161 + (double)(1000.0f / ((float)this.spawnDelay + 200.0f))) % 360.0;
        }
    }

    public void serverTick(ServerWorld world, BlockPos pos) {
        if (!this.isPlayerInRange(world, pos)) {
            return;
        }
        if (this.spawnDelay == -1) {
            this.updateSpawns(world, pos);
        }
        if (this.spawnDelay > 0) {
            --this.spawnDelay;
            return;
        }
        boolean bl = false;
        for (int i = 0; i < this.spawnCount; ++i) {
            MobSpawnerEntry.CustomSpawnRules customSpawnRules;
            double f;
            NbtCompound nbtCompound = this.spawnEntry.getNbt();
            Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);
            if (optional.isEmpty()) {
                this.updateSpawns(world, pos);
                return;
            }
            NbtList nbtList = nbtCompound.getList("Pos", NbtElement.DOUBLE_TYPE);
            int j = nbtList.size();
            Random random = world.getRandom();
            double d = j >= 1 ? nbtList.getDouble(0) : (double)pos.getX() + (random.nextDouble() - random.nextDouble()) * (double)this.spawnRange + 0.5;
            double e = j >= 2 ? nbtList.getDouble(1) : (double)(pos.getY() + random.nextInt(3) - 1);
            double d2 = f = j >= 3 ? nbtList.getDouble(2) : (double)pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double)this.spawnRange + 0.5;
            if (!world.isSpaceEmpty(optional.get().createSimpleBoundingBox(d, e, f))) continue;
            BlockPos blockPos = new BlockPos(d, e, f);
            if (!this.spawnEntry.getCustomSpawnRules().isPresent() ? !SpawnRestriction.canSpawn(optional.get(), world, SpawnReason.SPAWNER, blockPos, world.getRandom()) : !optional.get().getSpawnGroup().isPeaceful() && world.getDifficulty() == Difficulty.PEACEFUL || !(customSpawnRules = this.spawnEntry.getCustomSpawnRules().get()).blockLightLimit().contains(world.getLightLevel(LightType.BLOCK, blockPos)) || !customSpawnRules.skyLightLimit().contains(world.getLightLevel(LightType.SKY, blockPos))) continue;
            Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, world, entity -> {
                entity.refreshPositionAndAngles(d, e, f, entity.getYaw(), entity.getPitch());
                return entity;
            });
            if (entity2 == null) {
                this.updateSpawns(world, pos);
                return;
            }
            int k = world.getNonSpectatingEntities(entity2.getClass(), new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).expand(this.spawnRange)).size();
            if (k >= this.maxNearbyEntities) {
                this.updateSpawns(world, pos);
                return;
            }
            entity2.refreshPositionAndAngles(entity2.getX(), entity2.getY(), entity2.getZ(), random.nextFloat() * 360.0f, 0.0f);
            if (entity2 instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity)entity2;
                if (this.spawnEntry.getCustomSpawnRules().isEmpty() && !mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) continue;
                if (this.spawnEntry.getNbt().getSize() == 1 && this.spawnEntry.getNbt().contains("id", NbtElement.STRING_TYPE)) {
                    ((MobEntity)entity2).initialize(world, world.getLocalDifficulty(entity2.getBlockPos()), SpawnReason.SPAWNER, null, null);
                }
            }
            if (!world.spawnNewEntityAndPassengers(entity2)) {
                this.updateSpawns(world, pos);
                return;
            }
            world.syncWorldEvent(WorldEvents.SPAWNER_SPAWNS_MOB, pos, 0);
            world.emitGameEvent(entity2, GameEvent.ENTITY_PLACE, blockPos);
            if (entity2 instanceof MobEntity) {
                ((MobEntity)entity2).playSpawnEffects();
            }
            bl = true;
        }
        if (bl) {
            this.updateSpawns(world, pos);
        }
    }

    private void updateSpawns(World world, BlockPos pos) {
        Random random = world.random;
        this.spawnDelay = this.maxSpawnDelay <= this.minSpawnDelay ? this.minSpawnDelay : this.minSpawnDelay + random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        this.spawnPotentials.getOrEmpty(random).ifPresent(spawnPotential -> this.setSpawnEntry(world, pos, (MobSpawnerEntry)spawnPotential.getData()));
        this.sendStatus(world, pos, 1);
    }

    public void readNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {
        this.spawnDelay = nbt.getShort("Delay");
        boolean bl = nbt.contains("SpawnPotentials", NbtElement.LIST_TYPE);
        boolean bl2 = nbt.contains("SpawnData", NbtElement.COMPOUND_TYPE);
        if (!bl) {
            MobSpawnerEntry mobSpawnerEntry = bl2 ? MobSpawnerEntry.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("SpawnData")).resultOrPartial(error -> LOGGER.warn("Invalid SpawnData: {}", error)).orElseGet(MobSpawnerEntry::new) : new MobSpawnerEntry();
            this.spawnPotentials = DataPool.of(mobSpawnerEntry);
            this.setSpawnEntry(world, pos, mobSpawnerEntry);
        } else {
            NbtList nbtList = nbt.getList("SpawnPotentials", NbtElement.COMPOUND_TYPE);
            this.spawnPotentials = MobSpawnerEntry.DATA_POOL_CODEC.parse(NbtOps.INSTANCE, nbtList).resultOrPartial(error -> LOGGER.warn("Invalid SpawnPotentials list: {}", error)).orElseGet(DataPool::empty);
            if (bl2) {
                MobSpawnerEntry mobSpawnerEntry2 = MobSpawnerEntry.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("SpawnData")).resultOrPartial(error -> LOGGER.warn("Invalid SpawnData: {}", error)).orElseGet(MobSpawnerEntry::new);
                this.setSpawnEntry(world, pos, mobSpawnerEntry2);
            } else {
                this.spawnPotentials.getOrEmpty(world.getRandom()).ifPresent(spawnPotential -> this.setSpawnEntry(world, pos, (MobSpawnerEntry)spawnPotential.getData()));
            }
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
        nbt.put("SpawnData", MobSpawnerEntry.CODEC.encodeStart(NbtOps.INSTANCE, this.spawnEntry).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData")));
        nbt.put("SpawnPotentials", MobSpawnerEntry.DATA_POOL_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());
        return nbt;
    }

    @Nullable
    public Entity getRenderedEntity(World world) {
        if (this.renderedEntity == null) {
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getNbt(), world, Function.identity());
            if (this.spawnEntry.getNbt().getSize() != 1 || !this.spawnEntry.getNbt().contains("id", NbtElement.STRING_TYPE) || this.renderedEntity instanceof MobEntity) {
                // empty if block
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
        }
        return false;
    }

    public void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
        this.spawnEntry = spawnEntry;
    }

    public abstract void sendStatus(World var1, BlockPos var2, int var3);

    public double method_8278() {
        return this.field_9161;
    }

    public double method_8279() {
        return this.field_9159;
    }
}

