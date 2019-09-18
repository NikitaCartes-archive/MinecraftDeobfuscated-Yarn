package net.minecraft.client.render.chunk;

import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.BufferBuilder;

@Environment(EnvType.CLIENT)
public class BlockLayeredBufferBuilder {
	private final Map<BlockRenderLayer, BufferBuilder> layerBuilders = (Map<BlockRenderLayer, BufferBuilder>)BlockRenderLayer.method_22720()
		.stream()
		.collect(Collectors.toMap(blockRenderLayer -> blockRenderLayer, blockRenderLayer -> new BufferBuilder(blockRenderLayer.method_22722())));

	public BufferBuilder get(BlockRenderLayer blockRenderLayer) {
		return (BufferBuilder)this.layerBuilders.get(blockRenderLayer);
	}

	public void method_22705() {
		this.layerBuilders.values().forEach(BufferBuilder::clear);
	}
}
