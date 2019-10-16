package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.placer.BlockPlacerType;

public abstract class class_4629 implements DynamicSerializable {
	protected final BlockPlacerType<?> field_21222;

	protected class_4629(BlockPlacerType<?> blockPlacerType) {
		this.field_21222 = blockPlacerType;
	}

	public abstract void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random);
}
