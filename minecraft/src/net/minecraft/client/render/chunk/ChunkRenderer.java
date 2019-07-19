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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Util;
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
	private final VertexBuffer[] buffers = new VertexBuffer[RenderLayer.values().length];
	public Box boundingBox;
	private int field_4471 = -1;
	private boolean rebuildScheduled = true;
	private final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
	private final BlockPos.Mutable[] neighborPositions = Util.make(new BlockPos.Mutable[6], mutables -> {
		for (int ix = 0; ix < mutables.length; ix++) {
			mutables[ix] = new BlockPos.Mutable();
		}
	});
	private boolean rebuildOnClientThread;

	public ChunkRenderer(World world, WorldRenderer worldRenderer) {
		this.world = world;
		this.renderer = worldRenderer;
		if (GLX.useVbo()) {
			for (int i = 0; i < RenderLayer.values().length; i++) {
				this.buffers[i] = new VertexBuffer(VertexFormats.POSITION_COLOR_UV_LMAP);
			}
		}
	}

	private static boolean isChunkNonEmpty(BlockPos pos, World world) {
		return !world.getChunk(pos.getX() >> 4, pos.getZ() >> 4).isEmpty();
	}

	public boolean shouldBuild() {
		int i = 24;
		if (!(this.getSquaredCameraDistance() > 576.0)) {
			return true;
		} else {
			World world = this.getWorld();
			return isChunkNonEmpty(this.neighborPositions[Direction.WEST.ordinal()], world)
				&& isChunkNonEmpty(this.neighborPositions[Direction.NORTH.ordinal()], world)
				&& isChunkNonEmpty(this.neighborPositions[Direction.EAST.ordinal()], world)
				&& isChunkNonEmpty(this.neighborPositions[Direction.SOUTH.ordinal()], world);
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

	public VertexBuffer getGlBuffer(int layer) {
		return this.buffers[layer];
	}

	public void setOrigin(int x, int y, int z) {
		if (x != this.origin.getX() || y != this.origin.getY() || z != this.origin.getZ()) {
			this.clear();
			this.origin.set(x, y, z);
			this.boundingBox = new Box((double)x, (double)y, (double)z, (double)(x + 16), (double)(y + 16), (double)(z + 16));

			for (Direction direction : Direction.values()) {
				this.neighborPositions[direction.ordinal()].set(this.origin).setOffset(direction, 16);
			}
		}
	}

	public void resortTransparency(float f, float g, float h, ChunkRenderTask chunkRenderTask) {
		ChunkRenderData chunkRenderData = chunkRenderTask.getRenderData();
		if (chunkRenderData.getBufferState() != null && !chunkRenderData.isEmpty(RenderLayer.TRANSLUCENT)) {
			this.beginBufferBuilding(chunkRenderTask.getBufferBuilders().get(RenderLayer.TRANSLUCENT), this.origin);
			chunkRenderTask.getBufferBuilders().get(RenderLayer.TRANSLUCENT).restoreState(chunkRenderData.getBufferState());
			this.endBufferBuilding(RenderLayer.TRANSLUCENT, f, g, h, chunkRenderTask.getBufferBuilders().get(RenderLayer.TRANSLUCENT), chunkRenderData);
		}
	}

	public void rebuildChunk(float cameraX, float cameraY, float cameraZ, ChunkRenderTask task) {
		ChunkRenderData chunkRenderData = new ChunkRenderData();
		int i = 1;
		BlockPos blockPos = this.origin.toImmutable();
		BlockPos blockPos2 = blockPos.add(15, 15, 15);
		World world = this.world;
		if (world != null) {
			task.getLock().lock();

			try {
				if (task.getStage() != ChunkRenderTask.Stage.COMPILING) {
					return;
				}

				task.setRenderData(chunkRenderData);
			} finally {
				task.getLock().unlock();
			}

			ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
			HashSet set = Sets.newHashSet();
			ChunkRendererRegion chunkRendererRegion = task.takeRegion();
			if (chunkRendererRegion != null) {
				chunkUpdateCount++;
				boolean[] bls = new boolean[RenderLayer.values().length];
				BlockModelRenderer.enableBrightnessCache();
				Random random = new Random();
				BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();

				for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
					BlockState blockState = chunkRendererRegion.getBlockState(blockPos3);
					Block block = blockState.getBlock();
					if (blockState.isFullOpaque(chunkRendererRegion, blockPos3)) {
						chunkOcclusionDataBuilder.markClosed(blockPos3);
					}

					if (block.hasBlockEntity()) {
						BlockEntity blockEntity = chunkRendererRegion.getBlockEntity(blockPos3, WorldChunk.CreationType.CHECK);
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
						RenderLayer renderLayer = fluidState.getRenderLayer();
						int j = renderLayer.ordinal();
						BufferBuilder bufferBuilder = task.getBufferBuilders().get(j);
						if (!chunkRenderData.isBufferInitialized(renderLayer)) {
							chunkRenderData.markBufferInitialized(renderLayer);
							this.beginBufferBuilding(bufferBuilder, blockPos);
						}

						bls[j] |= blockRenderManager.tesselateFluid(blockPos3, chunkRendererRegion, bufferBuilder, fluidState);
					}

					if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
						RenderLayer renderLayer = block.getRenderLayer();
						int j = renderLayer.ordinal();
						BufferBuilder bufferBuilder = task.getBufferBuilders().get(j);
						if (!chunkRenderData.isBufferInitialized(renderLayer)) {
							chunkRenderData.markBufferInitialized(renderLayer);
							this.beginBufferBuilding(bufferBuilder, blockPos);
						}

						bls[j] |= blockRenderManager.tesselateBlock(blockState, blockPos3, chunkRendererRegion, bufferBuilder, random);
					}
				}

				for (RenderLayer renderLayer2 : RenderLayer.values()) {
					if (bls[renderLayer2.ordinal()]) {
						chunkRenderData.setNonEmpty(renderLayer2);
					}

					if (chunkRenderData.isBufferInitialized(renderLayer2)) {
						this.endBufferBuilding(renderLayer2, cameraX, cameraY, cameraZ, task.getBufferBuilders().get(renderLayer2), chunkRenderData);
					}
				}

				BlockModelRenderer.disableBrightnessCache();
			}

			chunkRenderData.setOcclusionGraph(chunkOcclusionDataBuilder.build());
			this.lock.lock();

			try {
				Set<BlockEntity> set2 = Sets.<BlockEntity>newHashSet(set);
				Set<BlockEntity> set3 = Sets.<BlockEntity>newHashSet(this.blockEntities);
				set2.removeAll(this.blockEntities);
				set3.removeAll(set);
				this.blockEntities.clear();
				this.blockEntities.addAll(set);
				this.renderer.updateNoCullingBlockEntities(set3, set2);
			} finally {
				this.lock.unlock();
			}
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

	public ChunkRenderTask startRebuild() {
		this.lock.lock();

		ChunkRenderTask var4;
		try {
			this.cancel();
			BlockPos blockPos = this.origin.toImmutable();
			int i = 1;
			ChunkRendererRegion chunkRendererRegion = ChunkRendererRegion.create(this.world, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
			this.task = new ChunkRenderTask(this, ChunkRenderTask.Mode.REBUILD_CHUNK, this.getSquaredCameraDistance(), chunkRendererRegion);
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
			if (this.task == null || this.task.getStage() != ChunkRenderTask.Stage.PENDING) {
				if (this.task != null && this.task.getStage() != ChunkRenderTask.Stage.DONE) {
					this.task.cancel();
					this.task = null;
				}

				this.task = new ChunkRenderTask(this, ChunkRenderTask.Mode.RESORT_TRANSPARENCY, this.getSquaredCameraDistance(), null);
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
		double d = this.boundingBox.x1 + 8.0 - camera.getPos().x;
		double e = this.boundingBox.y1 + 8.0 - camera.getPos().y;
		double f = this.boundingBox.z1 + 8.0 - camera.getPos().z;
		return d * d + e * e + f * f;
	}

	private void beginBufferBuilding(BufferBuilder bufferBuilder, BlockPos blockPos) {
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
		bufferBuilder.setOffset((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()));
	}

	private void endBufferBuilding(RenderLayer renderLayer, float f, float g, float h, BufferBuilder bufferBuilder, ChunkRenderData chunkRenderData) {
		if (renderLayer == RenderLayer.TRANSLUCENT && !chunkRenderData.isEmpty(renderLayer)) {
			bufferBuilder.sortQuads(f, g, h);
			chunkRenderData.setBufferState(bufferBuilder.popState());
		}

		bufferBuilder.end();
	}

	public ChunkRenderData getData() {
		return this.data;
	}

	public void setData(ChunkRenderData data) {
		this.dataLock.lock();

		try {
			this.data = data;
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

		for (int i = 0; i < RenderLayer.values().length; i++) {
			if (this.buffers[i] != null) {
				this.buffers[i].delete();
			}
		}
	}

	public BlockPos getOrigin() {
		return this.origin;
	}

	public void scheduleRebuild(boolean rebuildOnClientThread) {
		if (this.rebuildScheduled) {
			rebuildOnClientThread |= this.rebuildOnClientThread;
		}

		this.rebuildScheduled = true;
		this.rebuildOnClientThread = rebuildOnClientThread;
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
