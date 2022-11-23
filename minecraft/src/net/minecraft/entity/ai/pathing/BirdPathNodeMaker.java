package net.minecraft.entity.ai.pathing;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;

public class BirdPathNodeMaker extends LandPathNodeMaker {
	private final Long2ObjectMap<PathNodeType> pathNodes = new Long2ObjectOpenHashMap<>();
	private static final float field_41681 = 1.5F;
	private static final int field_41682 = 10;

	@Override
	public void init(ChunkCache cachedWorld, MobEntity entity) {
		super.init(cachedWorld, entity);
		this.pathNodes.clear();
		this.waterPathNodeTypeWeight = entity.getPathfindingPenalty(PathNodeType.WATER);
	}

	@Override
	public void clear() {
		this.entity.setPathfindingPenalty(PathNodeType.WATER, this.waterPathNodeTypeWeight);
		this.pathNodes.clear();
		super.clear();
	}

	@Override
	public PathNode getStart() {
		int i;
		if (this.canSwim() && this.entity.isTouchingWater()) {
			i = this.entity.getBlockY();
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.getX(), (double)i, this.entity.getZ());

			for (BlockState blockState = this.cachedWorld.getBlockState(mutable); blockState.isOf(Blocks.WATER); blockState = this.cachedWorld.getBlockState(mutable)) {
				mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ());
			}
		} else {
			i = MathHelper.floor(this.entity.getY() + 0.5);
		}

		BlockPos blockPos = new BlockPos(this.entity.getX(), (double)i, this.entity.getZ());
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
		PathNodeType pathNodeType = this.getNodeType(this.entity, pos);
		return this.entity.getPathfindingPenalty(pathNodeType) >= 0.0F;
	}

	@Override
	public TargetPathNode getNode(double x, double y, double z) {
		return this.asTargetPathNode(this.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
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

	private PathNodeType getNodeType(int x, int y, int z) {
		return this.pathNodes
			.computeIfAbsent(
				BlockPos.asLong(x, y, z),
				(Long2ObjectFunction<? extends PathNodeType>)(pos -> this.getNodeType(
						this.cachedWorld,
						x,
						y,
						z,
						this.entity,
						this.entityBlockXSize,
						this.entityBlockYSize,
						this.entityBlockZSize,
						this.canOpenDoors(),
						this.canEnterOpenDoors()
					))
			);
	}

	@Override
	public PathNodeType getNodeType(
		BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors
	) {
		EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
		PathNodeType pathNodeType = PathNodeType.BLOCKED;
		BlockPos blockPos = mob.getBlockPos();
		pathNodeType = super.findNearbyNodeTypes(world, x, y, z, sizeX, sizeY, sizeZ, canOpenDoors, canEnterOpenDoors, enumSet, pathNodeType, blockPos);
		if (enumSet.contains(PathNodeType.FENCE)) {
			return PathNodeType.FENCE;
		} else {
			PathNodeType pathNodeType2 = PathNodeType.BLOCKED;

			for (PathNodeType pathNodeType3 : enumSet) {
				if (mob.getPathfindingPenalty(pathNodeType3) < 0.0F) {
					return pathNodeType3;
				}

				if (mob.getPathfindingPenalty(pathNodeType3) >= mob.getPathfindingPenalty(pathNodeType2)) {
					pathNodeType2 = pathNodeType3;
				}
			}

			return pathNodeType == PathNodeType.OPEN && mob.getPathfindingPenalty(pathNodeType2) == 0.0F ? PathNodeType.OPEN : pathNodeType2;
		}
	}

	@Override
	public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		PathNodeType pathNodeType = getCommonNodeType(world, mutable.set(x, y, z));
		if (pathNodeType == PathNodeType.OPEN && y >= world.getBottomY() + 1) {
			PathNodeType pathNodeType2 = getCommonNodeType(world, mutable.set(x, y - 1, z));
			if (pathNodeType2 == PathNodeType.DAMAGE_FIRE || pathNodeType2 == PathNodeType.LAVA) {
				pathNodeType = PathNodeType.DAMAGE_FIRE;
			} else if (pathNodeType2 == PathNodeType.DAMAGE_CACTUS) {
				pathNodeType = PathNodeType.DAMAGE_CACTUS;
			} else if (pathNodeType2 == PathNodeType.DAMAGE_OTHER) {
				pathNodeType = PathNodeType.DAMAGE_OTHER;
			} else if (pathNodeType2 == PathNodeType.COCOA) {
				pathNodeType = PathNodeType.COCOA;
			} else if (pathNodeType2 == PathNodeType.FENCE) {
				if (!mutable.equals(this.entity.getBlockPos())) {
					pathNodeType = PathNodeType.FENCE;
				}
			} else {
				pathNodeType = pathNodeType2 != PathNodeType.WALKABLE && pathNodeType2 != PathNodeType.OPEN && pathNodeType2 != PathNodeType.WATER
					? PathNodeType.WALKABLE
					: PathNodeType.OPEN;
			}
		}

		if (pathNodeType == PathNodeType.WALKABLE || pathNodeType == PathNodeType.OPEN) {
			pathNodeType = getNodeTypeFromNeighbors(world, mutable.set(x, y, z), pathNodeType);
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
		float f = 1.0F;
		Box box = entity.getBoundingBox();
		boolean bl = box.getAverageSideLength() < 1.0;
		if (!bl) {
			return List.of(
				new BlockPos(box.minX, (double)entity.getBlockY(), box.minZ),
				new BlockPos(box.minX, (double)entity.getBlockY(), box.maxZ),
				new BlockPos(box.maxX, (double)entity.getBlockY(), box.minZ),
				new BlockPos(box.maxX, (double)entity.getBlockY(), box.maxZ)
			);
		} else {
			double d = Math.max(0.0, (1.5 - box.getZLength()) / 2.0);
			double e = Math.max(0.0, (1.5 - box.getXLength()) / 2.0);
			double g = Math.max(0.0, (1.5 - box.getYLength()) / 2.0);
			Box box2 = box.expand(e, g, d);
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
