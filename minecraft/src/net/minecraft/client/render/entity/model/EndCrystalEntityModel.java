package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class EndCrystalEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart cube;
	private final ModelPart glass = new ModelPart(this, "glass");
	private final ModelPart base;

	public EndCrystalEntityModel(float f, boolean bl) {
		this.glass.setTextureOffset(0, 0).addCuboid(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		this.cube = new ModelPart(this, "cube");
		this.cube.setTextureOffset(32, 0).addCuboid(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		if (bl) {
			this.base = new ModelPart(this, "base");
			this.base.setTextureOffset(0, 16).addCuboid(-6.0F, 0.0F, -6.0F, 12, 4, 12);
		} else {
			this.base = null;
		}
	}

	@Override
	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		GlStateManager.pushMatrix();
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		GlStateManager.translatef(0.0F, -0.5F, 0.0F);
		if (this.base != null) {
			this.base.render(scale);
		}

		GlStateManager.rotatef(limbDistance, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, 0.8F + age, 0.0F);
		GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		this.glass.render(scale);
		float f = 0.875F;
		GlStateManager.scalef(0.875F, 0.875F, 0.875F);
		GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotatef(limbDistance, 0.0F, 1.0F, 0.0F);
		this.glass.render(scale);
		GlStateManager.scalef(0.875F, 0.875F, 0.875F);
		GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotatef(limbDistance, 0.0F, 1.0F, 0.0F);
		this.cube.render(scale);
		GlStateManager.popMatrix();
	}
}
