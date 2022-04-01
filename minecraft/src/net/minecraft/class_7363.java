package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class class_7363<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
	public class_7363(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		if (livingEntity instanceof SkeletonEntity skeletonEntity) {
			int m = skeletonEntity.method_42827();
			if (m == 0) {
				return;
			}

			ItemStack itemStack = new ItemStack(Items.SPYGLASS);
			double d = 0.15625;
			double e = -0.28125;
			double n = 0.5;
			float o = 0.75F;
			matrixStack.push();
			this.getContextModel().getHead().rotate(matrixStack);
			matrixStack.scale(0.75F, 0.75F, 0.75F);
			if (m >= 1) {
				matrixStack.push();
				matrixStack.translate(-0.15625, -0.28125, 0.5);
				MinecraftClient.getInstance()
					.getHeldItemRenderer()
					.renderItem(livingEntity, itemStack, ModelTransformation.Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
				matrixStack.pop();
			}

			if (m >= 2) {
				matrixStack.push();
				matrixStack.translate(0.15625, -0.28125, 0.5);
				MinecraftClient.getInstance()
					.getHeldItemRenderer()
					.renderItem(livingEntity, itemStack, ModelTransformation.Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
				matrixStack.pop();
			}

			matrixStack.pop();
		}
	}
}
