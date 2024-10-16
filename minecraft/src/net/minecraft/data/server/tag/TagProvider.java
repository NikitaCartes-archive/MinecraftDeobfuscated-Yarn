package net.minecraft.data.server.tag;

import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public abstract class TagProvider<T> implements DataProvider {
	protected final DataOutput.PathResolver pathResolver;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;
	private final CompletableFuture<Void> registryLoadFuture = new CompletableFuture();
	private final CompletableFuture<TagProvider.TagLookup<T>> parentTagLookupFuture;
	protected final RegistryKey<? extends Registry<T>> registryRef;
	private final Map<Identifier, TagBuilder> tagBuilders = Maps.<Identifier, TagBuilder>newLinkedHashMap();

	protected TagProvider(DataOutput output, RegistryKey<? extends Registry<T>> registryRef, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		this(output, registryRef, registriesFuture, CompletableFuture.completedFuture(TagProvider.TagLookup.empty()));
	}

	protected TagProvider(
		DataOutput output,
		RegistryKey<? extends Registry<T>> registryRef,
		CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture,
		CompletableFuture<TagProvider.TagLookup<T>> parentTagLookupFuture
	) {
		this.pathResolver = output.getTagResolver(registryRef);
		this.registryRef = registryRef;
		this.parentTagLookupFuture = parentTagLookupFuture;
		this.registriesFuture = registriesFuture;
	}

	@Override
	public final String getName() {
		return "Tags for " + this.registryRef.getValue();
	}

	protected abstract void configure(RegistryWrapper.WrapperLookup registries);

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		record RegistryInfo<T>(RegistryWrapper.WrapperLookup contents, TagProvider.TagLookup<T> parent) {
		}

		return this.getRegistriesFuture()
			.thenApply(registriesFuture -> {
				this.registryLoadFuture.complete(null);
				return registriesFuture;
			})
			.thenCombineAsync(this.parentTagLookupFuture, (registries, parent) -> new RegistryInfo(registries, parent), Util.getMainWorkerExecutor())
			.thenCompose(
				info -> {
					RegistryWrapper.Impl<T> impl = info.contents.getOrThrow(this.registryRef);
					Predicate<Identifier> predicate = id -> impl.getOptional(RegistryKey.of(this.registryRef, id)).isPresent();
					Predicate<Identifier> predicate2 = id -> this.tagBuilders.containsKey(id) || info.parent.contains(TagKey.of(this.registryRef, id));
					return CompletableFuture.allOf(
						(CompletableFuture[])this.tagBuilders
							.entrySet()
							.stream()
							.map(
								entry -> {
									Identifier identifier = (Identifier)entry.getKey();
									TagBuilder tagBuilder = (TagBuilder)entry.getValue();
									List<TagEntry> list = tagBuilder.build();
									List<TagEntry> list2 = list.stream().filter(tagEntry -> !tagEntry.canAdd(predicate, predicate2)).toList();
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
										Path path = this.pathResolver.resolveJson(identifier);
										return DataProvider.writeCodecToPath(writer, info.contents, TagFile.CODEC, new TagFile(list, false), path);
									}
								}
							)
							.toArray(CompletableFuture[]::new)
					);
				}
			);
	}

	protected TagProvider.ProvidedTagBuilder<T> getOrCreateTagBuilder(TagKey<T> tag) {
		TagBuilder tagBuilder = this.getTagBuilder(tag);
		return new TagProvider.ProvidedTagBuilder<>(tagBuilder);
	}

	protected TagBuilder getTagBuilder(TagKey<T> tag) {
		return (TagBuilder)this.tagBuilders.computeIfAbsent(tag.id(), id -> TagBuilder.create());
	}

	public CompletableFuture<TagProvider.TagLookup<T>> getTagLookupFuture() {
		return this.registryLoadFuture.thenApply(void_ -> tag -> Optional.ofNullable((TagBuilder)this.tagBuilders.get(tag.id())));
	}

	protected CompletableFuture<RegistryWrapper.WrapperLookup> getRegistriesFuture() {
		return this.registriesFuture.thenApply(registries -> {
			this.tagBuilders.clear();
			this.configure(registries);
			return registries;
		});
	}

	protected static class ProvidedTagBuilder<T> {
		private final TagBuilder builder;

		protected ProvidedTagBuilder(TagBuilder builder) {
			this.builder = builder;
		}

		public final TagProvider.ProvidedTagBuilder<T> add(RegistryKey<T> key) {
			this.builder.add(key.getValue());
			return this;
		}

		@SafeVarargs
		public final TagProvider.ProvidedTagBuilder<T> add(RegistryKey<T>... keys) {
			for (RegistryKey<T> registryKey : keys) {
				this.builder.add(registryKey.getValue());
			}

			return this;
		}

		public final TagProvider.ProvidedTagBuilder<T> add(List<RegistryKey<T>> keys) {
			for (RegistryKey<T> registryKey : keys) {
				this.builder.add(registryKey.getValue());
			}

			return this;
		}

		public TagProvider.ProvidedTagBuilder<T> addOptional(Identifier id) {
			this.builder.addOptional(id);
			return this;
		}

		public TagProvider.ProvidedTagBuilder<T> addTag(TagKey<T> identifiedTag) {
			this.builder.addTag(identifiedTag.id());
			return this;
		}

		public TagProvider.ProvidedTagBuilder<T> addOptionalTag(Identifier id) {
			this.builder.addOptionalTag(id);
			return this;
		}
	}

	@FunctionalInterface
	public interface TagLookup<T> extends Function<TagKey<T>, Optional<TagBuilder>> {
		static <T> TagProvider.TagLookup<T> empty() {
			return tag -> Optional.empty();
		}

		default boolean contains(TagKey<T> tag) {
			return ((Optional)this.apply(tag)).isPresent();
		}
	}
}
