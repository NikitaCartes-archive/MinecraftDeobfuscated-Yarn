package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_396 implements class_389 {
	private static final Logger field_16242 = LogManager.getLogger();
	private final class_2960 field_2330;
	private final float field_2329;
	private final float field_2328;
	private final float field_2327;
	private final float field_2326;
	private final String field_2331;

	public class_396(class_2960 arg, float f, float g, float h, float i, String string) {
		this.field_2330 = arg;
		this.field_2329 = f;
		this.field_2328 = g;
		this.field_2327 = h;
		this.field_2326 = i;
		this.field_2331 = string;
	}

	public static class_389 method_2059(JsonObject jsonObject) {
		float f = 0.0F;
		float g = 0.0F;
		if (jsonObject.has("shift")) {
			JsonArray jsonArray = jsonObject.getAsJsonArray("shift");
			if (jsonArray.size() != 2) {
				throw new JsonParseException("Expected 2 elements in 'shift', found " + jsonArray.size());
			}

			f = class_3518.method_15269(jsonArray.get(0), "shift[0]");
			g = class_3518.method_15269(jsonArray.get(1), "shift[1]");
		}

		StringBuilder stringBuilder = new StringBuilder();
		if (jsonObject.has("skip")) {
			JsonElement jsonElement = jsonObject.get("skip");
			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray2 = class_3518.method_15252(jsonElement, "skip");

				for (int i = 0; i < jsonArray2.size(); i++) {
					stringBuilder.append(class_3518.method_15287(jsonArray2.get(i), "skip[" + i + "]"));
				}
			} else {
				stringBuilder.append(class_3518.method_15287(jsonElement, "skip"));
			}
		}

		return new class_396(
			new class_2960(class_3518.method_15265(jsonObject, "file")),
			class_3518.method_15277(jsonObject, "size", 11.0F),
			class_3518.method_15277(jsonObject, "oversample", 1.0F),
			f,
			g,
			stringBuilder.toString()
		);
	}

	@Nullable
	@Override
	public class_390 method_2039(class_3300 arg) {
		try {
			class_3298 lv = arg.method_14486(new class_2960(this.field_2330.method_12836(), "font/" + this.field_2330.method_12832()));
			Throwable var3 = null;

			class_395 var5;
			try {
				field_16242.info("Loading font");
				ByteBuffer byteBuffer = TextureUtil.readResource(lv.method_14482());
				byteBuffer.flip();
				field_16242.info("Reading font");
				var5 = new class_395(class_395.method_15975(byteBuffer), this.field_2329, this.field_2328, this.field_2327, this.field_2326, this.field_2331);
			} catch (Throwable var15) {
				var3 = var15;
				throw var15;
			} finally {
				if (lv != null) {
					if (var3 != null) {
						try {
							lv.close();
						} catch (Throwable var14) {
							var3.addSuppressed(var14);
						}
					} else {
						lv.close();
					}
				}
			}

			return var5;
		} catch (IOException var17) {
			field_16242.error("Couldn't load truetype font {}", this.field_2330, var17);
			return null;
		}
	}
}
