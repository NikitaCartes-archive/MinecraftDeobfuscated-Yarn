/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.util.profiler.SamplingChannel;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public class PathNodeNavigator {
    private static final float TARGET_DISTANCE_MULTIPLIER = 1.5f;
    private final PathNode[] successors = new PathNode[32];
    private final int range;
    private final PathNodeMaker pathNodeMaker;
    private static final boolean field_31808 = false;
    private final PathMinHeap minHeap = new PathMinHeap();

    public PathNodeNavigator(PathNodeMaker pathNodeMaker, int range) {
        this.pathNodeMaker = pathNodeMaker;
        this.range = range;
    }

    @Nullable
    public Path findPathToAny(ChunkCache world, MobEntity mob, Set<BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
        this.minHeap.clear();
        this.pathNodeMaker.init(world, mob);
        PathNode pathNode = this.pathNodeMaker.getStart();
        Map<TargetPathNode, BlockPos> map = positions.stream().collect(Collectors.toMap(blockPos -> this.pathNodeMaker.getNode((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), Function.identity()));
        Path path = this.findPathToAny(world.method_37233(), pathNode, map, followRange, distance, rangeMultiplier);
        this.pathNodeMaker.clear();
        return path;
    }

    @Nullable
    private Path findPathToAny(Profiler profiler, PathNode pathNode, Map<TargetPathNode, BlockPos> map, float f, int i, float g) {
        profiler.push("find_path");
        profiler.method_37167(SamplingChannel.PATH_FINDING);
        Set<TargetPathNode> set = map.keySet();
        pathNode.penalizedPathLength = 0.0f;
        pathNode.heapWeight = pathNode.distanceToNearestTarget = this.calculateDistances(pathNode, set);
        this.minHeap.clear();
        this.minHeap.push(pathNode);
        ImmutableSet set2 = ImmutableSet.of();
        int j = 0;
        HashSet<TargetPathNode> set3 = Sets.newHashSetWithExpectedSize(set.size());
        int k = (int)((float)this.range * g);
        while (!this.minHeap.isEmpty() && ++j < k) {
            PathNode pathNode2 = this.minHeap.pop();
            pathNode2.visited = true;
            for (TargetPathNode targetPathNode2 : set) {
                if (!(pathNode2.getManhattanDistance(targetPathNode2) <= (float)i)) continue;
                targetPathNode2.markReached();
                set3.add(targetPathNode2);
            }
            if (!set3.isEmpty()) break;
            if (pathNode2.getDistance(pathNode) >= f) continue;
            int l = this.pathNodeMaker.getSuccessors(this.successors, pathNode2);
            for (int m = 0; m < l; ++m) {
                PathNode pathNode3 = this.successors[m];
                float h = pathNode2.getDistance(pathNode3);
                pathNode3.pathLength = pathNode2.pathLength + h;
                float n = pathNode2.penalizedPathLength + h + pathNode3.penalty;
                if (!(pathNode3.pathLength < f) || pathNode3.isInHeap() && !(n < pathNode3.penalizedPathLength)) continue;
                pathNode3.previous = pathNode2;
                pathNode3.penalizedPathLength = n;
                pathNode3.distanceToNearestTarget = this.calculateDistances(pathNode3, set) * 1.5f;
                if (pathNode3.isInHeap()) {
                    this.minHeap.setNodeWeight(pathNode3, pathNode3.penalizedPathLength + pathNode3.distanceToNearestTarget);
                    continue;
                }
                pathNode3.heapWeight = pathNode3.penalizedPathLength + pathNode3.distanceToNearestTarget;
                this.minHeap.push(pathNode3);
            }
        }
        Optional<Path> optional = !set3.isEmpty() ? set3.stream().map(targetPathNode -> this.createPath(targetPathNode.getNearestNode(), (BlockPos)map.get(targetPathNode), true)).min(Comparator.comparingInt(Path::getLength)) : set.stream().map(targetPathNode -> this.createPath(targetPathNode.getNearestNode(), (BlockPos)map.get(targetPathNode), false)).min(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
        profiler.pop();
        if (!optional.isPresent()) {
            return null;
        }
        Path path = optional.get();
        return path;
    }

    private float calculateDistances(PathNode node, Set<TargetPathNode> targets) {
        float f = Float.MAX_VALUE;
        for (TargetPathNode targetPathNode : targets) {
            float g = node.getDistance(targetPathNode);
            targetPathNode.updateNearestNode(g, node);
            f = Math.min(g, f);
        }
        return f;
    }

    private Path createPath(PathNode endNode, BlockPos target, boolean reachesTarget) {
        ArrayList<PathNode> list = Lists.newArrayList();
        PathNode pathNode = endNode;
        list.add(0, pathNode);
        while (pathNode.previous != null) {
            pathNode = pathNode.previous;
            list.add(0, pathNode);
        }
        return new Path(list, target, reachesTarget);
    }
}

