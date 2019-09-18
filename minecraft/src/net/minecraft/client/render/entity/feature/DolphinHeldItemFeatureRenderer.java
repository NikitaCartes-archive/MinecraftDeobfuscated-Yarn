package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer extends FeatureRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
	private final ItemRenderer field_4847 = MinecraftClient.getInstance().getItemRenderer();

	public DolphinHeldItemFeatureRenderer(FeatureRendererContext<DolphinEntity, DolphinEntityModel<DolphinEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_17160(DolphinEntity dolphinEntity, float f, float g, float h, float i, float j, float k, float l) {
		boolean bl = dolphinEntity.getMainArm() == Arm.RIGHT;
		ItemStack itemStack = bl ? dolphinEntity.getOffHandStack() : dolphinEntity.getMainHandStack();
		ItemStack itemStack2 = bl ? dolphinEntity.getMainHandStack() : dolphinEntity.getOffHandStack();
		if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
			this.method_4180(dolphinEntity, itemStack2);
		}
	}

	private void method_4180(LivingEntity livingEntity, ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			Block block = Block.getBlockFromItem(item);
			RenderSystem.pushMatrix();
			boolean bl = this.field_4847.hasDepthInGui(itemStack) && BlockRenderLayer.method_22715(block.getDefaultState()) == BlockRenderLayer.field_9179;
			if (bl) {
				RenderSystem.depthMask(false);
			}

			float f = 1.0F;
			float g = -1.0F;
			float h = MathHelper.abs(livingEntity.pitch) / 60.0F;
			if (livingEntity.pitch < 0.0F) {
				RenderSystem.translatef(0.0F, 1.0F - h * 0.5F, -1.0F + h * 0.5F);
			} else {
				RenderSystem.translatef(0.0F, 1.0F + h * 0.8F, -1.0F + h * 0.2F);
			}

			this.field_4847.renderHeldItem(itemStack, livingEntity, ModelTransformation.Type.GROUND, false);
			if (bl) {
				RenderSystem.depthMask(true);
			}

			RenderSystem.popMatrix();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
