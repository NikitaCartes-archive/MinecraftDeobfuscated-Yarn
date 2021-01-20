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
		List<TagManagerLoader.class_5751<?>> list = Lists.<TagManagerLoader.class_5751<?>>newArrayList();
		RequiredTagListRegistry.forEach(requiredTagList -> {
			TagManagerLoader.class_5751<?> lv = this.method_33178(manager, prepareExecutor, requiredTagList);
			if (lv != null) {
				list.add(lv);
			}
		});
		return CompletableFuture.allOf((CompletableFuture[])list.stream().map(arg -> arg.field_28314).toArray(CompletableFuture[]::new))
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(
				void_ -> {
					TagManager.class_5749 lv = new TagManager.class_5749();
					list.forEach(arg2 -> arg2.method_33183(lv));
					TagManager tagManager = lv.method_33171();
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
	private <T> TagManagerLoader.class_5751<T> method_33178(ResourceManager resourceManager, Executor executor, RequiredTagList<T> requiredTagList) {
		Optional<? extends Registry<T>> optional = this.registryManager.getOptional(requiredTagList.getRegistryKey());
		if (optional.isPresent()) {
			Registry<T> registry = (Registry<T>)optional.get();
			TagGroupLoader<T> tagGroupLoader = new TagGroupLoader<>(registry::getOrEmpty, requiredTagList.method_33149());
			CompletableFuture<? extends TagGroup<T>> completableFuture = CompletableFuture.supplyAsync(() -> tagGroupLoader.method_33176(resourceManager), executor);
			return new TagManagerLoader.class_5751<>(requiredTagList, completableFuture);
		} else {
			LOGGER.warn("Can't find registry for {}", requiredTagList.getRegistryKey());
			return null;
		}
	}

	static class class_5751<T> {
		private final RequiredTagList<T> field_28313;
		private final CompletableFuture<? extends TagGroup<T>> field_28314;

		private class_5751(RequiredTagList<T> requiredTagList, CompletableFuture<? extends TagGroup<T>> completableFuture) {
			this.field_28313 = requiredTagList;
			this.field_28314 = completableFuture;
		}

		public void method_33183(TagManager.class_5749 arg) {
			arg.method_33172(this.field_28313.getRegistryKey(), (TagGroup<T>)this.field_28314.join());
		}
	}
}
