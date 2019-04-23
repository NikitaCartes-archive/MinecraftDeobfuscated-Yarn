/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.WorldNibbleStorage;
import net.minecraft.world.chunk.light.LightStorage;

public class BlockLightStorage
extends LightStorage<Data> {
    protected BlockLightStorage(ChunkProvider chunkProvider) {
        super(LightType.BLOCK, chunkProvider, new Data(new Long2ObjectOpenHashMap<ChunkNibbleArray>()));
    }

    @Override
    protected int getLight(long l) {
        long m = ChunkSectionPos.toChunkLong(l);
        ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(m, false);
        if (chunkNibbleArray == null) {
            return 0;
        }
        return chunkNibbleArray.get(ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(l)));
    }

    public static final class Data
    extends WorldNibbleStorage<Data> {
        public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap) {
            super(long2ObjectOpenHashMap);
        }

        public Data method_15443() {
            return new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)this.arraysByChunk.clone());
        }

        @Override
        public /* synthetic */ WorldNibbleStorage copy() {
            return this.method_15443();
        }
    }
}

