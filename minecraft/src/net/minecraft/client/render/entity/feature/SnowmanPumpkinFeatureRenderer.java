package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
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
	public SnowmanPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4201(SnowGolemEntity snowGolemEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!snowGolemEntity.isInvisible() && snowGolemEntity.hasPumpkin()) {
			RenderSystem.pushMatrix();
			this.getModel().method_2834().applyTransform(0.0625F);
			float m = 0.625F;
			RenderSystem.translatef(0.0F, -0.34375F, 0.0F);
			RenderSystem.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			RenderSystem.scalef(0.625F, -0.625F, -0.625F);
			MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(snowGolemEntity, new ItemStack(Blocks.CARVED_PUMPKIN), ModelTransformation.Type.HEAD);
			RenderSystem.popMatrix();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
