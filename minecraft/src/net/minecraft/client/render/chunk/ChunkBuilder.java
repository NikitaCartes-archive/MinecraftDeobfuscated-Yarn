package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
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
							CrashReport crashReport = CrashReport.create(throwable, "Batching chunks");
							MinecraftClient.getInstance().setCrashReportSupplier(() -> MinecraftClient.getInstance().addDetailsToCrashReport(crashReport));
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
		return String.format("pC: %03d, pU: %02d, aB: %02d", this.queuedTaskCount, this.uploadQueue.size(), this.bufferCount);
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

	public CompletableFuture<Void> scheduleUpload(BufferBuilder buffer, VertexBuffer glBuffer) {
		return CompletableFuture.runAsync(() -> {
		}, this.uploadQueue::add).thenCompose(void_ -> this.upload(buffer, glBuffer));
	}

	private CompletableFuture<Void> upload(BufferBuilder buffer, VertexBuffer glBuffer) {
		return glBuffer.submitUpload(buffer);
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
			.collect(Collectors.toMap(renderLayer -> renderLayer, renderLayer -> new VertexBuffer()));
		private Box boundingBox;
		private boolean needsRebuild = true;
		final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
		private final BlockPos.Mutable[] neighborPositions = Util.make(new BlockPos.Mutable[6], mutables -> {
			for (int i = 0; i < mutables.length; i++) {
				mutables[i] = new BlockPos.Mutable();
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

			if (!chunkData.initializedLayers.contains(layer)) {
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

		void setNoCullingBlockEntities(Set<BlockEntity> noCullingBlockEntities) {
			Set<BlockEntity> set = Sets.<BlockEntity>newHashSet(noCullingBlockEntities);
			Set<BlockEntity> set2;
			synchronized (this.blockEntities) {
				set2 = Sets.<BlockEntity>newHashSet(this.blockEntities);
				set.removeAll(this.blockEntities);
				set2.removeAll(noCullingBlockEntities);
				this.blockEntities.clear();
				this.blockEntities.addAll(noCullingBlockEntities);
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
					ChunkBuilder.ChunkData chunkData = new ChunkBuilder.ChunkData();
					Set<BlockEntity> set = this.render(f, g, h, chunkData, buffers);
					BuiltChunk.this.setNoCullingBlockEntities(set);
					if (this.cancelled.get()) {
						return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
					} else {
						List<CompletableFuture<Void>> list = Lists.<CompletableFuture<Void>>newArrayList();
						chunkData.initializedLayers
							.forEach(renderLayer -> list.add(ChunkBuilder.this.scheduleUpload(buffers.get(renderLayer), BuiltChunk.this.getBuffer(renderLayer))));
						return Util.combine(list).handle((results, throwable) -> {
							if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
								CrashReport crashReport = CrashReport.create(throwable, "Rendering chunk");
								MinecraftClient.getInstance().setCrashReportSupplier(() -> crashReport);
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

			private Set<BlockEntity> render(float cameraX, float cameraY, float cameraZ, ChunkBuilder.ChunkData data, BlockBufferBuilderStorage buffers) {
				int i = 1;
				BlockPos blockPos = BuiltChunk.this.origin.toImmutable();
				BlockPos blockPos2 = blockPos.add(15, 15, 15);
				ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
				Set<BlockEntity> set = Sets.<BlockEntity>newHashSet();
				ChunkRendererRegion chunkRendererRegion = this.region;
				this.region = null;
				MatrixStack matrixStack = new MatrixStack();
				if (chunkRendererRegion != null) {
					BlockModelRenderer.enableBrightnessCache();
					Random random = new Random();
					BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();

					for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
						BlockState blockState = chunkRendererRegion.getBlockState(blockPos3);
						if (blockState.isOpaqueFullCube(chunkRendererRegion, blockPos3)) {
							chunkOcclusionDataBuilder.markClosed(blockPos3);
						}

						if (blockState.hasBlockEntity()) {
							BlockEntity blockEntity = chunkRendererRegion.getBlockEntity(blockPos3);
							if (blockEntity != null) {
								this.addBlockEntity(data, set, blockEntity);
							}
						}

						BlockState blockState2 = chunkRendererRegion.getBlockState(blockPos3);
						FluidState fluidState = blockState2.getFluidState();
						if (!fluidState.isEmpty()) {
							RenderLayer renderLayer = RenderLayers.getFluidLayer(fluidState);
							BufferBuilder bufferBuilder = buffers.get(renderLayer);
							if (data.initializedLayers.add(renderLayer)) {
								BuiltChunk.this.beginBufferBuilding(bufferBuilder);
							}

							if (blockRenderManager.renderFluid(blockPos3, chunkRendererRegion, bufferBuilder, blockState2, fluidState)) {
								data.empty = false;
								data.nonEmptyLayers.add(renderLayer);
							}
						}

						if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
							RenderLayer renderLayerx = RenderLayers.getBlockLayer(blockState);
							BufferBuilder bufferBuilderx = buffers.get(renderLayerx);
							if (data.initializedLayers.add(renderLayerx)) {
								BuiltChunk.this.beginBufferBuilding(bufferBuilderx);
							}

							matrixStack.push();
							matrixStack.translate((double)(blockPos3.getX() & 15), (double)(blockPos3.getY() & 15), (double)(blockPos3.getZ() & 15));
							if (blockRenderManager.renderBlock(blockState, blockPos3, chunkRendererRegion, matrixStack, bufferBuilderx, true, random)) {
								data.empty = false;
								data.nonEmptyLayers.add(renderLayerx);
							}

							matrixStack.pop();
						}
					}

					if (data.nonEmptyLayers.contains(RenderLayer.getTranslucent())) {
						BufferBuilder bufferBuilder2 = buffers.get(RenderLayer.getTranslucent());
						bufferBuilder2.setCameraPosition(cameraX - (float)blockPos.getX(), cameraY - (float)blockPos.getY(), cameraZ - (float)blockPos.getZ());
						data.bufferState = bufferBuilder2.popState();
					}

					data.initializedLayers.stream().map(buffers::get).forEach(BufferBuilder::end);
					BlockModelRenderer.disableBrightnessCache();
				}

				data.occlusionGraph = chunkOcclusionDataBuilder.build();
				return set;
			}

			private <E extends BlockEntity> void addBlockEntity(ChunkBuilder.ChunkData data, Set<BlockEntity> blockEntities, E blockEntity) {
				BlockEntityRenderer<E> blockEntityRenderer = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(blockEntity);
				if (blockEntityRenderer != null) {
					data.blockEntities.add(blockEntity);
					if (blockEntityRenderer.rendersOutsideBoundingBox(blockEntity)) {
						blockEntities.add(blockEntity);
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
					BufferBuilder.State state = this.data.bufferState;
					if (state != null && this.data.nonEmptyLayers.contains(RenderLayer.getTranslucent())) {
						BufferBuilder bufferBuilder = buffers.get(RenderLayer.getTranslucent());
						BuiltChunk.this.beginBufferBuilding(bufferBuilder);
						bufferBuilder.restoreState(state);
						bufferBuilder.setCameraPosition(
							f - (float)BuiltChunk.this.origin.getX(), g - (float)BuiltChunk.this.origin.getY(), h - (float)BuiltChunk.this.origin.getZ()
						);
						this.data.bufferState = bufferBuilder.popState();
						bufferBuilder.end();
						if (this.cancelled.get()) {
							return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
						} else {
							CompletableFuture<ChunkBuilder.Result> completableFuture = ChunkBuilder.this.scheduleUpload(
									buffers.get(RenderLayer.getTranslucent()), BuiltChunk.this.getBuffer(RenderLayer.getTranslucent())
								)
								.thenApply(void_ -> ChunkBuilder.Result.CANCELLED);
							return completableFuture.handle((result, throwable) -> {
								if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
									CrashReport crashReport = CrashReport.create(throwable, "Rendering chunk");
									MinecraftClient.getInstance().setCrashReportSupplier(() -> crashReport);
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
		final Set<RenderLayer> nonEmptyLayers = new ObjectArraySet<>();
		final Set<RenderLayer> initializedLayers = new ObjectArraySet<>();
		boolean empty = true;
		final List<BlockEntity> blockEntities = Lists.<BlockEntity>newArrayList();
		ChunkOcclusionData occlusionGraph = new ChunkOcclusionData();
		@Nullable
		BufferBuilder.State bufferState;

		public boolean isEmpty() {
			return this.empty;
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
