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
import net.minecraft.client.util.profiler.SamplingChannel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.chunk.ChunkCache;

public class PathNodeNavigator {
	private static final float TARGET_DISTANCE_MULTIPLIER = 1.5F;
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
		Map<TargetPathNode, BlockPos> map = (Map<TargetPathNode, BlockPos>)positions.stream()
			.collect(
				Collectors.toMap(blockPos -> this.pathNodeMaker.getNode((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), Function.identity())
			);
		Path path = this.findPathToAny(world.method_37233(), pathNode, map, followRange, distance, rangeMultiplier);
		this.pathNodeMaker.clear();
		return path;
	}

	@Nullable
	private Path findPathToAny(Profiler profiler, PathNode pathNode, Map<TargetPathNode, BlockPos> map, float f, int i, float g) {
		profiler.push("find_path");
		profiler.method_37167(SamplingChannel.PATH_FINDING);
		Set<TargetPathNode> set = map.keySet();
		pathNode.penalizedPathLength = 0.0F;
		pathNode.distanceToNearestTarget = this.calculateDistances(pathNode, set);
		pathNode.heapWeight = pathNode.distanceToNearestTarget;
		this.minHeap.clear();
		this.minHeap.push(pathNode);
		Set<PathNode> set2 = ImmutableSet.of();
		int j = 0;
		Set<TargetPathNode> set3 = Sets.<TargetPathNode>newHashSetWithExpectedSize(set.size());
		int k = (int)((float)this.range * g);

		while (!this.minHeap.isEmpty()) {
			if (++j >= k) {
				break;
			}

			PathNode pathNode2 = this.minHeap.pop();
			pathNode2.visited = true;

			for (TargetPathNode targetPathNode : set) {
				if (pathNode2.getManhattanDistance(targetPathNode) <= (float)i) {
					targetPathNode.markReached();
					set3.add(targetPathNode);
				}
			}

			if (!set3.isEmpty()) {
				break;
			}

			if (!(pathNode2.getDistance(pathNode) >= f)) {
				int l = this.pathNodeMaker.getSuccessors(this.successors, pathNode2);

				for (int m = 0; m < l; m++) {
					PathNode pathNode3 = this.successors[m];
					float h = pathNode2.getDistance(pathNode3);
					pathNode3.pathLength = pathNode2.pathLength + h;
					float n = pathNode2.penalizedPathLength + h + pathNode3.penalty;
					if (pathNode3.pathLength < f && (!pathNode3.isInHeap() || n < pathNode3.penalizedPathLength)) {
						pathNode3.previous = pathNode2;
						pathNode3.penalizedPathLength = n;
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

		Optional<Path> optional = !set3.isEmpty()
			? set3.stream()
				.map(targetPathNodex -> this.createPath(targetPathNodex.getNearestNode(), (BlockPos)map.get(targetPathNodex), true))
				.min(Comparator.comparingInt(Path::getLength))
			: set.stream()
				.map(targetPathNodex -> this.createPath(targetPathNodex.getNearestNode(), (BlockPos)map.get(targetPathNodex), false))
				.min(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
		profiler.pop();
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
