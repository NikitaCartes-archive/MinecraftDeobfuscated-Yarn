package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class HayPileFeature extends AbstractPileFeature {
	public HayPileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected BlockState getPileBlockState(IWorld iWorld) {
		Direction.Axis axis = Direction.Axis.method_16699(iWorld.getRandom());
		return Blocks.field_10359.getDefaultState().with(PillarBlock.AXIS, axis);
	}
}
