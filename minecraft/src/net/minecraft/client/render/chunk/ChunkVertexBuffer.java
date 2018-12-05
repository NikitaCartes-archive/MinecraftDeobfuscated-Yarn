package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.block.BlockRenderLayer;

@Environment(EnvType.CLIENT)
public class ChunkVertexBuffer {
	private final VertexBuffer[] renderLayerVertexBuffers = new VertexBuffer[BlockRenderLayer.values().length];

	public ChunkVertexBuffer() {
		this.renderLayerVertexBuffers[BlockRenderLayer.SOLID.ordinal()] = new VertexBuffer(2097152);
		this.renderLayerVertexBuffers[BlockRenderLayer.CUTOUT.ordinal()] = new VertexBuffer(131072);
		this.renderLayerVertexBuffers[BlockRenderLayer.MIPPED_CUTOUT.ordinal()] = new VertexBuffer(131072);
		this.renderLayerVertexBuffers[BlockRenderLayer.TRANSLUCENT.ordinal()] = new VertexBuffer(262144);
	}

	public VertexBuffer getVertexBuffer(BlockRenderLayer blockRenderLayer) {
		return this.renderLayerVertexBuffers[blockRenderLayer.ordinal()];
	}

	public VertexBuffer getVertexBuffer(int i) {
		return this.renderLayerVertexBuffers[i];
	}
}
