package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_4624;
import net.minecraft.class_4638;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class DefaultFlowerFeature extends class_4624<class_4638> {
	public DefaultFlowerFeature(Function<Dynamic<?>, ? extends class_4638> function) {
		super(function);
	}

	public boolean method_23390(IWorld iWorld, BlockPos blockPos, class_4638 arg) {
		return !arg.field_21240.contains(iWorld.getBlockState(blockPos));
	}

	public int method_23391(class_4638 arg) {
		return arg.field_21241;
	}

	public BlockPos method_23392(Random random, BlockPos blockPos, class_4638 arg) {
		return blockPos.add(
			random.nextInt(arg.field_21242) - random.nextInt(arg.field_21242),
			random.nextInt(arg.field_21243) - random.nextInt(arg.field_21243),
			random.nextInt(arg.field_21244) - random.nextInt(arg.field_21244)
		);
	}

	public BlockState method_23393(Random random, BlockPos blockPos, class_4638 arg) {
		return arg.field_21237.method_23455(random, blockPos);
	}
}
