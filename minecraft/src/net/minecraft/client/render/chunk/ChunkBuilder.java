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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.BufferAllocator;
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
	private final ChunkRenderTaskScheduler scheduler = new ChunkRenderTaskScheduler();
	private final Queue<Runnable> uploadQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	final BlockBufferAllocatorStorage buffers;
	private final BlockBufferBuilderPool buffersPool;
	private volatile int queuedTaskCount;
	private volatile boolean stopped;
	private final TaskExecutor<Runnable> mailbox;
	private final Executor executor;
	ClientWorld world;
	final WorldRenderer worldRenderer;
	private Vec3d cameraPosition = Vec3d.ZERO;
	final SectionBuilder sectionBuilder;

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
		this.sectionBuilder = new SectionBuilder(blockRenderManager, blockEntityRenderDispatcher);
	}

	public void setWorld(ClientWorld world) {
		this.world = world;
	}

	private void scheduleRunTasks() {
		if (!this.stopped && !this.buffersPool.hasNoAvailableBuilder()) {
			ChunkBuilder.BuiltChunk.Task task = this.scheduler.dequeueNearest(this.getCameraPosition());
			if (task != null) {
				BlockBufferAllocatorStorage blockBufferAllocatorStorage = (BlockBufferAllocatorStorage)Objects.requireNonNull(this.buffersPool.acquire());
				this.queuedTaskCount = this.scheduler.size();
				CompletableFuture.supplyAsync(Util.debugSupplier(task.getName(), () -> task.run(blockBufferAllocatorStorage)), this.executor)
					.thenCompose(future -> future)
					.whenComplete((result, throwable) -> {
						if (throwable != null) {
							MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Batching sections"));
						} else {
							this.mailbox.send(() -> {
								if (result == ChunkBuilder.Result.SUCCESSFUL) {
									blockBufferAllocatorStorage.clear();
								} else {
									blockBufferAllocatorStorage.reset();
								}

								this.buffersPool.release(blockBufferAllocatorStorage);
								this.scheduleRunTasks();
							});
						}
					});
			}
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
					this.scheduler.enqueue(task);
					this.queuedTaskCount = this.scheduler.size();
					this.scheduleRunTasks();
				}
			});
		}
	}

	public CompletableFuture<Void> scheduleUpload(BuiltBuffer builtBuffer, VertexBuffer glBuffer) {
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

	public CompletableFuture<Void> scheduleIndexBufferUpload(BufferAllocator.CloseableBuffer indexBuffer, VertexBuffer vertexBuffer) {
		return this.stopped ? CompletableFuture.completedFuture(null) : CompletableFuture.runAsync(() -> {
			if (vertexBuffer.isClosed()) {
				indexBuffer.close();
			} else {
				vertexBuffer.bind();
				vertexBuffer.uploadIndexBuffer(indexBuffer);
				VertexBuffer.unbind();
			}
		}, this.uploadQueue::add);
	}

	private void clear() {
		this.scheduler.cancelAll();
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
		private long sectionPos = ChunkSectionPos.asLong(-1, -1, -1);
		final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
		private boolean needsImportantRebuild;

		public BuiltChunk(final int index, final long sectionPos) {
			this.index = index;
			this.setSectionPos(sectionPos);
		}

		private boolean isChunkNonEmpty(long sectionPos) {
			return ChunkBuilder.this.world.getChunk(ChunkSectionPos.unpackX(sectionPos), ChunkSectionPos.unpackZ(sectionPos), ChunkStatus.FULL, false) != null;
		}

		public boolean shouldBuild() {
			int i = 24;
			return !(this.getSquaredCameraDistance() > 576.0)
				? true
				: this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.WEST))
					&& this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.NORTH))
					&& this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.EAST))
					&& this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.SOUTH));
		}

		public Box getBoundingBox() {
			return this.boundingBox;
		}

		public VertexBuffer getBuffer(RenderLayer layer) {
			return (VertexBuffer)this.buffers.get(layer);
		}

		public void setSectionPos(long sectionPos) {
			this.clear();
			this.sectionPos = sectionPos;
			int i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackX(sectionPos));
			int j = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(sectionPos));
			int k = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackZ(sectionPos));
			this.origin.set(i, j, k);
			this.boundingBox = new Box((double)i, (double)j, (double)k, (double)(i + 16), (double)(j + 16), (double)(k + 16));
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

		public long getSectionPos() {
			return this.sectionPos;
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

		public long getOffsetSectionPos(Direction direction) {
			return ChunkSectionPos.offset(this.sectionPos, direction);
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

		public ChunkBuilder.BuiltChunk.Task createRebuildTask(ChunkRendererRegionBuilder builder) {
			this.cancel();
			ChunkRendererRegion chunkRendererRegion = builder.build(ChunkBuilder.this.world, ChunkSectionPos.from(this.sectionPos));
			boolean bl = this.data.get() != ChunkBuilder.ChunkData.EMPTY;
			this.rebuildTask = new ChunkBuilder.BuiltChunk.RebuildTask(this.getSquaredCameraDistance(), chunkRendererRegion, bl);
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

		public boolean isAtPos(int sectionX, int sectionY, int sectionZ) {
			return sectionX == ChunkSectionPos.unpackX(this.sectionPos)
				|| sectionZ == ChunkSectionPos.unpackZ(this.sectionPos)
				|| sectionY == ChunkSectionPos.unpackY(this.sectionPos);
		}

		void setData(ChunkBuilder.ChunkData chunkData) {
			this.data.set(chunkData);
			ChunkBuilder.this.worldRenderer.addBuiltChunk(this);
		}

		VertexSorter getVertexSorter() {
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
			public CompletableFuture<ChunkBuilder.Result> run(BlockBufferAllocatorStorage buffers) {
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
						BuiltChunk.this.setData(ChunkBuilder.ChunkData.field_52172);
						return CompletableFuture.completedFuture(ChunkBuilder.Result.SUCCESSFUL);
					} else {
						ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(BuiltChunk.this.origin);
						SectionBuilder.RenderData renderData = ChunkBuilder.this.sectionBuilder
							.build(chunkSectionPos, chunkRendererRegion, BuiltChunk.this.getVertexSorter(), buffers);
						BuiltChunk.this.setNoCullingBlockEntities(renderData.noCullingBlockEntities);
						if (this.cancelled.get()) {
							renderData.close();
							return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
						} else {
							ChunkBuilder.ChunkData chunkData = new ChunkBuilder.ChunkData();
							chunkData.occlusionGraph = renderData.chunkOcclusionData;
							chunkData.blockEntities.addAll(renderData.blockEntities);
							chunkData.transparentSortingData = renderData.translucencySortingData;
							List<CompletableFuture<Void>> list = new ArrayList(renderData.buffers.size());
							renderData.buffers.forEach((renderLayer, buffer) -> {
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
									BuiltChunk.this.setData(chunkData);
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
			public CompletableFuture<ChunkBuilder.Result> run(BlockBufferAllocatorStorage buffers) {
				if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else if (!BuiltChunk.this.shouldBuild()) {
					this.cancelled.set(true);
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
				} else {
					BuiltBuffer.SortState sortState = this.data.transparentSortingData;
					if (sortState != null && !this.data.isEmpty(RenderLayer.getTranslucent())) {
						VertexSorter vertexSorter = BuiltChunk.this.getVertexSorter();
						BufferAllocator.CloseableBuffer closeableBuffer = sortState.sortAndStore(buffers.get(RenderLayer.getTranslucent()), vertexSorter);
						if (closeableBuffer == null) {
							return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
						} else if (this.cancelled.get()) {
							closeableBuffer.close();
							return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
						} else {
							CompletableFuture<ChunkBuilder.Result> completableFuture = ChunkBuilder.this.scheduleIndexBufferUpload(
									closeableBuffer, BuiltChunk.this.getBuffer(RenderLayer.getTranslucent())
								)
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
		public abstract class Task implements Comparable<ChunkBuilder.BuiltChunk.Task> {
			protected final double distance;
			protected final AtomicBoolean cancelled = new AtomicBoolean(false);
			protected final boolean prioritized;

			public Task(final double distance, final boolean prioritized) {
				this.distance = distance;
				this.prioritized = prioritized;
			}

			public abstract CompletableFuture<ChunkBuilder.Result> run(BlockBufferAllocatorStorage buffers);

			public abstract void cancel();

			protected abstract String getName();

			public boolean isPrioritized() {
				return this.prioritized;
			}

			public BlockPos getOrigin() {
				return BuiltChunk.this.origin;
			}

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
		BuiltBuffer.SortState transparentSortingData;

		public boolean hasNonEmptyLayers() {
			return !this.nonEmptyLayers.isEmpty();
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
