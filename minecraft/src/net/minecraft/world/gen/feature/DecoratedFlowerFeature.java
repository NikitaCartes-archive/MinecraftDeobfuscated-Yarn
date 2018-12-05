package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.config.feature.DecoratedFeatureConfig;

public class DecoratedFlowerFeature extends DecoratedFeature {
	public DecoratedFlowerFeature(Function<Dynamic<?>, ? extends DecoratedFeatureConfig> function) {
		super(function);
	}
}
