package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WindChargeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WindChargeEntityRenderer extends EntityRenderer<WindChargeEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/projectiles/wind_charge.png");
	private final WindChargeEntityModel model;

	public WindChargeEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.model = new WindChargeEntityModel(context.getPart(EntityModelLayers.WIND_CHARGE));
	}

	public void render(WindChargeEntity windChargeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		float h = (float)windChargeEntity.age + g;
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getBreezeWind(TEXTURE, this.getXOffset(h) % 1.0F, 0.0F));
		this.model.setAngles(windChargeEntity, 0.0F, 0.0F, h, 0.0F, 0.0F);
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		super.render(windChargeEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	protected float getXOffset(float tickDelta) {
		return tickDelta * 0.03F;
	}

	public Identifier getTexture(WindChargeEntity windChargeEntity) {
		return TEXTURE;
	}
}
