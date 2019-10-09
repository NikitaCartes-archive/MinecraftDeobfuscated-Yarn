package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public abstract class class_4629 implements DynamicSerializable {
	protected final class_4630<?> field_21222;

	protected class_4629(class_4630<?> arg) {
		this.field_21222 = arg;
	}

	public abstract void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random);
}
