package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class FlyToOwnerGoal extends FollowOwnerGoal {
	public FlyToOwnerGoal(TameableEntity tameableEntity, double d, float f, float g) {
		super(tameableEntity, d, f, g);
	}

	@Override
	protected boolean method_6263(BlockPos blockPos) {
		BlockState blockState = this.world.getBlockState(blockPos);
		return (blockState.hasSolidTopSurface(this.world, blockPos, this.caller) || blockState.matches(BlockTags.field_15503))
			&& this.world.isAir(blockPos.up())
			&& this.world.isAir(blockPos.up(2));
	}
}
