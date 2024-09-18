package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class SkyRendering implements AutoCloseable {
	private static final Identifier SUN_TEXTURE = Identifier.ofVanilla("textures/environment/sun.png");
	private static final Identifier MOON_PHASES_TEXTURE = Identifier.ofVanilla("textures/environment/moon_phases.png");
	private static final Identifier END_SKY_TEXTURE = Identifier.ofVanilla("textures/environment/end_sky.png");
	private static final float field_53144 = 512.0F;
	private final VertexBuffer starBuffer = this.createStarBuffer();
	private final VertexBuffer skyBuffer = this.createSkyBuffer();
	private final VertexBuffer darkSkyBuffer = this.createDarkSkyBuffer();

	private VertexBuffer createStarBuffer() {
		VertexBuffer vertexBuffer = new VertexBuffer(GlUsage.STATIC_WRITE);
		vertexBuffer.bind();
		vertexBuffer.upload(this.tessellateStars(Tessellator.getInstance()));
		VertexBuffer.unbind();
		return vertexBuffer;
	}

	private VertexBuffer createSkyBuffer() {
		VertexBuffer vertexBuffer = new VertexBuffer(GlUsage.STATIC_WRITE);
		vertexBuffer.bind();
		vertexBuffer.upload(this.tessellateSky(Tessellator.getInstance(), 16.0F));
		VertexBuffer.unbind();
		return vertexBuffer;
	}

	private VertexBuffer createDarkSkyBuffer() {
		VertexBuffer vertexBuffer = new VertexBuffer(GlUsage.STATIC_WRITE);
		vertexBuffer.bind();
		vertexBuffer.upload(this.tessellateSky(Tessellator.getInstance(), -16.0F));
		VertexBuffer.unbind();
		return vertexBuffer;
	}

	private BuiltBuffer tessellateStars(Tessellator tesselator) {
		Random random = Random.create(10842L);
		int i = 1500;
		float f = 100.0F;
		BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		for (int j = 0; j < 1500; j++) {
			float g = random.nextFloat() * 2.0F - 1.0F;
			float h = random.nextFloat() * 2.0F - 1.0F;
			float k = random.nextFloat() * 2.0F - 1.0F;
			float l = 0.15F + random.nextFloat() * 0.1F;
			float m = MathHelper.magnitude(g, h, k);
			if (!(m <= 0.010000001F) && !(m >= 1.0F)) {
				Vector3f vector3f = new Vector3f(g, h, k).normalize(100.0F);
				float n = (float)(random.nextDouble() * (float) Math.PI * 2.0);
				Matrix3f matrix3f = new Matrix3f().rotateTowards(new Vector3f(vector3f).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-n);
				bufferBuilder.vertex(new Vector3f(l, -l, 0.0F).mul(matrix3f).add(vector3f));
				bufferBuilder.vertex(new Vector3f(l, l, 0.0F).mul(matrix3f).add(vector3f));
				bufferBuilder.vertex(new Vector3f(-l, l, 0.0F).mul(matrix3f).add(vector3f));
				bufferBuilder.vertex(new Vector3f(-l, -l, 0.0F).mul(matrix3f).add(vector3f));
			}
		}

		return bufferBuilder.end();
	}

	private BuiltBuffer tessellateSky(Tessellator tesselator, float height) {
		float f = Math.signum(height) * 512.0F;
		BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
		bufferBuilder.vertex(0.0F, height, 0.0F);

		for (int i = -180; i <= 180; i += 45) {
			bufferBuilder.vertex(f * MathHelper.cos((float)i * (float) (Math.PI / 180.0)), height, 512.0F * MathHelper.sin((float)i * (float) (Math.PI / 180.0)));
		}

		return bufferBuilder.end();
	}

	public void renderSky(float red, float green, float blue) {
		RenderSystem.depthMask(false);
		RenderSystem.setShader(ShaderProgramKeys.POSITION);
		RenderSystem.setShaderColor(red, green, blue, 1.0F);
		this.skyBuffer.bind();
		this.skyBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
		VertexBuffer.unbind();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.depthMask(true);
	}

	public void renderSkyDark(MatrixStack matrices) {
		RenderSystem.depthMask(false);
		RenderSystem.setShader(ShaderProgramKeys.POSITION);
		RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
		matrices.push();
		matrices.translate(0.0F, 12.0F, 0.0F);
		this.darkSkyBuffer.bind();
		this.darkSkyBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
		VertexBuffer.unbind();
		matrices.pop();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.depthMask(true);
	}

	public void renderCelestialBodies(MatrixStack matrices, Tessellator tesselator, float rot, int phase, float alpha, float starBrightness, Fog fog) {
		matrices.push();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rot * 360.0F));
		this.renderSun(alpha, tesselator, matrices);
		this.renderMoon(phase, alpha, tesselator, matrices);
		if (starBrightness > 0.0F) {
			this.renderStars(fog, starBrightness, matrices);
		}

		matrices.pop();
	}

	private void renderSun(float alpha, Tessellator tesselator, MatrixStack matrices) {
		float f = 30.0F;
		float g = 100.0F;
		BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		RenderSystem.depthMask(false);
		RenderSystem.overlayBlendFunc();
		RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.setShaderTexture(0, SUN_TEXTURE);
		RenderSystem.enableBlend();
		bufferBuilder.vertex(matrix4f, -30.0F, 100.0F, -30.0F).texture(0.0F, 0.0F);
		bufferBuilder.vertex(matrix4f, 30.0F, 100.0F, -30.0F).texture(1.0F, 0.0F);
		bufferBuilder.vertex(matrix4f, 30.0F, 100.0F, 30.0F).texture(1.0F, 1.0F);
		bufferBuilder.vertex(matrix4f, -30.0F, 100.0F, 30.0F).texture(0.0F, 1.0F);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(true);
	}

	private void renderMoon(int phase, float alpha, Tessellator tesselator, MatrixStack matrices) {
		float f = 20.0F;
		int i = phase % 4;
		int j = phase / 4 % 2;
		float g = (float)(i + 0) / 4.0F;
		float h = (float)(j + 0) / 2.0F;
		float k = (float)(i + 1) / 4.0F;
		float l = (float)(j + 1) / 2.0F;
		float m = 100.0F;
		BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		RenderSystem.depthMask(false);
		RenderSystem.overlayBlendFunc();
		RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.setShaderTexture(0, MOON_PHASES_TEXTURE);
		RenderSystem.enableBlend();
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		bufferBuilder.vertex(matrix4f, -20.0F, -100.0F, 20.0F).texture(k, l);
		bufferBuilder.vertex(matrix4f, 20.0F, -100.0F, 20.0F).texture(g, l);
		bufferBuilder.vertex(matrix4f, 20.0F, -100.0F, -20.0F).texture(g, h);
		bufferBuilder.vertex(matrix4f, -20.0F, -100.0F, -20.0F).texture(k, h);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(true);
	}

	private void renderStars(Fog fog, float color, MatrixStack matrices) {
		Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
		matrix4fStack.pushMatrix();
		matrix4fStack.mul(matrices.peek().getPositionMatrix());
		RenderSystem.depthMask(false);
		RenderSystem.overlayBlendFunc();
		RenderSystem.setShader(ShaderProgramKeys.POSITION);
		RenderSystem.setShaderColor(color, color, color, color);
		RenderSystem.enableBlend();
		RenderSystem.setShaderFog(Fog.DUMMY);
		this.starBuffer.bind();
		this.starBuffer.draw(matrix4fStack, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
		VertexBuffer.unbind();
		RenderSystem.setShaderFog(fog);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(true);
		matrix4fStack.popMatrix();
	}

	public void renderGlowingSky(MatrixStack matrices, Tessellator tesselator, float angleRadians, int color) {
		RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		matrices.push();
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
		float f = MathHelper.sin(angleRadians) < 0.0F ? 180.0F : 0.0F;
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
		float g = ColorHelper.floatFromChannel(ColorHelper.getAlpha(color));
		bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(color);
		int i = ColorHelper.zeroAlpha(color);
		int j = 16;

		for (int k = 0; k <= 16; k++) {
			float h = (float)k * (float) (Math.PI * 2) / 16.0F;
			float l = MathHelper.sin(h);
			float m = MathHelper.cos(h);
			bufferBuilder.vertex(matrix4f, l * 120.0F, m * 120.0F, -m * 40.0F * g).color(i);
		}

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		matrices.pop();
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
	}

	public void renderEndSky(MatrixStack matrices) {
		RenderSystem.enableBlend();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
		RenderSystem.setShaderTexture(0, END_SKY_TEXTURE);
		Tessellator tessellator = Tessellator.getInstance();

		for (int i = 0; i < 6; i++) {
			matrices.push();
			if (i == 1) {
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
			}

			if (i == 2) {
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
			}

			if (i == 3) {
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
			}

			if (i == 4) {
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
			}

			if (i == 5) {
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
			}

			Matrix4f matrix4f = matrices.peek().getPositionMatrix();
			BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(-14145496);
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(-14145496);
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(-14145496);
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(-14145496);
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			matrices.pop();
		}

		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
	}

	public void close() {
		this.starBuffer.close();
		this.skyBuffer.close();
		this.darkSkyBuffer.close();
	}
}
