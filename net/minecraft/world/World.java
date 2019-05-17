/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.util.MaterialPredicate;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;
import org.jetbrains.annotations.Nullable;

public abstract class World
implements ExtendedBlockView,
IWorld,
AutoCloseable {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Direction[] DIRECTIONS = Direction.values();
    public final List<BlockEntity> blockEntities = Lists.newArrayList();
    public final List<BlockEntity> tickingBlockEntities = Lists.newArrayList();
    protected final List<BlockEntity> pendingBlockEntities = Lists.newArrayList();
    protected final List<BlockEntity> unloadedBlockEntities = Lists.newArrayList();
    private final long unusedWhite = 0xFFFFFFL;
    private final Thread thread;
    private int ambientDarkness;
    protected int lcgBlockSeed = new Random().nextInt();
    protected final int unusedIncrement = 1013904223;
    protected float rainGradientPrev;
    protected float rainGradient;
    protected float thunderGradientPrev;
    protected float thunderGradient;
    private int ticksSinceLightning;
    public final Random random = new Random();
    public final Dimension dimension;
    protected final ChunkManager chunkManager;
    protected final LevelProperties properties;
    private final Profiler profiler;
    public final boolean isClient;
    protected boolean iteratingTickingBlockEntities;
    private final WorldBorder border;

    protected World(LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> biFunction, Profiler profiler, boolean bl) {
        this.profiler = profiler;
        this.properties = levelProperties;
        this.dimension = dimensionType.create(this);
        this.chunkManager = biFunction.apply(this, this.dimension);
        this.isClient = bl;
        this.border = this.dimension.createWorldBorder();
        this.thread = Thread.currentThread();
    }

    @Override
    public Biome getBiome(BlockPos blockPos) {
        ChunkManager chunkManager = this.getChunkManager();
        WorldChunk worldChunk = chunkManager.getWorldChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, false);
        if (worldChunk != null) {
            return worldChunk.getBiome(blockPos);
        }
        ChunkGenerator<?> chunkGenerator = this.getChunkManager().getChunkGenerator();
        if (chunkGenerator == null) {
            return Biomes.PLAINS;
        }
        return chunkGenerator.getBiomeSource().getBiome(blockPos);
    }

    @Override
    public boolean isClient() {
        return this.isClient;
    }

    @Nullable
    public MinecraftServer getServer() {
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public void setDefaultSpawnClient() {
        this.setSpawnPos(new BlockPos(8, 64, 8));
    }

    public BlockState getTopNonAirState(BlockPos blockPos) {
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());
        while (!this.isAir(blockPos2.up())) {
            blockPos2 = blockPos2.up();
        }
        return this.getBlockState(blockPos2);
    }

    public static boolean isValid(BlockPos blockPos) {
        return !World.isHeightInvalid(blockPos) && blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000;
    }

    public static boolean isHeightInvalid(BlockPos blockPos) {
        return World.isHeightInvalid(blockPos.getY());
    }

    public static boolean isHeightInvalid(int i) {
        return i < 0 || i >= 256;
    }

    public WorldChunk getWorldChunk(BlockPos blockPos) {
        return this.method_8497(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    public WorldChunk method_8497(int i, int j) {
        return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
    }

    @Override
    public Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
        Chunk chunk = this.chunkManager.getChunk(i, j, chunkStatus, bl);
        if (chunk == null && bl) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        }
        return chunk;
    }

    @Override
    public boolean setBlockState(BlockPos blockPos, BlockState blockState, int i) {
        if (World.isHeightInvalid(blockPos)) {
            return false;
        }
        if (!this.isClient && this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            return false;
        }
        WorldChunk worldChunk = this.getWorldChunk(blockPos);
        Block block = blockState.getBlock();
        BlockState blockState2 = worldChunk.setBlockState(blockPos, blockState, (i & 0x40) != 0);
        if (blockState2 != null) {
            BlockState blockState3 = this.getBlockState(blockPos);
            if (blockState3 != blockState2 && (blockState3.getLightSubtracted(this, blockPos) != blockState2.getLightSubtracted(this, blockPos) || blockState3.getLuminance() != blockState2.getLuminance() || blockState3.hasSidedTransparency() || blockState2.hasSidedTransparency())) {
                this.profiler.push("queueCheckLight");
                this.getChunkManager().getLightingProvider().enqueueLightUpdate(blockPos);
                this.profiler.pop();
            }
            if (blockState3 == blockState) {
                if (blockState2 != blockState3) {
                    this.scheduleBlockRender(blockPos);
                }
                if ((i & 2) != 0 && (!this.isClient || (i & 4) == 0) && (this.isClient || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
                    this.updateListeners(blockPos, blockState2, blockState, i);
                }
                if (!this.isClient && (i & 1) != 0) {
                    this.updateNeighbors(blockPos, blockState2.getBlock());
                    if (blockState.hasComparatorOutput()) {
                        this.updateHorizontalAdjacent(blockPos, block);
                    }
                }
                if ((i & 0x10) == 0) {
                    int j = i & 0xFFFFFFFE;
                    blockState2.method_11637(this, blockPos, j);
                    blockState.updateNeighborStates(this, blockPos, j);
                    blockState.method_11637(this, blockPos, j);
                }
                this.onBlockChanged(blockPos, blockState2, blockState3);
            }
            return true;
        }
        return false;
    }

    public void onBlockChanged(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
    }

    @Override
    public boolean clearBlockState(BlockPos blockPos, boolean bl) {
        FluidState fluidState = this.getFluidState(blockPos);
        return this.setBlockState(blockPos, fluidState.getBlockState(), 3 | (bl ? 64 : 0));
    }

    @Override
    public boolean breakBlock(BlockPos blockPos, boolean bl) {
        BlockState blockState = this.getBlockState(blockPos);
        if (blockState.isAir()) {
            return false;
        }
        FluidState fluidState = this.getFluidState(blockPos);
        this.playLevelEvent(2001, blockPos, Block.getRawIdFromState(blockState));
        if (bl) {
            BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.getBlockEntity(blockPos) : null;
            Block.dropStacks(blockState, this, blockPos, blockEntity);
        }
        return this.setBlockState(blockPos, fluidState.getBlockState(), 3);
    }

    public boolean setBlockState(BlockPos blockPos, BlockState blockState) {
        return this.setBlockState(blockPos, blockState, 3);
    }

    public abstract void updateListeners(BlockPos var1, BlockState var2, BlockState var3, int var4);

    @Override
    public void updateNeighbors(BlockPos blockPos, Block block) {
        if (this.properties.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            this.updateNeighborsAlways(blockPos, block);
        }
    }

    public void scheduleBlockRender(BlockPos blockPos) {
    }

    public void updateNeighborsAlways(BlockPos blockPos, Block block) {
        this.updateNeighbor(blockPos.west(), block, blockPos);
        this.updateNeighbor(blockPos.east(), block, blockPos);
        this.updateNeighbor(blockPos.down(), block, blockPos);
        this.updateNeighbor(blockPos.up(), block, blockPos);
        this.updateNeighbor(blockPos.north(), block, blockPos);
        this.updateNeighbor(blockPos.south(), block, blockPos);
    }

    public void updateNeighborsExcept(BlockPos blockPos, Block block, Direction direction) {
        if (direction != Direction.WEST) {
            this.updateNeighbor(blockPos.west(), block, blockPos);
        }
        if (direction != Direction.EAST) {
            this.updateNeighbor(blockPos.east(), block, blockPos);
        }
        if (direction != Direction.DOWN) {
            this.updateNeighbor(blockPos.down(), block, blockPos);
        }
        if (direction != Direction.UP) {
            this.updateNeighbor(blockPos.up(), block, blockPos);
        }
        if (direction != Direction.NORTH) {
            this.updateNeighbor(blockPos.north(), block, blockPos);
        }
        if (direction != Direction.SOUTH) {
            this.updateNeighbor(blockPos.south(), block, blockPos);
        }
    }

    public void updateNeighbor(BlockPos blockPos, Block block, BlockPos blockPos2) {
        if (this.isClient) {
            return;
        }
        BlockState blockState = this.getBlockState(blockPos);
        try {
            blockState.neighborUpdate(this, blockPos, block, blockPos2, false);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Exception while updating neighbours");
            CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
            crashReportSection.add("Source block type", () -> {
                try {
                    return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(block), block.getTranslationKey(), block.getClass().getCanonicalName());
                } catch (Throwable throwable) {
                    return "ID #" + Registry.BLOCK.getId(block);
                }
            });
            CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
            throw new CrashException(crashReport);
        }
    }

    @Override
    public int getLightLevel(BlockPos blockPos, int i) {
        if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
            return 15;
        }
        if (blockPos.getY() < 0) {
            return 0;
        }
        if (blockPos.getY() >= 256) {
            blockPos = new BlockPos(blockPos.getX(), 255, blockPos.getZ());
        }
        return this.getWorldChunk(blockPos).getLightLevel(blockPos, i);
    }

    @Override
    public int getTop(Heightmap.Type type, int i, int j) {
        int k = i < -30000000 || j < -30000000 || i >= 30000000 || j >= 30000000 ? this.getSeaLevel() + 1 : (this.isChunkLoaded(i >> 4, j >> 4) ? this.method_8497(i >> 4, j >> 4).sampleHeightmap(type, i & 0xF, j & 0xF) + 1 : 0);
        return k;
    }

    @Override
    public int getLightLevel(LightType lightType, BlockPos blockPos) {
        return this.getChunkManager().getLightingProvider().get(lightType).getLightLevel(blockPos);
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        if (World.isHeightInvalid(blockPos)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        WorldChunk worldChunk = this.method_8497(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        return worldChunk.getBlockState(blockPos);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        if (World.isHeightInvalid(blockPos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        WorldChunk worldChunk = this.getWorldChunk(blockPos);
        return worldChunk.getFluidState(blockPos);
    }

    public boolean isDaylight() {
        return this.ambientDarkness < 4;
    }

    @Override
    public void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
        this.playSound(playerEntity, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, soundEvent, soundCategory, f, g);
    }

    public abstract void playSound(@Nullable PlayerEntity var1, double var2, double var4, double var6, SoundEvent var8, SoundCategory var9, float var10, float var11);

    public abstract void playSoundFromEntity(@Nullable PlayerEntity var1, Entity var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

    public void playSound(double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h, boolean bl) {
    }

    @Override
    public void addParticle(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
    }

    @Environment(value=EnvType.CLIENT)
    public void addParticle(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
    }

    public void addImportantParticle(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
    }

    public void addImportantParticle(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
    }

    @Environment(value=EnvType.CLIENT)
    public float getAmbientLight(float f) {
        float g = this.getSkyAngle(f);
        float h = 1.0f - (MathHelper.cos(g * ((float)Math.PI * 2)) * 2.0f + 0.2f);
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        h = 1.0f - h;
        h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0f) / 16.0));
        h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0f) / 16.0));
        return h * 0.8f + 0.2f;
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d getSkyColor(BlockPos blockPos, float f) {
        float p;
        float o;
        float g = this.getSkyAngle(f);
        float h = MathHelper.cos(g * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        Biome biome = this.getBiome(blockPos);
        float i = biome.getTemperature(blockPos);
        int j = biome.getSkyColor(i);
        float k = (float)(j >> 16 & 0xFF) / 255.0f;
        float l = (float)(j >> 8 & 0xFF) / 255.0f;
        float m = (float)(j & 0xFF) / 255.0f;
        k *= h;
        l *= h;
        m *= h;
        float n = this.getRainGradient(f);
        if (n > 0.0f) {
            o = (k * 0.3f + l * 0.59f + m * 0.11f) * 0.6f;
            p = 1.0f - n * 0.75f;
            k = k * p + o * (1.0f - p);
            l = l * p + o * (1.0f - p);
            m = m * p + o * (1.0f - p);
        }
        if ((o = this.getThunderGradient(f)) > 0.0f) {
            p = (k * 0.3f + l * 0.59f + m * 0.11f) * 0.2f;
            float q = 1.0f - o * 0.75f;
            k = k * q + p * (1.0f - q);
            l = l * q + p * (1.0f - q);
            m = m * q + p * (1.0f - q);
        }
        if (this.ticksSinceLightning > 0) {
            p = (float)this.ticksSinceLightning - f;
            if (p > 1.0f) {
                p = 1.0f;
            }
            k = k * (1.0f - (p *= 0.45f)) + 0.8f * p;
            l = l * (1.0f - p) + 0.8f * p;
            m = m * (1.0f - p) + 1.0f * p;
        }
        return new Vec3d(k, l, m);
    }

    public float getSkyAngleRadians(float f) {
        float g = this.getSkyAngle(f);
        return g * ((float)Math.PI * 2);
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d getCloudColor(float f) {
        float n;
        float m;
        float g = this.getSkyAngle(f);
        float h = MathHelper.cos(g * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        float i = 1.0f;
        float j = 1.0f;
        float k = 1.0f;
        float l = this.getRainGradient(f);
        if (l > 0.0f) {
            m = (i * 0.3f + j * 0.59f + k * 0.11f) * 0.6f;
            n = 1.0f - l * 0.95f;
            i = i * n + m * (1.0f - n);
            j = j * n + m * (1.0f - n);
            k = k * n + m * (1.0f - n);
        }
        i *= h * 0.9f + 0.1f;
        j *= h * 0.9f + 0.1f;
        k *= h * 0.85f + 0.15f;
        m = this.getThunderGradient(f);
        if (m > 0.0f) {
            n = (i * 0.3f + j * 0.59f + k * 0.11f) * 0.2f;
            float o = 1.0f - m * 0.95f;
            i = i * o + n * (1.0f - o);
            j = j * o + n * (1.0f - o);
            k = k * o + n * (1.0f - o);
        }
        return new Vec3d(i, j, k);
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d getFogColor(float f) {
        float g = this.getSkyAngle(f);
        return this.dimension.getFogColor(g, f);
    }

    @Environment(value=EnvType.CLIENT)
    public float getStarsBrightness(float f) {
        float g = this.getSkyAngle(f);
        float h = 1.0f - (MathHelper.cos(g * ((float)Math.PI * 2)) * 2.0f + 0.25f);
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        return h * h * 0.5f;
    }

    public boolean addBlockEntity(BlockEntity blockEntity) {
        boolean bl;
        if (this.iteratingTickingBlockEntities) {
            Supplier[] supplierArray = new Supplier[2];
            supplierArray[0] = () -> Registry.BLOCK_ENTITY.getId(blockEntity.getType());
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

    public void addBlockEntities(Collection<BlockEntity> collection) {
        if (this.iteratingTickingBlockEntities) {
            this.pendingBlockEntities.addAll(collection);
        } else {
            for (BlockEntity blockEntity : collection) {
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
            BlockPos blockPos;
            BlockEntity blockEntity = iterator.next();
            if (!blockEntity.isInvalid() && blockEntity.hasWorld() && this.chunkManager.shouldTickBlock(blockPos = blockEntity.getPos()) && this.getWorldBorder().contains(blockPos)) {
                try {
                    profiler.push(() -> String.valueOf(BlockEntityType.getId(blockEntity.getType())));
                    if (blockEntity.getType().supports(this.getBlockState(blockPos).getBlock())) {
                        ((Tickable)((Object)blockEntity)).tick();
                    } else {
                        blockEntity.method_20525();
                    }
                    profiler.pop();
                } catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "Ticking block entity");
                    CrashReportSection crashReportSection = crashReport.addElement("Block entity being ticked");
                    blockEntity.populateCrashReport(crashReportSection);
                    throw new CrashException(crashReport);
                }
            }
            if (!blockEntity.isInvalid()) continue;
            iterator.remove();
            this.blockEntities.remove(blockEntity);
            if (!this.isBlockLoaded(blockEntity.getPos())) continue;
            this.getWorldChunk(blockEntity.getPos()).removeBlockEntity(blockEntity.getPos());
        }
        this.iteratingTickingBlockEntities = false;
        profiler.swap("pendingBlockEntities");
        if (!this.pendingBlockEntities.isEmpty()) {
            for (int i = 0; i < this.pendingBlockEntities.size(); ++i) {
                BlockEntity blockEntity2 = this.pendingBlockEntities.get(i);
                if (blockEntity2.isInvalid()) continue;
                if (!this.blockEntities.contains(blockEntity2)) {
                    this.addBlockEntity(blockEntity2);
                }
                if (!this.isBlockLoaded(blockEntity2.getPos())) continue;
                WorldChunk worldChunk = this.getWorldChunk(blockEntity2.getPos());
                BlockState blockState = worldChunk.getBlockState(blockEntity2.getPos());
                worldChunk.setBlockEntity(blockEntity2.getPos(), blockEntity2);
                this.updateListeners(blockEntity2.getPos(), blockState, blockState, 3);
            }
            this.pendingBlockEntities.clear();
        }
        profiler.pop();
    }

    public void tickEntity(Consumer<Entity> consumer, Entity entity) {
        try {
            consumer.accept(entity);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Ticking entity");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being ticked");
            entity.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    public boolean isAreaNotEmpty(BoundingBox boundingBox) {
        int i = MathHelper.floor(boundingBox.minX);
        int j = MathHelper.ceil(boundingBox.maxX);
        int k = MathHelper.floor(boundingBox.minY);
        int l = MathHelper.ceil(boundingBox.maxY);
        int m = MathHelper.floor(boundingBox.minZ);
        int n = MathHelper.ceil(boundingBox.maxZ);
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int o = i; o < j; ++o) {
                for (int p = k; p < l; ++p) {
                    for (int q = m; q < n; ++q) {
                        BlockState blockState = this.getBlockState(pooledMutable.method_10113(o, p, q));
                        if (blockState.isAir()) continue;
                        boolean bl = true;
                        return bl;
                    }
                }
            }
        }
        return false;
    }

    public boolean doesAreaContainFireSource(BoundingBox boundingBox) {
        int n;
        int i = MathHelper.floor(boundingBox.minX);
        int j = MathHelper.ceil(boundingBox.maxX);
        int k = MathHelper.floor(boundingBox.minY);
        int l = MathHelper.ceil(boundingBox.maxY);
        int m = MathHelper.floor(boundingBox.minZ);
        if (this.isAreaLoaded(i, k, m, j, l, n = MathHelper.ceil(boundingBox.maxZ))) {
            try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
                for (int o = i; o < j; ++o) {
                    for (int p = k; p < l; ++p) {
                        for (int q = m; q < n; ++q) {
                            Block block = this.getBlockState(pooledMutable.method_10113(o, p, q)).getBlock();
                            if (block != Blocks.FIRE && block != Blocks.LAVA) continue;
                            boolean bl = true;
                            return bl;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public BlockState getBlockState(BoundingBox boundingBox, Block block) {
        int n;
        int i = MathHelper.floor(boundingBox.minX);
        int j = MathHelper.ceil(boundingBox.maxX);
        int k = MathHelper.floor(boundingBox.minY);
        int l = MathHelper.ceil(boundingBox.maxY);
        int m = MathHelper.floor(boundingBox.minZ);
        if (this.isAreaLoaded(i, k, m, j, l, n = MathHelper.ceil(boundingBox.maxZ))) {
            try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
                for (int o = i; o < j; ++o) {
                    for (int p = k; p < l; ++p) {
                        for (int q = m; q < n; ++q) {
                            BlockState blockState = this.getBlockState(pooledMutable.method_10113(o, p, q));
                            if (blockState.getBlock() != block) continue;
                            BlockState blockState2 = blockState;
                            return blockState2;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean containsBlockWithMaterial(BoundingBox boundingBox, Material material) {
        int i = MathHelper.floor(boundingBox.minX);
        int j = MathHelper.ceil(boundingBox.maxX);
        int k = MathHelper.floor(boundingBox.minY);
        int l = MathHelper.ceil(boundingBox.maxY);
        int m = MathHelper.floor(boundingBox.minZ);
        int n = MathHelper.ceil(boundingBox.maxZ);
        MaterialPredicate materialPredicate = MaterialPredicate.create(material);
        return BlockPos.stream(i, k, m, j - 1, l - 1, n - 1).anyMatch(blockPos -> materialPredicate.method_11745(this.getBlockState((BlockPos)blockPos)));
    }

    public Explosion createExplosion(@Nullable Entity entity, double d, double e, double f, float g, Explosion.DestructionType destructionType) {
        return this.createExplosion(entity, null, d, e, f, g, false, destructionType);
    }

    public Explosion createExplosion(@Nullable Entity entity, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType) {
        return this.createExplosion(entity, null, d, e, f, g, bl, destructionType);
    }

    public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType) {
        Explosion explosion = new Explosion(this, entity, d, e, f, g, bl, destructionType);
        if (damageSource != null) {
            explosion.setDamageSource(damageSource);
        }
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
        return explosion;
    }

    public boolean method_8506(@Nullable PlayerEntity playerEntity, BlockPos blockPos, Direction direction) {
        if (this.getBlockState(blockPos = blockPos.offset(direction)).getBlock() == Blocks.FIRE) {
            this.playLevelEvent(playerEntity, 1009, blockPos, 0);
            this.clearBlockState(blockPos, false);
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public String getChunkProviderStatus() {
        return this.chunkManager.getStatus();
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        if (World.isHeightInvalid(blockPos)) {
            return null;
        }
        if (!this.isClient && Thread.currentThread() != this.thread) {
            return null;
        }
        BlockEntity blockEntity = null;
        if (this.iteratingTickingBlockEntities) {
            blockEntity = this.getPendingBlockEntity(blockPos);
        }
        if (blockEntity == null) {
            blockEntity = this.getWorldChunk(blockPos).getBlockEntity(blockPos, WorldChunk.CreationType.IMMEDIATE);
        }
        if (blockEntity == null) {
            blockEntity = this.getPendingBlockEntity(blockPos);
        }
        return blockEntity;
    }

    @Nullable
    private BlockEntity getPendingBlockEntity(BlockPos blockPos) {
        for (int i = 0; i < this.pendingBlockEntities.size(); ++i) {
            BlockEntity blockEntity = this.pendingBlockEntities.get(i);
            if (blockEntity.isInvalid() || !blockEntity.getPos().equals(blockPos)) continue;
            return blockEntity;
        }
        return null;
    }

    public void setBlockEntity(BlockPos blockPos, @Nullable BlockEntity blockEntity) {
        if (World.isHeightInvalid(blockPos)) {
            return;
        }
        if (blockEntity != null && !blockEntity.isInvalid()) {
            if (this.iteratingTickingBlockEntities) {
                blockEntity.setPos(blockPos);
                Iterator<BlockEntity> iterator = this.pendingBlockEntities.iterator();
                while (iterator.hasNext()) {
                    BlockEntity blockEntity2 = iterator.next();
                    if (!blockEntity2.getPos().equals(blockPos)) continue;
                    blockEntity2.invalidate();
                    iterator.remove();
                }
                this.pendingBlockEntities.add(blockEntity);
            } else {
                this.getWorldChunk(blockPos).setBlockEntity(blockPos, blockEntity);
                this.addBlockEntity(blockEntity);
            }
        }
    }

    public void removeBlockEntity(BlockPos blockPos) {
        BlockEntity blockEntity = this.getBlockEntity(blockPos);
        if (blockEntity != null && this.iteratingTickingBlockEntities) {
            blockEntity.invalidate();
            this.pendingBlockEntities.remove(blockEntity);
        } else {
            if (blockEntity != null) {
                this.pendingBlockEntities.remove(blockEntity);
                this.blockEntities.remove(blockEntity);
                this.tickingBlockEntities.remove(blockEntity);
            }
            this.getWorldChunk(blockPos).removeBlockEntity(blockPos);
        }
    }

    public boolean isHeightValidAndBlockLoaded(BlockPos blockPos) {
        if (World.isHeightInvalid(blockPos)) {
            return false;
        }
        return this.chunkManager.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    public boolean doesBlockHaveSolidTopSurface(BlockPos blockPos, Entity entity) {
        if (World.isHeightInvalid(blockPos)) {
            return false;
        }
        Chunk chunk = this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, false);
        if (chunk == null) {
            return false;
        }
        return chunk.getBlockState(blockPos).hasSolidTopSurface(this, blockPos, entity);
    }

    public void calculateAmbientDarkness() {
        double d = 1.0 - (double)(this.getRainGradient(1.0f) * 5.0f) / 16.0;
        double e = 1.0 - (double)(this.getThunderGradient(1.0f) * 5.0f) / 16.0;
        double f = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.getSkyAngle(1.0f) * ((float)Math.PI * 2)), -0.25, 0.25);
        this.ambientDarkness = (int)((1.0 - f * d * e) * 11.0);
    }

    public void setMobSpawnOptions(boolean bl, boolean bl2) {
        this.getChunkManager().setMobSpawnOptions(bl, bl2);
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
        this.chunkManager.close();
    }

    @Override
    public ChunkStatus getLeastChunkStatusForCollisionCalculation() {
        return ChunkStatus.FULL;
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, BoundingBox boundingBox, @Nullable Predicate<? super Entity> predicate) {
        ArrayList<Entity> list = Lists.newArrayList();
        int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
        int j = MathHelper.floor((boundingBox.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
        int l = MathHelper.floor((boundingBox.maxZ + 2.0) / 16.0);
        for (int m = i; m <= j; ++m) {
            for (int n = k; n <= l; ++n) {
                WorldChunk worldChunk = this.getChunkManager().getWorldChunk(m, n, false);
                if (worldChunk == null) continue;
                worldChunk.appendEntities(entity, boundingBox, list, predicate);
            }
        }
        return list;
    }

    public List<Entity> getEntities(@Nullable EntityType<?> entityType, BoundingBox boundingBox, Predicate<? super Entity> predicate) {
        int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((boundingBox.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((boundingBox.maxZ + 2.0) / 16.0);
        ArrayList<Entity> list = Lists.newArrayList();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk worldChunk = this.getChunkManager().getWorldChunk(m, n, false);
                if (worldChunk == null) continue;
                worldChunk.appendEntities(entityType, boundingBox, list, predicate);
            }
        }
        return list;
    }

    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> class_, BoundingBox boundingBox, @Nullable Predicate<? super T> predicate) {
        int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((boundingBox.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((boundingBox.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk worldChunk = this.getChunkManager().getWorldChunk(m, n, false);
                if (worldChunk == null) continue;
                worldChunk.appendEntities(class_, boundingBox, list, predicate);
            }
        }
        return list;
    }

    @Nullable
    public abstract Entity getEntityById(int var1);

    public void markDirty(BlockPos blockPos, BlockEntity blockEntity) {
        if (this.isBlockLoaded(blockPos)) {
            this.getWorldChunk(blockPos).markDirty();
        }
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    @Override
    public World getWorld() {
        return this;
    }

    public LevelGeneratorType getGeneratorType() {
        return this.properties.getGeneratorType();
    }

    public int getReceivedStrongRedstonePower(BlockPos blockPos) {
        int i = 0;
        if ((i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.down(), Direction.DOWN))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.up(), Direction.UP))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.north(), Direction.NORTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.south(), Direction.SOUTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.west(), Direction.WEST))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getEmittedStrongRedstonePower(blockPos.east(), Direction.EAST))) >= 15) {
            return i;
        }
        return i;
    }

    public boolean isEmittingRedstonePower(BlockPos blockPos, Direction direction) {
        return this.getEmittedRedstonePower(blockPos, direction) > 0;
    }

    public int getEmittedRedstonePower(BlockPos blockPos, Direction direction) {
        BlockState blockState = this.getBlockState(blockPos);
        if (blockState.isSimpleFullBlock(this, blockPos)) {
            return this.getReceivedStrongRedstonePower(blockPos);
        }
        return blockState.getWeakRedstonePower(this, blockPos, direction);
    }

    public boolean isReceivingRedstonePower(BlockPos blockPos) {
        if (this.getEmittedRedstonePower(blockPos.down(), Direction.DOWN) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(blockPos.up(), Direction.UP) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(blockPos.north(), Direction.NORTH) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(blockPos.south(), Direction.SOUTH) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(blockPos.west(), Direction.WEST) > 0) {
            return true;
        }
        return this.getEmittedRedstonePower(blockPos.east(), Direction.EAST) > 0;
    }

    public int getReceivedRedstonePower(BlockPos blockPos) {
        int i = 0;
        for (Direction direction : DIRECTIONS) {
            int j = this.getEmittedRedstonePower(blockPos.offset(direction), direction);
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

    public void setTime(long l) {
        this.properties.setTime(l);
    }

    @Override
    public long getSeed() {
        return this.properties.getSeed();
    }

    public long getTime() {
        return this.properties.getTime();
    }

    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }

    public void setTimeOfDay(long l) {
        this.properties.setTimeOfDay(l);
    }

    protected void tickTime() {
        this.setTime(this.properties.getTime() + 1L);
        if (this.properties.getGameRules().getBoolean("doDaylightCycle")) {
            this.setTimeOfDay(this.properties.getTimeOfDay() + 1L);
        }
    }

    @Override
    public BlockPos getSpawnPos() {
        BlockPos blockPos = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
        if (!this.getWorldBorder().contains(blockPos)) {
            blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return blockPos;
    }

    public void setSpawnPos(BlockPos blockPos) {
        this.properties.setSpawnPos(blockPos);
    }

    public boolean canPlayerModifyAt(PlayerEntity playerEntity, BlockPos blockPos) {
        return true;
    }

    public void sendEntityStatus(Entity entity, byte b) {
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }

    public void addBlockAction(BlockPos blockPos, Block block, int i, int j) {
        this.getBlockState(blockPos).onBlockAction(this, blockPos, i, j);
    }

    @Override
    public LevelProperties getLevelProperties() {
        return this.properties;
    }

    public GameRules getGameRules() {
        return this.properties.getGameRules();
    }

    public float getThunderGradient(float f) {
        return MathHelper.lerp(f, this.thunderGradientPrev, this.thunderGradient) * this.getRainGradient(f);
    }

    @Environment(value=EnvType.CLIENT)
    public void setThunderGradient(float f) {
        this.thunderGradientPrev = f;
        this.thunderGradient = f;
    }

    public float getRainGradient(float f) {
        return MathHelper.lerp(f, this.rainGradientPrev, this.rainGradient);
    }

    @Environment(value=EnvType.CLIENT)
    public void setRainGradient(float f) {
        this.rainGradientPrev = f;
        this.rainGradient = f;
    }

    public boolean isThundering() {
        if (!this.dimension.hasSkyLight() || this.dimension.isNether()) {
            return false;
        }
        return (double)this.getThunderGradient(1.0f) > 0.9;
    }

    public boolean isRaining() {
        return (double)this.getRainGradient(1.0f) > 0.2;
    }

    public boolean hasRain(BlockPos blockPos) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.isSkyVisible(blockPos)) {
            return false;
        }
        if (this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()) {
            return false;
        }
        return this.getBiome(blockPos).getPrecipitation() == Biome.Precipitation.RAIN;
    }

    public boolean hasHighHumidity(BlockPos blockPos) {
        Biome biome = this.getBiome(blockPos);
        return biome.hasHighHumidity();
    }

    @Nullable
    public abstract MapState getMapState(String var1);

    public abstract void putMapState(MapState var1);

    public abstract int getNextMapId();

    public void playGlobalEvent(int i, BlockPos blockPos, int j) {
    }

    public int getEffectiveHeight() {
        return this.dimension.isNether() ? 128 : 256;
    }

    @Environment(value=EnvType.CLIENT)
    public double getHorizonHeight() {
        if (this.properties.getGeneratorType() == LevelGeneratorType.FLAT) {
            return 0.0;
        }
        return 63.0;
    }

    public CrashReportSection addDetailsToCrashReport(CrashReport crashReport) {
        CrashReportSection crashReportSection = crashReport.addElement("Affected level", 1);
        crashReportSection.add("Level name", this.properties == null ? "????" : this.properties.getLevelName());
        crashReportSection.add("All players", () -> this.getPlayers().size() + " total; " + this.getPlayers());
        crashReportSection.add("Chunk stats", this.chunkManager::getStatus);
        try {
            this.properties.populateCrashReport(crashReportSection);
        } catch (Throwable throwable) {
            crashReportSection.add("Level Data Unobtainable", throwable);
        }
        return crashReportSection;
    }

    public abstract void setBlockBreakingProgress(int var1, BlockPos var2, int var3);

    @Environment(value=EnvType.CLIENT)
    public void addFireworkParticle(double d, double e, double f, double g, double h, double i, @Nullable CompoundTag compoundTag) {
    }

    public abstract Scoreboard getScoreboard();

    public void updateHorizontalAdjacent(BlockPos blockPos, Block block) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.offset(direction);
            if (!this.isBlockLoaded(blockPos2)) continue;
            BlockState blockState = this.getBlockState(blockPos2);
            if (blockState.getBlock() == Blocks.COMPARATOR) {
                blockState.neighborUpdate(this, blockPos2, block, blockPos, false);
                continue;
            }
            if (!blockState.isSimpleFullBlock(this, blockPos2) || (blockState = this.getBlockState(blockPos2 = blockPos2.offset(direction))).getBlock() != Blocks.COMPARATOR) continue;
            blockState.neighborUpdate(this, blockPos2, block, blockPos, false);
        }
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos blockPos) {
        long l = 0L;
        float f = 0.0f;
        if (this.isBlockLoaded(blockPos)) {
            f = this.getMoonSize();
            l = this.getWorldChunk(blockPos).getInhabitedTime();
        }
        return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), l, f);
    }

    @Override
    public int getAmbientDarkness() {
        return this.ambientDarkness;
    }

    @Environment(value=EnvType.CLIENT)
    public int getTicksSinceLightning() {
        return this.ticksSinceLightning;
    }

    public void setTicksSinceLightning(int i) {
        this.ticksSinceLightning = i;
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.border;
    }

    public void sendPacket(Packet<?> packet) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    @Nullable
    public BlockPos locateStructure(String string, BlockPos blockPos, int i, boolean bl) {
        return null;
    }

    @Override
    public Dimension getDimension() {
        return this.dimension;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public boolean testBlockState(BlockPos blockPos, Predicate<BlockState> predicate) {
        return predicate.test(this.getBlockState(blockPos));
    }

    public abstract RecipeManager getRecipeManager();

    public abstract RegistryTagManager getTagManager();

    public BlockPos getRandomPosInChunk(int i, int j, int k, int l) {
        this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
        int m = this.lcgBlockSeed >> 2;
        return new BlockPos(i + (m & 0xF), j + (m >> 16 & l), k + (m >> 8 & 0xF));
    }

    public boolean isSavingDisabled() {
        return false;
    }

    public Profiler getProfiler() {
        return this.profiler;
    }

    @Override
    public BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
        return new BlockPos(blockPos.getX(), this.getTop(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
    }

    @Override
    public /* synthetic */ Chunk getChunk(int i, int j) {
        return this.method_8497(i, j);
    }
}

