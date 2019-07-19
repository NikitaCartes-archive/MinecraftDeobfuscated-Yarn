package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;

public class MelonPileFeature extends AbstractPileFeature {
	public MelonPileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	protected BlockState getPileBlockState(IWorld world) {
		return Blocks.MELON.getDefaultState();
	}
}
