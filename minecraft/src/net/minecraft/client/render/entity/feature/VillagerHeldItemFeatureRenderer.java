package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, VillagerResemblingModel<T>> {
	private final ItemRenderer itemRenderer = MinecraftClient.getInstance().method_1480();

	public VillagerHeldItemFeatureRenderer(FeatureRendererContext<T, VillagerResemblingModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_18147(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.field_6173);
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			Block block = Block.getBlockFromItem(item);
			GlStateManager.pushMatrix();
			boolean bl = this.itemRenderer.hasDepthInGui(itemStack) && block.getRenderLayer() == BlockRenderLayer.field_9179;
			if (bl) {
				GlStateManager.depthMask(false);
			}

			GlStateManager.translatef(0.0F, 0.4F, -0.4F);
			GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
			this.itemRenderer.renderHeldItem(itemStack, livingEntity, ModelTransformation.Type.field_4318, false);
			if (bl) {
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
