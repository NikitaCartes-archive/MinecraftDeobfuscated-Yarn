package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer extends FeatureRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
	private final HeldItemRenderer heldItemRenderer;

	public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntity, PandaEntityModel<PandaEntity>> context, HeldItemRenderer heldItemRenderer) {
		super(context);
		this.heldItemRenderer = heldItemRenderer;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PandaEntity pandaEntity, float f, float g, float h, float j, float k, float l
	) {
		ItemStack itemStack = pandaEntity.getEquippedStack(EquipmentSlot.MAINHAND);
		if (pandaEntity.isSitting() && !pandaEntity.isScaredByThunderstorm()) {
			float m = -0.6F;
			float n = 1.4F;
			if (pandaEntity.isEating()) {
				m -= 0.2F * MathHelper.sin(j * 0.6F) + 0.2F;
				n -= 0.09F * MathHelper.sin(j * 0.6F);
			}

			matrixStack.push();
			matrixStack.translate(0.1F, n, m);
			this.heldItemRenderer.renderItem(pandaEntity, itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i);
			matrixStack.pop();
		}
	}
}
