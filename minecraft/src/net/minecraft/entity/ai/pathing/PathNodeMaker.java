package net.minecraft.entity.ai.pathing;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.class_4459;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;

public abstract class PathNodeMaker {
	protected ChunkCache field_20622;
	protected MobEntity entity;
	protected final Int2ObjectMap<PathNode> pathNodeCache = new Int2ObjectOpenHashMap<>();
	protected int field_31;
	protected int field_30;
	protected int field_28;
	protected boolean entersOpenDoors;
	protected boolean pathsThroughDoors;
	protected boolean swims;

	public void init(ChunkCache chunkCache, MobEntity mobEntity) {
		this.field_20622 = chunkCache;
		this.entity = mobEntity;
		this.pathNodeCache.clear();
		this.field_31 = MathHelper.floor(mobEntity.getWidth() + 1.0F);
		this.field_30 = MathHelper.floor(mobEntity.getHeight() + 1.0F);
		this.field_28 = MathHelper.floor(mobEntity.getWidth() + 1.0F);
	}

	public void clear() {
		this.field_20622 = null;
		this.entity = null;
	}

	protected PathNode getPathNode(int i, int j, int k) {
		return this.pathNodeCache.computeIfAbsent(PathNode.calculateHashCode(i, j, k), l -> new PathNode(i, j, k));
	}

	public abstract PathNode getStart();

	public abstract class_4459 getPathNode(double d, double e, double f);

	public abstract int getPathNodes(PathNode[] pathNodes, PathNode pathNode);

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
