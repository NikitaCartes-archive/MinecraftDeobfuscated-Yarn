package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.feature.EnderDragonDeathFeatureRenderer;
import net.minecraft.client.render.entity.feature.EnderDragonEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.DragonEntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EnderDragonEntityRenderer extends MobEntityRenderer<EnderDragonEntity, DragonEntityModel> {
	public static final Identifier CRYSTAL_BEAM = new Identifier("textures/entity/end_crystal/end_crystal_beam.png");
	private static final Identifier EXPLOSION_TEX = new Identifier("textures/entity/enderdragon/dragon_exploding.png");
	private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon.png");

	public EnderDragonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new DragonEntityModel(0.0F), 0.5F);
		this.addFeature(new EnderDragonEyesFeatureRenderer(this));
		this.addFeature(new EnderDragonDeathFeatureRenderer(this));
	}

	protected void method_3915(EnderDragonEntity enderDragonEntity, float f, float g, float h) {
		float i = (float)enderDragonEntity.method_6817(7, h)[0];
		float j = (float)(enderDragonEntity.method_6817(5, h)[1] - enderDragonEntity.method_6817(10, h)[1]);
		GlStateManager.rotatef(-i, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(j * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translatef(0.0F, 0.0F, 1.0F);
		if (enderDragonEntity.deathCounter > 0) {
			float k = ((float)enderDragonEntity.deathCounter + h - 1.0F) / 20.0F * 1.6F;
			k = MathHelper.sqrt(k);
			if (k > 1.0F) {
				k = 1.0F;
			}

			GlStateManager.rotatef(k * this.getLyingAngle(enderDragonEntity), 0.0F, 0.0F, 1.0F);
		}
	}

	protected void render(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k) {
		if (enderDragonEntity.field_7031 > 0) {
			float l = (float)enderDragonEntity.field_7031 / 200.0F;
			GlStateManager.depthFunc(515);
			GlStateManager.enableAlphaTest();
			GlStateManager.alphaFunc(516, l);
			this.bindTexture(EXPLOSION_TEX);
			this.model.method_17137(enderDragonEntity, f, g, h, i, j, k);
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.depthFunc(514);
		}

		this.bindEntityTexture(enderDragonEntity);
		this.model.method_17137(enderDragonEntity, f, g, h, i, j, k);
		if (enderDragonEntity.hurtTime > 0) {
			GlStateManager.depthFunc(514);
			GlStateManager.disableTexture();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.class_1033.SRC_ALPHA, GlStateManager.class_1027.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color4f(1.0F, 0.0F, 0.0F, 0.5F);
			this.model.method_17137(enderDragonEntity, f, g, h, i, j, k);
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.depthFunc(515);
		}
	}

	public void method_3918(EnderDragonEntity enderDragonEntity, double d, double e, double f, float g, float h) {
		super.render(enderDragonEntity, d, e, f, g, h);
		if (enderDragonEntity.field_7024 != null) {
			this.bindTexture(CRYSTAL_BEAM);
			float i = MathHelper.sin(((float)enderDragonEntity.field_7024.age + h) * 0.2F) / 2.0F + 0.5F;
			i = (i * i + i) * 0.2F;
			method_3917(
				d,
				e,
				f,
				h,
				MathHelper.lerp((double)(1.0F - h), enderDragonEntity.x, enderDragonEntity.prevX),
				MathHelper.lerp((double)(1.0F - h), enderDragonEntity.y, enderDragonEntity.prevY),
				MathHelper.lerp((double)(1.0F - h), enderDragonEntity.z, enderDragonEntity.prevZ),
				enderDragonEntity.age,
				enderDragonEntity.field_7024.x,
				(double)i + enderDragonEntity.field_7024.y,
				enderDragonEntity.field_7024.z
			);
		}
	}

	public static void method_3917(double d, double e, double f, float g, double h, double i, double j, int k, double l, double m, double n) {
		float o = (float)(l - h);
		float p = (float)(m - 1.0 - i);
		float q = (float)(n - j);
		float r = MathHelper.sqrt(o * o + q * q);
		float s = MathHelper.sqrt(o * o + p * p + q * q);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e + 2.0F, (float)f);
		GlStateManager.rotatef((float)(-Math.atan2((double)q, (double)o)) * (180.0F / (float)Math.PI) - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(-Math.atan2((double)r, (double)p)) * (180.0F / (float)Math.PI) - 90.0F, 1.0F, 0.0F, 0.0F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		GuiLighting.disable();
		GlStateManager.disableCull();
		GlStateManager.shadeModel(7425);
		float t = 0.0F - ((float)k + g) * 0.01F;
		float u = MathHelper.sqrt(o * o + p * p + q * q) / 32.0F - ((float)k + g) * 0.01F;
		bufferBuilder.begin(5, VertexFormats.POSITION_UV_COLOR);
		int v = 8;

		for (int w = 0; w <= 8; w++) {
			float x = MathHelper.sin((float)(w % 8) * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float y = MathHelper.cos((float)(w % 8) * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float z = (float)(w % 8) / 8.0F;
			bufferBuilder.vertex((double)(x * 0.2F), (double)(y * 0.2F), 0.0).texture((double)z, (double)t).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)x, (double)y, (double)s).texture((double)z, (double)u).color(255, 255, 255, 255).next();
		}

		tessellator.draw();
		GlStateManager.enableCull();
		GlStateManager.shadeModel(7424);
		GuiLighting.enable();
		GlStateManager.popMatrix();
	}

	protected Identifier getTexture(EnderDragonEntity enderDragonEntity) {
		return SKIN;
	}
}
