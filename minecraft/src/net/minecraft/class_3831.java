package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.HayBaleFeature;

public class class_3831 extends HayBaleFeature {
	public class_3831(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected BlockState method_16843(IWorld iWorld) {
		Direction.Axis axis = Direction.Axis.method_16699(iWorld.getRandom());
		return Blocks.field_10359.getDefaultState().with(PillarBlock.AXIS, axis);
	}
}
