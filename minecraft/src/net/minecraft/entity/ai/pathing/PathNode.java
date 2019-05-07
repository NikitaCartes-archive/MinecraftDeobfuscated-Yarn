package net.minecraft.entity.ai.pathing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class PathNode {
	public final int x;
	public final int y;
	public final int z;
	private final int hashCode;
	public int heapIndex = -1;
	public float field_36;
	public float field_34;
	public float heapWeight;
	public PathNode field_35;
	public boolean field_42;
	public float field_46;
	public float field_43;
	public PathNodeType type = PathNodeType.field_22;

	public PathNode(int i, int j, int k) {
		this.x = i;
		this.y = j;
		this.z = k;
		this.hashCode = calculateHashCode(i, j, k);
	}

	public PathNode copyWithNewPosition(int i, int j, int k) {
		PathNode pathNode = new PathNode(i, j, k);
		pathNode.heapIndex = this.heapIndex;
		pathNode.field_36 = this.field_36;
		pathNode.field_34 = this.field_34;
		pathNode.heapWeight = this.heapWeight;
		pathNode.field_35 = this.field_35;
		pathNode.field_42 = this.field_42;
		pathNode.field_46 = this.field_46;
		pathNode.field_43 = this.field_43;
		pathNode.type = this.type;
		return pathNode;
	}

	public static int calculateHashCode(int i, int j, int k) {
		return j & 0xFF | (i & 32767) << 8 | (k & 32767) << 24 | (i < 0 ? Integer.MIN_VALUE : 0) | (k < 0 ? 32768 : 0);
	}

	public float distance(PathNode pathNode) {
		float f = (float)(pathNode.x - this.x);
		float g = (float)(pathNode.y - this.y);
		float h = (float)(pathNode.z - this.z);
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public float distanceSquared(PathNode pathNode) {
		float f = (float)(pathNode.x - this.x);
		float g = (float)(pathNode.y - this.y);
		float h = (float)(pathNode.z - this.z);
		return f * f + g * g + h * h;
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
		return this.x + ", " + this.y + ", " + this.z;
	}

	@Environment(EnvType.CLIENT)
	public static PathNode fromBuffer(PacketByteBuf packetByteBuf) {
		PathNode pathNode = new PathNode(packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt());
		pathNode.field_46 = packetByteBuf.readFloat();
		pathNode.field_43 = packetByteBuf.readFloat();
		pathNode.field_42 = packetByteBuf.readBoolean();
		pathNode.type = PathNodeType.values()[packetByteBuf.readInt()];
		pathNode.heapWeight = packetByteBuf.readFloat();
		return pathNode;
	}
}
