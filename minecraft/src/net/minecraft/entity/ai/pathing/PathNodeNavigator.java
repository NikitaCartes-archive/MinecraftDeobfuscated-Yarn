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
import net.minecraft.world.CollisionView;

public class PathNodeNavigator {
	private final PathMinHeap minHeap = new PathMinHeap();
	private final Set<PathNode> field_59 = Sets.<PathNode>newHashSet();
	private final PathNode[] field_60 = new PathNode[32];
	private final int field_18708;
	private PathNodeMaker pathNodeMaker;

	public PathNodeNavigator(PathNodeMaker pathNodeMaker, int i) {
		this.pathNodeMaker = pathNodeMaker;
		this.field_18708 = i;
	}

	@Nullable
	public Path pathfind(CollisionView collisionView, MobEntity mobEntity, Set<BlockPos> set, float f, int i) {
		this.minHeap.clear();
		this.pathNodeMaker.init(collisionView, mobEntity);
		PathNode pathNode = this.pathNodeMaker.getStart();
		Map<TargetPathNode, BlockPos> map = (Map<TargetPathNode, BlockPos>)set.stream()
			.collect(
				Collectors.toMap(blockPos -> this.pathNodeMaker.getNode((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), Function.identity())
			);
		Path path = this.pathfind(pathNode, map, f, i);
		this.pathNodeMaker.clear();
		return path;
	}

	@Nullable
	private Path pathfind(PathNode pathNode, Map<TargetPathNode, BlockPos> map, float f, int i) {
		Set<TargetPathNode> set = map.keySet();
		pathNode.field_36 = 0.0F;
		pathNode.field_34 = this.method_21658(pathNode, set);
		pathNode.heapWeight = pathNode.field_34;
		this.minHeap.clear();
		this.field_59.clear();
		this.minHeap.push(pathNode);
		int j = 0;

		while (!this.minHeap.isEmpty()) {
			if (++j >= this.field_18708) {
				break;
			}

			PathNode pathNode2 = this.minHeap.pop();
			pathNode2.field_42 = true;
			set.stream().filter(targetPathNode -> pathNode2.method_21653(targetPathNode) <= (float)i).forEach(TargetPathNode::markReached);
			if (set.stream().anyMatch(TargetPathNode::isReached)) {
				break;
			}

			if (!(pathNode2.getDistance(pathNode) >= f)) {
				int k = this.pathNodeMaker.getSuccessors(this.field_60, pathNode2);

				for (int l = 0; l < k; l++) {
					PathNode pathNode3 = this.field_60[l];
					float g = pathNode2.getDistance(pathNode3);
					pathNode3.field_46 = pathNode2.field_46 + g;
					float h = pathNode2.field_36 + g + pathNode3.field_43;
					if (pathNode3.field_46 < f && (!pathNode3.isInHeap() || h < pathNode3.field_36)) {
						pathNode3.field_35 = pathNode2;
						pathNode3.field_36 = h;
						pathNode3.field_34 = this.method_21658(pathNode3, set) * 1.5F;
						if (pathNode3.isInHeap()) {
							this.minHeap.setNodeWeight(pathNode3, pathNode3.field_36 + pathNode3.field_34);
						} else {
							pathNode3.heapWeight = pathNode3.field_36 + pathNode3.field_34;
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
				.map(targetPathNode -> this.method_55(targetPathNode.getNearestNode(), (BlockPos)map.get(targetPathNode), true))
				.sorted(Comparator.comparingInt(Path::getLength));
		} else {
			stream = set.stream()
				.map(targetPathNode -> this.method_55(targetPathNode.getNearestNode(), (BlockPos)map.get(targetPathNode), false))
				.sorted(Comparator.comparingDouble(Path::method_21656).thenComparingInt(Path::getLength));
		}

		Optional<Path> optional = stream.findFirst();
		return !optional.isPresent() ? null : (Path)optional.get();
	}

	private float method_21658(PathNode pathNode, Set<TargetPathNode> set) {
		float f = Float.MAX_VALUE;

		for (TargetPathNode targetPathNode : set) {
			float g = pathNode.getDistance(targetPathNode);
			targetPathNode.updateNearestNode(g, pathNode);
			f = Math.min(g, f);
		}

		return f;
	}

	private Path method_55(PathNode pathNode, BlockPos blockPos, boolean bl) {
		List<PathNode> list = Lists.<PathNode>newArrayList();
		PathNode pathNode2 = pathNode;
		list.add(0, pathNode);

		while (pathNode2.field_35 != null) {
			pathNode2 = pathNode2.field_35;
			list.add(0, pathNode2);
		}

		return new Path(list, blockPos, bl);
	}
}
