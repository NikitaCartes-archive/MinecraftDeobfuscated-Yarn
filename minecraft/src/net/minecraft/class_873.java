package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_873 implements class_863.class_864 {
	private final class_310 field_4634;
	private final List<class_2338> field_4640 = Lists.<class_2338>newArrayList();
	private final List<Float> field_4635 = Lists.<Float>newArrayList();
	private final List<Float> field_4637 = Lists.<Float>newArrayList();
	private final List<Float> field_4639 = Lists.<Float>newArrayList();
	private final List<Float> field_4636 = Lists.<Float>newArrayList();
	private final List<Float> field_4638 = Lists.<Float>newArrayList();

	public class_873(class_310 arg) {
		this.field_4634 = arg;
	}

	public void method_3872(class_2338 arg, float f, float g, float h, float i, float j) {
		this.field_4640.add(arg);
		this.field_4635.add(f);
		this.field_4637.add(j);
		this.field_4639.add(g);
		this.field_4636.add(h);
		this.field_4638.add(i);
	}

	@Override
	public void method_3715(float f, long l) {
		class_1657 lv = this.field_4634.field_1724;
		class_1922 lv2 = this.field_4634.field_1687;
		double d = class_3532.method_16436((double)f, lv.field_6038, lv.field_5987);
		double e = class_3532.method_16436((double)f, lv.field_5971, lv.field_6010);
		double g = class_3532.method_16436((double)f, lv.field_5989, lv.field_6035);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.disableTexture();
		new class_2338(lv.field_5987, 0.0, lv.field_6035);
		class_289 lv4 = class_289.method_1348();
		class_287 lv5 = lv4.method_1349();
		lv5.method_1328(5, class_290.field_1576);

		for (int i = 0; i < this.field_4640.size(); i++) {
			class_2338 lv6 = (class_2338)this.field_4640.get(i);
			Float float_ = (Float)this.field_4635.get(i);
			float h = float_ / 2.0F;
			class_761.method_3253(
				lv5,
				(double)((float)lv6.method_10263() + 0.5F - h) - d,
				(double)((float)lv6.method_10264() + 0.5F - h) - e,
				(double)((float)lv6.method_10260() + 0.5F - h) - g,
				(double)((float)lv6.method_10263() + 0.5F + h) - d,
				(double)((float)lv6.method_10264() + 0.5F + h) - e,
				(double)((float)lv6.method_10260() + 0.5F + h) - g,
				(Float)this.field_4639.get(i),
				(Float)this.field_4636.get(i),
				(Float)this.field_4638.get(i),
				(Float)this.field_4637.get(i)
			);
		}

		lv4.method_1350();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
