package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.chunk.ChunkStatus;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ChunkBuilder {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_32831 = 4;
	private static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT_NORMAL = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
	private static final int field_35300 = 2;
	private final PriorityBlockingQueue<ChunkBuilder.BuiltChunk.Task> prioritizedTaskQueue = Queues.newPriorityBlockingQueue();
	private final Queue<ChunkBuilder.BuiltChunk.Task> taskQueue = Queues.<ChunkBuilder.BuiltChunk.Task>newLinkedBlockingDeque();
	/**
	 * The number of tasks it can poll from {@link #prioritizedTaskQueue}
	 * before polling from {@link #taskQueue} first instead.
	 */
	private int processablePrioritizedTaskCount = 2;
	private final Queue<BlockBufferBuilderStorage> threadBuffers;
	private final Queue<Runnable> uploadQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	private volatile int queuedTaskCount;
	private volatile int bufferCount;
	final BlockBufferBuilderStorage buffers;
	private final TaskExecutor<Runnable> mailbox;
	private final Executor executor;
	ClientWorld world;
	final WorldRenderer worldRenderer;
	private Vec3d cameraPosition = Vec3d.ZERO;

	public ChunkBuilder(ClientWorld world, WorldRenderer worldRenderer, Executor executor, boolean is64Bits, BlockBufferBuilderStorage buffers) {
		this.world = world;
		this.worldRenderer = worldRenderer;
		int i = Math.max(
			1,
			(int)((double)Runtime.getRuntime().maxMemory() * 0.3) / (RenderLayer.getBlockLayers().stream().mapToInt(RenderLayer::getExpectedBufferSize).sum() * 4) - 1
		);
		int j = Runtime.getRuntime().availableProcessors();
		int k = is64Bits ? j : Math.min(j, 4);
		int l = Math.max(1, Math.min(k, i));
		this.buffers = buffers;
		List<BlockBufferBuilderStorage> list = Lists.<BlockBufferBuilderStorage>newArrayListWithExpectedSize(l);

		try {
			for (int m = 0; m < l; m++) {
				list.add(new BlockBufferBuilderStorage());
			}
		} catch (OutOfMemoryError var14) {
			LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
			int n = Math.min(list.size() * 2 / 3, list.size() - 1);

			for (int o = 0; o < n; o++) {
				list.remove(list.size() - 1);
			}

			System.gc();
		}

		this.threadBuffers = Queues.<BlockBufferBuilderStorage>newArrayDeque(list);
		this.bufferCount = this.threadBuffers.size();
		this.executor = executor;
		this.mailbox = TaskExecutor.create(executor, "Chunk Renderer");
		this.mailbox.send(this::scheduleRunTasks);
	}

	public void setWorld(ClientWorld world) {
		this.world = world;
	}

	private void scheduleRunTasks() {
		if (!this.threadBuffers.isEmpty()) {
			ChunkBuilder.BuiltChunk.Task task = this.pollTask();
			if (task != null) {
				BlockBufferBuilderStorage blockBufferBuilderStorage = (BlockBufferBuilderStorage)this.threadBuffers.poll();
				this.queuedTaskCount = this.prioritizedTaskQueue.size() + this.taskQueue.size();
				this.bufferCount = this.threadBuffers.size();
				CompletableFuture.supplyAsync(Util.debugSupplier(task.getName(), () -> task.run(blockBufferBuilderStorage)), this.executor)
					.thenCompose(future -> future)
					.whenComplete((result, throwable) -> {
						if (throwable != null) {
							MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Batching chunks"));
						} else {
							this.mailbox.send(() -> {
								if (result == ChunkBuilder.Result.SUCCESSFUL) {
									blockBufferBuilderStorage.clear();
								} else {
									blockBufferBuilderStorage.reset();
								}

								this.threadBuffers.add(blockBufferBuilderStorage);
								this.bufferCount = this.threadBuffers.size();
								this.scheduleRunTasks();
							});
						}
					});
			}
		}
	}

	@Nullable
	private ChunkBuilder.BuiltChunk.Task pollTask() {
		if (this.processablePrioritizedTaskCount <= 0) {
			ChunkBuilder.BuiltChunk.Task task = (ChunkBuilder.BuiltChunk.Task)this.taskQueue.poll();
			if (task != null) {
				this.processablePrioritizedTaskCount = 2;
				return task;
			}
		}

		ChunkBuilder.BuiltChunk.Task task = (ChunkBuilder.BuiltChunk.Task)this.prioritizedTaskQueue.poll();
		if (task != null) {
			this.processablePrioritizedTaskCount--;
			return task;
		} else {
			this.processablePrioritizedTaskCount = 2;
			return (ChunkBuilder.BuiltChunk.Task)this.taskQueue.poll();
		}
	}

	public String getDebugString() {
		return String.format(Locale.ROOT, "pC: %03d, pU: %02d, aB: %02d", this.queuedTaskCount, this.uploadQueue.size(), this.bufferCount);
	}

	public int getToBatchCount() {
		return this.queuedTaskCount;
	}

	public int getChunksToUpload() {
		return this.uploadQueue.size();
	}

	public int getFreeBufferCount() {
		return this.bufferCount;
	}

	public void setCameraPosition(Vec3d cameraPosition) {
		this.cameraPosition = cameraPosition;
	}

	public Vec3d getCameraPosition() {
		return this.cameraPosition;
	}

	public void upload() {
		Runnable runnable;
		while ((runnable = (Runnable)this.uploadQueue.poll()) != null) {
			runnable.run();
		}
	}

	public void rebuild(ChunkBuilder.BuiltChunk chunk, ChunkRendererRegionBuilder builder) {
		chunk.rebuild(builder);
	}

	public void reset() {
		this.clear();
	}

	public void send(ChunkBuilder.BuiltChunk.Task task) {
		this.mailbox.send(() -> {
			if (task.prioritized) {
				this.prioritizedTaskQueue.offer(task);
			} else {
				this.taskQueue.offer(task);
			}

			this.queuedTaskCount = this.prioritizedTaskQueue.size() + this.taskQueue.size();
			this.scheduleRunTasks();
		});
	}

	public CompletableFuture<Void> scheduleUpload(BufferBuilder.BuiltBuffer builtBuffer, VertexBuffer glBuffer) {
		return CompletableFuture.runAsync(() -> {
			if (!glBuffer.isClosed()) {
				glBuffer.bind();
				glBuffer.upload(builtBuffer);
				VertexBuffer.unbind();
			}
		}, this.uploadQueue::add);
	}

	private void clear() {
		while (!this.prioritizedTaskQueue.isEmpty()) {
			ChunkBuilder.BuiltChunk.Task task = (ChunkBuilder.BuiltChunk.Task)this.prioritizedTaskQueue.poll();
			if (task != null) {
				task.cancel();
			}
		}

		while (!this.taskQueue.isEmpty()) {
			ChunkBuilder.BuiltChunk.Task task = (ChunkBuilder.BuiltChunk.Task)this.taskQueue.poll();
			if (task != null) {
				task.cancel();
			}
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

	@Environment(EnvType.CLIENT)
	public class BuiltChunk {
		public static final int field_32832 = 16;
		public final int index;
		public final AtomicReference<ChunkBuilder.ChunkData> data = new AtomicReference(ChunkBuilder.ChunkData.EMPTY);
		final AtomicInteger field_36374 = new AtomicInteger(0);
		@Nullable
		private ChunkBuilder.BuiltChunk.RebuildTask rebuildTask;
		@Nullable
		private ChunkBuilder.BuiltChunk.SortTask sortTask;
		private final Set<BlockEntity> blockEntities = Sets.<BlockEntity>newHashSet();
		private final Map<RenderLayer, VertexBuffer> buffers = (Map<RenderLayer, VertexBuffer>)RenderLayer.getBlockLayers()
			.stream()
			.collect(Collectors.toMap(renderLayer -> renderLayer, renderLayer -> new VertexBuffer(VertexBuffer.Usage.STATIC)));
		private Box boundingBox;
		private boolean needsRebuild = true;
		final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
		private final BlockPos.Mutable[] neighborPositions = Util.make(new BlockPos.Mutable[6], neighborPositions -> {
			for (int i = 0; i < neighborPositions.length; i++) {
				neighborPositions[i] = new BlockPos.Mutable();
			}
		});
		private boolean needsImportantRebuild;

		public BuiltChunk(int index, int originX, int originY, int originZ) {
			this.index = index;
			this.setOrigin(originX, originY, originZ);
		}

		private boolean isChunkNonEmpty(BlockPos pos) {
			return ChunkBuilder.this.world.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ChunkStatus.FULL, false)
				!= null;
		}

		public boolean shouldBuild() {
			int i = 24;
			return !(this.getSquaredCameraDistance() > 576.0)
				? true
				: this.isChunkNonEmpty(this.neighborPositions[Direction.WEST.ordinal()])
					&& this.isChunkNonEmpty(this.neighborPositions[Direction.NORTH.ordinal()])
					&& this.isChunkNonEmpty(this.neighborPositions[Direction.EAST.ordinal()])
					&& this.isChunkNonEmpty(this.neighborPositions[Direction.SOUTH.ordinal()]);
		}

		public Box getBoundingBox() {
			return this.boundingBox;
		}

		public VertexBuffer getBuffer(RenderLayer layer) {
			return (VertexBuffer)this.buffers.get(layer);
		}

		public void setOrigin(int x, int y, int z) {
			this.clear();
			this.origin.set(x, y, z);
			this.boundingBox = new Box((double)x, (double)y, (double)z, (double)(x + 16), (double)(y + 16), (double)(z + 16));

			for (Direction direction : Direction.values()) {
				this.neighborPositions[direction.ordinal()].set(this.origin).move(direction, 16);
			}
		}

		protected double getSquaredCameraDistance() {
			Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
			double d = this.boundingBox.minX + 8.0 - camera.getPos().x;
			double e = this.boundingBox.minY + 8.0 - camera.getPos().y;
			double f = this.boundingBox.minZ + 8.0 - camera.getPos().z;
			return d * d + e * e + f * f;
		}

		void beginBufferBuilding(BufferBuilder buffer) {
			buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		}

		public ChunkBuilder.ChunkData getData() {
			return (ChunkBuilder.ChunkData)this.data.get();
		}

		private void clear() {
			this.cancel();
			this.data.set(ChunkBuilder.ChunkData.EMPTY);
			this.needsRebuild = true;
		}

		public void delete() {
			this.clear();
			this.buffers.values().forEach(VertexBuffer::close);
		}

		public BlockPos getOrigin() {
			return this.origin;
		}

		public void scheduleRebuild(boolean important) {
			boolean bl = this.needsRebuild;
			this.needsRebuild = true;
			this.needsImportantRebuild = important | (bl && this.needsImportantRebuild);
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

		public boolean scheduleSort(RenderLayer layer, ChunkBuilder chunkRenderer) {
			ChunkBuilder.ChunkData chunkData = this.getData();
			if (this.sortTask != null) {
				this.sortTask.cancel();
			}

			if (!chunkData.nonEmptyLayers.contains(layer)) {
				return false;
			} else {
				this.sortTask = new ChunkBuilder.BuiltChunk.SortTask(this.getSquaredCameraDistance(), chunkData);
				chunkRenderer.send(this.sortTask);
				return true;
			}
		}

		protected boolean cancel() {
			boolean bl = false;
			if (this.rebuildTask != null) {
				this.rebuildTask.cancel();
				this.rebuildTask = null;
				bl = true;
			}

			if (this.sortTask != null) {
				this.sortTask.cancel();
				this.sortTask = null;
			}

			return bl;
		}

		public ChunkBuilder.BuiltChunk.Task createRebuildTask(ChunkRendererRegionBuilder builder) {
			boolean bl = this.cancel();
			BlockPos blockPos = this.origin.toImmutable();
			int i = 1;
			ChunkRendererRegion chunkRendererRegion = builder.build(ChunkBuilder.this.world, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
			boolean bl2 = this.data.get() == ChunkBuilder.ChunkData.EMPTY;
			if (bl2 && bl) {
				this.field_36374.incrementAndGet();
			}

			this.rebuildTask = new ChunkBuilder.BuiltChunk.RebuildTask(this.getSquaredCameraDistance(), chunkRendererRegion, !bl2 || this.field_36374.get() > 2);
			return this.rebuildTask;
		}

		public void scheduleRebuild(ChunkBuilder chunkRenderer, ChunkRendererRegionBuilder builder) {
			ChunkBuilder.BuiltChunk.Task task = this.createRebuildTask(builder);
			chunkRenderer.send(task);
		}

		void setNoCullingBlockEntities(Collection<BlockEntity> collection) {
			Set<BlockEntity> set = Sets.<BlockEntity>newHashSet(collection);
			Set<BlockEntity> set2;
			synchronized (this.blockEntities) {
				set2 = Sets.<BlockEntity>newHashSet(this.blockEntities);
				set.removeAll(this.blockEntities);
				set2.removeAll(collection);
				this.blockEntities.clear();
				this.blockEntities.addAll(collection);
			}

			ChunkBuilder.this.worldRenderer.updateNoCullingBlockEntities(set2, set);
		}

		public void rebuild(ChunkRendererRegionBuilder builder) {
			ChunkBuilder.BuiltChunk.Task task = this.createRebuildTask(builder);
			task.run(ChunkBuilder.this.buffers);
		}

		@Environment(EnvType.CLIENT)
		class RebuildTask extends ChunkBuilder.BuiltChunk.Task {
			@Nullable
			protected ChunkRendererRegion region;

			public RebuildTask(double distance, @Nullable ChunkRendererRegion region, boolean prioritized) {
				super(distance, prioritized);
				this.region = region;
			}

			@Override
			protected String getName() {
				return "rend_chk_rebuild";
			}

			@Override
			public CompletableFuture<ChunkBuilder.Result> run(BlockBufferBuilderStorage buffers) {
				if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else if (!BuiltChunk.this.shouldBuild()) {
					this.region = null;
					BuiltChunk.this.scheduleRebuild(false);
					this.cancelled.set(true);
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else {
					Vec3d vec3d = ChunkBuilder.this.getCameraPosition();
					float f = (float)vec3d.x;
					float g = (float)vec3d.y;
					float h = (float)vec3d.z;
					ChunkBuilder.BuiltChunk.RebuildTask.RenderData renderData = this.render(f, g, h, buffers);
					BuiltChunk.this.setNoCullingBlockEntities(renderData.noCullingBlockEntities);
					if (this.cancelled.get()) {
						renderData.field_39081.values().forEach(BufferBuilder.BuiltBuffer::release);
						return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
					} else {
						ChunkBuilder.ChunkData chunkData = new ChunkBuilder.ChunkData();
						chunkData.occlusionGraph = renderData.chunkOcclusionData;
						chunkData.blockEntities.addAll(renderData.blockEntities);
						chunkData.transparentSortingData = renderData.translucencySortingData;
						List<CompletableFuture<Void>> list = Lists.<CompletableFuture<Void>>newArrayList();
						renderData.field_39081.forEach((renderLayer, builtBuffer) -> {
							list.add(ChunkBuilder.this.scheduleUpload(builtBuffer, BuiltChunk.this.getBuffer(renderLayer)));
							chunkData.nonEmptyLayers.add(renderLayer);
						});
						return Util.combine(list).handle((results, throwable) -> {
							if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
								MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Rendering chunk"));
							}

							if (this.cancelled.get()) {
								return ChunkBuilder.Result.CANCELLED;
							} else {
								BuiltChunk.this.data.set(chunkData);
								BuiltChunk.this.field_36374.set(0);
								ChunkBuilder.this.worldRenderer.addBuiltChunk(BuiltChunk.this);
								return ChunkBuilder.Result.SUCCESSFUL;
							}
						});
					}
				}
			}

			private ChunkBuilder.BuiltChunk.RebuildTask.RenderData render(
				float cameraX, float cameraY, float cameraZ, BlockBufferBuilderStorage blockBufferBuilderStorage
			) {
				ChunkBuilder.BuiltChunk.RebuildTask.RenderData renderData = new ChunkBuilder.BuiltChunk.RebuildTask.RenderData();
				int i = 1;
				BlockPos blockPos = BuiltChunk.this.origin.toImmutable();
				BlockPos blockPos2 = blockPos.add(15, 15, 15);
				ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
				ChunkRendererRegion chunkRendererRegion = this.region;
				this.region = null;
				MatrixStack matrixStack = new MatrixStack();
				if (chunkRendererRegion != null) {
					BlockModelRenderer.enableBrightnessCache();
					Set<RenderLayer> set = new ReferenceArraySet<>(RenderLayer.getBlockLayers().size());
					Random random = Random.create();
					BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();

					for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
						BlockState blockState = chunkRendererRegion.getBlockState(blockPos3);
						if (blockState.isOpaqueFullCube(chunkRendererRegion, blockPos3)) {
							chunkOcclusionDataBuilder.markClosed(blockPos3);
						}

						if (blockState.hasBlockEntity()) {
							BlockEntity blockEntity = chunkRendererRegion.getBlockEntity(blockPos3);
							if (blockEntity != null) {
								this.addBlockEntity(renderData, blockEntity);
							}
						}

						BlockState blockState2 = chunkRendererRegion.getBlockState(blockPos3);
						FluidState fluidState = blockState2.getFluidState();
						if (!fluidState.isEmpty()) {
							RenderLayer renderLayer = RenderLayers.getFluidLayer(fluidState);
							BufferBuilder bufferBuilder = blockBufferBuilderStorage.get(renderLayer);
							if (set.add(renderLayer)) {
								BuiltChunk.this.beginBufferBuilding(bufferBuilder);
							}

							blockRenderManager.renderFluid(blockPos3, chunkRendererRegion, bufferBuilder, blockState2, fluidState);
						}

						if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
							RenderLayer renderLayer = RenderLayers.getBlockLayer(blockState);
							BufferBuilder bufferBuilder = blockBufferBuilderStorage.get(renderLayer);
							if (set.add(renderLayer)) {
								BuiltChunk.this.beginBufferBuilding(bufferBuilder);
							}

							matrixStack.push();
							matrixStack.translate((float)(blockPos3.getX() & 15), (float)(blockPos3.getY() & 15), (float)(blockPos3.getZ() & 15));
							blockRenderManager.renderBlock(blockState, blockPos3, chunkRendererRegion, matrixStack, bufferBuilder, true, random);
							matrixStack.pop();
						}
					}

					if (set.contains(RenderLayer.getTranslucent())) {
						BufferBuilder bufferBuilder2 = blockBufferBuilderStorage.get(RenderLayer.getTranslucent());
						if (!bufferBuilder2.isBatchEmpty()) {
							bufferBuilder2.setSorter(VertexSorter.byDistance(cameraX - (float)blockPos.getX(), cameraY - (float)blockPos.getY(), cameraZ - (float)blockPos.getZ()));
							renderData.translucencySortingData = bufferBuilder2.getSortingData();
						}
					}

					for (RenderLayer renderLayer2 : set) {
						BufferBuilder.BuiltBuffer builtBuffer = blockBufferBuilderStorage.get(renderLayer2).endNullable();
						if (builtBuffer != null) {
							renderData.field_39081.put(renderLayer2, builtBuffer);
						}
					}

					BlockModelRenderer.disableBrightnessCache();
				}

				renderData.chunkOcclusionData = chunkOcclusionDataBuilder.build();
				return renderData;
			}

			private <E extends BlockEntity> void addBlockEntity(ChunkBuilder.BuiltChunk.RebuildTask.RenderData renderData, E blockEntity) {
				BlockEntityRenderer<E> blockEntityRenderer = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(blockEntity);
				if (blockEntityRenderer != null) {
					renderData.blockEntities.add(blockEntity);
					if (blockEntityRenderer.rendersOutsideBoundingBox(blockEntity)) {
						renderData.noCullingBlockEntities.add(blockEntity);
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

			@Environment(EnvType.CLIENT)
			static final class RenderData {
				public final List<BlockEntity> noCullingBlockEntities = new ArrayList();
				public final List<BlockEntity> blockEntities = new ArrayList();
				public final Map<RenderLayer, BufferBuilder.BuiltBuffer> field_39081 = new Reference2ObjectArrayMap<>();
				public ChunkOcclusionData chunkOcclusionData = new ChunkOcclusionData();
				@Nullable
				public BufferBuilder.TransparentSortingData translucencySortingData;
			}
		}

		@Environment(EnvType.CLIENT)
		class SortTask extends ChunkBuilder.BuiltChunk.Task {
			private final ChunkBuilder.ChunkData data;

			public SortTask(double distance, ChunkBuilder.ChunkData data) {
				super(distance, true);
				this.data = data;
			}

			@Override
			protected String getName() {
				return "rend_chk_sort";
			}

			@Override
			public CompletableFuture<ChunkBuilder.Result> run(BlockBufferBuilderStorage buffers) {
				if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else if (!BuiltChunk.this.shouldBuild()) {
					this.cancelled.set(true);
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else {
					Vec3d vec3d = ChunkBuilder.this.getCameraPosition();
					float f = (float)vec3d.x;
					float g = (float)vec3d.y;
					float h = (float)vec3d.z;
					BufferBuilder.TransparentSortingData transparentSortingData = this.data.transparentSortingData;
					if (transparentSortingData != null && !this.data.isEmpty(RenderLayer.getTranslucent())) {
						BufferBuilder bufferBuilder = buffers.get(RenderLayer.getTranslucent());
						BuiltChunk.this.beginBufferBuilding(bufferBuilder);
						bufferBuilder.beginSortedIndexBuffer(transparentSortingData);
						bufferBuilder.setSorter(
							VertexSorter.byDistance(f - (float)BuiltChunk.this.origin.getX(), g - (float)BuiltChunk.this.origin.getY(), h - (float)BuiltChunk.this.origin.getZ())
						);
						this.data.transparentSortingData = bufferBuilder.getSortingData();
						BufferBuilder.BuiltBuffer builtBuffer = bufferBuilder.end();
						if (this.cancelled.get()) {
							builtBuffer.release();
							return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
						} else {
							CompletableFuture<ChunkBuilder.Result> completableFuture = ChunkBuilder.this.scheduleUpload(
									builtBuffer, BuiltChunk.this.getBuffer(RenderLayer.getTranslucent())
								)
								.thenApply(void_ -> ChunkBuilder.Result.CANCELLED);
							return completableFuture.handle((result, throwable) -> {
								if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
									MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Rendering chunk"));
								}

								return this.cancelled.get() ? ChunkBuilder.Result.CANCELLED : ChunkBuilder.Result.SUCCESSFUL;
							});
						}
					} else {
						return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
					}
				}
			}

			@Override
			public void cancel() {
				this.cancelled.set(true);
			}
		}

		@Environment(EnvType.CLIENT)
		abstract class Task implements Comparable<ChunkBuilder.BuiltChunk.Task> {
			protected final double distance;
			protected final AtomicBoolean cancelled = new AtomicBoolean(false);
			protected final boolean prioritized;

			public Task(double distance, boolean prioritized) {
				this.distance = distance;
				this.prioritized = prioritized;
			}

			public abstract CompletableFuture<ChunkBuilder.Result> run(BlockBufferBuilderStorage buffers);

			public abstract void cancel();

			protected abstract String getName();

			public int compareTo(ChunkBuilder.BuiltChunk.Task task) {
				return Doubles.compare(this.distance, task.distance);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class ChunkData {
		public static final ChunkBuilder.ChunkData EMPTY = new ChunkBuilder.ChunkData() {
			@Override
			public boolean isVisibleThrough(Direction from, Direction to) {
				return false;
			}
		};
		final Set<RenderLayer> nonEmptyLayers = new ObjectArraySet<>(RenderLayer.getBlockLayers().size());
		final List<BlockEntity> blockEntities = Lists.<BlockEntity>newArrayList();
		ChunkOcclusionData occlusionGraph = new ChunkOcclusionData();
		@Nullable
		BufferBuilder.TransparentSortingData transparentSortingData;

		public boolean isEmpty() {
			return this.nonEmptyLayers.isEmpty();
		}

		public boolean isEmpty(RenderLayer layer) {
			return !this.nonEmptyLayers.contains(layer);
		}

		public List<BlockEntity> getBlockEntities() {
			return this.blockEntities;
		}

		public boolean isVisibleThrough(Direction from, Direction to) {
			return this.occlusionGraph.isVisibleThrough(from, to);
		}
	}

	@Environment(EnvType.CLIENT)
	static enum Result {
		SUCCESSFUL,
		CANCELLED;
	}
}
