package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.ViewableWorld;

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
	public Path pathfind(ViewableWorld viewableWorld, MobEntity mobEntity, double d, double e, double f, float g) {
		this.minHeap.clear();
		this.pathNodeMaker.init(viewableWorld, mobEntity);
		PathNode pathNode = this.pathNodeMaker.getStart();
		PathNode pathNode2 = this.pathNodeMaker.getPathNode(d, e, f);
		Path path = this.pathfind(pathNode, pathNode2, g);
		this.pathNodeMaker.clear();
		return path;
	}

	@Nullable
	private Path pathfind(PathNode pathNode, PathNode pathNode2, float f) {
		pathNode.field_36 = 0.0F;
		pathNode.field_34 = pathNode.distance(pathNode2);
		pathNode.heapWeight = pathNode.field_34;
		this.minHeap.clear();
		this.field_59.clear();
		this.minHeap.method_2(pathNode);
		PathNode pathNode3 = pathNode;
		int i = 0;

		while (!this.minHeap.isEmpty()) {
			if (++i >= this.field_18708) {
				break;
			}

			PathNode pathNode4 = this.minHeap.method_6();
			pathNode4.field_42 = true;
			if (pathNode4.equals(pathNode2)) {
				pathNode3 = pathNode2;
				break;
			}

			if (pathNode4.distance(pathNode2) < pathNode3.distance(pathNode2)) {
				pathNode3 = pathNode4;
			}

			if (!(pathNode4.distance(pathNode2) >= f)) {
				int j = this.pathNodeMaker.getPathNodes(this.field_60, pathNode4);

				for (int k = 0; k < j; k++) {
					PathNode pathNode5 = this.field_60[k];
					float g = pathNode4.distance(pathNode5);
					pathNode5.field_46 = pathNode4.field_46 + g;
					float h = pathNode4.field_36 + g + pathNode5.field_43;
					if (pathNode5.field_46 < f && (!pathNode5.isInHeap() || h < pathNode5.field_36)) {
						pathNode5.field_35 = pathNode4;
						pathNode5.field_36 = h;
						pathNode5.field_34 = pathNode5.distance(pathNode2) * 1.5F + pathNode5.field_43;
						if (pathNode5.isInHeap()) {
							this.minHeap.method_3(pathNode5, pathNode5.field_36 + pathNode5.field_34);
						} else {
							pathNode5.heapWeight = pathNode5.field_36 + pathNode5.field_34;
							this.minHeap.method_2(pathNode5);
						}
					}
				}
			}
		}

		return pathNode3.equals(pathNode) ? null : this.method_55(pathNode3);
	}

	private Path method_55(PathNode pathNode) {
		List<PathNode> list = Lists.<PathNode>newArrayList();
		PathNode pathNode2 = pathNode;
		list.add(0, pathNode);

		while (pathNode2.field_35 != null) {
			pathNode2 = pathNode2.field_35;
			list.add(0, pathNode2);
		}

		return new Path(list);
	}
}
