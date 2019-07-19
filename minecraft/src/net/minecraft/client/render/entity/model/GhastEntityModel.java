package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
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
		this.field_3373.pivotY += 8.0F;
		Random random = new Random(1660L);

		for (int j = 0; j < this.field_3372.length; j++) {
			this.field_3372[j] = new ModelPart(this, 0, 0);
			float f = (((float)(j % 3) - (float)(j / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
			float g = ((float)(j / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
			int k = random.nextInt(7) + 8;
			this.field_3372[j].addCuboid(-1.0F, 0.0F, -1.0F, 2, k, 2);
			this.field_3372[j].pivotX = f;
			this.field_3372[j].pivotZ = g;
			this.field_3372[j].pivotY = 15.0F;
		}
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		for (int i = 0; i < this.field_3372.length; i++) {
			this.field_3372[i].pitch = 0.2F * MathHelper.sin(age * 0.3F + (float)i) + 0.4F;
		}
	}

	@Override
	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, 0.6F, 0.0F);
		this.field_3373.render(scale);

		for (ModelPart modelPart : this.field_3372) {
			modelPart.render(scale);
		}

		GlStateManager.popMatrix();
	}
}
