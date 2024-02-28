package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;

public class PathContext {
	private final CollisionView world;
	@Nullable
	private final PathNodeTypeCache nodeTypeCache;
	private final BlockPos entityPos;
	private final BlockPos.Mutable lastNodePos = new BlockPos.Mutable();

	public PathContext(CollisionView world, MobEntity entity) {
		this.world = world;
		if (entity.getWorld() instanceof ServerWorld serverWorld) {
			this.nodeTypeCache = serverWorld.getPathNodeTypeCache();
		} else {
			this.nodeTypeCache = null;
		}

		this.entityPos = entity.getBlockPos();
	}

	public PathNodeType getNodeType(int x, int y, int z) {
		BlockPos blockPos = this.lastNodePos.set(x, y, z);
		return this.nodeTypeCache == null ? LandPathNodeMaker.getCommonNodeType(this.world, blockPos) : this.nodeTypeCache.add(this.world, blockPos);
	}

	public BlockState getBlockState(BlockPos pos) {
		return this.world.getBlockState(pos);
	}

	public CollisionView getWorld() {
		return this.world;
	}

	public BlockPos getEntityPos() {
		return this.entityPos;
	}
}
