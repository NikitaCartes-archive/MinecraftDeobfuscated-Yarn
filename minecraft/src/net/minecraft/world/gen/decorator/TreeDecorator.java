package net.minecraft.world.gen.decorator;

import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableWorld;

public abstract class TreeDecorator implements DynamicSerializable {
	protected final TreeDecoratorType<?> field_21319;

	protected TreeDecorator(TreeDecoratorType<?> treeDecoratorType) {
		this.field_21319 = treeDecoratorType;
	}

	public abstract void method_23469(IWorld iWorld, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox blockBox);

	protected void method_23471(ModifiableWorld modifiableWorld, BlockPos blockPos, BooleanProperty booleanProperty, Set<BlockPos> set, BlockBox blockBox) {
		this.method_23470(modifiableWorld, blockPos, Blocks.VINE.getDefaultState().with(booleanProperty, Boolean.valueOf(true)), set, blockBox);
	}

	protected void method_23470(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState, Set<BlockPos> set, BlockBox blockBox) {
		modifiableWorld.setBlockState(blockPos, blockState, 19);
		set.add(blockPos);
		blockBox.encompass(new BlockBox(blockPos, blockPos));
	}
}
