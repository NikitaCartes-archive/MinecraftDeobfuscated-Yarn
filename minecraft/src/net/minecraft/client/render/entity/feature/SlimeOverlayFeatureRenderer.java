package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
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
		method_23195(this.getModel(), this.model, this.method_23194(livingEntity), matrixStack, layeredVertexConsumerStorage, i, livingEntity, f, g, j, k, l, m, h);
	}
}
