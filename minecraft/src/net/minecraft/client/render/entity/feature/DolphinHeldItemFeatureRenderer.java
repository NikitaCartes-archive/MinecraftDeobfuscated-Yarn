package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer extends FeatureRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
	private final HeldItemRenderer heldItemRenderer;

	public DolphinHeldItemFeatureRenderer(FeatureRendererContext<DolphinEntity, DolphinEntityModel<DolphinEntity>> context, HeldItemRenderer heldItemRenderer) {
		super(context);
		this.heldItemRenderer = heldItemRenderer;
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		DolphinEntity dolphinEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		boolean bl = dolphinEntity.getMainArm() == Arm.RIGHT;
		matrixStack.push();
		float m = 1.0F;
		float n = -1.0F;
		float o = MathHelper.abs(dolphinEntity.getPitch()) / 60.0F;
		if (dolphinEntity.getPitch() < 0.0F) {
			matrixStack.translate(0.0F, 1.0F - o * 0.5F, -1.0F + o * 0.5F);
		} else {
			matrixStack.translate(0.0F, 1.0F + o * 0.8F, -1.0F + o * 0.2F);
		}

		ItemStack itemStack = bl ? dolphinEntity.getMainHandStack() : dolphinEntity.getOffHandStack();
		this.heldItemRenderer.renderItem(dolphinEntity, itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i);
		matrixStack.pop();
	}
}
