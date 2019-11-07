package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DolphinEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart field_4658;
	private final ModelPart field_4657;
	private final ModelPart field_4655;

	public DolphinEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		float f = 18.0F;
		float g = -8.0F;
		this.field_4658 = new ModelPart(this, 22, 0);
		this.field_4658.addCuboid(-4.0F, -7.0F, 0.0F, 8.0F, 7.0F, 13.0F);
		this.field_4658.setPivot(0.0F, 22.0F, -5.0F);
		ModelPart modelPart = new ModelPart(this, 51, 0);
		modelPart.addCuboid(-0.5F, 0.0F, 8.0F, 1.0F, 4.0F, 5.0F);
		modelPart.pitch = (float) (Math.PI / 3);
		this.field_4658.addChild(modelPart);
		ModelPart modelPart2 = new ModelPart(this, 48, 20);
		modelPart2.mirror = true;
		modelPart2.addCuboid(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F);
		modelPart2.setPivot(2.0F, -2.0F, 4.0F);
		modelPart2.pitch = (float) (Math.PI / 3);
		modelPart2.roll = (float) (Math.PI * 2.0 / 3.0);
		this.field_4658.addChild(modelPart2);
		ModelPart modelPart3 = new ModelPart(this, 48, 20);
		modelPart3.addCuboid(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F);
		modelPart3.setPivot(-2.0F, -2.0F, 4.0F);
		modelPart3.pitch = (float) (Math.PI / 3);
		modelPart3.roll = (float) (-Math.PI * 2.0 / 3.0);
		this.field_4658.addChild(modelPart3);
		this.field_4657 = new ModelPart(this, 0, 19);
		this.field_4657.addCuboid(-2.0F, -2.5F, 0.0F, 4.0F, 5.0F, 11.0F);
		this.field_4657.setPivot(0.0F, -2.5F, 11.0F);
		this.field_4657.pitch = -0.10471976F;
		this.field_4658.addChild(this.field_4657);
		this.field_4655 = new ModelPart(this, 19, 20);
		this.field_4655.addCuboid(-5.0F, -0.5F, 0.0F, 10.0F, 1.0F, 6.0F);
		this.field_4655.setPivot(0.0F, 0.0F, 9.0F);
		this.field_4655.pitch = 0.0F;
		this.field_4657.addChild(this.field_4655);
		ModelPart modelPart4 = new ModelPart(this, 0, 0);
		modelPart4.addCuboid(-4.0F, -3.0F, -3.0F, 8.0F, 7.0F, 6.0F);
		modelPart4.setPivot(0.0F, -4.0F, -3.0F);
		ModelPart modelPart5 = new ModelPart(this, 0, 13);
		modelPart5.addCuboid(-1.0F, 2.0F, -7.0F, 2.0F, 2.0F, 4.0F);
		modelPart4.addChild(modelPart5);
		this.field_4658.addChild(modelPart4);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_4658);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		this.field_4658.pitch = headPitch * (float) (Math.PI / 180.0);
		this.field_4658.yaw = headYaw * (float) (Math.PI / 180.0);
		if (Entity.squaredHorizontalLength(entity.getVelocity()) > 1.0E-7) {
			this.field_4658.pitch = this.field_4658.pitch + -0.05F + -0.05F * MathHelper.cos(age * 0.3F);
			this.field_4657.pitch = -0.1F * MathHelper.cos(age * 0.3F);
			this.field_4655.pitch = -0.2F * MathHelper.cos(age * 0.3F);
		}
	}
}
