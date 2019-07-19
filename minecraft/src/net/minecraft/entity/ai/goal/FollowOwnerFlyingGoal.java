package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class FollowOwnerFlyingGoal extends FollowOwnerGoal {
	public FollowOwnerFlyingGoal(TameableEntity tameableEntity, double d, float f, float g) {
		super(tameableEntity, d, f, g);
	}

	@Override
	protected boolean method_6263(BlockPos pos) {
		BlockState blockState = this.world.getBlockState(pos);
		return (blockState.hasSolidTopSurface(this.world, pos, this.tameable) || blockState.matches(BlockTags.LEAVES))
			&& this.world.isAir(pos.up())
			&& this.world.isAir(pos.up(2));
	}
}
