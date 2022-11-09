/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry;

import java.util.List;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;

public enum ServerDynamicRegistryType {
    STATIC,
    WORLDGEN,
    DIMENSIONS,
    RELOADABLE;

    private static final List<ServerDynamicRegistryType> VALUES;
    private static final DynamicRegistryManager.Immutable STATIC_REGISTRY_MANAGER;

    public static CombinedDynamicRegistries<ServerDynamicRegistryType> createCombinedDynamicRegistries() {
        return new CombinedDynamicRegistries<ServerDynamicRegistryType>(VALUES).with(STATIC, STATIC_REGISTRY_MANAGER);
    }

    static {
        VALUES = List.of(ServerDynamicRegistryType.values());
        STATIC_REGISTRY_MANAGER = DynamicRegistryManager.of(Registries.REGISTRIES);
    }
}

