package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1058 {
	private final class_2960 field_5257;
	protected final int field_5261;
	protected final int field_5260;
	protected class_1011[] field_5262;
	@Nullable
	protected int[] field_5265;
	@Nullable
	protected int[] field_5264;
	protected class_1011[] field_5263;
	private class_1079 field_5271;
	protected int field_5258;
	protected int field_5256;
	private float field_5270;
	private float field_5269;
	private float field_5268;
	private float field_5267;
	protected int field_5273;
	protected int field_5272;
	private static final int[] field_5259 = new int[4];
	private static final float[] field_5274 = class_156.method_654(new float[256], fs -> {
		for (int i = 0; i < fs.length; i++) {
			fs[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2);
		}
	});

	protected class_1058(class_2960 arg, int i, int j) {
		this.field_5257 = arg;
		this.field_5261 = i;
		this.field_5260 = j;
	}

	protected class_1058(class_2960 arg, class_1050 arg2, @Nullable class_1079 arg3) {
		this.field_5257 = arg;
		if (arg3 != null) {
			Pair<Integer, Integer> pair = method_18341(arg3.method_4687(), arg3.method_4686(), arg2.field_5227, arg2.field_5226);
			this.field_5261 = pair.getFirst();
			this.field_5260 = pair.getSecond();
			if (!method_18340(arg2.field_5227, this.field_5261) || !method_18340(arg2.field_5226, this.field_5260)) {
				throw new IllegalArgumentException(
					String.format("Image size %s,%s is not multiply of frame size %s,%s", this.field_5261, this.field_5260, arg2.field_5227, arg2.field_5226)
				);
			}
		} else {
			this.field_5261 = arg2.field_5227;
			this.field_5260 = arg2.field_5226;
		}

		this.field_5271 = arg3;
	}

	private static Pair<Integer, Integer> method_18341(int i, int j, int k, int l) {
		if (i != -1) {
			return j != -1 ? Pair.of(i, j) : Pair.of(i, l);
		} else if (j != -1) {
			return Pair.of(k, j);
		} else {
			int m = Math.min(k, l);
			return Pair.of(m, m);
		}
	}

	private static boolean method_18340(int i, int j) {
		return i / j * j == i;
	}

	private void method_4591(int i) {
		class_1011[] lvs = new class_1011[i + 1];
		lvs[0] = this.field_5262[0];
		if (i > 0) {
			boolean bl = false;

			label71:
			for (int j = 0; j < this.field_5262[0].method_4307(); j++) {
				for (int k = 0; k < this.field_5262[0].method_4323(); k++) {
					if (this.field_5262[0].method_4315(j, k) >> 24 == 0) {
						bl = true;
						break label71;
					}
				}
			}

			for (int j = 1; j <= i; j++) {
				if (this.field_5262.length > j && this.field_5262[j] != null) {
					lvs[j] = this.field_5262[j];
				} else {
					class_1011 lv = lvs[j - 1];
					class_1011 lv2 = new class_1011(lv.method_4307() >> 1, lv.method_4323() >> 1, false);
					int l = lv2.method_4307();
					int m = lv2.method_4323();

					for (int n = 0; n < l; n++) {
						for (int o = 0; o < m; o++) {
							lv2.method_4305(
								n,
								o,
								method_4581(
									lv.method_4315(n * 2 + 0, o * 2 + 0),
									lv.method_4315(n * 2 + 1, o * 2 + 0),
									lv.method_4315(n * 2 + 0, o * 2 + 1),
									lv.method_4315(n * 2 + 1, o * 2 + 1),
									bl
								)
							);
						}
					}

					lvs[j] = lv2;
				}
			}

			for (int jx = i + 1; jx < this.field_5262.length; jx++) {
				if (this.field_5262[jx] != null) {
					this.field_5262[jx].close();
				}
			}
		}

		this.field_5262 = lvs;
	}

	private static int method_4581(int i, int j, int k, int l, boolean bl) {
		if (bl) {
			field_5259[0] = i;
			field_5259[1] = j;
			field_5259[2] = k;
			field_5259[3] = l;
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			float m = 0.0F;

			for (int n = 0; n < 4; n++) {
				if (field_5259[n] >> 24 != 0) {
					f += method_4574(field_5259[n] >> 24);
					g += method_4574(field_5259[n] >> 16);
					h += method_4574(field_5259[n] >> 8);
					m += method_4574(field_5259[n] >> 0);
				}
			}

			f /= 4.0F;
			g /= 4.0F;
			h /= 4.0F;
			m /= 4.0F;
			int nx = (int)(Math.pow((double)f, 0.45454545454545453) * 255.0);
			int o = (int)(Math.pow((double)g, 0.45454545454545453) * 255.0);
			int p = (int)(Math.pow((double)h, 0.45454545454545453) * 255.0);
			int q = (int)(Math.pow((double)m, 0.45454545454545453) * 255.0);
			if (nx < 96) {
				nx = 0;
			}

			return nx << 24 | o << 16 | p << 8 | q;
		} else {
			int r = method_4600(i, j, k, l, 24);
			int s = method_4600(i, j, k, l, 16);
			int t = method_4600(i, j, k, l, 8);
			int u = method_4600(i, j, k, l, 0);
			return r << 24 | s << 16 | t << 8 | u;
		}
	}

	private static int method_4600(int i, int j, int k, int l, int m) {
		float f = method_4574(i >> m);
		float g = method_4574(j >> m);
		float h = method_4574(k >> m);
		float n = method_4574(l >> m);
		float o = (float)((double)((float)Math.pow((double)(f + g + h + n) * 0.25, 0.45454545454545453)));
		return (int)((double)o * 255.0);
	}

	private static float method_4574(int i) {
		return field_5274[i & 0xFF];
	}

	private void method_4573(int i) {
		int j = 0;
		int k = 0;
		if (this.field_5265 != null) {
			j = this.field_5265[i] * this.field_5261;
			k = this.field_5264[i] * this.field_5260;
		}

		this.method_4579(j, k, this.field_5262);
	}

	private void method_4579(int i, int j, class_1011[] args) {
		for (int k = 0; k < this.field_5262.length; k++) {
			args[k].method_4312(k, this.field_5258 >> k, this.field_5256 >> k, i >> k, j >> k, this.field_5261 >> k, this.field_5260 >> k, this.field_5262.length > 1);
		}
	}

	public void method_4587(int i, int j, int k, int l) {
		this.field_5258 = k;
		this.field_5256 = l;
		this.field_5270 = (float)k / (float)i;
		this.field_5269 = (float)(k + this.field_5261) / (float)i;
		this.field_5268 = (float)l / (float)j;
		this.field_5267 = (float)(l + this.field_5260) / (float)j;
	}

	public int method_4578() {
		return this.field_5261;
	}

	public int method_4595() {
		return this.field_5260;
	}

	public float method_4594() {
		return this.field_5270;
	}

	public float method_4577() {
		return this.field_5269;
	}

	public float method_4580(double d) {
		float f = this.field_5269 - this.field_5270;
		return this.field_5270 + f * (float)d / 16.0F;
	}

	public float method_4582(float f) {
		float g = this.field_5269 - this.field_5270;
		return (f - this.field_5270) / g * 16.0F;
	}

	public float method_4593() {
		return this.field_5268;
	}

	public float method_4575() {
		return this.field_5267;
	}

	public float method_4570(double d) {
		float f = this.field_5267 - this.field_5268;
		return this.field_5268 + f * (float)d / 16.0F;
	}

	public float method_4572(float f) {
		float g = this.field_5267 - this.field_5268;
		return (f - this.field_5268) / g * 16.0F;
	}

	public class_2960 method_4598() {
		return this.field_5257;
	}

	public void method_4597() {
		this.field_5272++;
		if (this.field_5272 >= this.field_5271.method_4683(this.field_5273)) {
			int i = this.field_5271.method_4680(this.field_5273);
			int j = this.field_5271.method_4682() == 0 ? this.method_4592() : this.field_5271.method_4682();
			this.field_5273 = (this.field_5273 + 1) % j;
			this.field_5272 = 0;
			int k = this.field_5271.method_4680(this.field_5273);
			if (i != k && k >= 0 && k < this.method_4592()) {
				this.method_4573(k);
			}
		} else if (this.field_5271.method_4685()) {
			this.method_4585();
		}
	}

	private void method_4585() {
		double d = 1.0 - (double)this.field_5272 / (double)this.field_5271.method_4683(this.field_5273);
		int i = this.field_5271.method_4680(this.field_5273);
		int j = this.field_5271.method_4682() == 0 ? this.method_4592() : this.field_5271.method_4682();
		int k = this.field_5271.method_4680((this.field_5273 + 1) % j);
		if (i != k && k >= 0 && k < this.method_4592()) {
			if (this.field_5263 == null || this.field_5263.length != this.field_5262.length) {
				if (this.field_5263 != null) {
					for (class_1011 lv : this.field_5263) {
						if (lv != null) {
							lv.close();
						}
					}
				}

				this.field_5263 = new class_1011[this.field_5262.length];
			}

			for (int l = 0; l < this.field_5262.length; l++) {
				int m = this.field_5261 >> l;
				int n = this.field_5260 >> l;
				if (this.field_5263[l] == null) {
					this.field_5263[l] = new class_1011(m, n, false);
				}

				for (int o = 0; o < n; o++) {
					for (int p = 0; p < m; p++) {
						int q = this.method_4589(i, l, p, o);
						int r = this.method_4589(k, l, p, o);
						int s = this.method_4571(d, q >> 16 & 0xFF, r >> 16 & 0xFF);
						int t = this.method_4571(d, q >> 8 & 0xFF, r >> 8 & 0xFF);
						int u = this.method_4571(d, q & 0xFF, r & 0xFF);
						this.field_5263[l].method_4305(p, o, q & 0xFF000000 | s << 16 | t << 8 | u);
					}
				}
			}

			this.method_4579(0, 0, this.field_5263);
		}
	}

	private int method_4571(double d, int i, int j) {
		return (int)(d * (double)i + (1.0 - d) * (double)j);
	}

	public int method_4592() {
		return this.field_5265 == null ? 0 : this.field_5265.length;
	}

	public void method_4576(class_3298 arg, int i) throws IOException {
		class_1011 lv = class_1011.method_4309(arg.method_14482());
		this.field_5262 = new class_1011[i];
		this.field_5262[0] = lv;
		int j;
		if (this.field_5271 != null && this.field_5271.method_4687() != -1) {
			j = lv.method_4307() / this.field_5271.method_4687();
		} else {
			j = lv.method_4307() / this.field_5261;
		}

		int k;
		if (this.field_5271 != null && this.field_5271.method_4686() != -1) {
			k = lv.method_4323() / this.field_5271.method_4686();
		} else {
			k = lv.method_4323() / this.field_5260;
		}

		if (this.field_5271 != null && this.field_5271.method_4682() > 0) {
			int l = (Integer)this.field_5271.method_4688().stream().max(Integer::compareTo).get() + 1;
			this.field_5265 = new int[l];
			this.field_5264 = new int[l];
			Arrays.fill(this.field_5265, -1);
			Arrays.fill(this.field_5264, -1);

			for (int m : this.field_5271.method_4688()) {
				if (m >= j * k) {
					throw new RuntimeException("invalid frameindex " + m);
				}

				int n = m / j;
				int o = m % j;
				this.field_5265[m] = o;
				this.field_5264[m] = n;
			}
		} else {
			List<class_1080> list = Lists.<class_1080>newArrayList();
			int p = j * k;
			this.field_5265 = new int[p];
			this.field_5264 = new int[p];

			for (int m = 0; m < k; m++) {
				for (int n = 0; n < j; n++) {
					int o = m * j + n;
					this.field_5265[o] = n;
					this.field_5264[o] = m;
					list.add(new class_1080(o, -1));
				}
			}

			int m = 1;
			boolean bl = false;
			if (this.field_5271 != null) {
				m = this.field_5271.method_4684();
				bl = this.field_5271.method_4685();
			}

			this.field_5271 = new class_1079(list, this.field_5261, this.field_5260, m, bl);
		}
	}

	public void method_4590(int i) {
		try {
			this.method_4591(i);
		} catch (Throwable var5) {
			class_128 lv = class_128.method_560(var5, "Generating mipmaps for frame");
			class_129 lv2 = lv.method_562("Frame being iterated");
			lv2.method_577("Frame sizes", () -> {
				StringBuilder stringBuilder = new StringBuilder();

				for (class_1011 lvx : this.field_5262) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(lvx == null ? "null" : lvx.method_4307() + "x" + lvx.method_4323());
				}

				return stringBuilder.toString();
			});
			throw new class_148(lv);
		}
	}

	public void method_4588() {
		if (this.field_5262 != null) {
			for (class_1011 lv : this.field_5262) {
				if (lv != null) {
					lv.close();
				}
			}
		}

		this.field_5262 = null;
		if (this.field_5263 != null) {
			for (class_1011 lvx : this.field_5263) {
				if (lvx != null) {
					lvx.close();
				}
			}
		}

		this.field_5263 = null;
	}

	public boolean method_4599() {
		return this.field_5271 != null && this.field_5271.method_4682() > 1;
	}

	public String toString() {
		int i = this.field_5265 == null ? 0 : this.field_5265.length;
		return "TextureAtlasSprite{name='"
			+ this.field_5257
			+ '\''
			+ ", frameCount="
			+ i
			+ ", x="
			+ this.field_5258
			+ ", y="
			+ this.field_5256
			+ ", height="
			+ this.field_5260
			+ ", width="
			+ this.field_5261
			+ ", u0="
			+ this.field_5270
			+ ", u1="
			+ this.field_5269
			+ ", v0="
			+ this.field_5268
			+ ", v1="
			+ this.field_5267
			+ '}';
	}

	private int method_4589(int i, int j, int k, int l) {
		return this.field_5262[j].method_4315(k + (this.field_5265[i] * this.field_5261 >> j), l + (this.field_5264[i] * this.field_5260 >> j));
	}

	public boolean method_4583(int i, int j, int k) {
		return (this.field_5262[0].method_4315(j + this.field_5265[i] * this.field_5261, k + this.field_5264[i] * this.field_5260) >> 24 & 0xFF) == 0;
	}

	public void method_4584() {
		this.method_4573(0);
	}
}
