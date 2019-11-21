/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public class PathNodeNavigator {
    private final PathMinHeap minHeap = new PathMinHeap();
    private final Set<PathNode> field_59 = Sets.newHashSet();
    private final PathNode[] successors = new PathNode[32];
    private final int range;
    private final PathNodeMaker pathNodeMaker;

    public PathNodeNavigator(PathNodeMaker pathNodeMaker, int i) {
        this.pathNodeMaker = pathNodeMaker;
        this.range = i;
    }

    @Nullable
    public Path findPathToAny(ChunkCache chunkCache, MobEntity mobEntity, Set<BlockPos> set, float f, int i, float g) {
        this.minHeap.clear();
        this.pathNodeMaker.init(chunkCache, mobEntity);
        PathNode pathNode = this.pathNodeMaker.getStart();
        Map<TargetPathNode, BlockPos> map = set.stream().collect(Collectors.toMap(blockPos -> this.pathNodeMaker.getNode((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), Function.identity()));
        Path path = this.findPathToAny(pathNode, map, f, i, g);
        this.pathNodeMaker.clear();
        return path;
    }

    @Nullable
    private Path findPathToAny(PathNode pathNode, Map<TargetPathNode, BlockPos> map, float f, int i, float g) {
        Stream<Path> stream;
        Optional<Path> optional;
        Set<TargetPathNode> set = map.keySet();
        pathNode.penalizedPathLength = 0.0f;
        pathNode.heapWeight = pathNode.distanceToNearestTarget = this.calculateDistances(pathNode, set);
        this.minHeap.clear();
        this.field_59.clear();
        this.minHeap.push(pathNode);
        int j = 0;
        int k = (int)((float)this.range * g);
        while (!this.minHeap.isEmpty() && ++j < k) {
            PathNode pathNode2 = this.minHeap.pop();
            pathNode2.visited = true;
            set.stream().filter(targetPathNode -> pathNode2.getManhattanDistance((PathNode)targetPathNode) <= (float)i).forEach(TargetPathNode::markReached);
            if (set.stream().anyMatch(TargetPathNode::isReached)) break;
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
        if (!(optional = (stream = set.stream().anyMatch(TargetPathNode::isReached) ? set.stream().filter(TargetPathNode::isReached).map(targetPathNode -> this.createPath(targetPathNode.getNearestNode(), (BlockPos)map.get(targetPathNode), true)).sorted(Comparator.comparingInt(Path::getLength)) : set.stream().map(targetPathNode -> this.createPath(targetPathNode.getNearestNode(), (BlockPos)map.get(targetPathNode), false)).sorted(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength))).findFirst()).isPresent()) {
            return null;
        }
        Path path = optional.get();
        return path;
    }

    private float calculateDistances(PathNode pathNode, Set<TargetPathNode> set) {
        float f = Float.MAX_VALUE;
        for (TargetPathNode targetPathNode : set) {
            float g = pathNode.getDistance(targetPathNode);
            targetPathNode.updateNearestNode(g, pathNode);
            f = Math.min(g, f);
        }
        return f;
    }

    private Path createPath(PathNode pathNode, BlockPos blockPos, boolean bl) {
        ArrayList<PathNode> list = Lists.newArrayList();
        PathNode pathNode2 = pathNode;
        list.add(0, pathNode2);
        while (pathNode2.previous != null) {
            pathNode2 = pathNode2.previous;
            list.add(0, pathNode2);
        }
        return new Path(list, blockPos, bl);
    }
}

