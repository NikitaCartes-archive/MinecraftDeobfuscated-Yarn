package net.minecraft.entity.ai.pathing;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
	protected boolean canEnterOpenDoors;
	protected boolean canOpenDoors;
	protected boolean canSwim;

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

	protected PathNode getNode(int x, int y, int z) {
		return this.pathNodeCache.computeIfAbsent(PathNode.hash(x, y, z), l -> new PathNode(x, y, z));
	}

	public abstract PathNode getStart();

	public abstract TargetPathNode getNode(double x, double y, double z);

	public abstract int getSuccessors(PathNode[] successors, PathNode node);

	public abstract PathNodeType getNodeType(
		BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors
	);

	public abstract PathNodeType getNodeType(BlockView world, int x, int y, int z);

	public void setCanEnterOpenDoors(boolean canEnterOpenDoors) {
		this.canEnterOpenDoors = canEnterOpenDoors;
	}

	public void setCanOpenDoors(boolean canOpenDoors) {
		this.canOpenDoors = canOpenDoors;
	}

	public void setCanSwim(boolean canSwim) {
		this.canSwim = canSwim;
	}

	public boolean canEnterOpenDoors() {
		return this.canEnterOpenDoors;
	}

	public boolean canOpenDoors() {
		return this.canOpenDoors;
	}

	public boolean canSwim() {
		return this.canSwim;
	}
}
