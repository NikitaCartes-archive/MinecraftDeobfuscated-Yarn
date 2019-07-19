package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class HayPileFeature extends AbstractPileFeature {
	public HayPileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	protected BlockState getPileBlockState(IWorld world) {
		Direction.Axis axis = Direction.Axis.method_16699(world.getRandom());
		return Blocks.HAY_BLOCK.getDefaultState().with(PillarBlock.AXIS, axis);
	}
}
