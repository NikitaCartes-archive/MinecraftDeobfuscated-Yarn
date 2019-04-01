package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_775 {
	private final class_1058[] field_4165 = new class_1058[2];
	private final class_1058[] field_4166 = new class_1058[2];
	private class_1058 field_4164;

	protected void method_3345() {
		class_1059 lv = class_310.method_1551().method_1549();
		this.field_4165[0] = class_310.method_1551().method_1554().method_4743().method_3335(class_2246.field_10164.method_9564()).method_4711();
		this.field_4165[1] = lv.method_4608(class_1088.field_5381);
		this.field_4166[0] = class_310.method_1551().method_1554().method_4743().method_3335(class_2246.field_10382.method_9564()).method_4711();
		this.field_4166[1] = lv.method_4608(class_1088.field_5391);
		this.field_4164 = lv.method_4608(class_1088.field_5388);
	}

	private static boolean method_3348(class_1922 arg, class_2338 arg2, class_2350 arg3, class_3610 arg4) {
		class_2338 lv = arg2.method_10093(arg3);
		class_3610 lv2 = arg.method_8316(lv);
		return lv2.method_15772().method_15780(arg4.method_15772());
	}

	private static boolean method_3344(class_1922 arg, class_2338 arg2, class_2350 arg3, float f) {
		class_2338 lv = arg2.method_10093(arg3);
		class_2680 lv2 = arg.method_8320(lv);
		if (lv2.method_11619()) {
			class_265 lv3 = class_259.method_1081(0.0, 0.0, 0.0, 1.0, (double)f, 1.0);
			class_265 lv4 = lv2.method_11615(arg, lv);
			return class_259.method_1083(lv3, lv4, arg3);
		} else {
			return false;
		}
	}

	public boolean method_3347(class_1920 arg, class_2338 arg2, class_287 arg3, class_3610 arg4) {
		boolean bl = arg4.method_15767(class_3486.field_15518);
		class_1058[] lvs = bl ? this.field_4165 : this.field_4166;
		int i = bl ? 16777215 : class_1163.method_4961(arg, arg2);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		boolean bl2 = !method_3348(arg, arg2, class_2350.field_11036, arg4);
		boolean bl3 = !method_3348(arg, arg2, class_2350.field_11033, arg4) && !method_3344(arg, arg2, class_2350.field_11033, 0.8888889F);
		boolean bl4 = !method_3348(arg, arg2, class_2350.field_11043, arg4);
		boolean bl5 = !method_3348(arg, arg2, class_2350.field_11035, arg4);
		boolean bl6 = !method_3348(arg, arg2, class_2350.field_11039, arg4);
		boolean bl7 = !method_3348(arg, arg2, class_2350.field_11034, arg4);
		if (!bl2 && !bl3 && !bl7 && !bl6 && !bl4 && !bl5) {
			return false;
		} else {
			boolean bl8 = false;
			float j = 0.5F;
			float k = 1.0F;
			float l = 0.8F;
			float m = 0.6F;
			float n = this.method_3346(arg, arg2, arg4.method_15772());
			float o = this.method_3346(arg, arg2.method_10072(), arg4.method_15772());
			float p = this.method_3346(arg, arg2.method_10078().method_10072(), arg4.method_15772());
			float q = this.method_3346(arg, arg2.method_10078(), arg4.method_15772());
			double d = (double)arg2.method_10263();
			double e = (double)arg2.method_10264();
			double r = (double)arg2.method_10260();
			float s = 0.001F;
			if (bl2 && !method_3344(arg, arg2, class_2350.field_11036, Math.min(Math.min(n, o), Math.min(p, q)))) {
				bl8 = true;
				n -= 0.001F;
				o -= 0.001F;
				p -= 0.001F;
				q -= 0.001F;
				class_243 lv = arg4.method_15758(arg, arg2);
				float t;
				float v;
				float x;
				float z;
				float u;
				float w;
				float y;
				float aa;
				if (lv.field_1352 == 0.0 && lv.field_1350 == 0.0) {
					class_1058 lv2 = lvs[0];
					t = lv2.method_4580(0.0);
					u = lv2.method_4570(0.0);
					v = t;
					w = lv2.method_4570(16.0);
					x = lv2.method_4580(16.0);
					y = w;
					z = x;
					aa = u;
				} else {
					class_1058 lv2 = lvs[1];
					float ab = (float)class_3532.method_15349(lv.field_1350, lv.field_1352) - (float) (Math.PI / 2);
					float ac = class_3532.method_15374(ab) * 0.25F;
					float ad = class_3532.method_15362(ab) * 0.25F;
					float ae = 8.0F;
					t = lv2.method_4580((double)(8.0F + (-ad - ac) * 16.0F));
					u = lv2.method_4570((double)(8.0F + (-ad + ac) * 16.0F));
					v = lv2.method_4580((double)(8.0F + (-ad + ac) * 16.0F));
					w = lv2.method_4570((double)(8.0F + (ad + ac) * 16.0F));
					x = lv2.method_4580((double)(8.0F + (ad + ac) * 16.0F));
					y = lv2.method_4570((double)(8.0F + (ad - ac) * 16.0F));
					z = lv2.method_4580((double)(8.0F + (ad - ac) * 16.0F));
					aa = lv2.method_4570((double)(8.0F + (-ad - ac) * 16.0F));
				}

				float af = (t + v + x + z) / 4.0F;
				float ab = (u + w + y + aa) / 4.0F;
				float ac = (float)lvs[0].method_4578() / (lvs[0].method_4577() - lvs[0].method_4594());
				float ad = (float)lvs[0].method_4595() / (lvs[0].method_4575() - lvs[0].method_4593());
				float ae = 4.0F / Math.max(ad, ac);
				t = class_3532.method_16439(ae, t, af);
				v = class_3532.method_16439(ae, v, af);
				x = class_3532.method_16439(ae, x, af);
				z = class_3532.method_16439(ae, z, af);
				u = class_3532.method_16439(ae, u, ab);
				w = class_3532.method_16439(ae, w, ab);
				y = class_3532.method_16439(ae, y, ab);
				aa = class_3532.method_16439(ae, aa, ab);
				int ag = this.method_3343(arg, arg2);
				int ah = ag >> 16 & 65535;
				int ai = ag & 65535;
				float aj = 1.0F * f;
				float ak = 1.0F * g;
				float al = 1.0F * h;
				arg3.method_1315(d + 0.0, e + (double)n, r + 0.0).method_1336(aj, ak, al, 1.0F).method_1312((double)t, (double)u).method_1313(ah, ai).method_1344();
				arg3.method_1315(d + 0.0, e + (double)o, r + 1.0).method_1336(aj, ak, al, 1.0F).method_1312((double)v, (double)w).method_1313(ah, ai).method_1344();
				arg3.method_1315(d + 1.0, e + (double)p, r + 1.0).method_1336(aj, ak, al, 1.0F).method_1312((double)x, (double)y).method_1313(ah, ai).method_1344();
				arg3.method_1315(d + 1.0, e + (double)q, r + 0.0).method_1336(aj, ak, al, 1.0F).method_1312((double)z, (double)aa).method_1313(ah, ai).method_1344();
				if (arg4.method_15756(arg, arg2.method_10084())) {
					arg3.method_1315(d + 0.0, e + (double)n, r + 0.0).method_1336(aj, ak, al, 1.0F).method_1312((double)t, (double)u).method_1313(ah, ai).method_1344();
					arg3.method_1315(d + 1.0, e + (double)q, r + 0.0).method_1336(aj, ak, al, 1.0F).method_1312((double)z, (double)aa).method_1313(ah, ai).method_1344();
					arg3.method_1315(d + 1.0, e + (double)p, r + 1.0).method_1336(aj, ak, al, 1.0F).method_1312((double)x, (double)y).method_1313(ah, ai).method_1344();
					arg3.method_1315(d + 0.0, e + (double)o, r + 1.0).method_1336(aj, ak, al, 1.0F).method_1312((double)v, (double)w).method_1313(ah, ai).method_1344();
				}
			}

			if (bl3) {
				float tx = lvs[0].method_4594();
				float vx = lvs[0].method_4577();
				float xx = lvs[0].method_4593();
				float zx = lvs[0].method_4575();
				int am = this.method_3343(arg, arg2.method_10074());
				int an = am >> 16 & 65535;
				int ao = am & 65535;
				float aax = 0.5F * f;
				float ap = 0.5F * g;
				float af = 0.5F * h;
				arg3.method_1315(d, e, r + 1.0).method_1336(aax, ap, af, 1.0F).method_1312((double)tx, (double)zx).method_1313(an, ao).method_1344();
				arg3.method_1315(d, e, r).method_1336(aax, ap, af, 1.0F).method_1312((double)tx, (double)xx).method_1313(an, ao).method_1344();
				arg3.method_1315(d + 1.0, e, r).method_1336(aax, ap, af, 1.0F).method_1312((double)vx, (double)xx).method_1313(an, ao).method_1344();
				arg3.method_1315(d + 1.0, e, r + 1.0).method_1336(aax, ap, af, 1.0F).method_1312((double)vx, (double)zx).method_1313(an, ao).method_1344();
				bl8 = true;
			}

			for (int aq = 0; aq < 4; aq++) {
				float vx;
				float xx;
				double ar;
				double at;
				double as;
				double au;
				class_2350 lv3;
				boolean bl9;
				if (aq == 0) {
					vx = n;
					xx = q;
					ar = d;
					as = d + 1.0;
					at = r + 0.001F;
					au = r + 0.001F;
					lv3 = class_2350.field_11043;
					bl9 = bl4;
				} else if (aq == 1) {
					vx = p;
					xx = o;
					ar = d + 1.0;
					as = d;
					at = r + 1.0 - 0.001F;
					au = r + 1.0 - 0.001F;
					lv3 = class_2350.field_11035;
					bl9 = bl5;
				} else if (aq == 2) {
					vx = o;
					xx = n;
					ar = d + 0.001F;
					as = d + 0.001F;
					at = r + 1.0;
					au = r;
					lv3 = class_2350.field_11039;
					bl9 = bl6;
				} else {
					vx = q;
					xx = p;
					ar = d + 1.0 - 0.001F;
					as = d + 1.0 - 0.001F;
					at = r;
					au = r + 1.0;
					lv3 = class_2350.field_11034;
					bl9 = bl7;
				}

				if (bl9 && !method_3344(arg, arg2, lv3, Math.max(vx, xx))) {
					bl8 = true;
					class_2338 lv4 = arg2.method_10093(lv3);
					class_1058 lv5 = lvs[1];
					if (!bl) {
						class_2248 lv6 = arg.method_8320(lv4).method_11614();
						if (lv6 == class_2246.field_10033 || lv6 instanceof class_2506) {
							lv5 = this.field_4164;
						}
					}

					float av = lv5.method_4580(0.0);
					float aw = lv5.method_4580(8.0);
					float aj = lv5.method_4570((double)((1.0F - vx) * 16.0F * 0.5F));
					float ak = lv5.method_4570((double)((1.0F - xx) * 16.0F * 0.5F));
					float al = lv5.method_4570(8.0);
					int ax = this.method_3343(arg, lv4);
					int ay = ax >> 16 & 65535;
					int az = ax & 65535;
					float ba = aq < 2 ? 0.8F : 0.6F;
					float bb = 1.0F * ba * f;
					float bc = 1.0F * ba * g;
					float bd = 1.0F * ba * h;
					arg3.method_1315(ar, e + (double)vx, at).method_1336(bb, bc, bd, 1.0F).method_1312((double)av, (double)aj).method_1313(ay, az).method_1344();
					arg3.method_1315(as, e + (double)xx, au).method_1336(bb, bc, bd, 1.0F).method_1312((double)aw, (double)ak).method_1313(ay, az).method_1344();
					arg3.method_1315(as, e + 0.0, au).method_1336(bb, bc, bd, 1.0F).method_1312((double)aw, (double)al).method_1313(ay, az).method_1344();
					arg3.method_1315(ar, e + 0.0, at).method_1336(bb, bc, bd, 1.0F).method_1312((double)av, (double)al).method_1313(ay, az).method_1344();
					if (lv5 != this.field_4164) {
						arg3.method_1315(ar, e + 0.0, at).method_1336(bb, bc, bd, 1.0F).method_1312((double)av, (double)al).method_1313(ay, az).method_1344();
						arg3.method_1315(as, e + 0.0, au).method_1336(bb, bc, bd, 1.0F).method_1312((double)aw, (double)al).method_1313(ay, az).method_1344();
						arg3.method_1315(as, e + (double)xx, au).method_1336(bb, bc, bd, 1.0F).method_1312((double)aw, (double)ak).method_1313(ay, az).method_1344();
						arg3.method_1315(ar, e + (double)vx, at).method_1336(bb, bc, bd, 1.0F).method_1312((double)av, (double)aj).method_1313(ay, az).method_1344();
					}
				}
			}

			return bl8;
		}
	}

	private int method_3343(class_1920 arg, class_2338 arg2) {
		int i = arg.method_8313(arg2, 0);
		int j = arg.method_8313(arg2.method_10084(), 0);
		int k = i & 0xFF;
		int l = j & 0xFF;
		int m = i >> 16 & 0xFF;
		int n = j >> 16 & 0xFF;
		return (k > l ? k : l) | (m > n ? m : n) << 16;
	}

	private float method_3346(class_1922 arg, class_2338 arg2, class_3611 arg3) {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < 4; j++) {
			class_2338 lv = arg2.method_10069(-(j & 1), 0, -(j >> 1 & 1));
			if (arg.method_8316(lv.method_10084()).method_15772().method_15780(arg3)) {
				return 1.0F;
			}

			class_3610 lv2 = arg.method_8316(lv);
			if (lv2.method_15772().method_15780(arg3)) {
				float g = lv2.method_15763(arg, lv);
				if (g >= 0.8F) {
					f += g * 10.0F;
					i += 10;
				} else {
					f += g;
					i++;
				}
			} else if (!arg.method_8320(lv).method_11620().method_15799()) {
				i++;
			}
		}

		return f / (float)i;
	}
}
