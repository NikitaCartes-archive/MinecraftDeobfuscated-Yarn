package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
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
			|| this.field_4518;
	}

	public boolean method_3713() {
		this.field_4531 = !this.field_4531;
		return this.field_4531;
	}

	public void method_3709(float f, long l) {
		if (this.field_4530) {
			this.field_4523.method_3715(f, l);
		}

		if (this.field_4531 && !class_310.method_1551().method_1555()) {
			this.field_4532.method_3715(f, l);
		}

		if (this.field_4527) {
			this.field_4528.method_3715(f, l);
		}

		if (this.field_4526) {
			this.field_4538.method_3715(f, l);
		}

		if (this.field_4525) {
			this.field_4534.method_3715(f, l);
		}

		if (this.field_4524) {
			this.field_4535.method_3715(f, l);
		}

		if (this.field_4522) {
			this.field_4529.method_3715(f, l);
		}

		if (this.field_4521) {
			this.field_4539.method_3715(f, l);
		}

		if (this.field_4520) {
			this.field_4536.method_3715(f, l);
		}

		if (this.field_4519) {
			this.field_4537.method_3715(f, l);
		}

		if (this.field_4518) {
			this.field_4517.method_3715(f, l);
		}
	}

	public static void method_3711(String string, int i, int j, int k, float f, int l) {
		method_3714(string, (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, f, l);
	}

	public static void method_3714(String string, double d, double e, double f, float g, int i) {
		method_3712(string, d, e, f, g, i, 0.02F);
	}

	public static void method_3712(String string, double d, double e, double f, float g, int i, float h) {
		class_310 lv = class_310.method_1551();
		if (lv.field_1724 != null && lv.method_1561() != null && lv.method_1561().field_4692 != null) {
			class_327 lv2 = lv.field_1772;
			class_1657 lv3 = lv.field_1724;
			double j = class_3532.method_16436((double)g, lv3.field_6038, lv3.field_5987);
			double k = class_3532.method_16436((double)g, lv3.field_5971, lv3.field_6010);
			double l = class_3532.method_16436((double)g, lv3.field_5989, lv3.field_6035);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(d - j), (float)(e - k) + 0.07F, (float)(f - l));
			GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.scalef(h, -h, h);
			class_898 lv4 = lv.method_1561();
			GlStateManager.rotatef(-lv4.field_4679, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef((float)(lv4.field_4692.field_1850 == 2 ? 1 : -1) * lv4.field_4677, 1.0F, 0.0F, 0.0F);
			GlStateManager.disableLighting();
			GlStateManager.enableTexture();
			GlStateManager.enableDepthTest();
			GlStateManager.depthMask(true);
			GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
			lv2.method_1729(string, (float)(-lv2.method_1727(string) / 2), 0.0F, i);
			GlStateManager.enableLighting();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_864 {
		void method_3715(float f, long l);
	}
}
