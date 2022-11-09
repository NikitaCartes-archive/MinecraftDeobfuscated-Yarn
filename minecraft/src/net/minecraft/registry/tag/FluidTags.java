package net.minecraft.registry.tag;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class FluidTags {
	public static final TagKey<Fluid> WATER = of("water");
	public static final TagKey<Fluid> LAVA = of("lava");

	private FluidTags() {
	}

	private static TagKey<Fluid> of(String id) {
		return TagKey.of(RegistryKeys.FLUID, new Identifier(id));
	}
}
