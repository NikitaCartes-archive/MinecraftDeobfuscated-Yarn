package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BeaconBlockEntityRenderer extends BlockEntityRenderer<BeaconBlockEntity> {
	private static final Identifier BEAM_TEX = new Identifier("textures/entity/beacon_beam.png");

	public void render(BeaconBlockEntity beaconBlockEntity, double d, double e, double f, float g, int i) {
		this.method_3543(d, e, f, (double)g, (double)beaconBlockEntity.method_10933(), beaconBlockEntity.method_10937(), beaconBlockEntity.getWorld().getTime());
	}

	private void method_3543(double d, double e, double f, double g, double h, List<BeaconBlockEntity.class_2581> list, long l) {
		GlStateManager.alphaFunc(516, 0.1F);
		this.bindTexture(BEAM_TEX);
		if (h > 0.0) {
			GlStateManager.disableFog();
			int i = 0;

			for (int j = 0; j < list.size(); j++) {
				BeaconBlockEntity.class_2581 lv = (BeaconBlockEntity.class_2581)list.get(j);
				method_3544(d, e, f, g, h, l, i, lv.method_10943(), lv.method_10944());
				i += lv.method_10943();
			}

			GlStateManager.enableFog();
		}
	}

	private static void method_3544(double d, double e, double f, double g, double h, long l, int i, int j, float[] fs) {
		method_3545(d, e, f, g, h, l, i, j, fs, 0.2, 0.25);
	}

	public static void method_3545(double d, double e, double f, double g, double h, long l, int i, int j, float[] fs, double k, double m) {
		int n = i + j;
		GlStateManager.texParameter(3553, 10242, 10497);
		GlStateManager.texParameter(3553, 10243, 10497);
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.SRC_ALPHA, GlStateManager.class_1027.ONE, GlStateManager.class_1033.ONE, GlStateManager.class_1027.ZERO
		);
		GlStateManager.pushMatrix();
		GlStateManager.translated(d + 0.5, e, f + 0.5);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		double o = (double)Math.floorMod(l, 40L) + g;
		double p = j < 0 ? o : -o;
		double q = MathHelper.fractionalPart(p * 0.2 - (double)MathHelper.floor(p * 0.1));
		float r = fs[0];
		float s = fs[1];
		float t = fs[2];
		GlStateManager.pushMatrix();
		GlStateManager.rotated(o * 2.25 - 45.0, 0.0, 1.0, 0.0);
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
		bufferBuilder.vertex(0.0, (double)n, k).texture(1.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)i, k).texture(1.0, ae).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(k, (double)i, 0.0).texture(0.0, ae).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(k, (double)n, 0.0).texture(0.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)n, ab).texture(1.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)i, ab).texture(1.0, ae).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(y, (double)i, 0.0).texture(0.0, ae).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(y, (double)n, 0.0).texture(0.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(k, (double)n, 0.0).texture(1.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(k, (double)i, 0.0).texture(1.0, ae).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)i, ab).texture(0.0, ae).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)n, ab).texture(0.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(y, (double)n, 0.0).texture(1.0, af).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(y, (double)i, 0.0).texture(1.0, ae).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)i, k).texture(0.0, ae).color(r, s, t, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)n, k).texture(0.0, af).color(r, s, t, 1.0F).next();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.SRC_ALPHA, GlStateManager.class_1027.ONE_MINUS_SRC_ALPHA, GlStateManager.class_1033.ONE, GlStateManager.class_1027.ZERO
		);
		GlStateManager.depthMask(false);
		u = -m;
		double v = -m;
		x = -m;
		y = -m;
		ac = 0.0;
		ad = 1.0;
		ae = -1.0 + q;
		af = (double)j * h + ae;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex(u, (double)n, v).texture(1.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(u, (double)i, v).texture(1.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(m, (double)i, x).texture(0.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(m, (double)n, x).texture(0.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(m, (double)n, m).texture(1.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(m, (double)i, m).texture(1.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(y, (double)i, m).texture(0.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(y, (double)n, m).texture(0.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(m, (double)n, x).texture(1.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(m, (double)i, x).texture(1.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(m, (double)i, m).texture(0.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(m, (double)n, m).texture(0.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(y, (double)n, m).texture(1.0, af).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(y, (double)i, m).texture(1.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(u, (double)i, v).texture(0.0, ae).color(r, s, t, 0.125F).next();
		bufferBuilder.vertex(u, (double)n, v).texture(0.0, af).color(r, s, t, 0.125F).next();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
		GlStateManager.depthMask(true);
	}

	public boolean method_3542(BeaconBlockEntity beaconBlockEntity) {
		return true;
	}
}
