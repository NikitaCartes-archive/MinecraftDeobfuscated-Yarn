package net.minecraft.data.server;

import java.nio.file.Path;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidTagsProvider extends AbstractTagProvider<Fluid> {
	public FluidTagsProvider(DataGenerator root) {
		super(root, Registry.FLUID);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(FluidTags.WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
		this.getOrCreateTagBuilder(FluidTags.LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
	}

	@Override
	protected Path getOutput(Identifier id) {
		return this.root.getOutput().resolve("data/" + id.getNamespace() + "/tags/fluids/" + id.getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Fluid Tags";
	}
}
