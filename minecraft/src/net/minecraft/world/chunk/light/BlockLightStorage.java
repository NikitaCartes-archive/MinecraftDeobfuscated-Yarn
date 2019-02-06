package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.WorldNibbleStorage;

public class BlockLightStorage extends LightStorage<BlockLightStorage.Data> {
	protected BlockLightStorage(ChunkProvider chunkProvider) {
		super(LightType.BLOCK_LIGHT, chunkProvider, new BlockLightStorage.Data(new Long2ObjectOpenHashMap<>()));
	}

	@Override
	protected int getLight(long l) {
		long m = BlockPos.toChunkSectionOrigin(l);
		ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(m, false);
		return chunkNibbleArray == null ? 0 : chunkNibbleArray.get(BlockPos.unpackLongX(l) & 15, BlockPos.unpackLongY(l) & 15, BlockPos.unpackLongZ(l) & 15);
	}

	public static final class Data extends WorldNibbleStorage<BlockLightStorage.Data> {
		public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap) {
			super(long2ObjectOpenHashMap);
		}

		public BlockLightStorage.Data copy() {
			return new BlockLightStorage.Data(this.arraysByChunk.clone());
		}
	}
}
