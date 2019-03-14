package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ChunkRenderData {
	public static final ChunkRenderData EMPTY = new ChunkRenderData() {
		@Override
		protected void method_3643(BlockRenderLayer blockRenderLayer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void markBufferInitialized(BlockRenderLayer blockRenderLayer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean method_3650(Direction direction, Direction direction2) {
			return false;
		}
	};
	private final boolean[] field_4450 = new boolean[BlockRenderLayer.values().length];
	private final boolean[] initialized = new boolean[BlockRenderLayer.values().length];
	private boolean empty = true;
	private final List<BlockEntity> blockEntities = Lists.<BlockEntity>newArrayList();
	private ChunkOcclusionGraph field_4455 = new ChunkOcclusionGraph();
	private BufferBuilder.State bufferState;

	public boolean isEmpty() {
		return this.empty;
	}

	protected void method_3643(BlockRenderLayer blockRenderLayer) {
		this.empty = false;
		this.field_4450[blockRenderLayer.ordinal()] = true;
	}

	public boolean method_3641(BlockRenderLayer blockRenderLayer) {
		return !this.field_4450[blockRenderLayer.ordinal()];
	}

	public void markBufferInitialized(BlockRenderLayer blockRenderLayer) {
		this.initialized[blockRenderLayer.ordinal()] = true;
	}

	public boolean isBufferInitialized(BlockRenderLayer blockRenderLayer) {
		return this.initialized[blockRenderLayer.ordinal()];
	}

	public List<BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	public void addBlockEntity(BlockEntity blockEntity) {
		this.blockEntities.add(blockEntity);
	}

	public boolean method_3650(Direction direction, Direction direction2) {
		return this.field_4455.isVisibleThrough(direction, direction2);
	}

	public void method_3640(ChunkOcclusionGraph chunkOcclusionGraph) {
		this.field_4455 = chunkOcclusionGraph;
	}

	public BufferBuilder.State getBufferState() {
		return this.bufferState;
	}

	public void setBufferState(BufferBuilder.State state) {
		this.bufferState = state;
	}
}
