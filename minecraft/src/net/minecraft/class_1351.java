package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class class_1351 extends FollowOwnerGoal {
	public class_1351(TameableEntity tameableEntity, double d, float f, float g) {
		super(tameableEntity, d, f, g);
	}

	@Override
	protected boolean method_6263(int i, int j, int k, int l, int m) {
		BlockPos blockPos = new BlockPos(i + l, k - 1, j + m);
		BlockState blockState = this.field_6445.method_8320(blockPos);
		return (blockState.method_11631(this.field_6445, blockPos) || blockState.method_11602(BlockTags.field_15503))
			&& this.field_6445.method_8623(new BlockPos(i + l, k, j + m))
			&& this.field_6445.method_8623(new BlockPos(i + l, k + 1, j + m));
	}
}
