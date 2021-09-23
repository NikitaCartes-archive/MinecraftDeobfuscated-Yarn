package net.minecraft;

import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.registry.DynamicRegistryManager;

public record class_6625() {
	private final ResourceManager resourceManager;
	private final DynamicRegistryManager registryAccess;
	private final StructureManager structureManager;

	public class_6625(ResourceManager resourceManager, DynamicRegistryManager dynamicRegistryManager, StructureManager structureManager) {
		this.resourceManager = resourceManager;
		this.registryAccess = dynamicRegistryManager;
		this.structureManager = structureManager;
	}

	public static class_6625 method_38713(ServerWorld serverWorld) {
		MinecraftServer minecraftServer = serverWorld.getServer();
		return new class_6625(minecraftServer.getResourceManager(), minecraftServer.getRegistryManager(), minecraftServer.getStructureManager());
	}
}
