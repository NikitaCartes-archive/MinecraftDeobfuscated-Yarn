package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BreezeEyesFeatureRenderer extends FeatureRenderer<BreezeEntity, BreezeEntityModel<BreezeEntity>> {
	private final Identifier texture;
	private final BreezeEntityModel<BreezeEntity> model;

	public BreezeEyesFeatureRenderer(
		FeatureRendererContext<BreezeEntity, BreezeEntityModel<BreezeEntity>> breezeModel, EntityModelLoader entityModelLoader, Identifier texture
	) {
		super(breezeModel);
		this.model = new BreezeEntityModel<>(entityModelLoader.getModelPart(EntityModelLayers.BREEZE_EYES));
		this.texture = texture;
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		BreezeEntity breezeEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		this.model.animateModel(breezeEntity, f, g, h);
		this.getContextModel().copyStateTo(this.model);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissiveNoOutline(this.texture));
		this.model.setAngles(breezeEntity, f, g, j, k, l);
		this.model.getPart().render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	protected Identifier getTexture(BreezeEntity breezeEntity) {
		return this.texture;
	}
}
