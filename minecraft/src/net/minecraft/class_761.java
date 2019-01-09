package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_761 implements class_1938, AutoCloseable, class_3302 {
	private static final Logger field_4060 = LogManager.getLogger();
	private static final class_2960 field_4098 = new class_2960("textures/environment/moon_phases.png");
	private static final class_2960 field_4111 = new class_2960("textures/environment/sun.png");
	private static final class_2960 field_4108 = new class_2960("textures/environment/clouds.png");
	private static final class_2960 field_4061 = new class_2960("textures/environment/end_sky.png");
	private static final class_2960 field_4071 = new class_2960("textures/misc/forcefield.png");
	public static final class_2350[] field_4095 = class_2350.values();
	private final class_310 field_4088;
	private final class_1060 field_4057;
	private final class_898 field_4109;
	private class_638 field_4085;
	private Set<class_851> field_4075 = Sets.<class_851>newLinkedHashSet();
	private List<class_761.class_762> field_4086 = Lists.<class_761.class_762>newArrayListWithCapacity(69696);
	private final Set<class_2586> field_4055 = Sets.<class_2586>newHashSet();
	private class_769 field_4112;
	private int field_4099 = -1;
	private int field_4117 = -1;
	private int field_4067 = -1;
	private final class_293 field_4100;
	private class_291 field_4113;
	private class_291 field_4087;
	private class_291 field_4102;
	private final int field_4079 = 28;
	private boolean field_4107 = true;
	private int field_4114 = -1;
	private class_291 field_4094;
	private int field_4073;
	private final Map<Integer, class_3191> field_4058 = Maps.<Integer, class_3191>newHashMap();
	private final Map<class_2338, class_1113> field_4119 = Maps.<class_2338, class_1113>newHashMap();
	private final class_1058[] field_4068 = new class_1058[10];
	private class_276 field_4101;
	private class_279 field_4059;
	private double field_4104 = Double.MIN_VALUE;
	private double field_4120 = Double.MIN_VALUE;
	private double field_4070 = Double.MIN_VALUE;
	private int field_4084 = Integer.MIN_VALUE;
	private int field_4105 = Integer.MIN_VALUE;
	private int field_4121 = Integer.MIN_VALUE;
	private double field_4069 = Double.MIN_VALUE;
	private double field_4081 = Double.MIN_VALUE;
	private double field_4096 = Double.MIN_VALUE;
	private double field_4115 = Double.MIN_VALUE;
	private double field_4064 = Double.MIN_VALUE;
	private int field_4082 = Integer.MIN_VALUE;
	private int field_4097 = Integer.MIN_VALUE;
	private int field_4116 = Integer.MIN_VALUE;
	private class_243 field_4072 = class_243.field_1353;
	private int field_4080 = -1;
	private class_846 field_4106;
	private class_752 field_4092;
	private int field_4062 = -1;
	private int field_4076 = 2;
	private int field_4074;
	private int field_4089;
	private int field_4110;
	private boolean field_4066;
	private class_857 field_4056;
	private final class_1162[] field_4065 = new class_1162[8];
	private final class_1161 field_4091 = new class_1161();
	private boolean field_4063;
	private class_850 field_4078;
	private double field_4083;
	private double field_4103;
	private double field_4118;
	private boolean field_4077 = true;
	private boolean field_4090;

	public class_761(class_310 arg) {
		this.field_4088 = arg;
		this.field_4109 = arg.method_1561();
		this.field_4057 = arg.method_1531();
		this.field_4057.method_4618(field_4071);
		GlStateManager.texParameter(3553, 10242, 10497);
		GlStateManager.texParameter(3553, 10243, 10497);
		GlStateManager.bindTexture(0);
		this.method_3249();
		this.field_4063 = GLX.useVbo();
		if (this.field_4063) {
			this.field_4092 = new class_292();
			this.field_4078 = class_851::new;
		} else {
			this.field_4092 = new class_767();
			this.field_4078 = class_848::new;
		}

		this.field_4100 = new class_293();
		this.field_4100.method_1361(new class_296(0, class_296.class_297.field_1623, class_296.class_298.field_1633, 3));
		this.method_3293();
		this.method_3277();
		this.method_3265();
	}

	public void close() {
		if (this.field_4059 != null) {
			this.field_4059.close();
		}
	}

	@Override
	public void method_14491(class_3300 arg) {
		this.method_3249();
	}

	private void method_3249() {
		class_1059 lv = this.field_4088.method_1549();
		this.field_4068[0] = lv.method_4608(class_1088.field_5377);
		this.field_4068[1] = lv.method_4608(class_1088.field_5385);
		this.field_4068[2] = lv.method_4608(class_1088.field_5375);
		this.field_4068[3] = lv.method_4608(class_1088.field_5403);
		this.field_4068[4] = lv.method_4608(class_1088.field_5393);
		this.field_4068[5] = lv.method_4608(class_1088.field_5386);
		this.field_4068[6] = lv.method_4608(class_1088.field_5369);
		this.field_4068[7] = lv.method_4608(class_1088.field_5401);
		this.field_4068[8] = lv.method_4608(class_1088.field_5392);
		this.field_4068[9] = lv.method_4608(class_1088.field_5382);
	}

	public void method_3296() {
		if (GLX.usePostProcess) {
			if (class_285.method_1308() == null) {
				class_285.method_1305();
			}

			class_2960 lv = new class_2960("shaders/post/entity_outline.json");

			try {
				this.field_4059 = new class_279(this.field_4088.method_1531(), this.field_4088.method_1478(), this.field_4088.method_1522(), lv);
				this.field_4059.method_1259(this.field_4088.field_1704.method_4489(), this.field_4088.field_1704.method_4506());
				this.field_4101 = this.field_4059.method_1264("final");
			} catch (IOException var3) {
				field_4060.warn("Failed to load shader: {}", lv, var3);
				this.field_4059 = null;
				this.field_4101 = null;
			} catch (JsonSyntaxException var4) {
				field_4060.warn("Failed to load shader: {}", lv, var4);
				this.field_4059 = null;
				this.field_4101 = null;
			}
		} else {
			this.field_4059 = null;
			this.field_4101 = null;
		}
	}

	public void method_3254() {
		if (this.method_3270()) {
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5146, GlStateManager.class_1027.field_5078
			);
			this.field_4101.method_1233(this.field_4088.field_1704.method_4489(), this.field_4088.field_1704.method_4506(), false);
			GlStateManager.disableBlend();
		}
	}

	protected boolean method_3270() {
		return this.field_4101 != null && this.field_4059 != null && this.field_4088.field_1724 != null;
	}

	private void method_3265() {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		if (this.field_4102 != null) {
			this.field_4102.method_1355();
		}

		if (this.field_4067 >= 0) {
			class_311.method_1595(this.field_4067);
			this.field_4067 = -1;
		}

		if (this.field_4063) {
			this.field_4102 = new class_291(this.field_4100);
			this.method_3283(lv2, -16.0F, true);
			lv2.method_1326();
			lv2.method_1343();
			this.field_4102.method_1352(lv2.method_1342());
		} else {
			this.field_4067 = class_311.method_1593(1);
			GlStateManager.newList(this.field_4067, 4864);
			this.method_3283(lv2, -16.0F, true);
			lv.method_1350();
			GlStateManager.endList();
		}
	}

	private void method_3277() {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		if (this.field_4087 != null) {
			this.field_4087.method_1355();
		}

		if (this.field_4117 >= 0) {
			class_311.method_1595(this.field_4117);
			this.field_4117 = -1;
		}

		if (this.field_4063) {
			this.field_4087 = new class_291(this.field_4100);
			this.method_3283(lv2, 16.0F, false);
			lv2.method_1326();
			lv2.method_1343();
			this.field_4087.method_1352(lv2.method_1342());
		} else {
			this.field_4117 = class_311.method_1593(1);
			GlStateManager.newList(this.field_4117, 4864);
			this.method_3283(lv2, 16.0F, false);
			lv.method_1350();
			GlStateManager.endList();
		}
	}

	private void method_3283(class_287 arg, float f, boolean bl) {
		int i = 64;
		int j = 6;
		arg.method_1328(7, class_290.field_1592);

		for (int k = -384; k <= 384; k += 64) {
			for (int l = -384; l <= 384; l += 64) {
				float g = (float)k;
				float h = (float)(k + 64);
				if (bl) {
					h = (float)k;
					g = (float)(k + 64);
				}

				arg.method_1315((double)g, (double)f, (double)l).method_1344();
				arg.method_1315((double)h, (double)f, (double)l).method_1344();
				arg.method_1315((double)h, (double)f, (double)(l + 64)).method_1344();
				arg.method_1315((double)g, (double)f, (double)(l + 64)).method_1344();
			}
		}
	}

	private void method_3293() {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		if (this.field_4113 != null) {
			this.field_4113.method_1355();
		}

		if (this.field_4099 >= 0) {
			class_311.method_1595(this.field_4099);
			this.field_4099 = -1;
		}

		if (this.field_4063) {
			this.field_4113 = new class_291(this.field_4100);
			this.method_3255(lv2);
			lv2.method_1326();
			lv2.method_1343();
			this.field_4113.method_1352(lv2.method_1342());
		} else {
			this.field_4099 = class_311.method_1593(1);
			GlStateManager.pushMatrix();
			GlStateManager.newList(this.field_4099, 4864);
			this.method_3255(lv2);
			lv.method_1350();
			GlStateManager.endList();
			GlStateManager.popMatrix();
		}
	}

	private void method_3255(class_287 arg) {
		Random random = new Random(10842L);
		arg.method_1328(7, class_290.field_1592);

		for (int i = 0; i < 1500; i++) {
			double d = (double)(random.nextFloat() * 2.0F - 1.0F);
			double e = (double)(random.nextFloat() * 2.0F - 1.0F);
			double f = (double)(random.nextFloat() * 2.0F - 1.0F);
			double g = (double)(0.15F + random.nextFloat() * 0.1F);
			double h = d * d + e * e + f * f;
			if (h < 1.0 && h > 0.01) {
				h = 1.0 / Math.sqrt(h);
				d *= h;
				e *= h;
				f *= h;
				double j = d * 100.0;
				double k = e * 100.0;
				double l = f * 100.0;
				double m = Math.atan2(d, f);
				double n = Math.sin(m);
				double o = Math.cos(m);
				double p = Math.atan2(Math.sqrt(d * d + f * f), e);
				double q = Math.sin(p);
				double r = Math.cos(p);
				double s = random.nextDouble() * Math.PI * 2.0;
				double t = Math.sin(s);
				double u = Math.cos(s);

				for (int v = 0; v < 4; v++) {
					double w = 0.0;
					double x = (double)((v & 2) - 1) * g;
					double y = (double)((v + 1 & 2) - 1) * g;
					double z = 0.0;
					double aa = x * u - y * t;
					double ab = y * u + x * t;
					double ad = aa * q + 0.0 * r;
					double ae = 0.0 * q - aa * r;
					double af = ae * n - ab * o;
					double ah = ab * n + ae * o;
					arg.method_1315(j + af, k + ad, l + ah).method_1344();
				}
			}
		}
	}

	public void method_3244(@Nullable class_638 arg) {
		if (this.field_4085 != null) {
			this.field_4085.method_8415(this);
		}

		this.field_4104 = Double.MIN_VALUE;
		this.field_4120 = Double.MIN_VALUE;
		this.field_4070 = Double.MIN_VALUE;
		this.field_4084 = Integer.MIN_VALUE;
		this.field_4105 = Integer.MIN_VALUE;
		this.field_4121 = Integer.MIN_VALUE;
		this.field_4109.method_3944(arg);
		this.field_4085 = arg;
		if (arg != null) {
			arg.method_8521(this);
			this.method_3279();
		} else {
			this.field_4075.clear();
			this.field_4086.clear();
			if (this.field_4112 != null) {
				this.field_4112.method_3327();
				this.field_4112 = null;
			}

			if (this.field_4106 != null) {
				this.field_4106.method_3619();
			}

			this.field_4106 = null;
		}
	}

	public void method_3279() {
		if (this.field_4085 != null) {
			if (this.field_4106 == null) {
				this.field_4106 = new class_846();
			}

			this.field_4077 = true;
			this.field_4107 = true;
			class_2397.method_10301(this.field_4088.field_1690.field_1833);
			this.field_4062 = this.field_4088.field_1690.field_1870;
			boolean bl = this.field_4063;
			this.field_4063 = GLX.useVbo();
			if (bl && !this.field_4063) {
				this.field_4092 = new class_767();
				this.field_4078 = class_848::new;
			} else if (!bl && this.field_4063) {
				this.field_4092 = new class_292();
				this.field_4078 = class_851::new;
			}

			if (bl != this.field_4063) {
				this.method_3293();
				this.method_3277();
				this.method_3265();
			}

			if (this.field_4112 != null) {
				this.field_4112.method_3327();
			}

			this.method_3280();
			synchronized (this.field_4055) {
				this.field_4055.clear();
			}

			this.field_4112 = new class_769(this.field_4085, this.field_4088.field_1690.field_1870, this, this.field_4078);
			if (this.field_4085 != null) {
				class_1297 lv = this.field_4088.method_1560();
				if (lv != null) {
					this.field_4112.method_3330(lv.field_5987, lv.field_6035);
				}
			}

			this.field_4076 = 2;
		}
	}

	protected void method_3280() {
		this.field_4075.clear();
		this.field_4106.method_3632();
	}

	public void method_3242(int i, int j) {
		this.method_3292();
		if (GLX.usePostProcess) {
			if (this.field_4059 != null) {
				this.field_4059.method_1259(i, j);
			}
		}
	}

	public void method_3271(class_1297 arg, class_856 arg2, float f) {
		if (this.field_4076 > 0) {
			this.field_4076--;
		} else {
			double d = class_3532.method_16436((double)f, arg.field_6014, arg.field_5987);
			double e = class_3532.method_16436((double)f, arg.field_6036, arg.field_6010);
			double g = class_3532.method_16436((double)f, arg.field_5969, arg.field_6035);
			this.field_4085.method_16107().method_15396("prepare");
			class_824.field_4346
				.method_3549(this.field_4085, this.field_4088.method_1531(), this.field_4088.field_1772, this.field_4088.method_1560(), this.field_4088.field_1765, f);
			this.field_4109
				.method_3941(this.field_4085, this.field_4088.field_1772, this.field_4088.method_1560(), this.field_4088.field_1692, this.field_4088.field_1690, f);
			this.field_4074 = 0;
			this.field_4089 = 0;
			this.field_4110 = 0;
			class_1297 lv = this.field_4088.method_1560();
			double h = class_3532.method_16436((double)f, lv.field_6038, lv.field_5987);
			double i = class_3532.method_16436((double)f, lv.field_5971, lv.field_6010);
			double j = class_3532.method_16436((double)f, lv.field_5989, lv.field_6035);
			class_824.field_4343 = h;
			class_824.field_4341 = i;
			class_824.field_4340 = j;
			this.field_4109.method_3952(h, i, j);
			this.field_4088.field_1773.method_3180();
			this.field_4085.method_16107().method_15405("global");
			this.field_4074 = this.field_4085.method_8446();

			for (int k = 0; k < this.field_4085.field_9237.size(); k++) {
				class_1297 lv2 = (class_1297)this.field_4085.field_9237.get(k);
				this.field_4089++;
				if (lv2.method_5727(d, e, g)) {
					this.field_4109.method_3946(lv2, f, false);
				}
			}

			this.field_4085.method_16107().method_15405("entities");
			List<class_1297> list = Lists.<class_1297>newArrayList();
			List<class_1297> list2 = Lists.<class_1297>newArrayList();

			try (class_2338.class_2340 lv3 = class_2338.class_2340.method_10109()) {
				for (class_761.class_762 lv4 : this.field_4086) {
					class_2818 lv5 = this.field_4085.method_8500(lv4.field_4124.method_3670());
					class_3509<class_1297> lv6 = lv5.method_12215()[lv4.field_4124.method_3670().method_10264() / 16];
					if (!lv6.isEmpty()) {
						for (class_1297 lv7 : lv6) {
							boolean bl = this.field_4109.method_3950(lv7, arg2, d, e, g) || lv7.method_5821(this.field_4088.field_1724);
							if (bl) {
								boolean bl2 = this.field_4088.method_1560() instanceof class_1309 && ((class_1309)this.field_4088.method_1560()).method_6113();
								if ((lv7 != this.field_4088.method_1560() || this.field_4088.field_1690.field_1850 != 0 || bl2)
									&& (!(lv7.field_6010 >= 0.0) || !(lv7.field_6010 < 256.0) || this.field_4085.method_8591(lv3.method_10110(lv7)))) {
									this.field_4089++;
									this.field_4109.method_3946(lv7, f, false);
									if (this.method_3284(lv7, lv, arg2)) {
										list.add(lv7);
									}

									if (this.field_4109.method_3942(lv7)) {
										list2.add(lv7);
									}
								}
							}
						}
					}
				}
			}

			if (!list2.isEmpty()) {
				for (class_1297 lv8 : list2) {
					this.field_4109.method_3947(lv8, f);
				}
			}

			if (this.method_3270() && (!list.isEmpty() || this.field_4090)) {
				this.field_4085.method_16107().method_15405("entityOutlines");
				this.field_4101.method_1230(class_310.field_1703);
				this.field_4090 = !list.isEmpty();
				if (!list.isEmpty()) {
					GlStateManager.depthFunc(519);
					GlStateManager.disableFog();
					this.field_4101.method_1235(false);
					class_308.method_1450();
					this.field_4109.method_3943(true);

					for (int l = 0; l < list.size(); l++) {
						this.field_4109.method_3946((class_1297)list.get(l), f, false);
					}

					this.field_4109.method_3943(false);
					class_308.method_1452();
					GlStateManager.depthMask(false);
					this.field_4059.method_1258(f);
					GlStateManager.enableLighting();
					GlStateManager.depthMask(true);
					GlStateManager.enableFog();
					GlStateManager.enableBlend();
					GlStateManager.enableColorMaterial();
					GlStateManager.depthFunc(515);
					GlStateManager.enableDepthTest();
					GlStateManager.enableAlphaTest();
				}

				this.field_4088.method_1522().method_1235(false);
			}

			this.field_4085.method_16107().method_15405("blockentities");
			class_308.method_1452();

			for (class_761.class_762 lv9 : this.field_4086) {
				List<class_2586> list3 = lv9.field_4124.method_3677().method_3642();
				if (!list3.isEmpty()) {
					for (class_2586 lv10 : list3) {
						class_824.field_4346.method_3555(lv10, f, -1);
					}
				}
			}

			synchronized (this.field_4055) {
				for (class_2586 lv11 : this.field_4055) {
					class_824.field_4346.method_3555(lv11, f, -1);
				}
			}

			this.method_3263();

			for (class_3191 lv12 : this.field_4058.values()) {
				class_2338 lv13 = lv12.method_13991();
				class_2680 lv14 = this.field_4085.method_8320(lv13);
				if (lv14.method_11614().method_9570()) {
					class_2586 lv10 = this.field_4085.method_8321(lv13);
					if (lv10 instanceof class_2595 && lv14.method_11654(class_2281.field_10770) == class_2745.field_12574) {
						lv13 = lv13.method_10093(((class_2350)lv14.method_11654(class_2281.field_10768)).method_10170());
						lv10 = this.field_4085.method_8321(lv13);
					}

					if (lv10 != null && lv14.method_11608()) {
						class_824.field_4346.method_3555(lv10, f, lv12.method_13988());
					}
				}
			}

			this.method_3274();
			this.field_4088.field_1773.method_3187();
			this.field_4088.method_16011().method_15407();
		}
	}

	private boolean method_3284(class_1297 arg, class_1297 arg2, class_856 arg3) {
		boolean bl = arg2 instanceof class_1309 && ((class_1309)arg2).method_6113();
		if (arg == arg2 && this.field_4088.field_1690.field_1850 == 0 && !bl) {
			return false;
		} else if (arg.method_5851()) {
			return true;
		} else {
			return this.field_4088.field_1724.method_7325() && this.field_4088.field_1690.field_1906.method_1434() && arg instanceof class_1657
				? arg.field_5985 || arg3.method_3699(arg.method_5829()) || arg.method_5821(this.field_4088.field_1724)
				: false;
		}
	}

	public String method_3289() {
		int i = this.field_4112.field_4150.length;
		int j = this.method_3246();
		return String.format(
			"C: %d/%d %sD: %d, %s", j, i, this.field_4088.field_1730 ? "(s) " : "", this.field_4062, this.field_4106 == null ? "null" : this.field_4106.method_3622()
		);
	}

	protected int method_3246() {
		int i = 0;

		for (class_761.class_762 lv : this.field_4086) {
			class_849 lv2 = lv.field_4124.field_4459;
			if (lv2 != class_849.field_4451 && !lv2.method_3645()) {
				i++;
			}
		}

		return i;
	}

	public String method_3272() {
		return "E: " + this.field_4089 + "/" + this.field_4074 + ", B: " + this.field_4110;
	}

	public void method_3273(class_1297 arg, float f, class_856 arg2, int i, boolean bl) {
		if (this.field_4088.field_1690.field_1870 != this.field_4062) {
			this.method_3279();
		}

		this.field_4085.method_16107().method_15396("camera");
		double d = arg.field_5987 - this.field_4104;
		double e = arg.field_6010 - this.field_4120;
		double g = arg.field_6035 - this.field_4070;
		if (this.field_4084 != arg.field_6024 || this.field_4105 != arg.field_5959 || this.field_4121 != arg.field_5980 || d * d + e * e + g * g > 16.0) {
			this.field_4104 = arg.field_5987;
			this.field_4120 = arg.field_6010;
			this.field_4070 = arg.field_6035;
			this.field_4084 = arg.field_6024;
			this.field_4105 = arg.field_5959;
			this.field_4121 = arg.field_5980;
			this.field_4112.method_3330(arg.field_5987, arg.field_6035);
		}

		this.field_4085.method_16107().method_15405("renderlistcamera");
		double h = class_3532.method_16436((double)f, arg.field_6038, arg.field_5987);
		double j = class_3532.method_16436((double)f, arg.field_5971, arg.field_6010);
		double k = class_3532.method_16436((double)f, arg.field_5989, arg.field_6035);
		this.field_4092.method_3158(h, j, k);
		this.field_4085.method_16107().method_15405("cull");
		if (this.field_4056 != null) {
			class_858 lv = new class_858(this.field_4056);
			lv.method_3700(this.field_4091.field_5661, this.field_4091.field_5660, this.field_4091.field_5659);
			arg2 = lv;
		}

		this.field_4088.method_16011().method_15405("culling");
		class_2338 lv2 = new class_2338(h, j + (double)arg.method_5751(), k);
		class_851 lv3 = this.field_4112.method_3323(lv2);
		class_2338 lv4 = new class_2338(class_3532.method_15357(h / 16.0) * 16, class_3532.method_15357(j / 16.0) * 16, class_3532.method_15357(k / 16.0) * 16);
		float l = arg.method_5695(f);
		float m = arg.method_5705(f);
		this.field_4077 = this.field_4077
			|| !this.field_4075.isEmpty()
			|| arg.field_5987 != this.field_4069
			|| arg.field_6010 != this.field_4081
			|| arg.field_6035 != this.field_4096
			|| (double)l != this.field_4115
			|| (double)m != this.field_4064;
		this.field_4069 = arg.field_5987;
		this.field_4081 = arg.field_6010;
		this.field_4096 = arg.field_6035;
		this.field_4115 = (double)l;
		this.field_4064 = (double)m;
		boolean bl2 = this.field_4056 != null;
		this.field_4088.method_16011().method_15405("update");
		if (!bl2 && this.field_4077) {
			this.field_4077 = false;
			this.field_4086 = Lists.<class_761.class_762>newArrayList();
			Queue<class_761.class_762> queue = Queues.<class_761.class_762>newArrayDeque();
			class_1297.method_5840(class_3532.method_15350((double)this.field_4088.field_1690.field_1870 / 8.0, 1.0, 2.5));
			boolean bl3 = this.field_4088.field_1730;
			if (lv3 != null) {
				boolean bl4 = false;
				class_761.class_762 lv6 = new class_761.class_762(lv3, null, 0);
				Set<class_2350> set = this.method_3285(lv2);
				if (set.size() == 1) {
					class_1160 lv7 = this.method_3286(arg, (double)f);
					class_2350 lv8 = class_2350.method_10147(lv7.method_4943(), lv7.method_4945(), lv7.method_4947()).method_10153();
					set.remove(lv8);
				}

				if (set.isEmpty()) {
					bl4 = true;
				}

				if (bl4 && !bl) {
					this.field_4086.add(lv6);
				} else {
					if (bl && this.field_4085.method_8320(lv2).method_11598(this.field_4085, lv2)) {
						bl3 = false;
					}

					lv3.method_3671(i);
					queue.add(lv6);
				}
			} else {
				int n = lv2.method_10264() > 0 ? 248 : 8;

				for (int o = -this.field_4062; o <= this.field_4062; o++) {
					for (int p = -this.field_4062; p <= this.field_4062; p++) {
						class_851 lv5 = this.field_4112.method_3323(new class_2338((o << 4) + 8, n, (p << 4) + 8));
						if (lv5 != null && arg2.method_3699(lv5.field_4458)) {
							lv5.method_3671(i);
							queue.add(new class_761.class_762(lv5, null, 0));
						}
					}
				}
			}

			this.field_4088.method_16011().method_15396("iteration");

			while (!queue.isEmpty()) {
				class_761.class_762 lv9 = (class_761.class_762)queue.poll();
				class_851 lv10 = lv9.field_4124;
				class_2350 lv11 = lv9.field_4125;
				this.field_4086.add(lv9);

				for (class_2350 lv12 : field_4095) {
					class_851 lv13 = this.method_3241(lv4, lv10, lv12);
					if ((!bl3 || !lv9.method_3298(lv12.method_10153()))
						&& (!bl3 || lv11 == null || lv10.method_3677().method_3650(lv11.method_10153(), lv12))
						&& lv13 != null
						&& lv13.method_3673()
						&& lv13.method_3671(i)
						&& arg2.method_3699(lv13.field_4458)) {
						class_761.class_762 lv14 = new class_761.class_762(lv13, lv12, lv9.field_4122 + 1);
						lv14.method_3299(lv9.field_4126, lv12);
						queue.add(lv14);
					}
				}
			}

			this.field_4088.method_16011().method_15407();
		}

		this.field_4088.method_16011().method_15405("captureFrustum");
		if (this.field_4066) {
			this.method_3275(h, j, k);
			this.field_4066 = false;
		}

		this.field_4088.method_16011().method_15405("rebuildNear");
		Set<class_851> set2 = this.field_4075;
		this.field_4075 = Sets.<class_851>newLinkedHashSet();

		for (class_761.class_762 lv9 : this.field_4086) {
			class_851 lv10 = lv9.field_4124;
			if (lv10.method_3672() || set2.contains(lv10)) {
				this.field_4077 = true;
				class_2338 lv15 = lv10.method_3670().method_10069(8, 8, 8);
				boolean bl5 = lv15.method_10262(lv2) < 768.0;
				if (!lv10.method_3661() && !bl5) {
					this.field_4075.add(lv10);
				} else {
					this.field_4088.method_16011().method_15396("build near");
					this.field_4106.method_3627(lv10);
					lv10.method_3662();
					this.field_4088.method_16011().method_15407();
				}
			}
		}

		this.field_4075.addAll(set2);
		this.field_4088.method_16011().method_15407();
	}

	private Set<class_2350> method_3285(class_2338 arg) {
		class_852 lv = new class_852();
		class_2338 lv2 = new class_2338(arg.method_10263() >> 4 << 4, arg.method_10264() >> 4 << 4, arg.method_10260() >> 4 << 4);
		class_2818 lv3 = this.field_4085.method_8500(lv2);

		for (class_2338.class_2339 lv4 : class_2338.method_10082(lv2, lv2.method_10069(15, 15, 15))) {
			if (lv3.method_8320(lv4).method_11598(this.field_4085, lv4)) {
				lv.method_3682(lv4);
			}
		}

		return lv.method_3686(arg);
	}

	@Nullable
	private class_851 method_3241(class_2338 arg, class_851 arg2, class_2350 arg3) {
		class_2338 lv = arg2.method_3676(arg3);
		if (class_3532.method_15382(arg.method_10263() - lv.method_10263()) > this.field_4062 * 16) {
			return null;
		} else if (lv.method_10264() < 0 || lv.method_10264() >= 256) {
			return null;
		} else {
			return class_3532.method_15382(arg.method_10260() - lv.method_10260()) > this.field_4062 * 16 ? null : this.field_4112.method_3323(lv);
		}
	}

	private void method_3275(double d, double e, double f) {
	}

	protected class_1160 method_3286(class_1297 arg, double d) {
		float f = (float)class_3532.method_16436(d, (double)arg.field_6004, (double)arg.field_5965);
		float g = (float)class_3532.method_16436(d, (double)arg.field_5982, (double)arg.field_6031);
		if (class_310.method_1551().field_1690.field_1850 == 2) {
			f += 180.0F;
		}

		float h = class_3532.method_15362(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = class_3532.method_15374(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -class_3532.method_15362(-f * (float) (Math.PI / 180.0));
		float k = class_3532.method_15374(-f * (float) (Math.PI / 180.0));
		return new class_1160(i * j, k, h * j);
	}

	public int method_3251(class_1921 arg, double d, class_1297 arg2) {
		class_308.method_1450();
		if (arg == class_1921.field_9179) {
			this.field_4088.method_16011().method_15396("translucent_sort");
			double e = arg2.field_5987 - this.field_4083;
			double f = arg2.field_6010 - this.field_4103;
			double g = arg2.field_6035 - this.field_4118;
			if (e * e + f * f + g * g > 1.0) {
				this.field_4083 = arg2.field_5987;
				this.field_4103 = arg2.field_6010;
				this.field_4118 = arg2.field_6035;
				int i = 0;

				for (class_761.class_762 lv : this.field_4086) {
					if (lv.field_4124.field_4459.method_3649(arg) && i++ < 15) {
						this.field_4106.method_3620(lv.field_4124);
					}
				}
			}

			this.field_4088.method_16011().method_15407();
		}

		this.field_4088.method_16011().method_15396("filterempty");
		int j = 0;
		boolean bl = arg == class_1921.field_9179;
		int k = bl ? this.field_4086.size() - 1 : 0;
		int l = bl ? -1 : this.field_4086.size();
		int m = bl ? -1 : 1;

		for (int n = k; n != l; n += m) {
			class_851 lv2 = ((class_761.class_762)this.field_4086.get(n)).field_4124;
			if (!lv2.method_3677().method_3641(arg)) {
				j++;
				this.field_4092.method_3159(lv2, arg);
			}
		}

		this.field_4088.method_16011().method_15403(() -> "render_" + arg);
		this.method_3287(arg);
		this.field_4088.method_16011().method_15407();
		return j;
	}

	private void method_3287(class_1921 arg) {
		this.field_4088.field_1773.method_3180();
		if (GLX.useVbo()) {
			GlStateManager.enableClientState(32884);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
			GlStateManager.enableClientState(32888);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
			GlStateManager.enableClientState(32888);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
			GlStateManager.enableClientState(32886);
		}

		this.field_4092.method_3160(arg);
		if (GLX.useVbo()) {
			for (class_296 lv : class_290.field_1582.method_1357()) {
				class_296.class_298 lv2 = lv.method_1382();
				int i = lv.method_1385();
				switch (lv2) {
					case field_1633:
						GlStateManager.disableClientState(32884);
						break;
					case field_1636:
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + i);
						GlStateManager.disableClientState(32888);
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
						break;
					case field_1632:
						GlStateManager.disableClientState(32886);
						GlStateManager.clearCurrentColor();
				}
			}
		}

		this.field_4088.field_1773.method_3187();
	}

	private void method_3290(Iterator<class_3191> iterator) {
		while (iterator.hasNext()) {
			class_3191 lv = (class_3191)iterator.next();
			int i = lv.method_13990();
			if (this.field_4073 - i > 400) {
				iterator.remove();
			}
		}
	}

	public void method_3252() {
		this.field_4073++;
		if (this.field_4073 % 20 == 0) {
			this.method_3290(this.field_4058.values().iterator());
		}
	}

	private void method_3250() {
		GlStateManager.disableFog();
		GlStateManager.disableAlphaTest();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		class_308.method_1450();
		GlStateManager.depthMask(false);
		this.field_4057.method_4618(field_4061);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();

		for (int i = 0; i < 6; i++) {
			GlStateManager.pushMatrix();
			if (i == 1) {
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			}

			if (i == 2) {
				GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			}

			if (i == 3) {
				GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
			}

			if (i == 4) {
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
			}

			if (i == 5) {
				GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			}

			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315(-100.0, -100.0, -100.0).method_1312(0.0, 0.0).method_1323(40, 40, 40, 255).method_1344();
			lv2.method_1315(-100.0, -100.0, 100.0).method_1312(0.0, 16.0).method_1323(40, 40, 40, 255).method_1344();
			lv2.method_1315(100.0, -100.0, 100.0).method_1312(16.0, 16.0).method_1323(40, 40, 40, 255).method_1344();
			lv2.method_1315(100.0, -100.0, -100.0).method_1312(16.0, 0.0).method_1323(40, 40, 40, 255).method_1344();
			lv.method_1350();
			GlStateManager.popMatrix();
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.enableAlphaTest();
	}

	public void method_3257(float f) {
		if (this.field_4088.field_1687.field_9247.method_12460() == class_2874.field_13078) {
			this.method_3250();
		} else if (this.field_4088.field_1687.field_9247.method_12462()) {
			GlStateManager.disableTexture();
			class_243 lv = this.field_4085.method_8548(this.field_4088.method_1560(), f);
			float g = (float)lv.field_1352;
			float h = (float)lv.field_1351;
			float i = (float)lv.field_1350;
			GlStateManager.color3f(g, h, i);
			class_289 lv2 = class_289.method_1348();
			class_287 lv3 = lv2.method_1349();
			GlStateManager.depthMask(false);
			GlStateManager.enableFog();
			GlStateManager.color3f(g, h, i);
			if (this.field_4063) {
				this.field_4087.method_1353();
				GlStateManager.enableClientState(32884);
				GlStateManager.vertexPointer(3, 5126, 12, 0);
				this.field_4087.method_1351(7);
				class_291.method_1354();
				GlStateManager.disableClientState(32884);
			} else {
				GlStateManager.callList(this.field_4117);
			}

			GlStateManager.disableFog();
			GlStateManager.disableAlphaTest();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
			);
			class_308.method_1450();
			float[] fs = this.field_4085.field_9247.method_12446(this.field_4085.method_8400(f), f);
			if (fs != null) {
				GlStateManager.disableTexture();
				GlStateManager.shadeModel(7425);
				GlStateManager.pushMatrix();
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(class_3532.method_15374(this.field_4085.method_8442(f)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
				float j = fs[0];
				float k = fs[1];
				float l = fs[2];
				lv3.method_1328(6, class_290.field_1576);
				lv3.method_1315(0.0, 100.0, 0.0).method_1336(j, k, l, fs[3]).method_1344();
				int m = 16;

				for (int n = 0; n <= 16; n++) {
					float o = (float)n * (float) (Math.PI * 2) / 16.0F;
					float p = class_3532.method_15374(o);
					float q = class_3532.method_15362(o);
					lv3.method_1315((double)(p * 120.0F), (double)(q * 120.0F), (double)(-q * 40.0F * fs[3])).method_1336(fs[0], fs[1], fs[2], 0.0F).method_1344();
				}

				lv2.method_1350();
				GlStateManager.popMatrix();
				GlStateManager.shadeModel(7424);
			}

			GlStateManager.enableTexture();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5078, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
			);
			GlStateManager.pushMatrix();
			float j = 1.0F - this.field_4085.method_8430(f);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, j);
			GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(this.field_4085.method_8400(f) * 360.0F, 1.0F, 0.0F, 0.0F);
			float k = 30.0F;
			this.field_4057.method_4618(field_4111);
			lv3.method_1328(7, class_290.field_1585);
			lv3.method_1315((double)(-k), 100.0, (double)(-k)).method_1312(0.0, 0.0).method_1344();
			lv3.method_1315((double)k, 100.0, (double)(-k)).method_1312(1.0, 0.0).method_1344();
			lv3.method_1315((double)k, 100.0, (double)k).method_1312(1.0, 1.0).method_1344();
			lv3.method_1315((double)(-k), 100.0, (double)k).method_1312(0.0, 1.0).method_1344();
			lv2.method_1350();
			k = 20.0F;
			this.field_4057.method_4618(field_4098);
			int r = this.field_4085.method_8394();
			int m = r % 4;
			int n = r / 4 % 2;
			float o = (float)(m + 0) / 4.0F;
			float p = (float)(n + 0) / 2.0F;
			float q = (float)(m + 1) / 4.0F;
			float s = (float)(n + 1) / 2.0F;
			lv3.method_1328(7, class_290.field_1585);
			lv3.method_1315((double)(-k), -100.0, (double)k).method_1312((double)q, (double)s).method_1344();
			lv3.method_1315((double)k, -100.0, (double)k).method_1312((double)o, (double)s).method_1344();
			lv3.method_1315((double)k, -100.0, (double)(-k)).method_1312((double)o, (double)p).method_1344();
			lv3.method_1315((double)(-k), -100.0, (double)(-k)).method_1312((double)q, (double)p).method_1344();
			lv2.method_1350();
			GlStateManager.disableTexture();
			float t = this.field_4085.method_8550(f) * j;
			if (t > 0.0F) {
				GlStateManager.color4f(t, t, t, t);
				if (this.field_4063) {
					this.field_4113.method_1353();
					GlStateManager.enableClientState(32884);
					GlStateManager.vertexPointer(3, 5126, 12, 0);
					this.field_4113.method_1351(7);
					class_291.method_1354();
					GlStateManager.disableClientState(32884);
				} else {
					GlStateManager.callList(this.field_4099);
				}
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableAlphaTest();
			GlStateManager.enableFog();
			GlStateManager.popMatrix();
			GlStateManager.disableTexture();
			GlStateManager.color3f(0.0F, 0.0F, 0.0F);
			double d = this.field_4088.field_1724.method_5836(f).field_1351 - this.field_4085.method_8540();
			if (d < 0.0) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef(0.0F, 12.0F, 0.0F);
				if (this.field_4063) {
					this.field_4102.method_1353();
					GlStateManager.enableClientState(32884);
					GlStateManager.vertexPointer(3, 5126, 12, 0);
					this.field_4102.method_1351(7);
					class_291.method_1354();
					GlStateManager.disableClientState(32884);
				} else {
					GlStateManager.callList(this.field_4067);
				}

				GlStateManager.popMatrix();
			}

			if (this.field_4085.field_9247.method_12449()) {
				GlStateManager.color3f(g * 0.2F + 0.04F, h * 0.2F + 0.04F, i * 0.6F + 0.1F);
			} else {
				GlStateManager.color3f(g, h, i);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, -((float)(d - 16.0)), 0.0F);
			GlStateManager.callList(this.field_4067);
			GlStateManager.popMatrix();
			GlStateManager.enableTexture();
			GlStateManager.depthMask(true);
		}
	}

	public void method_3259(float f, double d, double e, double g) {
		if (this.field_4088.field_1687.field_9247.method_12462()) {
			float h = 12.0F;
			float i = 4.0F;
			double j = 2.0E-4;
			double k = (double)(((float)this.field_4073 + f) * 0.03F);
			double l = (d + k) / 12.0;
			double m = (double)(this.field_4085.field_9247.method_12455() - (float)e + 0.33F);
			double n = g / 12.0 + 0.33F;
			l -= (double)(class_3532.method_15357(l / 2048.0) * 2048);
			n -= (double)(class_3532.method_15357(n / 2048.0) * 2048);
			float o = (float)(l - (double)class_3532.method_15357(l));
			float p = (float)(m / 4.0 - (double)class_3532.method_15357(m / 4.0)) * 4.0F;
			float q = (float)(n - (double)class_3532.method_15357(n));
			class_243 lv = this.field_4085.method_8423(f);
			int r = (int)Math.floor(l);
			int s = (int)Math.floor(m / 4.0);
			int t = (int)Math.floor(n);
			if (r != this.field_4082
				|| s != this.field_4097
				|| t != this.field_4116
				|| this.field_4088.field_1690.method_1632() != this.field_4080
				|| this.field_4072.method_1025(lv) > 2.0E-4) {
				this.field_4082 = r;
				this.field_4097 = s;
				this.field_4116 = t;
				this.field_4072 = lv;
				this.field_4080 = this.field_4088.field_1690.method_1632();
				this.field_4107 = true;
			}

			if (this.field_4107) {
				this.field_4107 = false;
				class_289 lv2 = class_289.method_1348();
				class_287 lv3 = lv2.method_1349();
				if (this.field_4094 != null) {
					this.field_4094.method_1355();
				}

				if (this.field_4114 >= 0) {
					class_311.method_1595(this.field_4114);
					this.field_4114 = -1;
				}

				if (this.field_4063) {
					this.field_4094 = new class_291(class_290.field_1577);
					this.method_3239(lv3, l, m, n, lv);
					lv3.method_1326();
					lv3.method_1343();
					this.field_4094.method_1352(lv3.method_1342());
				} else {
					this.field_4114 = class_311.method_1593(1);
					GlStateManager.newList(this.field_4114, 4864);
					this.method_3239(lv3, l, m, n, lv);
					lv2.method_1350();
					GlStateManager.endList();
				}
			}

			GlStateManager.disableCull();
			this.field_4057.method_4618(field_4108);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
			);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(12.0F, 1.0F, 12.0F);
			GlStateManager.translatef(-o, p, -q);
			if (this.field_4063 && this.field_4094 != null) {
				this.field_4094.method_1353();
				GlStateManager.enableClientState(32884);
				GlStateManager.enableClientState(32888);
				GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
				GlStateManager.enableClientState(32886);
				GlStateManager.enableClientState(32885);
				GlStateManager.vertexPointer(3, 5126, 28, 0);
				GlStateManager.texCoordPointer(2, 5126, 28, 12);
				GlStateManager.colorPointer(4, 5121, 28, 20);
				GlStateManager.normalPointer(5120, 28, 24);
				int u = this.field_4080 == 2 ? 0 : 1;

				for (int v = u; v < 2; v++) {
					if (v == 0) {
						GlStateManager.colorMask(false, false, false, false);
					} else {
						GlStateManager.colorMask(true, true, true, true);
					}

					this.field_4094.method_1351(7);
				}

				class_291.method_1354();
				GlStateManager.disableClientState(32884);
				GlStateManager.disableClientState(32888);
				GlStateManager.disableClientState(32886);
				GlStateManager.disableClientState(32885);
			} else if (this.field_4114 >= 0) {
				int u = this.field_4080 == 2 ? 0 : 1;

				for (int v = u; v < 2; v++) {
					if (v == 0) {
						GlStateManager.colorMask(false, false, false, false);
					} else {
						GlStateManager.colorMask(true, true, true, true);
					}

					GlStateManager.callList(this.field_4114);
				}
			}

			GlStateManager.popMatrix();
			GlStateManager.clearCurrentColor();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableCull();
		}
	}

	private void method_3239(class_287 arg, double d, double e, double f, class_243 arg2) {
		float g = 4.0F;
		float h = 0.00390625F;
		int i = 8;
		int j = 4;
		float k = 9.765625E-4F;
		float l = (float)class_3532.method_15357(d) * 0.00390625F;
		float m = (float)class_3532.method_15357(f) * 0.00390625F;
		float n = (float)arg2.field_1352;
		float o = (float)arg2.field_1351;
		float p = (float)arg2.field_1350;
		float q = n * 0.9F;
		float r = o * 0.9F;
		float s = p * 0.9F;
		float t = n * 0.7F;
		float u = o * 0.7F;
		float v = p * 0.7F;
		float w = n * 0.8F;
		float x = o * 0.8F;
		float y = p * 0.8F;
		arg.method_1328(7, class_290.field_1577);
		float z = (float)Math.floor(e / 4.0) * 4.0F;
		if (this.field_4080 == 2) {
			for (int aa = -3; aa <= 4; aa++) {
				for (int ab = -3; ab <= 4; ab++) {
					float ac = (float)(aa * 8);
					float ad = (float)(ab * 8);
					if (z > -5.0F) {
						arg.method_1315((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
							.method_1312((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.method_1336(t, u, v, 0.8F)
							.method_1318(0.0F, -1.0F, 0.0F)
							.method_1344();
						arg.method_1315((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
							.method_1312((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.method_1336(t, u, v, 0.8F)
							.method_1318(0.0F, -1.0F, 0.0F)
							.method_1344();
						arg.method_1315((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
							.method_1312((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.method_1336(t, u, v, 0.8F)
							.method_1318(0.0F, -1.0F, 0.0F)
							.method_1344();
						arg.method_1315((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
							.method_1312((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.method_1336(t, u, v, 0.8F)
							.method_1318(0.0F, -1.0F, 0.0F)
							.method_1344();
					}

					if (z <= 5.0F) {
						arg.method_1315((double)(ac + 0.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 8.0F))
							.method_1312((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.method_1336(n, o, p, 0.8F)
							.method_1318(0.0F, 1.0F, 0.0F)
							.method_1344();
						arg.method_1315((double)(ac + 8.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 8.0F))
							.method_1312((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.method_1336(n, o, p, 0.8F)
							.method_1318(0.0F, 1.0F, 0.0F)
							.method_1344();
						arg.method_1315((double)(ac + 8.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 0.0F))
							.method_1312((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.method_1336(n, o, p, 0.8F)
							.method_1318(0.0F, 1.0F, 0.0F)
							.method_1344();
						arg.method_1315((double)(ac + 0.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 0.0F))
							.method_1312((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.method_1336(n, o, p, 0.8F)
							.method_1318(0.0F, 1.0F, 0.0F)
							.method_1344();
					}

					if (aa > -1) {
						for (int ae = 0; ae < 8; ae++) {
							arg.method_1315((double)(ac + (float)ae + 0.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
								.method_1312((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.method_1336(q, r, s, 0.8F)
								.method_1318(-1.0F, 0.0F, 0.0F)
								.method_1344();
							arg.method_1315((double)(ac + (float)ae + 0.0F), (double)(z + 4.0F), (double)(ad + 8.0F))
								.method_1312((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.method_1336(q, r, s, 0.8F)
								.method_1318(-1.0F, 0.0F, 0.0F)
								.method_1344();
							arg.method_1315((double)(ac + (float)ae + 0.0F), (double)(z + 4.0F), (double)(ad + 0.0F))
								.method_1312((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.method_1336(q, r, s, 0.8F)
								.method_1318(-1.0F, 0.0F, 0.0F)
								.method_1344();
							arg.method_1315((double)(ac + (float)ae + 0.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
								.method_1312((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.method_1336(q, r, s, 0.8F)
								.method_1318(-1.0F, 0.0F, 0.0F)
								.method_1344();
						}
					}

					if (aa <= 1) {
						for (int ae = 0; ae < 8; ae++) {
							arg.method_1315((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 0.0F), (double)(ad + 8.0F))
								.method_1312((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.method_1336(q, r, s, 0.8F)
								.method_1318(1.0F, 0.0F, 0.0F)
								.method_1344();
							arg.method_1315((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 4.0F), (double)(ad + 8.0F))
								.method_1312((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.method_1336(q, r, s, 0.8F)
								.method_1318(1.0F, 0.0F, 0.0F)
								.method_1344();
							arg.method_1315((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 4.0F), (double)(ad + 0.0F))
								.method_1312((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.method_1336(q, r, s, 0.8F)
								.method_1318(1.0F, 0.0F, 0.0F)
								.method_1344();
							arg.method_1315((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 0.0F), (double)(ad + 0.0F))
								.method_1312((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.method_1336(q, r, s, 0.8F)
								.method_1318(1.0F, 0.0F, 0.0F)
								.method_1344();
						}
					}

					if (ab > -1) {
						for (int ae = 0; ae < 8; ae++) {
							arg.method_1315((double)(ac + 0.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 0.0F))
								.method_1312((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.method_1336(w, x, y, 0.8F)
								.method_1318(0.0F, 0.0F, -1.0F)
								.method_1344();
							arg.method_1315((double)(ac + 8.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 0.0F))
								.method_1312((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.method_1336(w, x, y, 0.8F)
								.method_1318(0.0F, 0.0F, -1.0F)
								.method_1344();
							arg.method_1315((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 0.0F))
								.method_1312((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.method_1336(w, x, y, 0.8F)
								.method_1318(0.0F, 0.0F, -1.0F)
								.method_1344();
							arg.method_1315((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 0.0F))
								.method_1312((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.method_1336(w, x, y, 0.8F)
								.method_1318(0.0F, 0.0F, -1.0F)
								.method_1344();
						}
					}

					if (ab <= 1) {
						for (int ae = 0; ae < 8; ae++) {
							arg.method_1315((double)(ac + 0.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.method_1312((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.method_1336(w, x, y, 0.8F)
								.method_1318(0.0F, 0.0F, 1.0F)
								.method_1344();
							arg.method_1315((double)(ac + 8.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.method_1312((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.method_1336(w, x, y, 0.8F)
								.method_1318(0.0F, 0.0F, 1.0F)
								.method_1344();
							arg.method_1315((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.method_1312((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.method_1336(w, x, y, 0.8F)
								.method_1318(0.0F, 0.0F, 1.0F)
								.method_1344();
							arg.method_1315((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.method_1312((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.method_1336(w, x, y, 0.8F)
								.method_1318(0.0F, 0.0F, 1.0F)
								.method_1344();
						}
					}
				}
			}
		} else {
			int aa = 1;
			int ab = 32;

			for (int af = -32; af < 32; af += 32) {
				for (int ag = -32; ag < 32; ag += 32) {
					arg.method_1315((double)(af + 0), (double)z, (double)(ag + 32))
						.method_1312((double)((float)(af + 0) * 0.00390625F + l), (double)((float)(ag + 32) * 0.00390625F + m))
						.method_1336(n, o, p, 0.8F)
						.method_1318(0.0F, -1.0F, 0.0F)
						.method_1344();
					arg.method_1315((double)(af + 32), (double)z, (double)(ag + 32))
						.method_1312((double)((float)(af + 32) * 0.00390625F + l), (double)((float)(ag + 32) * 0.00390625F + m))
						.method_1336(n, o, p, 0.8F)
						.method_1318(0.0F, -1.0F, 0.0F)
						.method_1344();
					arg.method_1315((double)(af + 32), (double)z, (double)(ag + 0))
						.method_1312((double)((float)(af + 32) * 0.00390625F + l), (double)((float)(ag + 0) * 0.00390625F + m))
						.method_1336(n, o, p, 0.8F)
						.method_1318(0.0F, -1.0F, 0.0F)
						.method_1344();
					arg.method_1315((double)(af + 0), (double)z, (double)(ag + 0))
						.method_1312((double)((float)(af + 0) * 0.00390625F + l), (double)((float)(ag + 0) * 0.00390625F + m))
						.method_1336(n, o, p, 0.8F)
						.method_1318(0.0F, -1.0F, 0.0F)
						.method_1344();
				}
			}
		}
	}

	public void method_3269(long l) {
		this.field_4077 = this.field_4077 | this.field_4106.method_3631(l);
		if (!this.field_4075.isEmpty()) {
			Iterator<class_851> iterator = this.field_4075.iterator();

			while (iterator.hasNext()) {
				class_851 lv = (class_851)iterator.next();
				boolean bl;
				if (lv.method_3661()) {
					bl = this.field_4106.method_3627(lv);
				} else {
					bl = this.field_4106.method_3624(lv);
				}

				if (!bl) {
					break;
				}

				lv.method_3662();
				iterator.remove();
				long m = l - class_156.method_648();
				if (m < 0L) {
					break;
				}
			}
		}
	}

	public void method_3243(class_1297 arg, float f) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		class_2784 lv3 = this.field_4085.method_8621();
		double d = (double)(this.field_4088.field_1690.field_1870 * 16);
		if (!(arg.field_5987 < lv3.method_11963() - d)
			|| !(arg.field_5987 > lv3.method_11976() + d)
			|| !(arg.field_6035 < lv3.method_11977() - d)
			|| !(arg.field_6035 > lv3.method_11958() + d)) {
			double e = 1.0 - lv3.method_11979(arg) / d;
			e = Math.pow(e, 4.0);
			double g = class_3532.method_16436((double)f, arg.field_6038, arg.field_5987);
			double h = class_3532.method_16436((double)f, arg.field_5971, arg.field_6010);
			double i = class_3532.method_16436((double)f, arg.field_5989, arg.field_6035);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5078, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
			);
			this.field_4057.method_4618(field_4071);
			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			int j = lv3.method_11968().method_11999();
			float k = (float)(j >> 16 & 0xFF) / 255.0F;
			float l = (float)(j >> 8 & 0xFF) / 255.0F;
			float m = (float)(j & 0xFF) / 255.0F;
			GlStateManager.color4f(k, l, m, (float)e);
			GlStateManager.polygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolygonOffset();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableCull();
			float n = (float)(class_156.method_658() % 3000L) / 3000.0F;
			float o = 0.0F;
			float p = 0.0F;
			float q = 128.0F;
			lv2.method_1328(7, class_290.field_1585);
			lv2.method_1331(-g, -h, -i);
			double r = Math.max((double)class_3532.method_15357(i - d), lv3.method_11958());
			double s = Math.min((double)class_3532.method_15384(i + d), lv3.method_11977());
			if (g > lv3.method_11963() - d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					lv2.method_1315(lv3.method_11963(), 256.0, u).method_1312((double)(n + t), (double)(n + 0.0F)).method_1344();
					lv2.method_1315(lv3.method_11963(), 256.0, u + v).method_1312((double)(n + w + t), (double)(n + 0.0F)).method_1344();
					lv2.method_1315(lv3.method_11963(), 0.0, u + v).method_1312((double)(n + w + t), (double)(n + 128.0F)).method_1344();
					lv2.method_1315(lv3.method_11963(), 0.0, u).method_1312((double)(n + t), (double)(n + 128.0F)).method_1344();
					u++;
				}
			}

			if (g < lv3.method_11976() + d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					lv2.method_1315(lv3.method_11976(), 256.0, u).method_1312((double)(n + t), (double)(n + 0.0F)).method_1344();
					lv2.method_1315(lv3.method_11976(), 256.0, u + v).method_1312((double)(n + w + t), (double)(n + 0.0F)).method_1344();
					lv2.method_1315(lv3.method_11976(), 0.0, u + v).method_1312((double)(n + w + t), (double)(n + 128.0F)).method_1344();
					lv2.method_1315(lv3.method_11976(), 0.0, u).method_1312((double)(n + t), (double)(n + 128.0F)).method_1344();
					u++;
				}
			}

			r = Math.max((double)class_3532.method_15357(g - d), lv3.method_11976());
			s = Math.min((double)class_3532.method_15384(g + d), lv3.method_11963());
			if (i > lv3.method_11977() - d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					lv2.method_1315(u, 256.0, lv3.method_11977()).method_1312((double)(n + t), (double)(n + 0.0F)).method_1344();
					lv2.method_1315(u + v, 256.0, lv3.method_11977()).method_1312((double)(n + w + t), (double)(n + 0.0F)).method_1344();
					lv2.method_1315(u + v, 0.0, lv3.method_11977()).method_1312((double)(n + w + t), (double)(n + 128.0F)).method_1344();
					lv2.method_1315(u, 0.0, lv3.method_11977()).method_1312((double)(n + t), (double)(n + 128.0F)).method_1344();
					u++;
				}
			}

			if (i < lv3.method_11958() + d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					lv2.method_1315(u, 256.0, lv3.method_11958()).method_1312((double)(n + t), (double)(n + 0.0F)).method_1344();
					lv2.method_1315(u + v, 256.0, lv3.method_11958()).method_1312((double)(n + w + t), (double)(n + 0.0F)).method_1344();
					lv2.method_1315(u + v, 0.0, lv3.method_11958()).method_1312((double)(n + w + t), (double)(n + 128.0F)).method_1344();
					lv2.method_1315(u, 0.0, lv3.method_11958()).method_1312((double)(n + t), (double)(n + 128.0F)).method_1344();
					u++;
				}
			}

			lv.method_1350();
			lv2.method_1331(0.0, 0.0, 0.0);
			GlStateManager.enableCull();
			GlStateManager.disableAlphaTest();
			GlStateManager.polygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolygonOffset();
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
		}
	}

	private void method_3263() {
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5139, GlStateManager.class_1027.field_5081, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.enableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.polygonOffset(-1.0F, -10.0F);
		GlStateManager.enablePolygonOffset();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableAlphaTest();
		GlStateManager.pushMatrix();
	}

	private void method_3274() {
		GlStateManager.disableAlphaTest();
		GlStateManager.polygonOffset(0.0F, 0.0F);
		GlStateManager.disablePolygonOffset();
		GlStateManager.enableAlphaTest();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}

	public void method_3256(class_289 arg, class_287 arg2, class_1297 arg3, float f) {
		double d = class_3532.method_16436((double)f, arg3.field_6038, arg3.field_5987);
		double e = class_3532.method_16436((double)f, arg3.field_5971, arg3.field_6010);
		double g = class_3532.method_16436((double)f, arg3.field_5989, arg3.field_6035);
		if (!this.field_4058.isEmpty()) {
			this.field_4057.method_4618(class_1059.field_5275);
			this.method_3263();
			arg2.method_1328(7, class_290.field_1582);
			arg2.method_1331(-d, -e, -g);
			arg2.method_1327();
			Iterator<class_3191> iterator = this.field_4058.values().iterator();

			while (iterator.hasNext()) {
				class_3191 lv = (class_3191)iterator.next();
				class_2338 lv2 = lv.method_13991();
				class_2248 lv3 = this.field_4085.method_8320(lv2).method_11614();
				if (!(lv3 instanceof class_2281) && !(lv3 instanceof class_2336) && !(lv3 instanceof class_2478) && !(lv3 instanceof class_2190)) {
					double h = (double)lv2.method_10263() - d;
					double i = (double)lv2.method_10264() - e;
					double j = (double)lv2.method_10260() - g;
					if (h * h + i * i + j * j > 1024.0) {
						iterator.remove();
					} else {
						class_2680 lv4 = this.field_4085.method_8320(lv2);
						if (!lv4.method_11588()) {
							int k = lv.method_13988();
							class_1058 lv5 = this.field_4068[k];
							class_776 lv6 = this.field_4088.method_1541();
							lv6.method_3354(lv4, lv2, lv5, this.field_4085);
						}
					}
				}
			}

			arg.method_1350();
			arg2.method_1331(0.0, 0.0, 0.0);
			this.method_3274();
		}
	}

	public void method_3294(class_1657 arg, class_239 arg2, int i, float f) {
		if (i == 0 && arg2.field_1330 == class_239.class_240.field_1332) {
			class_2338 lv = arg2.method_1015();
			class_2680 lv2 = this.field_4085.method_8320(lv);
			if (!lv2.method_11588() && this.field_4085.method_8621().method_11952(lv)) {
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(
					GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
				);
				GlStateManager.lineWidth(Math.max(2.5F, (float)this.field_4088.field_1704.method_4489() / 1920.0F * 2.5F));
				GlStateManager.disableTexture();
				GlStateManager.depthMask(false);
				GlStateManager.matrixMode(5889);
				GlStateManager.pushMatrix();
				GlStateManager.scalef(1.0F, 1.0F, 0.999F);
				double d = class_3532.method_16436((double)f, arg.field_6038, arg.field_5987);
				double e = class_3532.method_16436((double)f, arg.field_5971, arg.field_6010);
				double g = class_3532.method_16436((double)f, arg.field_5989, arg.field_6035);
				method_3291(
					lv2.method_11606(this.field_4085, lv), (double)lv.method_10263() - d, (double)lv.method_10264() - e, (double)lv.method_10260() - g, 0.0F, 0.0F, 0.0F, 0.4F
				);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
				GlStateManager.depthMask(true);
				GlStateManager.enableTexture();
				GlStateManager.disableBlend();
			}
		}
	}

	public static void method_3240(class_265 arg, double d, double e, double f, float g, float h, float i, float j) {
		List<class_238> list = arg.method_1090();
		int k = class_3532.method_15384((double)list.size() / 3.0);

		for (int l = 0; l < list.size(); l++) {
			class_238 lv = (class_238)list.get(l);
			float m = ((float)l % (float)k + 1.0F) / (float)k;
			float n = (float)(l / k);
			float o = m * (float)(n == 0.0F ? 1 : 0);
			float p = m * (float)(n == 1.0F ? 1 : 0);
			float q = m * (float)(n == 2.0F ? 1 : 0);
			method_3291(class_259.method_1078(lv.method_989(0.0, 0.0, 0.0)), d, e, f, o, p, q, 1.0F);
		}
	}

	public static void method_3291(class_265 arg, double d, double e, double f, float g, float h, float i, float j) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(1, class_290.field_1576);
		arg.method_1104((k, l, m, n, o, p) -> {
			lv2.method_1315(k + d, l + e, m + f).method_1336(g, h, i, j).method_1344();
			lv2.method_1315(n + d, o + e, p + f).method_1336(g, h, i, j).method_1344();
		});
		lv.method_1350();
	}

	public static void method_3260(class_238 arg, float f, float g, float h, float i) {
		method_3262(arg.field_1323, arg.field_1322, arg.field_1321, arg.field_1320, arg.field_1325, arg.field_1324, f, g, h, i);
	}

	public static void method_3262(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(3, class_290.field_1576);
		method_3258(lv2, d, e, f, g, h, i, j, k, l, m);
		lv.method_1350();
	}

	public static void method_3258(class_287 arg, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		arg.method_1315(d, e, f).method_1336(j, k, l, 0.0F).method_1344();
		arg.method_1315(d, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, i).method_1336(j, k, l, 0.0F).method_1344();
		arg.method_1315(d, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, i).method_1336(j, k, l, 0.0F).method_1344();
		arg.method_1315(g, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, f).method_1336(j, k, l, 0.0F).method_1344();
		arg.method_1315(g, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, e, f).method_1336(j, k, l, 0.0F).method_1344();
	}

	public static void method_3261(class_238 arg, float f, float g, float h, float i) {
		method_3266(arg.field_1323, arg.field_1322, arg.field_1321, arg.field_1320, arg.field_1325, arg.field_1324, f, g, h, i);
	}

	public static void method_3266(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(5, class_290.field_1576);
		method_3253(lv2, d, e, f, g, h, i, j, k, l, m);
		lv.method_1350();
	}

	public static void method_3253(class_287 arg, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		arg.method_1315(d, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, e, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, e, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(d, h, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, f).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, i).method_1336(j, k, l, m).method_1344();
		arg.method_1315(g, h, i).method_1336(j, k, l, m).method_1344();
	}

	@Override
	public void method_8570(class_1922 arg, class_2338 arg2, class_2680 arg3, class_2680 arg4, int i) {
		this.method_16037(arg2, (i & 8) != 0);
	}

	private void method_16037(class_2338 arg, boolean bl) {
		for (int i = arg.method_10260() - 1; i <= arg.method_10260() + 1; i++) {
			for (int j = arg.method_10263() - 1; j <= arg.method_10263() + 1; j++) {
				for (int k = arg.method_10264() - 1; k <= arg.method_10264() + 1; k++) {
					this.method_3295(j >> 4, k >> 4, i >> 4, bl);
				}
			}
		}
	}

	@Override
	public void method_8571(int i, int j, int k) {
		this.method_3295(i, j, k, false);
	}

	private void method_3295(int i, int j, int k, boolean bl) {
		this.field_4112.method_16040(i, j, k, bl);
	}

	@Override
	public void method_8562(@Nullable class_3414 arg, class_2338 arg2) {
		class_1113 lv = (class_1113)this.field_4119.get(arg2);
		if (lv != null) {
			this.field_4088.method_1483().method_4870(lv);
			this.field_4119.remove(arg2);
		}

		if (arg != null) {
			class_1813 lv2 = class_1813.method_8012(arg);
			if (lv2 != null) {
				this.field_4088.field_1705.method_1732(lv2.method_8011().method_10863());
			}

			class_1113 var5 = class_1109.method_4760(arg, (float)arg2.method_10263(), (float)arg2.method_10264(), (float)arg2.method_10260());
			this.field_4119.put(arg2, var5);
			this.field_4088.method_1483().method_4873(var5);
		}

		this.method_3247(this.field_4085, arg2, arg != null);
	}

	private void method_3247(class_1937 arg, class_2338 arg2, boolean bl) {
		for (class_1309 lv : arg.method_8403(class_1309.class, new class_238(arg2).method_1014(3.0))) {
			lv.method_6006(arg2, bl);
		}
	}

	@Override
	public void method_8572(@Nullable class_1657 arg, class_3414 arg2, class_3419 arg3, double d, double e, double f, float g, float h) {
	}

	@Override
	public void method_8565(@Nullable class_1657 arg, class_3414 arg2, class_3419 arg3, class_1297 arg4, float f, float g) {
	}

	@Override
	public void method_8568(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i) {
		this.method_8563(arg, bl, false, d, e, f, g, h, i);
	}

	@Override
	public void method_8563(class_2394 arg, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
		try {
			this.method_3288(arg, bl, bl2, d, e, f, g, h, i);
		} catch (Throwable var19) {
			class_128 lv = class_128.method_560(var19, "Exception while adding particle");
			class_129 lv2 = lv.method_562("Particle being added");
			lv2.method_578("ID", class_2378.field_11141.method_10221((class_2396<? extends class_2394>)arg.method_10295()));
			lv2.method_578("Parameters", arg.method_10293());
			lv2.method_577("Position", () -> class_129.method_583(d, e, f));
			throw new class_148(lv);
		}
	}

	private <T extends class_2394> void method_3276(T arg, double d, double e, double f, double g, double h, double i) {
		this.method_8568(arg, arg.method_10295().method_10299(), d, e, f, g, h, i);
	}

	@Nullable
	private class_703 method_3282(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i) {
		return this.method_3288(arg, bl, false, d, e, f, g, h, i);
	}

	@Nullable
	private class_703 method_3288(class_2394 arg, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
		class_1297 lv = this.field_4088.method_1560();
		if (this.field_4088 != null && lv != null && this.field_4088.field_1713 != null) {
			int j = this.method_3268(bl2);
			double k = lv.field_5987 - d;
			double l = lv.field_6010 - e;
			double m = lv.field_6035 - f;
			if (bl) {
				return this.field_4088.field_1713.method_3056(arg, d, e, f, g, h, i);
			} else if (k * k + l * l + m * m > 1024.0) {
				return null;
			} else {
				return j > 1 ? null : this.field_4088.field_1713.method_3056(arg, d, e, f, g, h, i);
			}
		} else {
			return null;
		}
	}

	private int method_3268(boolean bl) {
		int i = this.field_4088.field_1690.field_1882;
		if (bl && i == 2 && this.field_4085.field_9229.nextInt(10) == 0) {
			i = 1;
		}

		if (i == 1 && this.field_4085.field_9229.nextInt(3) == 0) {
			i = 2;
		}

		return i;
	}

	@Override
	public void method_8561(class_1297 arg) {
	}

	@Override
	public void method_8566(class_1297 arg) {
	}

	public void method_3267() {
	}

	@Override
	public void method_8564(int i, class_2338 arg, int j) {
		switch (i) {
			case 1023:
			case 1028:
			case 1038:
				class_1297 lv = this.field_4088.method_1560();
				if (lv != null) {
					double d = (double)arg.method_10263() - lv.field_5987;
					double e = (double)arg.method_10264() - lv.field_6010;
					double f = (double)arg.method_10260() - lv.field_6035;
					double g = Math.sqrt(d * d + e * e + f * f);
					double h = lv.field_5987;
					double k = lv.field_6010;
					double l = lv.field_6035;
					if (g > 0.0) {
						h += d / g * 2.0;
						k += e / g * 2.0;
						l += f / g * 2.0;
					}

					if (i == 1023) {
						this.field_4085.method_8486(h, k, l, class_3417.field_14792, class_3419.field_15251, 1.0F, 1.0F, false);
					} else if (i == 1038) {
						this.field_4085.method_8486(h, k, l, class_3417.field_14981, class_3419.field_15251, 1.0F, 1.0F, false);
					} else {
						this.field_4085.method_8486(h, k, l, class_3417.field_14773, class_3419.field_15251, 5.0F, 1.0F, false);
					}
				}
		}
	}

	@Override
	public void method_8567(class_1657 arg, int i, class_2338 arg2, int j) {
		Random random = this.field_4085.field_9229;
		switch (i) {
			case 1000:
				this.field_4085.method_2947(arg2, class_3417.field_14611, class_3419.field_15245, 1.0F, 1.0F, false);
				break;
			case 1001:
				this.field_4085.method_2947(arg2, class_3417.field_14701, class_3419.field_15245, 1.0F, 1.2F, false);
				break;
			case 1002:
				this.field_4085.method_2947(arg2, class_3417.field_14711, class_3419.field_15245, 1.0F, 1.2F, false);
				break;
			case 1003:
				this.field_4085.method_2947(arg2, class_3417.field_15155, class_3419.field_15254, 1.0F, 1.2F, false);
				break;
			case 1004:
				this.field_4085.method_2947(arg2, class_3417.field_14712, class_3419.field_15254, 1.0F, 1.2F, false);
				break;
			case 1005:
				this.field_4085.method_2947(arg2, class_3417.field_14567, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1006:
				this.field_4085.method_2947(arg2, class_3417.field_14664, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1007:
				this.field_4085.method_2947(arg2, class_3417.field_14932, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1008:
				this.field_4085.method_2947(arg2, class_3417.field_14766, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1009:
				this.field_4085.method_2947(arg2, class_3417.field_15102, class_3419.field_15245, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
				break;
			case 1010:
				if (class_1792.method_7875(j) instanceof class_1813) {
					this.field_4085.method_8432(arg2, ((class_1813)class_1792.method_7875(j)).method_8009());
				} else {
					this.field_4085.method_8432(arg2, null);
				}
				break;
			case 1011:
				this.field_4085.method_2947(arg2, class_3417.field_14819, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1012:
				this.field_4085.method_2947(arg2, class_3417.field_14541, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1013:
				this.field_4085.method_2947(arg2, class_3417.field_15080, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1014:
				this.field_4085.method_2947(arg2, class_3417.field_14861, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1015:
				this.field_4085.method_2947(arg2, class_3417.field_15130, class_3419.field_15251, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1016:
				this.field_4085.method_2947(arg2, class_3417.field_15231, class_3419.field_15251, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1017:
				this.field_4085.method_2947(arg2, class_3417.field_14934, class_3419.field_15251, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1018:
				this.field_4085.method_2947(arg2, class_3417.field_14970, class_3419.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1019:
				this.field_4085.method_2947(arg2, class_3417.field_14562, class_3419.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1020:
				this.field_4085.method_2947(arg2, class_3417.field_14670, class_3419.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1021:
				this.field_4085.method_2947(arg2, class_3417.field_14742, class_3419.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1022:
				this.field_4085.method_2947(arg2, class_3417.field_15236, class_3419.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1024:
				this.field_4085.method_2947(arg2, class_3417.field_14588, class_3419.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1025:
				this.field_4085.method_2947(arg2, class_3417.field_14610, class_3419.field_15254, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1026:
				this.field_4085.method_2947(arg2, class_3417.field_14986, class_3419.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1027:
				this.field_4085.method_2947(arg2, class_3417.field_15168, class_3419.field_15254, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1029:
				this.field_4085.method_2947(arg2, class_3417.field_14665, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1030:
				this.field_4085.method_2947(arg2, class_3417.field_14559, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1031:
				this.field_4085.method_2947(arg2, class_3417.field_14833, class_3419.field_15245, 0.3F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1032:
				this.field_4088.method_1483().method_4873(class_1109.method_4758(class_3417.field_14716, random.nextFloat() * 0.4F + 0.8F));
				break;
			case 1033:
				this.field_4085.method_2947(arg2, class_3417.field_14817, class_3419.field_15245, 1.0F, 1.0F, false);
				break;
			case 1034:
				this.field_4085.method_2947(arg2, class_3417.field_14739, class_3419.field_15245, 1.0F, 1.0F, false);
				break;
			case 1035:
				this.field_4085.method_2947(arg2, class_3417.field_14978, class_3419.field_15245, 1.0F, 1.0F, false);
				break;
			case 1036:
				this.field_4085.method_2947(arg2, class_3417.field_15131, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1037:
				this.field_4085.method_2947(arg2, class_3417.field_15082, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1039:
				this.field_4085.method_2947(arg2, class_3417.field_14729, class_3419.field_15251, 0.3F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1040:
				this.field_4085.method_2947(arg2, class_3417.field_14850, class_3419.field_15254, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1041:
				this.field_4085.method_2947(arg2, class_3417.field_15128, class_3419.field_15254, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1042:
				this.field_4085.method_2947(arg2, class_3417.field_16865, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1043:
				this.field_4085.method_2947(arg2, class_3417.field_17481, class_3419.field_15245, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 2000:
				class_2350 lv = class_2350.method_10143(j);
				int k = lv.method_10148();
				int l = lv.method_10164();
				int m = lv.method_10165();
				double d = (double)arg2.method_10263() + (double)k * 0.6 + 0.5;
				double e = (double)arg2.method_10264() + (double)l * 0.6 + 0.5;
				double f = (double)arg2.method_10260() + (double)m * 0.6 + 0.5;

				for (int nx = 0; nx < 10; nx++) {
					double g = random.nextDouble() * 0.2 + 0.01;
					double h = d + (double)k * 0.01 + (random.nextDouble() - 0.5) * (double)m * 0.5;
					double o = e + (double)l * 0.01 + (random.nextDouble() - 0.5) * (double)l * 0.5;
					double p = f + (double)m * 0.01 + (random.nextDouble() - 0.5) * (double)k * 0.5;
					double q = (double)k * g + random.nextGaussian() * 0.01;
					double r = (double)l * g + random.nextGaussian() * 0.01;
					double s = (double)m * g + random.nextGaussian() * 0.01;
					this.method_3276(class_2398.field_11251, h, o, p, q, r, s);
				}
				break;
			case 2001:
				class_2680 lv4 = class_2248.method_9531(j);
				if (!lv4.method_11588()) {
					class_2498 lv5 = lv4.method_11638();
					this.field_4085.method_2947(arg2, lv5.method_10595(), class_3419.field_15245, (lv5.method_10597() + 1.0F) / 2.0F, lv5.method_10599() * 0.8F, false);
				}

				this.field_4088.field_1713.method_3046(arg2, lv4);
				break;
			case 2002:
			case 2007:
				double t = (double)arg2.method_10263();
				double u = (double)arg2.method_10264();
				double d = (double)arg2.method_10260();

				for (int v = 0; v < 8; v++) {
					this.method_3276(
						new class_2392(class_2398.field_11218, new class_1799(class_1802.field_8436)),
						t,
						u,
						d,
						random.nextGaussian() * 0.15,
						random.nextDouble() * 0.2,
						random.nextGaussian() * 0.15
					);
				}

				float w = (float)(j >> 16 & 0xFF) / 255.0F;
				float x = (float)(j >> 8 & 0xFF) / 255.0F;
				float y = (float)(j >> 0 & 0xFF) / 255.0F;
				class_2394 lv2 = i == 2007 ? class_2398.field_11213 : class_2398.field_11245;

				for (int n = 0; n < 100; n++) {
					double g = random.nextDouble() * 4.0;
					double h = random.nextDouble() * Math.PI * 2.0;
					double o = Math.cos(h) * g;
					double p = 0.01 + random.nextDouble() * 0.5;
					double q = Math.sin(h) * g;
					class_703 lv3 = this.method_3282(lv2, lv2.method_10295().method_10299(), t + o * 0.1, u + 0.3, d + q * 0.1, o, p, q);
					if (lv3 != null) {
						float z = 0.75F + random.nextFloat() * 0.25F;
						lv3.method_3084(w * z, x * z, y * z);
						lv3.method_3075((float)g);
					}
				}

				this.field_4085.method_2947(arg2, class_3417.field_14839, class_3419.field_15254, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 2003:
				double t = (double)arg2.method_10263() + 0.5;
				double u = (double)arg2.method_10264();
				double d = (double)arg2.method_10260() + 0.5;

				for (int v = 0; v < 8; v++) {
					this.method_3276(
						new class_2392(class_2398.field_11218, new class_1799(class_1802.field_8449)),
						t,
						u,
						d,
						random.nextGaussian() * 0.15,
						random.nextDouble() * 0.2,
						random.nextGaussian() * 0.15
					);
				}

				for (double e = 0.0; e < Math.PI * 2; e += Math.PI / 20) {
					this.method_3276(class_2398.field_11214, t + Math.cos(e) * 5.0, u - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -5.0, 0.0, Math.sin(e) * -5.0);
					this.method_3276(class_2398.field_11214, t + Math.cos(e) * 5.0, u - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -7.0, 0.0, Math.sin(e) * -7.0);
				}
				break;
			case 2004:
				for (int aax = 0; aax < 20; aax++) {
					double ab = (double)arg2.method_10263() + 0.5 + ((double)this.field_4085.field_9229.nextFloat() - 0.5) * 2.0;
					double ac = (double)arg2.method_10264() + 0.5 + ((double)this.field_4085.field_9229.nextFloat() - 0.5) * 2.0;
					double ad = (double)arg2.method_10260() + 0.5 + ((double)this.field_4085.field_9229.nextFloat() - 0.5) * 2.0;
					this.field_4085.method_8406(class_2398.field_11251, ab, ac, ad, 0.0, 0.0, 0.0);
					this.field_4085.method_8406(class_2398.field_11240, ab, ac, ad, 0.0, 0.0, 0.0);
				}
				break;
			case 2005:
				class_1752.method_7721(this.field_4085, arg2, j);
				break;
			case 2006:
				for (int aa = 0; aa < 200; aa++) {
					float ae = random.nextFloat() * 4.0F;
					float af = random.nextFloat() * (float) (Math.PI * 2);
					double ac = (double)(class_3532.method_15362(af) * ae);
					double ad = 0.01 + random.nextDouble() * 0.5;
					double ag = (double)(class_3532.method_15374(af) * ae);
					class_703 lv6 = this.method_3282(
						class_2398.field_11216,
						false,
						(double)arg2.method_10263() + ac * 0.1,
						(double)arg2.method_10264() + 0.3,
						(double)arg2.method_10260() + ag * 0.1,
						ac,
						ad,
						ag
					);
					if (lv6 != null) {
						lv6.method_3075(ae);
					}
				}

				this.field_4085.method_2947(arg2, class_3417.field_14803, class_3419.field_15251, 1.0F, this.field_4085.field_9229.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 3000:
				this.field_4085
					.method_8466(
						class_2398.field_11221, true, (double)arg2.method_10263() + 0.5, (double)arg2.method_10264() + 0.5, (double)arg2.method_10260() + 0.5, 0.0, 0.0, 0.0
					);
				this.field_4085
					.method_2947(
						arg2,
						class_3417.field_14816,
						class_3419.field_15245,
						10.0F,
						(1.0F + (this.field_4085.field_9229.nextFloat() - this.field_4085.field_9229.nextFloat()) * 0.2F) * 0.7F,
						false
					);
				break;
			case 3001:
				this.field_4085.method_2947(arg2, class_3417.field_14671, class_3419.field_15251, 64.0F, 0.8F + this.field_4085.field_9229.nextFloat() * 0.3F, false);
		}
	}

	@Override
	public void method_8569(int i, class_2338 arg, int j) {
		if (j >= 0 && j < 10) {
			class_3191 lv = (class_3191)this.field_4058.get(i);
			if (lv == null
				|| lv.method_13991().method_10263() != arg.method_10263()
				|| lv.method_13991().method_10264() != arg.method_10264()
				|| lv.method_13991().method_10260() != arg.method_10260()) {
				lv = new class_3191(i, arg);
				this.field_4058.put(i, lv);
			}

			lv.method_13987(j);
			lv.method_13989(this.field_4073);
		} else {
			this.field_4058.remove(i);
		}
	}

	public boolean method_3281() {
		return this.field_4075.isEmpty() && this.field_4106.method_3630();
	}

	public void method_3292() {
		this.field_4077 = true;
		this.field_4107 = true;
	}

	public void method_3245(Collection<class_2586> collection, Collection<class_2586> collection2) {
		synchronized (this.field_4055) {
			this.field_4055.removeAll(collection);
			this.field_4055.addAll(collection2);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_762 {
		private final class_851 field_4124;
		private final class_2350 field_4125;
		private byte field_4126;
		private final int field_4122;

		private class_762(class_851 arg2, @Nullable class_2350 arg3, int i) {
			this.field_4124 = arg2;
			this.field_4125 = arg3;
			this.field_4122 = i;
		}

		public void method_3299(byte b, class_2350 arg) {
			this.field_4126 = (byte)(this.field_4126 | b | 1 << arg.ordinal());
		}

		public boolean method_3298(class_2350 arg) {
			return (this.field_4126 & 1 << arg.ordinal()) > 0;
		}
	}
}
