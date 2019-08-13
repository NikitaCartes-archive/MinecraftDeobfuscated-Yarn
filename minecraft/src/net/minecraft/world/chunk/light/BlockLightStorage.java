package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.WorldNibbleStorage;

public class BlockLightStorage extends LightStorage<BlockLightStorage.Data> {
	protected BlockLightStorage(ChunkProvider chunkProvider) {
		super(LightType.field_9282, chunkProvider, new BlockLightStorage.Data(new Long2ObjectOpenHashMap<>()));
	}

	@Override
	protected int getLight(long l) {
		long m = ChunkSectionPos.toChunkLong(l);
		ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(m, false);
		return chunkNibbleArray == null
			? 0
			: chunkNibbleArray.get(
				ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(l)),
				ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(l)),
				ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(l))
			);
	}

	public static final class Data extends WorldNibbleStorage<BlockLightStorage.Data> {
		public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap) {
			super(long2ObjectOpenHashMap);
		}

		public BlockLightStorage.Data method_15443() {
			return new BlockLightStorage.Data(this.arraysByChunk.clone());
		}
	}
}
