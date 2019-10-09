package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class SlimeOverlayFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, SlimeEntityModel<T>> {
	private final EntityModel<T> model = new SlimeEntityModel<>(0);

	public SlimeOverlayFeatureRenderer(FeatureRendererContext<T, SlimeEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_23200(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		T livingEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		if (!livingEntity.isInvisible()) {
			this.getModel().copyStateTo(this.model);
			this.model.animateModel(livingEntity, f, g, h);
			this.model.setAngles(livingEntity, f, g, j, k, l, m);
			VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityTranslucent(this.method_23194(livingEntity)));
			this.model.renderItem(matrixStack, vertexConsumer, i, LivingEntityRenderer.method_23622(livingEntity, 0.0F), 1.0F, 1.0F, 1.0F);
		}
	}
}
