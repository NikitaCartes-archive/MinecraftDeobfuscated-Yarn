package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_370 implements class_368 {
	private final class_370.class_371 field_2213;
	private String field_2215;
	private String field_2217;
	private long field_2216;
	private boolean field_2214;

	public class_370(class_370.class_371 arg, class_2561 arg2, @Nullable class_2561 arg3) {
		this.field_2213 = arg;
		this.field_2215 = arg2.getString();
		this.field_2217 = arg3 == null ? null : arg3.getString();
	}

	@Override
	public class_368.class_369 method_1986(class_374 arg, long l) {
		if (this.field_2214) {
			this.field_2216 = l;
			this.field_2214 = false;
		}

		arg.method_1995().method_1531().method_4618(field_2207);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		arg.method_1788(0, 0, 0, 64, 160, 32);
		if (this.field_2217 == null) {
			arg.method_1995().field_1772.method_1729(this.field_2215, 18.0F, 12.0F, -256);
		} else {
			arg.method_1995().field_1772.method_1729(this.field_2215, 18.0F, 7.0F, -256);
			arg.method_1995().field_1772.method_1729(this.field_2217, 18.0F, 18.0F, -1);
		}

		return l - this.field_2216 < 5000L ? class_368.class_369.field_2210 : class_368.class_369.field_2209;
	}

	public void method_1991(class_2561 arg, @Nullable class_2561 arg2) {
		this.field_2215 = arg.getString();
		this.field_2217 = arg2 == null ? null : arg2.getString();
		this.field_2214 = true;
	}

	public class_370.class_371 method_1989() {
		return this.field_2213;
	}

	public static void method_1990(class_374 arg, class_370.class_371 arg2, class_2561 arg3, @Nullable class_2561 arg4) {
		class_370 lv = arg.method_1997(class_370.class, arg2);
		if (lv == null) {
			arg.method_1999(new class_370(arg2, arg3, arg4));
		} else {
			lv.method_1991(arg3, arg4);
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_371 {
		field_2218,
		field_2219,
		field_2220;
	}
}
