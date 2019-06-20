package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_959 extends class_927<class_1474, class_583<class_1474>> {
	private final class_612<class_1474> field_4800 = new class_612<>();
	private final class_615<class_1474> field_4799 = new class_615<>();

	public class_959(class_898 arg) {
		super(arg, new class_612<>(), 0.15F);
		this.method_4046(new class_1001(this));
	}

	@Nullable
	protected class_2960 method_4141(class_1474 arg) {
		return arg.method_6650();
	}

	public void method_4140(class_1474 arg, double d, double e, double f, float g, float h) {
		this.field_4737 = (class_583<class_1474>)(arg.method_6654() == 0 ? this.field_4800 : this.field_4799);
		float[] fs = arg.method_6658();
		GlStateManager.color3f(fs[0], fs[1], fs[2]);
		super.method_4072(arg, d, e, f, g, h);
	}

	protected void method_4142(class_1474 arg, float f, float g, float h) {
		super.method_4058(arg, f, g, h);
		float i = 4.3F * class_3532.method_15374(0.6F * f);
		GlStateManager.rotatef(i, 0.0F, 1.0F, 0.0F);
		if (!arg.method_5799()) {
			GlStateManager.translatef(0.2F, 0.1F, 0.0F);
			GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
