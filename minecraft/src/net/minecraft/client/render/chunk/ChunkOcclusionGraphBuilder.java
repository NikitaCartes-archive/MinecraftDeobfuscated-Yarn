package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ChunkOcclusionGraphBuilder {
	private static final int STEP_X = (int)Math.pow(16.0, 0.0);
	private static final int STEP_Z = (int)Math.pow(16.0, 1.0);
	private static final int STEP_Y = (int)Math.pow(16.0, 2.0);
	private static final Direction[] DIRECTIONS = Direction.values();
	private final BitSet closed = new BitSet(4096);
	private static final int[] EDGE_POINTS = SystemUtil.consume(new int[1352], is -> {
		int i = 0;
		int j = 15;
		int k = 0;

		for (int l = 0; l < 16; l++) {
			for (int m = 0; m < 16; m++) {
				for (int n = 0; n < 16; n++) {
					if (l == 0 || l == 15 || m == 0 || m == 15 || n == 0 || n == 15) {
						is[k++] = pack(l, m, n);
					}
				}
			}
		}
	});
	private int openCount = 4096;

	public void markClosed(BlockPos blockPos) {
		this.closed.set(pack(blockPos), true);
		this.openCount--;
	}

	private static int pack(BlockPos blockPos) {
		return pack(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
	}

	private static int pack(int i, int j, int k) {
		return i << 0 | j << 8 | k << 4;
	}

	public ChunkOcclusionGraph method_3679() {
		ChunkOcclusionGraph chunkOcclusionGraph = new ChunkOcclusionGraph();
		if (4096 - this.openCount < 256) {
			chunkOcclusionGraph.fill(true);
		} else if (this.openCount == 0) {
			chunkOcclusionGraph.fill(false);
		} else {
			for (int i : EDGE_POINTS) {
				if (!this.closed.get(i)) {
					chunkOcclusionGraph.addOpenEdgeFaces(this.getOpenFaces(i));
				}
			}
		}

		return chunkOcclusionGraph;
	}

	public Set<Direction> getOpenFaces(BlockPos blockPos) {
		return this.getOpenFaces(pack(blockPos));
	}

	private Set<Direction> getOpenFaces(int i) {
		Set<Direction> set = EnumSet.noneOf(Direction.class);
		IntPriorityQueue intPriorityQueue = new IntArrayFIFOQueue();
		intPriorityQueue.enqueue(i);
		this.closed.set(i, true);

		while (!intPriorityQueue.isEmpty()) {
			int j = intPriorityQueue.dequeueInt();
			this.addEdgeFaces(j, set);

			for (Direction direction : DIRECTIONS) {
				int k = this.offset(j, direction);
				if (k >= 0 && !this.closed.get(k)) {
					this.closed.set(k, true);
					intPriorityQueue.enqueue(k);
				}
			}
		}

		return set;
	}

	private void addEdgeFaces(int i, Set<Direction> set) {
		int j = i >> 0 & 15;
		if (j == 0) {
			set.add(Direction.field_11039);
		} else if (j == 15) {
			set.add(Direction.field_11034);
		}

		int k = i >> 8 & 15;
		if (k == 0) {
			set.add(Direction.field_11033);
		} else if (k == 15) {
			set.add(Direction.field_11036);
		}

		int l = i >> 4 & 15;
		if (l == 0) {
			set.add(Direction.field_11043);
		} else if (l == 15) {
			set.add(Direction.field_11035);
		}
	}

	private int offset(int i, Direction direction) {
		switch (direction) {
			case field_11033:
				if ((i >> 8 & 15) == 0) {
					return -1;
				}

				return i - STEP_Y;
			case field_11036:
				if ((i >> 8 & 15) == 15) {
					return -1;
				}

				return i + STEP_Y;
			case field_11043:
				if ((i >> 4 & 15) == 0) {
					return -1;
				}

				return i - STEP_Z;
			case field_11035:
				if ((i >> 4 & 15) == 15) {
					return -1;
				}

				return i + STEP_Z;
			case field_11039:
				if ((i >> 0 & 15) == 0) {
					return -1;
				}

				return i - STEP_X;
			case field_11034:
				if ((i >> 0 & 15) == 15) {
					return -1;
				}

				return i + STEP_X;
			default:
				return -1;
		}
	}
}
