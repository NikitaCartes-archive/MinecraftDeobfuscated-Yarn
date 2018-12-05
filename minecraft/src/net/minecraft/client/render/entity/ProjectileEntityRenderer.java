package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends ProjectileEntity> extends EntityRenderer<T> {
	public ProjectileEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3875(T projectileEntity, double d, double e, double f, float g, float h) {
		this.method_3925(projectileEntity);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.rotatef(MathHelper.lerp(h, projectileEntity.prevYaw, projectileEntity.yaw) - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(MathHelper.lerp(h, projectileEntity.prevPitch, projectileEntity.pitch), 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		int i = 0;
		float j = 0.0F;
		float k = 0.5F;
		float l = 0.0F;
		float m = 0.15625F;
		float n = 0.0F;
		float o = 0.15625F;
		float p = 0.15625F;
		float q = 0.3125F;
		float r = 0.05625F;
		GlStateManager.enableRescaleNormal();
		float s = (float)projectileEntity.shake - h;
		if (s > 0.0F) {
			float t = -MathHelper.sin(s * 3.0F) * s;
			GlStateManager.rotatef(t, 0.0F, 0.0F, 1.0F);
		}

		GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(0.05625F, 0.05625F, 0.05625F);
		GlStateManager.translatef(-4.0F, 0.0F, 0.0F);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(projectileEntity));
		}

		GlStateManager.normal3f(0.05625F, 0.0F, 0.0F);
		vertexBuffer.begin(7, VertexFormats.POSITION_UV);
		vertexBuffer.vertex(-7.0, -2.0, -2.0).texture(0.0, 0.15625).next();
		vertexBuffer.vertex(-7.0, -2.0, 2.0).texture(0.15625, 0.15625).next();
		vertexBuffer.vertex(-7.0, 2.0, 2.0).texture(0.15625, 0.3125).next();
		vertexBuffer.vertex(-7.0, 2.0, -2.0).texture(0.0, 0.3125).next();
		tessellator.draw();
		GlStateManager.normal3f(-0.05625F, 0.0F, 0.0F);
		vertexBuffer.begin(7, VertexFormats.POSITION_UV);
		vertexBuffer.vertex(-7.0, 2.0, -2.0).texture(0.0, 0.15625).next();
		vertexBuffer.vertex(-7.0, 2.0, 2.0).texture(0.15625, 0.15625).next();
		vertexBuffer.vertex(-7.0, -2.0, 2.0).texture(0.15625, 0.3125).next();
		vertexBuffer.vertex(-7.0, -2.0, -2.0).texture(0.0, 0.3125).next();
		tessellator.draw();

		for (int u = 0; u < 4; u++) {
			GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.normal3f(0.0F, 0.0F, 0.05625F);
			vertexBuffer.begin(7, VertexFormats.POSITION_UV);
			vertexBuffer.vertex(-8.0, -2.0, 0.0).texture(0.0, 0.0).next();
			vertexBuffer.vertex(8.0, -2.0, 0.0).texture(0.5, 0.0).next();
			vertexBuffer.vertex(8.0, 2.0, 0.0).texture(0.5, 0.15625).next();
			vertexBuffer.vertex(-8.0, 2.0, 0.0).texture(0.0, 0.15625).next();
			tessellator.draw();
		}

		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
		super.method_3936(projectileEntity, d, e, f, g, h);
	}
}
