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
	public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntity, PandaEntityModel<PandaEntity>> context) {
		super(context);
	}

	public void method_4194(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		PandaEntity pandaEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		ItemStack itemStack = pandaEntity.getEquippedStack(EquipmentSlot.MAINHAND);
		if (pandaEntity.isScared() && !pandaEntity.method_6524()) {
			float n = -0.6F;
			float o = 1.4F;
			if (pandaEntity.isEating()) {
				n -= 0.2F * MathHelper.sin(j * 0.6F) + 0.2F;
				o -= 0.09F * MathHelper.sin(j * 0.6F);
			}

			matrixStack.push();
			matrixStack.translate(0.1F, (double)o, (double)n);
			MinecraftClient.getInstance()
				.getFirstPersonRenderer()
				.renderItem(pandaEntity, itemStack, ModelTransformation.Type.GROUND, false, matrixStack, vertexConsumerProvider);
			matrixStack.pop();
		}
	}
}
