package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class SnowmanPumpkinFeatureRenderer extends FeatureRenderer<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> {
	public SnowmanPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4201(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		SnowGolemEntity snowGolemEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		if (!snowGolemEntity.isInvisible() && snowGolemEntity.hasPumpkin()) {
			matrixStack.push();
			this.getModel().method_2834().rotate(matrixStack, 0.0625F);
			float n = 0.625F;
			matrixStack.translate(0.0, -0.34375, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F));
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
					layeredVertexConsumerStorage,
					snowGolemEntity.world,
					snowGolemEntity.getLightmapCoordinates(),
					LivingEntityRenderer.method_23622(snowGolemEntity, 0.0F)
				);
			matrixStack.pop();
		}
	}
}
