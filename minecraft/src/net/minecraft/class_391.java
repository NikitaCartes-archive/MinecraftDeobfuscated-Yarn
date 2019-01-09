package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_391 implements class_390 {
	private static final Logger field_2303 = LogManager.getLogger();
	private final class_3300 field_2302;
	private final byte[] field_2301;
	private final String field_2300;
	private final Map<class_2960, class_1011> field_2299 = Maps.<class_2960, class_1011>newHashMap();

	public class_391(class_3300 arg, byte[] bs, String string) {
		this.field_2302 = arg;
		this.field_2301 = bs;
		this.field_2300 = string;

		for (int i = 0; i < 256; i++) {
			char c = (char)(i * 256);
			class_2960 lv = this.method_2041(c);

			try {
				class_3298 lv2 = this.field_2302.method_14486(lv);
				Throwable var8 = null;

				try (class_1011 lv3 = class_1011.method_4310(class_1011.class_1012.field_4997, lv2.method_14482())) {
					if (lv3.method_4307() == 256 && lv3.method_4323() == 256) {
						for (int j = 0; j < 256; j++) {
							byte b = bs[c + j];
							if (b != 0 && method_2043(b) > method_2044(b)) {
								bs[c + j] = 0;
							}
						}
						continue;
					}
				} catch (Throwable var41) {
					var8 = var41;
					throw var41;
				} finally {
					if (lv2 != null) {
						if (var8 != null) {
							try {
								lv2.close();
							} catch (Throwable var37) {
								var8.addSuppressed(var37);
							}
						} else {
							lv2.close();
						}
					}
				}
			} catch (IOException var43) {
			}

			Arrays.fill(bs, c, c + 256, (byte)0);
		}
	}

	@Override
	public void close() {
		this.field_2299.values().forEach(class_1011::close);
	}

	private class_2960 method_2041(char c) {
		class_2960 lv = new class_2960(String.format(this.field_2300, String.format("%02x", c / 256)));
		return new class_2960(lv.method_12836(), "textures/" + lv.method_12832());
	}

	@Nullable
	@Override
	public class_383 method_2040(char c) {
		byte b = this.field_2301[c];
		if (b != 0) {
			class_1011 lv = (class_1011)this.field_2299.computeIfAbsent(this.method_2041(c), this::method_2042);
			if (lv != null) {
				int i = method_2043(b);
				return new class_391.class_393(c % 16 * 16 + i, (c & 255) / 16 * 16, method_2044(b) - i, 16, lv);
			}
		}

		return null;
	}

	@Nullable
	private class_1011 method_2042(class_2960 arg) {
		try {
			class_3298 lv = this.field_2302.method_14486(arg);
			Throwable var3 = null;

			class_1011 var4;
			try {
				var4 = class_1011.method_4310(class_1011.class_1012.field_4997, lv.method_14482());
			} catch (Throwable var14) {
				var3 = var14;
				throw var14;
			} finally {
				if (lv != null) {
					if (var3 != null) {
						try {
							lv.close();
						} catch (Throwable var13) {
							var3.addSuppressed(var13);
						}
					} else {
						lv.close();
					}
				}
			}

			return var4;
		} catch (IOException var16) {
			field_2303.error("Couldn't load texture {}", arg, var16);
			return null;
		}
	}

	private static int method_2043(byte b) {
		return b >> 4 & 15;
	}

	private static int method_2044(byte b) {
		return (b & 15) + 1;
	}

	@Environment(EnvType.CLIENT)
	public static class class_392 implements class_389 {
		private final class_2960 field_2304;
		private final String field_2305;

		public class_392(class_2960 arg, String string) {
			this.field_2304 = arg;
			this.field_2305 = string;
		}

		public static class_389 method_2046(JsonObject jsonObject) {
			return new class_391.class_392(new class_2960(class_3518.method_15265(jsonObject, "sizes")), class_3518.method_15265(jsonObject, "template"));
		}

		@Nullable
		@Override
		public class_390 method_2039(class_3300 arg) {
			try {
				class_3298 lv = class_310.method_1551().method_1478().method_14486(this.field_2304);
				Throwable var3 = null;

				class_391 var5;
				try {
					byte[] bs = new byte[65536];
					lv.method_14482().read(bs);
					var5 = new class_391(arg, bs, this.field_2305);
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
				class_391.field_2303.error("Cannot load {}, unicode glyphs will not render correctly", this.field_2304);
				return null;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_393 implements class_383 {
		private final int field_2309;
		private final int field_2308;
		private final int field_2307;
		private final int field_2306;
		private final class_1011 field_2310;

		private class_393(int i, int j, int k, int l, class_1011 arg) {
			this.field_2309 = k;
			this.field_2308 = l;
			this.field_2307 = i;
			this.field_2306 = j;
			this.field_2310 = arg;
		}

		@Override
		public float method_2035() {
			return 2.0F;
		}

		@Override
		public int method_2031() {
			return this.field_2309;
		}

		@Override
		public int method_2032() {
			return this.field_2308;
		}

		@Override
		public float getAdvance() {
			return (float)(this.field_2309 / 2 + 1);
		}

		@Override
		public void method_2030(int i, int j) {
			this.field_2310.method_4312(0, i, j, this.field_2307, this.field_2306, this.field_2309, this.field_2308, false);
		}

		@Override
		public boolean method_2033() {
			return this.field_2310.method_4318().method_4335() > 1;
		}

		@Override
		public float method_16800() {
			return 0.5F;
		}

		@Override
		public float method_16799() {
			return 0.5F;
		}
	}
}
