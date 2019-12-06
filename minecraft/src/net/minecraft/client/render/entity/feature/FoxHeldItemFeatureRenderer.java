package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class FoxHeldItemFeatureRenderer extends FeatureRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
	public FoxHeldItemFeatureRenderer(FeatureRendererContext<FoxEntity, FoxEntityModel<FoxEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, FoxEntity foxEntity, float f, float g, float h, float j, float k, float l
	) {
		boolean bl = foxEntity.isSleeping();
		boolean bl2 = foxEntity.isBaby();
		matrixStack.push();
		if (bl2) {
			float m = 0.75F;
			matrixStack.scale(0.75F, 0.75F, 0.75F);
			matrixStack.translate(0.0, 0.5, 0.209375F);
		}

		matrixStack.translate(
			(double)(this.getContextModel().head.pivotX / 16.0F),
			(double)(this.getContextModel().head.pivotY / 16.0F),
			(double)(this.getContextModel().head.pivotZ / 16.0F)
		);
		float m = foxEntity.getHeadRoll(h);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(m));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(k));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(l));
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

		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
		if (bl) {
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
		}

		ItemStack itemStack = foxEntity.getEquippedStack(EquipmentSlot.MAINHAND);
		MinecraftClient.getInstance()
			.getHeldItemRenderer()
			.renderItem(foxEntity, itemStack, ModelTransformation.Type.GROUND, false, matrixStack, vertexConsumerProvider, i);
		matrixStack.pop();
	}
}
