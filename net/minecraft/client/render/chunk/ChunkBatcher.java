/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
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
import net.minecraft.client.gl.GlBuffer;
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
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkOcclusionGraph;
import net.minecraft.client.render.chunk.ChunkOcclusionGraphBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkBatcher {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PriorityQueue<ChunkRenderer.class_4577> pendingChunks = Queues.newPriorityQueue();
    private final Queue<BlockLayeredBufferBuilderStorage> field_20827;
    private final Queue<Runnable> pendingUploads = Queues.newConcurrentLinkedQueue();
    private volatile int field_20992;
    private volatile int field_20993;
    private final BlockLayeredBufferBuilderStorage field_20828;
    private final MailboxProcessor<Runnable> field_20829;
    private final Executor field_20830;
    private World field_20831;
    private final WorldRenderer field_20832;
    private Vec3d cameraPosition = Vec3d.ZERO;

    public ChunkBatcher(World world, WorldRenderer worldRenderer, Executor executor, boolean bl, BlockLayeredBufferBuilderStorage blockLayeredBufferBuilderStorage) {
        this.field_20831 = world;
        this.field_20832 = worldRenderer;
        int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / (RenderLayer.getBlockLayers().stream().mapToInt(RenderLayer::getExpectedBufferSize).sum() * 4) - 1);
        int j = Runtime.getRuntime().availableProcessors();
        int k = bl ? j : Math.min(j, 4);
        int l = Math.max(1, Math.min(k, i));
        this.field_20828 = blockLayeredBufferBuilderStorage;
        ArrayList<BlockLayeredBufferBuilderStorage> list = Lists.newArrayListWithExpectedSize(l);
        try {
            for (int m = 0; m < l; ++m) {
                list.add(new BlockLayeredBufferBuilderStorage());
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            LOGGER.warn("Allocated only {}/{} buffers", (Object)list.size(), (Object)l);
            int n = Math.min(list.size() * 2 / 3, list.size() - 1);
            for (int o = 0; o < n; ++o) {
                list.remove(list.size() - 1);
            }
            System.gc();
        }
        this.field_20827 = Queues.newArrayDeque(list);
        this.field_20993 = this.field_20827.size();
        this.field_20830 = executor;
        this.field_20829 = MailboxProcessor.create(executor, "Chunk Renderer");
        this.field_20829.send(this::method_22763);
    }

    public void method_22752(World world) {
        this.field_20831 = world;
    }

    private void method_22763() {
        if (this.field_20827.isEmpty()) {
            return;
        }
        ChunkRenderer.class_4577 lv = this.pendingChunks.poll();
        if (lv == null) {
            return;
        }
        BlockLayeredBufferBuilderStorage blockLayeredBufferBuilderStorage = this.field_20827.poll();
        this.field_20992 = this.pendingChunks.size();
        this.field_20993 = this.field_20827.size();
        ((CompletableFuture)CompletableFuture.runAsync(() -> {}, this.field_20830).thenCompose(void_ -> lv.scheduleBatching(blockLayeredBufferBuilderStorage))).whenComplete((result, throwable) -> {
            this.field_20829.send(() -> {
                if (result == Result.SUCCESSFUL) {
                    blockLayeredBufferBuilderStorage.clear();
                } else {
                    blockLayeredBufferBuilderStorage.reset();
                }
                this.field_20827.add(blockLayeredBufferBuilderStorage);
                this.field_20993 = this.field_20827.size();
                this.method_22763();
            });
            if (throwable != null) {
                CrashReport crashReport = CrashReport.create(throwable, "Batching chunks");
                MinecraftClient.getInstance().setCrashReport(MinecraftClient.getInstance().populateCrashReport(crashReport));
            }
        });
    }

    public String getDebugString() {
        return String.format("pC: %03d, pU: %02d, aB: %02d", this.field_20992, this.pendingUploads.size(), this.field_20993);
    }

    public void setCameraPosition(Vec3d vec3d) {
        this.cameraPosition = vec3d;
    }

    public Vec3d getCameraPosition() {
        return this.cameraPosition;
    }

    public boolean method_22761() {
        Runnable runnable;
        boolean bl = false;
        while ((runnable = this.pendingUploads.poll()) != null) {
            runnable.run();
            bl = true;
        }
        return bl;
    }

    public void rebuildSync(ChunkRenderer chunkRenderer) {
        chunkRenderer.method_22781();
    }

    public void reset() {
        this.clear();
    }

    public void method_22756(ChunkRenderer.class_4577 arg) {
        this.field_20829.send(() -> {
            this.pendingChunks.offer(arg);
            this.field_20992 = this.pendingChunks.size();
            this.method_22763();
        });
    }

    public CompletableFuture<Void> upload(BufferBuilder bufferBuilder, GlBuffer glBuffer) {
        return CompletableFuture.runAsync(() -> {}, this.pendingUploads::add).thenCompose(void_ -> this.method_22759(bufferBuilder, glBuffer));
    }

    private CompletableFuture<Void> method_22759(BufferBuilder bufferBuilder, GlBuffer glBuffer) {
        return glBuffer.method_22643(bufferBuilder);
    }

    private void clear() {
        while (!this.pendingChunks.isEmpty()) {
            ChunkRenderer.class_4577 lv = this.pendingChunks.poll();
            if (lv == null) continue;
            lv.cancel();
        }
        this.field_20992 = 0;
    }

    public boolean isEmpty() {
        return this.field_20992 == 0 && this.pendingUploads.isEmpty();
    }

    public void stop() {
        this.clear();
        this.field_20829.close();
        this.field_20827.clear();
    }

    @Environment(value=EnvType.CLIENT)
    public static class ChunkRenderData {
        public static final ChunkRenderData EMPTY = new ChunkRenderData(){

            @Override
            public boolean isVisibleThrough(Direction direction, Direction direction2) {
                return false;
            }
        };
        private final Set<RenderLayer> nonEmpty = Sets.newHashSet();
        private final Set<RenderLayer> initialized = Sets.newHashSet();
        private boolean empty = true;
        private final List<BlockEntity> blockEntities = Lists.newArrayList();
        private ChunkOcclusionGraph occlusionGraph = new ChunkOcclusionGraph();
        @Nullable
        private BufferBuilder.State bufferState;

        public boolean isEmpty() {
            return this.empty;
        }

        public boolean isEmpty(RenderLayer renderLayer) {
            return !this.nonEmpty.contains(renderLayer);
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
    public class ChunkRenderer {
        public final AtomicReference<ChunkRenderData> data = new AtomicReference<ChunkRenderData>(ChunkRenderData.EMPTY);
        @Nullable
        private class_4578 field_20834;
        @Nullable
        private class_4579 task;
        private final Set<BlockEntity> blockEntities = Sets.newHashSet();
        private final Map<RenderLayer, GlBuffer> buffers = RenderLayer.getBlockLayers().stream().collect(Collectors.toMap(renderLayer -> renderLayer, renderLayer -> new GlBuffer(VertexFormats.POSITION_COLOR_UV_NORMAL)));
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

        private boolean isChunkNonEmpty(BlockPos blockPos) {
            return !ChunkBatcher.this.field_20831.method_8497(blockPos.getX() >> 4, blockPos.getZ() >> 4).isEmpty();
        }

        public boolean shouldBuild() {
            int i = 24;
            if (this.getSquaredCameraDistance() > 576.0) {
                return this.isChunkNonEmpty(this.neighborPositions[Direction.WEST.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.NORTH.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.EAST.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.SOUTH.ordinal()]);
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

        public GlBuffer getGlBuffer(RenderLayer renderLayer) {
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
            double d = this.boundingBox.minX + 8.0 - camera.getPos().x;
            double e = this.boundingBox.minY + 8.0 - camera.getPos().y;
            double f = this.boundingBox.minZ + 8.0 - camera.getPos().z;
            return d * d + e * e + f * f;
        }

        private void beginBufferBuilding(BufferBuilder bufferBuilder) {
            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
        }

        public ChunkRenderData getData() {
            return this.data.get();
        }

        private void clear() {
            this.cancel();
            this.data.set(ChunkRenderData.EMPTY);
            this.rebuildScheduled = true;
        }

        public void delete() {
            this.clear();
            this.buffers.values().forEach(GlBuffer::delete);
        }

        public BlockPos getOrigin() {
            return this.origin;
        }

        public void scheduleRebuild(boolean bl) {
            boolean bl2 = this.rebuildScheduled;
            this.rebuildScheduled = true;
            this.rebuildOnClientThread = bl | (bl2 && this.rebuildOnClientThread);
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

        public boolean method_22773(RenderLayer renderLayer, ChunkBatcher chunkBatcher) {
            ChunkRenderData chunkRenderData = this.getData();
            if (this.task != null) {
                this.task.cancel();
            }
            if (!chunkRenderData.initialized.contains(renderLayer)) {
                return false;
            }
            this.task = new class_4579(this.getSquaredCameraDistance(), chunkRenderData);
            chunkBatcher.method_22756(this.task);
            return true;
        }

        protected void cancel() {
            if (this.field_20834 != null) {
                this.field_20834.cancel();
                this.field_20834 = null;
            }
            if (this.task != null) {
                this.task.cancel();
                this.task = null;
            }
        }

        public class_4577 startRebuild() {
            this.cancel();
            BlockPos blockPos = this.origin.toImmutable();
            boolean i = true;
            ChunkRendererRegion chunkRendererRegion = ChunkRendererRegion.create(ChunkBatcher.this.field_20831, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
            this.field_20834 = new class_4578(this.getSquaredCameraDistance(), chunkRendererRegion);
            return this.field_20834;
        }

        public void method_22777(ChunkBatcher chunkBatcher) {
            class_4577 lv = this.startRebuild();
            chunkBatcher.method_22756(lv);
        }

        private void method_22778(Set<BlockEntity> set) {
            HashSet<BlockEntity> set2 = Sets.newHashSet(set);
            HashSet<BlockEntity> set3 = Sets.newHashSet(this.blockEntities);
            set2.removeAll(this.blockEntities);
            set3.removeAll(set);
            this.blockEntities.clear();
            this.blockEntities.addAll(set);
            ChunkBatcher.this.field_20832.updateBlockEntities(set3, set2);
        }

        public void method_22781() {
            class_4577 lv = this.startRebuild();
            lv.scheduleBatching(ChunkBatcher.this.field_20828);
        }

        @Environment(value=EnvType.CLIENT)
        abstract class class_4577
        implements Comparable<class_4577> {
            protected final double field_20835;
            protected final AtomicBoolean cancelled = new AtomicBoolean(false);

            public class_4577(double d) {
                this.field_20835 = d;
            }

            public abstract CompletableFuture<Result> scheduleBatching(BlockLayeredBufferBuilderStorage var1);

            public abstract void cancel();

            public int method_22784(class_4577 arg) {
                return Doubles.compare(this.field_20835, arg.field_20835);
            }

            @Override
            public /* synthetic */ int compareTo(Object object) {
                return this.method_22784((class_4577)object);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class class_4579
        extends class_4577 {
            private final ChunkRenderData data;

            public class_4579(double d, ChunkRenderData chunkRenderData) {
                super(d);
                this.data = chunkRenderData;
            }

            @Override
            public CompletableFuture<Result> scheduleBatching(BlockLayeredBufferBuilderStorage blockLayeredBufferBuilderStorage) {
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (!ChunkRenderer.this.shouldBuild()) {
                    this.cancelled.set(true);
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                Vec3d vec3d = ChunkBatcher.this.getCameraPosition();
                float f = (float)vec3d.x;
                float g = (float)vec3d.y;
                float h = (float)vec3d.z;
                BufferBuilder.State state = this.data.bufferState;
                if (state == null || !this.data.nonEmpty.contains(RenderLayer.getTranslucent())) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                BufferBuilder bufferBuilder = blockLayeredBufferBuilderStorage.get(RenderLayer.getTranslucent());
                ChunkRenderer.this.beginBufferBuilding(bufferBuilder);
                bufferBuilder.restoreState(state);
                bufferBuilder.sortQuads(f - (float)ChunkRenderer.this.origin.getX(), g - (float)ChunkRenderer.this.origin.getY(), h - (float)ChunkRenderer.this.origin.getZ());
                this.data.bufferState = bufferBuilder.toBufferState();
                bufferBuilder.end();
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                CompletionStage completableFuture = ChunkBatcher.this.upload(blockLayeredBufferBuilderStorage.get(RenderLayer.getTranslucent()), ChunkRenderer.this.getGlBuffer(RenderLayer.getTranslucent())).thenApply(void_ -> Result.CANCELLED);
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
        class class_4578
        extends class_4577 {
            @Nullable
            protected ChunkRendererRegion field_20838;

            public class_4578(@Nullable double d, ChunkRendererRegion chunkRendererRegion) {
                super(d);
                this.field_20838 = chunkRendererRegion;
            }

            @Override
            public CompletableFuture<Result> scheduleBatching(BlockLayeredBufferBuilderStorage blockLayeredBufferBuilderStorage) {
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (!ChunkRenderer.this.shouldBuild()) {
                    this.field_20838 = null;
                    ChunkRenderer.this.scheduleRebuild(false);
                    this.cancelled.set(true);
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                Vec3d vec3d = ChunkBatcher.this.getCameraPosition();
                float f = (float)vec3d.x;
                float g = (float)vec3d.y;
                float h = (float)vec3d.z;
                ChunkRenderData chunkRenderData = new ChunkRenderData();
                Set<BlockEntity> set = this.method_22785(f, g, h, chunkRenderData, blockLayeredBufferBuilderStorage);
                ChunkRenderer.this.method_22778(set);
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                ArrayList list2 = Lists.newArrayList();
                chunkRenderData.initialized.forEach(renderLayer -> list2.add(ChunkBatcher.this.upload(blockLayeredBufferBuilderStorage.get((RenderLayer)renderLayer), ChunkRenderer.this.getGlBuffer((RenderLayer)renderLayer))));
                return SystemUtil.thenCombine(list2).handle((list, throwable) -> {
                    if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                        MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
                    }
                    if (this.cancelled.get()) {
                        return Result.CANCELLED;
                    }
                    ChunkRenderer.this.data.set(chunkRenderData);
                    return Result.SUCCESSFUL;
                });
            }

            private Set<BlockEntity> method_22785(float f, float g, float h, ChunkRenderData chunkRenderData, BlockLayeredBufferBuilderStorage blockLayeredBufferBuilderStorage) {
                boolean i = true;
                BlockPos blockPos = ChunkRenderer.this.origin.toImmutable();
                BlockPos blockPos2 = blockPos.add(15, 15, 15);
                ChunkOcclusionGraphBuilder chunkOcclusionGraphBuilder = new ChunkOcclusionGraphBuilder();
                HashSet<BlockEntity> set = Sets.newHashSet();
                ChunkRendererRegion chunkRendererRegion = this.field_20838;
                this.field_20838 = null;
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
                            chunkOcclusionGraphBuilder.markClosed(blockPos3);
                        }
                        if (block.hasBlockEntity() && (blockEntity = chunkRendererRegion.getBlockEntity(blockPos3, WorldChunk.CreationType.CHECK)) != null) {
                            this.method_23087(chunkRenderData, set, blockEntity);
                        }
                        if (!(fluidState = chunkRendererRegion.getFluidState(blockPos3)).isEmpty()) {
                            renderLayer = RenderLayers.getFluidLayer(fluidState);
                            bufferBuilder = blockLayeredBufferBuilderStorage.get(renderLayer);
                            if (chunkRenderData.initialized.add(renderLayer)) {
                                ChunkRenderer.this.beginBufferBuilding(bufferBuilder);
                            }
                            if (blockRenderManager.tesselateFluid(blockPos3, chunkRendererRegion, bufferBuilder, fluidState)) {
                                chunkRenderData.empty = false;
                                chunkRenderData.nonEmpty.add(renderLayer);
                            }
                        }
                        if (blockState.getRenderType() == BlockRenderType.INVISIBLE) continue;
                        renderLayer = RenderLayers.getBlockLayer(blockState);
                        bufferBuilder = blockLayeredBufferBuilderStorage.get(renderLayer);
                        if (chunkRenderData.initialized.add(renderLayer)) {
                            ChunkRenderer.this.beginBufferBuilding(bufferBuilder);
                        }
                        matrixStack.push();
                        matrixStack.translate(blockPos3.getX() & 0xF, blockPos3.getY() & 0xF, blockPos3.getZ() & 0xF);
                        if (blockRenderManager.tesselateBlock(blockState, blockPos3, chunkRendererRegion, matrixStack, bufferBuilder, true, random)) {
                            chunkRenderData.empty = false;
                            chunkRenderData.nonEmpty.add(renderLayer);
                        }
                        matrixStack.pop();
                    }
                    if (chunkRenderData.nonEmpty.contains(RenderLayer.getTranslucent())) {
                        BufferBuilder bufferBuilder2 = blockLayeredBufferBuilderStorage.get(RenderLayer.getTranslucent());
                        bufferBuilder2.sortQuads(f - (float)blockPos.getX(), g - (float)blockPos.getY(), h - (float)blockPos.getZ());
                        chunkRenderData.bufferState = bufferBuilder2.toBufferState();
                    }
                    chunkRenderData.initialized.stream().map(blockLayeredBufferBuilderStorage::get).forEach(BufferBuilder::end);
                    BlockModelRenderer.disableBrightnessCache();
                }
                chunkRenderData.occlusionGraph = chunkOcclusionGraphBuilder.build();
                return set;
            }

            private <E extends BlockEntity> void method_23087(ChunkRenderData chunkRenderData, Set<BlockEntity> set, E blockEntity) {
                BlockEntityRenderer<E> blockEntityRenderer = BlockEntityRenderDispatcher.INSTANCE.get(blockEntity);
                if (blockEntityRenderer != null) {
                    chunkRenderData.blockEntities.add(blockEntity);
                    if (blockEntityRenderer.method_3563(blockEntity)) {
                        set.add(blockEntity);
                    }
                }
            }

            @Override
            public void cancel() {
                this.field_20838 = null;
                if (this.cancelled.compareAndSet(false, true)) {
                    ChunkRenderer.this.scheduleRebuild(false);
                }
            }
        }
    }
}

