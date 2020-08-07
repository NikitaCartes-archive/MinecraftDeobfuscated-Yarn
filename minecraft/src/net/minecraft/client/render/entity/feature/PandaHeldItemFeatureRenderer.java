package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer extends FeatureRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
	public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntity, PandaEntityModel<PandaEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4194(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PandaEntity pandaEntity, float f, float g, float h, float j, float k, float l
	) {
		ItemStack itemStack = pandaEntity.getEquippedStack(EquipmentSlot.field_6173);
		if (pandaEntity.isScared() && !pandaEntity.isScaredByThunderstorm()) {
			float m = -0.6F;
			float n = 1.4F;
			if (pandaEntity.isEating()) {
				m -= 0.2F * MathHelper.sin(j * 0.6F) + 0.2F;
				n -= 0.09F * MathHelper.sin(j * 0.6F);
			}

			matrixStack.push();
			matrixStack.translate(0.1F, (double)n, (double)m);
			MinecraftClient.getInstance()
				.getHeldItemRenderer()
				.renderItem(pandaEntity, itemStack, ModelTransformation.Mode.field_4318, false, matrixStack, vertexConsumerProvider, i);
			matrixStack.pop();
		}
	}
}
