package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class Path {
	private final List<PathNode> nodes;
	private PathNode[] field_57 = new PathNode[0];
	private PathNode[] field_55 = new PathNode[0];
	private PathNode field_56;
	private int currentNodeIndex;

	public Path(List<PathNode> list) {
		this.nodes = list;
	}

	public void next() {
		this.currentNodeIndex++;
	}

	public boolean isFinished() {
		return this.currentNodeIndex >= this.nodes.size();
	}

	@Nullable
	public PathNode getEnd() {
		return !this.nodes.isEmpty() ? (PathNode)this.nodes.get(this.nodes.size() - 1) : null;
	}

	public PathNode getNode(int i) {
		return (PathNode)this.nodes.get(i);
	}

	public List<PathNode> getNodes() {
		return this.nodes;
	}

	public void setLength(int i) {
		if (this.nodes.size() > i) {
			this.nodes.subList(i, this.nodes.size()).clear();
		}
	}

	public void setNode(int i, PathNode pathNode) {
		this.nodes.set(i, pathNode);
	}

	public int getLength() {
		return this.nodes.size();
	}

	public int getCurrentNodeIndex() {
		return this.currentNodeIndex;
	}

	public void setCurrentNodeIndex(int i) {
		this.currentNodeIndex = i;
	}

	public Vec3d getNodePosition(Entity entity, int i) {
		PathNode pathNode = (PathNode)this.nodes.get(i);
		double d = (double)pathNode.x + (double)((int)(entity.getWidth() + 1.0F)) * 0.5;
		double e = (double)pathNode.y;
		double f = (double)pathNode.z + (double)((int)(entity.getWidth() + 1.0F)) * 0.5;
		return new Vec3d(d, e, f);
	}

	public Vec3d getNodePosition(Entity entity) {
		return this.getNodePosition(entity, this.currentNodeIndex);
	}

	public Vec3d getCurrentPosition() {
		PathNode pathNode = (PathNode)this.nodes.get(this.currentNodeIndex);
		return new Vec3d((double)pathNode.x, (double)pathNode.y, (double)pathNode.z);
	}

	public boolean equalsPath(@Nullable Path path) {
		if (path == null) {
			return false;
		} else if (path.nodes.size() != this.nodes.size()) {
			return false;
		} else {
			for (int i = 0; i < this.nodes.size(); i++) {
				PathNode pathNode = (PathNode)this.nodes.get(i);
				PathNode pathNode2 = (PathNode)path.nodes.get(i);
				if (pathNode.x != pathNode2.x || pathNode.y != pathNode2.y || pathNode.z != pathNode2.z) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean method_19315() {
		PathNode pathNode = this.getEnd();
		return pathNode != null && this.method_19313(pathNode.getPos());
	}

	public boolean method_19313(Vec3d vec3d) {
		PathNode pathNode = this.getEnd();
		return pathNode == null ? false : pathNode.x == (int)vec3d.x && pathNode.y == (int)vec3d.y && pathNode.z == (int)vec3d.z;
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
	public static Path fromBuffer(PacketByteBuf packetByteBuf) {
		int i = packetByteBuf.readInt();
		PathNode pathNode = PathNode.fromBuffer(packetByteBuf);
		List<PathNode> list = Lists.<PathNode>newArrayList();
		int j = packetByteBuf.readInt();

		for (int k = 0; k < j; k++) {
			list.add(PathNode.fromBuffer(packetByteBuf));
		}

		PathNode[] pathNodes = new PathNode[packetByteBuf.readInt()];

		for (int l = 0; l < pathNodes.length; l++) {
			pathNodes[l] = PathNode.fromBuffer(packetByteBuf);
		}

		PathNode[] pathNodes2 = new PathNode[packetByteBuf.readInt()];

		for (int m = 0; m < pathNodes2.length; m++) {
			pathNodes2[m] = PathNode.fromBuffer(packetByteBuf);
		}

		Path path = new Path(list);
		path.field_57 = pathNodes;
		path.field_55 = pathNodes2;
		path.field_56 = pathNode;
		path.currentNodeIndex = i;
		return path;
	}

	public String toString() {
		return "Path(length=" + this.nodes.size() + ")";
	}
}
