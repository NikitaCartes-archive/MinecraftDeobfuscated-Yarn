package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class WolfArmorFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
	private final WolfEntityModel<WolfEntity> model;

	public WolfArmorFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context, EntityModelLoader loader) {
		super(context);
		this.model = new WolfEntityModel<>(loader.getModelPart(EntityModelLayers.WOLF_ARMOR));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, WolfEntity wolfEntity, float f, float g, float h, float j, float k, float l
	) {
		if (wolfEntity.hasArmor()) {
			this.getContextModel().copyStateTo(this.model);
			this.model.animateModel(wolfEntity, f, g, h);
			this.model.setAngles(wolfEntity, f, g, j, k, l);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(((AnimalArmorItem)Items.WOLF_ARMOR).getEntityTexture()));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
