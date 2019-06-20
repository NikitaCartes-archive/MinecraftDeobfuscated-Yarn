package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_776 implements class_4013 {
	private final class_773 field_4168;
	private final class_778 field_4170;
	private final class_774 field_4171 = new class_774();
	private final class_775 field_4167;
	private final Random field_4169 = new Random();

	public class_776(class_773 arg, class_324 arg2) {
		this.field_4168 = arg;
		this.field_4170 = new class_778(arg2);
		this.field_4167 = new class_775();
	}

	public class_773 method_3351() {
		return this.field_4168;
	}

	public void method_3354(class_2680 arg, class_2338 arg2, class_1058 arg3, class_1920 arg4) {
		if (arg.method_11610() == class_2464.field_11458) {
			class_1087 lv = this.field_4168.method_3335(arg);
			long l = arg.method_11617(arg2);
			class_1087 lv2 = new class_1093.class_1094(arg, lv, arg3, this.field_4169, l).method_4746();
			this.field_4170.method_3374(arg4, lv2, arg, arg2, class_289.method_1348().method_1349(), true, this.field_4169, l);
		}
	}

	public boolean method_3355(class_2680 arg, class_2338 arg2, class_1920 arg3, class_287 arg4, Random random) {
		try {
			class_2464 lv = arg.method_11610();
			if (lv == class_2464.field_11455) {
				return false;
			} else {
				switch (lv) {
					case field_11458:
						return this.field_4170.method_3374(arg3, this.method_3349(arg), arg, arg2, arg4, true, random, arg.method_11617(arg2));
					case field_11456:
						return false;
					default:
						return false;
				}
			}
		} catch (Throwable var9) {
			class_128 lv2 = class_128.method_560(var9, "Tesselating block in world");
			class_129 lv3 = lv2.method_562("Block being tesselated");
			class_129.method_586(lv3, arg2, arg);
			throw new class_148(lv2);
		}
	}

	public boolean method_3352(class_2338 arg, class_1920 arg2, class_287 arg3, class_3610 arg4) {
		try {
			return this.field_4167.method_3347(arg2, arg, arg3, arg4);
		} catch (Throwable var8) {
			class_128 lv = class_128.method_560(var8, "Tesselating liquid in world");
			class_129 lv2 = lv.method_562("Block being tesselated");
			class_129.method_586(lv2, arg, null);
			throw new class_148(lv);
		}
	}

	public class_778 method_3350() {
		return this.field_4170;
	}

	public class_1087 method_3349(class_2680 arg) {
		return this.field_4168.method_3335(arg);
	}

	public void method_3353(class_2680 arg, float f) {
		class_2464 lv = arg.method_11610();
		if (lv != class_2464.field_11455) {
			switch (lv) {
				case field_11458:
					class_1087 lv2 = this.method_3349(arg);
					this.field_4170.method_3366(lv2, arg, f, true);
					break;
				case field_11456:
					this.field_4171.method_3342(arg.method_11614(), f);
			}
		}
	}

	@Override
	public void method_14491(class_3300 arg) {
		this.field_4167.method_3345();
	}
}
