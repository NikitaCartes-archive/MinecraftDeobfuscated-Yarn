package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
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
		RenderSystem.pushMatrix();
		switch (shulkerEntity.getAttachedFace()) {
			case DOWN:
			default:
				break;
			case EAST:
				RenderSystem.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
				RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.translatef(1.0F, -1.0F, 0.0F);
				RenderSystem.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				break;
			case WEST:
				RenderSystem.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.translatef(-1.0F, -1.0F, 0.0F);
				RenderSystem.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				break;
			case NORTH:
				RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.translatef(0.0F, -1.0F, -1.0F);
				break;
			case SOUTH:
				RenderSystem.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.translatef(0.0F, -1.0F, 1.0F);
				break;
			case UP:
				RenderSystem.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.translatef(0.0F, -2.0F, 0.0F);
		}

		ModelPart modelPart = this.getModel().method_2830();
		modelPart.yaw = j * (float) (Math.PI / 180.0);
		modelPart.pitch = k * (float) (Math.PI / 180.0);
		DyeColor dyeColor = shulkerEntity.getColor();
		if (dyeColor == null) {
			this.bindTexture(ShulkerEntityRenderer.SKIN);
		} else {
			this.bindTexture(ShulkerEntityRenderer.SKIN_COLOR[dyeColor.getId()]);
		}

		modelPart.render(l);
		RenderSystem.popMatrix();
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
