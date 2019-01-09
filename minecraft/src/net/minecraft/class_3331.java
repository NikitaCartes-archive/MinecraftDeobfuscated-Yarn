package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3331<K, V extends class_3330<K>> {
	protected static final Logger field_14373 = LogManager.getLogger();
	protected final Gson field_14374;
	private final File field_14370;
	private final Map<String, V> field_14371 = Maps.<String, V>newHashMap();
	private boolean field_14372 = true;
	private static final ParameterizedType field_14369 = new ParameterizedType() {
		public Type[] getActualTypeArguments() {
			return new Type[]{class_3330.class};
		}

		public Type getRawType() {
			return List.class;
		}

		public Type getOwnerType() {
			return null;
		}
	};

	public class_3331(File file) {
		this.field_14370 = file;
		GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
		gsonBuilder.registerTypeHierarchyAdapter(class_3330.class, new class_3331.class_3332());
		this.field_14374 = gsonBuilder.create();
	}

	public boolean method_14639() {
		return this.field_14372;
	}

	public void method_14637(boolean bl) {
		this.field_14372 = bl;
	}

	public File method_14643() {
		return this.field_14370;
	}

	public void method_14633(V arg) {
		this.field_14371.put(this.method_14634(arg.method_14626()), arg);

		try {
			this.method_14629();
		} catch (IOException var3) {
			field_14373.warn("Could not save the list after adding a user.", (Throwable)var3);
		}
	}

	@Nullable
	public V method_14640(K object) {
		this.method_14631();
		return (V)this.field_14371.get(this.method_14634(object));
	}

	public void method_14635(K object) {
		this.field_14371.remove(this.method_14634(object));

		try {
			this.method_14629();
		} catch (IOException var3) {
			field_14373.warn("Could not save the list after removing a user.", (Throwable)var3);
		}
	}

	public void method_14638(class_3330<K> arg) {
		this.method_14635(arg.method_14626());
	}

	public String[] method_14636() {
		return (String[])this.field_14371.keySet().toArray(new String[this.field_14371.size()]);
	}

	public boolean method_14641() {
		return this.field_14371.size() < 1;
	}

	protected String method_14634(K object) {
		return object.toString();
	}

	protected boolean method_14644(K object) {
		return this.field_14371.containsKey(this.method_14634(object));
	}

	private void method_14631() {
		List<K> list = Lists.<K>newArrayList();

		for (V lv : this.field_14371.values()) {
			if (lv.method_14627()) {
				list.add(lv.method_14626());
			}
		}

		for (K object : list) {
			this.field_14371.remove(this.method_14634(object));
		}
	}

	protected class_3330<K> method_14642(JsonObject jsonObject) {
		return new class_3330<>(null, jsonObject);
	}

	public Collection<V> method_14632() {
		return this.field_14371.values();
	}

	public void method_14629() throws IOException {
		Collection<V> collection = this.field_14371.values();
		String string = this.field_14374.toJson(collection);
		BufferedWriter bufferedWriter = null;

		try {
			bufferedWriter = Files.newWriter(this.field_14370, StandardCharsets.UTF_8);
			bufferedWriter.write(string);
		} finally {
			IOUtils.closeQuietly(bufferedWriter);
		}
	}

	public void method_14630() throws FileNotFoundException {
		if (this.field_14370.exists()) {
			BufferedReader bufferedReader = null;

			try {
				bufferedReader = Files.newReader(this.field_14370, StandardCharsets.UTF_8);
				Collection<class_3330<K>> collection = class_3518.method_15297(this.field_14374, bufferedReader, field_14369);
				if (collection != null) {
					this.field_14371.clear();

					for (class_3330<K> lv : collection) {
						if (lv.method_14626() != null) {
							this.field_14371.put(this.method_14634(lv.method_14626()), lv);
						}
					}
				}
			} finally {
				IOUtils.closeQuietly(bufferedReader);
			}
		}
	}

	class class_3332 implements JsonDeserializer<class_3330<K>>, JsonSerializer<class_3330<K>> {
		private class_3332() {
		}

		public JsonElement method_14646(class_3330<K> arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			arg.method_14628(jsonObject);
			return jsonObject;
		}

		public class_3330<K> method_14645(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				return class_3331.this.method_14642(jsonObject);
			} else {
				return null;
			}
		}
	}
}
