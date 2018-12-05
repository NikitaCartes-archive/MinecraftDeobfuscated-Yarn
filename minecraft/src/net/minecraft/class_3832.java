package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.HayBaleFeature;

public class class_3832 extends HayBaleFeature {
	public class_3832(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected BlockState method_16843(IWorld iWorld) {
		return iWorld.getRandom().nextInt(7) == 0 ? Blocks.field_10384.getDefaultState() : Blocks.field_10225.getDefaultState();
	}
}
