package net.minecraft.entity.ai.pathing;

public class PathMinHeap {
	private PathNode[] field_1 = new PathNode[128];
	private int count;

	public PathNode method_2(PathNode pathNode) {
		if (pathNode.heapIndex >= 0) {
			throw new IllegalStateException("OW KNOWS!");
		} else {
			if (this.count == this.field_1.length) {
				PathNode[] pathNodes = new PathNode[this.count << 1];
				System.arraycopy(this.field_1, 0, pathNodes, 0, this.count);
				this.field_1 = pathNodes;
			}

			this.field_1[this.count] = pathNode;
			pathNode.heapIndex = this.count;
			this.shiftUp(this.count++);
			return pathNode;
		}
	}

	public void clear() {
		this.count = 0;
	}

	public PathNode method_6() {
		PathNode pathNode = this.field_1[0];
		this.field_1[0] = this.field_1[--this.count];
		this.field_1[this.count] = null;
		if (this.count > 0) {
			this.shiftDown(0);
		}

		pathNode.heapIndex = -1;
		return pathNode;
	}

	public void method_3(PathNode pathNode, float f) {
		float g = pathNode.heapWeight;
		pathNode.heapWeight = f;
		if (f < g) {
			this.shiftUp(pathNode.heapIndex);
		} else {
			this.shiftDown(pathNode.heapIndex);
		}
	}

	private void shiftUp(int i) {
		PathNode pathNode = this.field_1[i];
		float f = pathNode.heapWeight;

		while (i > 0) {
			int j = i - 1 >> 1;
			PathNode pathNode2 = this.field_1[j];
			if (!(f < pathNode2.heapWeight)) {
				break;
			}

			this.field_1[i] = pathNode2;
			pathNode2.heapIndex = i;
			i = j;
		}

		this.field_1[i] = pathNode;
		pathNode.heapIndex = i;
	}

	private void shiftDown(int i) {
		PathNode pathNode = this.field_1[i];
		float f = pathNode.heapWeight;

		while (true) {
			int j = 1 + (i << 1);
			int k = j + 1;
			if (j >= this.count) {
				break;
			}

			PathNode pathNode2 = this.field_1[j];
			float g = pathNode2.heapWeight;
			PathNode pathNode3;
			float h;
			if (k >= this.count) {
				pathNode3 = null;
				h = Float.POSITIVE_INFINITY;
			} else {
				pathNode3 = this.field_1[k];
				h = pathNode3.heapWeight;
			}

			if (g < h) {
				if (!(g < f)) {
					break;
				}

				this.field_1[i] = pathNode2;
				pathNode2.heapIndex = i;
				i = j;
			} else {
				if (!(h < f)) {
					break;
				}

				this.field_1[i] = pathNode3;
				pathNode3.heapIndex = i;
				i = k;
			}
		}

		this.field_1[i] = pathNode;
		pathNode.heapIndex = i;
	}

	public boolean isEmpty() {
		return this.count == 0;
	}
}
