package net.minecraft.tag;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagManagerLoader implements ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final DynamicRegistryManager registryManager;
	private TagManager tagManager = TagManager.EMPTY;

	public TagManagerLoader(DynamicRegistryManager registryManager) {
		this.registryManager = registryManager;
	}

	public TagManager getTagManager() {
		return this.tagManager;
	}

	@Override
	public CompletableFuture<Void> reload(
		ResourceReloadListener.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		List<TagManagerLoader.RequiredGroup<?>> list = Lists.<TagManagerLoader.RequiredGroup<?>>newArrayList();
		RequiredTagListRegistry.forEach(requiredTagList -> {
			TagManagerLoader.RequiredGroup<?> requiredGroup = this.buildRequiredGroup(manager, prepareExecutor, requiredTagList);
			if (requiredGroup != null) {
				list.add(requiredGroup);
			}
		});
		return CompletableFuture.allOf((CompletableFuture[])list.stream().map(requiredGroup -> requiredGroup.groupLoadFuture).toArray(CompletableFuture[]::new))
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(
				void_ -> {
					TagManager.Builder builder = new TagManager.Builder();
					list.forEach(requiredGroup -> requiredGroup.addTo(builder));
					TagManager tagManager = builder.build();
					Multimap<RegistryKey<? extends Registry<?>>, Identifier> multimap = RequiredTagListRegistry.getMissingTags(tagManager);
					if (!multimap.isEmpty()) {
						throw new IllegalStateException(
							"Missing required tags: "
								+ (String)multimap.entries().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(","))
						);
					} else {
						ServerTagManagerHolder.setTagManager(tagManager);
						this.tagManager = tagManager;
					}
				},
				applyExecutor
			);
	}

	@Nullable
	private <T> TagManagerLoader.RequiredGroup<T> buildRequiredGroup(ResourceManager resourceManager, Executor prepareExecutor, RequiredTagList<T> requirement) {
		Optional<? extends Registry<T>> optional = this.registryManager.getOptional(requirement.getRegistryKey());
		if (optional.isPresent()) {
			Registry<T> registry = (Registry<T>)optional.get();
			TagGroupLoader<T> tagGroupLoader = new TagGroupLoader<>(registry::getOrEmpty, requirement.getDataType());
			CompletableFuture<? extends TagGroup<T>> completableFuture = CompletableFuture.supplyAsync(() -> tagGroupLoader.load(resourceManager), prepareExecutor);
			return new TagManagerLoader.RequiredGroup<>(requirement, completableFuture);
		} else {
			LOGGER.warn("Can't find registry for {}", requirement.getRegistryKey());
			return null;
		}
	}

	static class RequiredGroup<T> {
		private final RequiredTagList<T> requirement;
		private final CompletableFuture<? extends TagGroup<T>> groupLoadFuture;

		private RequiredGroup(RequiredTagList<T> requirement, CompletableFuture<? extends TagGroup<T>> groupLoadFuture) {
			this.requirement = requirement;
			this.groupLoadFuture = groupLoadFuture;
		}

		public void addTo(TagManager.Builder builder) {
			builder.add(this.requirement.getRegistryKey(), (TagGroup<T>)this.groupLoadFuture.join());
		}
	}
}
