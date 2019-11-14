package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public VillagerHeldItemFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4208(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		matrixStack.push();
		matrixStack.translate(0.0, 0.4F, -0.4F);
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.MAINHAND);
		MinecraftClient.getInstance()
			.getFirstPersonRenderer()
			.renderItem(livingEntity, itemStack, ModelTransformation.Type.GROUND, false, matrixStack, vertexConsumerProvider, i);
		matrixStack.pop();
	}
}
