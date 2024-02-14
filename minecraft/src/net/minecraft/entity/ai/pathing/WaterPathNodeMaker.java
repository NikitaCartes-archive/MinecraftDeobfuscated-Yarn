package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;

public class WaterPathNodeMaker extends PathNodeMaker {
	private final boolean canJumpOutOfWater;
	private final Long2ObjectMap<PathNodeType> nodePosToType = new Long2ObjectOpenHashMap<>();

	public WaterPathNodeMaker(boolean canJumpOutOfWater) {
		this.canJumpOutOfWater = canJumpOutOfWater;
	}

	@Override
	public void init(ChunkCache cachedWorld, MobEntity entity) {
		super.init(cachedWorld, entity);
		this.nodePosToType.clear();
	}

	@Override
	public void clear() {
		super.clear();
		this.nodePosToType.clear();
	}

	@Override
	public PathNode getStart() {
		return this.getNode(
			MathHelper.floor(this.entity.getBoundingBox().minX),
			MathHelper.floor(this.entity.getBoundingBox().minY + 0.5),
			MathHelper.floor(this.entity.getBoundingBox().minZ)
		);
	}

	@Override
	public TargetPathNode getNode(double x, double y, double z) {
		return this.createNode(x, y, z);
	}

	@Override
	public int getSuccessors(PathNode[] successors, PathNode node) {
		int i = 0;
		Map<Direction, PathNode> map = Maps.newEnumMap(Direction.class);

		for (Direction direction : Direction.values()) {
			PathNode pathNode = this.getPassableNode(node.x + direction.getOffsetX(), node.y + direction.getOffsetY(), node.z + direction.getOffsetZ());
			map.put(direction, pathNode);
			if (this.hasNotVisited(pathNode)) {
				successors[i++] = pathNode;
			}
		}

		for (Direction direction2 : Direction.Type.HORIZONTAL) {
			Direction direction3 = direction2.rotateYClockwise();
			if (hasPenalty((PathNode)map.get(direction2)) && hasPenalty((PathNode)map.get(direction3))) {
				PathNode pathNode2 = this.getPassableNode(
					node.x + direction2.getOffsetX() + direction3.getOffsetX(), node.y, node.z + direction2.getOffsetZ() + direction3.getOffsetZ()
				);
				if (this.hasNotVisited(pathNode2)) {
					successors[i++] = pathNode2;
				}
			}
		}

		return i;
	}

	protected boolean hasNotVisited(@Nullable PathNode node) {
		return node != null && !node.visited;
	}

	private static boolean hasPenalty(@Nullable PathNode node) {
		return node != null && node.penalty >= 0.0F;
	}

	@Nullable
	protected PathNode getPassableNode(int x, int y, int z) {
		PathNode pathNode = null;
		PathNodeType pathNodeType = this.addPathNodePos(x, y, z);
		if (this.canJumpOutOfWater && pathNodeType == PathNodeType.BREACH || pathNodeType == PathNodeType.WATER) {
			float f = this.entity.getPathfindingPenalty(pathNodeType);
			if (f >= 0.0F) {
				pathNode = this.getNode(x, y, z);
				pathNode.type = pathNodeType;
				pathNode.penalty = Math.max(pathNode.penalty, f);
				if (this.cachedWorld.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
					pathNode.penalty += 8.0F;
				}
			}
		}

		return pathNode;
	}

	protected PathNodeType addPathNodePos(int x, int y, int z) {
		return this.nodePosToType
			.computeIfAbsent(BlockPos.asLong(x, y, z), (Long2ObjectFunction<? extends PathNodeType>)(pos -> this.getDefaultNodeType(this.cachedWorld, x, y, z)));
	}

	@Override
	public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
		return this.getNodeType(world, x, y, z, this.entity);
	}

	@Override
	public PathNodeType getNodeType(BlockView world, int x, int y, int z, MobEntity mob) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = x; i < x + this.entityBlockXSize; i++) {
			for (int j = y; j < y + this.entityBlockYSize; j++) {
				for (int k = z; k < z + this.entityBlockZSize; k++) {
					FluidState fluidState = world.getFluidState(mutable.set(i, j, k));
					BlockState blockState = world.getBlockState(mutable.set(i, j, k));
					if (fluidState.isEmpty() && blockState.canPathfindThrough(world, mutable.down(), NavigationType.WATER) && blockState.isAir()) {
						return PathNodeType.BREACH;
					}

					if (!fluidState.isIn(FluidTags.WATER)) {
						return PathNodeType.BLOCKED;
					}
				}
			}
		}

		BlockState blockState2 = world.getBlockState(mutable);
		return blockState2.canPathfindThrough(world, mutable, NavigationType.WATER) ? PathNodeType.WATER : PathNodeType.BLOCKED;
	}
}
