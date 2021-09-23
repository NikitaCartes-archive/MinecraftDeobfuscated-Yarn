/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.registry.DynamicRegistryManager;

public record class_6625(ResourceManager resourceManager, DynamicRegistryManager registryAccess, StructureManager structureManager) {
    public static class_6625 method_38713(ServerWorld serverWorld) {
        MinecraftServer minecraftServer = serverWorld.getServer();
        return new class_6625(minecraftServer.getResourceManager(), minecraftServer.getRegistryManager(), minecraftServer.getStructureManager());
    }
}

