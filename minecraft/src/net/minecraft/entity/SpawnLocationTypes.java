package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;

public interface SpawnLocationTypes {
	SpawnLocation UNRESTRICTED = (world, pos, entityType) -> true;
	SpawnLocation IN_WATER = (world, pos, entityType) -> {
		if (entityType != null && world.getWorldBorder().contains(pos)) {
			BlockPos blockPos = pos.up();
			return world.getFluidState(pos).isIn(FluidTags.WATER) && !world.getBlockState(blockPos).isSolidBlock(world, blockPos);
		} else {
			return false;
		}
	};
	SpawnLocation IN_LAVA = (world, pos, entityType) -> entityType != null && world.getWorldBorder().contains(pos)
			? world.getFluidState(pos).isIn(FluidTags.LAVA)
			: false;
	SpawnLocation ON_GROUND = new SpawnLocation() {
		@Override
		public boolean isSpawnPositionOk(WorldView worldView, BlockPos blockPos, @Nullable EntityType<?> entityType) {
			if (entityType != null && worldView.getWorldBorder().contains(blockPos)) {
				BlockPos blockPos2 = blockPos.up();
				BlockPos blockPos3 = blockPos.down();
				BlockState blockState = worldView.getBlockState(blockPos3);
				return !blockState.allowsSpawning(worldView, blockPos3, entityType)
					? false
					: this.isClearForSpawn(worldView, blockPos, entityType) && this.isClearForSpawn(worldView, blockPos2, entityType);
			} else {
				return false;
			}
		}

		private boolean isClearForSpawn(WorldView world, BlockPos pos, EntityType<?> entityType) {
			BlockState blockState = world.getBlockState(pos);
			return SpawnHelper.isClearForSpawn(world, pos, blockState, blockState.getFluidState(), entityType);
		}

		@Override
		public BlockPos adjustPosition(WorldView world, BlockPos pos) {
			BlockPos blockPos = pos.down();
			return world.getBlockState(blockPos).canPathfindThrough(NavigationType.LAND) ? blockPos : pos;
		}
	};
}
