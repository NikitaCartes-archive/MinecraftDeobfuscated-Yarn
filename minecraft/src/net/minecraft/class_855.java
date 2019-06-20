package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_855 extends class_857 {
	private static final class_855 field_4495 = new class_855();
	private final FloatBuffer field_4496 = class_311.method_1597(16);
	private final FloatBuffer field_4494 = class_311.method_1597(16);
	private final FloatBuffer field_4493 = class_311.method_1597(16);

	public static class_857 method_3696() {
		field_4495.method_3698();
		return field_4495;
	}

	private void method_3697(float[] fs) {
		float f = class_3532.method_15355(fs[0] * fs[0] + fs[1] * fs[1] + fs[2] * fs[2]);
		fs[0] /= f;
		fs[1] /= f;
		fs[2] /= f;
		fs[3] /= f;
	}

	public void method_3698() {
		this.field_4496.clear();
		this.field_4494.clear();
		this.field_4493.clear();
		GlStateManager.getMatrix(2983, this.field_4496);
		GlStateManager.getMatrix(2982, this.field_4494);
		float[] fs = this.field_4498;
		float[] gs = this.field_4500;
		this.field_4496.flip().limit(16);
		this.field_4496.get(fs);
		this.field_4494.flip().limit(16);
		this.field_4494.get(gs);
		this.field_4499[0] = gs[0] * fs[0] + gs[1] * fs[4] + gs[2] * fs[8] + gs[3] * fs[12];
		this.field_4499[1] = gs[0] * fs[1] + gs[1] * fs[5] + gs[2] * fs[9] + gs[3] * fs[13];
		this.field_4499[2] = gs[0] * fs[2] + gs[1] * fs[6] + gs[2] * fs[10] + gs[3] * fs[14];
		this.field_4499[3] = gs[0] * fs[3] + gs[1] * fs[7] + gs[2] * fs[11] + gs[3] * fs[15];
		this.field_4499[4] = gs[4] * fs[0] + gs[5] * fs[4] + gs[6] * fs[8] + gs[7] * fs[12];
		this.field_4499[5] = gs[4] * fs[1] + gs[5] * fs[5] + gs[6] * fs[9] + gs[7] * fs[13];
		this.field_4499[6] = gs[4] * fs[2] + gs[5] * fs[6] + gs[6] * fs[10] + gs[7] * fs[14];
		this.field_4499[7] = gs[4] * fs[3] + gs[5] * fs[7] + gs[6] * fs[11] + gs[7] * fs[15];
		this.field_4499[8] = gs[8] * fs[0] + gs[9] * fs[4] + gs[10] * fs[8] + gs[11] * fs[12];
		this.field_4499[9] = gs[8] * fs[1] + gs[9] * fs[5] + gs[10] * fs[9] + gs[11] * fs[13];
		this.field_4499[10] = gs[8] * fs[2] + gs[9] * fs[6] + gs[10] * fs[10] + gs[11] * fs[14];
		this.field_4499[11] = gs[8] * fs[3] + gs[9] * fs[7] + gs[10] * fs[11] + gs[11] * fs[15];
		this.field_4499[12] = gs[12] * fs[0] + gs[13] * fs[4] + gs[14] * fs[8] + gs[15] * fs[12];
		this.field_4499[13] = gs[12] * fs[1] + gs[13] * fs[5] + gs[14] * fs[9] + gs[15] * fs[13];
		this.field_4499[14] = gs[12] * fs[2] + gs[13] * fs[6] + gs[14] * fs[10] + gs[15] * fs[14];
		this.field_4499[15] = gs[12] * fs[3] + gs[13] * fs[7] + gs[14] * fs[11] + gs[15] * fs[15];
		float[] hs = this.field_4497[0];
		hs[0] = this.field_4499[3] - this.field_4499[0];
		hs[1] = this.field_4499[7] - this.field_4499[4];
		hs[2] = this.field_4499[11] - this.field_4499[8];
		hs[3] = this.field_4499[15] - this.field_4499[12];
		this.method_3697(hs);
		float[] is = this.field_4497[1];
		is[0] = this.field_4499[3] + this.field_4499[0];
		is[1] = this.field_4499[7] + this.field_4499[4];
		is[2] = this.field_4499[11] + this.field_4499[8];
		is[3] = this.field_4499[15] + this.field_4499[12];
		this.method_3697(is);
		float[] js = this.field_4497[2];
		js[0] = this.field_4499[3] + this.field_4499[1];
		js[1] = this.field_4499[7] + this.field_4499[5];
		js[2] = this.field_4499[11] + this.field_4499[9];
		js[3] = this.field_4499[15] + this.field_4499[13];
		this.method_3697(js);
		float[] ks = this.field_4497[3];
		ks[0] = this.field_4499[3] - this.field_4499[1];
		ks[1] = this.field_4499[7] - this.field_4499[5];
		ks[2] = this.field_4499[11] - this.field_4499[9];
		ks[3] = this.field_4499[15] - this.field_4499[13];
		this.method_3697(ks);
		float[] ls = this.field_4497[4];
		ls[0] = this.field_4499[3] - this.field_4499[2];
		ls[1] = this.field_4499[7] - this.field_4499[6];
		ls[2] = this.field_4499[11] - this.field_4499[10];
		ls[3] = this.field_4499[15] - this.field_4499[14];
		this.method_3697(ls);
		float[] ms = this.field_4497[5];
		ms[0] = this.field_4499[3] + this.field_4499[2];
		ms[1] = this.field_4499[7] + this.field_4499[6];
		ms[2] = this.field_4499[11] + this.field_4499[10];
		ms[3] = this.field_4499[15] + this.field_4499[14];
		this.method_3697(ms);
	}
}
