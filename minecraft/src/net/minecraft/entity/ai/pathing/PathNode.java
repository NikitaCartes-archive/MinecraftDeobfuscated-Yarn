package net.minecraft.entity.ai.pathing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PathNode {
	public final int x;
	public final int y;
	public final int z;
	private final int hashCode;
	public int heapIndex = -1;
	public float penalizedPathLength;
	public float distanceToNearestTarget;
	public float heapWeight;
	public PathNode previous;
	public boolean visited;
	public float pathLength;
	public float penalty;
	public PathNodeType type = PathNodeType.BLOCKED;

	public PathNode(int i, int j, int k) {
		this.x = i;
		this.y = j;
		this.z = k;
		this.hashCode = hash(i, j, k);
	}

	public PathNode copyWithNewPosition(int i, int j, int k) {
		PathNode pathNode = new PathNode(i, j, k);
		pathNode.heapIndex = this.heapIndex;
		pathNode.penalizedPathLength = this.penalizedPathLength;
		pathNode.distanceToNearestTarget = this.distanceToNearestTarget;
		pathNode.heapWeight = this.heapWeight;
		pathNode.previous = this.previous;
		pathNode.visited = this.visited;
		pathNode.pathLength = this.pathLength;
		pathNode.penalty = this.penalty;
		pathNode.type = this.type;
		return pathNode;
	}

	public static int hash(int i, int j, int k) {
		return j & 0xFF | (i & 32767) << 8 | (k & 32767) << 24 | (i < 0 ? Integer.MIN_VALUE : 0) | (k < 0 ? 32768 : 0);
	}

	public float getDistance(PathNode pathNode) {
		float f = (float)(pathNode.x - this.x);
		float g = (float)(pathNode.y - this.y);
		float h = (float)(pathNode.z - this.z);
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public float getSquaredDistance(PathNode pathNode) {
		float f = (float)(pathNode.x - this.x);
		float g = (float)(pathNode.y - this.y);
		float h = (float)(pathNode.z - this.z);
		return f * f + g * g + h * h;
	}

	public float getManhattanDistance(PathNode pathNode) {
		float f = (float)Math.abs(pathNode.x - this.x);
		float g = (float)Math.abs(pathNode.y - this.y);
		float h = (float)Math.abs(pathNode.z - this.z);
		return f + g + h;
	}

	public float getManhattanDistance(BlockPos blockPos) {
		float f = (float)Math.abs(blockPos.getX() - this.x);
		float g = (float)Math.abs(blockPos.getY() - this.y);
		float h = (float)Math.abs(blockPos.getZ() - this.z);
		return f + g + h;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return new BlockPos(this.x, this.y, this.z);
	}

	public boolean equals(Object object) {
		if (!(object instanceof PathNode)) {
			return false;
		} else {
			PathNode pathNode = (PathNode)object;
			return this.hashCode == pathNode.hashCode && this.x == pathNode.x && this.y == pathNode.y && this.z == pathNode.z;
		}
	}

	public int hashCode() {
		return this.hashCode;
	}

	public boolean isInHeap() {
		return this.heapIndex >= 0;
	}

	public String toString() {
		return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
	}

	@Environment(EnvType.CLIENT)
	public static PathNode fromBuffer(PacketByteBuf packetByteBuf) {
		PathNode pathNode = new PathNode(packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt());
		pathNode.pathLength = packetByteBuf.readFloat();
		pathNode.penalty = packetByteBuf.readFloat();
		pathNode.visited = packetByteBuf.readBoolean();
		pathNode.type = PathNodeType.values()[packetByteBuf.readInt()];
		pathNode.heapWeight = packetByteBuf.readFloat();
		return pathNode;
	}
}
