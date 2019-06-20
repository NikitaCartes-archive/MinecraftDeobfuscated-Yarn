package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4042 extends class_927<class_4019, class_4041<class_4019>> {
	private static final class_2960 field_18026 = new class_2960("textures/entity/fox/fox.png");
	private static final class_2960 field_18027 = new class_2960("textures/entity/fox/fox_sleep.png");
	private static final class_2960 field_18028 = new class_2960("textures/entity/fox/snow_fox.png");
	private static final class_2960 field_18029 = new class_2960("textures/entity/fox/snow_fox_sleep.png");

	public class_4042(class_898 arg) {
		super(arg, new class_4041<>(), 0.4F);
		this.method_4046(new class_4043(this));
	}

	protected void method_18334(class_4019 arg, float f, float g, float h) {
		super.method_4058(arg, f, g, h);
		if (arg.method_18274() || arg.method_18273()) {
			GlStateManager.rotatef(-class_3532.method_16439(h, arg.field_6004, arg.field_5965), 1.0F, 0.0F, 0.0F);
		}
	}

	@Nullable
	protected class_2960 method_18333(class_4019 arg) {
		if (arg.method_18271() == class_4019.class_4039.field_17996) {
			return arg.method_6113() ? field_18027 : field_18026;
		} else {
			return arg.method_6113() ? field_18029 : field_18028;
		}
	}
}
