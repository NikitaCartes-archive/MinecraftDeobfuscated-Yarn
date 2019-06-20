package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_970<T extends class_1309, M extends class_572<T>, A extends class_572<T>> extends class_3887<T, M> {
	protected static final class_2960 field_4827 = new class_2960("textures/misc/enchanted_item_glint.png");
	protected final A field_4830;
	protected final A field_4831;
	private float field_4825 = 1.0F;
	private float field_4824 = 1.0F;
	private float field_4833 = 1.0F;
	private float field_4832 = 1.0F;
	private boolean field_4826;
	private static final Map<String, class_2960> field_4829 = Maps.<String, class_2960>newHashMap();

	protected class_970(class_3883<T, M> arg, A arg2, A arg3) {
		super(arg);
		this.field_4830 = arg2;
		this.field_4831 = arg3;
	}

	public void method_17157(T arg, float f, float g, float h, float i, float j, float k, float l) {
		this.method_4169(arg, f, g, h, i, j, k, l, class_1304.field_6174);
		this.method_4169(arg, f, g, h, i, j, k, l, class_1304.field_6172);
		this.method_4169(arg, f, g, h, i, j, k, l, class_1304.field_6166);
		this.method_4169(arg, f, g, h, i, j, k, l, class_1304.field_6169);
	}

	@Override
	public boolean method_4200() {
		return false;
	}

	private void method_4169(T arg, float f, float g, float h, float i, float j, float k, float l, class_1304 arg2) {
		class_1799 lv = arg.method_6118(arg2);
		if (lv.method_7909() instanceof class_1738) {
			class_1738 lv2 = (class_1738)lv.method_7909();
			if (lv2.method_7685() == arg2) {
				A lv3 = this.method_4172(arg2);
				this.method_17165().method_2818(lv3);
				lv3.method_17086(arg, f, g, h);
				this.method_4170(lv3, arg2);
				boolean bl = this.method_4173(arg2);
				this.method_17164(this.method_4168(lv2, bl));
				if (lv2 instanceof class_4057) {
					int m = ((class_4057)lv2).method_7800(lv);
					float n = (float)(m >> 16 & 0xFF) / 255.0F;
					float o = (float)(m >> 8 & 0xFF) / 255.0F;
					float p = (float)(m & 0xFF) / 255.0F;
					GlStateManager.color4f(this.field_4824 * n, this.field_4833 * o, this.field_4832 * p, this.field_4825);
					lv3.method_17088(arg, f, g, i, j, k, l);
					this.method_17164(this.method_4174(lv2, bl, "overlay"));
				}

				GlStateManager.color4f(this.field_4824, this.field_4833, this.field_4832, this.field_4825);
				lv3.method_17088(arg, f, g, i, j, k, l);
				if (!this.field_4826 && lv.method_7942()) {
					method_4171(this::method_17164, arg, lv3, f, g, h, i, j, k, l);
				}
			}
		}
	}

	public A method_4172(class_1304 arg) {
		return this.method_4173(arg) ? this.field_4830 : this.field_4831;
	}

	private boolean method_4173(class_1304 arg) {
		return arg == class_1304.field_6172;
	}

	public static <T extends class_1297> void method_4171(
		Consumer<class_2960> consumer, T arg, class_583<T> arg2, float f, float g, float h, float i, float j, float k, float l
	) {
		float m = (float)arg.field_6012 + h;
		consumer.accept(field_4827);
		class_757 lv = class_310.method_1551().field_1773;
		lv.method_3201(true);
		GlStateManager.enableBlend();
		GlStateManager.depthFunc(514);
		GlStateManager.depthMask(false);
		float n = 0.5F;
		GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);

		for (int o = 0; o < 2; o++) {
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.class_5119.SRC_COLOR, GlStateManager.class_5118.ONE);
			float p = 0.76F;
			GlStateManager.color4f(0.38F, 0.19F, 0.608F, 1.0F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float q = 0.33333334F;
			GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
			GlStateManager.rotatef(30.0F - (float)o * 60.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translatef(0.0F, m * (0.001F + (float)o * 0.003F) * 20.0F, 0.0F);
			GlStateManager.matrixMode(5888);
			arg2.method_2819(arg, f, g, i, j, k, l);
			GlStateManager.blendFunc(GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO);
		}

		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		GlStateManager.enableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(515);
		GlStateManager.disableBlend();
		lv.method_3201(false);
	}

	private class_2960 method_4168(class_1738 arg, boolean bl) {
		return this.method_4174(arg, bl, null);
	}

	private class_2960 method_4174(class_1738 arg, boolean bl, @Nullable String string) {
		String string2 = "textures/models/armor/" + arg.method_7686().method_7694() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
		return (class_2960)field_4829.computeIfAbsent(string2, class_2960::new);
	}

	protected abstract void method_4170(A arg, class_1304 arg2);

	protected abstract void method_4190(A arg);
}
