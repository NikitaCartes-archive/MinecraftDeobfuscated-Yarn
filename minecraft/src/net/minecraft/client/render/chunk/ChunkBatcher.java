package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
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
import net.minecraft.class_4587;
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
import net.minecraft.fluid.FluidState;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChunkBatcher {
	private static final Logger LOGGER = LogManager.getLogger();
	private final PriorityQueue<ChunkBatcher.ChunkRenderer.class_4577> pendingChunks = Queues.newPriorityQueue();
	private final Queue<BlockLayeredBufferBuilder> field_20827;
	private final Queue<Runnable> pendingUploads = Queues.<Runnable>newConcurrentLinkedQueue();
	private volatile int field_20992;
	private volatile int field_20993;
	private final BlockLayeredBufferBuilder field_20828;
	private final MailboxProcessor<Runnable> field_20829;
	private final Executor field_20830;
	private World field_20831;
	private final WorldRenderer field_20832;
	private Vec3d cameraPosition = Vec3d.ZERO;

	public ChunkBatcher(World world, WorldRenderer worldRenderer, Executor executor, boolean bl, BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
		this.field_20831 = world;
		this.field_20832 = worldRenderer;
		int i = Math.max(
			1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / (BlockRenderLayer.method_22720().stream().mapToInt(BlockRenderLayer::method_22722).sum() * 4) - 1
		);
		int j = Runtime.getRuntime().availableProcessors();
		int k = bl ? j : Math.min(j, 4);
		int l = Math.max(1, Math.min(k, i));
		this.field_20828 = blockLayeredBufferBuilder;
		List<BlockLayeredBufferBuilder> list = Lists.<BlockLayeredBufferBuilder>newArrayListWithExpectedSize(l);

		try {
			for (int m = 0; m < l; m++) {
				list.add(new BlockLayeredBufferBuilder());
			}
		} catch (OutOfMemoryError var14) {
			LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
			int n = Math.min(list.size() * 2 / 3, list.size() - 1);

			for (int o = 0; o < n; o++) {
				list.remove(list.size() - 1);
			}

			System.gc();
		}

		this.field_20827 = Queues.<BlockLayeredBufferBuilder>newArrayDeque(list);
		this.field_20993 = this.field_20827.size();
		this.field_20830 = executor;
		this.field_20829 = MailboxProcessor.create(executor, "Chunk Renderer");
		this.field_20829.send(this::method_22763);
	}

	public void method_22752(World world) {
		this.field_20831 = world;
	}

	private void method_22763() {
		if (!this.field_20827.isEmpty()) {
			ChunkBatcher.ChunkRenderer.class_4577 lv = (ChunkBatcher.ChunkRenderer.class_4577)this.pendingChunks.poll();
			if (lv != null) {
				BlockLayeredBufferBuilder blockLayeredBufferBuilder = (BlockLayeredBufferBuilder)this.field_20827.poll();
				this.field_20992 = this.pendingChunks.size();
				this.field_20993 = this.field_20827.size();
				CompletableFuture.runAsync(() -> {
				}, this.field_20830).thenCompose(void_ -> lv.method_22783(blockLayeredBufferBuilder)).whenComplete((unit, throwable) -> {
					this.field_20829.send(() -> {
						blockLayeredBufferBuilder.method_22705();
						this.field_20827.add(blockLayeredBufferBuilder);
						this.field_20993 = this.field_20827.size();
						this.method_22763();
					});
					if (throwable != null) {
						CrashReport crashReport = CrashReport.create(throwable, "Batching chunks");
						MinecraftClient.getInstance().setCrashReport(MinecraftClient.getInstance().populateCrashReport(crashReport));
					}
				});
			}
		}
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
		boolean bl;
		Runnable runnable;
		for (bl = false; (runnable = (Runnable)this.pendingUploads.poll()) != null; bl = true) {
			runnable.run();
		}

		return bl;
	}

	public void rebuildSync(ChunkBatcher.ChunkRenderer chunkRenderer) {
		chunkRenderer.method_22781();
	}

	public void reset() {
		this.clear();
	}

	public void method_22756(ChunkBatcher.ChunkRenderer.class_4577 arg) {
		this.field_20829.send(() -> {
			this.pendingChunks.offer(arg);
			this.field_20992 = this.pendingChunks.size();
			this.method_22763();
		});
	}

	public CompletableFuture<Void> upload(BufferBuilder bufferBuilder, GlBuffer glBuffer) {
		return CompletableFuture.runAsync(() -> {
		}, this.pendingUploads::add).thenCompose(void_ -> this.method_22759(bufferBuilder, glBuffer));
	}

	private CompletableFuture<Void> method_22759(BufferBuilder bufferBuilder, GlBuffer glBuffer) {
		return glBuffer.method_22643(bufferBuilder);
	}

	private void clear() {
		while (!this.pendingChunks.isEmpty()) {
			ChunkBatcher.ChunkRenderer.class_4577 lv = (ChunkBatcher.ChunkRenderer.class_4577)this.pendingChunks.poll();
			if (lv != null) {
				lv.method_22782();
			}
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

	@Environment(EnvType.CLIENT)
	public static class ChunkRenderData {
		public static final ChunkBatcher.ChunkRenderData EMPTY = new ChunkBatcher.ChunkRenderData() {
			@Override
			public boolean isVisibleThrough(Direction direction, Direction direction2) {
				return false;
			}
		};
		private final Set<BlockRenderLayer> nonEmpty = Sets.<BlockRenderLayer>newHashSet();
		private final Set<BlockRenderLayer> initialized = Sets.<BlockRenderLayer>newHashSet();
		private boolean empty = true;
		private final List<BlockEntity> blockEntities = Lists.<BlockEntity>newArrayList();
		private ChunkOcclusionGraph occlusionGraph = new ChunkOcclusionGraph();
		@Nullable
		private BufferBuilder.State bufferState;

		public boolean isEmpty() {
			return this.empty;
		}

		public boolean isEmpty(BlockRenderLayer blockRenderLayer) {
			return !this.nonEmpty.contains(blockRenderLayer);
		}

		public List<BlockEntity> getBlockEntities() {
			return this.blockEntities;
		}

		public boolean isVisibleThrough(Direction direction, Direction direction2) {
			return this.occlusionGraph.isVisibleThrough(direction, direction2);
		}
	}

	@Environment(EnvType.CLIENT)
	public class ChunkRenderer {
		public final AtomicReference<ChunkBatcher.ChunkRenderData> data = new AtomicReference(ChunkBatcher.ChunkRenderData.EMPTY);
		@Nullable
		private ChunkBatcher.ChunkRenderer.class_4578 field_20834;
		@Nullable
		private ChunkBatcher.ChunkRenderer.class_4579 task;
		private final Set<BlockEntity> blockEntities = Sets.<BlockEntity>newHashSet();
		private final Map<BlockRenderLayer, GlBuffer> buffers = (Map<BlockRenderLayer, GlBuffer>)BlockRenderLayer.method_22720()
			.stream()
			.collect(Collectors.toMap(blockRenderLayer -> blockRenderLayer, blockRenderLayer -> new GlBuffer(VertexFormats.POSITION_COLOR_UV_NORMAL)));
		public Box boundingBox;
		private int field_4471 = -1;
		private boolean rebuildScheduled = true;
		private final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
		private final BlockPos.Mutable[] neighborPositions = SystemUtil.consume(new BlockPos.Mutable[6], mutables -> {
			for (int i = 0; i < mutables.length; i++) {
				mutables[i] = new BlockPos.Mutable();
			}
		});
		private boolean rebuildOnClientThread;

		private boolean isChunkNonEmpty(BlockPos blockPos) {
			return !ChunkBatcher.this.field_20831.method_8497(blockPos.getX() >> 4, blockPos.getZ() >> 4).isEmpty();
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

		public boolean method_3671(int i) {
			if (this.field_4471 == i) {
				return false;
			} else {
				this.field_4471 = i;
				return true;
			}
		}

		public GlBuffer getGlBuffer(BlockRenderLayer blockRenderLayer) {
			return (GlBuffer)this.buffers.get(blockRenderLayer);
		}

		public void setOrigin(int i, int j, int k) {
			if (i != this.origin.getX() || j != this.origin.getY() || k != this.origin.getZ()) {
				this.clear();
				this.origin.set(i, j, k);
				this.boundingBox = new Box((double)i, (double)j, (double)k, (double)(i + 16), (double)(j + 16), (double)(k + 16));

				for (Direction direction : Direction.values()) {
					this.neighborPositions[direction.ordinal()].set(this.origin).setOffset(direction, 16);
				}
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

		public ChunkBatcher.ChunkRenderData getData() {
			return (ChunkBatcher.ChunkRenderData)this.data.get();
		}

		private void clear() {
			this.cancel();
			this.data.set(ChunkBatcher.ChunkRenderData.EMPTY);
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

		public boolean method_22773(BlockRenderLayer blockRenderLayer, ChunkBatcher chunkBatcher) {
			ChunkBatcher.ChunkRenderData chunkRenderData = this.getData();
			if (this.task != null) {
				this.task.method_22782();
			}

			if (!chunkRenderData.initialized.contains(blockRenderLayer)) {
				return false;
			} else {
				this.task = new ChunkBatcher.ChunkRenderer.class_4579(this.getSquaredCameraDistance(), chunkRenderData);
				chunkBatcher.method_22756(this.task);
				return true;
			}
		}

		protected void cancel() {
			if (this.field_20834 != null) {
				this.field_20834.method_22782();
				this.field_20834 = null;
			}

			if (this.task != null) {
				this.task.method_22782();
				this.task = null;
			}
		}

		public ChunkBatcher.ChunkRenderer.class_4577 startRebuild() {
			this.cancel();
			BlockPos blockPos = this.origin.toImmutable();
			int i = 1;
			ChunkRendererRegion chunkRendererRegion = ChunkRendererRegion.create(ChunkBatcher.this.field_20831, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
			this.field_20834 = new ChunkBatcher.ChunkRenderer.class_4578(this.getSquaredCameraDistance(), chunkRendererRegion);
			return this.field_20834;
		}

		public void method_22777(ChunkBatcher chunkBatcher) {
			ChunkBatcher.ChunkRenderer.class_4577 lv = this.startRebuild();
			chunkBatcher.method_22756(lv);
		}

		private void method_22778(Set<BlockEntity> set) {
			Set<BlockEntity> set2 = Sets.<BlockEntity>newHashSet(set);
			Set<BlockEntity> set3 = Sets.<BlockEntity>newHashSet(this.blockEntities);
			set2.removeAll(this.blockEntities);
			set3.removeAll(set);
			this.blockEntities.clear();
			this.blockEntities.addAll(set);
			ChunkBatcher.this.field_20832.updateBlockEntities(set3, set2);
		}

		public void method_22781() {
			ChunkBatcher.ChunkRenderer.class_4577 lv = this.startRebuild();
			lv.method_22783(ChunkBatcher.this.field_20828);
		}

		@Environment(EnvType.CLIENT)
		abstract class class_4577 implements Comparable<ChunkBatcher.ChunkRenderer.class_4577> {
			protected final double field_20835;
			protected final AtomicBoolean field_20836 = new AtomicBoolean(false);

			public class_4577(double d) {
				this.field_20835 = d;
			}

			public abstract CompletableFuture<Unit> method_22783(BlockLayeredBufferBuilder blockLayeredBufferBuilder);

			public abstract void method_22782();

			public int method_22784(ChunkBatcher.ChunkRenderer.class_4577 arg) {
				return Doubles.compare(this.field_20835, arg.field_20835);
			}
		}

		@Environment(EnvType.CLIENT)
		class class_4578 extends ChunkBatcher.ChunkRenderer.class_4577 {
			@Nullable
			protected ChunkRendererRegion field_20838;

			public class_4578(double d, @Nullable ChunkRendererRegion chunkRendererRegion) {
				super(d);
				this.field_20838 = chunkRendererRegion;
			}

			@Override
			public CompletableFuture<Unit> method_22783(BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
				if (this.field_20836.get()) {
					return CompletableFuture.completedFuture(Unit.INSTANCE);
				} else if (!ChunkRenderer.this.shouldBuild()) {
					this.field_20838 = null;
					ChunkRenderer.this.scheduleRebuild(false);
					this.field_20836.set(true);
					return CompletableFuture.completedFuture(Unit.INSTANCE);
				} else if (this.field_20836.get()) {
					return CompletableFuture.completedFuture(Unit.INSTANCE);
				} else {
					Vec3d vec3d = ChunkBatcher.this.getCameraPosition();
					float f = (float)vec3d.x;
					float g = (float)vec3d.y;
					float h = (float)vec3d.z;
					ChunkBatcher.ChunkRenderData chunkRenderData = new ChunkBatcher.ChunkRenderData();
					Set<BlockEntity> set = this.method_22785(f, g, h, chunkRenderData, blockLayeredBufferBuilder);
					ChunkRenderer.this.method_22778(set);
					if (this.field_20836.get()) {
						return CompletableFuture.completedFuture(Unit.INSTANCE);
					} else {
						List<CompletableFuture<Void>> list = Lists.<CompletableFuture<Void>>newArrayList();
						chunkRenderData.initialized
							.forEach(
								blockRenderLayer -> list.add(
										ChunkBatcher.this.upload(blockLayeredBufferBuilder.get(blockRenderLayer), ChunkRenderer.this.getGlBuffer(blockRenderLayer))
									)
							);
						CompletableFuture<Unit> completableFuture = SystemUtil.thenCombine(list).thenApply(listx -> Unit.INSTANCE);
						return completableFuture.whenComplete((unit, throwable) -> {
							if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
								MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
							}

							if (!this.field_20836.get()) {
								ChunkRenderer.this.data.set(chunkRenderData);
							}
						});
					}
				}
			}

			private Set<BlockEntity> method_22785(
				float f, float g, float h, ChunkBatcher.ChunkRenderData chunkRenderData, BlockLayeredBufferBuilder blockLayeredBufferBuilder
			) {
				int i = 1;
				BlockPos blockPos = ChunkRenderer.this.origin.toImmutable();
				BlockPos blockPos2 = blockPos.add(15, 15, 15);
				ChunkOcclusionGraphBuilder chunkOcclusionGraphBuilder = new ChunkOcclusionGraphBuilder();
				Set<BlockEntity> set = Sets.<BlockEntity>newHashSet();
				ChunkRendererRegion chunkRendererRegion = this.field_20838;
				this.field_20838 = null;
				class_4587 lv = new class_4587();
				if (chunkRendererRegion != null) {
					BlockModelRenderer.enableBrightnessCache();
					Random random = new Random();
					BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();

					for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
						BlockState blockState = chunkRendererRegion.getBlockState(blockPos3);
						Block block = blockState.getBlock();
						if (blockState.isFullOpaque(chunkRendererRegion, blockPos3)) {
							chunkOcclusionGraphBuilder.markClosed(blockPos3);
						}

						if (block.hasBlockEntity()) {
							BlockEntity blockEntity = chunkRendererRegion.getBlockEntity(blockPos3, WorldChunk.CreationType.CHECK);
							if (blockEntity != null) {
								this.method_23087(chunkRenderData, set, blockEntity);
							}
						}

						FluidState fluidState = chunkRendererRegion.getFluidState(blockPos3);
						if (!fluidState.isEmpty()) {
							BlockRenderLayer blockRenderLayer = BlockRenderLayer.method_22716(fluidState);
							BufferBuilder bufferBuilder = blockLayeredBufferBuilder.get(blockRenderLayer);
							if (chunkRenderData.initialized.add(blockRenderLayer)) {
								ChunkRenderer.this.beginBufferBuilding(bufferBuilder);
							}

							if (blockRenderManager.tesselateFluid(blockPos3, chunkRendererRegion, bufferBuilder, fluidState)) {
								chunkRenderData.empty = false;
								chunkRenderData.nonEmpty.add(blockRenderLayer);
							}
						}

						if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
							BlockRenderLayer blockRenderLayerx = BlockRenderLayer.method_22715(blockState);
							BufferBuilder bufferBuilderx = blockLayeredBufferBuilder.get(blockRenderLayerx);
							if (chunkRenderData.initialized.add(blockRenderLayerx)) {
								ChunkRenderer.this.beginBufferBuilding(bufferBuilderx);
							}

							if (blockRenderManager.tesselateBlock(blockState, blockPos3, chunkRendererRegion, lv, bufferBuilderx, true, random)) {
								chunkRenderData.empty = false;
								chunkRenderData.nonEmpty.add(blockRenderLayerx);
							}
						}
					}

					if (chunkRenderData.nonEmpty.contains(BlockRenderLayer.TRANSLUCENT)) {
						BufferBuilder bufferBuilder2 = blockLayeredBufferBuilder.get(BlockRenderLayer.TRANSLUCENT);
						bufferBuilder2.sortQuads(f - (float)blockPos.getX(), g - (float)blockPos.getY(), h - (float)blockPos.getZ());
						chunkRenderData.bufferState = bufferBuilder2.toBufferState();
					}

					chunkRenderData.initialized.stream().map(blockLayeredBufferBuilder::get).forEach(BufferBuilder::end);
					BlockModelRenderer.disableBrightnessCache();
				}

				chunkRenderData.occlusionGraph = chunkOcclusionGraphBuilder.build();
				return set;
			}

			private <E extends BlockEntity> void method_23087(ChunkBatcher.ChunkRenderData chunkRenderData, Set<BlockEntity> set, E blockEntity) {
				BlockEntityRenderer<E> blockEntityRenderer = BlockEntityRenderDispatcher.INSTANCE.get(blockEntity);
				if (blockEntityRenderer != null) {
					chunkRenderData.blockEntities.add(blockEntity);
					if (blockEntityRenderer.method_3563(blockEntity)) {
						set.add(blockEntity);
					}
				}
			}

			@Override
			public void method_22782() {
				this.field_20838 = null;
				if (this.field_20836.compareAndSet(false, true)) {
					ChunkRenderer.this.scheduleRebuild(false);
				}
			}
		}

		@Environment(EnvType.CLIENT)
		class class_4579 extends ChunkBatcher.ChunkRenderer.class_4577 {
			private final ChunkBatcher.ChunkRenderData field_20841;

			public class_4579(double d, ChunkBatcher.ChunkRenderData chunkRenderData) {
				super(d);
				this.field_20841 = chunkRenderData;
			}

			@Override
			public CompletableFuture<Unit> method_22783(BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
				if (this.field_20836.get()) {
					return CompletableFuture.completedFuture(Unit.INSTANCE);
				} else if (!ChunkRenderer.this.shouldBuild()) {
					this.field_20836.set(true);
					return CompletableFuture.completedFuture(Unit.INSTANCE);
				} else if (this.field_20836.get()) {
					return CompletableFuture.completedFuture(Unit.INSTANCE);
				} else {
					Vec3d vec3d = ChunkBatcher.this.getCameraPosition();
					float f = (float)vec3d.x;
					float g = (float)vec3d.y;
					float h = (float)vec3d.z;
					BufferBuilder.State state = this.field_20841.bufferState;
					if (state != null && this.field_20841.nonEmpty.contains(BlockRenderLayer.TRANSLUCENT)) {
						BufferBuilder bufferBuilder = blockLayeredBufferBuilder.get(BlockRenderLayer.TRANSLUCENT);
						ChunkRenderer.this.beginBufferBuilding(bufferBuilder);
						bufferBuilder.restoreState(state);
						bufferBuilder.sortQuads(
							f - (float)ChunkRenderer.this.origin.getX(), g - (float)ChunkRenderer.this.origin.getY(), h - (float)ChunkRenderer.this.origin.getZ()
						);
						this.field_20841.bufferState = bufferBuilder.toBufferState();
						bufferBuilder.end();
						if (this.field_20836.get()) {
							return CompletableFuture.completedFuture(Unit.INSTANCE);
						} else {
							CompletableFuture<Unit> completableFuture = ChunkBatcher.this.upload(
									blockLayeredBufferBuilder.get(BlockRenderLayer.TRANSLUCENT), ChunkRenderer.this.getGlBuffer(BlockRenderLayer.TRANSLUCENT)
								)
								.thenApply(void_ -> Unit.INSTANCE);
							return completableFuture.whenComplete((unit, throwable) -> {
								if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
									MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
								}
							});
						}
					} else {
						return CompletableFuture.completedFuture(Unit.INSTANCE);
					}
				}
			}

			@Override
			public void method_22782() {
				this.field_20836.set(true);
			}
		}
	}
}
