package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class SnowmanPumpkinFeatureRenderer extends FeatureRenderer<SnowGolemEntity, SnowGolemEntityModel<SnowGolemEntity>> {
	public SnowmanPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntity, SnowGolemEntityModel<SnowGolemEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		SnowGolemEntity snowGolemEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		if (!snowGolemEntity.isInvisible() && snowGolemEntity.hasPumpkin()) {
			matrixStack.push();
			this.getContextModel().getTopSnowball().rotate(matrixStack);
			float m = 0.625F;
			matrixStack.translate(0.0, -0.34375, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			matrixStack.scale(0.625F, -0.625F, -0.625F);
			ItemStack itemStack = new ItemStack(Blocks.CARVED_PUMPKIN);
			MinecraftClient.getInstance()
				.getItemRenderer()
				.renderItem(
					snowGolemEntity,
					itemStack,
					ModelTransformation.Mode.HEAD,
					false,
					matrixStack,
					vertexConsumerProvider,
					snowGolemEntity.world,
					i,
					LivingEntityRenderer.getOverlay(snowGolemEntity, 0.0F)
				);
			matrixStack.pop();
		}
	}
}
