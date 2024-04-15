package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public abstract class ServerConfigList<K, V extends ServerConfigEntry<K>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final File file;
	private final Map<String, V> map = Maps.<String, V>newHashMap();

	public ServerConfigList(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public void add(V entry) {
		this.map.put(this.toString(entry.getKey()), entry);

		try {
			this.save();
		} catch (IOException var3) {
			LOGGER.warn("Could not save the list after adding a user.", (Throwable)var3);
		}
	}

	@Nullable
	public V get(K key) {
		this.removeInvalidEntries();
		return (V)this.map.get(this.toString(key));
	}

	public void remove(K key) {
		this.map.remove(this.toString(key));

		try {
			this.save();
		} catch (IOException var3) {
			LOGGER.warn("Could not save the list after removing a user.", (Throwable)var3);
		}
	}

	public void remove(ServerConfigEntry<K> entry) {
		this.remove(entry.getKey());
	}

	public String[] getNames() {
		return (String[])this.map.keySet().toArray(new String[0]);
	}

	public boolean isEmpty() {
		return this.map.size() < 1;
	}

	protected String toString(K profile) {
		return profile.toString();
	}

	protected boolean contains(K object) {
		return this.map.containsKey(this.toString(object));
	}

	private void removeInvalidEntries() {
		List<K> list = Lists.<K>newArrayList();

		for (V serverConfigEntry : this.map.values()) {
			if (serverConfigEntry.isInvalid()) {
				list.add(serverConfigEntry.getKey());
			}
		}

		for (K object : list) {
			this.map.remove(this.toString(object));
		}
	}

	protected abstract ServerConfigEntry<K> fromJson(JsonObject json);

	public Collection<V> values() {
		return this.map.values();
	}

	public void save() throws IOException {
		JsonArray jsonArray = new JsonArray();
		this.map.values().stream().map(entry -> Util.make(new JsonObject(), entry::write)).forEach(jsonArray::add);
		BufferedWriter bufferedWriter = Files.newWriter(this.file, StandardCharsets.UTF_8);

		try {
			GSON.toJson(jsonArray, GSON.newJsonWriter(bufferedWriter));
		} catch (Throwable var6) {
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (Throwable var5) {
					var6.addSuppressed(var5);
				}
			}

			throw var6;
		}

		if (bufferedWriter != null) {
			bufferedWriter.close();
		}
	}

	public void load() throws IOException {
		if (this.file.exists()) {
			BufferedReader bufferedReader = Files.newReader(this.file, StandardCharsets.UTF_8);

			label54: {
				try {
					this.map.clear();
					JsonArray jsonArray = GSON.fromJson(bufferedReader, JsonArray.class);
					if (jsonArray == null) {
						break label54;
					}

					for (JsonElement jsonElement : jsonArray) {
						JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
						ServerConfigEntry<K> serverConfigEntry = this.fromJson(jsonObject);
						if (serverConfigEntry.getKey() != null) {
							this.map.put(this.toString(serverConfigEntry.getKey()), serverConfigEntry);
						}
					}
				} catch (Throwable var8) {
					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var7) {
							var8.addSuppressed(var7);
						}
					}

					throw var8;
				}

				if (bufferedReader != null) {
					bufferedReader.close();
				}

				return;
			}

			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}
}
