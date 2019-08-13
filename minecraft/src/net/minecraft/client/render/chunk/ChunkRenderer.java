package net.minecraft.client.render.chunk;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
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
import net.minecraft.fluid.FluidState;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
public class ChunkRenderer {
	private volatile World world;
	private final WorldRenderer renderer;
	public static int chunkUpdateCount;
	public ChunkRenderData data = ChunkRenderData.EMPTY;
	private final ReentrantLock lock = new ReentrantLock();
	private final ReentrantLock dataLock = new ReentrantLock();
	private ChunkRenderTask task;
	private final Set<BlockEntity> blockEntities = Sets.<BlockEntity>newHashSet();
	private final GlBuffer[] buffers = new GlBuffer[BlockRenderLayer.values().length];
	public Box boundingBox;
	private int field_4471 = -1;
	private boolean rebuildScheduled = true;
	private final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
	private final BlockPos.Mutable[] neighborPositions = SystemUtil.consume(new BlockPos.Mutable[6], mutables -> {
		for (int ix = 0; ix < mutables.length; ix++) {
			mutables[ix] = new BlockPos.Mutable();
		}
	});
	private boolean rebuildOnClientThread;

	public ChunkRenderer(World world, WorldRenderer worldRenderer) {
		this.world = world;
		this.renderer = worldRenderer;
		if (GLX.useVbo()) {
			for (int i = 0; i < BlockRenderLayer.values().length; i++) {
				this.buffers[i] = new GlBuffer(VertexFormats.POSITION_COLOR_UV_LMAP);
			}
		}
	}

	private static boolean isChunkNonEmpty(BlockPos blockPos, World world) {
		return !world.method_8497(blockPos.getX() >> 4, blockPos.getZ() >> 4).isEmpty();
	}

	public boolean shouldBuild() {
		int i = 24;
		if (!(this.getSquaredCameraDistance() > 576.0)) {
			return true;
		} else {
			World world = this.getWorld();
			return isChunkNonEmpty(this.neighborPositions[Direction.field_11039.ordinal()], world)
				&& isChunkNonEmpty(this.neighborPositions[Direction.field_11043.ordinal()], world)
				&& isChunkNonEmpty(this.neighborPositions[Direction.field_11034.ordinal()], world)
				&& isChunkNonEmpty(this.neighborPositions[Direction.field_11035.ordinal()], world);
		}
	}

	public boolean method_3671(int i) {
		if (this.field_4471 == i) {
			return false;
		} else {
			this.field_4471 = i;
			return true;
		}
	}

