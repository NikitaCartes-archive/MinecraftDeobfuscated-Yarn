package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.VillagerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class class_4004<T extends LivingEntity> extends FeatureRenderer<T, VillagerEntityModel<T>> {
	private final ItemRenderer field_17893 = MinecraftClient.getInstance().getItemRenderer();

	public class_4004(FeatureRendererContext<T, VillagerEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_18147(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HAND_MAIN);
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			Block block = Block.getBlockFromItem(item);
			GlStateManager.pushMatrix();
			boolean bl = this.field_17893.hasDepthInGui(itemStack) && block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
			if (bl) {
				GlStateManager.depthMask(false);
			}

			GlStateManager.translatef(0.0F, 0.4F, -0.4F);
			GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
			this.field_17893.renderHeldItem(itemStack, livingEntity, ModelTransformation.Type.field_4318, false);
			if (bl) {
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
