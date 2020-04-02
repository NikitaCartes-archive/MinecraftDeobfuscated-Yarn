package net.minecraft.data.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractTagProvider<T> implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	protected final DataGenerator root;
	protected final Registry<T> registry;
	protected final Map<Identifier, Tag.ObjectBuilder<T>> tagBuilders = Maps.<Identifier, Tag.ObjectBuilder<T>>newLinkedHashMap();

	protected AbstractTagProvider(DataGenerator root, Registry<T> registry) {
		this.root = root;
		this.registry = registry;
	}

	protected abstract void configure();

	@Override
	public void run(DataCache cache) {
		this.tagBuilders.clear();
		this.configure();
		Tag<T> tag = Tag.of(ImmutableSet.of());
		Function<Identifier, Tag<T>> function = identifier -> this.tagBuilders.containsKey(identifier) ? tag : null;
		Function<Identifier, T> function2 = identifier -> this.registry.getOrEmpty(identifier).orElse(null);
		this.tagBuilders
			.forEach(
				(identifier, objectBuilder) -> {
					List<Tag.Entry> list = (List<Tag.Entry>)objectBuilder.streamUnresolvedEntries(function, function2).collect(Collectors.toList());
					if (!list.isEmpty()) {
						throw new IllegalArgumentException(
							String.format(
								"Couldn't define tag %s as it is missing following references: %s", identifier, list.stream().map(Objects::toString).collect(Collectors.joining(","))
							)
						);
					} else {
						JsonObject jsonObject = objectBuilder.toJson();
						Path path = this.getOutput(identifier);

						try {
							String string = GSON.toJson((JsonElement)jsonObject);
							String string2 = SHA1.hashUnencodedChars(string).toString();
							if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
								Files.createDirectories(path.getParent());
								BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
								Throwable var12 = null;

								try {
									bufferedWriter.write(string);
								} catch (Throwable var22) {
									var12 = var22;
									throw var22;
								} finally {
									if (bufferedWriter != null) {
										if (var12 != null) {
											try {
												bufferedWriter.close();
											} catch (Throwable var21) {
												var12.addSuppressed(var21);
											}
										} else {
											bufferedWriter.close();
										}
									}
								}
							}

							cache.updateSha1(path, string2);
						} catch (IOException var24) {
							LOGGER.error("Couldn't save tags to {}", path, var24);
						}
					}
				}
			);
	}

	protected abstract Path getOutput(Identifier identifier);

	protected Tag.ObjectBuilder<T> getOrCreateTagBuilder(Tag.Identified<T> identified) {
		return this.method_27047(identified.getId());
	}

	protected Tag.ObjectBuilder<T> method_27047(Identifier identifier) {
		return (Tag.ObjectBuilder<T>)this.tagBuilders.computeIfAbsent(identifier, identifierx -> new Tag.ObjectBuilder(this.registry::getId));
	}
}
