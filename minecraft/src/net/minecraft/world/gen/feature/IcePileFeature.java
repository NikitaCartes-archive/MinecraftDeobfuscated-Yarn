package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;

public class IcePileFeature extends AbstractPileFeature {
	public IcePileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected BlockState getPileBlockState(IWorld iWorld) {
		return iWorld.getRandom().nextInt(7) == 0 ? Blocks.field_10384.getDefaultState() : Blocks.field_10225.getDefaultState();
	}
}
