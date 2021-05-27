/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GravityField;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.DirectBiomeAccessType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class SpawnHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int field_30974 = 24;
    public static final int field_30972 = 8;
    public static final int field_30973 = 128;
    static final int CHUNK_AREA = (int)Math.pow(17.0, 2.0);
    private static final SpawnGroup[] SPAWNABLE_GROUPS = (SpawnGroup[])Stream.of(SpawnGroup.values()).filter(spawnGroup -> spawnGroup != SpawnGroup.MISC).toArray(SpawnGroup[]::new);

    private SpawnHelper() {
    }

    public static Info setupSpawn(int spawningChunkCount, Iterable<Entity> entities, ChunkSource chunkSource) {
        GravityField gravityField = new GravityField();
        Object2IntOpenHashMap<SpawnGroup> object2IntOpenHashMap = new Object2IntOpenHashMap<SpawnGroup>();
        for (Entity entity : entities) {
            SpawnGroup spawnGroup;
            MobEntity mobEntity;
            if (entity instanceof MobEntity && ((mobEntity = (MobEntity)entity).isPersistent() || mobEntity.cannotDespawn()) || (spawnGroup = entity.getType().getSpawnGroup()) == SpawnGroup.MISC) continue;
            BlockPos blockPos = entity.getBlockPos();
            long l = ChunkPos.toLong(ChunkSectionPos.getSectionCoord(blockPos.getX()), ChunkSectionPos.getSectionCoord(blockPos.getZ()));
            chunkSource.query(l, chunk -> {
                SpawnSettings.SpawnDensity spawnDensity = SpawnHelper.getBiomeDirectly(blockPos, chunk).getSpawnSettings().getSpawnDensity(entity.getType());
                if (spawnDensity != null) {
                    gravityField.addPoint(entity.getBlockPos(), spawnDensity.getMass());
                }
                object2IntOpenHashMap.addTo(spawnGroup, 1);
            });
        }
        return new Info(spawningChunkCount, object2IntOpenHashMap, gravityField);
    }

    static Biome getBiomeDirectly(BlockPos pos, Chunk chunk) {
        return DirectBiomeAccessType.INSTANCE.getBiome(0L, pos.getX(), pos.getY(), pos.getZ(), chunk.getBiomeArray());
    }

    public static void spawn(ServerWorld world, WorldChunk chunk, Info info, boolean spawnAnimals, boolean spawnMonsters, boolean rareSpawn) {
        world.getProfiler().push("spawner");
        for (SpawnGroup spawnGroup : SPAWNABLE_GROUPS) {
            if (!spawnAnimals && spawnGroup.isPeaceful() || !spawnMonsters && !spawnGroup.isPeaceful() || !rareSpawn && spawnGroup.isRare() || !info.isBelowCap(spawnGroup)) continue;
            SpawnHelper.spawnEntitiesInChunk(spawnGroup, world, chunk, info::test, info::run);
        }
        world.getProfiler().pop();
    }

    public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, WorldChunk chunk, Checker checker, Runner runner) {
        BlockPos blockPos = SpawnHelper.getSpawnPos(world, chunk);
        if (blockPos.getY() < world.getBottomY() + 1) {
            return;
        }
        SpawnHelper.spawnEntitiesInChunk(group, world, chunk, blockPos, checker, runner);
    }

    @Debug
    public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, BlockPos pos2) {
        SpawnHelper.spawnEntitiesInChunk(group, world, world.getChunk(pos2), pos2, (type, pos, chunk) -> true, (entity, chunk) -> {});
    }

    public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, Checker checker, Runner runner) {
        StructureAccessor structureAccessor = world.getStructureAccessor();
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
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
            SpawnSettings.SpawnEntry spawnEntry = null;
            EntityData entityData = null;
            int o = MathHelper.ceil(world.random.nextFloat() * 4.0f);
            int p = 0;
            for (int q = 0; q < o; ++q) {
                double f;
                mutable.set(l += world.random.nextInt(6) - world.random.nextInt(6), i, m += world.random.nextInt(6) - world.random.nextInt(6));
                double d = (double)l + 0.5;
                double e = (double)m + 0.5;
                PlayerEntity playerEntity = world.getClosestPlayer(d, (double)i, e, -1.0, false);
                if (playerEntity == null || !SpawnHelper.isAcceptableSpawnPosition(world, chunk, mutable, f = playerEntity.squaredDistanceTo(d, i, e))) continue;
                if (spawnEntry == null) {
                    Optional<SpawnSettings.SpawnEntry> optional = SpawnHelper.pickRandomSpawnEntry(world, structureAccessor, chunkGenerator, group, world.random, mutable);
                    if (!optional.isPresent()) continue block0;
                    spawnEntry = optional.get();
                    o = spawnEntry.minGroupSize + world.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
                }
                if (!SpawnHelper.canSpawn(world, group, structureAccessor, chunkGenerator, spawnEntry, mutable, f) || !checker.test(spawnEntry.type, mutable, chunk)) continue;
                MobEntity mobEntity = SpawnHelper.createMob(world, spawnEntry.type);
                if (mobEntity == null) {
                    return;
                }
                mobEntity.refreshPositionAndAngles(d, i, e, world.random.nextFloat() * 360.0f, 0.0f);
                if (!SpawnHelper.isValidSpawn(world, mobEntity, f)) continue;
                entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.NATURAL, entityData, null);
                ++p;
                world.spawnEntityAndPassengers(mobEntity);
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
        if (world.getSpawnPos().isWithinDistance(new Vec3d((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5), 24.0)) {
            return false;
        }
        return Objects.equals(new ChunkPos(pos), chunk.getPos()) || world.method_37118(pos);
    }

    private static boolean canSpawn(ServerWorld world, SpawnGroup group, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnSettings.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance) {
        EntityType<?> entityType = spawnEntry.type;
        if (entityType.getSpawnGroup() == SpawnGroup.MISC) {
            return false;
        }
        if (!entityType.isSpawnableFarFromPlayer() && squaredDistance > (double)(entityType.getSpawnGroup().getImmediateDespawnRange() * entityType.getSpawnGroup().getImmediateDespawnRange())) {
            return false;
        }
        if (!entityType.isSummonable() || !SpawnHelper.containsSpawnEntry(world, structureAccessor, chunkGenerator, group, spawnEntry, pos)) {
            return false;
        }
        SpawnRestriction.Location location = SpawnRestriction.getLocation(entityType);
        if (!SpawnHelper.canSpawn(location, world, pos, entityType)) {
            return false;
        }
        if (!SpawnRestriction.canSpawn(entityType, world, SpawnReason.NATURAL, pos, world.random)) {
            return false;
        }
        return world.isSpaceEmpty(entityType.createSimpleBoundingBox((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5));
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
        if (squaredDistance > (double)(entity.getType().getSpawnGroup().getImmediateDespawnRange() * entity.getType().getSpawnGroup().getImmediateDespawnRange()) && entity.canImmediatelyDespawn(squaredDistance)) {
            return false;
        }
        return entity.canSpawn(world, SpawnReason.NATURAL) && entity.canSpawn(world);
    }

    private static Optional<SpawnSettings.SpawnEntry> pickRandomSpawnEntry(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, Random random, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        if (spawnGroup == SpawnGroup.WATER_AMBIENT && biome.getCategory() == Biome.Category.RIVER && random.nextFloat() < 0.98f) {
            return Optional.empty();
        }
        return SpawnHelper.getSpawnEntries(world, structureAccessor, chunkGenerator, spawnGroup, pos, biome).getOrEmpty(random);
    }

    private static boolean containsSpawnEntry(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, SpawnSettings.SpawnEntry spawnEntry, BlockPos pos) {
        return SpawnHelper.getSpawnEntries(world, structureAccessor, chunkGenerator, spawnGroup, pos, null).getEntries().contains(spawnEntry);
    }

    private static Pool<SpawnSettings.SpawnEntry> getSpawnEntries(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, BlockPos pos, @Nullable Biome biome) {
        if (spawnGroup == SpawnGroup.MONSTER && world.getBlockState(pos.down()).isOf(Blocks.NETHER_BRICKS) && structureAccessor.getStructureAt(pos, false, StructureFeature.FORTRESS).hasChildren()) {
            return StructureFeature.FORTRESS.getMonsterSpawns();
        }
        return chunkGenerator.getEntitySpawnList(biome != null ? biome : world.getBiome(pos), structureAccessor, spawnGroup, pos);
    }

    private static BlockPos getSpawnPos(World world, WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX() + world.random.nextInt(16);
        int j = chunkPos.getStartZ() + world.random.nextInt(16);
        int k = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
        int l = MathHelper.nextBetween(world.random, world.getBottomY(), k);
        return new BlockPos(i, l, j);
    }

    public static boolean isClearForSpawn(BlockView blockView, BlockPos pos, BlockState state, FluidState fluidState, EntityType<?> entityType) {
        if (state.isFullCube(blockView, pos)) {
            return false;
        }
        if (state.emitsRedstonePower()) {
            return false;
        }
        if (!fluidState.isEmpty()) {
            return false;
        }
        if (state.isIn(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
            return false;
        }
        return !entityType.isInvalidSpawn(state);
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
                return fluidState.isIn(FluidTags.WATER) && world.getFluidState(blockPos2).isIn(FluidTags.WATER) && !world.getBlockState(blockPos).isSolidBlock(world, blockPos);
            }
            case IN_LAVA: {
                return fluidState.isIn(FluidTags.LAVA);
            }
        }
        BlockState blockState2 = world.getBlockState(blockPos2);
        if (!blockState2.allowsSpawning(world, blockPos2, entityType)) {
            return false;
        }
        return SpawnHelper.isClearForSpawn(world, pos, blockState, fluidState, entityType) && SpawnHelper.isClearForSpawn(world, blockPos, world.getBlockState(blockPos), world.getFluidState(blockPos), entityType);
    }

    public static void populateEntities(ServerWorldAccess world, Biome biome, ChunkPos chunkPos, Random random) {
        SpawnSettings spawnSettings = biome.getSpawnSettings();
        Pool<SpawnSettings.SpawnEntry> pool = spawnSettings.getSpawnEntries(SpawnGroup.CREATURE);
        if (pool.isEmpty()) {
            return;
        }
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        while (random.nextFloat() < spawnSettings.getCreatureSpawnProbability()) {
            Optional<SpawnSettings.SpawnEntry> optional = pool.getOrEmpty(random);
            if (!optional.isPresent()) continue;
            SpawnSettings.SpawnEntry spawnEntry = optional.get();
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
                    if (spawnEntry.type.isSummonable() && SpawnHelper.canSpawn(SpawnRestriction.getLocation(spawnEntry.type), world, blockPos, spawnEntry.type)) {
                        MobEntity mobEntity;
                        Object entity;
                        float f = spawnEntry.type.getWidth();
                        double d = MathHelper.clamp((double)l, (double)i + (double)f, (double)i + 16.0 - (double)f);
                        double e = MathHelper.clamp((double)m, (double)j + (double)f, (double)j + 16.0 - (double)f);
                        if (!world.isSpaceEmpty(spawnEntry.type.createSimpleBoundingBox(d, blockPos.getY(), e)) || !SpawnRestriction.canSpawn(spawnEntry.type, world, SpawnReason.CHUNK_GENERATION, new BlockPos(d, (double)blockPos.getY(), e), world.getRandom())) continue;
                        try {
                            entity = spawnEntry.type.create(world.toServerWorld());
                        } catch (Exception exception) {
                            LOGGER.warn("Failed to create mob", (Throwable)exception);
                            continue;
                        }
                        ((Entity)entity).refreshPositionAndAngles(d, blockPos.getY(), e, random.nextFloat() * 360.0f, 0.0f);
                        if (entity instanceof MobEntity && (mobEntity = (MobEntity)entity).canSpawn(world, SpawnReason.CHUNK_GENERATION) && mobEntity.canSpawn(world)) {
                            entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.CHUNK_GENERATION, entityData, null);
                            world.spawnEntityAndPassengers(mobEntity);
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

    private static BlockPos getEntitySpawnPos(WorldView world, EntityType<?> entityType, int x, int z) {
        Vec3i blockPos;
        int i = world.getTopY(SpawnRestriction.getHeightmapType(entityType), x, z);
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, i, z);
        if (world.getDimension().hasCeiling()) {
            do {
                mutable.move(Direction.DOWN);
            } while (!world.getBlockState(mutable).isAir());
            do {
                mutable.move(Direction.DOWN);
            } while (world.getBlockState(mutable).isAir() && mutable.getY() > world.getBottomY());
        }
        if (SpawnRestriction.getLocation(entityType) == SpawnRestriction.Location.ON_GROUND && world.getBlockState((BlockPos)(blockPos = mutable.down())).canPathfindThrough(world, (BlockPos)blockPos, NavigationType.LAND)) {
            return blockPos;
        }
        return mutable.toImmutable();
    }

    @FunctionalInterface
    public static interface ChunkSource {
        public void query(long var1, Consumer<WorldChunk> var3);
    }

    public static class Info {
        private final int spawningChunkCount;
        private final Object2IntOpenHashMap<SpawnGroup> groupToCount;
        private final GravityField densityField;
        private final Object2IntMap<SpawnGroup> groupToCountView;
        @Nullable
        private BlockPos cachedPos;
        @Nullable
        private EntityType<?> cachedEntityType;
        private double cachedDensityMass;

        Info(int i, Object2IntOpenHashMap<SpawnGroup> object2IntOpenHashMap, GravityField gravityField) {
            this.spawningChunkCount = i;
            this.groupToCount = object2IntOpenHashMap;
            this.densityField = gravityField;
            this.groupToCountView = Object2IntMaps.unmodifiable(object2IntOpenHashMap);
        }

        private boolean test(EntityType<?> type, BlockPos pos, Chunk chunk) {
            double d;
            this.cachedPos = pos;
            this.cachedEntityType = type;
            SpawnSettings.SpawnDensity spawnDensity = SpawnHelper.getBiomeDirectly(pos, chunk).getSpawnSettings().getSpawnDensity(type);
            if (spawnDensity == null) {
                this.cachedDensityMass = 0.0;
                return true;
            }
            this.cachedDensityMass = d = spawnDensity.getMass();
            double e = this.densityField.calculate(pos, d);
            return e <= spawnDensity.getGravityLimit();
        }

        private void run(MobEntity entity, Chunk chunk) {
            SpawnSettings.SpawnDensity spawnDensity;
            EntityType<?> entityType = entity.getType();
            BlockPos blockPos = entity.getBlockPos();
            double d = blockPos.equals(this.cachedPos) && entityType == this.cachedEntityType ? this.cachedDensityMass : ((spawnDensity = SpawnHelper.getBiomeDirectly(blockPos, chunk).getSpawnSettings().getSpawnDensity(entityType)) != null ? spawnDensity.getMass() : 0.0);
            this.densityField.addPoint(blockPos, d);
            this.groupToCount.addTo(entityType.getSpawnGroup(), 1);
        }

        public int getSpawningChunkCount() {
            return this.spawningChunkCount;
        }

        public Object2IntMap<SpawnGroup> getGroupToCount() {
            return this.groupToCountView;
        }

        boolean isBelowCap(SpawnGroup group) {
            int i = group.getCapacity() * this.spawningChunkCount / CHUNK_AREA;
            return this.groupToCount.getInt(group) < i;
        }
    }

    @FunctionalInterface
    public static interface Checker {
        public boolean test(EntityType<?> var1, BlockPos var2, Chunk var3);
    }

    @FunctionalInterface
    public static interface Runner {
        public void run(MobEntity var1, Chunk var2);
    }
}

