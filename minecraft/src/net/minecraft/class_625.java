package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_625 extends class_583<class_1510> {
	private final class_630 field_3630;
	private final class_630 field_3637;
	private final class_630 field_3631;
	private final class_630 field_3627;
	private final class_630 field_3633;
	private final class_630 field_3632;
	private final class_630 field_3626;
	private final class_630 field_3634;
	private final class_630 field_3628;
	private final class_630 field_3625;
	private final class_630 field_3629;
	private final class_630 field_3635;
	private float field_3636;

	public class_625(float f) {
		this.field_17138 = 256;
		this.field_17139 = 256;
		float g = -16.0F;
		this.field_3630 = new class_630(this, "head");
		this.field_3630.method_2848("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, f, 176, 44);
		this.field_3630.method_2848("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, f, 112, 30);
		this.field_3630.field_3666 = true;
		this.field_3630.method_2848("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.field_3630.method_2848("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.field_3630.field_3666 = false;
		this.field_3630.method_2848("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.field_3630.method_2848("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.field_3631 = new class_630(this, "jaw");
		this.field_3631.method_2851(0.0F, 4.0F, -8.0F);
		this.field_3631.method_2848("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, f, 176, 65);
		this.field_3630.method_2845(this.field_3631);
		this.field_3637 = new class_630(this, "neck");
		this.field_3637.method_2848("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, f, 192, 104);
		this.field_3637.method_2848("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, f, 48, 0);
		this.field_3627 = new class_630(this, "body");
		this.field_3627.method_2851(0.0F, 4.0F, 8.0F);
		this.field_3627.method_2848("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, f, 0, 0);
		this.field_3627.method_2848("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, f, 220, 53);
		this.field_3627.method_2848("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, f, 220, 53);
		this.field_3627.method_2848("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, f, 220, 53);
		this.field_3629 = new class_630(this, "wing");
		this.field_3629.method_2851(-12.0F, 5.0F, 2.0F);
		this.field_3629.method_2848("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, f, 112, 88);
		this.field_3629.method_2848("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, f, -56, 88);
		this.field_3635 = new class_630(this, "wingtip");
		this.field_3635.method_2851(-56.0F, 0.0F, 0.0F);
		this.field_3635.method_2848("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, f, 112, 136);
		this.field_3635.method_2848("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, f, -56, 144);
		this.field_3629.method_2845(this.field_3635);
		this.field_3632 = new class_630(this, "frontleg");
		this.field_3632.method_2851(-12.0F, 20.0F, 2.0F);
		this.field_3632.method_2848("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, f, 112, 104);
		this.field_3634 = new class_630(this, "frontlegtip");
		this.field_3634.method_2851(0.0F, 20.0F, -1.0F);
		this.field_3634.method_2848("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, f, 226, 138);
		this.field_3632.method_2845(this.field_3634);
		this.field_3625 = new class_630(this, "frontfoot");
		this.field_3625.method_2851(0.0F, 23.0F, 0.0F);
		this.field_3625.method_2848("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, f, 144, 104);
		this.field_3634.method_2845(this.field_3625);
		this.field_3633 = new class_630(this, "rearleg");
		this.field_3633.method_2851(-16.0F, 16.0F, 42.0F);
		this.field_3633.method_2848("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, f, 0, 0);
		this.field_3626 = new class_630(this, "rearlegtip");
		this.field_3626.method_2851(0.0F, 32.0F, -4.0F);
		this.field_3626.method_2848("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, f, 196, 0);
		this.field_3633.method_2845(this.field_3626);
		this.field_3628 = new class_630(this, "rearfoot");
		this.field_3628.method_2851(0.0F, 31.0F, 4.0F);
		this.field_3628.method_2848("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, f, 112, 0);
		this.field_3626.method_2845(this.field_3628);
	}

	public void method_17136(class_1510 arg, float f, float g, float h) {
		this.field_3636 = h;
	}

	public void method_17137(class_1510 arg, float f, float g, float h, float i, float j, float k) {
		GlStateManager.pushMatrix();
		float l = class_3532.method_16439(this.field_3636, arg.field_7019, arg.field_7030);
		this.field_3631.field_3654 = (float)(Math.sin((double)(l * (float) (Math.PI * 2))) + 1.0) * 0.2F;
		float m = (float)(Math.sin((double)(l * (float) (Math.PI * 2) - 1.0F)) + 1.0);
		m = (m * m + m * 2.0F) * 0.05F;
		GlStateManager.translatef(0.0F, m - 2.0F, -3.0F);
		GlStateManager.rotatef(m * 2.0F, 1.0F, 0.0F, 0.0F);
		float n = 0.0F;
		float o = 20.0F;
		float p = -12.0F;
		float q = 1.5F;
		double[] ds = arg.method_6817(6, this.field_3636);
		float r = this.method_2841(arg.method_6817(5, this.field_3636)[0] - arg.method_6817(10, this.field_3636)[0]);
		float s = this.method_2841(arg.method_6817(5, this.field_3636)[0] + (double)(r / 2.0F));
		float t = l * (float) (Math.PI * 2);

		for (int u = 0; u < 5; u++) {
			double[] es = arg.method_6817(5 - u, this.field_3636);
			float v = (float)Math.cos((double)((float)u * 0.45F + t)) * 0.15F;
			this.field_3637.field_3675 = this.method_2841(es[0] - ds[0]) * (float) (Math.PI / 180.0) * 1.5F;
			this.field_3637.field_3654 = v + arg.method_6823(u, ds, es) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			this.field_3637.field_3674 = -this.method_2841(es[0] - (double)s) * (float) (Math.PI / 180.0) * 1.5F;
			this.field_3637.field_3656 = o;
			this.field_3637.field_3655 = p;
			this.field_3637.field_3657 = n;
			o = (float)((double)o + Math.sin((double)this.field_3637.field_3654) * 10.0);
			p = (float)((double)p - Math.cos((double)this.field_3637.field_3675) * Math.cos((double)this.field_3637.field_3654) * 10.0);
			n = (float)((double)n - Math.sin((double)this.field_3637.field_3675) * Math.cos((double)this.field_3637.field_3654) * 10.0);
			this.field_3637.method_2846(k);
		}

		this.field_3630.field_3656 = o;
		this.field_3630.field_3655 = p;
		this.field_3630.field_3657 = n;
		double[] fs = arg.method_6817(0, this.field_3636);
		this.field_3630.field_3675 = this.method_2841(fs[0] - ds[0]) * (float) (Math.PI / 180.0);
		this.field_3630.field_3654 = this.method_2841((double)arg.method_6823(6, ds, fs)) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
		this.field_3630.field_3674 = -this.method_2841(fs[0] - (double)s) * (float) (Math.PI / 180.0);
		this.field_3630.method_2846(k);
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-r * 1.5F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translatef(0.0F, -1.0F, 0.0F);
		this.field_3627.field_3674 = 0.0F;
		this.field_3627.method_2846(k);

		for (int w = 0; w < 2; w++) {
			GlStateManager.enableCull();
			float v = l * (float) (Math.PI * 2);
			this.field_3629.field_3654 = 0.125F - (float)Math.cos((double)v) * 0.2F;
			this.field_3629.field_3675 = 0.25F;
			this.field_3629.field_3674 = (float)(Math.sin((double)v) + 0.125) * 0.8F;
			this.field_3635.field_3674 = -((float)(Math.sin((double)(v + 2.0F)) + 0.5)) * 0.75F;
			this.field_3633.field_3654 = 1.0F + m * 0.1F;
			this.field_3626.field_3654 = 0.5F + m * 0.1F;
			this.field_3628.field_3654 = 0.75F + m * 0.1F;
			this.field_3632.field_3654 = 1.3F + m * 0.1F;
			this.field_3634.field_3654 = -0.5F - m * 0.1F;
			this.field_3625.field_3654 = 0.75F + m * 0.1F;
			this.field_3629.method_2846(k);
			this.field_3632.method_2846(k);
			this.field_3633.method_2846(k);
			GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
			if (w == 0) {
				GlStateManager.cullFace(GlStateManager.class_1024.field_5068);
			}
		}

		GlStateManager.popMatrix();
		GlStateManager.cullFace(GlStateManager.class_1024.field_5070);
		GlStateManager.disableCull();
		float x = -((float)Math.sin((double)(l * (float) (Math.PI * 2)))) * 0.0F;
		t = l * (float) (Math.PI * 2);
		o = 10.0F;
		p = 60.0F;
		n = 0.0F;
		ds = arg.method_6817(11, this.field_3636);

		for (int y = 0; y < 12; y++) {
			fs = arg.method_6817(12 + y, this.field_3636);
			x = (float)((double)x + Math.sin((double)((float)y * 0.45F + t)) * 0.05F);
			this.field_3637.field_3675 = (this.method_2841(fs[0] - ds[0]) * 1.5F + 180.0F) * (float) (Math.PI / 180.0);
			this.field_3637.field_3654 = x + (float)(fs[1] - ds[1]) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			this.field_3637.field_3674 = this.method_2841(fs[0] - (double)s) * (float) (Math.PI / 180.0) * 1.5F;
			this.field_3637.field_3656 = o;
			this.field_3637.field_3655 = p;
			this.field_3637.field_3657 = n;
			o = (float)((double)o + Math.sin((double)this.field_3637.field_3654) * 10.0);
			p = (float)((double)p - Math.cos((double)this.field_3637.field_3675) * Math.cos((double)this.field_3637.field_3654) * 10.0);
			n = (float)((double)n - Math.sin((double)this.field_3637.field_3675) * Math.cos((double)this.field_3637.field_3654) * 10.0);
			this.field_3637.method_2846(k);
		}

		GlStateManager.popMatrix();
	}

	private float method_2841(double d) {
		while (d >= 180.0) {
			d -= 360.0;
		}

		while (d < -180.0) {
			d += 360.0;
		}

		return (float)d;
	}
}
