package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.BufferBuilder;

@Environment(EnvType.CLIENT)
public class BlockLayeredBufferBuilder {
	private final BufferBuilder[] layerBuilders = new BufferBuilder[BlockRenderLayer.values().length];

	public BlockLayeredBufferBuilder() {
		this.layerBuilders[BlockRenderLayer.SOLID.ordinal()] = new BufferBuilder(2097152);
		this.layerBuilders[BlockRenderLayer.CUTOUT.ordinal()] = new BufferBuilder(131072);
		this.layerBuilders[BlockRenderLayer.CUTOUT_MIPPED.ordinal()] = new BufferBuilder(131072);
		this.layerBuilders[BlockRenderLayer.TRANSLUCENT.ordinal()] = new BufferBuilder(262144);
	}

	public BufferBuilder get(BlockRenderLayer blockRenderLayer) {
		return this.layerBuilders[blockRenderLayer.ordinal()];
	}

	public BufferBuilder get(int i) {
		return this.layerBuilders[i];
	}
}
