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
		this.render(d, e, f, (double)g, beaconBlockEntity.getBeamSegments(), beaconBlockEntity.getWorld().getTime());
	}

	private void render(double d, double e, double f, double g, List<BeaconBlockEntity.BeamSegment> list, long l) {
		GlStateManager.alphaFunc(516, 0.1F);
		this.bindTexture(BEAM_TEX);
		GlStateManager.disableFog();
		int i = 0;

		for (int j = 0; j < list.size(); j++) {
			BeaconBlockEntity.BeamSegment beamSegment = (BeaconBlockEntity.BeamSegment)list.get(j);
			renderBeaconLightBeam(d, e, f, g, l, i, j == list.size() - 1 ? 1024 : beamSegment.getHeight(), beamSegment.getColor());
			i += beamSegment.getHeight();
		}

		GlStateManager.enableFog();
	}

	private static void renderBeaconLightBeam(double x, double y, double z, double tickDelta, long l, int i, int j, float[] fs) {
		renderLightBeam(x, y, z, tickDelta, 1.0, l, i, j, fs, 0.2, 0.25);
	}

	public static void renderLightBeam(
		double x,
		double y,
		double z,
		double tickDelta,
		double textureOffset,
		long worldTime,
		int startHeight,
		int height,
		float[] beamColor,
		double innerRadius,
		double outerRadius
	) {
		int i = startHeight + height;
		GlStateManager.texParameter(3553, 10242, 10497);
		GlStateManager.texParameter(3553, 10243, 10497);
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.pushMatrix();
		GlStateManager.translated(x + 0.5, y, z + 0.5);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		double d = (double)Math.floorMod(worldTime, 40L) + tickDelta;
		double e = height < 0 ? d : -d;
		double f = MathHelper.fractionalPart(e * 0.2 - (double)MathHelper.floor(e * 0.1));
		float g = beamColor[0];
		float h = beamColor[1];
		float j = beamColor[2];
		GlStateManager.pushMatrix();
		GlStateManager.rotated(d * 2.25 - 45.0, 0.0, 1.0, 0.0);
		double k = 0.0;
		double n = 0.0;
		double o = -innerRadius;
		double p = 0.0;
		double q = 0.0;
		double r = -innerRadius;
		double s = 0.0;
		double t = 1.0;
		double u = -1.0 + f;
		double v = (double)height * textureOffset * (0.5 / innerRadius) + u;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)i, innerRadius).texture(1.0, v).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)startHeight, innerRadius).texture(1.0, u).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(innerRadius, (double)startHeight, 0.0).texture(0.0, u).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(innerRadius, (double)i, 0.0).texture(0.0, v).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)i, r).texture(1.0, v).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)startHeight, r).texture(1.0, u).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(o, (double)startHeight, 0.0).texture(0.0, u).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(o, (double)i, 0.0).texture(0.0, v).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(innerRadius, (double)i, 0.0).texture(1.0, v).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(innerRadius, (double)startHeight, 0.0).texture(1.0, u).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)startHeight, r).texture(0.0, u).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)i, r).texture(0.0, v).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(o, (double)i, 0.0).texture(1.0, v).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(o, (double)startHeight, 0.0).texture(1.0, u).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)startHeight, innerRadius).texture(0.0, u).color(g, h, j, 1.0F).next();
		bufferBuilder.vertex(0.0, (double)i, innerRadius).texture(0.0, v).color(g, h, j, 1.0F).next();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.depthMask(false);
		k = -outerRadius;
		double l = -outerRadius;
		n = -outerRadius;
		o = -outerRadius;
		s = 0.0;
		t = 1.0;
		u = -1.0 + f;
		v = (double)height * textureOffset + u;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(k, (double)i, l).texture(1.0, v).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(k, (double)startHeight, l).texture(1.0, u).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(outerRadius, (double)startHeight, n).texture(0.0, u).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(outerRadius, (double)i, n).texture(0.0, v).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(outerRadius, (double)i, outerRadius).texture(1.0, v).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(outerRadius, (double)startHeight, outerRadius).texture(1.0, u).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(o, (double)startHeight, outerRadius).texture(0.0, u).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(o, (double)i, outerRadius).texture(0.0, v).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(outerRadius, (double)i, n).texture(1.0, v).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(outerRadius, (double)startHeight, n).texture(1.0, u).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(outerRadius, (double)startHeight, outerRadius).texture(0.0, u).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(outerRadius, (double)i, outerRadius).texture(0.0, v).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(o, (double)i, outerRadius).texture(1.0, v).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(o, (double)startHeight, outerRadius).texture(1.0, u).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(k, (double)startHeight, l).texture(0.0, u).color(g, h, j, 0.125F).next();
		bufferBuilder.vertex(k, (double)i, l).texture(0.0, v).color(g, h, j, 0.125F).next();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
		GlStateManager.depthMask(true);
	}

	public boolean method_3563(BeaconBlockEntity beaconBlockEntity) {
		return true;
	}
}
