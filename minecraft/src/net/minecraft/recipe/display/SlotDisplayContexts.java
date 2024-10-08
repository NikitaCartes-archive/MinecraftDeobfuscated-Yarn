package net.minecraft.recipe.display;

import net.minecraft.item.FuelRegistry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import net.minecraft.world.World;

public class SlotDisplayContexts {
	public static final ContextParameter<FuelRegistry> FUEL_REGISTRY = ContextParameter.of("fuel_values");
	public static final ContextParameter<RegistryWrapper.WrapperLookup> REGISTRIES = ContextParameter.of("registries");
	public static final ContextType CONTEXT_TYPE = new ContextType.Builder().allow(FUEL_REGISTRY).allow(REGISTRIES).build();

	public static ContextParameterMap createParameters(World world) {
		return new ContextParameterMap.Builder().add(FUEL_REGISTRY, world.getFuelRegistry()).add(REGISTRIES, world.getRegistryManager()).build(CONTEXT_TYPE);
	}
}
