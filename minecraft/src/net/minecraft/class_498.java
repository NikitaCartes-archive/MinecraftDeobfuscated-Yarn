package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_498 extends class_437 {
	private final class_2625 field_3031;
	private int field_3030;
	private int field_3029;
	private class_3728 field_3032;

	public class_498(class_2625 arg) {
		this.field_3031 = arg;
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		this.method_2219(new class_339(0, this.field_2561 / 2 - 100, this.field_2559 / 4 + 120, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_498.this.method_2526();
			}
		});
		this.field_3031.method_11303(false);
		this.field_3032 = new class_3728(
			this.field_2563,
			() -> this.field_3031.method_11302(this.field_3029).getString(),
			string -> this.field_3031.method_11299(this.field_3029, new class_2585(string)),
			90
		);
	}

	@Override
	public void method_2234() {
		this.field_2563.field_1774.method_1462(false);
		class_634 lv = this.field_2563.method_1562();
		if (lv != null) {
			lv.method_2883(
				new class_2877(
					this.field_3031.method_11016(),
					this.field_3031.method_11302(0),
					this.field_3031.method_11302(1),
					this.field_3031.method_11302(2),
					this.field_3031.method_11302(3)
				)
			);
		}

		this.field_3031.method_11303(true);
	}

	@Override
	public void method_2225() {
		this.field_3030++;
	}

	private void method_2526() {
		this.field_3031.method_5431();
		this.field_2563.method_1507(null);
	}

	@Override
	public boolean method_16806(char c, int i) {
		this.field_3032.method_16199(c);
		return true;
	}

	@Override
	public void method_2210() {
		this.method_2526();
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (i == 265) {
			this.field_3029 = this.field_3029 - 1 & 3;
			this.field_3032.method_16204();
			return true;
		} else if (i == 264 || i == 257 || i == 335) {
			this.field_3029 = this.field_3029 + 1 & 3;
			this.field_3032.method_16204();
			return true;
		} else {
			return this.field_3032.method_16202(i) ? true : super.method_16805(i, j, k);
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, class_1074.method_4662("sign.edit"), this.field_2561 / 2, 40, 16777215);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)(this.field_2561 / 2), 0.0F, 50.0F);
		float g = 93.75F;
		GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		class_2680 lv = this.field_3031.method_11010();
		float h;
		if (lv.method_11614().method_9525(class_3481.field_15472)) {
			h = (float)((Integer)lv.method_11654(class_2508.field_11559) * 360) / 16.0F;
		} else {
			h = ((class_2350)lv.method_11654(class_2551.field_11726)).method_10144();
		}

		GlStateManager.rotatef(h, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
		this.field_3031.method_16332(this.field_3029, this.field_3032.method_16201(), this.field_3032.method_16203(), this.field_3030 / 6 % 2 == 0);
		class_824.field_4346.method_3548(this.field_3031, -0.5, -0.75, -0.5, 0.0F);
		this.field_3031.method_16335();
		GlStateManager.popMatrix();
		super.method_2214(i, j, f);
	}
}
