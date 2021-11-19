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
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.SampleType;
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
        Map<TargetPathNode, BlockPos> map = positions.stream().collect(Collectors.toMap(pos -> this.pathNodeMaker.getNode((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), Function.identity()));
        Path path = this.findPathToAny(world.getProfiler(), pathNode, map, followRange, distance, rangeMultiplier);
        this.pathNodeMaker.clear();
        return path;
    }

    @Nullable
    private Path findPathToAny(Profiler profiler, PathNode startNode, Map<TargetPathNode, BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
        profiler.push("find_path");
        profiler.markSampleType(SampleType.PATH_FINDING);
        Set<TargetPathNode> set = positions.keySet();
        startNode.penalizedPathLength = 0.0f;
        startNode.heapWeight = startNode.distanceToNearestTarget = this.calculateDistances(startNode, set);
        this.minHeap.clear();
        this.minHeap.push(startNode);
        ImmutableSet set2 = ImmutableSet.of();
        int i = 0;
        HashSet<TargetPathNode> set3 = Sets.newHashSetWithExpectedSize(set.size());
        int j = (int)((float)this.range * rangeMultiplier);
        while (!this.minHeap.isEmpty() && ++i < j) {
            PathNode pathNode = this.minHeap.pop();
            pathNode.visited = true;
            for (TargetPathNode targetPathNode2 : set) {
                if (!(pathNode.getManhattanDistance(targetPathNode2) <= (float)distance)) continue;
                targetPathNode2.markReached();
                set3.add(targetPathNode2);
            }
            if (!set3.isEmpty()) break;
            if (pathNode.getDistance(startNode) >= followRange) continue;
            int k = this.pathNodeMaker.getSuccessors(this.successors, pathNode);
            for (int l = 0; l < k; ++l) {
                PathNode pathNode2 = this.successors[l];
                float f = pathNode.getDistance(pathNode2);
                pathNode2.pathLength = pathNode.pathLength + f;
                float g = pathNode.penalizedPathLength + f + pathNode2.penalty;
                if (!(pathNode2.pathLength < followRange) || pathNode2.isInHeap() && !(g < pathNode2.penalizedPathLength)) continue;
                pathNode2.previous = pathNode;
                pathNode2.penalizedPathLength = g;
                pathNode2.distanceToNearestTarget = this.calculateDistances(pathNode2, set) * 1.5f;
                if (pathNode2.isInHeap()) {
                    this.minHeap.setNodeWeight(pathNode2, pathNode2.penalizedPathLength + pathNode2.distanceToNearestTarget);
                    continue;
                }
                pathNode2.heapWeight = pathNode2.penalizedPathLength + pathNode2.distanceToNearestTarget;
                this.minHeap.push(pathNode2);
            }
        }
        Optional<Path> optional = !set3.isEmpty() ? set3.stream().map(targetPathNode -> this.createPath(targetPathNode.getNearestNode(), (BlockPos)positions.get(targetPathNode), true)).min(Comparator.comparingInt(Path::getLength)) : set.stream().map(targetPathNode -> this.createPath(targetPathNode.getNearestNode(), (BlockPos)positions.get(targetPathNode), false)).min(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
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

