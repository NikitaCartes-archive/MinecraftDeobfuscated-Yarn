package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_854;
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
		public void method_3647(BlockRenderLayer blockRenderLayer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean method_3650(Direction direction, Direction direction2) {
			return false;
		}
	};
	private final boolean[] field_4450 = new boolean[BlockRenderLayer.values().length];
	private final boolean[] field_4452 = new boolean[BlockRenderLayer.values().length];
	private boolean empty = true;
	private final List<BlockEntity> blockEntities = Lists.<BlockEntity>newArrayList();
	private class_854 field_4455 = new class_854();
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

	public void method_3647(BlockRenderLayer blockRenderLayer) {
		this.field_4452[blockRenderLayer.ordinal()] = true;
	}

	public boolean method_3649(BlockRenderLayer blockRenderLayer) {
		return this.field_4452[blockRenderLayer.ordinal()];
	}

	public List<BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	public void addBlockEntity(BlockEntity blockEntity) {
		this.blockEntities.add(blockEntity);
	}

	public boolean method_3650(Direction direction, Direction direction2) {
		return this.field_4455.method_3695(direction, direction2);
	}

	public void method_3640(class_854 arg) {
		this.field_4455 = arg;
	}

	public BufferBuilder.State getBufferState() {
		return this.bufferState;
	}

	public void setBufferState(BufferBuilder.State state) {
		this.bufferState = state;
	}
}
