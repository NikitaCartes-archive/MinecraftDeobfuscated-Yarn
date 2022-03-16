package net.minecraft.structure;

import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.DynamicRegistryManager;

public record StructureContext(ResourceManager resourceManager, DynamicRegistryManager registryManager, StructureManager structureTemplateManager) {
	public static StructureContext from(ServerWorld world) {
		MinecraftServer minecraftServer = world.getServer();
		return new StructureContext(minecraftServer.getResourceManager(), minecraftServer.getRegistryManager(), minecraftServer.getStructureManager());
	}
}
