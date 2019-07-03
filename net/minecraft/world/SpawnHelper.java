/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
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
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class SpawnHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void spawnEntitiesInChunk(EntityCategory entityCategory, World world, WorldChunk worldChunk, BlockPos blockPos) {
        ChunkGenerator<?> chunkGenerator = world.getChunkManager().getChunkGenerator();
        int i = 0;
        BlockPos blockPos2 = SpawnHelper.method_8657(world, worldChunk);
        int j = blockPos2.getX();
        int k = blockPos2.getY();
        int l = blockPos2.getZ();
        if (k < 1) {
            return;
        }
        BlockState blockState = worldChunk.getBlockState(blockPos2);
        if (blockState.isSimpleFullBlock(worldChunk, blockPos2)) {
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
                mutable.set(n += world.random.nextInt(6) - world.random.nextInt(6), k, o += world.random.nextInt(6) - world.random.nextInt(6));
                float f = (float)n + 0.5f;
                float g = (float)o + 0.5f;
                PlayerEntity playerEntity = world.getClosestPlayer(f, g, -1.0);
                if (playerEntity == null || (d = playerEntity.squaredDistanceTo(f, k, g)) <= 576.0 || blockPos.isWithinDistance(new Vec3d(f, k, g), 24.0) || !Objects.equals(chunkPos = new ChunkPos(mutable), worldChunk.getPos()) && !world.getChunkManager().shouldTickChunk(chunkPos)) continue;
                if (spawnEntry == null) {
                    spawnEntry = SpawnHelper.method_8664(chunkGenerator, entityCategory, world.random, mutable);
                    if (spawnEntry == null) continue block2;
                    q = spawnEntry.minGroupSize + world.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
                }
                if (spawnEntry.type.getCategory() == EntityCategory.MISC || !spawnEntry.type.method_20814() && d > 16384.0 || !(entityType = spawnEntry.type).isSummonable() || !SpawnHelper.method_8659(chunkGenerator, entityCategory, spawnEntry, mutable) || !SpawnHelper.canSpawn(location = SpawnRestriction.getLocation(entityType), world, mutable, entityType) || !SpawnRestriction.method_20638(entityType, world, SpawnType.NATURAL, mutable, world.random) || !world.doesNotCollide(entityType.createSimpleBoundingBox(f, k, g))) continue;
                try {
                    Object entity = entityType.create(world);
                    if (!(entity instanceof MobEntity)) {
                        throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getId(entityType));
                    }
                    mobEntity = (MobEntity)entity;
                } catch (Exception exception) {
                    LOGGER.warn("Failed to create mob", (Throwable)exception);
                    return;
                }
                mobEntity.setPositionAndAngles(f, k, g, world.random.nextFloat() * 360.0f, 0.0f);
                if (d > 16384.0 && mobEntity.canImmediatelyDespawn(d) || !mobEntity.canSpawn(world, SpawnType.NATURAL) || !mobEntity.canSpawn(world)) continue;
                entityData = mobEntity.initialize(world, world.getLocalDifficulty(new BlockPos(mobEntity)), SpawnType.NATURAL, entityData, null);
                ++r;
                world.spawnEntity(mobEntity);
                if (++i >= mobEntity.getLimitPerChunk()) {
                    return;
                }
                if (mobEntity.spawnsTooManyForEachTry(r)) continue block2;
            }
        }
    }

    @Nullable
    private static Biome.SpawnEntry method_8664(ChunkGenerator<?> chunkGenerator, EntityCategory entityCategory, Random random, BlockPos blockPos) {
        List<Biome.SpawnEntry> list = chunkGenerator.getEntitySpawnList(entityCategory, blockPos);
        if (list.isEmpty()) {
            return null;
        }
        return WeightedPicker.getRandom(random, list);
    }

    private static boolean method_8659(ChunkGenerator<?> chunkGenerator, EntityCategory entityCategory, Biome.SpawnEntry spawnEntry, BlockPos blockPos) {
        List<Biome.SpawnEntry> list = chunkGenerator.getEntitySpawnList(entityCategory, blockPos);
        if (list.isEmpty()) {
            return false;
        }
        return list.contains(spawnEntry);
    }

    private static BlockPos method_8657(World world, WorldChunk worldChunk) {
        ChunkPos chunkPos = worldChunk.getPos();
        int i = chunkPos.getStartX() + world.random.nextInt(16);
        int j = chunkPos.getStartZ() + world.random.nextInt(16);
        int k = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
        int l = world.random.nextInt(k + 1);
        return new BlockPos(i, l, j);
    }

    public static boolean isClearForSpawn(BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        if (Block.isShapeFullCube(blockState.getCollisionShape(blockView, blockPos))) {
            return false;
        }
        if (blockState.emitsRedstonePower()) {
            return false;
        }
        if (!fluidState.isEmpty()) {
            return false;
        }
        return !blockState.matches(BlockTags.RAILS);
    }

    public static boolean canSpawn(SpawnRestriction.Location location, ViewableWorld viewableWorld, BlockPos blockPos, @Nullable EntityType<?> entityType) {
        if (location == SpawnRestriction.Location.NO_RESTRICTIONS) {
            return true;
        }
        if (entityType == null || !viewableWorld.getWorldBorder().contains(blockPos)) {
            return false;
        }
        BlockState blockState = viewableWorld.getBlockState(blockPos);
        FluidState fluidState = viewableWorld.getFluidState(blockPos);
        BlockPos blockPos2 = blockPos.up();
        BlockPos blockPos3 = blockPos.down();
        switch (location) {
            case IN_WATER: {
                return fluidState.matches(FluidTags.WATER) && viewableWorld.getFluidState(blockPos3).matches(FluidTags.WATER) && !viewableWorld.getBlockState(blockPos2).isSimpleFullBlock(viewableWorld, blockPos2);
            }
        }
        BlockState blockState2 = viewableWorld.getBlockState(blockPos3);
        if (!blockState2.allowsSpawning(viewableWorld, blockPos3, entityType)) {
            return false;
        }
        return SpawnHelper.isClearForSpawn(viewableWorld, blockPos, blockState, fluidState) && SpawnHelper.isClearForSpawn(viewableWorld, blockPos2, viewableWorld.getBlockState(blockPos2), viewableWorld.getFluidState(blockPos2));
    }

    public static void populateEntities(IWorld iWorld, Biome biome, int i, int j, Random random) {
        List<Biome.SpawnEntry> list = biome.getEntitySpawnList(EntityCategory.CREATURE);
        if (list.isEmpty()) {
            return;
        }
        int k = i << 4;
        int l = j << 4;
        while (random.nextFloat() < biome.getMaxSpawnLimit()) {
            Biome.SpawnEntry spawnEntry = WeightedPicker.getRandom(random, list);
            int m = spawnEntry.minGroupSize + random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
            EntityData entityData = null;
            int n = k + random.nextInt(16);
            int o = l + random.nextInt(16);
            int p = n;
            int q = o;
            for (int r = 0; r < m; ++r) {
                boolean bl = false;
                for (int s = 0; !bl && s < 4; ++s) {
                    BlockPos blockPos = SpawnHelper.method_8658(iWorld, spawnEntry.type, n, o);
                    if (spawnEntry.type.isSummonable() && SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, iWorld, blockPos, spawnEntry.type)) {
                        MobEntity mobEntity;
                        Object entity;
                        float f = spawnEntry.type.getWidth();
                        double d = MathHelper.clamp((double)n, (double)k + (double)f, (double)k + 16.0 - (double)f);
                        double e = MathHelper.clamp((double)o, (double)l + (double)f, (double)l + 16.0 - (double)f);
                        if (!iWorld.doesNotCollide(spawnEntry.type.createSimpleBoundingBox(d, blockPos.getY(), e)) || !SpawnRestriction.method_20638(spawnEntry.type, iWorld, SpawnType.CHUNK_GENERATION, new BlockPos(d, (double)blockPos.getY(), e), iWorld.getRandom())) continue;
                        try {
                            entity = spawnEntry.type.create(iWorld.getWorld());
                        } catch (Exception exception) {
                            LOGGER.warn("Failed to create mob", (Throwable)exception);
                            continue;
                        }
                        ((Entity)entity).setPositionAndAngles(d, blockPos.getY(), e, random.nextFloat() * 360.0f, 0.0f);
                        if (entity instanceof MobEntity && (mobEntity = (MobEntity)entity).canSpawn(iWorld, SpawnType.CHUNK_GENERATION) && mobEntity.canSpawn(iWorld)) {
                            entityData = mobEntity.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(mobEntity)), SpawnType.CHUNK_GENERATION, entityData, null);
                            iWorld.spawnEntity(mobEntity);
                            bl = true;
                        }
                    }
                    n += random.nextInt(5) - random.nextInt(5);
                    o += random.nextInt(5) - random.nextInt(5);
                    while (n < k || n >= k + 16 || o < l || o >= l + 16) {
                        n = p + random.nextInt(5) - random.nextInt(5);
                        o = q + random.nextInt(5) - random.nextInt(5);
                    }
                }
            }
        }
    }

    private static BlockPos method_8658(ViewableWorld viewableWorld, @Nullable EntityType<?> entityType, int i, int j) {
        BlockPos blockPos = new BlockPos(i, viewableWorld.getTop(SpawnRestriction.getHeightMapType(entityType), i, j), j);
        BlockPos blockPos2 = blockPos.down();
        if (viewableWorld.getBlockState(blockPos2).canPlaceAtSide(viewableWorld, blockPos2, BlockPlacementEnvironment.LAND)) {
            return blockPos2;
        }
        return blockPos;
    }
}

