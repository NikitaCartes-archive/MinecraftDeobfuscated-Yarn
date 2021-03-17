/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class MobSpawnerLogic {
    private static final Logger LOGGER = LogManager.getLogger();
    private int spawnDelay = 20;
    private final List<MobSpawnerEntry> spawnPotentials = Lists.newArrayList();
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
        } catch (InvalidIdentifierException invalidIdentifierException) {
            LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", (Object)string, world != null ? world.getRegistryKey().getValue() : "<null>", (Object)pos.getX(), (Object)pos.getY(), (Object)pos.getZ());
            return null;
        }
    }

    public void setEntityId(EntityType<?> type) {
        this.spawnEntry.getEntityNbt().putString("id", Registry.ENTITY_TYPE.getId(type).toString());
    }

    private boolean isPlayerInRange(World world, BlockPos pos) {
        return world.isPlayerInRange((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, this.requiredPlayerRange);
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
            double f;
            NbtCompound nbtCompound = this.spawnEntry.getEntityNbt();
            Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);
            if (!optional.isPresent()) {
                this.updateSpawns(world, pos);
                return;
            }
            NbtList nbtList = nbtCompound.getList("Pos", NbtTypeIds.DOUBLE);
            int j = nbtList.size();
            double d = j >= 1 ? nbtList.getDouble(0) : (double)pos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
            double e = j >= 2 ? nbtList.getDouble(1) : (double)(pos.getY() + world.random.nextInt(3) - 1);
            double d2 = f = j >= 3 ? nbtList.getDouble(2) : (double)pos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
            if (!world.isSpaceEmpty(optional.get().createSimpleBoundingBox(d, e, f)) || !SpawnRestriction.canSpawn(optional.get(), world, SpawnReason.SPAWNER, new BlockPos(d, e, f), world.getRandom())) continue;
            Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, world, entity -> {
                entity.refreshPositionAndAngles(d, e, f, entity.yaw, entity.pitch);
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
            entity2.refreshPositionAndAngles(entity2.getX(), entity2.getY(), entity2.getZ(), world.random.nextFloat() * 360.0f, 0.0f);
            if (entity2 instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity)entity2;
                if (!mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) continue;
                if (this.spawnEntry.getEntityNbt().getSize() == 1 && this.spawnEntry.getEntityNbt().contains("id", NbtTypeIds.STRING)) {
                    ((MobEntity)entity2).initialize(world, world.getLocalDifficulty(entity2.getBlockPos()), SpawnReason.SPAWNER, null, null);
                }
            }
            if (!world.shouldCreateNewEntityWithPassenger(entity2)) {
                this.updateSpawns(world, pos);
                return;
            }
            world.syncWorldEvent(2004, pos, 0);
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
        this.spawnDelay = this.maxSpawnDelay <= this.minSpawnDelay ? this.minSpawnDelay : this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        if (!this.spawnPotentials.isEmpty()) {
            WeightedPicker.getRandom(this.random, this.spawnPotentials).ifPresent(mobSpawnerEntry -> this.setSpawnEntry(world, pos, (MobSpawnerEntry)mobSpawnerEntry));
        }
        this.sendStatus(world, pos, 1);
    }

    public void readNbt(@Nullable World world, BlockPos pos, NbtCompound tag) {
        this.spawnDelay = tag.getShort("Delay");
        this.spawnPotentials.clear();
        if (tag.contains("SpawnPotentials", NbtTypeIds.LIST)) {
            NbtList nbtList = tag.getList("SpawnPotentials", NbtTypeIds.COMPOUND);
            for (int i = 0; i < nbtList.size(); ++i) {
                this.spawnPotentials.add(new MobSpawnerEntry(nbtList.getCompound(i)));
            }
        }
        if (tag.contains("SpawnData", NbtTypeIds.COMPOUND)) {
            this.setSpawnEntry(world, pos, new MobSpawnerEntry(1, tag.getCompound("SpawnData")));
        } else if (!this.spawnPotentials.isEmpty()) {
            WeightedPicker.getRandom(this.random, this.spawnPotentials).ifPresent(mobSpawnerEntry -> this.setSpawnEntry(world, pos, (MobSpawnerEntry)mobSpawnerEntry));
        }
        if (tag.contains("MinSpawnDelay", NbtTypeIds.NUMBER)) {
            this.minSpawnDelay = tag.getShort("MinSpawnDelay");
            this.maxSpawnDelay = tag.getShort("MaxSpawnDelay");
            this.spawnCount = tag.getShort("SpawnCount");
        }
        if (tag.contains("MaxNearbyEntities", NbtTypeIds.NUMBER)) {
            this.maxNearbyEntities = tag.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = tag.getShort("RequiredPlayerRange");
        }
        if (tag.contains("SpawnRange", NbtTypeIds.NUMBER)) {
            this.spawnRange = tag.getShort("SpawnRange");
        }
        this.renderedEntity = null;
    }

    public NbtCompound writeNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {
        Identifier identifier = this.getEntityId(world, pos);
        if (identifier == null) {
            return nbt;
        }
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
            nbtList.add(this.spawnEntry.serialize());
        } else {
            for (MobSpawnerEntry mobSpawnerEntry : this.spawnPotentials) {
                nbtList.add(mobSpawnerEntry.serialize());
            }
        }
        nbt.put("SpawnPotentials", nbtList);
        return nbt;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Entity getRenderedEntity(World world) {
        if (this.renderedEntity == null) {
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getEntityNbt(), world, Function.identity());
            if (this.spawnEntry.getEntityNbt().getSize() != 1 || !this.spawnEntry.getEntityNbt().contains("id", NbtTypeIds.STRING) || this.renderedEntity instanceof MobEntity) {
                // empty if block
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
        }
        return false;
    }

    public void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
        this.spawnEntry = spawnEntry;
    }

    public abstract void sendStatus(World var1, BlockPos var2, int var3);

    @Environment(value=EnvType.CLIENT)
    public double method_8278() {
        return this.field_9161;
    }

    @Environment(value=EnvType.CLIENT)
    public double method_8279() {
        return this.field_9159;
    }
}

