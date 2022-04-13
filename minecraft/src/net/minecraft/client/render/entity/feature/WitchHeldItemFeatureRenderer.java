package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer<T extends LivingEntity> extends VillagerHeldItemFeatureRenderer<T, WitchEntityModel<T>> {
	public WitchHeldItemFeatureRenderer(FeatureRendererContext<T, WitchEntityModel<T>> featureRendererContext, HeldItemRenderer heldItemRenderer) {
		super(featureRendererContext, heldItemRenderer);
	}

	@Override
	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		ItemStack itemStack = livingEntity.getMainHandStack();
		matrixStack.push();
		if (itemStack.isOf(Items.POTION)) {
			this.getContextModel().getHead().rotate(matrixStack);
			this.getContextModel().getNose().rotate(matrixStack);
			matrixStack.translate(0.0625, 0.25, 0.0);
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(140.0F));
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(10.0F));
			matrixStack.translate(0.0, -0.4F, 0.4F);
		}

		super.render(matrixStack, vertexConsumerProvider, i, livingEntity, f, g, h, j, k, l);
		matrixStack.pop();
	}
}