	public GlBuffer getGlBuffer(int i) {
		return this.buffers[i];
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

	public void resortTransparency(float f, float g, float h, ChunkRenderTask chunkRenderTask) {
		ChunkRenderData chunkRenderData = chunkRenderTask.getRenderData();
		if (chunkRenderData.getBufferState() != null && !chunkRenderData.isEmpty(BlockRenderLayer.field_9179)) {
			this.beginBufferBuilding(chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.field_9179), this.origin);
			chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.field_9179).restoreState(chunkRenderData.getBufferState());
			this.endBufferBuilding(BlockRenderLayer.field_9179, f, g, h, chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.field_9179), chunkRenderData);
		}
	}

	public void rebuildChunk(float f, float g, float h, ChunkRenderTask chunkRenderTask) {
		ChunkRenderData chunkRenderData = new ChunkRenderData();
		int i = 1;
		BlockPos blockPos = this.origin.toImmutable();
		BlockPos blockPos2 = blockPos.add(15, 15, 15);
		World world = this.world;
		if (world != null) {
			chunkRenderTask.getLock().lock();

			try {
				if (chunkRenderTask.getStage() != ChunkRenderTask.Stage.field_4424) {
					return;
				}

				chunkRenderTask.setRenderData(chunkRenderData);
			} finally {
				chunkRenderTask.getLock().unlock();
			}

			ChunkOcclusionGraphBuilder chunkOcclusionGraphBuilder = new ChunkOcclusionGraphBuilder();
			HashSet set = Sets.newHashSet();
			ChunkRendererRegion chunkRendererRegion = chunkRenderTask.takeRegion();
			if (chunkRendererRegion != null) {
				chunkUpdateCount++;
				boolean[] bls = new boolean[BlockRenderLayer.values().length];
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
						BlockEntity blockEntity = chunkRendererRegion.getBlockEntity(blockPos3, WorldChunk.CreationType.field_12859);
						if (blockEntity != null) {
							BlockEntityRenderer<BlockEntity> blockEntityRenderer = BlockEntityRenderDispatcher.INSTANCE.get(blockEntity);
							if (blockEntityRenderer != null) {
								chunkRenderData.addBlockEntity(blockEntity);
								if (blockEntityRenderer.method_3563(blockEntity)) {
									set.add(blockEntity);
								}
							}
						}
					}

					FluidState fluidState = chunkRendererRegion.getFluidState(blockPos3);
					if (!fluidState.isEmpty()) {
						BlockRenderLayer blockRenderLayer = fluidState.getRenderLayer();
						int j = blockRenderLayer.ordinal();
						BufferBuilder bufferBuilder = chunkRenderTask.getBufferBuilders().get(j);
						if (!chunkRenderData.isBufferInitialized(blockRenderLayer)) {
							chunkRenderData.markBufferInitialized(blockRenderLayer);
							this.beginBufferBuilding(bufferBuilder, blockPos);
						}

						bls[j] |= blockRenderManager.tesselateFluid(blockPos3, chunkRendererRegion, bufferBuilder, fluidState);
					}

					if (blockState.getRenderType() != BlockRenderType.field_11455) {
						BlockRenderLayer blockRenderLayer = block.getRenderLayer();
						int j = blockRenderLayer.ordinal();
						BufferBuilder bufferBuilder = chunkRenderTask.getBufferBuilders().get(j);
						if (!chunkRenderData.isBufferInitialized(blockRenderLayer)) {
							chunkRenderData.markBufferInitialized(blockRenderLayer);
							this.beginBufferBuilding(bufferBuilder, blockPos);
						}

						bls[j] |= blockRenderManager.tesselateBlock(blockState, blockPos3, chunkRendererRegion, bufferBuilder, random);
					}
				}

				for (BlockRenderLayer blockRenderLayer2 : BlockRenderLayer.values()) {
					if (bls[blockRenderLayer2.ordinal()]) {
						chunkRenderData.setNonEmpty(blockRenderLayer2);
					}

					if (chunkRenderData.isBufferInitialized(blockRenderLayer2)) {
						this.endBufferBuilding(blockRenderLayer2, f, g, h, chunkRenderTask.getBufferBuilders().get(blockRenderLayer2), chunkRenderData);
					}
				}

				BlockModelRenderer.disableBrightnessCache();
			}

			chunkRenderData.setOcclusionGraph(chunkOcclusionGraphBuilder.build());
			this.lock.lock();

			try {
				Set<BlockEntity> set2 = Sets.<BlockEntity>newHashSet(set);
				Set<BlockEntity> set3 = Sets.<BlockEntity>newHashSet(this.blockEntities);
				set2.removeAll(this.blockEntities);
				set3.removeAll(set);
				this.blockEntities.clear();
				this.blockEntities.addAll(set);
				this.renderer.updateBlockEntities(set3, set2);
			} finally {
				this.lock.unlock();
			}
		}
	}

	protected void cancel() {
		this.lock.lock();

		try {
			if (this.task != null && this.task.getStage() != ChunkRenderTask.Stage.field_4423) {
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

	public ChunkRenderTask startRebuild() {
		this.lock.lock();

		ChunkRenderTask var4;
		try {
			this.cancel();
			BlockPos blockPos = this.origin.toImmutable();
			int i = 1;
			ChunkRendererRegion chunkRendererRegion = ChunkRendererRegion.create(this.world, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
			this.task = new ChunkRenderTask(this, ChunkRenderTask.Mode.field_4426, this.getSquaredCameraDistance(), chunkRendererRegion);
			var4 = this.task;
		} finally {
			this.lock.unlock();
		}

		return var4;
	}

	@Nullable
	public ChunkRenderTask startResortTransparency() {
		this.lock.lock();

		Object var1;
		try {
			if (this.task == null || this.task.getStage() != ChunkRenderTask.Stage.field_4422) {
				if (this.task != null && this.task.getStage() != ChunkRenderTask.Stage.field_4423) {
					this.task.cancel();
					this.task = null;
				}

				this.task = new ChunkRenderTask(this, ChunkRenderTask.Mode.field_4427, this.getSquaredCameraDistance(), null);
				this.task.setRenderData(this.data);
				return this.task;
			}

			var1 = null;
		} finally {
			this.lock.unlock();
		}

		return (ChunkRenderTask)var1;
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
		bufferBuilder.setOffset((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()));
	}

	private void endBufferBuilding(BlockRenderLayer blockRenderLayer, float f, float g, float h, BufferBuilder bufferBuilder, ChunkRenderData chunkRenderData) {
		if (blockRenderLayer == BlockRenderLayer.field_9179 && !chunkRenderData.isEmpty(blockRenderLayer)) {
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

		for (int i = 0; i < BlockRenderLayer.values().length; i++) {
			if (this.buffers[i] != null) {
				this.buffers[i].delete();
			}
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
