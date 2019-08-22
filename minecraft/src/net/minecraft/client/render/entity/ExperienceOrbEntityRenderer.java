package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ExperienceOrbEntityRenderer extends EntityRenderer<ExperienceOrbEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/experience_orb.png");

	public ExperienceOrbEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.15F;
		this.field_4672 = 0.75F;
	}

	public void method_3966(ExperienceOrbEntity experienceOrbEntity, double d, double e, double f, float g, float h) {
		if (!this.renderOutlines && MinecraftClient.getInstance().getEntityRenderManager().gameOptions != null) {
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float)d, (float)e, (float)f);
			this.bindEntityTexture(experienceOrbEntity);
			GuiLighting.enable();
			int i = experienceOrbEntity.getOrbSize();
			float j = (float)(i % 4 * 16 + 0) / 64.0F;
			float k = (float)(i % 4 * 16 + 16) / 64.0F;
			float l = (float)(i / 4 * 16 + 0) / 64.0F;
			float m = (float)(i / 4 * 16 + 16) / 64.0F;
			float n = 1.0F;
			float o = 0.5F;
			float p = 0.25F;
			int q = experienceOrbEntity.getLightmapCoordinates();
			int r = q % 65536;
			int s = q / 65536;
			RenderSystem.glMultiTexCoord2f(33985, (float)r, (float)s);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float t = 255.0F;
			float u = ((float)experienceOrbEntity.renderTicks + h) / 2.0F;
			int v = (int)((MathHelper.sin(u + 0.0F) + 1.0F) * 0.5F * 255.0F);
			int w = 255;
			int x = (int)((MathHelper.sin(u + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
			RenderSystem.translatef(0.0F, 0.1F, 0.0F);
			RenderSystem.rotatef(180.0F - this.renderManager.cameraYaw, 0.0F, 1.0F, 0.0F);
			RenderSystem.rotatef((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch, 1.0F, 0.0F, 0.0F);
			float y = 0.3F;
			RenderSystem.scalef(0.3F, 0.3F, 0.3F);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_NORMAL);
			bufferBuilder.vertex(-0.5, -0.25, 0.0).texture((double)j, (double)m).color(v, 255, x, 128).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.5, -0.25, 0.0).texture((double)k, (double)m).color(v, 255, x, 128).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.5, 0.75, 0.0).texture((double)k, (double)l).color(v, 255, x, 128).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(-0.5, 0.75, 0.0).texture((double)j, (double)l).color(v, 255, x, 128).normal(0.0F, 1.0F, 0.0F).next();
			tessellator.draw();
			RenderSystem.disableBlend();
			RenderSystem.disableRescaleNormal();
			RenderSystem.popMatrix();
			super.render(experienceOrbEntity, d, e, f, g, h);
		}
	}

	protected Identifier method_3967(ExperienceOrbEntity experienceOrbEntity) {
		return SKIN;
	}
}
