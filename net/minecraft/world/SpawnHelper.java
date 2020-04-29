/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.math.GravityField;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.DirectBiomeAccessType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class SpawnHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int CHUNK_AREA = (int)Math.pow(17.0, 2.0);
    private static final EntityCategory[] SPAWNABLE_CATEGORIES = (EntityCategory[])Stream.of(EntityCategory.values()).filter(entityCategory -> entityCategory != EntityCategory.MISC).toArray(EntityCategory[]::new);

    public static Info setupSpawn(int spawningChunkCount, Iterable<Entity> entities, ChunkSource chunkSource) {
        GravityField gravityField = new GravityField();
        Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
        for (Entity entity : entities) {
            EntityCategory entityCategory;
            MobEntity mobEntity;
            if (entity instanceof MobEntity && ((mobEntity = (MobEntity)entity).isPersistent() || mobEntity.cannotDespawn()) || (entityCategory = entity.getType().getCategory()) == EntityCategory.MISC) continue;
            BlockPos blockPos = entity.getBlockPos();
            long l = ChunkPos.toLong(blockPos.getX() >> 4, blockPos.getZ() >> 4);
            chunkSource.query(l, worldChunk -> {
                Biome biome = SpawnHelper.getBiomeDirectly(blockPos, worldChunk);
                Biome.SpawnDensity spawnDensity = biome.getSpawnDensity(entity.getType());
                if (spawnDensity != null) {
                    gravityField.addPoint(entity.getBlockPos(), spawnDensity.getMass());
                }
                object2IntOpenHashMap.addTo(entityCategory, 1);
            });
        }
        return new Info(spawningChunkCount, object2IntOpenHashMap, gravityField);
    }

    private static Biome getBiomeDirectly(BlockPos pos, Chunk chunk) {
        return DirectBiomeAccessType.INSTANCE.getBiome(0L, pos.getX(), pos.getY(), pos.getZ(), chunk.getBiomeArray());
    }

    public static void spawn(ServerWorld world, WorldChunk chunk2, Info info, boolean spawnAnimals, boolean spawnMonsters, boolean shouldSpawnAnimals) {
        world.getProfiler().push("spawner");
        for (EntityCategory entityCategory : SPAWNABLE_CATEGORIES) {
            if (!spawnAnimals && entityCategory.isPeaceful() || !spawnMonsters && !entityCategory.isPeaceful() || !shouldSpawnAnimals && entityCategory.isAnimal() || !info.isBelowCap(entityCategory)) continue;
            SpawnHelper.spawnEntitiesInChunk(entityCategory, world, chunk2, (entityType, blockPos, chunk) -> info.test(entityType, blockPos, chunk), (mobEntity, chunk) -> info.run(mobEntity, chunk));
        }
        world.getProfiler().pop();
    }

    public static void spawnEntitiesInChunk(EntityCategory category, ServerWorld world, WorldChunk chunk, Checker checker, Runner runner) {
        BlockPos blockPos = SpawnHelper.getSpawnPos(world, chunk);
        if (blockPos.getY() < 1) {
            return;
        }
        SpawnHelper.spawnEntitiesInChunk(category, world, chunk, blockPos, checker, runner);
    }

    public static void spawnEntitiesInChunk(EntityCategory category, ServerWorld world, Chunk chunk, BlockPos pos, Checker checker, Runner runner) {
        StructureAccessor structureAccessor = world.getStructureAccessor();
        ChunkGenerator<?> chunkGenerator = world.getChunkManager().getChunkGenerator();
        int i = pos.getY();
        BlockState blockState = chunk.getBlockState(pos);
        if (blockState.isSolidBlock(chunk, pos)) {
            return;
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int j = 0;
        block0: for (int k = 0; k < 3; ++k) {
            int l = pos.getX();
            int m = pos.getZ();
            int n = 6;
            Biome.SpawnEntry spawnEntry = null;
            EntityData entityData = null;
            int o = MathHelper.ceil(world.random.nextFloat() * 4.0f);
            int p = 0;
            for (int q = 0; q < o; ++q) {
                double d;
                mutable.set(l += world.random.nextInt(6) - world.random.nextInt(6), i, m += world.random.nextInt(6) - world.random.nextInt(6));
                float f = (float)l + 0.5f;
                float g = (float)m + 0.5f;
                PlayerEntity playerEntity = world.getClosestPlayer((double)f, (double)i, (double)g, -1.0, false);
                if (playerEntity == null || !SpawnHelper.isAcceptableSpawnPosition(world, chunk, mutable, d = playerEntity.squaredDistanceTo(f, i, g))) continue;
                if (spawnEntry == null) {
                    spawnEntry = SpawnHelper.pickRandomSpawnEntry(structureAccessor, chunkGenerator, category, world.random, mutable);
                    if (spawnEntry == null) continue block0;
                    o = spawnEntry.minGroupSize + world.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
                }
                if (!SpawnHelper.canSpawn(world, category, structureAccessor, chunkGenerator, spawnEntry, mutable, d) || !checker.test(spawnEntry.type, mutable, chunk)) continue;
                MobEntity mobEntity = SpawnHelper.createMob(world, spawnEntry.type);
                if (mobEntity == null) {
                    return;
                }
                mobEntity.refreshPositionAndAngles(f, i, g, world.random.nextFloat() * 360.0f, 0.0f);
                if (!SpawnHelper.isValidSpawn(world, mobEntity, d)) continue;
                entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnType.NATURAL, entityData, null);
                ++p;
                world.spawnEntity(mobEntity);
                runner.run(mobEntity, chunk);
                if (++j >= mobEntity.getLimitPerChunk()) {
                    return;
                }
                if (mobEntity.spawnsTooManyForEachTry(p)) continue block0;
            }
        }
    }

    private static boolean isAcceptableSpawnPosition(ServerWorld world, Chunk chunk, BlockPos.Mutable pos, double squaredDistance) {
        if (squaredDistance <= 576.0) {
            return false;
        }
        if (world.method_27911().isWithinDistance(new Vec3d((float)pos.getX() + 0.5f, pos.getY(), (float)pos.getZ() + 0.5f), 24.0)) {
            return false;
        }
        ChunkPos chunkPos = new ChunkPos(pos);
        return Objects.equals(chunkPos, chunk.getPos()) || world.getChunkManager().shouldTickChunk(chunkPos);
    }

    private static boolean canSpawn(ServerWorld world, EntityCategory category, StructureAccessor structureAccessor, ChunkGenerator<?> chunkGenerator, Biome.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance) {
        EntityType<?> entityType = spawnEntry.type;
        if (entityType.getCategory() == EntityCategory.MISC) {
            return false;
        }
        if (!entityType.isSpawnableFarFromPlayer() && squaredDistance > (double)(entityType.getImmediateDespawnRange() * entityType.getImmediateDespawnRange())) {
            return false;
        }
        if (!entityType.isSummonable() || !SpawnHelper.containsSpawnEntry(structureAccessor, chunkGenerator, category, spawnEntry, pos)) {
            return false;
        }
        SpawnRestriction.Location location = SpawnRestriction.getLocation(entityType);
        if (!SpawnHelper.canSpawn(location, world, pos, entityType)) {
            return false;
        }
        if (!SpawnRestriction.canSpawn(entityType, world, SpawnType.NATURAL, pos, world.random)) {
            return false;
        }
        return world.doesNotCollide(entityType.createSimpleBoundingBox((float)pos.getX() + 0.5f, pos.getY(), (float)pos.getZ() + 0.5f));
    }

    @Nullable
    private static MobEntity createMob(ServerWorld world, EntityType<?> type) {
        MobEntity mobEntity;
        try {
            Object entity = type.create(world);
            if (!(entity instanceof MobEntity)) {
                throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getId(type));
            }
            mobEntity = (MobEntity)entity;
        } catch (Exception exception) {
            LOGGER.warn("Failed to create mob", (Throwable)exception);
            return null;
        }
        return mobEntity;
    }

    private static boolean isValidSpawn(ServerWorld world, MobEntity entity, double squaredDistance) {
        if (squaredDistance > (double)(entity.getType().getImmediateDespawnRange() * entity.getType().getImmediateDespawnRange()) && entity.canImmediatelyDespawn(squaredDistance)) {
            return false;
        }
        return entity.canSpawn(world, SpawnType.NATURAL) && entity.canSpawn(world);
    }

    @Nullable
    private static Biome.SpawnEntry pickRandomSpawnEntry(StructureAccessor structureAccessor, ChunkGenerator<?> chunkGenerator, EntityCategory category, Random random, BlockPos pos) {
        List<Biome.SpawnEntry> list = chunkGenerator.getEntitySpawnList(structureAccessor, category, pos);
        if (list.isEmpty()) {
            return null;
        }
        return WeightedPicker.getRandom(random, list);
    }

    private static boolean containsSpawnEntry(StructureAccessor structureAccessor, ChunkGenerator<?> chunkGenerator, EntityCategory category, Biome.SpawnEntry spawnEntry, BlockPos pos) {
        return chunkGenerator.getEntitySpawnList(structureAccessor, category, pos).contains(spawnEntry);
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
        return !state.isIn(BlockTags.RAILS);
    }

    public static boolean canSpawn(SpawnRestriction.Location location, WorldView world, BlockPos pos, @Nullable EntityType<?> entityType) {
        if (location == SpawnRestriction.Location.NO_RESTRICTIONS) {
            return true;
        }
        if (entityType == null || !world.getWorldBorder().contains(pos)) {
            return false;
        }
        BlockState blockState = world.getBlockState(pos);
        FluidState fluidState = world.getFluidState(pos);
        BlockPos blockPos = pos.up();
        BlockPos blockPos2 = pos.down();
        switch (location) {
            case IN_WATER: {
                return fluidState.matches(FluidTags.WATER) && world.getFluidState(blockPos2).matches(FluidTags.WATER) && !world.getBlockState(blockPos).isSolidBlock(world, blockPos);
            }
            case IN_LAVA: {
                return fluidState.matches(FluidTags.LAVA) && world.getFluidState(blockPos2).matches(FluidTags.LAVA) && !world.getBlockState(blockPos).isSolidBlock(world, blockPos);
            }
        }
        BlockState blockState2 = world.getBlockState(blockPos2);
        if (!blockState2.allowsSpawning(world, blockPos2, entityType)) {
            return false;
        }
        return SpawnHelper.isClearForSpawn(world, pos, blockState, fluidState) && SpawnHelper.isClearForSpawn(world, blockPos, world.getBlockState(blockPos), world.getFluidState(blockPos));
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
                            entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnType.CHUNK_GENERATION, entityData, null);
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

    private static BlockPos getEntitySpawnPos(WorldView world, @Nullable EntityType<?> entityType, int x, int z) {
        BlockPos blockPos = new BlockPos(x, world.getTopY(SpawnRestriction.getHeightmapType(entityType), x, z), z);
        BlockPos blockPos2 = blockPos.down();
        if (world.getBlockState(blockPos2).canPathfindThrough(world, blockPos2, NavigationType.LAND)) {
            return blockPos2;
        }
        return blockPos;
    }

    @FunctionalInterface
    public static interface ChunkSource {
        public void query(long var1, Consumer<WorldChunk> var3);
    }

    @FunctionalInterface
    public static interface Runner {
        public void run(MobEntity var1, Chunk var2);
    }

    @FunctionalInterface
    public static interface Checker {
        public boolean test(EntityType<?> var1, BlockPos var2, Chunk var3);
    }

    public static class Info {
        private final int spawningChunkCount;
        private final Object2IntMap<EntityCategory> categoryToCount;
        private final GravityField densityField;
        private final Object2IntMap<EntityCategory> categoryToCountView;
        @Nullable
        private BlockPos cachedPos;
        @Nullable
        private EntityType<?> cachedEntityType;
        private double cachedDensityMass;

        private Info(int spawningChunkCount, Object2IntMap<EntityCategory> categoryToCount, GravityField densityField) {
            this.spawningChunkCount = spawningChunkCount;
            this.categoryToCount = categoryToCount;
            this.densityField = densityField;
            this.categoryToCountView = Object2IntMaps.unmodifiable(categoryToCount);
        }

        private boolean test(EntityType<?> type, BlockPos pos, Chunk chunk) {
            double d;
            this.cachedPos = pos;
            this.cachedEntityType = type;
            Biome biome = SpawnHelper.getBiomeDirectly(pos, chunk);
            Biome.SpawnDensity spawnDensity = biome.getSpawnDensity(type);
            if (spawnDensity == null) {
                this.cachedDensityMass = 0.0;
                return true;
            }
            this.cachedDensityMass = d = spawnDensity.getMass();
            double e = this.densityField.calculate(pos, d);
            return e <= spawnDensity.getGravityLimit();
        }

        private void run(MobEntity entity, Chunk chunk) {
            double d;
            EntityType<?> entityType = entity.getType();
            BlockPos blockPos = entity.getBlockPos();
            if (blockPos.equals(this.cachedPos) && entityType == this.cachedEntityType) {
                d = this.cachedDensityMass;
            } else {
                Biome biome = SpawnHelper.getBiomeDirectly(blockPos, chunk);
                Biome.SpawnDensity spawnDensity = biome.getSpawnDensity(entityType);
                if (spawnDensity == null) {
                    return;
                }
                d = spawnDensity.getMass();
            }
            this.densityField.addPoint(blockPos, d);
        }

        @Environment(value=EnvType.CLIENT)
        public int getSpawningChunkCount() {
            return this.spawningChunkCount;
        }

        public Object2IntMap<EntityCategory> getCategoryToCount() {
            return this.categoryToCountView;
        }

        private boolean isBelowCap(EntityCategory category) {
            int i = category.getSpawnCap() * this.spawningChunkCount / CHUNK_AREA;
            return this.categoryToCount.getInt((Object)category) < i;
        }
    }
}

