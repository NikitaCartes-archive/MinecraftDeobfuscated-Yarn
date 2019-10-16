package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer extends FeatureRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
	public DolphinHeldItemFeatureRenderer(FeatureRendererContext<DolphinEntity, DolphinEntityModel<DolphinEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_17160(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		DolphinEntity dolphinEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		boolean bl = dolphinEntity.getMainArm() == Arm.RIGHT;
		matrixStack.push();
		float n = 1.0F;
		float o = -1.0F;
		float p = MathHelper.abs(dolphinEntity.pitch) / 60.0F;
		if (dolphinEntity.pitch < 0.0F) {
			matrixStack.translate(0.0, (double)(1.0F - p * 0.5F), (double)(-1.0F + p * 0.5F));
		} else {
			matrixStack.translate(0.0, (double)(1.0F + p * 0.8F), (double)(-1.0F + p * 0.2F));
		}

		ItemStack itemStack = bl ? dolphinEntity.getMainHandStack() : dolphinEntity.getOffHandStack();
		MinecraftClient.getInstance()
			.getFirstPersonRenderer()
			.renderItem(dolphinEntity, itemStack, ModelTransformation.Type.GROUND, false, matrixStack, layeredVertexConsumerStorage);
		matrixStack.pop();
	}
}
