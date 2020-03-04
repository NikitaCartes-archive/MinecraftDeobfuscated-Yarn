package net.minecraft.entity.ai.pathing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;

public class TargetPathNode extends PathNode {
	private float nearestNodeDistance = Float.MAX_VALUE;
	private PathNode nearestNode;
	private boolean reached;

	public TargetPathNode(PathNode node) {
		super(node.x, node.y, node.z);
	}

	@Environment(EnvType.CLIENT)
	public TargetPathNode(int i, int j, int k) {
		super(i, j, k);
	}

	public void updateNearestNode(float distance, PathNode node) {
		if (distance < this.nearestNodeDistance) {
			this.nearestNodeDistance = distance;
			this.nearestNode = node;
		}
	}

	public PathNode getNearestNode() {
		return this.nearestNode;
	}

	public void markReached() {
		this.reached = true;
	}

	public boolean isReached() {
		return this.reached;
	}

	@Environment(EnvType.CLIENT)
	public static TargetPathNode fromBuffer(PacketByteBuf buffer) {
		TargetPathNode targetPathNode = new TargetPathNode(buffer.readInt(), buffer.readInt(), buffer.readInt());
		targetPathNode.pathLength = buffer.readFloat();
		targetPathNode.penalty = buffer.readFloat();
		targetPathNode.visited = buffer.readBoolean();
		targetPathNode.type = PathNodeType.values()[buffer.readInt()];
		targetPathNode.heapWeight = buffer.readFloat();
		return targetPathNode;
	}
}
