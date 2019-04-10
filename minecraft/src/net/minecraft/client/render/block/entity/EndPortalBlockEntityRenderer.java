package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class EndPortalBlockEntityRenderer extends BlockEntityRenderer<EndPortalBlockEntity> {
	private static final Identifier SKY_TEX = new Identifier("textures/environment/end_sky.png");
	private static final Identifier PORTAL_TEX = new Identifier("textures/entity/end_portal.png");
	private static final Random RANDOM = new Random(31100L);
	private static final FloatBuffer field_4408 = GlAllocationUtils.allocateFloatBuffer(16);
	private static final FloatBuffer field_4404 = GlAllocationUtils.allocateFloatBuffer(16);
	private final FloatBuffer field_4403 = GlAllocationUtils.allocateFloatBuffer(16);

	public void method_3591(EndPortalBlockEntity endPortalBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.disableLighting();
		RANDOM.setSeed(31100L);
		GlStateManager.getMatrix(2982, field_4408);
		GlStateManager.getMatrix(2983, field_4404);
		double h = d * d + e * e + f * f;
		int j = this.method_3592(h);
		float k = this.method_3594();
		boolean bl = false;
		GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;

		for (int l = 0; l < j; l++) {
			GlStateManager.pushMatrix();
			float m = 2.0F / (float)(18 - l);
			if (l == 0) {
				this.bindTexture(SKY_TEX);
				m = 0.15F;
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			}

			if (l >= 1) {
				this.bindTexture(PORTAL_TEX);
				bl = true;
				gameRenderer.setFogBlack(true);
			}

			if (l == 1) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			}

			GlStateManager.texGenMode(GlStateManager.TexCoord.field_5154, 9216);
			GlStateManager.texGenMode(GlStateManager.TexCoord.field_5155, 9216);
			GlStateManager.texGenMode(GlStateManager.TexCoord.field_5156, 9216);
			GlStateManager.texGenParam(GlStateManager.TexCoord.field_5154, 9474, this.method_3593(1.0F, 0.0F, 0.0F, 0.0F));
			GlStateManager.texGenParam(GlStateManager.TexCoord.field_5155, 9474, this.method_3593(0.0F, 1.0F, 0.0F, 0.0F));
			GlStateManager.texGenParam(GlStateManager.TexCoord.field_5156, 9474, this.method_3593(0.0F, 0.0F, 1.0F, 0.0F));
			GlStateManager.enableTexGen(GlStateManager.TexCoord.field_5154);
			GlStateManager.enableTexGen(GlStateManager.TexCoord.field_5155);
			GlStateManager.enableTexGen(GlStateManager.TexCoord.field_5156);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.translatef(0.5F, 0.5F, 0.0F);
			GlStateManager.scalef(0.5F, 0.5F, 1.0F);
			float n = (float)(l + 1);
			GlStateManager.translatef(17.0F / n, (2.0F + n / 1.5F) * ((float)(SystemUtil.getMeasuringTimeMs() % 800000L) / 800000.0F), 0.0F);
			GlStateManager.rotatef((n * n * 4321.0F + n * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.scalef(4.5F - n / 4.0F, 4.5F - n / 4.0F, 1.0F);
			GlStateManager.multMatrix(field_4404);
			GlStateManager.multMatrix(field_4408);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
			float o = (RANDOM.nextFloat() * 0.5F + 0.1F) * m;
			float p = (RANDOM.nextFloat() * 0.5F + 0.4F) * m;
			float q = (RANDOM.nextFloat() * 0.5F + 0.5F) * m;
			if (endPortalBlockEntity.shouldDrawSide(Direction.SOUTH)) {
				bufferBuilder.vertex(d, e, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e + 1.0, f + 1.0).color(o, p, q, 1.0F).next();
			}

			if (endPortalBlockEntity.shouldDrawSide(Direction.NORTH)) {
				bufferBuilder.vertex(d, e + 1.0, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e, f).color(o, p, q, 1.0F).next();
			}

			if (endPortalBlockEntity.shouldDrawSide(Direction.EAST)) {
				bufferBuilder.vertex(d + 1.0, e + 1.0, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f).color(o, p, q, 1.0F).next();
			}

			if (endPortalBlockEntity.shouldDrawSide(Direction.WEST)) {
				bufferBuilder.vertex(d, e, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e + 1.0, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e + 1.0, f).color(o, p, q, 1.0F).next();
			}

			if (endPortalBlockEntity.shouldDrawSide(Direction.DOWN)) {
				bufferBuilder.vertex(d, e, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e, f + 1.0).color(o, p, q, 1.0F).next();
			}

			if (endPortalBlockEntity.shouldDrawSide(Direction.UP)) {
				bufferBuilder.vertex(d, e + (double)k, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + (double)k, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + (double)k, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e + (double)k, f).color(o, p, q, 1.0F).next();
			}

			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			this.bindTexture(SKY_TEX);
		}

		GlStateManager.disableBlend();
		GlStateManager.disableTexGen(GlStateManager.TexCoord.field_5154);
		GlStateManager.disableTexGen(GlStateManager.TexCoord.field_5155);
		GlStateManager.disableTexGen(GlStateManager.TexCoord.field_5156);
		GlStateManager.enableLighting();
		if (bl) {
			gameRenderer.setFogBlack(false);
		}
	}

	protected int method_3592(double d) {
		int i;
		if (d > 36864.0) {
			i = 1;
		} else if (d > 25600.0) {
			i = 3;
		} else if (d > 16384.0) {
			i = 5;
		} else if (d > 9216.0) {
			i = 7;
		} else if (d > 4096.0) {
			i = 9;
		} else if (d > 1024.0) {
			i = 11;
		} else if (d > 576.0) {
			i = 13;
		} else if (d > 256.0) {
			i = 14;
		} else {
			i = 15;
		}

		return i;
	}

	protected float method_3594() {
		return 0.75F;
	}

	private FloatBuffer method_3593(float f, float g, float h, float i) {
		this.field_4403.clear();
		this.field_4403.put(f).put(g).put(h).put(i);
		this.field_4403.flip();
		return this.field_4403;
	}
}
