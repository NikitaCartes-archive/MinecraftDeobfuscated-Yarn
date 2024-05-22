package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.BreezeEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.BreezeWindFeatureRenderer;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BreezeEntityRenderer extends MobEntityRenderer<BreezeEntity, BreezeEntityModel<BreezeEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/breeze/breeze.png");

	public BreezeEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new BreezeEntityModel<>(context.getPart(EntityModelLayers.BREEZE)), 0.5F);
		this.addFeature(new BreezeWindFeatureRenderer(context, this));
		this.addFeature(new BreezeEyesFeatureRenderer(this));
	}

	public void render(BreezeEntity breezeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		BreezeEntityModel<BreezeEntity> breezeEntityModel = this.getModel();
		updatePartVisibility(breezeEntityModel, breezeEntityModel.getHead(), breezeEntityModel.getRods());
		super.render(breezeEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(BreezeEntity breezeEntity) {
		return TEXTURE;
	}

	public static BreezeEntityModel<BreezeEntity> updatePartVisibility(BreezeEntityModel<BreezeEntity> model, ModelPart... modelParts) {
		model.getHead().visible = false;
		model.getEyes().visible = false;
		model.getRods().visible = false;
		model.getWindBody().visible = false;

		for (ModelPart modelPart : modelParts) {
			modelPart.visible = true;
		}

		return model;
	}
}
