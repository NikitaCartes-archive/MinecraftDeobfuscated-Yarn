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
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class TrueTypeFontLoader implements FontLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Identifier filename;
	private final float size;
	private final float oversample;
	private final float shiftX;
	private final float shiftY;
	private final String excludedCharacters;

	public TrueTypeFontLoader(Identifier filename, float size, float oversample, float shiftX, float shiftY, String excludedCharacters) {
		this.filename = filename;
		this.size = size;
		this.oversample = oversample;
		this.shiftX = shiftX;
		this.shiftY = shiftY;
		this.excludedCharacters = excludedCharacters;
	}

	public static FontLoader fromJson(JsonObject json) {
		float f = 0.0F;
		float g = 0.0F;
		if (json.has("shift")) {
			JsonArray jsonArray = json.getAsJsonArray("shift");
			if (jsonArray.size() != 2) {
				throw new JsonParseException("Expected 2 elements in 'shift', found " + jsonArray.size());
			}

			f = JsonHelper.asFloat(jsonArray.get(0), "shift[0]");
			g = JsonHelper.asFloat(jsonArray.get(1), "shift[1]");
		}

		StringBuilder stringBuilder = new StringBuilder();
		if (json.has("skip")) {
			JsonElement jsonElement = json.get("skip");
			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray2 = JsonHelper.asArray(jsonElement, "skip");

				for (int i = 0; i < jsonArray2.size(); i++) {
					stringBuilder.append(JsonHelper.asString(jsonArray2.get(i), "skip[" + i + "]"));
				}
			} else {
				stringBuilder.append(JsonHelper.asString(jsonElement, "skip"));
			}
		}

		return new TrueTypeFontLoader(
			new Identifier(JsonHelper.getString(json, "file")),
			JsonHelper.getFloat(json, "size", 11.0F),
			JsonHelper.getFloat(json, "oversample", 1.0F),
			f,
			g,
			stringBuilder.toString()
		);
	}

	@Nullable
	@Override
	public Font load(ResourceManager manager) {
		STBTTFontinfo sTBTTFontinfo = null;
		ByteBuffer byteBuffer = null;

		try {
			Resource resource = manager.getResource(new Identifier(this.filename.getNamespace(), "font/" + this.filename.getPath()));
			Throwable var5 = null;

			TrueTypeFont var6;
			try {
				LOGGER.debug("Loading font {}", this.filename);
				sTBTTFontinfo = STBTTFontinfo.malloc();
				byteBuffer = TextureUtil.readResource(resource.getInputStream());
				byteBuffer.flip();
				LOGGER.debug("Reading font {}", this.filename);
				if (!STBTruetype.stbtt_InitFont(sTBTTFontinfo, byteBuffer)) {
					throw new IOException("Invalid ttf");
				}

				var6 = new TrueTypeFont(byteBuffer, sTBTTFontinfo, this.size, this.oversample, this.shiftX, this.shiftY, this.excludedCharacters);
			} catch (Throwable var16) {
				var5 = var16;
				throw var16;
			} finally {
				if (resource != null) {
					if (var5 != null) {
						try {
							resource.close();
						} catch (Throwable var15) {
							var5.addSuppressed(var15);
						}
					} else {
						resource.close();
					}
				}
			}

			return var6;
		} catch (Exception var18) {
			LOGGER.error("Couldn't load truetype font {}", this.filename, var18);
			if (sTBTTFontinfo != null) {
				sTBTTFontinfo.free();
			}

			MemoryUtil.memFree(byteBuffer);
			return null;
		}
	}
}
