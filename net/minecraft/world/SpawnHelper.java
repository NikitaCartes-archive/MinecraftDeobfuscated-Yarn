/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class SpawnHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void spawnEntitiesInChunk(EntityCategory category, ServerWorld serverWorld, WorldChunk chunk) {
        BlockPos blockPos = SpawnHelper.getSpawnPos(serverWorld, chunk);
        if (blockPos.getY() < 1) {
            return;
        }
        SpawnHelper.method_24930(category, serverWorld, chunk, blockPos);
    }

    public static void method_24930(EntityCategory entityCategory, ServerWorld serverWorld, Chunk chunk, BlockPos blockPos) {
        ChunkGenerator<?> chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
        int i = blockPos.getY();
        BlockState blockState = chunk.getBlockState(blockPos);
        if (blockState.isSimpleFullBlock(chunk, blockPos)) {
            return;
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int j = 0;
        block0: for (int k = 0; k < 3; ++k) {
            int l = blockPos.getX();
            int m = blockPos.getZ();
            int n = 6;
            Biome.SpawnEntry spawnEntry = null;
            EntityData entityData = null;
            int o = MathHelper.ceil(Math.random() * 4.0);
            int p = 0;
            for (int q = 0; q < o; ++q) {
                double d;
                mutable.set(l += serverWorld.random.nextInt(6) - serverWorld.random.nextInt(6), i, m += serverWorld.random.nextInt(6) - serverWorld.random.nextInt(6));
                float f = (float)l + 0.5f;
                float g = (float)m + 0.5f;
                PlayerEntity playerEntity = serverWorld.getClosestPlayer((double)f, (double)i, (double)g, -1.0, false);
                if (playerEntity == null || !SpawnHelper.method_24933(serverWorld, chunk, mutable, d = playerEntity.squaredDistanceTo(f, i, g))) continue;
                if (spawnEntry == null) {
                    spawnEntry = SpawnHelper.pickRandomSpawnEntry(chunkGenerator, entityCategory, serverWorld.random, mutable);
                    if (spawnEntry == null) continue block0;
                    o = spawnEntry.minGroupSize + serverWorld.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
                }
                if (!SpawnHelper.method_24934(serverWorld, chunkGenerator, spawnEntry, mutable, d)) continue;
                MobEntity mobEntity = SpawnHelper.method_24931(serverWorld, spawnEntry.type);
                if (mobEntity == null) {
                    return;
                }
                mobEntity.refreshPositionAndAngles(f, i, g, serverWorld.random.nextFloat() * 360.0f, 0.0f);
                if (!SpawnHelper.method_24932(serverWorld, mobEntity, d)) continue;
                entityData = mobEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(mobEntity.getSenseCenterPos()), SpawnType.NATURAL, entityData, null);
                ++p;
                serverWorld.spawnEntity(mobEntity);
                if (++j >= mobEntity.getLimitPerChunk()) {
                    return;
                }
                if (mobEntity.spawnsTooManyForEachTry(p)) continue block0;
            }
        }
    }

    private static boolean method_24933(ServerWorld serverWorld, Chunk chunk, BlockPos.Mutable mutable, double d) {
        if (d <= 576.0) {
            return false;
        }
        if (serverWorld.getSpawnPos().isWithinDistance(new Vec3d((float)mutable.getX() + 0.5f, mutable.getY(), (float)mutable.getZ() + 0.5f), 24.0)) {
            return false;
        }
        ChunkPos chunkPos = new ChunkPos(mutable);
        return Objects.equals(chunkPos, chunk.getPos()) || serverWorld.getChunkManager().shouldTickChunk(chunkPos);
    }

    private static boolean method_24934(ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, Biome.SpawnEntry spawnEntry, BlockPos.Mutable mutable, double d) {
        EntityType<?> entityType = spawnEntry.type;
        if (entityType.getCategory() == EntityCategory.MISC) {
            return false;
        }
        if (!entityType.isSpawnableFarFromPlayer() && d > (double)(entityType.method_24908() * entityType.method_24908())) {
            return false;
        }
        if (!entityType.isSummonable() || !SpawnHelper.containsSpawnEntry(chunkGenerator, entityType.getCategory(), spawnEntry, mutable)) {
            return false;
        }
        SpawnRestriction.Location location = SpawnRestriction.getLocation(entityType);
        if (!SpawnHelper.canSpawn(location, serverWorld, mutable, entityType)) {
            return false;
        }
        if (!SpawnRestriction.canSpawn(entityType, serverWorld, SpawnType.NATURAL, mutable, serverWorld.random)) {
            return false;
        }
        return serverWorld.doesNotCollide(entityType.createSimpleBoundingBox((float)mutable.getX() + 0.5f, mutable.getY(), (float)mutable.getZ() + 0.5f));
    }

    @Nullable
    private static MobEntity method_24931(ServerWorld serverWorld, EntityType<?> entityType) {
        MobEntity mobEntity;
        try {
            Object entity = entityType.create(serverWorld);
            if (!(entity instanceof MobEntity)) {
                throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getId(entityType));
            }
            mobEntity = (MobEntity)entity;
        } catch (Exception exception) {
            LOGGER.warn("Failed to create mob", (Throwable)exception);
            return null;
        }
        return mobEntity;
    }

    private static boolean method_24932(ServerWorld serverWorld, MobEntity mobEntity, double d) {
        if (d > (double)(mobEntity.getType().method_24908() * mobEntity.getType().method_24908()) && mobEntity.canImmediatelyDespawn(d)) {
            return false;
        }
        return mobEntity.canSpawn(serverWorld, SpawnType.NATURAL) && mobEntity.canSpawn(serverWorld);
    }

    @Nullable
    private static Biome.SpawnEntry pickRandomSpawnEntry(ChunkGenerator<?> chunkGenerator, EntityCategory entityCategory, Random random, BlockPos pos) {
        List<Biome.SpawnEntry> list = chunkGenerator.getEntitySpawnList(entityCategory, pos);
        if (list.isEmpty()) {
            return null;
        }
        return WeightedPicker.getRandom(random, list);
    }

    private static boolean containsSpawnEntry(ChunkGenerator<?> chunkGenerator, EntityCategory entityCategory, Biome.SpawnEntry spawnEntry, BlockPos pos) {
        List<Biome.SpawnEntry> list = chunkGenerator.getEntitySpawnList(entityCategory, pos);
        if (list.isEmpty()) {
            return false;
        }
        return list.contains(spawnEntry);
    }

    private static BlockPos getSpawnPos(World world, WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX() + world.random.nextInt(16);
        int j = chunkPos.getStartZ() + world.random.nextInt(16);
        int k = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
        int l = world.random.nextInt(k + 1);
        return new BlockPos(i, l, j);
    }

    public static boolean isClearForSpawn(BlockView blockView, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.isFullCube(blockView, pos)) {
            return false;
        }
        if (state.emitsRedstonePower()) {
            return false;
        }
        if (!fluidState.isEmpty()) {
            return false;
        }
        return !state.matches(BlockTags.RAILS);
    }

    public static boolean canSpawn(SpawnRestriction.Location location, WorldView worldView, BlockPos pos, @Nullable EntityType<?> entityType) {
        if (location == SpawnRestriction.Location.NO_RESTRICTIONS) {
            return true;
        }
        if (entityType == null || !worldView.getWorldBorder().contains(pos)) {
            return false;
        }
        BlockState blockState = worldView.getBlockState(pos);
        FluidState fluidState = worldView.getFluidState(pos);
        BlockPos blockPos = pos.up();
        BlockPos blockPos2 = pos.down();
        switch (location) {
            case IN_WATER: {
                return fluidState.matches(FluidTags.WATER) && worldView.getFluidState(blockPos2).matches(FluidTags.WATER) && !worldView.getBlockState(blockPos).isSimpleFullBlock(worldView, blockPos);
            }
        }
        BlockState blockState2 = worldView.getBlockState(blockPos2);
        if (!blockState2.allowsSpawning(worldView, blockPos2, entityType)) {
            return false;
        }
        return SpawnHelper.isClearForSpawn(worldView, pos, blockState, fluidState) && SpawnHelper.isClearForSpawn(worldView, blockPos, worldView.getBlockState(blockPos), worldView.getFluidState(blockPos));
    }

    public static void populateEntities(IWorld world, Biome biome, int chunkX, int chunkZ, Random random) {
        List<Biome.SpawnEntry> list = biome.getEntitySpawnList(EntityCategory.CREATURE);
        if (list.isEmpty()) {
            return;
        }
        int i = chunkX << 4;
        int j = chunkZ << 4;
        while (random.nextFloat() < biome.getMaxSpawnLimit()) {
            Biome.SpawnEntry spawnEntry = WeightedPicker.getRandom(random, list);
            int k = spawnEntry.minGroupSize + random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
            EntityData entityData = null;
            int l = i + random.nextInt(16);
            int m = j + random.nextInt(16);
            int n = l;
            int o = m;
            for (int p = 0; p < k; ++p) {
                boolean bl = false;
                for (int q = 0; !bl && q < 4; ++q) {
                    BlockPos blockPos = SpawnHelper.getEntitySpawnPos(world, spawnEntry.type, l, m);
                    if (spawnEntry.type.isSummonable() && SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, blockPos, spawnEntry.type)) {
                        MobEntity mobEntity;
                        Object entity;
                        float f = spawnEntry.type.getWidth();
                        double d = MathHelper.clamp((double)l, (double)i + (double)f, (double)i + 16.0 - (double)f);
                        double e = MathHelper.clamp((double)m, (double)j + (double)f, (double)j + 16.0 - (double)f);
                        if (!world.doesNotCollide(spawnEntry.type.createSimpleBoundingBox(d, blockPos.getY(), e)) || !SpawnRestriction.canSpawn(spawnEntry.type, world, SpawnType.CHUNK_GENERATION, new BlockPos(d, (double)blockPos.getY(), e), world.getRandom())) continue;
                        try {
                            entity = spawnEntry.type.create(world.getWorld());
                        } catch (Exception exception) {
                            LOGGER.warn("Failed to create mob", (Throwable)exception);
                            continue;
                        }
                        ((Entity)entity).refreshPositionAndAngles(d, blockPos.getY(), e, random.nextFloat() * 360.0f, 0.0f);
                        if (entity instanceof MobEntity && (mobEntity = (MobEntity)entity).canSpawn(world, SpawnType.CHUNK_GENERATION) && mobEntity.canSpawn(world)) {
                            entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getSenseCenterPos()), SpawnType.CHUNK_GENERATION, entityData, null);
                            world.spawnEntity(mobEntity);
                            bl = true;
                        }
                    }
                    l += random.nextInt(5) - random.nextInt(5);
                    m += random.nextInt(5) - random.nextInt(5);
                    while (l < i || l >= i + 16 || m < j || m >= j + 16) {
                        l = n + random.nextInt(5) - random.nextInt(5);
                        m = o + random.nextInt(5) - random.nextInt(5);
                    }
                }
            }
        }
    }

    private static BlockPos getEntitySpawnPos(WorldView worldView, @Nullable EntityType<?> entityType, int x, int z) {
        BlockPos blockPos = new BlockPos(x, worldView.getTopY(SpawnRestriction.getHeightmapType(entityType), x, z), z);
        BlockPos blockPos2 = blockPos.down();
        if (worldView.getBlockState(blockPos2).canPathfindThrough(worldView, blockPos2, NavigationType.LAND)) {
            return blockPos2;
        }
        return blockPos;
    }
}

