package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class SnowmanPumpkinFeatureRenderer extends FeatureRenderer<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> {
	public SnowmanPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> context) {
		super(context);
	}

	public void method_4201(
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
			this.getModel().method_2834().rotate(matrixStack);
			float m = 0.625F;
			matrixStack.translate(0.0, -0.34375, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			matrixStack.scale(0.625F, -0.625F, -0.625F);
			ItemStack itemStack = new ItemStack(Blocks.CARVED_PUMPKIN);
			MinecraftClient.getInstance()
				.getItemRenderer()
				.method_23177(
					snowGolemEntity,
					itemStack,
					ModelTransformation.Type.HEAD,
					false,
					matrixStack,
					vertexConsumerProvider,
					snowGolemEntity.world,
					i,
					LivingEntityRenderer.method_23622(snowGolemEntity, 0.0F)
				);
			matrixStack.pop();
		}
	}
}
