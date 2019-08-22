package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class GlMatrixFrustum extends Frustum {
	private static final GlMatrixFrustum INSTANCE = new GlMatrixFrustum();
	private final FloatBuffer projectionMatrixBuffer = GlAllocationUtils.allocateFloatBuffer(16);
	private final FloatBuffer modelviewMatrixBuffer = GlAllocationUtils.allocateFloatBuffer(16);
	private final FloatBuffer field_4493 = GlAllocationUtils.allocateFloatBuffer(16);

	public static Frustum get() {
		INSTANCE.loadFromGlMatrices();
		return INSTANCE;
	}

	private void normalize(float[] fs) {
		float f = MathHelper.sqrt(fs[0] * fs[0] + fs[1] * fs[1] + fs[2] * fs[2]);
		fs[0] /= f;
		fs[1] /= f;
		fs[2] /= f;
		fs[3] /= f;
	}

	public void loadFromGlMatrices() {
		this.projectionMatrixBuffer.clear();
		this.modelviewMatrixBuffer.clear();
		this.field_4493.clear();
		RenderSystem.getMatrix(2983, this.projectionMatrixBuffer);
		RenderSystem.getMatrix(2982, this.modelviewMatrixBuffer);
		float[] fs = this.projectionMatrix;
		float[] gs = this.modelViewMatrix;
		this.projectionMatrixBuffer.flip().limit(16);
		this.projectionMatrixBuffer.get(fs);
		this.modelviewMatrixBuffer.flip().limit(16);
		this.modelviewMatrixBuffer.get(gs);
		this.mvpMatrix[0] = gs[0] * fs[0] + gs[1] * fs[4] + gs[2] * fs[8] + gs[3] * fs[12];
		this.mvpMatrix[1] = gs[0] * fs[1] + gs[1] * fs[5] + gs[2] * fs[9] + gs[3] * fs[13];
		this.mvpMatrix[2] = gs[0] * fs[2] + gs[1] * fs[6] + gs[2] * fs[10] + gs[3] * fs[14];
		this.mvpMatrix[3] = gs[0] * fs[3] + gs[1] * fs[7] + gs[2] * fs[11] + gs[3] * fs[15];
		this.mvpMatrix[4] = gs[4] * fs[0] + gs[5] * fs[4] + gs[6] * fs[8] + gs[7] * fs[12];
		this.mvpMatrix[5] = gs[4] * fs[1] + gs[5] * fs[5] + gs[6] * fs[9] + gs[7] * fs[13];
		this.mvpMatrix[6] = gs[4] * fs[2] + gs[5] * fs[6] + gs[6] * fs[10] + gs[7] * fs[14];
		this.mvpMatrix[7] = gs[4] * fs[3] + gs[5] * fs[7] + gs[6] * fs[11] + gs[7] * fs[15];
		this.mvpMatrix[8] = gs[8] * fs[0] + gs[9] * fs[4] + gs[10] * fs[8] + gs[11] * fs[12];
		this.mvpMatrix[9] = gs[8] * fs[1] + gs[9] * fs[5] + gs[10] * fs[9] + gs[11] * fs[13];
		this.mvpMatrix[10] = gs[8] * fs[2] + gs[9] * fs[6] + gs[10] * fs[10] + gs[11] * fs[14];
		this.mvpMatrix[11] = gs[8] * fs[3] + gs[9] * fs[7] + gs[10] * fs[11] + gs[11] * fs[15];
		this.mvpMatrix[12] = gs[12] * fs[0] + gs[13] * fs[4] + gs[14] * fs[8] + gs[15] * fs[12];
		this.mvpMatrix[13] = gs[12] * fs[1] + gs[13] * fs[5] + gs[14] * fs[9] + gs[15] * fs[13];
		this.mvpMatrix[14] = gs[12] * fs[2] + gs[13] * fs[6] + gs[14] * fs[10] + gs[15] * fs[14];
		this.mvpMatrix[15] = gs[12] * fs[3] + gs[13] * fs[7] + gs[14] * fs[11] + gs[15] * fs[15];
		float[] hs = this.sides[0];
		hs[0] = this.mvpMatrix[3] - this.mvpMatrix[0];
		hs[1] = this.mvpMatrix[7] - this.mvpMatrix[4];
		hs[2] = this.mvpMatrix[11] - this.mvpMatrix[8];
		hs[3] = this.mvpMatrix[15] - this.mvpMatrix[12];
		this.normalize(hs);
		float[] is = this.sides[1];
		is[0] = this.mvpMatrix[3] + this.mvpMatrix[0];
		is[1] = this.mvpMatrix[7] + this.mvpMatrix[4];
		is[2] = this.mvpMatrix[11] + this.mvpMatrix[8];
		is[3] = this.mvpMatrix[15] + this.mvpMatrix[12];
		this.normalize(is);
		float[] js = this.sides[2];
		js[0] = this.mvpMatrix[3] + this.mvpMatrix[1];
		js[1] = this.mvpMatrix[7] + this.mvpMatrix[5];
		js[2] = this.mvpMatrix[11] + this.mvpMatrix[9];
		js[3] = this.mvpMatrix[15] + this.mvpMatrix[13];
		this.normalize(js);
		float[] ks = this.sides[3];
		ks[0] = this.mvpMatrix[3] - this.mvpMatrix[1];
		ks[1] = this.mvpMatrix[7] - this.mvpMatrix[5];
		ks[2] = this.mvpMatrix[11] - this.mvpMatrix[9];
		ks[3] = this.mvpMatrix[15] - this.mvpMatrix[13];
		this.normalize(ks);
		float[] ls = this.sides[4];
		ls[0] = this.mvpMatrix[3] - this.mvpMatrix[2];
		ls[1] = this.mvpMatrix[7] - this.mvpMatrix[6];
		ls[2] = this.mvpMatrix[11] - this.mvpMatrix[10];
		ls[3] = this.mvpMatrix[15] - this.mvpMatrix[14];
		this.normalize(ls);
		float[] ms = this.sides[5];
		ms[0] = this.mvpMatrix[3] + this.mvpMatrix[2];
		ms[1] = this.mvpMatrix[7] + this.mvpMatrix[6];
		ms[2] = this.mvpMatrix[11] + this.mvpMatrix[10];
		ms[3] = this.mvpMatrix[15] + this.mvpMatrix[14];
		this.normalize(ms);
	}
}
