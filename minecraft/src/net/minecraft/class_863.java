package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_863 {
	public final class_868 field_4523;
	public final class_863.class_864 field_4528;
	public final class_863.class_864 field_4532;
	public final class_863.class_864 field_4538;
	public final class_863.class_864 field_4534;
	public final class_863.class_864 field_4535;
	public final class_859 field_4529;
	public final class_870 field_4539;
	public final class_863.class_864 field_4536;
	public final class_863.class_864 field_4537;
	public final class_863.class_864 field_4517;
	public final class_863.class_864 field_4533;
	public final class_4207 field_18777;
	public final class_4205 field_18778;
	private boolean field_4531;
	private boolean field_4530;
	private boolean field_4527;
	private boolean field_4526;
	private boolean field_4525;
	private boolean field_4524;
	private boolean field_4522;
	private boolean field_4521;
	private boolean field_4520;
	private boolean field_4519;
	private boolean field_4518;
	private boolean field_18775;
	private boolean field_18776;

	public class_863(class_310 arg) {
		this.field_4523 = new class_868(arg);
		this.field_4528 = new class_872(arg);
		this.field_4532 = new class_862(arg);
		this.field_4538 = new class_867(arg);
		this.field_4534 = new class_865(arg);
		this.field_4535 = new class_869(arg);
		this.field_4529 = new class_859(arg);
		this.field_4539 = new class_870(arg);
		this.field_4536 = new class_866(arg);
		this.field_4537 = new class_873(arg);
		this.field_4517 = new class_871(arg);
		this.field_4533 = new class_860(arg);
		this.field_18777 = new class_4207(arg);
		this.field_18778 = new class_4205(arg);
	}

	public boolean method_3710() {
		return this.field_4531
			|| this.field_4530
			|| this.field_4527
			|| this.field_4526
			|| this.field_4525
			|| this.field_4524
			|| this.field_4520
			|| this.field_4519
			|| this.field_4518
			|| this.field_18775
			|| this.field_18776;
	}

	public boolean method_3713() {
		this.field_4531 = !this.field_4531;
		return this.field_4531;
	}

	public void method_3709(long l) {
		if (this.field_4530) {
			this.field_4523.method_3715(l);
		}

		if (this.field_4531 && !class_310.method_1551().method_1555()) {
			this.field_4532.method_3715(l);
		}

		if (this.field_4527) {
			this.field_4528.method_3715(l);
		}

		if (this.field_4526) {
			this.field_4538.method_3715(l);
		}

		if (this.field_4525) {
			this.field_4534.method_3715(l);
		}

		if (this.field_4524) {
			this.field_4535.method_3715(l);
		}

		if (this.field_4522) {
			this.field_4529.method_3715(l);
		}

		if (this.field_4521) {
			this.field_4539.method_3715(l);
		}

		if (this.field_4520) {
			this.field_4536.method_3715(l);
		}

		if (this.field_4519) {
			this.field_4537.method_3715(l);
		}

		if (this.field_4518) {
			this.field_4517.method_3715(l);
		}

		if (this.field_18776) {
			this.field_18778.method_3715(l);
		}
	}

	public static Optional<class_1297> method_19694(@Nullable class_1297 arg, int i) {
		if (arg == null) {
			return Optional.empty();
		} else {
			class_243 lv = arg.method_5836(1.0F);
			class_243 lv2 = arg.method_5828(1.0F).method_1021((double)i);
			class_243 lv3 = lv.method_1019(lv2);
			class_238 lv4 = arg.method_5829().method_18804(lv2).method_1014(1.0);
			int j = i * i;
			Predicate<class_1297> predicate = argx -> !argx.method_7325() && argx.method_5863();
			class_3966 lv5 = class_1675.method_18075(arg, lv, lv3, lv4, predicate, (double)j);
			if (lv5 == null) {
				return Optional.empty();
			} else {
				return lv.method_1025(lv5.method_17784()) > (double)j ? Optional.empty() : Optional.of(lv5.method_17782());
			}
		}
	}

	public static void method_19697(class_2338 arg, class_2338 arg2, float f, float g, float h, float i) {
		class_4184 lv = class_310.method_1551().field_1773.method_19418();
		if (lv.method_19332()) {
			class_243 lv2 = lv.method_19326().method_19637();
			class_238 lv3 = new class_238(arg, arg2).method_997(lv2);
			method_19695(lv3, f, g, h, i);
		}
	}

	public static void method_19696(class_2338 arg, float f, float g, float h, float i, float j) {
		class_4184 lv = class_310.method_1551().field_1773.method_19418();
		if (lv.method_19332()) {
			class_243 lv2 = lv.method_19326().method_19637();
			class_238 lv3 = new class_238(arg).method_997(lv2).method_1014((double)f);
			method_19695(lv3, g, h, i, j);
		}
	}

	public static void method_19695(class_238 arg, float f, float g, float h, float i) {
		method_19692(arg.field_1323, arg.field_1322, arg.field_1321, arg.field_1320, arg.field_1325, arg.field_1324, f, g, h, i);
	}

	public static void method_19692(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(5, class_290.field_1576);
		class_761.method_3253(lv2, d, e, f, g, h, i, j, k, l, m);
		lv.method_1350();
	}

	public static void method_3711(String string, int i, int j, int k, int l) {
		method_3714(string, (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, l);
	}

	public static void method_3714(String string, double d, double e, double f, int i) {
		method_19429(string, d, e, f, i, 0.02F);
	}

	public static void method_19429(String string, double d, double e, double f, int i, float g) {
		method_3712(string, d, e, f, i, g, true, 0.0F, false);
	}

	public static void method_3712(String string, double d, double e, double f, int i, float g, boolean bl, float h, boolean bl2) {
		class_310 lv = class_310.method_1551();
		class_4184 lv2 = lv.field_1773.method_19418();
		if (lv2.method_19332() && lv.method_1561().field_4692 != null) {
			class_327 lv3 = lv.field_1772;
			double j = lv2.method_19326().field_1352;
			double k = lv2.method_19326().field_1351;
			double l = lv2.method_19326().field_1350;
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(d - j), (float)(e - k) + 0.07F, (float)(f - l));
			GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.scalef(g, -g, g);
			class_898 lv4 = lv.method_1561();
			GlStateManager.rotatef(-lv4.field_4679, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(-lv4.field_4677, 1.0F, 0.0F, 0.0F);
			GlStateManager.enableTexture();
			if (bl2) {
				GlStateManager.disableDepthTest();
			} else {
				GlStateManager.enableDepthTest();
			}

			GlStateManager.depthMask(true);
			GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
			float m = bl ? (float)(-lv3.method_1727(string)) / 2.0F : 0.0F;
			m -= h / g;
			lv3.method_1729(string, m, 0.0F, i);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableDepthTest();
			GlStateManager.popMatrix();
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_864 {
		void method_3715(long l);
	}
}
