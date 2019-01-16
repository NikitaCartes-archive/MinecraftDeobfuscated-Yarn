package net.minecraft.client.render.chunk;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_852;
import net.minecraft.class_853;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
public class ChunkRenderer {
	private volatile World world;
	private final WorldRenderer renderer;
	public static int chunkUpdateCount;
	public ChunkRenderData chunkRenderData = ChunkRenderData.EMPTY;
	private final ReentrantLock chunkRenderLock = new ReentrantLock();
	private final ReentrantLock field_4470 = new ReentrantLock();
	private ChunkRenderTask chunkRenderDataTask;
	private final Set<BlockEntity> blockEntities = Sets.<BlockEntity>newHashSet();
	private final FloatBuffer transformationMatrix = GlAllocationUtils.allocateFloatBuffer(16);
	private final GlBuffer[] buffers = new GlBuffer[BlockRenderLayer.values().length];
	public BoundingBox boundingBox;
	private int field_4471 = -1;
	private boolean field_4464 = true;
	private final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
	private final BlockPos.Mutable[] field_4472 = SystemUtil.consume(new BlockPos.Mutable[6], mutables -> {
		for (int ix = 0; ix < mutables.length; ix++) {
			mutables[ix] = new BlockPos.Mutable();
		}
	});
	private boolean field_4463;

	public ChunkRenderer(World world, WorldRenderer worldRenderer) {
		this.world = world;
		this.renderer = worldRenderer;
		if (GLX.useVbo()) {
			for (int i = 0; i < BlockRenderLayer.values().length; i++) {
				this.buffers[i] = new GlBuffer(VertexFormats.POSITION_COLOR_UV_LMAP);
			}
		}
	}

