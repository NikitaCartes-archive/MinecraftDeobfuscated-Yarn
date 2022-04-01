package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public VillagerHeldItemFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		matrixStack.push();
		matrixStack.translate(0.0, 0.4F, -0.4F);
		matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.MAINHAND);
		if (!itemStack.isEmpty()) {
			MinecraftClient.getInstance()
				.getHeldItemRenderer()
				.renderItem(livingEntity, itemStack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, i);
		}

		if (livingEntity instanceof MerchantEntity merchantEntity) {
			Entity entity = merchantEntity.method_42833();
			if (entity != null) {
				Box box = entity.getBoundingBox();
				double d = 0.5;
				float m = Math.min((float)(0.5 / box.getYLength()), 1.0F);
				matrixStack.push();
				matrixStack.scale(m, m, m);
				MinecraftClient.getInstance().getEntityRenderDispatcher().render(entity, 0.0, 0.0, 0.0, 0.0F, h, matrixStack, vertexConsumerProvider, i);
				matrixStack.pop();
			}
		}

		matrixStack.pop();
	}
}
