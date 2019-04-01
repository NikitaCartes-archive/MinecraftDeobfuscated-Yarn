package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_476 extends class_465<class_1707> implements class_3936<class_1707> {
	private static final class_2960 field_2861 = new class_2960("textures/gui/container/generic_54.png");
	private final int field_2864;

	public class_476(class_1707 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
		this.passEvents = false;
		int i = 222;
		int j = 114;
		this.field_2864 = arg.method_17388();
		this.field_2779 = 114 + this.field_2864 * 18;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);
		this.method_2380(i, j);
	}

	@Override
	protected void method_2388(int i, int j) {
		this.font.method_1729(this.title.method_10863(), 8.0F, 6.0F, 4210752);
		this.font.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2861);
		int k = (this.width - this.field_2792) / 2;
		int l = (this.height - this.field_2779) / 2;
		this.blit(k, l, 0, 0, this.field_2792, this.field_2864 * 18 + 17);
		this.blit(k, l + this.field_2864 * 18 + 17, 0, 126, this.field_2792, 96);
	}
}
