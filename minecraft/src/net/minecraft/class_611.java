package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_611<T extends class_1297> extends class_583<T> {
	private final class_630 field_3583;
	private final class_630 field_3585;
	private final class_630 field_3584;
	private final class_630 field_3580;
	private final class_630 field_3578;
	private final class_630 field_3586;
	private final class_630 field_3577;
	private final class_630 field_3579;
	private final class_630 field_3581;
	private final class_630 field_3576;
	private final class_630 field_3582;

	public class_611() {
		float f = 0.0F;
		int i = 15;
		this.field_3583 = new class_630(this, 32, 4);
		this.field_3583.method_2856(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
		this.field_3583.method_2851(0.0F, 15.0F, -3.0F);
		this.field_3585 = new class_630(this, 0, 0);
		this.field_3585.method_2856(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F);
		this.field_3585.method_2851(0.0F, 15.0F, 0.0F);
		this.field_3584 = new class_630(this, 0, 12);
		this.field_3584.method_2856(-5.0F, -4.0F, -6.0F, 10, 8, 12, 0.0F);
		this.field_3584.method_2851(0.0F, 15.0F, 9.0F);
		this.field_3580 = new class_630(this, 18, 0);
		this.field_3580.method_2856(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3580.method_2851(-4.0F, 15.0F, 2.0F);
		this.field_3578 = new class_630(this, 18, 0);
		this.field_3578.method_2856(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3578.method_2851(4.0F, 15.0F, 2.0F);
		this.field_3586 = new class_630(this, 18, 0);
		this.field_3586.method_2856(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3586.method_2851(-4.0F, 15.0F, 1.0F);
		this.field_3577 = new class_630(this, 18, 0);
		this.field_3577.method_2856(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3577.method_2851(4.0F, 15.0F, 1.0F);
		this.field_3579 = new class_630(this, 18, 0);
		this.field_3579.method_2856(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3579.method_2851(-4.0F, 15.0F, 0.0F);
		this.field_3581 = new class_630(this, 18, 0);
		this.field_3581.method_2856(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3581.method_2851(4.0F, 15.0F, 0.0F);
		this.field_3576 = new class_630(this, 18, 0);
		this.field_3576.method_2856(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3576.method_2851(-4.0F, 15.0F, -1.0F);
		this.field_3582 = new class_630(this, 18, 0);
		this.field_3582.method_2856(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3582.method_2851(4.0F, 15.0F, -1.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3583.method_2846(k);
		this.field_3585.method_2846(k);
		this.field_3584.method_2846(k);
		this.field_3580.method_2846(k);
		this.field_3578.method_2846(k);
		this.field_3586.method_2846(k);
		this.field_3577.method_2846(k);
		this.field_3579.method_2846(k);
		this.field_3581.method_2846(k);
		this.field_3576.method_2846(k);
		this.field_3582.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3583.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3583.field_3654 = j * (float) (Math.PI / 180.0);
		float l = (float) (Math.PI / 4);
		this.field_3580.field_3674 = (float) (-Math.PI / 4);
		this.field_3578.field_3674 = (float) (Math.PI / 4);
		this.field_3586.field_3674 = -0.58119464F;
		this.field_3577.field_3674 = 0.58119464F;
		this.field_3579.field_3674 = -0.58119464F;
		this.field_3581.field_3674 = 0.58119464F;
		this.field_3576.field_3674 = (float) (-Math.PI / 4);
		this.field_3582.field_3674 = (float) (Math.PI / 4);
		float m = -0.0F;
		float n = (float) (Math.PI / 8);
		this.field_3580.field_3675 = (float) (Math.PI / 4);
		this.field_3578.field_3675 = (float) (-Math.PI / 4);
		this.field_3586.field_3675 = (float) (Math.PI / 8);
		this.field_3577.field_3675 = (float) (-Math.PI / 8);
		this.field_3579.field_3675 = (float) (-Math.PI / 8);
		this.field_3581.field_3675 = (float) (Math.PI / 8);
		this.field_3576.field_3675 = (float) (-Math.PI / 4);
		this.field_3582.field_3675 = (float) (Math.PI / 4);
		float o = -(class_3532.method_15362(f * 0.6662F * 2.0F + 0.0F) * 0.4F) * g;
		float p = -(class_3532.method_15362(f * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * g;
		float q = -(class_3532.method_15362(f * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * g;
		float r = -(class_3532.method_15362(f * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * g;
		float s = Math.abs(class_3532.method_15374(f * 0.6662F + 0.0F) * 0.4F) * g;
		float t = Math.abs(class_3532.method_15374(f * 0.6662F + (float) Math.PI) * 0.4F) * g;
		float u = Math.abs(class_3532.method_15374(f * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * g;
		float v = Math.abs(class_3532.method_15374(f * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * g;
		this.field_3580.field_3675 += o;
		this.field_3578.field_3675 += -o;
		this.field_3586.field_3675 += p;
		this.field_3577.field_3675 += -p;
		this.field_3579.field_3675 += q;
		this.field_3581.field_3675 += -q;
		this.field_3576.field_3675 += r;
		this.field_3582.field_3675 += -r;
		this.field_3580.field_3674 += s;
		this.field_3578.field_3674 += -s;
		this.field_3586.field_3674 += t;
		this.field_3577.field_3674 += -t;
		this.field_3579.field_3674 += u;
		this.field_3581.field_3674 += -u;
		this.field_3576.field_3674 += v;
		this.field_3582.field_3674 += -v;
	}
}
