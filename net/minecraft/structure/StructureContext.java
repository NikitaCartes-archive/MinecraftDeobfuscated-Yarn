/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;

public record StructureContext(ResourceManager resourceManager, DynamicRegistryManager registryManager, StructureTemplateManager structureTemplateManager) {
    public static StructureContext from(ServerWorld world) {
        MinecraftServer minecraftServer = world.getServer();
        return new StructureContext(minecraftServer.getResourceManager(), minecraftServer.getRegistryManager(), minecraftServer.getStructureTemplateManager());
    }
}

