/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.chunk.ChunkOcclusionGraphBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderTask;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkRenderer {
    private volatile World world;
    private final WorldRenderer renderer;
    public static int chunkUpdateCount;
    public ChunkRenderData data = ChunkRenderData.EMPTY;
    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock dataLock = new ReentrantLock();
    private ChunkRenderTask task;
    private final Set<BlockEntity> blockEntities = Sets.newHashSet();
    private final GlBuffer[] buffers = new GlBuffer[BlockRenderLayer.values().length];
    public Box boundingBox;
    private int field_4471 = -1;
    private boolean rebuildScheduled = true;
    private final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
    private final BlockPos.Mutable[] neighborPositions = SystemUtil.consume(new BlockPos.Mutable[6], mutables -> {
        for (int i = 0; i < ((BlockPos.Mutable[])mutables).length; ++i) {
            mutables[i] = new BlockPos.Mutable();
        }
    });
    private boolean rebuildOnClientThread;

    public ChunkRenderer(World world, WorldRenderer worldRenderer) {
        this.world = world;
        this.renderer = worldRenderer;
        if (GLX.useVbo()) {
            for (int i = 0; i < BlockRenderLayer.values().length; ++i) {
                this.buffers[i] = new GlBuffer(VertexFormats.POSITION_COLOR_UV_LMAP);
            }
        }
    }

    private static boolean isChunkNonEmpty(BlockPos blockPos, World world) {
        return !world.method_8497(blockPos.getX() >> 4, blockPos.getZ() >> 4).isEmpty();
    }

    public boolean shouldBuild() {
        int i = 24;
        if (this.getSquaredCameraDistance() > 576.0) {
            World world = this.getWorld();
            return ChunkRenderer.isChunkNonEmpty(this.neighborPositions[Direction.WEST.ordinal()], world) && ChunkRenderer.isChunkNonEmpty(this.neighborPositions[Direction.NORTH.ordinal()], world) && ChunkRenderer.isChunkNonEmpty(this.neighborPositions[Direction.EAST.ordinal()], world) && ChunkRenderer.isChunkNonEmpty(this.neighborPositions[Direction.SOUTH.ordinal()], world);
        }
        return true;
    }

    public boolean method_3671(int i) {
        if (this.field_4471 == i) {
            return false;
        }
        this.field_4471 = i;
        return true;
    }

    public GlBuffer getGlBuffer(int i) {
        return this.buffers[i];
    }

    public void setOrigin(int i, int j, int k) {
        if (i == this.origin.getX() && j == this.origin.getY() && k == this.origin.getZ()) {
            return;
        }
        this.clear();
        this.origin.set(i, j, k);
        this.boundingBox = new Box(i, j, k, i + 16, j + 16, k + 16);
        for (Direction direction : Direction.values()) {
            this.neighborPositions[direction.ordinal()].set(this.origin).setOffset(direction, 16);
        }
    }

    public void resortTransparency(float f, float g, float h, ChunkRenderTask chunkRenderTask) {
        ChunkRenderData chunkRenderData = chunkRenderTask.getRenderData();
        if (chunkRenderData.getBufferState() == null || chunkRenderData.isEmpty(BlockRenderLayer.TRANSLUCENT)) {
            return;
        }
        this.beginBufferBuilding(chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT), this.origin);
        chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT).restoreState(chunkRenderData.getBufferState());
        this.endBufferBuilding(BlockRenderLayer.TRANSLUCENT, f, g, h, chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT), chunkRenderData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void rebuildChunk(float f, float g, float h, ChunkRenderTask chunkRenderTask) {
        ChunkRenderData chunkRenderData = new ChunkRenderData();
        boolean i = true;
        BlockPos blockPos = this.origin.toImmutable();
        BlockPos blockPos2 = blockPos.add(15, 15, 15);
        World world = this.world;
        if (world == null) {
            return;
        }
        chunkRenderTask.getLock().lock();
        try {
            if (chunkRenderTask.getStage() != ChunkRenderTask.Stage.COMPILING) {
                return;
            }
            chunkRenderTask.setRenderData(chunkRenderData);
        } finally {
            chunkRenderTask.getLock().unlock();
        }
        ChunkOcclusionGraphBuilder chunkOcclusionGraphBuilder = new ChunkOcclusionGraphBuilder();
        HashSet<BlockEntity> set = Sets.newHashSet();
        ChunkRendererRegion chunkRendererRegion = chunkRenderTask.takeRegion();
        if (chunkRendererRegion != null) {
            ++chunkUpdateCount;
            boolean[] bls = new boolean[BlockRenderLayer.values().length];
            BlockModelRenderer.enableBrightnessCache();
            Random random = new Random();
            BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
            for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
                BufferBuilder bufferBuilder;
                int j;
                BlockRenderLayer blockRenderLayer;
                FluidState fluidState;
                BlockEntityRenderer<BlockEntity> blockEntityRenderer;
                BlockEntity blockEntity;
                BlockState blockState = chunkRendererRegion.getBlockState(blockPos3);
                Block block = blockState.getBlock();
                if (blockState.isFullOpaque(chunkRendererRegion, blockPos3)) {
                    chunkOcclusionGraphBuilder.markClosed(blockPos3);
                }
                if (block.hasBlockEntity() && (blockEntity = chunkRendererRegion.getBlockEntity(blockPos3, WorldChunk.CreationType.CHECK)) != null && (blockEntityRenderer = BlockEntityRenderDispatcher.INSTANCE.get(blockEntity)) != null) {
                    chunkRenderData.addBlockEntity(blockEntity);
                    if (blockEntityRenderer.method_3563(blockEntity)) {
                        set.add(blockEntity);
                    }
                }
                if (!(fluidState = chunkRendererRegion.getFluidState(blockPos3)).isEmpty()) {
                    blockRenderLayer = fluidState.getRenderLayer();
                    j = blockRenderLayer.ordinal();
                    bufferBuilder = chunkRenderTask.getBufferBuilders().get(j);
                    if (!chunkRenderData.isBufferInitialized(blockRenderLayer)) {
                        chunkRenderData.markBufferInitialized(blockRenderLayer);
                        this.beginBufferBuilding(bufferBuilder, blockPos);
                    }
                    int n = j;
                    bls[n] = bls[n] | blockRenderManager.tesselateFluid(blockPos3, chunkRendererRegion, bufferBuilder, fluidState);
                }
                if (blockState.getRenderType() == BlockRenderType.INVISIBLE) continue;
                blockRenderLayer = block.getRenderLayer();
                j = blockRenderLayer.ordinal();
                bufferBuilder = chunkRenderTask.getBufferBuilders().get(j);
                if (!chunkRenderData.isBufferInitialized(blockRenderLayer)) {
                    chunkRenderData.markBufferInitialized(blockRenderLayer);
                    this.beginBufferBuilding(bufferBuilder, blockPos);
                }
                int n = j;
                bls[n] = bls[n] | blockRenderManager.tesselateBlock(blockState, blockPos3, chunkRendererRegion, bufferBuilder, random);
            }
            for (BlockRenderLayer blockRenderLayer2 : BlockRenderLayer.values()) {
                if (bls[blockRenderLayer2.ordinal()]) {
                    chunkRenderData.setNonEmpty(blockRenderLayer2);
                }
                if (!chunkRenderData.isBufferInitialized(blockRenderLayer2)) continue;
                this.endBufferBuilding(blockRenderLayer2, f, g, h, chunkRenderTask.getBufferBuilders().get(blockRenderLayer2), chunkRenderData);
            }
            BlockModelRenderer.disableBrightnessCache();
        }
        chunkRenderData.setOcclusionGraph(chunkOcclusionGraphBuilder.build());
        this.lock.lock();
        try {
            HashSet<BlockEntity> set2 = Sets.newHashSet(set);
            HashSet<BlockEntity> set3 = Sets.newHashSet(this.blockEntities);
            set2.removeAll(this.blockEntities);
            set3.removeAll(set);
            this.blockEntities.clear();
            this.blockEntities.addAll(set);
            this.renderer.updateBlockEntities(set3, set2);
        } finally {
            this.lock.unlock();
        }
    }

    protected void cancel() {
        this.lock.lock();
        try {
            if (this.task != null && this.task.getStage() != ChunkRenderTask.Stage.DONE) {
                this.task.cancel();
                this.task = null;
            }
        } finally {
            this.lock.unlock();
        }
    }

    public ReentrantLock getLock() {
        return this.lock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChunkRenderTask startRebuild() {
        this.lock.lock();
        try {
            this.cancel();
            BlockPos blockPos = this.origin.toImmutable();
            boolean i = true;
            ChunkRendererRegion chunkRendererRegion = ChunkRendererRegion.create(this.world, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
            ChunkRenderTask chunkRenderTask = this.task = new ChunkRenderTask(this, ChunkRenderTask.Mode.REBUILD_CHUNK, this.getSquaredCameraDistance(), chunkRendererRegion);
            return chunkRenderTask;
        } finally {
            this.lock.unlock();
        }
    }

    @Nullable
    public ChunkRenderTask startResortTransparency() {
        this.lock.lock();
        try {
            if (this.task != null && this.task.getStage() == ChunkRenderTask.Stage.PENDING) {
                ChunkRenderTask chunkRenderTask = null;
                return chunkRenderTask;
            }
            if (this.task != null && this.task.getStage() != ChunkRenderTask.Stage.DONE) {
                this.task.cancel();
                this.task = null;
            }
            this.task = new ChunkRenderTask(this, ChunkRenderTask.Mode.RESORT_TRANSPARENCY, this.getSquaredCameraDistance(), null);
            this.task.setRenderData(this.data);
            ChunkRenderTask chunkRenderTask = this.task;
            return chunkRenderTask;
        } finally {
            this.lock.unlock();
        }
    }

    protected double getSquaredCameraDistance() {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        double d = this.boundingBox.minX + 8.0 - camera.getPos().x;
        double e = this.boundingBox.minY + 8.0 - camera.getPos().y;
        double f = this.boundingBox.minZ + 8.0 - camera.getPos().z;
        return d * d + e * e + f * f;
    }

    private void beginBufferBuilding(BufferBuilder bufferBuilder, BlockPos blockPos) {
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
        bufferBuilder.setOffset(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
    }

    private void endBufferBuilding(BlockRenderLayer blockRenderLayer, float f, float g, float h, BufferBuilder bufferBuilder, ChunkRenderData chunkRenderData) {
        if (blockRenderLayer == BlockRenderLayer.TRANSLUCENT && !chunkRenderData.isEmpty(blockRenderLayer)) {
            bufferBuilder.sortQuads(f, g, h);
            chunkRenderData.setBufferState(bufferBuilder.toBufferState());
        }
        bufferBuilder.end();
    }

    public ChunkRenderData getData() {
        return this.data;
    }

    public void setData(ChunkRenderData chunkRenderData) {
        this.dataLock.lock();
        try {
            this.data = chunkRenderData;
        } finally {
            this.dataLock.unlock();
        }
    }

    public void clear() {
        this.cancel();
        this.data = ChunkRenderData.EMPTY;
        this.rebuildScheduled = true;
    }

    public void delete() {
        this.clear();
        this.world = null;
        for (int i = 0; i < BlockRenderLayer.values().length; ++i) {
            if (this.buffers[i] == null) continue;
            this.buffers[i].delete();
        }
    }

    public BlockPos getOrigin() {
        return this.origin;
    }

    public void scheduleRebuild(boolean bl) {
        if (this.rebuildScheduled) {
            bl |= this.rebuildOnClientThread;
        }
        this.rebuildScheduled = true;
        this.rebuildOnClientThread = bl;
    }

    public void unscheduleRebuild() {
        this.rebuildScheduled = false;
        this.rebuildOnClientThread = false;
    }

    public boolean shouldRebuild() {
        return this.rebuildScheduled;
    }

    public boolean shouldRebuildOnClientThread() {
        return this.rebuildScheduled && this.rebuildOnClientThread;
    }

    public BlockPos getNeighborPosition(Direction direction) {
        return this.neighborPositions[direction.ordinal()];
    }

    public World getWorld() {
        return this.world;
    }
}

