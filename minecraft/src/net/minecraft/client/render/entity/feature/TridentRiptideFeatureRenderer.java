package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TridentRiptideFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/trident_riptide.png");
	private final ModelPart aura = new ModelPart(64, 64, 0, 0);

	public TridentRiptideFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
		this.aura.addCuboid(-8.0F, -16.0F, -8.0F, 16.0F, 32.0F, 16.0F);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		if (livingEntity.isUsingRiptide()) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));

			for (int m = 0; m < 3; m++) {
				matrixStack.push();
				float n = j * (float)(-(45 + m * 5));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(n));
				float o = 0.75F * (float)m;
				matrixStack.scale(o, o, o);
				matrixStack.translate(0.0, (double)(-0.2F + 0.6F * (float)m), 0.0);
				this.aura.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
				matrixStack.pop();
			}
		}
	}
}
