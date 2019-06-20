package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_417 extends class_437 {
	private static final class_2960 field_2447 = new class_2960("textures/gui/demo_background.png");

	public class_417() {
		super(new class_2588("demo.help.title"));
	}

	@Override
	protected void init() {
		int i = -16;
		this.addButton(new class_4185(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, class_1074.method_4662("demo.help.buy"), arg -> {
			arg.active = false;
			class_156.method_668().method_670("http://www.minecraft.net/store?source=demo");
		}));
		this.addButton(new class_4185(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, class_1074.method_4662("demo.help.later"), arg -> {
			this.minecraft.method_1507(null);
			this.minecraft.field_1729.method_1612();
		}));
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2447);
		int i = (this.width - 248) / 2;
		int j = (this.height - 166) / 2;
		this.blit(i, j, 0, 0, 248, 166);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		int k = (this.width - 248) / 2 + 10;
		int l = (this.height - 166) / 2 + 8;
		this.font.method_1729(this.title.method_10863(), (float)k, (float)l, 2039583);
		l += 12;
		class_315 lv = this.minecraft.field_1690;
		this.font
			.method_1729(
				class_1074.method_4662(
					"demo.help.movementShort", lv.field_1894.method_16007(), lv.field_1913.method_16007(), lv.field_1881.method_16007(), lv.field_1849.method_16007()
				),
				(float)k,
				(float)l,
				5197647
			);
		this.font.method_1729(class_1074.method_4662("demo.help.movementMouse"), (float)k, (float)(l + 12), 5197647);
		this.font.method_1729(class_1074.method_4662("demo.help.jump", lv.field_1903.method_16007()), (float)k, (float)(l + 24), 5197647);
		this.font.method_1729(class_1074.method_4662("demo.help.inventory", lv.field_1822.method_16007()), (float)k, (float)(l + 36), 5197647);
		this.font.method_1712(class_1074.method_4662("demo.help.fullWrapped"), k, l + 68, 218, 2039583);
		super.render(i, j, f);
	}
}
