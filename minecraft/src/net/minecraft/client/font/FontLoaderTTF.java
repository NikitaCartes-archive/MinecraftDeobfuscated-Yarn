package net.minecraft.client.font;

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
import net.minecraft.class_390;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class FontLoaderTTF implements FontLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Identifier filename;
	private final float size;
	private final float oversample;
	private final float field_2327;
	private final float field_2326;
	private final String field_2331;

	public FontLoaderTTF(Identifier identifier, float f, float g, float h, float i, String string) {
		this.filename = identifier;
		this.size = f;
		this.oversample = g;
		this.field_2327 = h;
		this.field_2326 = i;
		this.field_2331 = string;
	}

	public static FontLoader method_2059(JsonObject jsonObject) {
		float f = 0.0F;
		float g = 0.0F;
		if (jsonObject.has("shift")) {
			JsonArray jsonArray = jsonObject.getAsJsonArray("shift");
			if (jsonArray.size() != 2) {
				throw new JsonParseException("Expected 2 elements in 'shift', found " + jsonArray.size());
			}

			f = JsonHelper.asFloat(jsonArray.get(0), "shift[0]");
			g = JsonHelper.asFloat(jsonArray.get(1), "shift[1]");
		}

		StringBuilder stringBuilder = new StringBuilder();
		if (jsonObject.has("skip")) {
			JsonElement jsonElement = jsonObject.get("skip");
			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray2 = JsonHelper.asArray(jsonElement, "skip");

				for (int i = 0; i < jsonArray2.size(); i++) {
					stringBuilder.append(JsonHelper.asString(jsonArray2.get(i), "skip[" + i + "]"));
				}
			} else {
				stringBuilder.append(JsonHelper.asString(jsonElement, "skip"));
			}
		}

		return new FontLoaderTTF(
			new Identifier(JsonHelper.getString(jsonObject, "file")),
			JsonHelper.getFloat(jsonObject, "size", 11.0F),
			JsonHelper.getFloat(jsonObject, "oversample", 1.0F),
			f,
			g,
			stringBuilder.toString()
		);
	}

	@Nullable
	@Override
	public class_390 load(ResourceManager resourceManager) {
		try {
			Resource resource = resourceManager.getResource(new Identifier(this.filename.getNamespace(), "font/" + this.filename.getPath()));
			Throwable var3 = null;

			TrueTypeFont var5;
			try {
				LOGGER.info("Loading font");
				ByteBuffer byteBuffer = TextureUtil.readResource(resource.getInputStream());
				byteBuffer.flip();
				LOGGER.info("Reading font");
				var5 = new TrueTypeFont(TrueTypeFont.method_15975(byteBuffer), this.size, this.oversample, this.field_2327, this.field_2326, this.field_2331);
			} catch (Throwable var15) {
				var3 = var15;
				throw var15;
			} finally {
				if (resource != null) {
					if (var3 != null) {
						try {
							resource.close();
						} catch (Throwable var14) {
							var3.addSuppressed(var14);
						}
					} else {
						resource.close();
					}
				}
			}

			return var5;
		} catch (IOException var17) {
			LOGGER.error("Couldn't load truetype font {}", this.filename, var17);
			return null;
		}
	}
}
