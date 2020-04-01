package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;

public abstract class class_5101 extends Carver<ProbabilityConfig> {
	public class_5101(Function<Dynamic<?>, ? extends ProbabilityConfig> function, int i) {
		super(function, i);
	}

	public ProbabilityConfig method_26583(Random random) {
		return new ProbabilityConfig(random.nextFloat() / 2.0F);
	}
}
