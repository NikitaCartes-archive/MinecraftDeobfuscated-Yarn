package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class PigSaddleFeatureRenderer extends FeatureRenderer<PigEntity, PigEntityModel<PigEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/pig/pig_saddle.png");
	private final PigEntityModel<PigEntity> model = new PigEntityModel<>(0.5F);

	public PigSaddleFeatureRenderer(FeatureRendererContext<PigEntity, PigEntityModel<PigEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4196(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		PigEntity pigEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		if (pigEntity.isSaddled()) {
			this.getModel().copyStateTo(this.model);
			this.model.animateModel(pigEntity, f, g, h);
			this.model.setAngles(pigEntity, f, g, j, k, l, m);
			method_23197(this.model, SKIN, matrixStack, layeredVertexConsumerStorage, i, 1.0F, 1.0F, 1.0F);
		}
	}
}
