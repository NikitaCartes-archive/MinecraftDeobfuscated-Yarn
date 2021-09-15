package net.minecraft.world.gen;

import javax.annotation.Nullable;
import net.minecraft.class_6568;
import net.minecraft.class_6583;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.BlockPosRandomDeriver;

public class DeepslateBlockSource implements class_6583 {
	private static final int DEFAULT_MIN_Y = -8;
	private static final int MAX_Y = 0;
	private final BlockPosRandomDeriver field_34587;
	private final BlockState deepslateState;

	public DeepslateBlockSource(BlockPosRandomDeriver blockPosRandomDeriver, BlockState blockState) {
		this.field_34587 = blockPosRandomDeriver;
		this.deepslateState = blockState;
	}

	@Nullable
	@Override
	public BlockState apply(class_6568 arg, int i, int j, int k) {
		if (j < -8) {
			return this.deepslateState;
		} else if (j > 0) {
			return null;
		} else {
			double d = (double)MathHelper.lerpFromProgress((float)j, -8.0F, 0.0F, 1.0F, 0.0F);
			AbstractRandom abstractRandom = this.field_34587.createRandom(i, j, k);
			return (double)abstractRandom.nextFloat() < d ? this.deepslateState : null;
		}
	}
}
