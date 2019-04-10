package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.passive.SnowmanEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class SnowmanPumpkinFeatureRenderer extends FeatureRenderer<SnowmanEntity, SnowmanEntityModel<SnowmanEntity>> {
	public SnowmanPumpkinFeatureRenderer(FeatureRendererContext<SnowmanEntity, SnowmanEntityModel<SnowmanEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4201(SnowmanEntity snowmanEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!snowmanEntity.isInvisible() && snowmanEntity.hasPumpkin()) {
			GlStateManager.pushMatrix();
			this.getModel().method_2834().applyTransform(0.0625F);
			float m = 0.625F;
			GlStateManager.translatef(0.0F, -0.34375F, 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scalef(0.625F, -0.625F, -0.625F);
			MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(snowmanEntity, new ItemStack(Blocks.field_10147), ModelTransformation.Type.field_4316);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
