package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class class_9085 extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
	private final WolfEntityModel<WolfEntity> field_47890;

	public class_9085(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> featureRendererContext, EntityModelLoader entityModelLoader) {
		super(featureRendererContext);
		this.field_47890 = new WolfEntityModel<>(entityModelLoader.getModelPart(EntityModelLayers.WOLF_ARMOR));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, WolfEntity wolfEntity, float f, float g, float h, float j, float k, float l
	) {
		if (wolfEntity.method_55710()) {
			this.getContextModel().copyStateTo(this.field_47890);
			this.field_47890.animateModel(wolfEntity, f, g, h);
			this.field_47890.setAngles(wolfEntity, f, g, j, k, l);
			renderModel(this.field_47890, ((HorseArmorItem)Items.WOLF_ARMOR).getEntityTexture(), matrixStack, vertexConsumerProvider, i, wolfEntity, 1.0F, 1.0F, 1.0F);
		}
	}
}
