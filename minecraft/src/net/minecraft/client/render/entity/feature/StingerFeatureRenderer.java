package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StingerFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>> extends StickingOutThingsFeatureRenderer<T, M> {
	private static final Identifier field_20529 = new Identifier("textures/entity/bee/bee_stinger.png");

	public StingerFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	@Override
	protected int getThingCount(T livingEntity) {
		return livingEntity.getStingerCount();
	}

	@Override
	protected void beforeRendering(T livingEntity) {
		GuiLighting.disable();
		RenderSystem.pushMatrix();
		this.bindTexture(field_20529);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableLighting();
		RenderSystem.enableRescaleNormal();
	}

	@Override
	protected void afterRendering() {
		RenderSystem.disableRescaleNormal();
		RenderSystem.enableLighting();
		RenderSystem.popMatrix();
		GuiLighting.enable();
	}

	@Override
	protected void renderThing(Entity entity, float f, float g, float h, float i) {
		RenderSystem.pushMatrix();
		float j = MathHelper.sqrt(f * f + h * h);
		float k = (float)(Math.atan2((double)f, (double)h) * 180.0F / (float)Math.PI);
		float l = (float)(Math.atan2((double)g, (double)j) * 180.0F / (float)Math.PI);
		RenderSystem.translatef(0.0F, 0.0F, 0.0F);
		RenderSystem.rotatef(k - 90.0F, 0.0F, 1.0F, 0.0F);
		RenderSystem.rotatef(l, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		float m = 0.0F;
		float n = 0.125F;
		float o = 0.0F;
		float p = 0.0625F;
		float q = 0.03125F;
		RenderSystem.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
		RenderSystem.scalef(0.03125F, 0.03125F, 0.03125F);
		RenderSystem.translatef(2.5F, 0.0F, 0.0F);

		for (int r = 0; r < 4; r++) {
			RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			bufferBuilder.begin(7, VertexFormats.POSITION_UV);
			bufferBuilder.vertex(-4.5, -1.0, 0.0).texture(0.0, 0.0).next();
			bufferBuilder.vertex(4.5, -1.0, 0.0).texture(0.125, 0.0).next();
			bufferBuilder.vertex(4.5, 1.0, 0.0).texture(0.125, 0.0625).next();
			bufferBuilder.vertex(-4.5, 1.0, 0.0).texture(0.0, 0.0625).next();
			tessellator.draw();
		}

		RenderSystem.popMatrix();
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
