package net.minecraft.entity.ai.pathing;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class PathNodeTypeCache {
	private static final int field_49417 = 4096;
	private static final int field_49418 = 4095;
	private final long[] positions = new long[4096];
	private final PathNodeType[] cache = new PathNodeType[4096];

	public PathNodeType add(BlockView world, BlockPos pos) {
		long l = pos.asLong();
		int i = hash(l);
		PathNodeType pathNodeType = this.get(i, l);
		return pathNodeType != null ? pathNodeType : this.compute(world, pos, i, l);
	}

	@Nullable
	private PathNodeType get(int index, long pos) {
		return this.positions[index] == pos ? this.cache[index] : null;
	}

	private PathNodeType compute(BlockView world, BlockPos pos, int index, long longPos) {
		PathNodeType pathNodeType = LandPathNodeMaker.getCommonNodeType(world, pos);
		this.positions[index] = longPos;
		this.cache[index] = pathNodeType;
		return pathNodeType;
	}

	public void invalidate(BlockPos pos) {
		long l = pos.asLong();
		int i = hash(l);
		if (this.positions[i] == l) {
			this.cache[i] = null;
		}
	}

	private static int hash(long pos) {
		return (int)HashCommon.mix(pos) & 4095;
	}
}
