/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PriorityQueue<BuiltChunk.Task> rebuildQueue = Queues.newPriorityQueue();
    private final Queue<BlockBufferBuilderStorage> threadBuffers;
    private final Queue<Runnable> uploadQueue = Queues.newConcurrentLinkedQueue();
    private volatile int queuedTaskCount;
    private volatile int bufferCount;
    private final BlockBufferBuilderStorage buffers;
    private final TaskExecutor<Runnable> mailbox;
    private final Executor executor;
    private World world;
    private final WorldRenderer worldRenderer;
    private Vec3d cameraPosition = Vec3d.ZERO;

    public ChunkBuilder(World world, WorldRenderer worldRenderer, Executor executor, boolean bl, BlockBufferBuilderStorage blockBufferBuilderStorage) {
        this.world = world;
        this.worldRenderer = worldRenderer;
        int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / (RenderLayer.getBlockLayers().stream().mapToInt(RenderLayer::getExpectedBufferSize).sum() * 4) - 1);
        int j = Runtime.getRuntime().availableProcessors();
        int k = bl ? j : Math.min(j, 4);
        int l = Math.max(1, Math.min(k, i));
        this.buffers = blockBufferBuilderStorage;
        ArrayList<BlockBufferBuilderStorage> list = Lists.newArrayListWithExpectedSize(l);
        try {
            for (int m = 0; m < l; ++m) {
                list.add(new BlockBufferBuilderStorage());
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            LOGGER.warn("Allocated only {}/{} buffers", (Object)list.size(), (Object)l);
            int n = Math.min(list.size() * 2 / 3, list.size() - 1);
            for (int o = 0; o < n; ++o) {
                list.remove(list.size() - 1);
            }
            System.gc();
        }
        this.threadBuffers = Queues.newArrayDeque(list);
        this.bufferCount = this.threadBuffers.size();
        this.executor = executor;
        this.mailbox = TaskExecutor.create(executor, "Chunk Renderer");
        this.mailbox.send(this::scheduleRunTasks);
    }

    public void setWorld(World world) {
        this.world = world;
    }

    private void scheduleRunTasks() {
        if (this.threadBuffers.isEmpty()) {
            return;
        }
        BuiltChunk.Task task = this.rebuildQueue.poll();
        if (task == null) {
            return;
        }
        BlockBufferBuilderStorage blockBufferBuilderStorage = this.threadBuffers.poll();
        this.queuedTaskCount = this.rebuildQueue.size();
        this.bufferCount = this.threadBuffers.size();
        ((CompletableFuture)CompletableFuture.runAsync(() -> {}, this.executor).thenCompose(void_ -> task.run(blockBufferBuilderStorage))).whenComplete((result, throwable) -> {
            this.mailbox.send(() -> {
                if (result == Result.SUCCESSFUL) {
                    blockBufferBuilderStorage.clear();
                } else {
                    blockBufferBuilderStorage.reset();
                }
                this.threadBuffers.add(blockBufferBuilderStorage);
                this.bufferCount = this.threadBuffers.size();
                this.scheduleRunTasks();
            });
            if (throwable != null) {
                CrashReport crashReport = CrashReport.create(throwable, "Batching chunks");
                MinecraftClient.getInstance().setCrashReport(MinecraftClient.getInstance().addDetailsToCrashReport(crashReport));
            }
        });
    }

    public String getDebugString() {
        return String.format("pC: %03d, pU: %02d, aB: %02d", this.queuedTaskCount, this.uploadQueue.size(), this.bufferCount);
    }

    public void setCameraPosition(Vec3d vec3d) {
        this.cameraPosition = vec3d;
    }

    public Vec3d getCameraPosition() {
        return this.cameraPosition;
    }

    public boolean upload() {
        Runnable runnable;
        boolean bl = false;
        while ((runnable = this.uploadQueue.poll()) != null) {
            runnable.run();
            bl = true;
        }
        return bl;
    }

    public void rebuild(BuiltChunk builtChunk) {
        builtChunk.rebuild();
    }

    public void reset() {
        this.clear();
    }

    public void send(BuiltChunk.Task task) {
        this.mailbox.send(() -> {
            this.rebuildQueue.offer(task);
            this.queuedTaskCount = this.rebuildQueue.size();
            this.scheduleRunTasks();
        });
    }

    public CompletableFuture<Void> scheduleUpload(BufferBuilder bufferBuilder, VertexBuffer vertexBuffer) {
        return CompletableFuture.runAsync(() -> {}, this.uploadQueue::add).thenCompose(void_ -> this.upload(bufferBuilder, vertexBuffer));
    }

    private CompletableFuture<Void> upload(BufferBuilder bufferBuilder, VertexBuffer vertexBuffer) {
        return vertexBuffer.submitUpload(bufferBuilder);
    }

    private void clear() {
        while (!this.rebuildQueue.isEmpty()) {
            BuiltChunk.Task task = this.rebuildQueue.poll();
            if (task == null) continue;
            task.cancel();
        }
        this.queuedTaskCount = 0;
    }

    public boolean isEmpty() {
        return this.queuedTaskCount == 0 && this.uploadQueue.isEmpty();
    }

    public void stop() {
        this.clear();
        this.mailbox.close();
        this.threadBuffers.clear();
    }

    @Environment(value=EnvType.CLIENT)
    public static class ChunkData {
        public static final ChunkData EMPTY = new ChunkData(){

            @Override
            public boolean isVisibleThrough(Direction direction, Direction direction2) {
                return false;
            }
        };
        private final Set<RenderLayer> nonEmptyLayers = new ObjectArraySet<RenderLayer>();
        private final Set<RenderLayer> initializedLayers = new ObjectArraySet<RenderLayer>();
        private boolean empty = true;
        private final List<BlockEntity> blockEntities = Lists.newArrayList();
        private ChunkOcclusionData occlusionGraph = new ChunkOcclusionData();
        @Nullable
        private BufferBuilder.State bufferState;

        public boolean isEmpty() {
            return this.empty;
        }

        public boolean isEmpty(RenderLayer renderLayer) {
            return !this.nonEmptyLayers.contains(renderLayer);
        }

        public List<BlockEntity> getBlockEntities() {
            return this.blockEntities;
        }

        public boolean isVisibleThrough(Direction direction, Direction direction2) {
            return this.occlusionGraph.isVisibleThrough(direction, direction2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Result {
        SUCCESSFUL,
        CANCELLED;

    }

    @Environment(value=EnvType.CLIENT)
    public class BuiltChunk {
        public final AtomicReference<ChunkData> data = new AtomicReference<ChunkData>(ChunkData.EMPTY);
        @Nullable
        private RebuildTask rebuildTask;
        @Nullable
        private SortTask sortTask;
        private final Set<BlockEntity> blockEntities = Sets.newHashSet();
        private final Map<RenderLayer, VertexBuffer> buffers = RenderLayer.getBlockLayers().stream().collect(Collectors.toMap(renderLayer -> renderLayer, renderLayer -> new VertexBuffer(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL)));
        public Box boundingBox;
        private int rebuildFrame = -1;
        private boolean needsRebuild = true;
        private final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
        private final BlockPos.Mutable[] neighborPositions = Util.create(new BlockPos.Mutable[6], mutables -> {
            for (int i = 0; i < ((BlockPos.Mutable[])mutables).length; ++i) {
                mutables[i] = new BlockPos.Mutable();
            }
        });
        private boolean needsImportantRebuild;

        private boolean isChunkNonEmpty(BlockPos blockPos) {
            return !ChunkBuilder.this.world.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4).isEmpty();
        }

        public boolean shouldBuild() {
            int i = 24;
            if (this.getSquaredCameraDistance() > 576.0) {
                return this.isChunkNonEmpty(this.neighborPositions[Direction.WEST.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.NORTH.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.EAST.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.SOUTH.ordinal()]);
            }
            return true;
        }

        public boolean setRebuildFrame(int i) {
            if (this.rebuildFrame == i) {
                return false;
            }
            this.rebuildFrame = i;
            return true;
        }

        public VertexBuffer getBuffer(RenderLayer renderLayer) {
            return this.buffers.get(renderLayer);
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

        protected double getSquaredCameraDistance() {
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            double d = this.boundingBox.x1 + 8.0 - camera.getPos().x;
            double e = this.boundingBox.y1 + 8.0 - camera.getPos().y;
            double f = this.boundingBox.z1 + 8.0 - camera.getPos().z;
            return d * d + e * e + f * f;
        }

        private void beginBufferBuilding(BufferBuilder bufferBuilder) {
            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
        }

        public ChunkData getData() {
            return this.data.get();
        }

        private void clear() {
            this.cancel();
            this.data.set(ChunkData.EMPTY);
            this.needsRebuild = true;
        }

        public void delete() {
            this.clear();
            this.buffers.values().forEach(VertexBuffer::close);
        }

        public BlockPos getOrigin() {
            return this.origin;
        }

        public void scheduleRebuild(boolean bl) {
            boolean bl2 = this.needsRebuild;
            this.needsRebuild = true;
            this.needsImportantRebuild = bl | (bl2 && this.needsImportantRebuild);
        }

        public void cancelRebuild() {
            this.needsRebuild = false;
            this.needsImportantRebuild = false;
        }

        public boolean needsRebuild() {
            return this.needsRebuild;
        }

        public boolean needsImportantRebuild() {
            return this.needsRebuild && this.needsImportantRebuild;
        }

        public BlockPos getNeighborPosition(Direction direction) {
            return this.neighborPositions[direction.ordinal()];
        }

        public boolean scheduleSort(RenderLayer renderLayer, ChunkBuilder chunkBuilder) {
            ChunkData chunkData = this.getData();
            if (this.sortTask != null) {
                this.sortTask.cancel();
            }
            if (!chunkData.initializedLayers.contains(renderLayer)) {
                return false;
            }
            this.sortTask = new SortTask(this.getSquaredCameraDistance(), chunkData);
            chunkBuilder.send(this.sortTask);
            return true;
        }

        protected void cancel() {
            if (this.rebuildTask != null) {
                this.rebuildTask.cancel();
                this.rebuildTask = null;
            }
            if (this.sortTask != null) {
                this.sortTask.cancel();
                this.sortTask = null;
            }
        }

        public Task createRebuildTask() {
            this.cancel();
            BlockPos blockPos = this.origin.toImmutable();
            boolean i = true;
            ChunkRendererRegion chunkRendererRegion = ChunkRendererRegion.create(ChunkBuilder.this.world, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
            this.rebuildTask = new RebuildTask(this.getSquaredCameraDistance(), chunkRendererRegion);
            return this.rebuildTask;
        }

        public void scheduleRebuild(ChunkBuilder chunkBuilder) {
            Task task = this.createRebuildTask();
            chunkBuilder.send(task);
        }

        private void setNoCullingBlockEntities(Set<BlockEntity> set) {
            HashSet<BlockEntity> set2 = Sets.newHashSet(set);
            HashSet<BlockEntity> set3 = Sets.newHashSet(this.blockEntities);
            set2.removeAll(this.blockEntities);
            set3.removeAll(set);
            this.blockEntities.clear();
            this.blockEntities.addAll(set);
            ChunkBuilder.this.worldRenderer.updateNoCullingBlockEntities(set3, set2);
        }

        public void rebuild() {
            Task task = this.createRebuildTask();
            task.run(ChunkBuilder.this.buffers);
        }

        @Environment(value=EnvType.CLIENT)
        abstract class Task
        implements Comparable<Task> {
            protected final double distance;
            protected final AtomicBoolean cancelled = new AtomicBoolean(false);

            public Task(double d) {
                this.distance = d;
            }

            public abstract CompletableFuture<Result> run(BlockBufferBuilderStorage var1);

            public abstract void cancel();

            @Override
            public int compareTo(Task task) {
                return Doubles.compare(this.distance, task.distance);
            }

            @Override
            public /* synthetic */ int compareTo(Object object) {
                return this.compareTo((Task)object);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class SortTask
        extends Task {
            private final ChunkData data;

            public SortTask(double d, ChunkData chunkData) {
                super(d);
                this.data = chunkData;
            }

            @Override
            public CompletableFuture<Result> run(BlockBufferBuilderStorage blockBufferBuilderStorage) {
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (!BuiltChunk.this.shouldBuild()) {
                    this.cancelled.set(true);
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                Vec3d vec3d = ChunkBuilder.this.getCameraPosition();
                float f = (float)vec3d.x;
                float g = (float)vec3d.y;
                float h = (float)vec3d.z;
                BufferBuilder.State state = this.data.bufferState;
                if (state == null || !this.data.nonEmptyLayers.contains(RenderLayer.getTranslucent())) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                BufferBuilder bufferBuilder = blockBufferBuilderStorage.get(RenderLayer.getTranslucent());
                BuiltChunk.this.beginBufferBuilding(bufferBuilder);
                bufferBuilder.restoreState(state);
                bufferBuilder.sortQuads(f - (float)BuiltChunk.this.origin.getX(), g - (float)BuiltChunk.this.origin.getY(), h - (float)BuiltChunk.this.origin.getZ());
                this.data.bufferState = bufferBuilder.popState();
                bufferBuilder.end();
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                CompletionStage completableFuture = ChunkBuilder.this.scheduleUpload(blockBufferBuilderStorage.get(RenderLayer.getTranslucent()), BuiltChunk.this.getBuffer(RenderLayer.getTranslucent())).thenApply(void_ -> Result.CANCELLED);
                return ((CompletableFuture)completableFuture).handle((result, throwable) -> {
                    if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                        MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
                    }
                    return this.cancelled.get() ? Result.CANCELLED : Result.SUCCESSFUL;
                });
            }

            @Override
            public void cancel() {
                this.cancelled.set(true);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class RebuildTask
        extends Task {
            @Nullable
            protected ChunkRendererRegion region;

            public RebuildTask(@Nullable double d, ChunkRendererRegion chunkRendererRegion) {
                super(d);
                this.region = chunkRendererRegion;
            }

            @Override
            public CompletableFuture<Result> run(BlockBufferBuilderStorage blockBufferBuilderStorage) {
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (!BuiltChunk.this.shouldBuild()) {
                    this.region = null;
                    BuiltChunk.this.scheduleRebuild(false);
                    this.cancelled.set(true);
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                Vec3d vec3d = ChunkBuilder.this.getCameraPosition();
                float f = (float)vec3d.x;
                float g = (float)vec3d.y;
                float h = (float)vec3d.z;
                ChunkData chunkData = new ChunkData();
                Set<BlockEntity> set = this.render(f, g, h, chunkData, blockBufferBuilderStorage);
                BuiltChunk.this.setNoCullingBlockEntities(set);
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                ArrayList list2 = Lists.newArrayList();
                chunkData.initializedLayers.forEach(renderLayer -> list2.add(ChunkBuilder.this.scheduleUpload(blockBufferBuilderStorage.get((RenderLayer)renderLayer), BuiltChunk.this.getBuffer((RenderLayer)renderLayer))));
                return Util.combine(list2).handle((list, throwable) -> {
                    if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                        MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
                    }
                    if (this.cancelled.get()) {
                        return Result.CANCELLED;
                    }
                    BuiltChunk.this.data.set(chunkData);
                    return Result.SUCCESSFUL;
                });
            }

            private Set<BlockEntity> render(float f, float g, float h, ChunkData chunkData, BlockBufferBuilderStorage blockBufferBuilderStorage) {
                boolean i = true;
                BlockPos blockPos = BuiltChunk.this.origin.toImmutable();
                BlockPos blockPos2 = blockPos.add(15, 15, 15);
                ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
                HashSet<BlockEntity> set = Sets.newHashSet();
                ChunkRendererRegion chunkRendererRegion = this.region;
                this.region = null;
                MatrixStack matrixStack = new MatrixStack();
                if (chunkRendererRegion != null) {
                    BlockModelRenderer.enableBrightnessCache();
                    Random random = new Random();
                    BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
                    for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
                        BufferBuilder bufferBuilder;
                        RenderLayer renderLayer;
                        FluidState fluidState;
                        BlockEntity blockEntity;
                        BlockState blockState = chunkRendererRegion.getBlockState(blockPos3);
                        Block block = blockState.getBlock();
                        if (blockState.isFullOpaque(chunkRendererRegion, blockPos3)) {
                            chunkOcclusionDataBuilder.markClosed(blockPos3);
                        }
                        if (block.hasBlockEntity() && (blockEntity = chunkRendererRegion.getBlockEntity(blockPos3, WorldChunk.CreationType.CHECK)) != null) {
                            this.addBlockEntity(chunkData, set, blockEntity);
                        }
                        if (!(fluidState = chunkRendererRegion.getFluidState(blockPos3)).isEmpty()) {
                            renderLayer = RenderLayers.getFluidLayer(fluidState);
                            bufferBuilder = blockBufferBuilderStorage.get(renderLayer);
                            if (chunkData.initializedLayers.add(renderLayer)) {
                                BuiltChunk.this.beginBufferBuilding(bufferBuilder);
                            }
                            if (blockRenderManager.renderFluid(blockPos3, chunkRendererRegion, bufferBuilder, fluidState)) {
                                chunkData.empty = false;
                                chunkData.nonEmptyLayers.add(renderLayer);
                            }
                        }
                        if (blockState.getRenderType() == BlockRenderType.INVISIBLE) continue;
                        renderLayer = RenderLayers.getBlockLayer(blockState);
                        bufferBuilder = blockBufferBuilderStorage.get(renderLayer);
                        if (chunkData.initializedLayers.add(renderLayer)) {
                            BuiltChunk.this.beginBufferBuilding(bufferBuilder);
                        }
                        matrixStack.push();
                        matrixStack.translate(blockPos3.getX() & 0xF, blockPos3.getY() & 0xF, blockPos3.getZ() & 0xF);
                        if (blockRenderManager.renderBlock(blockState, blockPos3, chunkRendererRegion, matrixStack, bufferBuilder, true, random)) {
                            chunkData.empty = false;
                            chunkData.nonEmptyLayers.add(renderLayer);
                        }
                        matrixStack.pop();
                    }
                    if (chunkData.nonEmptyLayers.contains(RenderLayer.getTranslucent())) {
                        BufferBuilder bufferBuilder2 = blockBufferBuilderStorage.get(RenderLayer.getTranslucent());
                        bufferBuilder2.sortQuads(f - (float)blockPos.getX(), g - (float)blockPos.getY(), h - (float)blockPos.getZ());
                        chunkData.bufferState = bufferBuilder2.popState();
                    }
                    chunkData.initializedLayers.stream().map(blockBufferBuilderStorage::get).forEach(BufferBuilder::end);
                    BlockModelRenderer.disableBrightnessCache();
                }
                chunkData.occlusionGraph = chunkOcclusionDataBuilder.build();
                return set;
            }

            private <E extends BlockEntity> void addBlockEntity(ChunkData chunkData, Set<BlockEntity> set, E blockEntity) {
                BlockEntityRenderer<E> blockEntityRenderer = BlockEntityRenderDispatcher.INSTANCE.get(blockEntity);
                if (blockEntityRenderer != null) {
                    chunkData.blockEntities.add(blockEntity);
                    if (blockEntityRenderer.rendersOutsideBoundingBox(blockEntity)) {
                        set.add(blockEntity);
                    }
                }
            }

            @Override
            public void cancel() {
                this.region = null;
                if (this.cancelled.compareAndSet(false, true)) {
                    BuiltChunk.this.scheduleRebuild(false);
                }
            }
        }
    }
}

