package net.minecraft.client.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.tag.TagPacketSerializer;

@Environment(EnvType.CLIENT)
public class ClientTagLoader {
	private final Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> tagsByRegistry = new HashMap();

	public void put(RegistryKey<? extends Registry<?>> registryRef, TagPacketSerializer.Serialized serialized) {
		this.tagsByRegistry.put(registryRef, serialized);
	}

	private static void onStaticTagsLoaded() {
		AbstractFurnaceBlockEntity.clearFuelTimes();
		Blocks.refreshShapeCache();
	}

	private void load(DynamicRegistryManager registryManager, Predicate<RegistryKey<? extends Registry<?>>> predicate) {
		this.tagsByRegistry.forEach((registryRef, serialized) -> {
			if (predicate.test(registryRef)) {
				serialized.loadTo(registryManager.get(registryRef));
			}
		});
	}

	public void load(DynamicRegistryManager registryManager, boolean local) {
		if (local) {
			this.load(registryManager, SerializableRegistries.SYNCED_REGISTRIES::contains);
		} else {
			registryManager.streamAllRegistries()
				.filter(registries -> !SerializableRegistries.SYNCED_REGISTRIES.contains(registries.key()))
				.forEach(entry -> entry.value().clearTags());
			this.load(registryManager, registryRef -> true);
			onStaticTagsLoaded();
		}
	}
}
