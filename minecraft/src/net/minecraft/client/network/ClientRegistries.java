package net.minecraft.client.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
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
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

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

	private static <T> Registry.PendingTagLoad<T> startTagReload(
		DynamicRegistryManager.Immutable registryManager, RegistryKey<? extends Registry<? extends T>> registryRef, TagPacketSerializer.Serialized tags
	) {
		Registry<T> registry = registryManager.getOrThrow(registryRef);
		return registry.startTagReload(tags.toRegistryTags(registry));
	}

	private DynamicRegistryManager createRegistryManager(ResourceFactory resourceFactory, ClientRegistries.DynamicRegistries dynamicRegistries, boolean local) {
		CombinedDynamicRegistries<ClientDynamicRegistryType> combinedDynamicRegistries = ClientDynamicRegistryType.createCombinedDynamicRegistries();
		DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries.getPrecedingRegistryManagers(ClientDynamicRegistryType.REMOTE);
		Map<RegistryKey<? extends Registry<?>>, RegistryLoader.ElementsAndTags> map = new HashMap();
		dynamicRegistries.dynamicRegistries
			.forEach((registryRef, entries) -> map.put(registryRef, new RegistryLoader.ElementsAndTags(entries, TagPacketSerializer.Serialized.NONE)));
		List<Registry.PendingTagLoad<?>> list = new ArrayList();
		if (this.tags != null) {
			this.tags.forEach((registryRef, tags) -> {
				if (!tags.isEmpty()) {
					if (SerializableRegistries.isSynced(registryRef)) {
						map.compute(registryRef, (key, value) -> {
							List<SerializableRegistries.SerializedRegistryEntry> listxx = value != null ? value.elements() : List.of();
							return new RegistryLoader.ElementsAndTags(listxx, tags);
						});
					} else if (!local) {
						list.add(startTagReload(immutable, registryRef, tags));
					}
				}
			});
		}

		List<RegistryWrapper.Impl<?>> list2 = TagGroupLoader.collectRegistries(immutable, list);

		DynamicRegistryManager.Immutable immutable2;
		try {
			immutable2 = RegistryLoader.loadFromNetwork(map, resourceFactory, list2, RegistryLoader.SYNCED_REGISTRIES).toImmutable();
		} catch (Exception var13) {
			CrashReport crashReport = CrashReport.create(var13, "Network Registry Load");
			addCrashReportSection(crashReport, map, list);
			throw new CrashException(crashReport);
		}

		DynamicRegistryManager dynamicRegistryManager = combinedDynamicRegistries.with(ClientDynamicRegistryType.REMOTE, immutable2).getCombinedRegistryManager();
		list.forEach(Registry.PendingTagLoad::apply);
		return dynamicRegistryManager;
	}

	private static void addCrashReportSection(
		CrashReport crashReport, Map<RegistryKey<? extends Registry<?>>, RegistryLoader.ElementsAndTags> data, List<Registry.PendingTagLoad<?>> tags
	) {
		CrashReportSection crashReportSection = crashReport.addElement("Received Elements and Tags");
		crashReportSection.add(
			"Dynamic Registries",
			(CrashCallable<String>)(() -> (String)data.entrySet()
					.stream()
					.sorted(Comparator.comparing(entry -> ((RegistryKey)entry.getKey()).getValue()))
					.map(
						entry -> String.format(
								Locale.ROOT,
								"\n\t\t%s: elements=%d tags=%d",
								((RegistryKey)entry.getKey()).getValue(),
								((RegistryLoader.ElementsAndTags)entry.getValue()).elements().size(),
								((RegistryLoader.ElementsAndTags)entry.getValue()).tags().size()
							)
					)
					.collect(Collectors.joining()))
		);
		crashReportSection.add(
			"Static Registries",
			(CrashCallable<String>)(() -> (String)tags.stream()
					.sorted(Comparator.comparing(tag -> tag.getKey().getValue()))
					.map(tag -> String.format(Locale.ROOT, "\n\t\t%s: tags=%d", tag.getKey().getValue(), tag.size()))
					.collect(Collectors.joining()))
		);
	}

	private void loadTags(ClientRegistries.Tags tags, DynamicRegistryManager.Immutable registryManager, boolean local) {
		tags.forEach((registryRef, serialized) -> {
			if (local || SerializableRegistries.isSynced(registryRef)) {
				startTagReload(registryManager, registryRef, serialized).apply();
			}
		});
	}

	public DynamicRegistryManager.Immutable createRegistryManager(ResourceFactory resourceFactory, DynamicRegistryManager.Immutable registryManager, boolean local) {
		DynamicRegistryManager dynamicRegistryManager;
		if (this.dynamicRegistries != null) {
			dynamicRegistryManager = this.createRegistryManager(resourceFactory, this.dynamicRegistries, local);
		} else {
			if (this.tags != null) {
				this.loadTags(this.tags, registryManager, !local);
			}

			dynamicRegistryManager = registryManager;
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
	static class Tags {
		private final Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> tags = new HashMap();

		public void put(RegistryKey<? extends Registry<?>> registryRef, TagPacketSerializer.Serialized tags) {
			this.tags.put(registryRef, tags);
		}

		public void forEach(BiConsumer<? super RegistryKey<? extends Registry<?>>, ? super TagPacketSerializer.Serialized> consumer) {
			this.tags.forEach(consumer);
		}
	}
}
