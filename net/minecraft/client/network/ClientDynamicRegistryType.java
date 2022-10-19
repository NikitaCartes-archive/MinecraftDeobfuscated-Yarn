/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.CombinedDynamicRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;

@Environment(value=EnvType.CLIENT)
public enum ClientDynamicRegistryType {
    STATIC,
    REMOTE;

    private static final List<ClientDynamicRegistryType> VALUES;
    private static final DynamicRegistryManager.Immutable STATIC_REGISTRY_MANAGER;

    public static CombinedDynamicRegistries<ClientDynamicRegistryType> createCombinedDynamicRegistries() {
        return new CombinedDynamicRegistries<ClientDynamicRegistryType>(VALUES).with(STATIC, STATIC_REGISTRY_MANAGER);
    }

    static {
        VALUES = List.of(ClientDynamicRegistryType.values());
        STATIC_REGISTRY_MANAGER = DynamicRegistryManager.of(Registry.REGISTRIES);
    }
}

