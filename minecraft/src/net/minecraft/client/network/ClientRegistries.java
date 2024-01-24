package net.minecraft.client.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.tag.TagPacketSerializer;

@Environment(EnvType.CLIENT)
public class ClientRegistries {
	@Nullable
	private ClientRegistries.DynamicRegistries dynamicRegistries;
	@Nullable
	private ClientRegistries.Tags tags;

	public void putDynamicRegistry(RegistryKey<? extends Registry<?>> registryRef, List<SerializableRegistries.SerializedRegistryEntry> entries) {
		if (this.dynamicRegistries == null) {
			this.dynamicRegistries = new ClientRegistries.DynamicRegistries();
		}

		this.dynamicRegistries.put(registryRef, entries);
	}

	public void putTags(Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> tags) {
		if (this.tags == null) {
			this.tags = new ClientRegistries.Tags();
		}

		tags.forEach(this.tags::put);
	}

	public DynamicRegistryManager.Immutable createRegistryManager(DynamicRegistryManager precedingRegistryManager, boolean local) {
		CombinedDynamicRegistries<ClientDynamicRegistryType> combinedDynamicRegistries = ClientDynamicRegistryType.createCombinedDynamicRegistries();
		DynamicRegistryManager dynamicRegistryManager;
		if (this.dynamicRegistries != null) {
			DynamicRegistryManager.Immutable immutable = this.dynamicRegistries
				.load(combinedDynamicRegistries.getPrecedingRegistryManagers(ClientDynamicRegistryType.REMOTE))
				.toImmutable();
			dynamicRegistryManager = combinedDynamicRegistries.with(ClientDynamicRegistryType.REMOTE, immutable).getCombinedRegistryManager();
		} else {
			dynamicRegistryManager = precedingRegistryManager;
		}

		if (this.tags != null && !local) {
			combinedDynamicRegistries.get(ClientDynamicRegistryType.STATIC).streamAllRegistries().forEach(entry -> entry.value().clearTags());
			this.tags.load(dynamicRegistryManager);
		}

		return dynamicRegistryManager.toImmutable();
	}

	@Environment(EnvType.CLIENT)
	static class DynamicRegistries {
		private final Map<RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>> dynamicRegistries = new HashMap();

		public void put(RegistryKey<? extends Registry<?>> registryRef, List<SerializableRegistries.SerializedRegistryEntry> entries) {
			((List)this.dynamicRegistries.computeIfAbsent(registryRef, registries -> new ArrayList())).addAll(entries);
		}

		public DynamicRegistryManager load(DynamicRegistryManager precedingRegistryManager) {
			return RegistryLoader.loadFromNetwork(this.dynamicRegistries, precedingRegistryManager, RegistryLoader.SYNCED_REGISTRIES);
		}
	}

	@Environment(EnvType.CLIENT)
	static class Tags {
		private final Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> tagsByRegistry = new HashMap();

		public void put(RegistryKey<? extends Registry<?>> registryRef, TagPacketSerializer.Serialized serialized) {
			this.tagsByRegistry.put(registryRef, serialized);
		}

		public void load(DynamicRegistryManager registryManager) {
			this.tagsByRegistry.forEach((registryRef, serialized) -> serialized.loadTo(registryManager.get(registryRef)));
		}
	}
}
