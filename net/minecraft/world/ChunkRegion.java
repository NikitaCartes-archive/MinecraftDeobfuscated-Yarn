/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MultiTickScheduler;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ChunkRegion
implements IWorld {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Chunk> chunks;
    private final int centerChunkX;
    private final int centerChunkZ;
    private final int width;
    private final ServerWorld world;
    private final long seed;
    private final int seaLevel;
    private final LevelProperties levelProperties;
    private final Random random;
    private final Dimension dimension;
    private final ChunkGeneratorConfig generatorSettings;
    private final TickScheduler<Block> blockTickScheduler = new MultiTickScheduler<Block>(blockPos -> this.method_22350((BlockPos)blockPos).getBlockTickScheduler());
    private final TickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<Fluid>(blockPos -> this.method_22350((BlockPos)blockPos).getFluidTickScheduler());

    public ChunkRegion(ServerWorld serverWorld, List<Chunk> list) {
        int i = MathHelper.floor(Math.sqrt(list.size()));
        if (i * i != list.size()) {
            throw SystemUtil.throwOrPause(new IllegalStateException("Cache size is not a square."));
        }
        ChunkPos chunkPos = list.get(list.size() / 2).getPos();
        this.chunks = list;
        this.centerChunkX = chunkPos.x;
        this.centerChunkZ = chunkPos.z;
        this.width = i;
        this.world = serverWorld;
        this.seed = serverWorld.getSeed();
        this.generatorSettings = serverWorld.method_14178().getChunkGenerator().getConfig();
        this.seaLevel = serverWorld.getSeaLevel();
        this.levelProperties = serverWorld.getLevelProperties();
        this.random = serverWorld.getRandom();
        this.dimension = serverWorld.getDimension();
    }

    public int getCenterChunkX() {
        return this.centerChunkX;
    }

    public int getCenterChunkZ() {
        return this.centerChunkZ;
    }

    @Override
    public Chunk getChunk(int i, int j) {
        return this.method_22342(i, j, ChunkStatus.EMPTY);
    }

    @Override
    @Nullable
    public Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
        Chunk chunk;
        if (this.isChunkLoaded(i, j)) {
            ChunkPos chunkPos = this.chunks.get(0).getPos();
            int k = i - chunkPos.x;
            int l = j - chunkPos.z;
            chunk = this.chunks.get(k + l * this.width);
            if (chunk.getStatus().isAtLeast(chunkStatus)) {
                return chunk;
            }
        } else {
            chunk = null;
        }
        if (!bl) {
            return null;
        }
        Chunk chunk2 = this.chunks.get(0);
        Chunk chunk3 = this.chunks.get(this.chunks.size() - 1);
        LOGGER.error("Requested chunk : {} {}", (Object)i, (Object)j);
        LOGGER.error("Region bounds : {} {} | {} {}", (Object)chunk2.getPos().x, (Object)chunk2.getPos().z, (Object)chunk3.getPos().x, (Object)chunk3.getPos().z);
        if (chunk != null) {
            throw SystemUtil.throwOrPause(new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", chunkStatus, chunk.getStatus(), i, j)));
        }
        throw SystemUtil.throwOrPause(new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", i, j)));
    }

    @Override
    public boolean isChunkLoaded(int i, int j) {
        Chunk chunk = this.chunks.get(0);
        Chunk chunk2 = this.chunks.get(this.chunks.size() - 1);
        return i >= chunk.getPos().x && i <= chunk2.getPos().x && j >= chunk.getPos().z && j <= chunk2.getPos().z;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4).getBlockState(blockPos);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return this.method_22350(blockPos).getFluidState(blockPos);
    }

    @Override
    @Nullable
    public PlayerEntity getClosestPlayer(double d, double e, double f, double g, Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public int getAmbientDarkness() {
        return 0;
    }

    @Override
    public Biome getBiome(BlockPos blockPos) {
        Biome biome = this.method_22350(blockPos).getBiomeArray()[blockPos.getX() & 0xF | (blockPos.getZ() & 0xF) << 4];
        if (biome == null) {
            throw new RuntimeException(String.format("Biome is null @ %s", blockPos));
        }
        return biome;
    }

    @Override
    public LightingProvider method_22336() {
        return this.world.method_22336();
    }

    @Override
    public boolean breakBlock(BlockPos blockPos, boolean bl, @Nullable Entity entity) {
        BlockState blockState = this.getBlockState(blockPos);
        if (blockState.isAir()) {
            return false;
        }
        if (bl) {
            BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.getBlockEntity(blockPos) : null;
            Block.dropStacks(blockState, this.world, blockPos, blockEntity, entity, ItemStack.EMPTY);
        }
        return this.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        Chunk chunk = this.method_22350(blockPos);
        BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
        if (blockEntity != null) {
            return blockEntity;
        }
        CompoundTag compoundTag = chunk.getBlockEntityTagAt(blockPos);
        if (compoundTag != null) {
            if ("DUMMY".equals(compoundTag.getString("id"))) {
                Block block = this.getBlockState(blockPos).getBlock();
                if (!(block instanceof BlockEntityProvider)) {
                    return null;
                }
                blockEntity = ((BlockEntityProvider)((Object)block)).createBlockEntity(this.world);
            } else {
                blockEntity = BlockEntity.createFromTag(compoundTag);
            }
            if (blockEntity != null) {
                chunk.setBlockEntity(blockPos, blockEntity);
                return blockEntity;
            }
        }
        if (chunk.getBlockState(blockPos).getBlock() instanceof BlockEntityProvider) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", (Object)blockPos);
        }
        return null;
    }

    @Override
    public boolean setBlockState(BlockPos blockPos, BlockState blockState, int i) {
        Block block;
        Chunk chunk = this.method_22350(blockPos);
        BlockState blockState2 = chunk.setBlockState(blockPos, blockState, false);
        if (blockState2 != null) {
            this.world.onBlockChanged(blockPos, blockState2, blockState);
        }
        if ((block = blockState.getBlock()).hasBlockEntity()) {
            if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
                chunk.setBlockEntity(blockPos, ((BlockEntityProvider)((Object)block)).createBlockEntity(this));
            } else {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putInt("x", blockPos.getX());
                compoundTag.putInt("y", blockPos.getY());
                compoundTag.putInt("z", blockPos.getZ());
                compoundTag.putString("id", "DUMMY");
                chunk.addPendingBlockEntityTag(compoundTag);
            }
        } else if (blockState2 != null && blockState2.getBlock().hasBlockEntity()) {
            chunk.removeBlockEntity(blockPos);
        }
        if (blockState.shouldPostProcess(this, blockPos)) {
            this.markBlockForPostProcessing(blockPos);
        }
        return true;
    }

    private void markBlockForPostProcessing(BlockPos blockPos) {
        this.method_22350(blockPos).markBlockForPostProcessing(blockPos);
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        int i = MathHelper.floor(entity.x / 16.0);
        int j = MathHelper.floor(entity.z / 16.0);
        this.getChunk(i, j).addEntity(entity);
        return true;
    }

    @Override
    public boolean clearBlockState(BlockPos blockPos, boolean bl) {
        return this.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Deprecated
    public ServerWorld method_19506() {
        return this.world;
    }

    @Override
    public LevelProperties getLevelProperties() {
        return this.levelProperties;
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos blockPos) {
        if (!this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        }
        return new LocalDifficulty(this.world.getDifficulty(), this.world.getTimeOfDay(), 0L, this.world.getMoonSize());
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.world.method_14178();
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }

    @Override
    public int getSeaLevel() {
        return this.seaLevel;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public void updateNeighbors(BlockPos blockPos, Block block) {
    }

    @Override
    public int getLightLevel(Heightmap.Type type, int i, int j) {
        return this.getChunk(i >> 4, j >> 4).sampleHeightmap(type, i & 0xF, j & 0xF) + 1;
    }

    @Override
    public void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
    }

    @Override
    public void addParticle(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
    }

    @Override
    public void playLevelEvent(@Nullable PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BlockPos getSpawnPos() {
        return this.world.getSpawnPos();
    }

    @Override
    public Dimension getDimension() {
        return this.dimension;
    }

    @Override
    public boolean testBlockState(BlockPos blockPos, Predicate<BlockState> predicate) {
        return predicate.test(this.getBlockState(blockPos));
    }

    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> class_, Box box, @Nullable Predicate<? super T> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, Box box, @Nullable Predicate<? super Entity> predicate) {
        return Collections.emptyList();
    }

    public List<PlayerEntity> getPlayers() {
        return Collections.emptyList();
    }

    @Override
    @Deprecated
    public /* synthetic */ World getWorld() {
        return this.method_19506();
    }
}

