package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;

public abstract class class_4651 implements DynamicSerializable {
	protected final class_4652<?> field_21304;

	protected class_4651(class_4652<?> arg) {
		this.field_21304 = arg;
	}

	public abstract BlockState method_23455(Random random, BlockPos blockPos);
}
