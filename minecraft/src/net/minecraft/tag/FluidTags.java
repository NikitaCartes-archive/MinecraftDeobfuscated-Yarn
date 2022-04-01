package net.minecraft.tag;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class FluidTags {
	public static final TagKey<Fluid> WATER = register("water");
	public static final TagKey<Fluid> LAVA = register("lava");

	private FluidTags() {
	}

	private static TagKey<Fluid> register(String id) {
		return TagKey.of(Registry.FLUID_KEY, new Identifier(id));
	}
}
