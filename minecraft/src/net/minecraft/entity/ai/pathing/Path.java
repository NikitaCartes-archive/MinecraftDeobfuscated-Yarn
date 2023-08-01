package net.minecraft.entity.ai.pathing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Path {
	private final List<PathNode> nodes;
	@Nullable
	private Path.DebugNodeInfo debugNodeInfos;
	private int currentNodeIndex;
	private final BlockPos target;
	private final float manhattanDistanceFromTarget;
	private final boolean reachesTarget;

	public Path(List<PathNode> nodes, BlockPos target, boolean reachesTarget) {
		this.nodes = nodes;
		this.target = target;
		this.manhattanDistanceFromTarget = nodes.isEmpty() ? Float.MAX_VALUE : ((PathNode)this.nodes.get(this.nodes.size() - 1)).getManhattanDistance(this.target);
		this.reachesTarget = reachesTarget;
	}

	public void next() {
		this.currentNodeIndex++;
	}

	public boolean isStart() {
		return this.currentNodeIndex <= 0;
	}

	public boolean isFinished() {
		return this.currentNodeIndex >= this.nodes.size();
	}

	@Nullable
	public PathNode getEnd() {
		return !this.nodes.isEmpty() ? (PathNode)this.nodes.get(this.nodes.size() - 1) : null;
	}

	public PathNode getNode(int index) {
		return (PathNode)this.nodes.get(index);
	}

	public void setLength(int length) {
		if (this.nodes.size() > length) {
			this.nodes.subList(length, this.nodes.size()).clear();
		}
	}

	public void setNode(int index, PathNode node) {
		this.nodes.set(index, node);
	}

	public int getLength() {
		return this.nodes.size();
	}

	public int getCurrentNodeIndex() {
		return this.currentNodeIndex;
	}

	public void setCurrentNodeIndex(int nodeIndex) {
		this.currentNodeIndex = nodeIndex;
	}

	public Vec3d getNodePosition(Entity entity, int index) {
		PathNode pathNode = (PathNode)this.nodes.get(index);
		double d = (double)pathNode.x + (double)((int)(entity.getWidth() + 1.0F)) * 0.5;
		double e = (double)pathNode.y;
		double f = (double)pathNode.z + (double)((int)(entity.getWidth() + 1.0F)) * 0.5;
		return new Vec3d(d, e, f);
	}

	public BlockPos getNodePos(int index) {
		return ((PathNode)this.nodes.get(index)).getBlockPos();
	}

	public Vec3d getNodePosition(Entity entity) {
		return this.getNodePosition(entity, this.currentNodeIndex);
	}

	public BlockPos getCurrentNodePos() {
		return ((PathNode)this.nodes.get(this.currentNodeIndex)).getBlockPos();
	}

	public PathNode getCurrentNode() {
		return (PathNode)this.nodes.get(this.currentNodeIndex);
	}

	@Nullable
	public PathNode getLastNode() {
		return this.currentNodeIndex > 0 ? (PathNode)this.nodes.get(this.currentNodeIndex - 1) : null;
	}

	public boolean equalsPath(@Nullable Path o) {
		if (o == null) {
			return false;
		} else if (o.nodes.size() != this.nodes.size()) {
			return false;
		} else {
			for (int i = 0; i < this.nodes.size(); i++) {
				PathNode pathNode = (PathNode)this.nodes.get(i);
				PathNode pathNode2 = (PathNode)o.nodes.get(i);
				if (pathNode.x != pathNode2.x || pathNode.y != pathNode2.y || pathNode.z != pathNode2.z) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean reachesTarget() {
		return this.reachesTarget;
	}

	@Debug
	void setDebugInfo(PathNode[] debugNodes, PathNode[] debugSecondNodes, Set<TargetPathNode> debugTargetNodes) {
		this.debugNodeInfos = new Path.DebugNodeInfo(debugNodes, debugSecondNodes, debugTargetNodes);
	}

	@Nullable
	public Path.DebugNodeInfo getDebugNodeInfos() {
		return this.debugNodeInfos;
	}

	public void toBuf(PacketByteBuf buf) {
		if (this.debugNodeInfos != null && !this.debugNodeInfos.targetNodes.isEmpty()) {
			buf.writeBoolean(this.reachesTarget);
			buf.writeInt(this.currentNodeIndex);
			buf.writeBlockPos(this.target);
			buf.writeCollection(this.nodes, (bufx, node) -> node.write(bufx));
			this.debugNodeInfos.write(buf);
		}
	}

	public static Path fromBuf(PacketByteBuf buf) {
		boolean bl = buf.readBoolean();
		int i = buf.readInt();
		BlockPos blockPos = buf.readBlockPos();
		List<PathNode> list = buf.readList(PathNode::fromBuf);
		Path.DebugNodeInfo debugNodeInfo = Path.DebugNodeInfo.fromBuf(buf);
		Path path = new Path(list, blockPos, bl);
		path.debugNodeInfos = debugNodeInfo;
		path.currentNodeIndex = i;
		return path;
	}

	public String toString() {
		return "Path(length=" + this.nodes.size() + ")";
	}

	public BlockPos getTarget() {
		return this.target;
	}

	public float getManhattanDistanceFromTarget() {
		return this.manhattanDistanceFromTarget;
	}

	static PathNode[] nodesFromBuf(PacketByteBuf buf) {
		PathNode[] pathNodes = new PathNode[buf.readVarInt()];

		for (int i = 0; i < pathNodes.length; i++) {
			pathNodes[i] = PathNode.fromBuf(buf);
		}

		return pathNodes;
	}

	static void write(PacketByteBuf buf, PathNode[] nodes) {
		buf.writeVarInt(nodes.length);

		for (PathNode pathNode : nodes) {
			pathNode.write(buf);
		}
	}

	public Path copy() {
		Path path = new Path(this.nodes, this.target, this.reachesTarget);
		path.debugNodeInfos = this.debugNodeInfos;
		path.currentNodeIndex = this.currentNodeIndex;
		return path;
	}

	public static record DebugNodeInfo(PathNode[] openSet, PathNode[] closedSet, Set<TargetPathNode> targetNodes) {

		public void write(PacketByteBuf buf) {
			buf.writeCollection(this.targetNodes, (bufx, node) -> node.write(bufx));
			Path.write(buf, this.openSet);
			Path.write(buf, this.closedSet);
		}

		public static Path.DebugNodeInfo fromBuf(PacketByteBuf buf) {
			HashSet<TargetPathNode> hashSet = buf.readCollection(HashSet::new, TargetPathNode::fromBuffer);
			PathNode[] pathNodes = Path.nodesFromBuf(buf);
			PathNode[] pathNodes2 = Path.nodesFromBuf(buf);
			return new Path.DebugNodeInfo(pathNodes, pathNodes2, hashSet);
		}
	}
}
