package net.minecraft.structure;

import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.DynamicRegistryManager;

public record StructureContext() {
	private final ResourceManager resourceManager;
	private final DynamicRegistryManager registryManager;
	private final StructureManager structureManager;

	public StructureContext(ResourceManager resourceManager, DynamicRegistryManager dynamicRegistryManager, StructureManager structureManager) {
		this.resourceManager = resourceManager;
		this.registryManager = dynamicRegistryManager;
		this.structureManager = structureManager;
	}

	public static StructureContext from(ServerWorld world) {
		MinecraftServer minecraftServer = world.getServer();
		return new StructureContext(minecraftServer.getResourceManager(), minecraftServer.getRegistryManager(), minecraftServer.getStructureManager());
	}
}
