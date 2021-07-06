package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ShulkerHeadFeatureRenderer extends FeatureRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
	public ShulkerHeadFeatureRenderer(FeatureRendererContext<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		ShulkerEntity shulkerEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		Identifier identifier = ShulkerEntityRenderer.getTexture(shulkerEntity.getColor());
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(identifier));
		this.getContextModel().getHead().render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(shulkerEntity, 0.0F));
	}
}
