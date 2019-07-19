/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ChunkRenderData {
    public static final ChunkRenderData EMPTY = new ChunkRenderData(){

        @Override
        protected void setNonEmpty(RenderLayer renderLayer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void markBufferInitialized(RenderLayer renderLayer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isVisibleThrough(Direction direction, Direction direction2) {
            return false;
        }
    };
    private final boolean[] nonEmpty = new boolean[RenderLayer.values().length];
    private final boolean[] initialized = new boolean[RenderLayer.values().length];
    private boolean empty = true;
    private final List<BlockEntity> blockEntities = Lists.newArrayList();
    private ChunkOcclusionData occlusionGraph = new ChunkOcclusionData();
    private BufferBuilder.State bufferState;

    public boolean isEmpty() {
        return this.empty;
    }

    protected void setNonEmpty(RenderLayer renderLayer) {
        this.empty = false;
        this.nonEmpty[renderLayer.ordinal()] = true;
    }

    public boolean isEmpty(RenderLayer renderLayer) {
        return !this.nonEmpty[renderLayer.ordinal()];
    }

    public void markBufferInitialized(RenderLayer renderLayer) {
        this.initialized[renderLayer.ordinal()] = true;
    }

    public boolean isBufferInitialized(RenderLayer renderLayer) {
        return this.initialized[renderLayer.ordinal()];
    }

    public List<BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        this.blockEntities.add(blockEntity);
    }

    public boolean isVisibleThrough(Direction direction, Direction direction2) {
        return this.occlusionGraph.isVisibleThrough(direction, direction2);
    }

    public void setOcclusionGraph(ChunkOcclusionData chunkOcclusionData) {
        this.occlusionGraph = chunkOcclusionData;
    }

    public BufferBuilder.State getBufferState() {
        return this.bufferState;
    }

    public void setBufferState(BufferBuilder.State state) {
        this.bufferState = state;
    }
}

