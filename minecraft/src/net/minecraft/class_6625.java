package net.minecraft;

import java.lang.runtime.ObjectMethods;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.registry.DynamicRegistryManager;

public final class class_6625 extends Record {
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",class_6625,"resourceManager;registryAccess;structureManager",class_6625::resourceManager,class_6625::registryAccess,class_6625::structureManager>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",class_6625,"resourceManager;registryAccess;structureManager",class_6625::resourceManager,class_6625::registryAccess,class_6625::structureManager>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",class_6625,"resourceManager;registryAccess;structureManager",class_6625::resourceManager,class_6625::registryAccess,class_6625::structureManager>(
			this, object
		);
	}

	public ResourceManager resourceManager() {
		return this.resourceManager;
	}

	public DynamicRegistryManager registryAccess() {
		return this.registryAccess;
	}

	public StructureManager structureManager() {
		return this.structureManager;
	}
}
