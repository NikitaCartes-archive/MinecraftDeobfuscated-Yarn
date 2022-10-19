package net.minecraft.server;

import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.util.registry.CombinedDynamicRegistries;
import net.minecraft.util.registry.ServerDynamicRegistryType;
import net.minecraft.world.SaveProperties;

public record SaveLoader(
	LifecycledResourceManager resourceManager,
	DataPackContents dataPackContents,
	CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries,
	SaveProperties saveProperties
) implements AutoCloseable {
	public void close() {
		this.resourceManager.close();
	}
}
