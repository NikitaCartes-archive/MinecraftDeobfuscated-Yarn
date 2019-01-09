package net.minecraft;

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
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3503<T> {
	private static final Logger field_15607 = LogManager.getLogger();
	private static final Gson field_15608 = new Gson();
	private static final int field_15603 = ".json".length();
	private final Map<class_2960, class_3494<T>> field_15602 = Maps.<class_2960, class_3494<T>>newHashMap();
	private final Function<class_2960, T> field_15609;
	private final Predicate<class_2960> field_15604;
	private final String field_15605;
	private final boolean field_15601;
	private final String field_15606;

	public class_3503(Predicate<class_2960> predicate, Function<class_2960, T> function, String string, boolean bl, String string2) {
		this.field_15604 = predicate;
		this.field_15609 = function;
		this.field_15605 = string;
		this.field_15601 = bl;
		this.field_15606 = string2;
	}

	public void method_15190(class_3494<T> arg) {
		if (this.field_15602.containsKey(arg.method_15143())) {
			throw new IllegalArgumentException("Duplicate " + this.field_15606 + " tag '" + arg.method_15143() + "'");
		} else {
			this.field_15602.put(arg.method_15143(), arg);
		}
	}

	@Nullable
	public class_3494<T> method_15193(class_2960 arg) {
		return (class_3494<T>)this.field_15602.get(arg);
	}

	public class_3494<T> method_15188(class_2960 arg) {
		class_3494<T> lv = (class_3494<T>)this.field_15602.get(arg);
		return lv == null ? new class_3494<>(arg) : lv;
	}

	public Collection<class_2960> method_15189() {
		return this.field_15602.keySet();
	}

	@Environment(EnvType.CLIENT)
	public Collection<class_2960> method_15191(T object) {
		List<class_2960> list = Lists.<class_2960>newArrayList();

		for (Entry<class_2960, class_3494<T>> entry : this.field_15602.entrySet()) {
			if (((class_3494)entry.getValue()).method_15141(object)) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public void method_15195() {
		this.field_15602.clear();
	}

	public void method_15192(class_3300 arg) {
		Map<class_2960, class_3494.class_3495<T>> map = Maps.<class_2960, class_3494.class_3495<T>>newHashMap();

		for (class_2960 lv : arg.method_14488(this.field_15605, string -> string.endsWith(".json"))) {
			String string = lv.method_12832();
			class_2960 lv2 = new class_2960(lv.method_12836(), string.substring(this.field_15605.length() + 1, string.length() - field_15603));

			try {
				for (class_3298 lv3 : arg.method_14489(lv)) {
					try {
						JsonObject jsonObject = class_3518.method_15284(field_15608, IOUtils.toString(lv3.method_14482(), StandardCharsets.UTF_8), JsonObject.class);
						if (jsonObject == null) {
							field_15607.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.field_15606, lv2, lv, lv3.method_14480());
						} else {
							class_3494.class_3495<T> lv4 = (class_3494.class_3495<T>)map.getOrDefault(lv2, class_3494.class_3495.method_15146());
							lv4.method_15147(this.field_15604, this.field_15609, jsonObject);
							map.put(lv2, lv4);
						}
					} catch (RuntimeException | IOException var15) {
						field_15607.error("Couldn't read {} tag list {} from {} in data pack {}", this.field_15606, lv2, lv, lv3.method_14480(), var15);
					} finally {
						IOUtils.closeQuietly(lv3);
					}
				}
			} catch (IOException var17) {
				field_15607.error("Couldn't read {} tag list {} from {}", this.field_15606, lv2, lv, var17);
			}
		}

		while (!map.isEmpty()) {
			boolean bl = false;
			Iterator<Entry<class_2960, class_3494.class_3495<T>>> iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<class_2960, class_3494.class_3495<T>> entry = (Entry<class_2960, class_3494.class_3495<T>>)iterator.next();
				if (((class_3494.class_3495)entry.getValue()).method_15152(this::method_15193)) {
					bl = true;
					this.method_15190(((class_3494.class_3495)entry.getValue()).method_15144((class_2960)entry.getKey()));
					iterator.remove();
				}
			}

			if (!bl) {
				for (Entry<class_2960, class_3494.class_3495<T>> entry : map.entrySet()) {
					field_15607.error(
						"Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.field_15606, entry.getKey()
					);
				}
				break;
			}
		}

		for (Entry<class_2960, class_3494.class_3495<T>> entry2 : map.entrySet()) {
			this.method_15190(((class_3494.class_3495)entry2.getValue()).method_15154(this.field_15601).method_15144((class_2960)entry2.getKey()));
		}
	}

	public Map<class_2960, class_3494<T>> method_15196() {
		return this.field_15602;
	}
}
