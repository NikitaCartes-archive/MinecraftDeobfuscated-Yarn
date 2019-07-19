package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class SnowmanPumpkinFeatureRenderer extends FeatureRenderer<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> {
	public SnowmanPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> context) {
		super(context);
	}

	public void render(SnowGolemEntity snowGolemEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!snowGolemEntity.isInvisible() && snowGolemEntity.hasPumpkin()) {
			GlStateManager.pushMatrix();
			this.getContextModel().method_2834().applyTransform(0.0625F);
			float m = 0.625F;
			GlStateManager.translatef(0.0F, -0.34375F, 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scalef(0.625F, -0.625F, -0.625F);
			MinecraftClient.getInstance().getHeldItemRenderer().renderItem(snowGolemEntity, new ItemStack(Blocks.CARVED_PUMPKIN), ModelTransformation.Type.HEAD);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
