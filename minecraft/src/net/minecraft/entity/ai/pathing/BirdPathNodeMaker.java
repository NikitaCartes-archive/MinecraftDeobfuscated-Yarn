package net.minecraft.entity.ai.pathing;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkCache;

public class BirdPathNodeMaker extends LandPathNodeMaker {
	private final Long2ObjectMap<PathNodeType> pathNodes = new Long2ObjectOpenHashMap<>();
	private static final float field_49843 = 1.0F;
	private static final float field_41681 = 1.1F;
	private static final int field_41682 = 10;

	@Override
	public void init(ChunkCache cachedWorld, MobEntity entity) {
		super.init(cachedWorld, entity);
		this.pathNodes.clear();
		entity.onStartPathfinding();
	}

	@Override
	public void clear() {
		this.entity.onFinishPathfinding();
		this.pathNodes.clear();
		super.clear();
	}

	@Override
	public PathNode getStart() {
		int i;
		if (this.canSwim() && this.entity.isTouchingWater()) {
			i = this.entity.getBlockY();
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.getX(), (double)i, this.entity.getZ());

			for (BlockState blockState = this.context.getBlockState(mutable); blockState.isOf(Blocks.WATER); blockState = this.context.getBlockState(mutable)) {
				mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ());
			}
		} else {
			i = MathHelper.floor(this.entity.getY() + 0.5);
		}

		BlockPos blockPos = BlockPos.ofFloored(this.entity.getX(), (double)i, this.entity.getZ());
		if (!this.canPathThrough(blockPos)) {
			for (BlockPos blockPos2 : this.getPotentialEscapePositions(this.entity)) {
				if (this.canPathThrough(blockPos2)) {
					return super.getStart(blockPos2);
				}
			}
		}

		return super.getStart(blockPos);
	}

	@Override
	protected boolean canPathThrough(BlockPos pos) {
		PathNodeType pathNodeType = this.getNodeType(pos.getX(), pos.getY(), pos.getZ());
		return this.entity.getPathfindingPenalty(pathNodeType) >= 0.0F;
	}

	@Override
	public TargetPathNode getNode(double x, double y, double z) {
		return this.createNode(x, y, z);
	}

	@Override
	public int getSuccessors(PathNode[] successors, PathNode node) {
		int i = 0;
		PathNode pathNode = this.getPassableNode(node.x, node.y, node.z + 1);
		if (this.unvisited(pathNode)) {
			successors[i++] = pathNode;
		}

		PathNode pathNode2 = this.getPassableNode(node.x - 1, node.y, node.z);
		if (this.unvisited(pathNode2)) {
			successors[i++] = pathNode2;
		}

		PathNode pathNode3 = this.getPassableNode(node.x + 1, node.y, node.z);
		if (this.unvisited(pathNode3)) {
			successors[i++] = pathNode3;
		}

		PathNode pathNode4 = this.getPassableNode(node.x, node.y, node.z - 1);
		if (this.unvisited(pathNode4)) {
			successors[i++] = pathNode4;
		}

		PathNode pathNode5 = this.getPassableNode(node.x, node.y + 1, node.z);
		if (this.unvisited(pathNode5)) {
			successors[i++] = pathNode5;
		}

		PathNode pathNode6 = this.getPassableNode(node.x, node.y - 1, node.z);
		if (this.unvisited(pathNode6)) {
			successors[i++] = pathNode6;
		}

		PathNode pathNode7 = this.getPassableNode(node.x, node.y + 1, node.z + 1);
		if (this.unvisited(pathNode7) && this.isPassable(pathNode) && this.isPassable(pathNode5)) {
			successors[i++] = pathNode7;
		}

		PathNode pathNode8 = this.getPassableNode(node.x - 1, node.y + 1, node.z);
		if (this.unvisited(pathNode8) && this.isPassable(pathNode2) && this.isPassable(pathNode5)) {
			successors[i++] = pathNode8;
		}

		PathNode pathNode9 = this.getPassableNode(node.x + 1, node.y + 1, node.z);
		if (this.unvisited(pathNode9) && this.isPassable(pathNode3) && this.isPassable(pathNode5)) {
			successors[i++] = pathNode9;
		}

		PathNode pathNode10 = this.getPassableNode(node.x, node.y + 1, node.z - 1);
		if (this.unvisited(pathNode10) && this.isPassable(pathNode4) && this.isPassable(pathNode5)) {
			successors[i++] = pathNode10;
		}

		PathNode pathNode11 = this.getPassableNode(node.x, node.y - 1, node.z + 1);
		if (this.unvisited(pathNode11) && this.isPassable(pathNode) && this.isPassable(pathNode6)) {
			successors[i++] = pathNode11;
		}

		PathNode pathNode12 = this.getPassableNode(node.x - 1, node.y - 1, node.z);
		if (this.unvisited(pathNode12) && this.isPassable(pathNode2) && this.isPassable(pathNode6)) {
			successors[i++] = pathNode12;
		}

		PathNode pathNode13 = this.getPassableNode(node.x + 1, node.y - 1, node.z);
		if (this.unvisited(pathNode13) && this.isPassable(pathNode3) && this.isPassable(pathNode6)) {
			successors[i++] = pathNode13;
		}

		PathNode pathNode14 = this.getPassableNode(node.x, node.y - 1, node.z - 1);
		if (this.unvisited(pathNode14) && this.isPassable(pathNode4) && this.isPassable(pathNode6)) {
			successors[i++] = pathNode14;
		}

		PathNode pathNode15 = this.getPassableNode(node.x + 1, node.y, node.z - 1);
		if (this.unvisited(pathNode15) && this.isPassable(pathNode4) && this.isPassable(pathNode3)) {
			successors[i++] = pathNode15;
		}

		PathNode pathNode16 = this.getPassableNode(node.x + 1, node.y, node.z + 1);
		if (this.unvisited(pathNode16) && this.isPassable(pathNode) && this.isPassable(pathNode3)) {
			successors[i++] = pathNode16;
		}

		PathNode pathNode17 = this.getPassableNode(node.x - 1, node.y, node.z - 1);
		if (this.unvisited(pathNode17) && this.isPassable(pathNode4) && this.isPassable(pathNode2)) {
			successors[i++] = pathNode17;
		}

		PathNode pathNode18 = this.getPassableNode(node.x - 1, node.y, node.z + 1);
		if (this.unvisited(pathNode18) && this.isPassable(pathNode) && this.isPassable(pathNode2)) {
			successors[i++] = pathNode18;
		}

		PathNode pathNode19 = this.getPassableNode(node.x + 1, node.y + 1, node.z - 1);
		if (this.unvisited(pathNode19)
			&& this.isPassable(pathNode15)
			&& this.isPassable(pathNode4)
			&& this.isPassable(pathNode3)
			&& this.isPassable(pathNode5)
			&& this.isPassable(pathNode10)
			&& this.isPassable(pathNode9)) {
			successors[i++] = pathNode19;
		}

		PathNode pathNode20 = this.getPassableNode(node.x + 1, node.y + 1, node.z + 1);
		if (this.unvisited(pathNode20)
			&& this.isPassable(pathNode16)
			&& this.isPassable(pathNode)
			&& this.isPassable(pathNode3)
			&& this.isPassable(pathNode5)
			&& this.isPassable(pathNode7)
			&& this.isPassable(pathNode9)) {
			successors[i++] = pathNode20;
		}

		PathNode pathNode21 = this.getPassableNode(node.x - 1, node.y + 1, node.z - 1);
		if (this.unvisited(pathNode21)
			&& this.isPassable(pathNode17)
			&& this.isPassable(pathNode4)
			&& this.isPassable(pathNode2)
			&& this.isPassable(pathNode5)
			&& this.isPassable(pathNode10)
			&& this.isPassable(pathNode8)) {
			successors[i++] = pathNode21;
		}

		PathNode pathNode22 = this.getPassableNode(node.x - 1, node.y + 1, node.z + 1);
		if (this.unvisited(pathNode22)
			&& this.isPassable(pathNode18)
			&& this.isPassable(pathNode)
			&& this.isPassable(pathNode2)
			&& this.isPassable(pathNode5)
			&& this.isPassable(pathNode7)
			&& this.isPassable(pathNode8)) {
			successors[i++] = pathNode22;
		}

		PathNode pathNode23 = this.getPassableNode(node.x + 1, node.y - 1, node.z - 1);
		if (this.unvisited(pathNode23)
			&& this.isPassable(pathNode15)
			&& this.isPassable(pathNode4)
			&& this.isPassable(pathNode3)
			&& this.isPassable(pathNode6)
			&& this.isPassable(pathNode14)
			&& this.isPassable(pathNode13)) {
			successors[i++] = pathNode23;
		}

		PathNode pathNode24 = this.getPassableNode(node.x + 1, node.y - 1, node.z + 1);
		if (this.unvisited(pathNode24)
			&& this.isPassable(pathNode16)
			&& this.isPassable(pathNode)
			&& this.isPassable(pathNode3)
			&& this.isPassable(pathNode6)
			&& this.isPassable(pathNode11)
			&& this.isPassable(pathNode13)) {
			successors[i++] = pathNode24;
		}

		PathNode pathNode25 = this.getPassableNode(node.x - 1, node.y - 1, node.z - 1);
		if (this.unvisited(pathNode25)
			&& this.isPassable(pathNode17)
			&& this.isPassable(pathNode4)
			&& this.isPassable(pathNode2)
			&& this.isPassable(pathNode6)
			&& this.isPassable(pathNode14)
			&& this.isPassable(pathNode12)) {
			successors[i++] = pathNode25;
		}

		PathNode pathNode26 = this.getPassableNode(node.x - 1, node.y - 1, node.z + 1);
		if (this.unvisited(pathNode26)
			&& this.isPassable(pathNode18)
			&& this.isPassable(pathNode)
			&& this.isPassable(pathNode2)
			&& this.isPassable(pathNode6)
			&& this.isPassable(pathNode11)
			&& this.isPassable(pathNode12)) {
			successors[i++] = pathNode26;
		}

		return i;
	}

	private boolean isPassable(@Nullable PathNode node) {
		return node != null && node.penalty >= 0.0F;
	}

	private boolean unvisited(@Nullable PathNode node) {
		return node != null && !node.visited;
	}

	@Nullable
	protected PathNode getPassableNode(int x, int y, int z) {
		PathNode pathNode = null;
		PathNodeType pathNodeType = this.getNodeType(x, y, z);
		float f = this.entity.getPathfindingPenalty(pathNodeType);
		if (f >= 0.0F) {
			pathNode = this.getNode(x, y, z);
			pathNode.type = pathNodeType;
			pathNode.penalty = Math.max(pathNode.penalty, f);
			if (pathNodeType == PathNodeType.WALKABLE) {
				pathNode.penalty++;
			}
		}

		return pathNode;
	}

	@Override
	protected PathNodeType getNodeType(int x, int y, int z) {
		return this.pathNodes
			.computeIfAbsent(BlockPos.asLong(x, y, z), (Long2ObjectFunction<? extends PathNodeType>)(pos -> this.getNodeType(this.context, x, y, z, this.entity)));
	}

	@Override
	public PathNodeType getDefaultNodeType(PathContext context, int x, int y, int z) {
		PathNodeType pathNodeType = context.getNodeType(x, y, z);
		if (pathNodeType == PathNodeType.OPEN && y >= context.getWorld().getBottomY() + 1) {
			BlockPos blockPos = new BlockPos(x, y - 1, z);
			PathNodeType pathNodeType2 = context.getNodeType(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			if (pathNodeType2 == PathNodeType.DAMAGE_FIRE || pathNodeType2 == PathNodeType.LAVA) {
				pathNodeType = PathNodeType.DAMAGE_FIRE;
			} else if (pathNodeType2 == PathNodeType.DAMAGE_OTHER) {
				pathNodeType = PathNodeType.DAMAGE_OTHER;
			} else if (pathNodeType2 == PathNodeType.COCOA) {
				pathNodeType = PathNodeType.COCOA;
			} else if (pathNodeType2 == PathNodeType.FENCE) {
				if (!blockPos.equals(context.getEntityPos())) {
					pathNodeType = PathNodeType.FENCE;
				}
			} else {
				pathNodeType = pathNodeType2 != PathNodeType.WALKABLE && pathNodeType2 != PathNodeType.OPEN && pathNodeType2 != PathNodeType.WATER
					? PathNodeType.WALKABLE
					: PathNodeType.OPEN;
			}
		}

		if (pathNodeType == PathNodeType.WALKABLE || pathNodeType == PathNodeType.OPEN) {
			pathNodeType = getNodeTypeFromNeighbors(context, x, y, z, pathNodeType);
		}

		return pathNodeType;
	}

	/**
	 * {@return the iterable of positions that the entity should try to pathfind to when escaping}
	 * 
	 * @apiNote This is used when the entity {@linkplain #canPathThrough cannot path through}
	 * the current position (e.g. because it is dangerous).
	 */
	private Iterable<BlockPos> getPotentialEscapePositions(MobEntity entity) {
		Box box = entity.getBoundingBox();
		boolean bl = box.getAverageSideLength() < 1.0;
		if (!bl) {
			return List.of(
				BlockPos.ofFloored(box.minX, (double)entity.getBlockY(), box.minZ),
				BlockPos.ofFloored(box.minX, (double)entity.getBlockY(), box.maxZ),
				BlockPos.ofFloored(box.maxX, (double)entity.getBlockY(), box.minZ),
				BlockPos.ofFloored(box.maxX, (double)entity.getBlockY(), box.maxZ)
			);
		} else {
			double d = Math.max(0.0, 1.1F - box.getLengthZ());
			double e = Math.max(0.0, 1.1F - box.getLengthX());
			double f = Math.max(0.0, 1.1F - box.getLengthY());
			Box box2 = box.expand(e, f, d);
			return BlockPos.iterateRandomly(
				entity.getRandom(),
				10,
				MathHelper.floor(box2.minX),
				MathHelper.floor(box2.minY),
				MathHelper.floor(box2.minZ),
				MathHelper.floor(box2.maxX),
				MathHelper.floor(box2.maxY),
				MathHelper.floor(box2.maxZ)
			);
		}
	}
}