	private static boolean method_3651(BlockPos blockPos, World world) {
		return !world.getWorldChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4).isEmpty();
	}

	public boolean method_3673() {
		BlockPos blockPos = new BlockPos(MinecraftClient.getInstance().player);
		BlockPos blockPos2 = this.getOrigin();
		int i = 16;
		int j = 8;
		int k = 24;
		if (!(blockPos2.add(8, 8, 8).squaredDistanceTo(blockPos) > 576.0)) {
			return true;
		} else {
			World world = this.getWorld();
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos2);
			return method_3651(mutable.set(blockPos2).setOffset(Direction.WEST, 16), world)
				&& method_3651(mutable.set(blockPos2).setOffset(Direction.NORTH, 16), world)
				&& method_3651(mutable.set(blockPos2).setOffset(Direction.EAST, 16), world)
				&& method_3651(mutable.set(blockPos2).setOffset(Direction.SOUTH, 16), world);
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

	public void method_3653(int i, int j, int k) {
		if (i != this.origin.getX() || j != this.origin.getY() || k != this.origin.getZ()) {
			this.clear();
			this.origin.set(i, j, k);
			this.boundingBox = new BoundingBox((double)i, (double)j, (double)k, (double)(i + 16), (double)(j + 16), (double)(k + 16));

			for (Direction direction : Direction.values()) {
				this.field_4472[direction.ordinal()].set(this.origin).setOffset(direction, 16);
			}

			this.updateTransformationMatrix();
		}
	}

	public void method_3657(float f, float g, float h, ChunkRenderTask chunkRenderTask) {
		ChunkRenderData chunkRenderData = chunkRenderTask.getRenderData();
		if (chunkRenderData.getBufferState() != null && !chunkRenderData.method_3641(BlockRenderLayer.TRANSLUCENT)) {
			this.beginBufferBuilding(chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT), this.origin);
			chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT).restoreState(chunkRenderData.getBufferState());
			this.endBufferBuilding(BlockRenderLayer.TRANSLUCENT, f, g, h, chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT), chunkRenderData);
		}
	}

	public void method_3652(float f, float g, float h, ChunkRenderTask chunkRenderTask) {
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

			class_852 lv = new class_852();
			HashSet set = Sets.newHashSet();
			class_853 lv2 = chunkRenderTask.method_3606();
			if (lv2 != null) {
				chunkUpdateCount++;
				boolean[] bls = new boolean[BlockRenderLayer.values().length];
				BlockModelRenderer.enableBrightnessCache();
				Random random = new Random();
				BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();

				for (BlockPos blockPos3 : BlockPos.iterateBoxPositions(blockPos, blockPos2)) {
					BlockState blockState = lv2.getBlockState(blockPos3);
					Block block = blockState.getBlock();
					if (blockState.isFullOpaque(lv2, blockPos3)) {
						lv.method_3682(blockPos3);
					}

					if (block.hasBlockEntity()) {
						BlockEntity blockEntity = lv2.method_3688(blockPos3, WorldChunk.AccessType.GET);
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

					FluidState fluidState = lv2.getFluidState(blockPos3);
					if (!fluidState.isEmpty()) {
						BlockRenderLayer blockRenderLayer = fluidState.getRenderLayer();
						int j = blockRenderLayer.ordinal();
						BufferBuilder bufferBuilder = chunkRenderTask.getBufferBuilders().get(j);
						if (!chunkRenderData.isBufferInitialized(blockRenderLayer)) {
							chunkRenderData.markBufferInitialized(blockRenderLayer);
							this.beginBufferBuilding(bufferBuilder, blockPos);
						}

						bls[j] |= blockRenderManager.tesselateFluid(blockPos3, lv2, bufferBuilder, fluidState);
					}

					if (blockState.getRenderType() != BlockRenderType.field_11455) {
						BlockRenderLayer blockRenderLayer = block.getRenderLayer();
						int j = blockRenderLayer.ordinal();
						BufferBuilder bufferBuilder = chunkRenderTask.getBufferBuilders().get(j);
						if (!chunkRenderData.isBufferInitialized(blockRenderLayer)) {
							chunkRenderData.markBufferInitialized(blockRenderLayer);
							this.beginBufferBuilding(bufferBuilder, blockPos);
						}

						bls[j] |= blockRenderManager.tesselateBlock(blockState, blockPos3, lv2, bufferBuilder, random);
					}
				}

				for (BlockRenderLayer blockRenderLayer2 : BlockRenderLayer.values()) {
					if (bls[blockRenderLayer2.ordinal()]) {
						chunkRenderData.method_3643(blockRenderLayer2);
					}

					if (chunkRenderData.isBufferInitialized(blockRenderLayer2)) {
						this.endBufferBuilding(blockRenderLayer2, f, g, h, chunkRenderTask.getBufferBuilders().get(blockRenderLayer2), chunkRenderData);
					}
				}

				BlockModelRenderer.disableBrightnessCache();
			}

			chunkRenderData.method_3640(lv.method_3679());
			this.chunkRenderLock.lock();

			try {
				Set<BlockEntity> set2 = Sets.<BlockEntity>newHashSet(set);
				Set<BlockEntity> set3 = Sets.<BlockEntity>newHashSet(this.blockEntities);
				set2.removeAll(this.blockEntities);
				set3.removeAll(set);
				this.blockEntities.clear();
				this.blockEntities.addAll(set);
				this.renderer.method_3245(set3, set2);
			} finally {
				this.chunkRenderLock.unlock();
			}
		}
	}

	protected void cancel() {
		this.chunkRenderLock.lock();

		try {
			if (this.chunkRenderDataTask != null && this.chunkRenderDataTask.getStage() != ChunkRenderTask.Stage.field_4423) {
				this.chunkRenderDataTask.cancel();
				this.chunkRenderDataTask = null;
			}
		} finally {
			this.chunkRenderLock.unlock();
		}
	}

	public ReentrantLock getChunkRenderLock() {
		return this.chunkRenderLock;
	}

	public ChunkRenderTask method_3674() {
		this.chunkRenderLock.lock();

		ChunkRenderTask var4;
		try {
			this.cancel();
			BlockPos blockPos = this.origin.toImmutable();
			int i = 1;
			class_853 lv = class_853.method_3689(this.world, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
			this.chunkRenderDataTask = new ChunkRenderTask(this, ChunkRenderTask.Mode.field_4426, this.getDistanceToPlayerSquared(), lv);
			var4 = this.chunkRenderDataTask;
		} finally {
			this.chunkRenderLock.unlock();
		}

		return var4;
	}

	@Nullable
	public ChunkRenderTask getResortTransparencyTask() {
		this.chunkRenderLock.lock();

		Object var1;
		try {
			if (this.chunkRenderDataTask == null || this.chunkRenderDataTask.getStage() != ChunkRenderTask.Stage.field_4422) {
				if (this.chunkRenderDataTask != null && this.chunkRenderDataTask.getStage() != ChunkRenderTask.Stage.field_4423) {
					this.chunkRenderDataTask.cancel();
					this.chunkRenderDataTask = null;
				}

				this.chunkRenderDataTask = new ChunkRenderTask(this, ChunkRenderTask.Mode.field_4427, this.getDistanceToPlayerSquared(), null);
				this.chunkRenderDataTask.setRenderData(this.chunkRenderData);
				return this.chunkRenderDataTask;
			}

			var1 = null;
		} finally {
			this.chunkRenderLock.unlock();
		}

		return (ChunkRenderTask)var1;
	}

	protected double getDistanceToPlayerSquared() {
		ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
		double d = this.boundingBox.minX + 8.0 - clientPlayerEntity.x;
		double e = this.boundingBox.minY + 8.0 - clientPlayerEntity.y;
		double f = this.boundingBox.minZ + 8.0 - clientPlayerEntity.z;
		return d * d + e * e + f * f;
	}

	private void beginBufferBuilding(BufferBuilder bufferBuilder, BlockPos blockPos) {
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
		bufferBuilder.setOffset((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()));
	}

	private void endBufferBuilding(BlockRenderLayer blockRenderLayer, float f, float g, float h, BufferBuilder bufferBuilder, ChunkRenderData chunkRenderData) {
		if (blockRenderLayer == BlockRenderLayer.TRANSLUCENT && !chunkRenderData.method_3641(blockRenderLayer)) {
			bufferBuilder.sortQuads(f, g, h);
			chunkRenderData.setBufferState(bufferBuilder.toBufferState());
		}

		bufferBuilder.end();
	}

	private void updateTransformationMatrix() {
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		float f = 1.000001F;
		GlStateManager.translatef(-8.0F, -8.0F, -8.0F);
		GlStateManager.scalef(1.000001F, 1.000001F, 1.000001F);
		GlStateManager.translatef(8.0F, 8.0F, 8.0F);
		GlStateManager.getMatrix(2982, this.transformationMatrix);
		GlStateManager.popMatrix();
	}

	public void multiplyMatrix() {
		GlStateManager.multMatrix(this.transformationMatrix);
	}

	public ChunkRenderData getChunkRenderData() {
		return this.chunkRenderData;
	}

	public void method_3665(ChunkRenderData chunkRenderData) {
		this.field_4470.lock();

		try {
			this.chunkRenderData = chunkRenderData;
		} finally {
			this.field_4470.unlock();
		}
	}

	public void clear() {
		this.cancel();
		this.chunkRenderData = ChunkRenderData.EMPTY;
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

	public void scheduleRender(boolean bl) {
		if (this.field_4464) {
			bl |= this.field_4463;
		}

		this.field_4464 = true;
		this.field_4463 = bl;
	}

	public void method_3662() {
		this.field_4464 = false;
		this.field_4463 = false;
	}

	public boolean method_3672() {
		return this.field_4464;
	}

	public boolean method_3661() {
		return this.field_4464 && this.field_4463;
	}

	public BlockPos method_3676(Direction direction) {
		return this.field_4472[direction.ordinal()];
	}

	public World getWorld() {
		return this.world;
	}
}
