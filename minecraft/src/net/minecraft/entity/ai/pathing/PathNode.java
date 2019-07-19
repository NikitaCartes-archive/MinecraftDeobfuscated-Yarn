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
	public float field_36;
	public float field_34;
	public float heapWeight;
	public PathNode field_35;
	public boolean field_42;
	public float field_46;
	public float field_43;
	public PathNodeType type = PathNodeType.BLOCKED;

	public PathNode(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.hashCode = hash(x, y, z);
	}

	public PathNode copyWithNewPosition(int x, int y, int z) {
		PathNode pathNode = new PathNode(x, y, z);
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

	public static int hash(int x, int y, int z) {
		return y & 0xFF | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
	}

	public float getDistance(PathNode node) {
		float f = (float)(node.x - this.x);
		float g = (float)(node.y - this.y);
		float h = (float)(node.z - this.z);
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public float getSquaredDistance(PathNode node) {
		float f = (float)(node.x - this.x);
		float g = (float)(node.y - this.y);
		float h = (float)(node.z - this.z);
		return f * f + g * g + h * h;
	}

	public float method_21653(PathNode pathNode) {
		float f = (float)Math.abs(pathNode.x - this.x);
		float g = (float)Math.abs(pathNode.y - this.y);
		float h = (float)Math.abs(pathNode.z - this.z);
		return f + g + h;
	}

	public float method_21654(BlockPos blockPos) {
		float f = (float)Math.abs(blockPos.getX() - this.x);
		float g = (float)Math.abs(blockPos.getY() - this.y);
		float h = (float)Math.abs(blockPos.getZ() - this.z);
		return f + g + h;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos method_21652() {
		return new BlockPos(this.x, this.y, this.z);
	}

	public boolean equals(Object o) {
		if (!(o instanceof PathNode)) {
			return false;
		} else {
			PathNode pathNode = (PathNode)o;
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
	public static PathNode fromBuffer(PacketByteBuf buffer) {
		PathNode pathNode = new PathNode(buffer.readInt(), buffer.readInt(), buffer.readInt());
		pathNode.field_46 = buffer.readFloat();
		pathNode.field_43 = buffer.readFloat();
		pathNode.field_42 = buffer.readBoolean();
		pathNode.type = PathNodeType.values()[buffer.readInt()];
		pathNode.heapWeight = buffer.readFloat();
		return pathNode;
	}
}
