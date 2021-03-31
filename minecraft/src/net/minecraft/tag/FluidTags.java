package net.minecraft.tag;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;

public final class FluidTags {
	protected static final RequiredTagList<Fluid> REQUIRED_TAGS = RequiredTagListRegistry.register(Registry.FLUID_KEY, "tags/fluids");
	private static final List<Tag<Fluid>> TAGS = Lists.<Tag<Fluid>>newArrayList();
	public static final Tag.Identified<Fluid> WATER = register("water");
	public static final Tag.Identified<Fluid> LAVA = register("lava");

	private FluidTags() {
	}

	private static Tag.Identified<Fluid> register(String id) {
		Tag.Identified<Fluid> identified = REQUIRED_TAGS.add(id);
		TAGS.add(identified);
		return identified;
	}

	public static TagGroup<Fluid> getTagGroup() {
		return REQUIRED_TAGS.getGroup();
	}

	@Deprecated
	public static List<Tag<Fluid>> getTags() {
		return TAGS;
	}
}
