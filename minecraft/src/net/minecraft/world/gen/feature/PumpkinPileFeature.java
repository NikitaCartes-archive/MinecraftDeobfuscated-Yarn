package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;

public class PumpkinPileFeature extends AbstractPileFeature {
	public PumpkinPileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected BlockState getPileBlockState(IWorld iWorld) {
		return iWorld.getRandom().nextFloat() < 0.95F ? Blocks.field_10261.getDefaultState() : Blocks.field_10009.getDefaultState();
	}
}
