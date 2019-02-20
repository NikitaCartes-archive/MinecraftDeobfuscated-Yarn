package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.class_4076;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.WorldNibbleStorage;

public class BlockLightStorage extends LightStorage<BlockLightStorage.Data> {
	protected BlockLightStorage(ChunkProvider chunkProvider) {
		super(LightType.BLOCK, chunkProvider, new BlockLightStorage.Data(new Long2ObjectOpenHashMap<>()));
	}

	@Override
	protected int getLight(long l) {
		long m = class_4076.method_18691(l);
		ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(m, false);
		return chunkNibbleArray == null
			? 0
			: chunkNibbleArray.get(
				class_4076.method_18684(BlockPos.unpackLongX(l)), class_4076.method_18684(BlockPos.unpackLongY(l)), class_4076.method_18684(BlockPos.unpackLongZ(l))
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
