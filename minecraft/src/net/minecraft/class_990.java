package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LayerEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_990 implements LayerEntityRenderer<PandaEntity> {
	protected final LivingEntityRenderer<?> field_4884;
	private final ItemRenderer field_4883;

	public class_990(LivingEntityRenderer<?> livingEntityRenderer) {
		this.field_4884 = livingEntityRenderer;
		this.field_4883 = MinecraftClient.getInstance().getItemRenderer();
	}

	public void render(PandaEntity pandaEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = pandaEntity.getEquippedStack(EquipmentSlot.HAND_MAIN);
		if (pandaEntity.method_6535() && !itemStack.isEmpty() && !pandaEntity.method_6524()) {
			float m = -0.6F;
			float n = 1.4F;
			if (pandaEntity.method_6527()) {
				m -= 0.2F * MathHelper.sin(i * 0.6F) + 0.2F;
				n -= 0.09F * MathHelper.sin(i * 0.6F);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.1F, n, m);
			this.field_4883.renderItemAmountAndDamageInGUI(itemStack, pandaEntity, ModelTransformations.Type.GROUND, false);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
