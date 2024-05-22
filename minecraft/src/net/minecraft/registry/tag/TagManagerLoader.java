package net.minecraft.registry.tag;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class TagManagerLoader implements ResourceReloader {
	private final DynamicRegistryManager registryManager;
	private List<TagManagerLoader.RegistryTags<?>> registryTags = List.of();

	public TagManagerLoader(DynamicRegistryManager registryManager) {
		this.registryManager = registryManager;
	}

	public List<TagManagerLoader.RegistryTags<?>> getRegistryTags() {
		return this.registryTags;
	}

	@Override
	public CompletableFuture<Void> reload(
		ResourceReloader.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		List<? extends CompletableFuture<? extends TagManagerLoader.RegistryTags<?>>> list = this.registryManager
			.streamAllRegistries()
			.map(registry -> this.buildRequiredGroup(manager, prepareExecutor, registry))
			.toList();
		return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new))
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(
				void_ -> this.registryTags = (List<TagManagerLoader.RegistryTags<?>>)list.stream().map(CompletableFuture::join).collect(Collectors.toUnmodifiableList()),
				applyExecutor
			);
	}

	private <T> CompletableFuture<TagManagerLoader.RegistryTags<T>> buildRequiredGroup(
		ResourceManager resourceManager, Executor prepareExecutor, DynamicRegistryManager.Entry<T> requirement
	) {
		RegistryKey<? extends Registry<T>> registryKey = requirement.key();
		Registry<T> registry = requirement.value();
		TagGroupLoader<RegistryEntry<T>> tagGroupLoader = new TagGroupLoader<>(registry::getEntry, RegistryKeys.getTagPath(registryKey));
		return CompletableFuture.supplyAsync(() -> new TagManagerLoader.RegistryTags<>(registryKey, tagGroupLoader.load(resourceManager)), prepareExecutor);
	}

	public static record RegistryTags<T>(RegistryKey<? extends Registry<T>> key, Map<Identifier, Collection<RegistryEntry<T>>> tags) {
	}
}
