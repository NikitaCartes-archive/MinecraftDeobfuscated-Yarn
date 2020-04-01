package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class DecoratedFlowerFeature extends DecoratedFeature {
	public DecoratedFlowerFeature(Function<Dynamic<?>, ? extends DecoratedFeatureConfig> function, Function<Random, ? extends DecoratedFeatureConfig> function2) {
		super(function, function2);
	}
}
