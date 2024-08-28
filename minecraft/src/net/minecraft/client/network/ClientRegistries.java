package net.minecraft.client.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.ResourceFactory;

@Environment(EnvType.CLIENT)
public class ClientRegistries {
	@Nullable
	private ClientRegistries.DynamicRegistries dynamicRegistries;
	@Nullable
	private ClientRegistries.class_9954 tagLoader;

	public void putDynamicRegistry(RegistryKey<? extends Registry<?>> registryRef, List<SerializableRegistries.SerializedRegistryEntry> entries) {
		if (this.dynamicRegistries == null) {
			this.dynamicRegistries = new ClientRegistries.DynamicRegistries();
		}

		this.dynamicRegistries.put(registryRef, entries);
	}

	public void putTags(Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> tags) {
		if (this.tagLoader == null) {
			this.tagLoader = new ClientRegistries.class_9954();
		}

		tags.forEach(this.tagLoader::method_62162);
	}

	private static <T> Registry.PendingTagLoad<T> method_62160(
		DynamicRegistryManager.Immutable immutable, RegistryKey<? extends Registry<? extends T>> registryKey, TagPacketSerializer.Serialized serialized
	) {
		Registry<T> registry = immutable.getOrThrow(registryKey);
		return registry.startTagReload(serialized.toRegistryTags(registry));
	}

	private DynamicRegistryManager method_62155(ResourceFactory resourceFactory, ClientRegistries.DynamicRegistries dynamicRegistries, boolean bl) {
		CombinedDynamicRegistries<ClientDynamicRegistryType> combinedDynamicRegistries = ClientDynamicRegistryType.createCombinedDynamicRegistries();
		DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries.getPrecedingRegistryManagers(ClientDynamicRegistryType.REMOTE);
		Map<RegistryKey<? extends Registry<?>>, RegistryLoader.ElementsAndTags> map = new HashMap();
		dynamicRegistries.dynamicRegistries
			.forEach((registryKey, listx) -> map.put(registryKey, new RegistryLoader.ElementsAndTags(listx, TagPacketSerializer.Serialized.NONE)));
		List<Registry.PendingTagLoad<?>> list = new ArrayList();
		if (this.tagLoader != null) {
			this.tagLoader.method_62163((registryKey, serialized) -> {
				if (!serialized.isEmpty()) {
					if (SerializableRegistries.isSynced(registryKey)) {
						map.compute(registryKey, (registryKeyx, elementsAndTags) -> {
							List<SerializableRegistries.SerializedRegistryEntry> listxx = elementsAndTags != null ? elementsAndTags.elements() : List.of();
							return new RegistryLoader.ElementsAndTags(listxx, serialized);
						});
					} else if (!bl) {
						list.add(method_62160(immutable, registryKey, serialized));
					}
				}
			});
		}

		List<RegistryWrapper.Impl<?>> list2 = TagGroupLoader.collectRegistries(immutable, list);
		DynamicRegistryManager.Immutable immutable2 = RegistryLoader.loadFromNetwork(map, resourceFactory, list2, RegistryLoader.SYNCED_REGISTRIES).toImmutable();
		DynamicRegistryManager dynamicRegistryManager = combinedDynamicRegistries.with(ClientDynamicRegistryType.REMOTE, immutable2).getCombinedRegistryManager();
		list.forEach(Registry.PendingTagLoad::apply);
		return dynamicRegistryManager;
	}

	private void method_62157(ClientRegistries.class_9954 arg, DynamicRegistryManager.Immutable immutable, boolean bl) {
		arg.method_62163((registryKey, serialized) -> {
			if (bl || SerializableRegistries.isSynced(registryKey)) {
				method_62160(immutable, registryKey, serialized).apply();
			}
		});
	}

	public DynamicRegistryManager.Immutable createRegistryManager(ResourceFactory resourceFactory, DynamicRegistryManager.Immutable immutable, boolean local) {
		DynamicRegistryManager dynamicRegistryManager;
		if (this.dynamicRegistries != null) {
			dynamicRegistryManager = this.method_62155(resourceFactory, this.dynamicRegistries, local);
		} else {
			if (this.tagLoader != null) {
				this.method_62157(this.tagLoader, immutable, !local);
			}

			dynamicRegistryManager = immutable;
		}

		return dynamicRegistryManager.toImmutable();
	}

	@Environment(EnvType.CLIENT)
	static class DynamicRegistries {
		final Map<RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>> dynamicRegistries = new HashMap();

		public void put(RegistryKey<? extends Registry<?>> registryRef, List<SerializableRegistries.SerializedRegistryEntry> entries) {
			((List)this.dynamicRegistries.computeIfAbsent(registryRef, registries -> new ArrayList())).addAll(entries);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_9954 {
		private final Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> field_53034 = new HashMap();

		public void method_62162(RegistryKey<? extends Registry<?>> registryKey, TagPacketSerializer.Serialized serialized) {
			this.field_53034.put(registryKey, serialized);
		}

		public void method_62163(BiConsumer<? super RegistryKey<? extends Registry<?>>, ? super TagPacketSerializer.Serialized> biConsumer) {
			this.field_53034.forEach(biConsumer);
		}
	}
}
