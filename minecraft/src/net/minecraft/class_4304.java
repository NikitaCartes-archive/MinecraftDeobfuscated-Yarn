package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4304 implements class_863.class_864 {
	private final class_310 field_19326;
	private Collection<class_2338> field_19327 = Lists.<class_2338>newArrayList();

	public class_4304(class_310 arg) {
		this.field_19326 = arg;
	}

	public void method_20561(Collection<class_2338> collection) {
		this.field_19327 = collection;
	}

	@Override
	public void method_3715(long l) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.disableTexture();
		this.method_20562();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	private void method_20562() {
		class_2338 lv = this.method_20563().method_19328();

		for (class_2338 lv2 : this.field_19327) {
			if (lv.method_19771(lv2, 160.0)) {
				method_20559(lv2);
			}
		}
	}

	private static void method_20559(class_2338 arg) {
		class_863.method_19697(arg.method_10080(-0.5, -0.5, -0.5), arg.method_10080(1.5, 1.5, 1.5), 1.0F, 0.0F, 0.0F, 0.15F);
		int i = -65536;
		method_20560("Raid center", arg, -65536);
	}

	private static void method_20560(String string, class_2338 arg, int i) {
		double d = (double)arg.method_10263() + 0.5;
		double e = (double)arg.method_10264() + 1.3;
		double f = (double)arg.method_10260() + 0.5;
		class_863.method_3712(string, d, e, f, i, 0.04F, true, 0.0F, true);
	}

	private class_4184 method_20563() {
		return this.field_19326.field_1773.method_19418();
	}
}
