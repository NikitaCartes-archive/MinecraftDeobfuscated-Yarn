package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_824 {
	private final Map<Class<? extends class_2586>, class_827<? extends class_2586>> field_4345 = Maps.<Class<? extends class_2586>, class_827<? extends class_2586>>newHashMap();
	public static final class_824 field_4346 = new class_824();
	private class_327 field_4342;
	public static double field_4343;
	public static double field_4341;
	public static double field_4340;
	public class_1060 field_4347;
	public class_1937 field_4348;
	public class_4184 field_4344;
	public class_239 field_4350;

	private class_824() {
		this.field_4345.put(class_2625.class, new class_837());
		this.field_4345.put(class_2636.class, new class_839());
		this.field_4345.put(class_2669.class, new class_835());
		this.field_4345.put(class_2595.class, new class_826());
		this.field_4345.put(class_2611.class, new class_826());
		this.field_4345.put(class_2605.class, new class_828());
		this.field_4345.put(class_3722.class, new class_3942());
		this.field_4345.put(class_2640.class, new class_840());
		this.field_4345.put(class_2643.class, new class_841());
		this.field_4345.put(class_2580.class, new class_822());
		this.field_4345.put(class_2631.class, new class_836());
		this.field_4345.put(class_2573.class, new class_823());
		this.field_4345.put(class_2633.class, new class_838());
		this.field_4345.put(class_2627.class, new class_834(new class_602()));
		this.field_4345.put(class_2587.class, new class_825());
		this.field_4345.put(class_2597.class, new class_829());
		this.field_4345.put(class_3721.class, new class_3880());
		this.field_4345.put(class_3924.class, new class_3941());
		this.field_4345.put(class_3719.class, new class_4297());

		for (class_827<?> lv : this.field_4345.values()) {
			lv.method_3568(this);
		}
	}

	public <T extends class_2586> class_827<T> method_3550(Class<? extends class_2586> class_) {
		class_827<? extends class_2586> lv = (class_827<? extends class_2586>)this.field_4345.get(class_);
		if (lv == null && class_ != class_2586.class) {
			lv = this.method_3550(class_.getSuperclass());
			this.field_4345.put(class_, lv);
		}

		return (class_827<T>)lv;
	}

	@Nullable
	public <T extends class_2586> class_827<T> method_3553(@Nullable class_2586 arg) {
		return arg == null ? null : this.method_3550(arg.getClass());
	}

	public void method_3549(class_1937 arg, class_1060 arg2, class_327 arg3, class_4184 arg4, class_239 arg5) {
		if (this.field_4348 != arg) {
			this.method_3551(arg);
		}

		this.field_4347 = arg2;
		this.field_4344 = arg4;
		this.field_4342 = arg3;
		this.field_4350 = arg5;
	}

	public void method_3555(class_2586 arg, float f, int i) {
		if (arg.method_11008(this.field_4344.method_19326().field_1352, this.field_4344.method_19326().field_1351, this.field_4344.method_19326().field_1350)
			< arg.method_11006()) {
			class_308.method_1452();
			int j = this.field_4348.method_8313(arg.method_11016(), 0);
			int k = j % 65536;
			int l = j / 65536;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)k, (float)l);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			class_2338 lv = arg.method_11016();
			this.method_3554(arg, (double)lv.method_10263() - field_4343, (double)lv.method_10264() - field_4341, (double)lv.method_10260() - field_4340, f, i, false);
		}
	}

	public void method_3548(class_2586 arg, double d, double e, double f, float g) {
		this.method_3554(arg, d, e, f, g, -1, false);
	}

	public void method_3552(class_2586 arg) {
		this.method_3554(arg, 0.0, 0.0, 0.0, 0.0F, -1, true);
	}

	public void method_3554(class_2586 arg, double d, double e, double f, float g, int i, boolean bl) {
		class_827<class_2586> lv = this.method_3553(arg);
		if (lv != null) {
			try {
				if (!bl && (!arg.method_11002() || !arg.method_11010().method_11614().method_9570())) {
					return;
				}

				lv.method_3569(arg, d, e, f, g, i);
			} catch (Throwable var15) {
				class_128 lv2 = class_128.method_560(var15, "Rendering Block Entity");
				class_129 lv3 = lv2.method_562("Block Entity Details");
				arg.method_11003(lv3);
				throw new class_148(lv2);
			}
		}
	}

	public void method_3551(@Nullable class_1937 arg) {
		this.field_4348 = arg;
		if (arg == null) {
			this.field_4344 = null;
		}
	}

	public class_327 method_3556() {
		return this.field_4342;
	}
}
