package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;

@Environment(EnvType.CLIENT)
public class WorldBorderRendering {
	private static final Identifier FORCEFIELD = Identifier.ofVanilla("textures/misc/forcefield.png");

	public void render(WorldBorder border, Vec3d vec3d, double d, double e) {
		double f = border.getBoundWest();
		double g = border.getBoundEast();
		double h = border.getBoundNorth();
		double i = border.getBoundSouth();
		if (!(vec3d.x < g - d) || !(vec3d.x > f + d) || !(vec3d.z < i - d) || !(vec3d.z > h + d)) {
			double j = 1.0 - border.getDistanceInsideBorder(vec3d.x, vec3d.z) / d;
			j = Math.pow(j, 4.0);
			j = MathHelper.clamp(j, 0.0, 1.0);
			double k = vec3d.x;
			double l = vec3d.z;
			float m = (float)e;
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
			RenderSystem.setShaderTexture(0, FORCEFIELD);
			RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
			int n = border.getStage().getColor();
			float o = (float)ColorHelper.getRed(n) / 255.0F;
			float p = (float)ColorHelper.getGreen(n) / 255.0F;
			float q = (float)ColorHelper.getBlue(n) / 255.0F;
			RenderSystem.setShaderColor(o, p, q, (float)j);
			RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
			RenderSystem.polygonOffset(-3.0F, -3.0F);
			RenderSystem.enablePolygonOffset();
			RenderSystem.disableCull();
			float r = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0F;
			float s = (float)(-MathHelper.fractionalPart(vec3d.y * 0.5));
			float t = s + m;
			BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			double u = Math.max((double)MathHelper.floor(l - d), h);
			double v = Math.min((double)MathHelper.ceil(l + d), i);
			float w = (float)(MathHelper.floor(u) & 1) * 0.5F;
			if (k > g - d) {
				float x = w;

				for (double y = u; y < v; x += 0.5F) {
					double z = Math.min(1.0, v - y);
					float aa = (float)z * 0.5F;
					bufferBuilder.vertex((float)(g - k), -m, (float)(y - l)).texture(r - x, r + t);
					bufferBuilder.vertex((float)(g - k), -m, (float)(y + z - l)).texture(r - (aa + x), r + t);
					bufferBuilder.vertex((float)(g - k), m, (float)(y + z - l)).texture(r - (aa + x), r + s);
					bufferBuilder.vertex((float)(g - k), m, (float)(y - l)).texture(r - x, r + s);
					y++;
				}
			}

			if (k < f + d) {
				float x = w;

				for (double y = u; y < v; x += 0.5F) {
					double z = Math.min(1.0, v - y);
					float aa = (float)z * 0.5F;
					bufferBuilder.vertex((float)(f - k), -m, (float)(y - l)).texture(r + x, r + t);
					bufferBuilder.vertex((float)(f - k), -m, (float)(y + z - l)).texture(r + aa + x, r + t);
					bufferBuilder.vertex((float)(f - k), m, (float)(y + z - l)).texture(r + aa + x, r + s);
					bufferBuilder.vertex((float)(f - k), m, (float)(y - l)).texture(r + x, r + s);
					y++;
				}
			}

			u = Math.max((double)MathHelper.floor(k - d), f);
			v = Math.min((double)MathHelper.ceil(k + d), g);
			w = (float)(MathHelper.floor(u) & 1) * 0.5F;
			if (l > i - d) {
				float x = w;

				for (double y = u; y < v; x += 0.5F) {
					double z = Math.min(1.0, v - y);
					float aa = (float)z * 0.5F;
					bufferBuilder.vertex((float)(y - k), -m, (float)(i - l)).texture(r + x, r + t);
					bufferBuilder.vertex((float)(y + z - k), -m, (float)(i - l)).texture(r + aa + x, r + t);
					bufferBuilder.vertex((float)(y + z - k), m, (float)(i - l)).texture(r + aa + x, r + s);
					bufferBuilder.vertex((float)(y - k), m, (float)(i - l)).texture(r + x, r + s);
					y++;
				}
			}

			if (l < h + d) {
				float x = w;

				for (double y = u; y < v; x += 0.5F) {
					double z = Math.min(1.0, v - y);
					float aa = (float)z * 0.5F;
					bufferBuilder.vertex((float)(y - k), -m, (float)(h - l)).texture(r - x, r + t);
					bufferBuilder.vertex((float)(y + z - k), -m, (float)(h - l)).texture(r - (aa + x), r + t);
					bufferBuilder.vertex((float)(y + z - k), m, (float)(h - l)).texture(r - (aa + x), r + s);
					bufferBuilder.vertex((float)(y - k), m, (float)(h - l)).texture(r - x, r + s);
					y++;
				}
			}

			BuiltBuffer builtBuffer = bufferBuilder.endNullable();
			if (builtBuffer != null) {
				BufferRenderer.drawWithGlobalProgram(builtBuffer);
			}

			RenderSystem.enableCull();
			RenderSystem.polygonOffset(0.0F, 0.0F);
			RenderSystem.disablePolygonOffset();
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.depthMask(true);
		}
	}
}
