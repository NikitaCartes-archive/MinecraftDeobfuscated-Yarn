package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkView;

public class class_3547 extends class_3560<class_3547.class_3548> {
	protected class_3547(ChunkView chunkView) {
		super(LightType.field_9282, chunkView, new class_3547.class_3548(new Long2ObjectOpenHashMap<>()));
	}

	@Override
	protected int method_15538(long l) {
		long m = BlockPos.method_10090(l);
		class_2804 lv = this.method_15522(m, false);
		return lv == null ? 0 : lv.method_12139(BlockPos.unpackLongX(l) & 15, BlockPos.unpackLongY(l) & 15, BlockPos.unpackLongZ(l) & 15);
	}

	public static final class class_3548 extends class_3556<class_3547.class_3548> {
		public class_3548(Long2ObjectOpenHashMap<class_2804> long2ObjectOpenHashMap) {
			super(long2ObjectOpenHashMap);
		}

		public class_3547.class_3548 method_15443() {
			return new class_3547.class_3548(this.field_15791.clone());
		}
	}
}
