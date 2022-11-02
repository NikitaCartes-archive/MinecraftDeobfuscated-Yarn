package net.minecraft.data.server.tag;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.tag.TagBuilder;
import net.minecraft.tag.TagEntry;
import net.minecraft.tag.TagFile;
import net.minecraft.tag.TagKey;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryWrapper;
import org.slf4j.Logger;

public abstract class AbstractTagProvider<T> implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	protected final DataOutput.PathResolver pathResolver;
	protected final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
	protected final RegistryKey<? extends Registry<T>> registryRef;
	private final Map<Identifier, TagBuilder> tagBuilders = Maps.<Identifier, TagBuilder>newLinkedHashMap();

	protected AbstractTagProvider(
		DataOutput output, RegistryKey<? extends Registry<T>> registryRef, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture
	) {
		this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, TagManagerLoader.getPath(registryRef));
		this.registryLookupFuture = registryLookupFuture;
		this.registryRef = registryRef;
	}

	@Override
	public final String getName() {
		return "Tags for " + this.registryRef.getValue();
	}

	protected abstract void configure(RegistryWrapper.WrapperLookup lookup);

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return this.registryLookupFuture
			.thenCompose(
				lookup -> {
					this.tagBuilders.clear();
					this.configure(lookup);
					RegistryWrapper.Impl<T> impl = lookup.getWrapperOrThrow(this.registryRef);
					Predicate<Identifier> predicate = id -> impl.getOptional(RegistryKey.of(this.registryRef, id)).isPresent();
					return CompletableFuture.allOf(
						(CompletableFuture[])this.tagBuilders
							.entrySet()
							.stream()
							.map(
								entry -> {
									Identifier identifier = (Identifier)entry.getKey();
									TagBuilder tagBuilder = (TagBuilder)entry.getValue();
									List<TagEntry> list = tagBuilder.build();
									List<TagEntry> list2 = list.stream().filter(tagEntry -> !tagEntry.canAdd(predicate, this.tagBuilders::containsKey)).toList();
									if (!list2.isEmpty()) {
										throw new IllegalArgumentException(
											String.format(
												Locale.ROOT,
												"Couldn't define tag %s as it is missing following references: %s",
												identifier,
												list2.stream().map(Objects::toString).collect(Collectors.joining(","))
											)
										);
									} else {
										JsonElement jsonElement = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(list, false)).getOrThrow(false, LOGGER::error);
										Path path = this.pathResolver.resolveJson(identifier);
										return DataProvider.writeToPath(writer, jsonElement, path);
									}
								}
							)
							.toArray(CompletableFuture[]::new)
					);
				}
			);
	}

	protected AbstractTagProvider.ProvidedTagBuilder<T> getOrCreateTagBuilder(TagKey<T> tag) {
		TagBuilder tagBuilder = this.getTagBuilder(tag);
		return new AbstractTagProvider.ProvidedTagBuilder<>(tagBuilder);
	}

	protected TagBuilder getTagBuilder(TagKey<T> tag) {
		return (TagBuilder)this.tagBuilders.computeIfAbsent(tag.id(), id -> TagBuilder.create());
	}

	protected static class ProvidedTagBuilder<T> {
		private final TagBuilder builder;

		protected ProvidedTagBuilder(TagBuilder builder) {
			this.builder = builder;
		}

		public final AbstractTagProvider.ProvidedTagBuilder<T> add(RegistryKey<T> key) {
			this.builder.add(key.getValue());
			return this;
		}

		@SafeVarargs
		public final AbstractTagProvider.ProvidedTagBuilder<T> add(RegistryKey<T>... keys) {
			for (RegistryKey<T> registryKey : keys) {
				this.builder.add(registryKey.getValue());
			}

			return this;
		}

		public AbstractTagProvider.ProvidedTagBuilder<T> addOptional(Identifier id) {
			this.builder.addOptional(id);
			return this;
		}

		public AbstractTagProvider.ProvidedTagBuilder<T> addTag(TagKey<T> identifiedTag) {
			this.builder.addTag(identifiedTag.id());
			return this;
		}

		public AbstractTagProvider.ProvidedTagBuilder<T> addOptionalTag(Identifier id) {
			this.builder.addOptionalTag(id);
			return this;
		}
	}
}
