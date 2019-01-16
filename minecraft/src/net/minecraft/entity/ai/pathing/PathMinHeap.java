package net.minecraft.entity.ai.pathing;

public class PathMinHeap {
	private PathNode[] pathNodes = new PathNode[128];
	private int count;

	public PathNode push(PathNode pathNode) {
		if (pathNode.heapIndex >= 0) {
			throw new IllegalStateException("OW KNOWS!");
		} else {
			if (this.count == this.pathNodes.length) {
				PathNode[] pathNodes = new PathNode[this.count << 1];
				System.arraycopy(this.pathNodes, 0, pathNodes, 0, this.count);
				this.pathNodes = pathNodes;
			}

			this.pathNodes[this.count] = pathNode;
			pathNode.heapIndex = this.count;
			this.shiftUp(this.count++);
			return pathNode;
		}
	}

	public void clear() {
		this.count = 0;
	}

	public PathNode pop() {
		PathNode pathNode = this.pathNodes[0];
		this.pathNodes[0] = this.pathNodes[--this.count];
		this.pathNodes[this.count] = null;
		if (this.count > 0) {
			this.shiftDown(0);
		}

		pathNode.heapIndex = -1;
		return pathNode;
	}

	public void setNodeWeight(PathNode pathNode, float f) {
		float g = pathNode.heapWeight;
		pathNode.heapWeight = f;
		if (f < g) {
			this.shiftUp(pathNode.heapIndex);
		} else {
			this.shiftDown(pathNode.heapIndex);
		}
	}

	private void shiftUp(int i) {
		PathNode pathNode = this.pathNodes[i];
		float f = pathNode.heapWeight;

		while (i > 0) {
			int j = i - 1 >> 1;
			PathNode pathNode2 = this.pathNodes[j];
			if (!(f < pathNode2.heapWeight)) {
				break;
			}

			this.pathNodes[i] = pathNode2;
			pathNode2.heapIndex = i;
			i = j;
		}

		this.pathNodes[i] = pathNode;
		pathNode.heapIndex = i;
	}

	private void shiftDown(int i) {
		PathNode pathNode = this.pathNodes[i];
		float f = pathNode.heapWeight;

		while (true) {
			int j = 1 + (i << 1);
			int k = j + 1;
			if (j >= this.count) {
				break;
			}

			PathNode pathNode2 = this.pathNodes[j];
			float g = pathNode2.heapWeight;
			PathNode pathNode3;
			float h;
			if (k >= this.count) {
				pathNode3 = null;
				h = Float.POSITIVE_INFINITY;
			} else {
				pathNode3 = this.pathNodes[k];
				h = pathNode3.heapWeight;
			}

			if (g < h) {
				if (!(g < f)) {
					break;
				}

				this.pathNodes[i] = pathNode2;
				pathNode2.heapIndex = i;
				i = j;
			} else {
				if (!(h < f)) {
					break;
				}

				this.pathNodes[i] = pathNode3;
				pathNode3.heapIndex = i;
				i = k;
			}
		}

		this.pathNodes[i] = pathNode;
		pathNode.heapIndex = i;
	}

	public boolean isEmpty() {
		return this.count == 0;
	}
}
