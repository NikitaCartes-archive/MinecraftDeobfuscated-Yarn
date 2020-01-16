/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.WeightedPicker;
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
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class SpawnHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void spawnEntitiesInChunk(EntityCategory category, ServerWorld serverWorld, WorldChunk chunk, BlockPos spawnPos) {
        ChunkGenerator<?> chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
        int i = 0;
        BlockPos blockPos = SpawnHelper.getSpawnPos(serverWorld, chunk);
        int j = blockPos.getX();
        int k = blockPos.getY();
        int l = blockPos.getZ();
        if (k < 1) {
            return;
        }
        BlockState blockState = chunk.getBlockState(blockPos);
        if (blockState.isSimpleFullBlock(chunk, blockPos)) {
            return;
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        block2: for (int m = 0; m < 3; ++m) {
            int n = j;
            int o = l;
            int p = 6;
            Biome.SpawnEntry spawnEntry = null;
            EntityData entityData = null;
            int q = MathHelper.ceil(Math.random() * 4.0);
            int r = 0;
            for (int s = 0; s < q; ++s) {
                MobEntity mobEntity;
                SpawnRestriction.Location location;
                EntityType<?> entityType;
                ChunkPos chunkPos;
                double d;
                mutable.set(n += serverWorld.random.nextInt(6) - serverWorld.random.nextInt(6), k, o += serverWorld.random.nextInt(6) - serverWorld.random.nextInt(6));
                float f = (float)n + 0.5f;
                float g = (float)o + 0.5f;
                PlayerEntity playerEntity = serverWorld.getClosestPlayer(f, g, -1.0);
                if (playerEntity == null || (d = playerEntity.squaredDistanceTo(f, k, g)) <= 576.0 || spawnPos.isWithinDistance(new Vec3d(f, k, g), 24.0) || !Objects.equals(chunkPos = new ChunkPos(mutable), chunk.getPos()) && !serverWorld.getChunkManager().shouldTickChunk(chunkPos)) continue;
                if (spawnEntry == null) {
                    spawnEntry = SpawnHelper.pickRandomSpawnEntry(chunkGenerator, category, serverWorld.random, mutable);
                    if (spawnEntry == null) continue block2;
                    q = spawnEntry.minGroupSize + serverWorld.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
                }
                if (spawnEntry.type.getCategory() == EntityCategory.MISC || !spawnEntry.type.isSpawnableFarFromPlayer() && d > 16384.0 || !(entityType = spawnEntry.type).isSummonable() || !SpawnHelper.containsSpawnEntry(chunkGenerator, category, spawnEntry, mutable) || !SpawnHelper.canSpawn(location = SpawnRestriction.getLocation(entityType), serverWorld, mutable, entityType) || !SpawnRestriction.canSpawn(entityType, serverWorld, SpawnType.NATURAL, mutable, serverWorld.random) || !serverWorld.doesNotCollide(entityType.createSimpleBoundingBox(f, k, g))) continue;
                try {
                    Object entity = entityType.create(serverWorld);
                    if (!(entity instanceof MobEntity)) {
                        throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getId(entityType));
                    }
                    mobEntity = (MobEntity)entity;
                } catch (Exception exception) {
                    LOGGER.warn("Failed to create mob", (Throwable)exception);
                    return;
                }
                mobEntity.refreshPositionAndAngles(f, k, g, serverWorld.random.nextFloat() * 360.0f, 0.0f);
                if (d > 16384.0 && mobEntity.canImmediatelyDespawn(d) || !mobEntity.canSpawn(serverWorld, SpawnType.NATURAL) || !mobEntity.canSpawn(serverWorld)) continue;
                entityData = mobEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(new BlockPos(mobEntity)), SpawnType.NATURAL, entityData, null);
                ++r;
                serverWorld.spawnEntity(mobEntity);
                if (++i >= mobEntity.getLimitPerChunk()) {
                    return;
                }
                if (mobEntity.spawnsTooManyForEachTry(r)) continue block2;
            }
        }
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
                            entityData = mobEntity.initialize(world, world.getLocalDifficulty(new BlockPos(mobEntity)), SpawnType.CHUNK_GENERATION, entityData, null);
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
        if (worldView.getBlockState(blockPos2).canPlaceAtSide(worldView, blockPos2, BlockPlacementEnvironment.LAND)) {
            return blockPos2;
        }
        return blockPos;
    }
}

