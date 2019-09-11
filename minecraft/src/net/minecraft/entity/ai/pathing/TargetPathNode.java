package net.minecraft.entity.ai.pathing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;

public class TargetPathNode extends PathNode {
	private float nearestNodeDistance = Float.MAX_VALUE;
	private PathNode nearestNode;
	private boolean reached;

	public TargetPathNode(PathNode pathNode) {
		super(pathNode.x, pathNode.y, pathNode.z);
	}

	@Environment(EnvType.CLIENT)
	public TargetPathNode(int i, int j, int k) {
		super(i, j, k);
	}

	public void updateNearestNode(float f, PathNode pathNode) {
		if (f < this.nearestNodeDistance) {
			this.nearestNodeDistance = f;
			this.nearestNode = pathNode;
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
	public static TargetPathNode fromBuffer(PacketByteBuf packetByteBuf) {
		TargetPathNode targetPathNode = new TargetPathNode(packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt());
		targetPathNode.pathLength = packetByteBuf.readFloat();
		targetPathNode.penalty = packetByteBuf.readFloat();
		targetPathNode.visited = packetByteBuf.readBoolean();
		targetPathNode.type = PathNodeType.values()[packetByteBuf.readInt()];
		targetPathNode.heapWeight = packetByteBuf.readFloat();
		return targetPathNode;
	}
}
