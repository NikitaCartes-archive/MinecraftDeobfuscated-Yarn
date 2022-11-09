package net.minecraft.client.network;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;

@Environment(EnvType.CLIENT)
public enum ClientDynamicRegistryType {
	STATIC,
	REMOTE;

	private static final List<ClientDynamicRegistryType> VALUES = List.of(values());
	private static final DynamicRegistryManager.Immutable STATIC_REGISTRY_MANAGER = DynamicRegistryManager.of(Registries.REGISTRIES);

	public static CombinedDynamicRegistries<ClientDynamicRegistryType> createCombinedDynamicRegistries() {
		return new CombinedDynamicRegistries<ClientDynamicRegistryType>(VALUES).with(STATIC, STATIC_REGISTRY_MANAGER);
	}
}
