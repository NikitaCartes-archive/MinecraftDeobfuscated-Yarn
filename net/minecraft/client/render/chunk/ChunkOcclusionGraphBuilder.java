/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkOcclusionGraph;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ChunkOcclusionGraphBuilder {
    private static final int STEP_X = (int)Math.pow(16.0, 0.0);
    private static final int STEP_Z = (int)Math.pow(16.0, 1.0);
    private static final int STEP_Y = (int)Math.pow(16.0, 2.0);
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BitSet closed = new BitSet(4096);
    private static final int[] EDGE_POINTS = SystemUtil.consume(new int[1352], is -> {
        boolean i = false;
        int j = 15;
        int k = 0;
        for (int l = 0; l < 16; ++l) {
            for (int m = 0; m < 16; ++m) {
                for (int n = 0; n < 16; ++n) {
                    if (l != 0 && l != 15 && m != 0 && m != 15 && n != 0 && n != 15) continue;
                    is[k++] = ChunkOcclusionGraphBuilder.pack(l, m, n);
                }
            }
        }
    });
    private int openCount = 4096;

    public void markClosed(BlockPos blockPos) {
        this.closed.set(ChunkOcclusionGraphBuilder.pack(blockPos), true);
        --this.openCount;
    }

    private static int pack(BlockPos blockPos) {
        return ChunkOcclusionGraphBuilder.pack(blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF);
    }

    private static int pack(int i, int j, int k) {
        return i << 0 | j << 8 | k << 4;
    }

    public ChunkOcclusionGraph build() {
        ChunkOcclusionGraph chunkOcclusionGraph = new ChunkOcclusionGraph();
        if (4096 - this.openCount < 256) {
            chunkOcclusionGraph.fill(true);
        } else if (this.openCount == 0) {
            chunkOcclusionGraph.fill(false);
        } else {
            for (int i : EDGE_POINTS) {
                if (this.closed.get(i)) continue;
                chunkOcclusionGraph.addOpenEdgeFaces(this.getOpenFaces(i));
            }
        }
        return chunkOcclusionGraph;
    }

    public Set<Direction> getOpenFaces(BlockPos blockPos) {
        return this.getOpenFaces(ChunkOcclusionGraphBuilder.pack(blockPos));
    }

    private Set<Direction> getOpenFaces(int i) {
        EnumSet<Direction> set = EnumSet.noneOf(Direction.class);
        IntArrayFIFOQueue intPriorityQueue = new IntArrayFIFOQueue();
        intPriorityQueue.enqueue(i);
        this.closed.set(i, true);
        while (!intPriorityQueue.isEmpty()) {
            int j = intPriorityQueue.dequeueInt();
            this.addEdgeFaces(j, set);
            for (Direction direction : DIRECTIONS) {
                int k = this.offset(j, direction);
                if (k < 0 || this.closed.get(k)) continue;
                this.closed.set(k, true);
                intPriorityQueue.enqueue(k);
            }
        }
        return set;
    }

    private void addEdgeFaces(int i, Set<Direction> set) {
        int j = i >> 0 & 0xF;
        if (j == 0) {
            set.add(Direction.WEST);
        } else if (j == 15) {
            set.add(Direction.EAST);
        }
        int k = i >> 8 & 0xF;
        if (k == 0) {
            set.add(Direction.DOWN);
        } else if (k == 15) {
            set.add(Direction.UP);
        }
        int l = i >> 4 & 0xF;
        if (l == 0) {
            set.add(Direction.NORTH);
        } else if (l == 15) {
            set.add(Direction.SOUTH);
        }
    }

    private int offset(int i, Direction direction) {
        switch (direction) {
            case DOWN: {
                if ((i >> 8 & 0xF) == 0) {
                    return -1;
                }
                return i - STEP_Y;
            }
            case UP: {
                if ((i >> 8 & 0xF) == 15) {
                    return -1;
                }
                return i + STEP_Y;
            }
            case NORTH: {
                if ((i >> 4 & 0xF) == 0) {
                    return -1;
                }
                return i - STEP_Z;
            }
            case SOUTH: {
                if ((i >> 4 & 0xF) == 15) {
                    return -1;
                }
                return i + STEP_Z;
            }
            case WEST: {
                if ((i >> 0 & 0xF) == 0) {
                    return -1;
                }
                return i - STEP_X;
            }
            case EAST: {
                if ((i >> 0 & 0xF) == 15) {
                    return -1;
                }
                return i + STEP_X;
            }
        }
        return -1;
    }
}

