package net.minecraft.tag;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagContainer<T> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new Gson();
	private static final int JSON_EXTENSION_LENGTH = ".json".length();
	private final Map<Identifier, Tag<T>> idMap = Maps.<Identifier, Tag<T>>newHashMap();
	private final Function<Identifier, Optional<T>> getter;
	private final String path;
	private final boolean ordered;
	private final String type;

	public TagContainer(Function<Identifier, Optional<T>> function, String string, boolean bl, String string2) {
		this.getter = function;
		this.path = string;
		this.ordered = bl;
		this.type = string2;
	}

	public void add(Tag<T> tag) {
		if (this.idMap.containsKey(tag.getId())) {
			throw new IllegalArgumentException("Duplicate " + this.type + " tag '" + tag.getId() + "'");
		} else {
			this.idMap.put(tag.getId(), tag);
		}
	}

	@Nullable
	public Tag<T> get(Identifier identifier) {
		return (Tag<T>)this.idMap.get(identifier);
	}

	public Tag<T> getOrCreate(Identifier identifier) {
		Tag<T> tag = (Tag<T>)this.idMap.get(identifier);
		return tag == null ? new Tag<>(identifier) : tag;
	}

	public Collection<Identifier> getKeys() {
		return this.idMap.keySet();
	}

	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTagsFor(T object) {
		List<Identifier> list = Lists.<Identifier>newArrayList();

		for (Entry<Identifier, Tag<T>> entry : this.idMap.entrySet()) {
			if (((Tag)entry.getValue()).contains(object)) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public void clear() {
		this.idMap.clear();
	}

	public void load(ResourceManager resourceManager) {
		Map<Identifier, Tag.Builder<T>> map = Maps.<Identifier, Tag.Builder<T>>newHashMap();

		for (Identifier identifier : resourceManager.findResources(this.path, string -> string.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(this.path.length() + 1, string.length() - JSON_EXTENSION_LENGTH));

			try {
				for (Resource resource : resourceManager.getAllResources(identifier)) {
					try {
						JsonObject jsonObject = JsonHelper.deserialize(GSON, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
						if (jsonObject == null) {
							LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.type, identifier2, identifier, resource.getPackName());
						} else {
							Tag.Builder<T> builder = (Tag.Builder<T>)map.getOrDefault(identifier2, Tag.Builder.create());
							builder.fromJson(this.getter, jsonObject);
							map.put(identifier2, builder);
						}
					} catch (RuntimeException | IOException var15) {
						LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.type, identifier2, identifier, resource.getPackName(), var15);
					} finally {
						IOUtils.closeQuietly(resource);
					}
				}
			} catch (IOException var17) {
				LOGGER.error("Couldn't read {} tag list {} from {}", this.type, identifier2, identifier, var17);
			}
		}

		while (!map.isEmpty()) {
			boolean bl = false;
			Iterator<Entry<Identifier, Tag.Builder<T>>> iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Identifier, Tag.Builder<T>> entry = (Entry<Identifier, Tag.Builder<T>>)iterator.next();
				if (((Tag.Builder)entry.getValue()).applyTagGetter(this::get)) {
					bl = true;
					this.add(((Tag.Builder)entry.getValue()).build((Identifier)entry.getKey()));
					iterator.remove();
				}
			}

			if (!bl) {
				for (Entry<Identifier, Tag.Builder<T>> entry : map.entrySet()) {
					LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.type, entry.getKey());
				}
				break;
			}
		}

		for (Entry<Identifier, Tag.Builder<T>> entry2 : map.entrySet()) {
			this.add(((Tag.Builder)entry2.getValue()).ordered(this.ordered).build((Identifier)entry2.getKey()));
		}
	}

	public Map<Identifier, Tag<T>> getEntries() {
		return this.idMap;
	}
}
