package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SmallPufferfishEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart field_3505;
	private final ModelPart field_3507;
	private final ModelPart field_3506;
	private final ModelPart field_3504;
	private final ModelPart field_3503;
	private final ModelPart field_3508;

	public SmallPufferfishEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 23;
		this.field_3505 = new ModelPart(this, 0, 27);
		this.field_3505.addCuboid(-1.5F, -2.0F, -1.5F, 3, 2, 3);
		this.field_3505.setPivot(0.0F, 23.0F, 0.0F);
		this.field_3507 = new ModelPart(this, 24, 6);
		this.field_3507.addCuboid(-1.5F, 0.0F, -1.5F, 1, 1, 1);
		this.field_3507.setPivot(0.0F, 20.0F, 0.0F);
		this.field_3506 = new ModelPart(this, 28, 6);
		this.field_3506.addCuboid(0.5F, 0.0F, -1.5F, 1, 1, 1);
		this.field_3506.setPivot(0.0F, 20.0F, 0.0F);
		this.field_3508 = new ModelPart(this, -3, 0);
		this.field_3508.addCuboid(-1.5F, 0.0F, 0.0F, 3, 0, 3);
		this.field_3508.setPivot(0.0F, 22.0F, 1.5F);
		this.field_3504 = new ModelPart(this, 25, 0);
		this.field_3504.addCuboid(-1.0F, 0.0F, 0.0F, 1, 0, 2);
		this.field_3504.setPivot(-1.5F, 22.0F, -1.5F);
		this.field_3503 = new ModelPart(this, 25, 0);
		this.field_3503.addCuboid(0.0F, 0.0F, 0.0F, 1, 0, 2);
		this.field_3503.setPivot(1.5F, 22.0F, -1.5F);
	}

	@Override
	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
		this.field_3505.render(scale);
		this.field_3507.render(scale);
		this.field_3506.render(scale);
		this.field_3508.render(scale);
		this.field_3504.render(scale);
		this.field_3503.render(scale);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.field_3504.roll = -0.2F + 0.4F * MathHelper.sin(age * 0.2F);
		this.field_3503.roll = 0.2F - 0.4F * MathHelper.sin(age * 0.2F);
	}
}
