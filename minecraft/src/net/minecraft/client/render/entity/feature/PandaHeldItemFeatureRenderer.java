package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer extends FeatureRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
	public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntity, PandaEntityModel<PandaEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4194(PandaEntity pandaEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = pandaEntity.method_6118(EquipmentSlot.HAND_MAIN);
		if (pandaEntity.method_6535() && !itemStack.isEmpty() && !pandaEntity.method_6524()) {
			float m = -0.6F;
			float n = 1.4F;
			if (pandaEntity.method_6527()) {
				m -= 0.2F * MathHelper.sin(i * 0.6F) + 0.2F;
				n -= 0.09F * MathHelper.sin(i * 0.6F);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.1F, n, m);
			MinecraftClient.getInstance().method_1480().renderHeldItem(itemStack, pandaEntity, ModelTransformation.Type.field_4318, false);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
