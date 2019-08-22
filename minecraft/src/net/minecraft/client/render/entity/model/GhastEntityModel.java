package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class GhastEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart field_3373;
	private final ModelPart[] field_3372 = new ModelPart[9];

	public GhastEntityModel() {
		int i = -16;
		this.field_3373 = new ModelPart(this, 0, 0);
		this.field_3373.addCuboid(-8.0F, -8.0F, -8.0F, 16, 16, 16);
		this.field_3373.rotationPointY += 8.0F;
		Random random = new Random(1660L);

		for (int j = 0; j < this.field_3372.length; j++) {
			this.field_3372[j] = new ModelPart(this, 0, 0);
			float f = (((float)(j % 3) - (float)(j / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
			float g = ((float)(j / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
			int k = random.nextInt(7) + 8;
			this.field_3372[j].addCuboid(-1.0F, 0.0F, -1.0F, 2, k, 2);
			this.field_3372[j].rotationPointX = f;
			this.field_3372[j].rotationPointZ = g;
			this.field_3372[j].rotationPointY = 15.0F;
		}
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		for (int l = 0; l < this.field_3372.length; l++) {
			this.field_3372[l].pitch = 0.2F * MathHelper.sin(h * 0.3F + (float)l) + 0.4F;
		}
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		RenderSystem.pushMatrix();
		RenderSystem.translatef(0.0F, 0.6F, 0.0F);
		this.field_3373.render(k);

		for (ModelPart modelPart : this.field_3372) {
			modelPart.render(k);
		}

		RenderSystem.popMatrix();
	}
}
