package net.minecraft.data.server;

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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractTagProvider<T> implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	protected final DataGenerator root;
	protected final Registry<T> registry;
	protected final Map<Tag<T>, Tag.Builder<T>> field_11481 = Maps.<Tag<T>, Tag.Builder<T>>newLinkedHashMap();

	protected AbstractTagProvider(DataGenerator root, Registry<T> registry) {
		this.root = root;
		this.registry = registry;
	}

	protected abstract void configure();

	@Override
	public void run(DataCache dataCache) {
		this.field_11481.clear();
		this.configure();
		TagContainer<T> tagContainer = new TagContainer<>(identifier -> Optional.empty(), "", false, "generated");
		Map<Identifier, Tag.Builder<T>> map = (Map<Identifier, Tag.Builder<T>>)this.field_11481
			.entrySet()
			.stream()
			.collect(Collectors.toMap(entry -> ((Tag)entry.getKey()).getId(), Entry::getValue));
		tagContainer.applyReload(map);
		tagContainer.getEntries().forEach((identifier, tag) -> {
			JsonObject jsonObject = tag.toJson(this.registry::getId);
			Path path = this.getOutput(identifier);

			try {
				String string = GSON.toJson((JsonElement)jsonObject);
				String string2 = SHA1.hashUnencodedChars(string).toString();
				if (!Objects.equals(dataCache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
					Files.createDirectories(path.getParent());
					BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
					Throwable var9 = null;

					try {
						bufferedWriter.write(string);
					} catch (Throwable var19) {
						var9 = var19;
						throw var19;
					} finally {
						if (bufferedWriter != null) {
							if (var9 != null) {
								try {
									bufferedWriter.close();
								} catch (Throwable var18) {
									var9.addSuppressed(var18);
								}
							} else {
								bufferedWriter.close();
							}
						}
					}
				}

				dataCache.updateSha1(path, string2);
			} catch (IOException var21) {
				LOGGER.error("Couldn't save tags to {}", path, var21);
			}
		});
		this.method_10511(tagContainer);
	}

	protected abstract void method_10511(TagContainer<T> tagContainer);

	protected abstract Path getOutput(Identifier identifier);

	protected Tag.Builder<T> method_10512(Tag<T> tag) {
		return (Tag.Builder<T>)this.field_11481.computeIfAbsent(tag, tagx -> Tag.Builder.create());
	}
}
