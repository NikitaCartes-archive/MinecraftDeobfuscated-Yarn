package net.minecraft.tag;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class FluidTags {
	public static final TagKey<Fluid> WATER = of("water");
	public static final TagKey<Fluid> LAVA = of("lava");

	private FluidTags() {
	}

	private static TagKey<Fluid> of(String id) {
		return TagKey.of(Registry.FLUID_KEY, new Identifier(id));
	}
}
