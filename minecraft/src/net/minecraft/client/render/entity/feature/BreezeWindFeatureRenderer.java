package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BreezeEntityRenderer;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BreezeWindFeatureRenderer extends FeatureRenderer<BreezeEntity, BreezeEntityModel<BreezeEntity>> {
	private static final Identifier texture = new Identifier("textures/entity/breeze/breeze_wind.png");
	private static final BreezeEntityModel<BreezeEntity> model = new BreezeEntityModel<>(BreezeEntityModel.getTexturedModelData(128, 128).createModel());

	public BreezeWindFeatureRenderer(FeatureRendererContext<BreezeEntity, BreezeEntityModel<BreezeEntity>> breezeModel) {
		super(breezeModel);
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
		float m = (float)breezeEntity.age + h;
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getBreezeWind(texture, this.getXOffset(m) % 1.0F, 0.0F));
		model.setAngles(breezeEntity, f, g, j, k, l);
		BreezeEntityRenderer.method_55830(model, model.method_55822()).render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	private float getXOffset(float tickDelta) {
		return tickDelta * 0.02F;
	}
}
