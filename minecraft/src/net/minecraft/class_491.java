package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_491 extends class_465<class_1724> {
	private static final class_2960 field_2937 = new class_2960("textures/gui/container/horse.png");
	private final class_1496 field_2941;
	private float field_2939;
	private float field_2938;

	public class_491(class_1724 arg, class_1661 arg2, class_1496 arg3) {
		super(arg, arg2, arg3.method_5476());
		this.field_2941 = arg3;
		this.passEvents = false;
	}

	@Override
	protected void method_2388(int i, int j) {
		this.font.method_1729(this.title.method_10863(), 8.0F, 6.0F, 4210752);
		this.font.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2937);
		int k = (this.width - this.field_2792) / 2;
		int l = (this.height - this.field_2779) / 2;
		this.blit(k, l, 0, 0, this.field_2792, this.field_2779);
		if (this.field_2941 instanceof class_1492) {
			class_1492 lv = (class_1492)this.field_2941;
			if (lv.method_6703()) {
				this.blit(k + 79, l + 17, 0, this.field_2779, lv.method_6702() * 18, 54);
			}
		}

		if (this.field_2941.method_6765()) {
			this.blit(k + 7, l + 35 - 18, 18, this.field_2779 + 54, 18, 18);
		}

		if (this.field_2941.method_6735()) {
			if (this.field_2941 instanceof class_1501) {
				this.blit(k + 7, l + 35, 36, this.field_2779 + 54, 18, 18);
			} else {
				this.blit(k + 7, l + 35, 0, this.field_2779 + 54, 18, 18);
			}
		}

		class_490.method_2486(k + 51, l + 60, 17, (float)(k + 51) - this.field_2939, (float)(l + 75 - 50) - this.field_2938, this.field_2941);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_2939 = (float)i;
		this.field_2938 = (float)j;
		super.render(i, j, f);
		this.method_2380(i, j);
	}
}
