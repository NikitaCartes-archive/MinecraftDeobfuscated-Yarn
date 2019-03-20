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
	protected boolean method_6263(int i, int j, int k, int l, int m) {
		BlockPos blockPos = new BlockPos(i + l, k - 1, j + m);
		BlockState blockState = this.world.getBlockState(blockPos);
		return (blockState.hasSolidTopSurface(this.world, blockPos) || blockState.matches(BlockTags.field_15503))
			&& this.world.isAir(new BlockPos(i + l, k, j + m))
			&& this.world.isAir(new BlockPos(i + l, k + 1, j + m));
	}
}
