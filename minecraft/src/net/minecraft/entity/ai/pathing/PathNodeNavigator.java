package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkCache;

public class PathNodeNavigator {
	private final PathMinHeap minHeap = new PathMinHeap();
	private final Set<PathNode> field_59 = Sets.<PathNode>newHashSet();
	private final PathNode[] successors = new PathNode[32];
	private final int range;
	private PathNodeMaker pathNodeMaker;

	public PathNodeNavigator(PathNodeMaker pathNodeMaker, int i) {
		this.pathNodeMaker = pathNodeMaker;
		this.range = i;
	}

	@Nullable
	public Path findPathToAny(ChunkCache chunkCache, MobEntity mobEntity, Set<BlockPos> set, float f, int i) {
		this.minHeap.clear();
		this.pathNodeMaker.init(chunkCache, mobEntity);
		PathNode pathNode = this.pathNodeMaker.getStart();
		Map<TargetPathNode, BlockPos> map = (Map<TargetPathNode, BlockPos>)set.stream()
			.collect(
				Collectors.toMap(blockPos -> this.pathNodeMaker.getNode((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), Function.identity())
			);
		Path path = this.findPathToAny(pathNode, map, f, i);
		this.pathNodeMaker.clear();
		return path;
	}

	@Nullable
	private Path findPathToAny(PathNode pathNode, Map<TargetPathNode, BlockPos> map, float f, int i) {
		Set<TargetPathNode> set = map.keySet();
		pathNode.penalizedPathLength = 0.0F;
		pathNode.distanceToNearestTarget = this.calculateDistances(pathNode, set);
		pathNode.heapWeight = pathNode.distanceToNearestTarget;
		this.minHeap.clear();
		this.field_59.clear();
		this.minHeap.push(pathNode);
		int j = 0;

		while (!this.minHeap.isEmpty()) {
			if (++j >= this.range) {
				break;
			}

			PathNode pathNode2 = this.minHeap.pop();
			pathNode2.visited = true;
			set.stream().filter(targetPathNode -> pathNode2.getManhattanDistance(targetPathNode) <= (float)i).forEach(TargetPathNode::markReached);
			if (set.stream().anyMatch(TargetPathNode::isReached)) {
				break;
			}

			if (!(pathNode2.getDistance(pathNode) >= f)) {
				int k = this.pathNodeMaker.getSuccessors(this.successors, pathNode2);

				for (int l = 0; l < k; l++) {
					PathNode pathNode3 = this.successors[l];
					float g = pathNode2.getDistance(pathNode3);
					pathNode3.pathLength = pathNode2.pathLength + g;
					float h = pathNode2.penalizedPathLength + g + pathNode3.penalty;
					if (pathNode3.pathLength < f && (!pathNode3.isInHeap() || h < pathNode3.penalizedPathLength)) {
						pathNode3.previous = pathNode2;
						pathNode3.penalizedPathLength = h;
						pathNode3.distanceToNearestTarget = this.calculateDistances(pathNode3, set) * 1.5F;
						if (pathNode3.isInHeap()) {
							this.minHeap.setNodeWeight(pathNode3, pathNode3.penalizedPathLength + pathNode3.distanceToNearestTarget);
						} else {
							pathNode3.heapWeight = pathNode3.penalizedPathLength + pathNode3.distanceToNearestTarget;
							this.minHeap.push(pathNode3);
						}
					}
				}
			}
		}

		Stream<Path> stream;
		if (set.stream().anyMatch(TargetPathNode::isReached)) {
			stream = set.stream()
				.filter(TargetPathNode::isReached)
				.map(targetPathNode -> this.createPath(targetPathNode.getNearestNode(), (BlockPos)map.get(targetPathNode), true))
				.sorted(Comparator.comparingInt(Path::getLength));
		} else {
			stream = set.stream()
				.map(targetPathNode -> this.createPath(targetPathNode.getNearestNode(), (BlockPos)map.get(targetPathNode), false))
				.sorted(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
		}

		Optional<Path> optional = stream.findFirst();
		return !optional.isPresent() ? null : (Path)optional.get();
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
		List<PathNode> list = Lists.<PathNode>newArrayList();
		PathNode pathNode2 = pathNode;
		list.add(0, pathNode);

		while (pathNode2.previous != null) {
			pathNode2 = pathNode2.previous;
			list.add(0, pathNode2);
		}

		return new Path(list, blockPos, bl);
	}
}
