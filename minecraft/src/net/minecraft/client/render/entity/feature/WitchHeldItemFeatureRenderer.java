package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer<T extends LivingEntity> extends VillagerHeldItemFeatureRenderer<T, WitchEntityModel<T>> {
	public WitchHeldItemFeatureRenderer(FeatureRendererContext<T, WitchEntityModel<T>> context) {
		super(context);
	}

	@Override
	public void method_4208(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		ItemStack itemStack = livingEntity.getMainHandStack();
		matrixStack.push();
		if (itemStack.getItem() == Items.POTION) {
			this.getModel().getHead().rotate(matrixStack);
			this.getModel().getNose().rotate(matrixStack);
			matrixStack.translate(0.0625, 0.25, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0F));
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(140.0F));
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(10.0F));
			matrixStack.translate(0.0, -0.4F, 0.4F);
		}

		super.method_4208(matrixStack, vertexConsumerProvider, i, livingEntity, f, g, h, j, k, l);
		matrixStack.pop();
	}
}
