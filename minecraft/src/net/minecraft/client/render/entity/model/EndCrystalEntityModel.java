package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class EndCrystalEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart cube;
	private final ModelPart glass = new ModelPart(this);
	private final ModelPart base;

	public EndCrystalEntityModel(float f, boolean bl) {
		this.glass.setTextureOffset(0, 0).addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		this.cube = new ModelPart(this);
		this.cube.setTextureOffset(32, 0).addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		if (bl) {
			this.base = new ModelPart(this);
			this.base.setTextureOffset(0, 16).addCuboid(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F);
		} else {
			this.base = null;
		}
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		RenderSystem.pushMatrix();
		RenderSystem.scalef(2.0F, 2.0F, 2.0F);
		RenderSystem.translatef(0.0F, -0.5F, 0.0F);
		if (this.base != null) {
			this.base.render(k);
		}

		RenderSystem.rotatef(g, 0.0F, 1.0F, 0.0F);
		RenderSystem.translatef(0.0F, 0.8F + h, 0.0F);
		RenderSystem.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		this.glass.render(k);
		float l = 0.875F;
		RenderSystem.scalef(0.875F, 0.875F, 0.875F);
		RenderSystem.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		RenderSystem.rotatef(g, 0.0F, 1.0F, 0.0F);
		this.glass.render(k);
		RenderSystem.scalef(0.875F, 0.875F, 0.875F);
		RenderSystem.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		RenderSystem.rotatef(g, 0.0F, 1.0F, 0.0F);
		this.cube.render(k);
		RenderSystem.popMatrix();
	}
}
