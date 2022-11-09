package net.minecraft.server;

import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.LifecycledResourceManager;
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
