package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_848 extends ChunkRenderer {
	private final int field_4449 = GlAllocationUtils.genLists(BlockRenderLayer.values().length);

	public class_848(World world, WorldRenderer worldRenderer) {
		super(world, worldRenderer);
	}

	public int method_3639(BlockRenderLayer blockRenderLayer, ChunkRenderData chunkRenderData) {
		return !chunkRenderData.method_3641(blockRenderLayer) ? this.field_4449 + blockRenderLayer.ordinal() : -1;
	}

	@Override
	public void delete() {
		super.delete();
		GlAllocationUtils.deleteLists(this.field_4449, BlockRenderLayer.values().length);
	}
}
