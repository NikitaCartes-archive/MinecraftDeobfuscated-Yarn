package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_383;
import net.minecraft.class_390;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class TextureFont implements class_390 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final NativeImage field_2285;
	private final Char2ObjectMap<TextureFont.class_388> field_2284;

	public TextureFont(NativeImage nativeImage, Char2ObjectMap<TextureFont.class_388> char2ObjectMap) {
		this.field_2285 = nativeImage;
		this.field_2284 = char2ObjectMap;
	}

	@Override
	public void close() {
		this.field_2285.close();
	}

	@Nullable
	@Override
	public class_383 method_2040(char c) {
		return this.field_2284.get(c);
	}

	@Environment(EnvType.CLIENT)
	public static class Loader implements FontLoader {
		private final Identifier filename;
		private final List<String> field_2290;
		private final int height;
		private final int ascent;

		public Loader(Identifier identifier, int i, int j, List<String> list) {
			this.filename = new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
			this.field_2290 = list;
			this.height = i;
			this.ascent = j;
		}

		public static TextureFont.Loader method_2037(JsonObject jsonObject) {
			int i = JsonHelper.getInt(jsonObject, "height", 8);
			int j = JsonHelper.getInt(jsonObject, "ascent");
			if (j > i) {
				throw new JsonParseException("Ascent " + j + " higher than height " + i);
			} else {
				List<String> list = Lists.<String>newArrayList();
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "chars");

				for (int k = 0; k < jsonArray.size(); k++) {
					String string = JsonHelper.asString(jsonArray.get(k), "chars[" + k + "]");
					if (k > 0) {
						int l = string.length();
						int m = ((String)list.get(0)).length();
						if (l != m) {
							throw new JsonParseException("Elements of chars have to be the same lenght (found: " + l + ", expected: " + m + "), pad with space or \\u0000");
						}
					}

					list.add(string);
				}

				if (!list.isEmpty() && !((String)list.get(0)).isEmpty()) {
					return new TextureFont.Loader(new Identifier(JsonHelper.getString(jsonObject, "file")), i, j, list);
				} else {
					throw new JsonParseException("Expected to find data in chars, found none.");
				}
			}
		}

		@Nullable
		@Override
		public class_390 load(ResourceManager resourceManager) {
			try {
				Resource resource = resourceManager.getResource(this.filename);
				Throwable var3 = null;

				TextureFont var27;
				try {
					NativeImage nativeImage = NativeImage.fromInputStream(NativeImage.Format.field_4997, resource.getInputStream());
					int i = nativeImage.getWidth();
					int j = nativeImage.getHeight();
					int k = i / ((String)this.field_2290.get(0)).length();
					int l = j / this.field_2290.size();
					float f = (float)this.height / (float)l;
					Char2ObjectMap<TextureFont.class_388> char2ObjectMap = new Char2ObjectOpenHashMap<>();

					for (int m = 0; m < this.field_2290.size(); m++) {
						String string = (String)this.field_2290.get(m);

						for (int n = 0; n < string.length(); n++) {
							char c = string.charAt(n);
							if (c != 0 && c != ' ') {
								int o = this.method_2038(nativeImage, k, l, n, m);
								char2ObjectMap.put(c, new TextureFont.class_388(f, nativeImage, n * k, m * l, k, l, (int)(0.5 + (double)((float)o * f)) + 1, this.ascent));
							}
						}
					}

					var27 = new TextureFont(nativeImage, char2ObjectMap);
				} catch (Throwable var24) {
					var3 = var24;
					throw var24;
				} finally {
					if (resource != null) {
						if (var3 != null) {
							try {
								resource.close();
							} catch (Throwable var23) {
								var3.addSuppressed(var23);
							}
						} else {
							resource.close();
						}
					}
				}

				return var27;
			} catch (IOException var26) {
				throw new RuntimeException(var26.getMessage());
			}
		}

		private int method_2038(NativeImage nativeImage, int i, int j, int k, int l) {
			int m;
			for (m = i - 1; m >= 0; m--) {
				int n = k * i + m;

				for (int o = 0; o < j; o++) {
					int p = l * j + o;
					if (nativeImage.method_4311(n, p) != 0) {
						return m + 1;
					}
				}
			}

			return m + 1;
		}
	}

	@Environment(EnvType.CLIENT)
	static final class class_388 implements class_383 {
		private final float field_2296;
		private final NativeImage field_2294;
		private final int field_2298;
		private final int field_2297;
		private final int field_2295;
		private final int field_2293;
		private final int advance;
		private final int field_2291;

		private class_388(float f, NativeImage nativeImage, int i, int j, int k, int l, int m, int n) {
			this.field_2296 = f;
			this.field_2294 = nativeImage;
			this.field_2298 = i;
			this.field_2297 = j;
			this.field_2295 = k;
			this.field_2293 = l;
			this.advance = m;
			this.field_2291 = n;
		}

		@Override
		public float method_2035() {
			return 1.0F / this.field_2296;
		}

		@Override
		public int method_2031() {
			return this.field_2295;
		}

		@Override
		public int method_2032() {
			return this.field_2293;
		}

		@Override
		public float getAdvance() {
			return (float)this.advance;
		}

		@Override
		public float method_15976() {
			return class_383.super.method_15976() + 7.0F - (float)this.field_2291;
		}

		@Override
		public void method_2030(int i, int j) {
			this.field_2294.upload(0, i, j, this.field_2298, this.field_2297, this.field_2295, this.field_2293, false);
		}

		@Override
		public boolean method_2033() {
			return this.field_2294.getFormat().getBytesPerPixel() > 1;
		}
	}
}
