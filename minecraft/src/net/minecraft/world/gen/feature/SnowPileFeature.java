package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;

public class SnowPileFeature extends AbstractPileFeature {
	public SnowPileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	protected BlockState getPileBlockState(IWorld world) {
		return Blocks.SNOW_BLOCK.getDefaultState();
	}
}
