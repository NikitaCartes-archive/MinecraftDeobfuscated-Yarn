package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class EndCrystalEntityModel<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_3640;
	private final Cuboid field_3642 = new Cuboid(this, "glass");
	private final Cuboid field_3641;

	public EndCrystalEntityModel(float f, boolean bl) {
		this.field_3642.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		this.field_3640 = new Cuboid(this, "cube");
		this.field_3640.setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		if (bl) {
			this.field_3641 = new Cuboid(this, "base");
			this.field_3641.setTextureOffset(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12);
		} else {
			this.field_3641 = null;
		}
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		GlStateManager.pushMatrix();
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		GlStateManager.translatef(0.0F, -0.5F, 0.0F);
		if (this.field_3641 != null) {
			this.field_3641.render(k);
		}

		GlStateManager.rotatef(g, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, 0.8F + h, 0.0F);
		GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		this.field_3642.render(k);
		float l = 0.875F;
		GlStateManager.scalef(0.875F, 0.875F, 0.875F);
		GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotatef(g, 0.0F, 1.0F, 0.0F);
		this.field_3642.render(k);
		GlStateManager.scalef(0.875F, 0.875F, 0.875F);
		GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotatef(g, 0.0F, 1.0F, 0.0F);
		this.field_3640.render(k);
		GlStateManager.popMatrix();
	}
}
