package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BeaconBlockEntityRenderer extends BlockEntityRenderer<BeaconBlockEntity> {
	private static final Identifier BEAM_TEX = new Identifier("textures/entity/beacon_beam.png");

	public void method_3541(BeaconBlockEntity beaconBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer) {
		this.render(d, e, f, (double)g, beaconBlockEntity.getBeamSegments(), beaconBlockEntity.getWorld().getTime());
	}

	private void render(double d, double e, double f, double g, List<BeaconBlockEntity.BeamSegment> list, long l) {
		RenderSystem.defaultAlphaFunc();
		this.bindTexture(BEAM_TEX);
		RenderSystem.disableFog();
		int i = 0;

		for (int j = 0; j < list.size(); j++) {
			BeaconBlockEntity.BeamSegment beamSegment = (BeaconBlockEntity.BeamSegment)list.get(j);
			renderBeaconLightBeam(d, e, f, g, l, i, j == list.size() - 1 ? 1024 : beamSegment.getHeight(), beamSegment.getColor());
			i += beamSegment.getHeight();
		}

		RenderSystem.enableFog();
	}

	private static void renderBeaconLightBeam(double d, double e, double f, double g, long l, int i, int j, float[] fs) {
		renderLightBeam(d, e, f, g, 1.0, l, i, j, fs, 0.2, 0.25);
	}

	public static void renderLightBeam(double d, double e, double f, double g, double h, long l, int i, int j, float[] fs, double k, double m) {
		int n = i + j;
		RenderSystem.texParameter(3553, 10242, 10497);
		RenderSystem.texParameter(3553, 10243, 10497);
		RenderSystem.disableLighting();
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.blendFuncSeparate(
			GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
		);
		RenderSystem.pushMatrix();
		RenderSystem.translated(d + 0.5, e, f + 0.5);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		double o = (double)Math.floorMod(l, 40L) + g;
		double p = j < 0 ? o : -o;
		double q = MathHelper.fractionalPart(p * 0.2 - (double)MathHelper.floor(p * 0.1));
		float r = fs[0];
		float s = fs[1];
		float t = fs[2];
		RenderSystem.pushMatrix();
		RenderSystem.rotated(o * 2.25 - 45.0, 0.0, 1.0, 0.0);
		double u = 0.0;
		double x = 0.0;
		double y = -k;
		double z = 0.0;
		double aa = 0.0;
		double ab = -k;
		double ac = 0.0;
		double ad = 1.0;
		double ae = -1.0 + q;
		double af = (double)j * h * (0.5 / k) + ae;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		method_22741(bufferBuilder, r, s, t, 1.0F, i, n, 0.0, k, k, 0.0, y, 0.0, 0.0, ab, 0.0, 1.0, af, ae);
		tessellator.draw();
		RenderSystem.popMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		u = -m;
		double v = -m;
		x = -m;
		y = -m;
		ac = 0.0;
		ad = 1.0;
		ae = -1.0 + q;
		af = (double)j * h + ae;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		method_22741(bufferBuilder, r, s, t, 0.125F, i, n, u, v, m, x, y, m, m, m, 0.0, 1.0, af, ae);
		tessellator.draw();
		RenderSystem.popMatrix();
		RenderSystem.enableLighting();
		RenderSystem.enableTexture();
		RenderSystem.depthMask(true);
	}

	private static void method_22741(
		BufferBuilder bufferBuilder,
		float f,
		float g,
		float h,
		float i,
		int j,
		int k,
		double d,
		double e,
		double l,
		double m,
		double n,
		double o,
		double p,
		double q,
		double r,
		double s,
		double t,
		double u
	) {
		method_22740(bufferBuilder, f, g, h, i, j, k, d, e, l, m, r, s, t, u);
		method_22740(bufferBuilder, f, g, h, i, j, k, p, q, n, o, r, s, t, u);
		method_22740(bufferBuilder, f, g, h, i, j, k, l, m, p, q, r, s, t, u);
		method_22740(bufferBuilder, f, g, h, i, j, k, n, o, d, e, r, s, t, u);
	}

	private static void method_22740(
		BufferBuilder bufferBuilder, float f, float g, float h, float i, int j, int k, double d, double e, double l, double m, double n, double o, double p, double q
	) {
		bufferBuilder.vertex(d, (double)k, e).texture(o, p).color(f, g, h, i).next();
		bufferBuilder.vertex(d, (double)j, e).texture(o, q).color(f, g, h, i).next();
		bufferBuilder.vertex(l, (double)j, m).texture(n, q).color(f, g, h, i).next();
		bufferBuilder.vertex(l, (double)k, m).texture(n, p).color(f, g, h, i).next();
	}

	public boolean method_3542(BeaconBlockEntity beaconBlockEntity) {
		return true;
	}
}
