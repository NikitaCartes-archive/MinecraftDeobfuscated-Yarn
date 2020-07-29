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
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
    private static final int CHUNK_AREA = (int)Math.pow(17.0, 2.0);
    private static final SpawnGroup[] SPAWNABLE_GROUPS = (SpawnGroup[])Stream.of(SpawnGroup.values()).filter(spawnGroup -> spawnGroup != SpawnGroup.MISC).toArray(SpawnGroup[]::new);

    public static Info setupSpawn(int spawningChunkCount, Iterable<Entity> entities, ChunkSource chunkSource) {
        GravityField gravityField = new GravityField();
        Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
        for (Entity entity : entities) {
            SpawnGroup spawnGroup;
            MobEntity mobEntity;
            if (entity instanceof MobEntity && ((mobEntity = (MobEntity)entity).isPersistent() || mobEntity.cannotDespawn()) || (spawnGroup = entity.getType().getSpawnGroup()) == SpawnGroup.MISC) continue;
            BlockPos blockPos = entity.getBlockPos();
            long l = ChunkPos.toLong(blockPos.getX() >> 4, blockPos.getZ() >> 4);
            chunkSource.query(l, worldChunk -> {
                SpawnSettings.SpawnDensity spawnDensity = SpawnHelper.getBiomeDirectly(blockPos, worldChunk).getSpawnSettings().getSpawnDensity(entity.getType());
                if (spawnDensity != null) {
                    gravityField.addPoint(entity.getBlockPos(), spawnDensity.getMass());
                }
                object2IntOpenHashMap.addTo(spawnGroup, 1);
            });
        }
        return new Info(spawningChunkCount, object2IntOpenHashMap, gravityField);
    }

    private static Biome getBiomeDirectly(BlockPos pos, Chunk chunk) {
        return DirectBiomeAccessType.INSTANCE.getBiome(0L, pos.getX(), pos.getY(), pos.getZ(), chunk.getBiomeArray());
    }

    public static void spawn(ServerWorld world, WorldChunk chunk2, Info info, boolean spawnAnimals, boolean spawnMonsters, boolean shouldSpawnAnimals) {
        world.getProfiler().push("spawner");
        for (SpawnGroup spawnGroup : SPAWNABLE_GROUPS) {
            if (!spawnAnimals && spawnGroup.isPeaceful() || !spawnMonsters && !spawnGroup.isPeaceful() || !shouldSpawnAnimals && spawnGroup.isAnimal() || !info.isBelowCap(spawnGroup)) continue;
            SpawnHelper.spawnEntitiesInChunk(spawnGroup, world, chunk2, (entityType, blockPos, chunk) -> info.test(entityType, blockPos, chunk), (mobEntity, chunk) -> info.run(mobEntity, chunk));
        }
        world.getProfiler().pop();
    }

    public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, WorldChunk chunk, Checker checker, Runner runner) {
        BlockPos blockPos = SpawnHelper.getSpawnPos(world, chunk);
        if (blockPos.getY() < 1) {
            return;
        }
        SpawnHelper.spawnEntitiesInChunk(group, world, chunk, blockPos, checker, runner);
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
                    spawnEntry = SpawnHelper.pickRandomSpawnEntry(world, structureAccessor, chunkGenerator, group, world.random, mutable);
                    if (spawnEntry == null) continue block0;
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
        ChunkPos chunkPos = new ChunkPos(pos);
        return Objects.equals(chunkPos, chunk.getPos()) || world.getChunkManager().shouldTickChunk(chunkPos);
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
        return world.doesNotCollide(entityType.createSimpleBoundingBox((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5));
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

    @Nullable
    private static SpawnSettings.SpawnEntry pickRandomSpawnEntry(ServerWorld serverWorld, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, Random random, BlockPos blockPos) {
        Biome biome = serverWorld.getBiome(blockPos);
        if (spawnGroup == SpawnGroup.WATER_AMBIENT && biome.getCategory() == Biome.Category.RIVER && random.nextFloat() < 0.98f) {
            return null;
        }
        List<SpawnSettings.SpawnEntry> list = SpawnHelper.method_29950(serverWorld, structureAccessor, chunkGenerator, spawnGroup, blockPos, biome);
        if (list.isEmpty()) {
            return null;
        }
        return WeightedPicker.getRandom(random, list);
    }

    private static boolean containsSpawnEntry(ServerWorld serverWorld, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, SpawnSettings.SpawnEntry spawnEntry, BlockPos blockPos) {
        return SpawnHelper.method_29950(serverWorld, structureAccessor, chunkGenerator, spawnGroup, blockPos, null).contains(spawnEntry);
    }

    private static List<SpawnSettings.SpawnEntry> method_29950(ServerWorld serverWorld, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, BlockPos blockPos, @Nullable Biome biome) {
        if (spawnGroup == SpawnGroup.MONSTER && serverWorld.getBlockState(blockPos.down()).getBlock() == Blocks.NETHER_BRICKS && structureAccessor.getStructureAt(blockPos, false, StructureFeature.FORTRESS).hasChildren()) {
            return StructureFeature.FORTRESS.getMonsterSpawns();
        }
        return chunkGenerator.getEntitySpawnList(biome != null ? biome : serverWorld.getBiome(blockPos), structureAccessor, spawnGroup, blockPos);
    }

    private static BlockPos getSpawnPos(World world, WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX() + world.random.nextInt(16);
        int j = chunkPos.getStartZ() + world.random.nextInt(16);
        int k = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
        int l = world.random.nextInt(k + 1);
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

    public static void populateEntities(ServerWorldAccess serverWorldAccess, Biome biome, int chunkX, int chunkZ, Random random) {
        SpawnSettings spawnSettings = biome.getSpawnSettings();
        List<SpawnSettings.SpawnEntry> list = spawnSettings.getSpawnEntry(SpawnGroup.CREATURE);
        if (list.isEmpty()) {
            return;
        }
        int i = chunkX << 4;
        int j = chunkZ << 4;
        while (random.nextFloat() < spawnSettings.getCreatureSpawnProbability()) {
            SpawnSettings.SpawnEntry spawnEntry = WeightedPicker.getRandom(random, list);
            int k = spawnEntry.minGroupSize + random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
            EntityData entityData = null;
            int l = i + random.nextInt(16);
            int m = j + random.nextInt(16);
            int n = l;
            int o = m;
            for (int p = 0; p < k; ++p) {
                boolean bl = false;
                for (int q = 0; !bl && q < 4; ++q) {
                    BlockPos blockPos = SpawnHelper.getEntitySpawnPos(serverWorldAccess, spawnEntry.type, l, m);
                    if (spawnEntry.type.isSummonable() && SpawnHelper.canSpawn(SpawnRestriction.getLocation(spawnEntry.type), serverWorldAccess, blockPos, spawnEntry.type)) {
                        MobEntity mobEntity;
                        Object entity;
                        float f = spawnEntry.type.getWidth();
                        double d = MathHelper.clamp((double)l, (double)i + (double)f, (double)i + 16.0 - (double)f);
                        double e = MathHelper.clamp((double)m, (double)j + (double)f, (double)j + 16.0 - (double)f);
                        if (!serverWorldAccess.doesNotCollide(spawnEntry.type.createSimpleBoundingBox(d, blockPos.getY(), e)) || !SpawnRestriction.canSpawn(spawnEntry.type, serverWorldAccess, SpawnReason.CHUNK_GENERATION, new BlockPos(d, (double)blockPos.getY(), e), serverWorldAccess.getRandom())) continue;
                        try {
                            entity = spawnEntry.type.create(serverWorldAccess.toServerWorld());
                        } catch (Exception exception) {
                            LOGGER.warn("Failed to create mob", (Throwable)exception);
                            continue;
                        }
                        ((Entity)entity).refreshPositionAndAngles(d, blockPos.getY(), e, random.nextFloat() * 360.0f, 0.0f);
                        if (entity instanceof MobEntity && (mobEntity = (MobEntity)entity).canSpawn(serverWorldAccess, SpawnReason.CHUNK_GENERATION) && mobEntity.canSpawn(serverWorldAccess)) {
                            entityData = mobEntity.initialize(serverWorldAccess, serverWorldAccess.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.CHUNK_GENERATION, entityData, null);
                            serverWorldAccess.spawnEntityAndPassengers(mobEntity);
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
            } while (world.getBlockState(mutable).isAir() && mutable.getY() > 0);
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
        private final Object2IntOpenHashMap<SpawnGroup> groupToCount;
        private final GravityField densityField;
        private final Object2IntMap<SpawnGroup> groupToCountView;
        @Nullable
        private BlockPos cachedPos;
        @Nullable
        private EntityType<?> cachedEntityType;
        private double cachedDensityMass;

        private Info(int spawningChunkCount, Object2IntOpenHashMap<SpawnGroup> groupToCount, GravityField densityField) {
            this.spawningChunkCount = spawningChunkCount;
            this.groupToCount = groupToCount;
            this.densityField = densityField;
            this.groupToCountView = Object2IntMaps.unmodifiable(groupToCount);
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

        @Environment(value=EnvType.CLIENT)
        public int getSpawningChunkCount() {
            return this.spawningChunkCount;
        }

        public Object2IntMap<SpawnGroup> getGroupToCount() {
            return this.groupToCountView;
        }

        private boolean isBelowCap(SpawnGroup group) {
            int i = group.getCapacity() * this.spawningChunkCount / CHUNK_AREA;
            return this.groupToCount.getInt(group) < i;
        }
    }
}

