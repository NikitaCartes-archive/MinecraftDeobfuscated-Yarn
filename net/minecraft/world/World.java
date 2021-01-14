/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class World
implements WorldAccess,
AutoCloseable {
    protected static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<RegistryKey<World>> CODEC = Identifier.CODEC.xmap(RegistryKey.createKeyFactory(Registry.WORLD_KEY), RegistryKey::getValue);
    public static final RegistryKey<World> OVERWORLD = RegistryKey.of(Registry.WORLD_KEY, new Identifier("overworld"));
    public static final RegistryKey<World> NETHER = RegistryKey.of(Registry.WORLD_KEY, new Identifier("the_nether"));
    public static final RegistryKey<World> END = RegistryKey.of(Registry.WORLD_KEY, new Identifier("the_end"));
    private static final Direction[] DIRECTIONS = Direction.values();
    public final List<BlockEntity> blockEntities = Lists.newArrayList();
    public final List<BlockEntity> tickingBlockEntities = Lists.newArrayList();
    protected final List<BlockEntity> pendingBlockEntities = Lists.newArrayList();
    protected final List<BlockEntity> unloadedBlockEntities = Lists.newArrayList();
    private final Thread thread;
    private final boolean debugWorld;
    private int ambientDarkness;
    protected int lcgBlockSeed = new Random().nextInt();
    protected final int lcgBlockSeedIncrement = 1013904223;
    protected float rainGradientPrev;
    protected float rainGradient;
    protected float thunderGradientPrev;
    protected float thunderGradient;
    public final Random random = new Random();
    private final DimensionType dimension;
    protected final MutableWorldProperties properties;
    private final Supplier<Profiler> profiler;
    public final boolean isClient;
    protected boolean iteratingTickingBlockEntities;
    private final WorldBorder border;
    private final BiomeAccess biomeAccess;
    private final RegistryKey<World> registryKey;

    protected World(MutableWorldProperties properties, RegistryKey<World> registryRef, final DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        this.profiler = profiler;
        this.properties = properties;
        this.dimension = dimensionType;
        this.registryKey = registryRef;
        this.isClient = isClient;
        this.border = dimensionType.getCoordinateScale() != 1.0 ? new WorldBorder(){

            @Override
            public double getCenterX() {
                return super.getCenterX() / dimensionType.getCoordinateScale();
            }

            @Override
            public double getCenterZ() {
                return super.getCenterZ() / dimensionType.getCoordinateScale();
            }
        } : new WorldBorder();
        this.thread = Thread.currentThread();
        this.biomeAccess = new BiomeAccess(this, seed, dimensionType.getBiomeAccessType());
        this.debugWorld = debugWorld;
    }

    @Override
    public boolean isClient() {
        return this.isClient;
    }

    @Nullable
    public MinecraftServer getServer() {
        return null;
    }

    public static boolean isInBuildLimit(BlockPos blockPos) {
        return !World.isOutOfBuildLimitVertically(blockPos) && World.isValidHorizontally(blockPos);
    }

    public static boolean isValid(BlockPos pos) {
        return !World.isInvalidVertically(pos.getY()) && World.isValidHorizontally(pos);
    }

    private static boolean isValidHorizontally(BlockPos pos) {
        return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000;
    }

    private static boolean isInvalidVertically(int y) {
        return y < -20000000 || y >= 20000000;
    }

    public static boolean isOutOfBuildLimitVertically(BlockPos pos) {
        return World.isOutOfBuildLimitVertically(pos.getY());
    }

    public static boolean isOutOfBuildLimitVertically(int y) {
        return y < 0 || y >= 256;
    }

    public WorldChunk getWorldChunk(BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Override
    public WorldChunk getChunk(int i, int j) {
        return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        Chunk chunk = this.getChunkManager().getChunk(chunkX, chunkZ, leastStatus, create);
        if (chunk == null && create) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        }
        return chunk;
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        return this.setBlockState(pos, state, flags, 512);
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        if (World.isOutOfBuildLimitVertically(pos)) {
            return false;
        }
        if (!this.isClient && this.isDebugWorld()) {
            return false;
        }
        WorldChunk worldChunk = this.getWorldChunk(pos);
        Block block = state.getBlock();
        BlockState blockState = worldChunk.setBlockState(pos, state, (flags & 0x40) != 0);
        if (blockState != null) {
            BlockState blockState2 = this.getBlockState(pos);
            if ((flags & 0x80) == 0 && blockState2 != blockState && (blockState2.getOpacity(this, pos) != blockState.getOpacity(this, pos) || blockState2.getLuminance() != blockState.getLuminance() || blockState2.hasSidedTransparency() || blockState.hasSidedTransparency())) {
                this.getProfiler().push("queueCheckLight");
                this.getChunkManager().getLightingProvider().checkBlock(pos);
                this.getProfiler().pop();
            }
            if (blockState2 == state) {
                if (blockState != blockState2) {
                    this.scheduleBlockRerenderIfNeeded(pos, blockState, blockState2);
                }
                if ((flags & 2) != 0 && (!this.isClient || (flags & 4) == 0) && (this.isClient || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
                    this.updateListeners(pos, blockState, state, flags);
                }
                if ((flags & 1) != 0) {
                    this.updateNeighbors(pos, blockState.getBlock());
                    if (!this.isClient && state.hasComparatorOutput()) {
                        this.updateComparators(pos, block);
                    }
                }
                if ((flags & 0x10) == 0 && maxUpdateDepth > 0) {
                    int i = flags & 0xFFFFFFDE;
                    blockState.prepare(this, pos, i, maxUpdateDepth - 1);
                    state.updateNeighbors(this, pos, i, maxUpdateDepth - 1);
                    state.prepare(this, pos, i, maxUpdateDepth - 1);
                }
                this.onBlockChanged(pos, blockState, blockState2);
            }
            return true;
        }
        return false;
    }

    public void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock) {
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        FluidState fluidState = this.getFluidState(pos);
        return this.setBlockState(pos, fluidState.getBlockState(), 3 | (move ? 64 : 0));
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        BlockState blockState = this.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        }
        FluidState fluidState = this.getFluidState(pos);
        if (!(blockState.getBlock() instanceof AbstractFireBlock)) {
            this.syncWorldEvent(2001, pos, Block.getRawIdFromState(blockState));
        }
        if (drop) {
            BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.getBlockEntity(pos) : null;
            Block.dropStacks(blockState, this, pos, blockEntity, breakingEntity, ItemStack.EMPTY);
        }
        return this.setBlockState(pos, fluidState.getBlockState(), 3, maxUpdateDepth);
    }

    public boolean setBlockState(BlockPos pos, BlockState state) {
        return this.setBlockState(pos, state, 3);
    }

    public abstract void updateListeners(BlockPos var1, BlockState var2, BlockState var3, int var4);

    public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
    }

    public void updateNeighborsAlways(BlockPos pos, Block block) {
        this.updateNeighbor(pos.west(), block, pos);
        this.updateNeighbor(pos.east(), block, pos);
        this.updateNeighbor(pos.down(), block, pos);
        this.updateNeighbor(pos.up(), block, pos);
        this.updateNeighbor(pos.north(), block, pos);
        this.updateNeighbor(pos.south(), block, pos);
    }

    public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction direction) {
        if (direction != Direction.WEST) {
            this.updateNeighbor(pos.west(), sourceBlock, pos);
        }
        if (direction != Direction.EAST) {
            this.updateNeighbor(pos.east(), sourceBlock, pos);
        }
        if (direction != Direction.DOWN) {
            this.updateNeighbor(pos.down(), sourceBlock, pos);
        }
        if (direction != Direction.UP) {
            this.updateNeighbor(pos.up(), sourceBlock, pos);
        }
        if (direction != Direction.NORTH) {
            this.updateNeighbor(pos.north(), sourceBlock, pos);
        }
        if (direction != Direction.SOUTH) {
            this.updateNeighbor(pos.south(), sourceBlock, pos);
        }
    }

    public void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos neighborPos) {
        if (this.isClient) {
            return;
        }
        BlockState blockState = this.getBlockState(pos);
        try {
            blockState.neighborUpdate(this, pos, sourceBlock, neighborPos, false);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Exception while updating neighbours");
            CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
            crashReportSection.add("Source block type", () -> {
                try {
                    return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
                } catch (Throwable throwable) {
                    return "ID #" + Registry.BLOCK.getId(sourceBlock);
                }
            });
            CrashReportSection.addBlockInfo(crashReportSection, pos, blockState);
            throw new CrashException(crashReport);
        }
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        int i = x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000 ? this.getSeaLevel() + 1 : (this.isChunkLoaded(x >> 4, z >> 4) ? this.getChunk(x >> 4, z >> 4).sampleHeightmap(heightmap, x & 0xF, z & 0xF) + 1 : 0);
        return i;
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.getChunkManager().getLightingProvider();
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (World.isOutOfBuildLimitVertically(pos)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        WorldChunk worldChunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        return worldChunk.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (World.isOutOfBuildLimitVertically(pos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        WorldChunk worldChunk = this.getWorldChunk(pos);
        return worldChunk.getFluidState(pos);
    }

    public boolean isDay() {
        return !this.getDimension().hasFixedTime() && this.ambientDarkness < 4;
    }

    public boolean isNight() {
        return !this.getDimension().hasFixedTime() && !this.isDay();
    }

    @Override
    public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.playSound(player, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, category, volume, pitch);
    }

    public abstract void playSound(@Nullable PlayerEntity var1, double var2, double var4, double var6, SoundEvent var8, SoundCategory var9, float var10, float var11);

    public abstract void playSoundFromEntity(@Nullable PlayerEntity var1, Entity var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

    public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    @Environment(value=EnvType.CLIENT)
    public void addParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public void addImportantParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public float getSkyAngleRadians(float tickDelta) {
        float f = this.getSkyAngle(tickDelta);
        return f * ((float)Math.PI * 2);
    }

    public boolean addBlockEntity(BlockEntity blockEntity) {
        boolean bl;
        if (this.iteratingTickingBlockEntities) {
            org.apache.logging.log4j.util.Supplier[] supplierArray = new org.apache.logging.log4j.util.Supplier[2];
            supplierArray[0] = () -> Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType());
            supplierArray[1] = blockEntity::getPos;
            LOGGER.error("Adding block entity while ticking: {} @ {}", supplierArray);
        }
        if ((bl = this.blockEntities.add(blockEntity)) && blockEntity instanceof Tickable) {
            this.tickingBlockEntities.add(blockEntity);
        }
        if (this.isClient) {
            BlockPos blockPos = blockEntity.getPos();
            BlockState blockState = this.getBlockState(blockPos);
            this.updateListeners(blockPos, blockState, blockState, 2);
        }
        return bl;
    }

    public void addBlockEntities(Collection<BlockEntity> blockEntities) {
        if (this.iteratingTickingBlockEntities) {
            this.pendingBlockEntities.addAll(blockEntities);
        } else {
            for (BlockEntity blockEntity : blockEntities) {
                this.addBlockEntity(blockEntity);
            }
        }
    }

    public void tickBlockEntities() {
        Profiler profiler = this.getProfiler();
        profiler.push("blockEntities");
        if (!this.unloadedBlockEntities.isEmpty()) {
            this.tickingBlockEntities.removeAll(this.unloadedBlockEntities);
            this.blockEntities.removeAll(this.unloadedBlockEntities);
            this.unloadedBlockEntities.clear();
        }
        this.iteratingTickingBlockEntities = true;
        Iterator<BlockEntity> iterator = this.tickingBlockEntities.iterator();
        while (iterator.hasNext()) {
            BlockEntity blockEntity = iterator.next();
            if (!blockEntity.isRemoved() && blockEntity.hasWorld()) {
                BlockPos blockPos = blockEntity.getPos();
                if (this.getChunkManager().shouldTickBlock(blockPos) && this.getWorldBorder().contains(blockPos)) {
                    try {
                        profiler.push(() -> String.valueOf(BlockEntityType.getId(blockEntity.getType())));
                        if (blockEntity.getType().supports(this.getBlockState(blockPos).getBlock())) {
                            ((Tickable)((Object)blockEntity)).tick();
                        } else {
                            blockEntity.markInvalid();
                        }
                        profiler.pop();
                    } catch (Throwable throwable) {
                        CrashReport crashReport = CrashReport.create(throwable, "Ticking block entity");
                        CrashReportSection crashReportSection = crashReport.addElement("Block entity being ticked");
                        blockEntity.populateCrashReport(crashReportSection);
                        throw new CrashException(crashReport);
                    }
                }
            }
            if (!blockEntity.isRemoved()) continue;
            iterator.remove();
            this.blockEntities.remove(blockEntity);
            if (!this.isChunkLoaded(blockEntity.getPos())) continue;
            this.getWorldChunk(blockEntity.getPos()).removeBlockEntity(blockEntity.getPos());
        }
        this.iteratingTickingBlockEntities = false;
        profiler.swap("pendingBlockEntities");
        if (!this.pendingBlockEntities.isEmpty()) {
            for (int i = 0; i < this.pendingBlockEntities.size(); ++i) {
                BlockEntity blockEntity2 = this.pendingBlockEntities.get(i);
                if (blockEntity2.isRemoved()) continue;
                if (!this.blockEntities.contains(blockEntity2)) {
                    this.addBlockEntity(blockEntity2);
                }
                if (!this.isChunkLoaded(blockEntity2.getPos())) continue;
                WorldChunk worldChunk = this.getWorldChunk(blockEntity2.getPos());
                BlockState blockState = worldChunk.getBlockState(blockEntity2.getPos());
                worldChunk.setBlockEntity(blockEntity2.getPos(), blockEntity2);
                this.updateListeners(blockEntity2.getPos(), blockState, blockState, 3);
            }
            this.pendingBlockEntities.clear();
        }
        profiler.pop();
    }

    public void tickEntity(Consumer<Entity> tickConsumer, Entity entity) {
        try {
            tickConsumer.accept(entity);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Ticking entity");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being ticked");
            entity.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, Explosion.DestructionType destructionType) {
        return this.createExplosion(entity, null, null, x, y, z, power, false, destructionType);
    }

    public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType) {
        return this.createExplosion(entity, null, null, x, y, z, power, createFire, destructionType);
    }

    public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType) {
        Explosion explosion = new Explosion(this, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
        return explosion;
    }

    public String getDebugString() {
        return this.getChunkManager().getDebugString();
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        if (World.isOutOfBuildLimitVertically(pos)) {
            return null;
        }
        if (!this.isClient && Thread.currentThread() != this.thread) {
            return null;
        }
        BlockEntity blockEntity = null;
        if (this.iteratingTickingBlockEntities) {
            blockEntity = this.getPendingBlockEntity(pos);
        }
        if (blockEntity == null) {
            blockEntity = this.getWorldChunk(pos).getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE);
        }
        if (blockEntity == null) {
            blockEntity = this.getPendingBlockEntity(pos);
        }
        return blockEntity;
    }

    @Nullable
    private BlockEntity getPendingBlockEntity(BlockPos pos) {
        for (int i = 0; i < this.pendingBlockEntities.size(); ++i) {
            BlockEntity blockEntity = this.pendingBlockEntities.get(i);
            if (blockEntity.isRemoved() || !blockEntity.getPos().equals(pos)) continue;
            return blockEntity;
        }
        return null;
    }

    public void setBlockEntity(BlockPos pos, @Nullable BlockEntity blockEntity) {
        if (World.isOutOfBuildLimitVertically(pos)) {
            return;
        }
        if (blockEntity != null && !blockEntity.isRemoved()) {
            if (this.iteratingTickingBlockEntities) {
                blockEntity.setLocation(this, pos);
                Iterator<BlockEntity> iterator = this.pendingBlockEntities.iterator();
                while (iterator.hasNext()) {
                    BlockEntity blockEntity2 = iterator.next();
                    if (!blockEntity2.getPos().equals(pos)) continue;
                    blockEntity2.markRemoved();
                    iterator.remove();
                }
                this.pendingBlockEntities.add(blockEntity);
            } else {
                this.getWorldChunk(pos).setBlockEntity(pos, blockEntity);
                this.addBlockEntity(blockEntity);
            }
        }
    }

    public void removeBlockEntity(BlockPos pos) {
        BlockEntity blockEntity = this.getBlockEntity(pos);
        if (blockEntity != null && this.iteratingTickingBlockEntities) {
            blockEntity.markRemoved();
            this.pendingBlockEntities.remove(blockEntity);
        } else {
            if (blockEntity != null) {
                this.pendingBlockEntities.remove(blockEntity);
                this.blockEntities.remove(blockEntity);
                this.tickingBlockEntities.remove(blockEntity);
            }
            this.getWorldChunk(pos).removeBlockEntity(pos);
        }
    }

    public boolean canSetBlock(BlockPos pos) {
        if (World.isOutOfBuildLimitVertically(pos)) {
            return false;
        }
        return this.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public boolean isDirectionSolid(BlockPos pos, Entity entity, Direction direction) {
        if (World.isOutOfBuildLimitVertically(pos)) {
            return false;
        }
        Chunk chunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        if (chunk == null) {
            return false;
        }
        return chunk.getBlockState(pos).hasSolidTopSurface(this, pos, entity, direction);
    }

    public boolean isTopSolid(BlockPos pos, Entity entity) {
        return this.isDirectionSolid(pos, entity, Direction.UP);
    }

    public void calculateAmbientDarkness() {
        double d = 1.0 - (double)(this.getRainGradient(1.0f) * 5.0f) / 16.0;
        double e = 1.0 - (double)(this.getThunderGradient(1.0f) * 5.0f) / 16.0;
        double f = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.getSkyAngle(1.0f) * ((float)Math.PI * 2)), -0.25, 0.25);
        this.ambientDarkness = (int)((1.0 - f * d * e) * 11.0);
    }

    public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
        this.getChunkManager().setMobSpawnOptions(spawnMonsters, spawnAnimals);
    }

    protected void initWeatherGradients() {
        if (this.properties.isRaining()) {
            this.rainGradient = 1.0f;
            if (this.properties.isThundering()) {
                this.thunderGradient = 1.0f;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.getChunkManager().close();
    }

    @Override
    @Nullable
    public BlockView getChunkAsView(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
    }

    @Override
    public List<Entity> getOtherEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate) {
        this.getProfiler().visit("getEntities");
        ArrayList<Entity> list = Lists.newArrayList();
        int i = MathHelper.floor((box.minX - 2.0) / 16.0);
        int j = MathHelper.floor((box.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
        int l = MathHelper.floor((box.maxZ + 2.0) / 16.0);
        ChunkManager chunkManager = this.getChunkManager();
        for (int m = i; m <= j; ++m) {
            for (int n = k; n <= l; ++n) {
                WorldChunk worldChunk = chunkManager.getWorldChunk(m, n, false);
                if (worldChunk == null) continue;
                worldChunk.collectOtherEntities(except, box, list, predicate);
            }
        }
        return list;
    }

    /**
     * Computes a list of entities of the given type within some region that satisfy the given predicate.
     * 
     * <strong>Warning:<strong> If {@code null} is passed as the entity type filter, care should be
     * taken that the type argument {@code T} is set to {@link Entity}, otherwise heap pollution
     * in the output list or {@link ClassCastException} can occur.
     * 
     * @return a list of entities
     * 
     * @param type the entity type of the returned entities, or {@code null} to return entities of all types
     * @param box the box in which to search for entities
     * @param predicate a predicate which entities must satisfy in order to be considered
     */
    public <T extends Entity> List<T> getEntitiesByType(@Nullable EntityType<T> type, Box box, Predicate<? super T> predicate) {
        this.getProfiler().visit("getEntities");
        int i = MathHelper.floor((box.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk worldChunk = this.getChunkManager().getWorldChunk(m, n, false);
                if (worldChunk == null) continue;
                worldChunk.collectEntities(type, box, list, predicate);
            }
        }
        return list;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesByClass(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
        this.getProfiler().visit("getEntities");
        int i = MathHelper.floor((box.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        ChunkManager chunkManager = this.getChunkManager();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk worldChunk = chunkManager.getWorldChunk(m, n, false);
                if (worldChunk == null) continue;
                worldChunk.collectEntitiesByClass(entityClass, box, list, predicate);
            }
        }
        return list;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
        this.getProfiler().visit("getLoadedEntities");
        int i = MathHelper.floor((box.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        ChunkManager chunkManager = this.getChunkManager();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk worldChunk = chunkManager.getWorldChunk(m, n);
                if (worldChunk == null) continue;
                worldChunk.collectEntitiesByClass(entityClass, box, list, predicate);
            }
        }
        return list;
    }

    @Nullable
    public abstract Entity getEntityById(int var1);

    public void markDirty(BlockPos pos, BlockEntity blockEntity) {
        if (this.isChunkLoaded(pos)) {
            this.getWorldChunk(pos).markDirty();
        }
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    public int getReceivedStrongRedstonePower(BlockPos pos) {
        int i = 0;
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.down(), Direction.DOWN))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.up(), Direction.UP))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.north(), Direction.NORTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.south(), Direction.SOUTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.west(), Direction.WEST))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.east(), Direction.EAST))) >= 15) {
            return i;
        }
        return i;
    }

    public boolean isEmittingRedstonePower(BlockPos pos, Direction direction) {
        return this.getEmittedRedstonePower(pos, direction) > 0;
    }

    public int getEmittedRedstonePower(BlockPos pos, Direction direction) {
        BlockState blockState = this.getBlockState(pos);
        int i = blockState.getWeakRedstonePower(this, pos, direction);
        if (blockState.isSolidBlock(this, pos)) {
            return Math.max(i, this.getReceivedStrongRedstonePower(pos));
        }
        return i;
    }

    public boolean isReceivingRedstonePower(BlockPos pos) {
        if (this.getEmittedRedstonePower(pos.down(), Direction.DOWN) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.up(), Direction.UP) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.north(), Direction.NORTH) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.south(), Direction.SOUTH) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.west(), Direction.WEST) > 0) {
            return true;
        }
        return this.getEmittedRedstonePower(pos.east(), Direction.EAST) > 0;
    }

    public int getReceivedRedstonePower(BlockPos pos) {
        int i = 0;
        for (Direction direction : DIRECTIONS) {
            int j = this.getEmittedRedstonePower(pos.offset(direction), direction);
            if (j >= 15) {
                return 15;
            }
            if (j <= i) continue;
            i = j;
        }
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    public void disconnect() {
    }

    public long getTime() {
        return this.properties.getTime();
    }

    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }

    public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
        return true;
    }

    public void sendEntityStatus(Entity entity, byte status) {
    }

    public void addSyncedBlockEvent(BlockPos pos, Block block, int type, int data) {
        this.getBlockState(pos).onSyncedBlockEvent(this, pos, type, data);
    }

    @Override
    public WorldProperties getLevelProperties() {
        return this.properties;
    }

    public GameRules getGameRules() {
        return this.properties.getGameRules();
    }

    public float getThunderGradient(float delta) {
        return MathHelper.lerp(delta, this.thunderGradientPrev, this.thunderGradient) * this.getRainGradient(delta);
    }

    @Environment(value=EnvType.CLIENT)
    public void setThunderGradient(float thunderGradient) {
        this.thunderGradientPrev = thunderGradient;
        this.thunderGradient = thunderGradient;
    }

    public float getRainGradient(float delta) {
        return MathHelper.lerp(delta, this.rainGradientPrev, this.rainGradient);
    }

    @Environment(value=EnvType.CLIENT)
    public void setRainGradient(float rainGradient) {
        this.rainGradientPrev = rainGradient;
        this.rainGradient = rainGradient;
    }

    public boolean isThundering() {
        if (!this.getDimension().hasSkyLight() || this.getDimension().hasCeiling()) {
            return false;
        }
        return (double)this.getThunderGradient(1.0f) > 0.9;
    }

    public boolean isRaining() {
        return (double)this.getRainGradient(1.0f) > 0.2;
    }

    public boolean hasRain(BlockPos pos) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.isSkyVisible(pos)) {
            return false;
        }
        if (this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        Biome biome = this.getBiome(pos);
        return biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.getTemperature(pos) >= 0.15f;
    }

    public boolean hasHighHumidity(BlockPos pos) {
        Biome biome = this.getBiome(pos);
        return biome.hasHighHumidity();
    }

    @Nullable
    public abstract MapState getMapState(String var1);

    public abstract void putMapState(MapState var1);

    public abstract int getNextMapId();

    public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
    }

    public CrashReportSection addDetailsToCrashReport(CrashReport report) {
        CrashReportSection crashReportSection = report.addElement("Affected level", 1);
        crashReportSection.add("All players", () -> this.getPlayers().size() + " total; " + this.getPlayers());
        crashReportSection.add("Chunk stats", this.getChunkManager()::getDebugString);
        crashReportSection.add("Level dimension", () -> this.getRegistryKey().getValue().toString());
        try {
            this.properties.populateCrashReport(crashReportSection);
        } catch (Throwable throwable) {
            crashReportSection.add("Level Data Unobtainable", throwable);
        }
        return crashReportSection;
    }

    public abstract void setBlockBreakingInfo(int var1, BlockPos var2, int var3);

    @Environment(value=EnvType.CLIENT)
    public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable NbtCompound nbt) {
    }

    public abstract Scoreboard getScoreboard();

    public void updateComparators(BlockPos pos, Block block) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (!this.isChunkLoaded(blockPos)) continue;
            BlockState blockState = this.getBlockState(blockPos);
            if (blockState.isOf(Blocks.COMPARATOR)) {
                blockState.neighborUpdate(this, blockPos, block, pos, false);
                continue;
            }
            if (!blockState.isSolidBlock(this, blockPos) || !(blockState = this.getBlockState(blockPos = blockPos.offset(direction))).isOf(Blocks.COMPARATOR)) continue;
            blockState.neighborUpdate(this, blockPos, block, pos, false);
        }
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        long l = 0L;
        float f = 0.0f;
        if (this.isChunkLoaded(pos)) {
            f = this.getMoonSize();
            l = this.getWorldChunk(pos).getInhabitedTime();
        }
        return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), l, f);
    }

    @Override
    public int getAmbientDarkness() {
        return this.ambientDarkness;
    }

    public void setLightningTicksLeft(int lightningTicksLeft) {
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.border;
    }

    public void sendPacket(Packet<?> packet) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    @Override
    public DimensionType getDimension() {
        return this.dimension;
    }

    public RegistryKey<World> getRegistryKey() {
        return this.registryKey;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
        return state.test(this.getBlockState(pos));
    }

    public abstract RecipeManager getRecipeManager();

    public abstract TagManager getTagManager();

    public BlockPos getRandomPosInChunk(int x, int y, int z, int i) {
        this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
        int j = this.lcgBlockSeed >> 2;
        return new BlockPos(x + (j & 0xF), y + (j >> 16 & i), z + (j >> 8 & 0xF));
    }

    public boolean isSavingDisabled() {
        return false;
    }

    public Profiler getProfiler() {
        return this.profiler.get();
    }

    public Supplier<Profiler> getProfilerSupplier() {
        return this.profiler;
    }

    @Override
    public BiomeAccess getBiomeAccess() {
        return this.biomeAccess;
    }

    /**
     * Checks if this world is a debug world.
     * 
     * <p>Debug worlds are not modifiable and are typically meant for development and debug use only.
     * See <a href="https://minecraft.gamepedia.com/Debug_mode">the minecraft wiki</a> as well.
     */
    public final boolean isDebugWorld() {
        return this.debugWorld;
    }

    @Override
    public /* synthetic */ Chunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ);
    }
}

