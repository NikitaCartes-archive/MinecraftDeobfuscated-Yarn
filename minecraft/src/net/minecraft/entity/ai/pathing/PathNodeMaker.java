package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public abstract class PathNodeMaker {
	protected BlockView blockView;
	protected MobEntity entity;
	protected final IntHashMap<PathNode> pathNodeCache = new IntHashMap<>();
	protected int field_31;
	protected int field_30;
	protected int field_28;
	protected boolean entersOpenDoors;
	protected boolean pathsThroughDoors;
	protected boolean swims;

	public void init(BlockView blockView, MobEntity mobEntity) {
		this.blockView = blockView;
		this.entity = mobEntity;
		this.pathNodeCache.clear();
		this.field_31 = MathHelper.floor(mobEntity.width + 1.0F);
		this.field_30 = MathHelper.floor(mobEntity.height + 1.0F);
		this.field_28 = MathHelper.floor(mobEntity.width + 1.0F);
	}

	public void clear() {
		this.blockView = null;
		this.entity = null;
	}

	protected PathNode getPathNode(int i, int j, int k) {
		int l = PathNode.calculateHashCode(i, j, k);
		PathNode pathNode = this.pathNodeCache.get(l);
		if (pathNode == null) {
			pathNode = new PathNode(i, j, k);
			this.pathNodeCache.put(l, pathNode);
		}

		return pathNode;
	}

	public abstract PathNode getStart();

	public abstract PathNode getPathNode(double d, double e, double f);

	public abstract int getPathNodes(PathNode[] pathNodes, PathNode pathNode, PathNode pathNode2, float f);

	public abstract PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2);

	public abstract PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k);

	public void setCanEnterOpenDoors(boolean bl) {
		this.entersOpenDoors = bl;
	}

	public void setCanPathThroughDoors(boolean bl) {
		this.pathsThroughDoors = bl;
	}

	public void setCanSwim(boolean bl) {
		this.swims = bl;
	}

	public boolean canEnterOpenDoors() {
		return this.entersOpenDoors;
	}

	public boolean canPathThroughDoors() {
		return this.pathsThroughDoors;
	}

	public boolean canSwim() {
		return this.swims;
	}
}
