package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.systems.VertexSorter;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
import net.minecraft.class_9799;
import net.minecraft.class_9801;
import net.minecraft.class_9810;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(EnvType.CLIENT)
public class ChunkBuilder {
	private static final int field_35300 = 2;
	private final PriorityBlockingQueue<ChunkBuilder.BuiltChunk.Task> prioritizedTaskQueue = Queues.newPriorityBlockingQueue();
	private final Queue<ChunkBuilder.BuiltChunk.Task> taskQueue = Queues.<ChunkBuilder.BuiltChunk.Task>newLinkedBlockingDeque();
	/**
	 * The number of tasks it can poll from {@link #prioritizedTaskQueue}
	 * before polling from {@link #taskQueue} first instead.
	 */
	private int processablePrioritizedTaskCount = 2;
	private final Queue<Runnable> uploadQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	final BlockBufferBuilderStorage buffers;
	private final BlockBufferBuilderPool buffersPool;
	private volatile int queuedTaskCount;
	private volatile boolean stopped;
	private final TaskExecutor<Runnable> mailbox;
	private final Executor executor;
	ClientWorld world;
	final WorldRenderer worldRenderer;
	private Vec3d cameraPosition = Vec3d.ZERO;
	final class_9810 field_52171;

	public ChunkBuilder(
		ClientWorld world,
		WorldRenderer worldRenderer,
		Executor executor,
		BufferBuilderStorage bufferBuilderStorage,
		BlockRenderManager blockRenderManager,
		BlockEntityRenderDispatcher blockEntityRenderDispatcher
	) {
		this.world = world;
		this.worldRenderer = worldRenderer;
		this.buffers = bufferBuilderStorage.getBlockBufferBuilders();
		this.buffersPool = bufferBuilderStorage.getBlockBufferBuildersPool();
		this.executor = executor;
		this.mailbox = TaskExecutor.create(executor, "Section Renderer");
		this.mailbox.send(this::scheduleRunTasks);
		this.field_52171 = new class_9810(blockRenderManager, blockEntityRenderDispatcher);
	}

	public void setWorld(ClientWorld world) {
		this.world = world;
	}

