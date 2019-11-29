package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigSaddleFeatureRenderer extends FeatureRenderer<PigEntity, PigEntityModel<PigEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/pig/pig_saddle.png");
	private final PigEntityModel<PigEntity> model = new PigEntityModel<>(0.5F);

	public PigSaddleFeatureRenderer(FeatureRendererContext<PigEntity, PigEntityModel<PigEntity>> context) {
		super(context);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PigEntity pigEntity, float f, float g, float h, float j, float k, float l
	) {
		if (pigEntity.isSaddled()) {
			this.getModel().copyStateTo(this.model);
			this.model.animateModel(pigEntity, f, g, h);
			this.model.setAngles(pigEntity, f, g, j, k, l);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(SKIN));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
