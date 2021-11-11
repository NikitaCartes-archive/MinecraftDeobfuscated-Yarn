package net.minecraft.structure;

import java.lang.runtime.ObjectMethods;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.DynamicRegistryManager;

public final class StructureContext extends Record {
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",StructureContext,"resourceManager;registryAccess;structureManager",StructureContext::resourceManager,StructureContext::registryManager,StructureContext::structureManager>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",StructureContext,"resourceManager;registryAccess;structureManager",StructureContext::resourceManager,StructureContext::registryManager,StructureContext::structureManager>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",StructureContext,"resourceManager;registryAccess;structureManager",StructureContext::resourceManager,StructureContext::registryManager,StructureContext::structureManager>(
			this, object
		);
	}

	public ResourceManager resourceManager() {
		return this.resourceManager;
	}

	public DynamicRegistryManager registryManager() {
		return this.registryManager;
	}

	public StructureManager structureManager() {
		return this.structureManager;
	}
}
