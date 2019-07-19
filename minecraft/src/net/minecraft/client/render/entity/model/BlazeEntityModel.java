package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BlazeEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart[] field_3328 = new ModelPart[12];
	private final ModelPart field_3329;

	public BlazeEntityModel() {
		for (int i = 0; i < this.field_3328.length; i++) {
			this.field_3328[i] = new ModelPart(this, 0, 16);
			this.field_3328[i].addCuboid(0.0F, 0.0F, 0.0F, 2, 8, 2);
		}

		this.field_3329 = new ModelPart(this, 0, 0);
		this.field_3329.addCuboid(-4.0F, -4.0F, -4.0F, 8, 8, 8);
	}

	@Override
	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
		this.field_3329.render(scale);

		for (ModelPart modelPart : this.field_3328) {
			modelPart.render(scale);
		}
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		float f = age * (float) Math.PI * -0.1F;

		for (int i = 0; i < 4; i++) {
			this.field_3328[i].pivotY = -2.0F + MathHelper.cos(((float)(i * 2) + age) * 0.25F);
			this.field_3328[i].pivotX = MathHelper.cos(f) * 9.0F;
			this.field_3328[i].pivotZ = MathHelper.sin(f) * 9.0F;
			f++;
		}

		f = (float) (Math.PI / 4) + age * (float) Math.PI * 0.03F;

		for (int i = 4; i < 8; i++) {
			this.field_3328[i].pivotY = 2.0F + MathHelper.cos(((float)(i * 2) + age) * 0.25F);
			this.field_3328[i].pivotX = MathHelper.cos(f) * 7.0F;
			this.field_3328[i].pivotZ = MathHelper.sin(f) * 7.0F;
			f++;
		}

		f = 0.47123894F + age * (float) Math.PI * -0.05F;

		for (int i = 8; i < 12; i++) {
			this.field_3328[i].pivotY = 11.0F + MathHelper.cos(((float)i * 1.5F + age) * 0.5F);
			this.field_3328[i].pivotX = MathHelper.cos(f) * 5.0F;
			this.field_3328[i].pivotZ = MathHelper.sin(f) * 5.0F;
			f++;
		}

		this.field_3329.yaw = headYaw * (float) (Math.PI / 180.0);
		this.field_3329.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
