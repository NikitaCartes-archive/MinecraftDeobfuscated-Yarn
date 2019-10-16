package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TridentRiptideFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/trident_riptide.png");
	private final ModelPart field_21012 = new ModelPart(64, 64, 0, 0);

	public TridentRiptideFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
		this.field_21012.addCuboid(-8.0F, -16.0F, -8.0F, 16.0F, 32.0F, 16.0F);
	}

	public void method_4203(
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
		if (livingEntity.isUsingRiptide()) {
			VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));

			for (int n = 0; n < 3; n++) {
				matrixStack.push();
				float o = j * (float)(-(45 + n * 5));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(o));
				float p = 0.75F * (float)n;
				matrixStack.scale(p, p, p);
				matrixStack.translate(0.0, (double)(-0.2F + 0.6F * (float)n), 0.0);
				this.field_21012.render(matrixStack, vertexConsumer, m, i, OverlayTexture.DEFAULT_UV, null);
				matrixStack.pop();
			}
		}
	}
}
