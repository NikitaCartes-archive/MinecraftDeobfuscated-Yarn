package net.minecraft.data.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

public abstract class AbstractTagProvider<T> implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	protected final DataGenerator root;
	protected final Registry<T> registry;
	private final Map<Identifier, Tag.Builder> tagBuilders = Maps.<Identifier, Tag.Builder>newLinkedHashMap();

	protected AbstractTagProvider(DataGenerator root, Registry<T> registry) {
		this.root = root;
		this.registry = registry;
	}

	protected abstract void configure();

	@Override
	public void run(DataCache cache) {
		this.tagBuilders.clear();
		this.configure();
		this.tagBuilders
			.forEach(
				(id, builder) -> {
					List<Tag.TrackedEntry> list = builder.streamEntries()
						.filter(tag -> !tag.entry().canAdd(this.registry::containsId, this.tagBuilders::containsKey))
						.toList();
					if (!list.isEmpty()) {
						throw new IllegalArgumentException(
							String.format(
								"Couldn't define tag %s as it is missing following references: %s", id, list.stream().map(Objects::toString).collect(Collectors.joining(","))
							)
						);
					} else {
						JsonObject jsonObject = builder.toJson();
						Path path = this.getOutput(id);

						try {
							String string = GSON.toJson((JsonElement)jsonObject);
							String string2 = SHA1.hashUnencodedChars(string).toString();
							if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
								Files.createDirectories(path.getParent());
								BufferedWriter bufferedWriter = Files.newBufferedWriter(path);

								try {
									bufferedWriter.write(string);
								} catch (Throwable var13) {
									if (bufferedWriter != null) {
										try {
											bufferedWriter.close();
										} catch (Throwable var12) {
											var13.addSuppressed(var12);
										}
									}

									throw var13;
								}

								if (bufferedWriter != null) {
									bufferedWriter.close();
								}
							}

							cache.updateSha1(path, string2);
						} catch (IOException var14) {
							LOGGER.error("Couldn't save tags to {}", path, var14);
						}
					}
				}
			);
	}

	protected abstract Path getOutput(Identifier id);

	protected AbstractTagProvider.ObjectBuilder<T> getOrCreateTagBuilder(TagKey<T> tag) {
		Tag.Builder builder = this.getTagBuilder(tag);
		return new AbstractTagProvider.ObjectBuilder<>(builder, this.registry, "vanilla");
	}

	protected Tag.Builder getTagBuilder(TagKey<T> tag) {
		return (Tag.Builder)this.tagBuilders.computeIfAbsent(tag.id(), id -> new Tag.Builder());
	}

	protected static class ObjectBuilder<T> {
		private final Tag.Builder builder;
		private final Registry<T> registry;
		private final String source;

		ObjectBuilder(Tag.Builder builder, Registry<T> registry, String source) {
			this.builder = builder;
			this.registry = registry;
			this.source = source;
		}

		public AbstractTagProvider.ObjectBuilder<T> add(T element) {
			this.builder.add(this.registry.getId(element), this.source);
			return this;
		}

		public AbstractTagProvider.ObjectBuilder<T> addOptional(Identifier id) {
			this.builder.addOptional(id, this.source);
			return this;
		}

		public AbstractTagProvider.ObjectBuilder<T> addTag(TagKey<T> identifiedTag) {
			this.builder.addTag(identifiedTag.id(), this.source);
			return this;
		}

		public AbstractTagProvider.ObjectBuilder<T> addOptionalTag(Identifier id) {
			this.builder.addOptionalTag(id, this.source);
			return this;
		}

		@SafeVarargs
		public final AbstractTagProvider.ObjectBuilder<T> add(T... elements) {
			Stream.of(elements).map(this.registry::getId).forEach(id -> this.builder.add(id, this.source));
			return this;
		}
	}
}
