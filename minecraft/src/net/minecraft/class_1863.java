package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1863 implements class_3302 {
	private static final Logger field_9027 = LogManager.getLogger();
	public static final int field_9026 = "recipes/".length();
	public static final int field_9025 = ".json".length();
	private final Map<class_2960, class_1860> field_9023 = Maps.<class_2960, class_1860>newHashMap();
	private boolean field_9024;

	@Override
	public void method_14491(class_3300 arg) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		this.field_9024 = false;
		this.field_9023.clear();

		for (class_2960 lv : arg.method_14488("recipes", stringx -> stringx.endsWith(".json"))) {
			String string = lv.method_12832();
			class_2960 lv2 = new class_2960(lv.method_12836(), string.substring(field_9026, string.length() - field_9025));

			try {
				class_3298 lv3 = arg.method_14486(lv);
				Throwable var8 = null;

				try {
					JsonObject jsonObject = class_3518.method_15284(gson, IOUtils.toString(lv3.method_14482(), StandardCharsets.UTF_8), JsonObject.class);
					if (jsonObject == null) {
						field_9027.error("Couldn't load recipe {} as it's null or empty", lv2);
					} else {
						this.method_8125(class_1865.method_8135(lv2, jsonObject));
					}
				} catch (Throwable var19) {
					var8 = var19;
					throw var19;
				} finally {
					if (lv3 != null) {
						if (var8 != null) {
							try {
								lv3.close();
							} catch (Throwable var18) {
								var8.addSuppressed(var18);
							}
						} else {
							lv3.close();
						}
					}
				}
			} catch (IllegalArgumentException | JsonParseException var21) {
				field_9027.error("Parsing error loading recipe {}", lv2, var21);
				this.field_9024 = true;
			} catch (IOException var22) {
				field_9027.error("Couldn't read custom advancement {} from {}", lv2, lv, var22);
				this.field_9024 = true;
			}
		}

		field_9027.info("Loaded {} recipes", this.field_9023.size());
	}

	public void method_8125(class_1860 arg) {
		if (this.field_9023.containsKey(arg.method_8114())) {
			throw new IllegalStateException("Duplicate recipe ignored with ID " + arg.method_8114());
		} else {
			this.field_9023.put(arg.method_8114(), arg);
		}
	}

	public class_1799 method_8129(class_1263 arg, class_1937 arg2) {
		for (class_1860 lv : this.field_9023.values()) {
			if (lv.method_8115(arg, arg2)) {
				return lv.method_8116(arg);
			}
		}

		return class_1799.field_8037;
	}

	@Nullable
	public class_1860 method_8132(class_1263 arg, class_1937 arg2) {
		for (class_1860 lv : this.field_9023.values()) {
			if (lv.method_8115(arg, arg2)) {
				return lv;
			}
		}

		return null;
	}

	public class_2371<class_1799> method_8128(class_1263 arg, class_1937 arg2) {
		for (class_1860 lv : this.field_9023.values()) {
			if (lv.method_8115(arg, arg2)) {
				return lv.method_8111(arg);
			}
		}

		class_2371<class_1799> lv2 = class_2371.method_10213(arg.method_5439(), class_1799.field_8037);

		for (int i = 0; i < lv2.size(); i++) {
			lv2.set(i, arg.method_5438(i));
		}

		return lv2;
	}

	@Nullable
	public class_1860 method_8130(class_2960 arg) {
		return (class_1860)this.field_9023.get(arg);
	}

	public Collection<class_1860> method_8126() {
		return this.field_9023.values();
	}

	public Collection<class_2960> method_8127() {
		return this.field_9023.keySet();
	}

	@Environment(EnvType.CLIENT)
	public void method_8133() {
		this.field_9023.clear();
	}
}