	private void scheduleRunTasks() {
		if (!this.stopped && !this.buffersPool.hasNoAvailableBuilder()) {
			ChunkBuilder.BuiltChunk.Task task = this.pollTask();
			if (task != null) {
				BlockBufferBuilderStorage blockBufferBuilderStorage = (BlockBufferBuilderStorage)Objects.requireNonNull(this.buffersPool.acquire());
				this.queuedTaskCount = this.prioritizedTaskQueue.size() + this.taskQueue.size();
				CompletableFuture.supplyAsync(Util.debugSupplier(task.getName(), () -> task.run(blockBufferBuilderStorage)), this.executor)
					.thenCompose(future -> future)
					.whenComplete((result, throwable) -> {
						if (throwable != null) {
							MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Batching sections"));
						} else {
							this.mailbox.send(() -> {
								if (result == ChunkBuilder.Result.SUCCESSFUL) {
									blockBufferBuilderStorage.clear();
								} else {
									blockBufferBuilderStorage.reset();
								}

								this.buffersPool.release(blockBufferBuilderStorage);
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
		return String.format(Locale.ROOT, "pC: %03d, pU: %02d, aB: %02d", this.queuedTaskCount, this.uploadQueue.size(), this.buffersPool.getAvailableBuilderCount());
	}

	public int getToBatchCount() {
		return this.queuedTaskCount;
	}

	public int getChunksToUpload() {
		return this.uploadQueue.size();
	}

	public int getFreeBufferCount() {
		return this.buffersPool.getAvailableBuilderCount();
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
		if (!this.stopped) {
			this.mailbox.send(() -> {
				if (!this.stopped) {
					if (task.prioritized) {
						this.prioritizedTaskQueue.offer(task);
					} else {
						this.taskQueue.offer(task);
					}

					this.queuedTaskCount = this.prioritizedTaskQueue.size() + this.taskQueue.size();
					this.scheduleRunTasks();
				}
			});
		}
	}

	public CompletableFuture<Void> scheduleUpload(class_9801 builtBuffer, VertexBuffer glBuffer) {
		return this.stopped ? CompletableFuture.completedFuture(null) : CompletableFuture.runAsync(() -> {
			if (glBuffer.isClosed()) {
				builtBuffer.close();
			} else {
				glBuffer.bind();
				glBuffer.upload(builtBuffer);
				VertexBuffer.unbind();
			}
		}, this.uploadQueue::add);
	}

	public CompletableFuture<Void> method_60906(class_9799.class_9800 arg, VertexBuffer vertexBuffer) {
		return this.stopped ? CompletableFuture.completedFuture(null) : CompletableFuture.runAsync(() -> {
			if (vertexBuffer.isClosed()) {
				arg.close();
			} else {
				vertexBuffer.bind();
				vertexBuffer.method_60829(arg);
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
		this.stopped = true;
		this.clear();
		this.upload();
	}

	@Environment(EnvType.CLIENT)
	public class BuiltChunk {
		public static final int field_32832 = 16;
		public final int index;
		public final AtomicReference<ChunkBuilder.ChunkData> data = new AtomicReference(ChunkBuilder.ChunkData.EMPTY);
		private final AtomicInteger numFailures = new AtomicInteger(0);
		@Nullable
		private ChunkBuilder.BuiltChunk.RebuildTask rebuildTask;
		@Nullable
		private ChunkBuilder.BuiltChunk.SortTask sortTask;
		private final Set<BlockEntity> blockEntities = Sets.<BlockEntity>newHashSet();
		private final Map<RenderLayer, VertexBuffer> buffers = (Map<RenderLayer, VertexBuffer>)RenderLayer.getBlockLayers()
			.stream()
			.collect(Collectors.toMap(layer -> layer, layer -> new VertexBuffer(VertexBuffer.Usage.STATIC)));
		private Box boundingBox;
		private boolean needsRebuild = true;
		final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
		private final BlockPos.Mutable[] neighborPositions = Util.make(new BlockPos.Mutable[6], neighborPositions -> {
			for (int i = 0; i < neighborPositions.length; i++) {
				neighborPositions[i] = new BlockPos.Mutable();
			}
		});
		private boolean needsImportantRebuild;

		public BuiltChunk(final int index, final int originX, final int originY, final int originZ) {
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

		public ChunkBuilder.BuiltChunk.Task createRebuildTask(ChunkRendererRegionBuilder chunkRendererRegionBuilder) {
			boolean bl = this.cancel();
			ChunkRendererRegion chunkRendererRegion = chunkRendererRegionBuilder.build(ChunkBuilder.this.world, ChunkSectionPos.from(this.origin));
			boolean bl2 = this.data.get() == ChunkBuilder.ChunkData.EMPTY;
			if (bl2 && bl) {
				this.numFailures.incrementAndGet();
			}

			this.rebuildTask = new ChunkBuilder.BuiltChunk.RebuildTask(this.getSquaredCameraDistance(), chunkRendererRegion, !bl2 || this.numFailures.get() > 2);
			return this.rebuildTask;
		}

		public void scheduleRebuild(ChunkBuilder chunkRenderer, ChunkRendererRegionBuilder builder) {
			ChunkBuilder.BuiltChunk.Task task = this.createRebuildTask(builder);
			chunkRenderer.send(task);
		}

		void setNoCullingBlockEntities(Collection<BlockEntity> blockEntities) {
			Set<BlockEntity> set = Sets.<BlockEntity>newHashSet(blockEntities);
			Set<BlockEntity> set2;
			synchronized (this.blockEntities) {
				set2 = Sets.<BlockEntity>newHashSet(this.blockEntities);
				set.removeAll(this.blockEntities);
				set2.removeAll(blockEntities);
				this.blockEntities.clear();
				this.blockEntities.addAll(blockEntities);
			}

			ChunkBuilder.this.worldRenderer.updateNoCullingBlockEntities(set2, set);
		}

		public void rebuild(ChunkRendererRegionBuilder builder) {
			ChunkBuilder.BuiltChunk.Task task = this.createRebuildTask(builder);
			task.run(ChunkBuilder.this.buffers);
		}

		public boolean method_52841(int i, int j, int k) {
			BlockPos blockPos = this.getOrigin();
			return i == ChunkSectionPos.getSectionCoord(blockPos.getX())
				|| k == ChunkSectionPos.getSectionCoord(blockPos.getZ())
				|| j == ChunkSectionPos.getSectionCoord(blockPos.getY());
		}

		void method_60908(ChunkBuilder.ChunkData chunkData) {
			this.data.set(chunkData);
			this.numFailures.set(0);
			ChunkBuilder.this.worldRenderer.addBuiltChunk(this);
		}

		VertexSorter method_60909() {
			Vec3d vec3d = ChunkBuilder.this.getCameraPosition();
			return VertexSorter.byDistance(
				(float)(vec3d.x - (double)this.origin.getX()), (float)(vec3d.y - (double)this.origin.getY()), (float)(vec3d.z - (double)this.origin.getZ())
			);
		}

		@Environment(EnvType.CLIENT)
		class RebuildTask extends ChunkBuilder.BuiltChunk.Task {
			@Nullable
			protected ChunkRendererRegion region;

			public RebuildTask(final double distance, @Nullable final ChunkRendererRegion region, final boolean prioritized) {
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
					this.cancel();
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else {
					ChunkRendererRegion chunkRendererRegion = this.region;
					this.region = null;
					if (chunkRendererRegion == null) {
						BuiltChunk.this.method_60908(ChunkBuilder.ChunkData.field_52172);
						return CompletableFuture.completedFuture(ChunkBuilder.Result.SUCCESSFUL);
					} else {
						ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(BuiltChunk.this.origin);
						class_9810.class_9811 lv = ChunkBuilder.this.field_52171.method_60904(chunkSectionPos, chunkRendererRegion, BuiltChunk.this.method_60909(), buffers);
						BuiltChunk.this.setNoCullingBlockEntities(lv.field_52166);
						if (this.cancelled.get()) {
							lv.method_60905();
							return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
						} else {
							ChunkBuilder.ChunkData chunkData = new ChunkBuilder.ChunkData();
							chunkData.occlusionGraph = lv.field_52169;
							chunkData.blockEntities.addAll(lv.field_52167);
							chunkData.transparentSortingData = lv.field_52170;
							List<CompletableFuture<Void>> list = new ArrayList(lv.field_52168.size());
							lv.field_52168.forEach((renderLayer, buffer) -> {
								list.add(ChunkBuilder.this.scheduleUpload(buffer, BuiltChunk.this.getBuffer(renderLayer)));
								chunkData.nonEmptyLayers.add(renderLayer);
							});
							return Util.combine(list).handle((results, throwable) -> {
								if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
									MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Rendering section"));
								}

								if (this.cancelled.get()) {
									return ChunkBuilder.Result.CANCELLED;
								} else {
									BuiltChunk.this.method_60908(chunkData);
									return ChunkBuilder.Result.SUCCESSFUL;
								}
							});
						}
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

		@Environment(EnvType.CLIENT)
		class SortTask extends ChunkBuilder.BuiltChunk.Task {
			private final ChunkBuilder.ChunkData data;

			public SortTask(final double distance, final ChunkBuilder.ChunkData data) {
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
					class_9801.class_9802 lv = this.data.transparentSortingData;
					if (lv != null && !this.data.isEmpty(RenderLayer.getTranslucent())) {
						VertexSorter vertexSorter = BuiltChunk.this.method_60909();
						class_9799.class_9800 lv2 = lv.method_60824(buffers.get(RenderLayer.getTranslucent()), vertexSorter);
						if (lv2 == null) {
							return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
						} else if (this.cancelled.get()) {
							lv2.close();
							return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
						} else {
							CompletableFuture<ChunkBuilder.Result> completableFuture = ChunkBuilder.this.method_60906(lv2, BuiltChunk.this.getBuffer(RenderLayer.getTranslucent()))
								.thenApply(v -> ChunkBuilder.Result.CANCELLED);
							return completableFuture.handle((result, throwable) -> {
								if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
									MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Rendering section"));
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

			public Task(final double distance, final boolean prioritized) {
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
		public static final ChunkBuilder.ChunkData field_52172 = new ChunkBuilder.ChunkData() {
			@Override
			public boolean isVisibleThrough(Direction from, Direction to) {
				return true;
			}
		};
		final Set<RenderLayer> nonEmptyLayers = new ObjectArraySet<>(RenderLayer.getBlockLayers().size());
		final List<BlockEntity> blockEntities = Lists.<BlockEntity>newArrayList();
		ChunkOcclusionData occlusionGraph = new ChunkOcclusionData();
		@Nullable
		class_9801.class_9802 transparentSortingData;

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
