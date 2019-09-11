package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;

public class BlockLightStorage extends LightStorage<BlockLightStorage.Data> {
	protected BlockLightStorage(ChunkProvider chunkProvider) {
		super(LightType.BLOCK, chunkProvider, new BlockLightStorage.Data(new Long2ObjectOpenHashMap<>()));
	}

	@Override
	protected int getLight(long l) {
		long m = ChunkSectionPos.fromGlobalPos(l);
		ChunkNibbleArray chunkNibbleArray = this.getLightArray(m, false);
		return chunkNibbleArray == null
			? 0
			: chunkNibbleArray.get(
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l))
			);
	}

	public static final class Data extends ChunkToNibbleArrayMap<BlockLightStorage.Data> {
		public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap) {
			super(long2ObjectOpenHashMap);
		}

		public BlockLightStorage.Data method_15443() {
			return new BlockLightStorage.Data(this.arrays.clone());
		}
	}
}
