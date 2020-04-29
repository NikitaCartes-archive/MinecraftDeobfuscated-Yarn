package net.minecraft.tag;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;

public class FluidTags {
	private static final GlobalTagAccessor<Fluid> ACCESSOR = new GlobalTagAccessor<>();
	public static final Tag.Identified<Fluid> WATER = register("water");
	public static final Tag.Identified<Fluid> LAVA = register("lava");

	private static Tag.Identified<Fluid> register(String id) {
		return ACCESSOR.get(id);
	}

	public static void setContainer(TagContainer<Fluid> container) {
		ACCESSOR.setContainer(container);
	}

	@Environment(EnvType.CLIENT)
	public static void markReady() {
		ACCESSOR.markReady();
	}

	public static TagContainer<Fluid> getContainer() {
		return ACCESSOR.getContainer();
	}
}
