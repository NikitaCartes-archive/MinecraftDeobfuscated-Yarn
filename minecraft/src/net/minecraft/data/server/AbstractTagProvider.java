package net.minecraft.data.server;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.DataGenerator;
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
import org.slf4j.Logger;

public abstract class AbstractTagProvider<T> implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	protected final DataGenerator.PathResolver pathResolver;
	protected final Registry<T> registry;
	private final Map<Identifier, TagBuilder> tagBuilders = Maps.<Identifier, TagBuilder>newLinkedHashMap();

	protected AbstractTagProvider(DataGenerator root, Registry<T> registry) {
		this.pathResolver = root.createPathResolver(DataGenerator.OutputType.DATA_PACK, TagManagerLoader.getPath(registry.getKey()));
		this.registry = registry;
	}

	@Override
	public final String getName() {
		return "Tags for " + this.registry.getKey().getValue();
	}

	protected abstract void configure();

	@Override
	public void run(DataWriter writer) {
		this.tagBuilders.clear();
		this.configure();
		this.tagBuilders
			.forEach(
				(id, builder) -> {
					List<TagEntry> list = builder.build();
					List<TagEntry> list2 = list.stream().filter(tag -> !tag.canAdd(this.registry::containsId, this.tagBuilders::containsKey)).toList();
					if (!list2.isEmpty()) {
						throw new IllegalArgumentException(
							String.format(
								Locale.ROOT,
								"Couldn't define tag %s as it is missing following references: %s",
								id,
								list2.stream().map(Objects::toString).collect(Collectors.joining(","))
							)
						);
					} else {
						JsonElement jsonElement = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(list, false)).getOrThrow(false, LOGGER::error);
						Path path = this.pathResolver.resolveJson(id);

						try {
							DataProvider.writeToPath(writer, jsonElement, path);
						} catch (IOException var9) {
							LOGGER.error("Couldn't save tags to {}", path, var9);
						}
					}
				}
			);
	}

	protected AbstractTagProvider.ObjectBuilder<T> getOrCreateTagBuilder(TagKey<T> tag) {
		TagBuilder tagBuilder = this.getTagBuilder(tag);
		return new AbstractTagProvider.ObjectBuilder<>(tagBuilder, this.registry);
	}

	protected TagBuilder getTagBuilder(TagKey<T> tag) {
		return (TagBuilder)this.tagBuilders.computeIfAbsent(tag.id(), id -> TagBuilder.create());
	}

	protected static class ObjectBuilder<T> {
		private final TagBuilder builder;
		private final Registry<T> registry;

		ObjectBuilder(TagBuilder builder, Registry<T> registry) {
			this.builder = builder;
			this.registry = registry;
		}

		public AbstractTagProvider.ObjectBuilder<T> add(T element) {
			this.builder.add(this.registry.getId(element));
			return this;
		}

		@SafeVarargs
		public final AbstractTagProvider.ObjectBuilder<T> add(RegistryKey<T>... keys) {
			for (RegistryKey<T> registryKey : keys) {
				this.builder.add(registryKey.getValue());
			}

			return this;
		}

		public AbstractTagProvider.ObjectBuilder<T> addOptional(Identifier id) {
			this.builder.addOptional(id);
			return this;
		}

		public AbstractTagProvider.ObjectBuilder<T> addTag(TagKey<T> identifiedTag) {
			this.builder.addTag(identifiedTag.id());
			return this;
		}

		public AbstractTagProvider.ObjectBuilder<T> addOptionalTag(Identifier id) {
			this.builder.addOptionalTag(id);
			return this;
		}

		@SafeVarargs
		public final AbstractTagProvider.ObjectBuilder<T> add(T... elements) {
			Stream.of(elements).map(this.registry::getId).forEach(id -> this.builder.add(id));
			return this;
		}
	}
}
