package net.minecraft.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.registry.Registry;

public class FluidTagProvider extends AbstractTagProvider<Fluid> {
	public FluidTagProvider(DataGenerator root) {
		super(root, Registry.FLUID);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(FluidTags.WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
		this.getOrCreateTagBuilder(FluidTags.LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
	}

	@Override
	public String getName() {
		return "Fluid Tags";
	}
}
