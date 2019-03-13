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

	protected AbstractTagProvider(DataGenerator dataGenerator, Registry<T> registry) {
		this.root = dataGenerator;
		this.registry = registry;
	}

	protected abstract void configure();

	@Override
	public void method_10319(DataCache dataCache) throws IOException {
		this.field_11481.clear();
		this.configure();
		TagContainer<T> tagContainer = new TagContainer<>(identifierx -> Optional.empty(), "", false, "generated");

		for (Entry<Tag<T>, Tag.Builder<T>> entry : this.field_11481.entrySet()) {
			Identifier identifier = ((Tag)entry.getKey()).getId();
			if (!((Tag.Builder)entry.getValue()).applyTagGetter(tagContainer::get)) {
				throw new UnsupportedOperationException("Unsupported referencing of tags!");
			}

			Tag<T> tag = ((Tag.Builder)entry.getValue()).build(identifier);
			JsonObject jsonObject = tag.toJson(this.registry::method_10221);
			Path path = this.method_10510(identifier);
			tagContainer.add(tag);
			this.method_10511(tagContainer);

			try {
				String string = GSON.toJson((JsonElement)jsonObject);
				String string2 = SHA1.hashUnencodedChars(string).toString();
				if (!Objects.equals(dataCache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
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

				dataCache.updateSha1(path, string2);
			} catch (IOException var24) {
				LOGGER.error("Couldn't save tags to {}", path, var24);
			}
		}
	}

	protected abstract void method_10511(TagContainer<T> tagContainer);

	protected abstract Path method_10510(Identifier identifier);

	protected Tag.Builder<T> method_10512(Tag<T> tag) {
		return (Tag.Builder<T>)this.field_11481.computeIfAbsent(tag, tagx -> Tag.Builder.create());
	}
}
