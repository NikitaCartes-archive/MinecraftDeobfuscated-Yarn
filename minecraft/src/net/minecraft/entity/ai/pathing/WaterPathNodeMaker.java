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
import net.minecraft.tag.FluidTags;
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

	@Nullable
	@Override
	public PathNode getStart() {
		return super.getNode(
			MathHelper.floor(this.entity.getBoundingBox().minX),
			MathHelper.floor(this.entity.getBoundingBox().minY + 0.5),
			MathHelper.floor(this.entity.getBoundingBox().minZ)
		);
	}

	@Nullable
	@Override
	public TargetPathNode getNode(double x, double y, double z) {
		return this.asTargetPathNode(super.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
	}

	@Override
	public int getSuccessors(PathNode[] successors, PathNode node) {
		int i = 0;
		Map<Direction, PathNode> map = Maps.newEnumMap(Direction.class);

		for (Direction direction : Direction.values()) {
			PathNode pathNode = this.getNode(node.x + direction.getOffsetX(), node.y + direction.getOffsetY(), node.z + direction.getOffsetZ());
			map.put(direction, pathNode);
			if (this.hasNotVisited(pathNode)) {
				successors[i++] = pathNode;
			}
		}

		for (Direction direction2 : Direction.Type.HORIZONTAL) {
			Direction direction3 = direction2.rotateYClockwise();
			PathNode pathNode2 = this.getNode(
				node.x + direction2.getOffsetX() + direction3.getOffsetX(), node.y, node.z + direction2.getOffsetZ() + direction3.getOffsetZ()
			);
			if (this.method_38488(pathNode2, (PathNode)map.get(direction2), (PathNode)map.get(direction3))) {
				successors[i++] = pathNode2;
			}
		}

		return i;
	}

	protected boolean hasNotVisited(@Nullable PathNode pathNode) {
		return pathNode != null && !pathNode.visited;
	}

	protected boolean method_38488(@Nullable PathNode pathNode, @Nullable PathNode pathNode2, @Nullable PathNode pathNode3) {
		return this.hasNotVisited(pathNode) && pathNode2 != null && pathNode2.penalty >= 0.0F && pathNode3 != null && pathNode3.penalty >= 0.0F;
	}

	@Nullable
	@Override
	protected PathNode getNode(int x, int y, int z) {
		PathNode pathNode = null;
		PathNodeType pathNodeType = this.addPathNodePos(x, y, z);
		if (this.canJumpOutOfWater && pathNodeType == PathNodeType.BREACH || pathNodeType == PathNodeType.WATER) {
			float f = this.entity.getPathfindingPenalty(pathNodeType);
			if (f >= 0.0F) {
				pathNode = super.getNode(x, y, z);
				if (pathNode != null) {
					pathNode.type = pathNodeType;
					pathNode.penalty = Math.max(pathNode.penalty, f);
					if (this.cachedWorld.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
						pathNode.penalty += 8.0F;
					}
				}
			}
		}

		return pathNode;
	}

	protected PathNodeType addPathNodePos(int x, int y, int z) {
		return this.nodePosToType
			.computeIfAbsent(BlockPos.asLong(x, y, z), (Long2ObjectFunction<? extends PathNodeType>)(l -> this.getDefaultNodeType(this.cachedWorld, x, y, z)));
	}

	@Override
	public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
		return this.getNodeType(
			world, x, y, z, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, this.canOpenDoors(), this.canEnterOpenDoors()
		);
	}

	@Override
	public PathNodeType getNodeType(
		BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = x; i < x + sizeX; i++) {
			for (int j = y; j < y + sizeY; j++) {
				for (int k = z; k < z + sizeZ; k++) {
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
