package net.minecraft.tag;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

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

	public static Set<Identifier> method_29216(TagContainer<Fluid> tagContainer) {
		return ACCESSOR.method_29224(tagContainer);
	}
}
