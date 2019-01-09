package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3934 extends class_465<class_3910> {
	private static final class_2960 field_17421 = new class_2960("textures/gui/container/cartography_table.png");

	public class_3934(class_3910 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		super.method_2214(i, j, f);
		this.method_2380(i, j);
	}

	@Override
	protected void method_2388(int i, int j) {
		this.field_2554.method_1729(this.field_17411.method_10863(), 8.0F, 4.0F, 4210752);
		this.field_2554.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		this.method_2240();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_17421);
		int k = this.field_2776;
		int l = this.field_2800;
		this.method_1788(k, l, 0, 0, this.field_2792, this.field_2779);
		class_1792 lv = this.field_2797.method_7611(1).method_7677().method_7909();
		boolean bl = lv == class_1802.field_8895;
		boolean bl2 = lv == class_1802.field_8407;
		boolean bl3 = lv == class_1802.field_8141;
		class_1799 lv2 = this.field_2797.method_7611(0).method_7677();
		boolean bl4 = false;
		class_22 lv3;
		if (lv2.method_7909() == class_1802.field_8204) {
			lv3 = class_1806.method_17441(lv2, this.field_2563.field_1687);
			if (lv3 != null) {
				if (lv3.field_17403) {
					bl4 = true;
					if (bl2 || bl3) {
						this.method_1788(k + 35, l + 31, this.field_2792 + 50, 132, 28, 21);
					}
				}

				if (bl2 && lv3.field_119 >= 4) {
					bl4 = true;
					this.method_1788(k + 35, l + 31, this.field_2792 + 50, 132, 28, 21);
				}
			}
		} else {
			lv3 = null;
		}

		this.method_17567(lv3, bl, bl2, bl3, bl4);
	}

	private void method_17567(@Nullable class_22 arg, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		int i = this.field_2776;
		int j = this.field_2800;
		if (bl2 && !bl4) {
			this.method_1788(i + 67, j + 13, this.field_2792, 66, 66, 66);
			this.method_17566(arg, i + 85, j + 31, 0.226F);
		} else if (bl) {
			this.method_1788(i + 67 + 16, j + 13, this.field_2792, 132, 50, 66);
			this.method_17566(arg, i + 86, j + 16, 0.34F);
			this.field_2563.method_1531().method_4618(field_17421);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 1.0F);
			this.method_1788(i + 67, j + 13 + 16, this.field_2792, 132, 50, 66);
			this.method_17566(arg, i + 70, j + 32, 0.34F);
			GlStateManager.popMatrix();
		} else if (bl3) {
			this.method_1788(i + 67, j + 13, this.field_2792, 0, 66, 66);
			this.method_17566(arg, i + 71, j + 17, 0.45F);
			this.field_2563.method_1531().method_4618(field_17421);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 1.0F);
			this.method_1788(i + 66, j + 12, 0, this.field_2779, 66, 66);
			GlStateManager.popMatrix();
		} else {
			this.method_1788(i + 67, j + 13, this.field_2792, 0, 66, 66);
			this.method_17566(arg, i + 71, j + 17, 0.45F);
		}
	}

	private void method_17566(@Nullable class_22 arg, int i, int j, float f) {
		if (arg != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)i, (float)j, 1.0F);
			GlStateManager.scalef(f, f, 1.0F);
			this.field_2563.field_1773.method_3194().method_1773(arg, true);
			GlStateManager.popMatrix();
		}
	}
}
