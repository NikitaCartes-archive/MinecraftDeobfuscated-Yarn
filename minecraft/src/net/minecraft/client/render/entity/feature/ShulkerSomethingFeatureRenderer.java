package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.DyeColor;

@Environment(EnvType.CLIENT)
public class ShulkerSomethingFeatureRenderer extends FeatureRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
	public ShulkerSomethingFeatureRenderer(FeatureRendererContext<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4115(ShulkerEntity shulkerEntity, float f, float g, float h, float i, float j, float k, float l) {
		GlStateManager.pushMatrix();
		switch (shulkerEntity.getAttachedFace()) {
			case DOWN:
			default:
				break;
			case EAST:
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(1.0F, -1.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				break;
			case WEST:
				GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(-1.0F, -1.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				break;
			case NORTH:
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(0.0F, -1.0F, -1.0F);
				break;
			case SOUTH:
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(0.0F, -1.0F, 1.0F);
				break;
			case UP:
				GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(0.0F, -2.0F, 0.0F);
		}

		Cuboid cuboid = this.getModel().method_2830();
		cuboid.yaw = j * (float) (Math.PI / 180.0);
		cuboid.pitch = k * (float) (Math.PI / 180.0);
		DyeColor dyeColor = shulkerEntity.getColor();
		if (dyeColor == null) {
			this.bindTexture(ShulkerEntityRenderer.field_4781);
		} else {
			this.bindTexture(ShulkerEntityRenderer.SKIN[dyeColor.getId()]);
		}

		cuboid.render(l);
		GlStateManager.popMatrix();
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
