package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_936 extends class_927<class_1454, class_583<class_1454>> {
	private static final class_2960 field_4762 = new class_2960("textures/entity/fish/pufferfish.png");
	private int field_4765;
	private final class_594<class_1454> field_4761 = new class_594<>();
	private final class_595<class_1454> field_4764 = new class_595<>();
	private final class_592<class_1454> field_4763 = new class_592<>();

	public class_936(class_898 arg) {
		super(arg, new class_592<>(), 0.1F);
		this.field_4765 = 3;
	}

	@Nullable
	protected class_2960 method_4096(class_1454 arg) {
		return field_4762;
	}

	public void method_4094(class_1454 arg, double d, double e, double f, float g, float h) {
		int i = arg.method_6594();
		if (i != this.field_4765) {
			if (i == 0) {
				this.field_4737 = this.field_4761;
			} else if (i == 1) {
				this.field_4737 = this.field_4764;
			} else {
				this.field_4737 = this.field_4763;
			}
		}

		this.field_4765 = i;
		this.field_4673 = 0.1F + 0.1F * (float)i;
		super.method_4072(arg, d, e, f, g, h);
	}

	protected void method_4095(class_1454 arg, float f, float g, float h) {
		GlStateManager.translatef(0.0F, class_3532.method_15362(f * 0.05F) * 0.08F, 0.0F);
		super.method_4058(arg, f, g, h);
	}
}
