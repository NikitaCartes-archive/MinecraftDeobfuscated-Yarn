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
	public void method_3715(long l) {
		class_4184 lv = this.field_4634.field_1773.method_19418();
		double d = lv.method_19326().field_1352;
		double e = lv.method_19326().field_1351;
		double f = lv.method_19326().field_1350;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.disableTexture();
		class_289 lv2 = class_289.method_1348();
		class_287 lv3 = lv2.method_1349();
		lv3.method_1328(5, class_290.field_1576);

		for (int i = 0; i < this.field_4640.size(); i++) {
			class_2338 lv4 = (class_2338)this.field_4640.get(i);
			Float float_ = (Float)this.field_4635.get(i);
			float g = float_ / 2.0F;
			class_761.method_3253(
				lv3,
				(double)((float)lv4.method_10263() + 0.5F - g) - d,
				(double)((float)lv4.method_10264() + 0.5F - g) - e,
				(double)((float)lv4.method_10260() + 0.5F - g) - f,
				(double)((float)lv4.method_10263() + 0.5F + g) - d,
				(double)((float)lv4.method_10264() + 0.5F + g) - e,
				(double)((float)lv4.method_10260() + 0.5F + g) - f,
				(Float)this.field_4639.get(i),
				(Float)this.field_4636.get(i),
				(Float)this.field_4638.get(i),
				(Float)this.field_4637.get(i)
			);
		}

		lv2.method_1350();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
