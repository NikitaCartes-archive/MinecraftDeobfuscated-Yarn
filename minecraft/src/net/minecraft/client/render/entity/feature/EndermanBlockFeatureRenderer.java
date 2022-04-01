package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_7323;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class EndermanBlockFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, BipedEntityModel<T>> {
	private final float field_38671;
	private final float field_38672;
	private final float field_38673;

	public EndermanBlockFeatureRenderer(FeatureRendererContext<T, BipedEntityModel<T>> featureRendererContext, float f, float g, float h) {
		super(featureRendererContext);
		this.field_38671 = f;
		this.field_38672 = g;
		this.field_38673 = h;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		if (livingEntity.method_42803() == LivingEntity.class_7316.BLOCK) {
			BlockState blockState = livingEntity.method_42800();
			matrixStack.push();
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(k));
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(l));
			Item item = class_7323.method_42881(blockState);
			if (item != null) {
				matrixStack.translate(0.0, 0.25, -1.0);
				matrixStack.translate((double)this.field_38671, (double)this.field_38672, (double)this.field_38673);
				float m = 1.5F;
				matrixStack.scale(-1.5F, -1.5F, 1.5F);
				ItemStack itemStack = item.getDefaultStack();
				MinecraftClient.getInstance()
					.getHeldItemRenderer()
					.renderItem(livingEntity, itemStack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, false, matrixStack, vertexConsumerProvider, i);
			} else {
				matrixStack.translate(0.0, 0.375, -0.75);
				matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(20.0F));
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(45.0F));
				matrixStack.translate((double)this.field_38671, (double)this.field_38672, (double)this.field_38673);
				float m = 0.5F;
				matrixStack.scale(-0.5F, -0.5F, 0.5F);
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
			}

			matrixStack.pop();
		}
	}
}
