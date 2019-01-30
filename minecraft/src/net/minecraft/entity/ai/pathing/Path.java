package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class Path {
	private final PathNode[] nodes;
	private PathNode[] field_57 = new PathNode[0];
	private PathNode[] field_55 = new PathNode[0];
	private PathNode field_56;
	private int currentNodeIndex;
	private int length;

	public Path(PathNode[] pathNodes) {
		this.nodes = pathNodes;
		this.length = pathNodes.length;
	}

	public void next() {
		this.currentNodeIndex++;
	}

	public boolean isFinished() {
		return this.currentNodeIndex >= this.length;
	}

	@Nullable
	public PathNode getEnd() {
		return this.length > 0 ? this.nodes[this.length - 1] : null;
	}

	public PathNode getNode(int i) {
		return this.nodes[i];
	}

	public void setNode(int i, PathNode pathNode) {
		this.nodes[i] = pathNode;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int i) {
		this.length = i;
	}

	public int getCurrentNodeIndex() {
		return this.currentNodeIndex;
	}

	public void setCurrentNodeIndex(int i) {
		this.currentNodeIndex = i;
	}

	public Vec3d getNodePosition(Entity entity, int i) {
		double d = (double)this.nodes[i].x + (double)((int)(entity.getWidth() + 1.0F)) * 0.5;
		double e = (double)this.nodes[i].y;
		double f = (double)this.nodes[i].z + (double)((int)(entity.getWidth() + 1.0F)) * 0.5;
		return new Vec3d(d, e, f);
	}

	public Vec3d getNodePosition(Entity entity) {
		return this.getNodePosition(entity, this.currentNodeIndex);
	}

	public Vec3d getCurrentPosition() {
		PathNode pathNode = this.nodes[this.currentNodeIndex];
		return new Vec3d((double)pathNode.x, (double)pathNode.y, (double)pathNode.z);
	}

	public boolean equalsPath(Path path) {
		if (path == null) {
			return false;
		} else if (path.nodes.length != this.nodes.length) {
			return false;
		} else {
			for (int i = 0; i < this.nodes.length; i++) {
				if (this.nodes[i].x != path.nodes[i].x || this.nodes[i].y != path.nodes[i].y || this.nodes[i].z != path.nodes[i].z) {
					return false;
				}
			}

			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	public PathNode[] method_43() {
		return this.field_57;
	}

	@Environment(EnvType.CLIENT)
	public PathNode[] method_37() {
		return this.field_55;
	}

	@Nullable
	public PathNode method_48() {
		return this.field_56;
	}

	@Environment(EnvType.CLIENT)
	public static Path method_34(PacketByteBuf packetByteBuf) {
		int i = packetByteBuf.readInt();
		PathNode pathNode = PathNode.method_28(packetByteBuf);
		PathNode[] pathNodes = new PathNode[packetByteBuf.readInt()];

		for (int j = 0; j < pathNodes.length; j++) {
			pathNodes[j] = PathNode.method_28(packetByteBuf);
		}

		PathNode[] pathNodes2 = new PathNode[packetByteBuf.readInt()];

		for (int k = 0; k < pathNodes2.length; k++) {
			pathNodes2[k] = PathNode.method_28(packetByteBuf);
		}

		PathNode[] pathNodes3 = new PathNode[packetByteBuf.readInt()];

		for (int l = 0; l < pathNodes3.length; l++) {
			pathNodes3[l] = PathNode.method_28(packetByteBuf);
		}

		Path path = new Path(pathNodes);
		path.field_57 = pathNodes2;
		path.field_55 = pathNodes3;
		path.field_56 = pathNode;
		path.currentNodeIndex = i;
		return path;
	}
}
