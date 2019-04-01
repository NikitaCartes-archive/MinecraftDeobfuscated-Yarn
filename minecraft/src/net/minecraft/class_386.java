package net.minecraft;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_386 implements class_390 {
	private static final Logger field_2286 = LogManager.getLogger();
	private final class_1011 field_2285;
	private final Char2ObjectMap<class_386.class_388> field_2284;

	public class_386(class_1011 arg, Char2ObjectMap<class_386.class_388> char2ObjectMap) {
		this.field_2285 = arg;
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
	public static class class_387 implements class_389 {
		private final class_2960 field_2289;
		private final List<String> field_2290;
		private final int field_2288;
		private final int field_2287;

		public class_387(class_2960 arg, int i, int j, List<String> list) {
			this.field_2289 = new class_2960(arg.method_12836(), "textures/" + arg.method_12832());
			this.field_2290 = list;
			this.field_2288 = i;
			this.field_2287 = j;
		}

		public static class_386.class_387 method_2037(JsonObject jsonObject) {
			int i = class_3518.method_15282(jsonObject, "height", 8);
			int j = class_3518.method_15260(jsonObject, "ascent");
			if (j > i) {
				throw new JsonParseException("Ascent " + j + " higher than height " + i);
			} else {
				List<String> list = Lists.<String>newArrayList();
				JsonArray jsonArray = class_3518.method_15261(jsonObject, "chars");

				for (int k = 0; k < jsonArray.size(); k++) {
					String string = class_3518.method_15287(jsonArray.get(k), "chars[" + k + "]");
					if (k > 0) {
						int l = string.length();
						int m = ((String)list.get(0)).length();
						if (l != m) {
							throw new JsonParseException("Elements of chars have to be the same length (found: " + l + ", expected: " + m + "), pad with space or \\u0000");
						}
					}

					list.add(string);
				}

				if (!list.isEmpty() && !((String)list.get(0)).isEmpty()) {
					return new class_386.class_387(new class_2960(class_3518.method_15265(jsonObject, "file")), i, j, list);
				} else {
					throw new JsonParseException("Expected to find data in chars, found none.");
				}
			}
		}

		@Nullable
		@Override
		public class_390 method_2039(class_3300 arg) {
			try {
				class_3298 lv = arg.method_14486(this.field_2289);
				Throwable var3 = null;

				class_386 var27;
				try {
					class_1011 lv2 = class_1011.method_4310(class_1011.class_1012.field_4997, lv.method_14482());
					int i = lv2.method_4307();
					int j = lv2.method_4323();
					int k = i / ((String)this.field_2290.get(0)).length();
					int l = j / this.field_2290.size();
					float f = (float)this.field_2288 / (float)l;
					Char2ObjectMap<class_386.class_388> char2ObjectMap = new Char2ObjectOpenHashMap<>();

					for (int m = 0; m < this.field_2290.size(); m++) {
						String string = (String)this.field_2290.get(m);

						for (int n = 0; n < string.length(); n++) {
							char c = string.charAt(n);
							if (c != 0 && c != ' ') {
								int o = this.method_2038(lv2, k, l, n, m);
								char2ObjectMap.put(c, new class_386.class_388(f, lv2, n * k, m * l, k, l, (int)(0.5 + (double)((float)o * f)) + 1, this.field_2287));
							}
						}
					}

					var27 = new class_386(lv2, char2ObjectMap);
				} catch (Throwable var24) {
					var3 = var24;
					throw var24;
				} finally {
					if (lv != null) {
						if (var3 != null) {
							try {
								lv.close();
							} catch (Throwable var23) {
								var3.addSuppressed(var23);
							}
						} else {
							lv.close();
						}
					}
				}

				return var27;
			} catch (IOException var26) {
				throw new RuntimeException(var26.getMessage());
			}
		}

		private int method_2038(class_1011 arg, int i, int j, int k, int l) {
			int m;
			for (m = i - 1; m >= 0; m--) {
				int n = k * i + m;

				for (int o = 0; o < j; o++) {
					int p = l * j + o;
					if (arg.method_4311(n, p) != 0) {
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
		private final class_1011 field_2294;
		private final int field_2298;
		private final int field_2297;
		private final int field_2295;
		private final int field_2293;
		private final int field_2292;
		private final int field_2291;

		private class_388(float f, class_1011 arg, int i, int j, int k, int l, int m, int n) {
			this.field_2296 = f;
			this.field_2294 = arg;
			this.field_2298 = i;
			this.field_2297 = j;
			this.field_2295 = k;
			this.field_2293 = l;
			this.field_2292 = m;
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
			return (float)this.field_2292;
		}

		@Override
		public float method_15976() {
			return class_383.super.method_15976() + 7.0F - (float)this.field_2291;
		}

		@Override
		public void method_2030(int i, int j) {
			this.field_2294.method_4312(0, i, j, this.field_2298, this.field_2297, this.field_2295, this.field_2293, false);
		}

		@Override
		public boolean method_2033() {
			return this.field_2294.method_4318().method_4335() > 1;
		}
	}
}
