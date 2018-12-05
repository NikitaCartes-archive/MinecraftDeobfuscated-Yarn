package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class PathNodeNavigator {
	private final PathMinHeap minHeap = new PathMinHeap();
	private final Set<PathNode> field_59 = Sets.<PathNode>newHashSet();
	private final PathNode[] field_60 = new PathNode[32];
	private PathNodeMaker field_61;

	public PathNodeNavigator(PathNodeMaker pathNodeMaker) {
		this.field_61 = pathNodeMaker;
	}

	@Nullable
	public Path pathfind(BlockView blockView, MobEntity mobEntity, Entity entity, float f) {
		return this.pathfind(blockView, mobEntity, entity.x, entity.getBoundingBox().minY, entity.z, f);
	}

	@Nullable
	public Path pathfind(BlockView blockView, MobEntity mobEntity, BlockPos blockPos, float f) {
		return this.pathfind(
			blockView, mobEntity, (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F), f
		);
	}

	@Nullable
	private Path pathfind(BlockView blockView, MobEntity mobEntity, double d, double e, double f, float g) {
		this.minHeap.clear();
		this.field_61.init(blockView, mobEntity);
		PathNode pathNode = this.field_61.getStart();
		PathNode pathNode2 = this.field_61.getPathNode(d, e, f);
		Path path = this.method_54(pathNode, pathNode2, g);
		this.field_61.clear();
		return path;
	}

	@Nullable
	private Path method_54(PathNode pathNode, PathNode pathNode2, float f) {
		pathNode.field_36 = 0.0F;
		pathNode.field_34 = pathNode.manhattanDistance(pathNode2);
		pathNode.heapWeight = pathNode.field_34;
		this.minHeap.clear();
		this.field_59.clear();
		this.minHeap.push(pathNode);
		PathNode pathNode3 = pathNode;
		int i = 0;

		while (!this.minHeap.isEmpty()) {
			if (++i >= 200) {
				break;
			}

			PathNode pathNode4 = this.minHeap.pop();
			if (pathNode4.equals(pathNode2)) {
				pathNode3 = pathNode2;
				break;
			}

			if (pathNode4.manhattanDistance(pathNode2) < pathNode3.manhattanDistance(pathNode2)) {
				pathNode3 = pathNode4;
			}

			pathNode4.field_42 = true;
			int j = this.field_61.getPathNodes(this.field_60, pathNode4, pathNode2, f);

			for (int k = 0; k < j; k++) {
				PathNode pathNode5 = this.field_60[k];
				float g = pathNode4.manhattanDistance(pathNode5);
				pathNode5.field_46 = pathNode4.field_46 + g;
				pathNode5.field_45 = g + pathNode5.field_43;
				float h = pathNode4.field_36 + pathNode5.field_45;
				if (pathNode5.field_46 < f && (!pathNode5.isInHeap() || h < pathNode5.field_36)) {
					pathNode5.field_35 = pathNode4;
					pathNode5.field_36 = h;
					pathNode5.field_34 = pathNode5.manhattanDistance(pathNode2) + pathNode5.field_43;
					if (pathNode5.isInHeap()) {
						this.minHeap.setNodeWeight(pathNode5, pathNode5.field_36 + pathNode5.field_34);
					} else {
						pathNode5.heapWeight = pathNode5.field_36 + pathNode5.field_34;
						this.minHeap.push(pathNode5);
					}
				}
			}
		}

		return pathNode3 == pathNode ? null : this.method_55(pathNode, pathNode3);
	}

	private Path method_55(PathNode pathNode, PathNode pathNode2) {
		int i = 1;

		for (PathNode pathNode3 = pathNode2; pathNode3.field_35 != null; pathNode3 = pathNode3.field_35) {
			i++;
		}

		PathNode[] pathNodes = new PathNode[i];
		PathNode var7 = pathNode2;
		i--;

		for (pathNodes[i] = pathNode2; var7.field_35 != null; pathNodes[i] = var7) {
			var7 = var7.field_35;
			i--;
		}

		return new Path(pathNodes);
	}
}
