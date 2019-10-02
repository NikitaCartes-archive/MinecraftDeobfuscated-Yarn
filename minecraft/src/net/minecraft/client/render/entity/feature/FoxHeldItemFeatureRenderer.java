package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class FoxHeldItemFeatureRenderer extends FeatureRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
	public FoxHeldItemFeatureRenderer(FeatureRendererContext<FoxEntity, FoxEntityModel<FoxEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_18335(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		FoxEntity foxEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		boolean bl = foxEntity.isSleeping();
		boolean bl2 = foxEntity.isBaby();
		matrixStack.push();
		if (bl2) {
			float n = 0.75F;
			matrixStack.scale(0.75F, 0.75F, 0.75F);
			matrixStack.translate(0.0, (double)(8.0F * m), (double)(3.35F * m));
		}

		matrixStack.translate(
			(double)(this.getModel().head.pivotX / 16.0F), (double)(this.getModel().head.pivotY / 16.0F), (double)(this.getModel().head.pivotZ / 16.0F)
		);
		float n = foxEntity.getHeadRoll(h);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(n, false));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k, true));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(l, true));
		if (foxEntity.isBaby()) {
			if (bl) {
				matrixStack.translate(0.4F, 0.26F, 0.15F);
			} else {
				matrixStack.translate(0.06F, 0.26F, -0.5);
			}
		} else if (bl) {
			matrixStack.translate(0.46F, 0.26F, 0.22F);
		} else {
			matrixStack.translate(0.06F, 0.27F, -0.5);
		}

		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F, true));
		if (bl) {
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0F, true));
		}

		ItemStack itemStack = foxEntity.getEquippedStack(EquipmentSlot.MAINHAND);
		MinecraftClient.getInstance()
			.getFirstPersonRenderer()
			.renderItem(foxEntity, itemStack, ModelTransformation.Type.GROUND, false, matrixStack, layeredVertexConsumerStorage);
		matrixStack.pop();
	}
}
