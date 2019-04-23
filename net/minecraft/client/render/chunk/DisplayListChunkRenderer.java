/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class DisplayListChunkRenderer
extends ChunkRenderer {
    private final int displayListsStartIndex = GlAllocationUtils.genLists(BlockRenderLayer.values().length);

    public DisplayListChunkRenderer(World world, WorldRenderer worldRenderer) {
        super(world, worldRenderer);
    }

    public int method_3639(BlockRenderLayer blockRenderLayer, ChunkRenderData chunkRenderData) {
        if (!chunkRenderData.isEmpty(blockRenderLayer)) {
            return this.displayListsStartIndex + blockRenderLayer.ordinal();
        }
        return -1;
    }

    @Override
    public void delete() {
        super.delete();
        GlAllocationUtils.deleteLists(this.displayListsStartIndex, BlockRenderLayer.values().length);
    }
}

