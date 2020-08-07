package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkCache;

public class PathNodeNavigator {
	private final PathNode[] successors = new PathNode[32];
	private final int range;
	private final PathNodeMaker pathNodeMaker;
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
		Map<TargetPathNode, BlockPos> map = (Map<TargetPathNode, BlockPos>)positions.stream()
			.collect(
				Collectors.toMap(blockPos -> this.pathNodeMaker.getNode((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), Function.identity())
			);
		Path path = this.findPathToAny(pathNode, map, followRange, distance, rangeMultiplier);
		this.pathNodeMaker.clear();
		return path;
	}

	@Nullable
	private Path findPathToAny(PathNode startNode, Map<TargetPathNode, BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
		Set<TargetPathNode> set = positions.keySet();
		startNode.penalizedPathLength = 0.0F;
		startNode.distanceToNearestTarget = this.calculateDistances(startNode, set);
		startNode.heapWeight = startNode.distanceToNearestTarget;
		this.minHeap.clear();
		this.minHeap.push(startNode);
		Set<PathNode> set2 = ImmutableSet.of();
		int i = 0;
		Set<TargetPathNode> set3 = Sets.<TargetPathNode>newHashSetWithExpectedSize(set.size());
		int j = (int)((float)this.range * rangeMultiplier);

		while (!this.minHeap.isEmpty()) {
			if (++i >= j) {
				break;
			}

			PathNode pathNode = this.minHeap.pop();
			pathNode.visited = true;

			for (TargetPathNode targetPathNode : set) {
				if (pathNode.getManhattanDistance(targetPathNode) <= (float)distance) {
					targetPathNode.markReached();
					set3.add(targetPathNode);
				}
			}

			if (!set3.isEmpty()) {
				break;
			}

			if (!(pathNode.getDistance(startNode) >= followRange)) {
				int k = this.pathNodeMaker.getSuccessors(this.successors, pathNode);

				for (int l = 0; l < k; l++) {
					PathNode pathNode2 = this.successors[l];
					float f = pathNode.getDistance(pathNode2);
					pathNode2.pathLength = pathNode.pathLength + f;
					float g = pathNode.penalizedPathLength + f + pathNode2.penalty;
					if (pathNode2.pathLength < followRange && (!pathNode2.isInHeap() || g < pathNode2.penalizedPathLength)) {
						pathNode2.previous = pathNode;
						pathNode2.penalizedPathLength = g;
						pathNode2.distanceToNearestTarget = this.calculateDistances(pathNode2, set) * 1.5F;
						if (pathNode2.isInHeap()) {
							this.minHeap.setNodeWeight(pathNode2, pathNode2.penalizedPathLength + pathNode2.distanceToNearestTarget);
						} else {
							pathNode2.heapWeight = pathNode2.penalizedPathLength + pathNode2.distanceToNearestTarget;
							this.minHeap.push(pathNode2);
						}
					}
				}
			}
		}

		Optional<Path> optional = !set3.isEmpty()
			? set3.stream()
				.map(targetPathNodex -> this.createPath(targetPathNodex.getNearestNode(), (BlockPos)positions.get(targetPathNodex), true))
				.min(Comparator.comparingInt(Path::getLength))
			: set.stream()
				.map(targetPathNodex -> this.createPath(targetPathNodex.getNearestNode(), (BlockPos)positions.get(targetPathNodex), false))
				.min(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
		return !optional.isPresent() ? null : (Path)optional.get();
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
		List<PathNode> list = Lists.<PathNode>newArrayList();
		PathNode pathNode = endNode;
		list.add(0, endNode);

		while (pathNode.previous != null) {
			pathNode = pathNode.previous;
			list.add(0, pathNode);
		}

		return new Path(list, target, reachesTarget);
	}
}
