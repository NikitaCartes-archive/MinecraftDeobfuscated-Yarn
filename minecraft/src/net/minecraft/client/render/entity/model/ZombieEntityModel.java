package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ZombieEntityModel extends BipedEntityModel {
	public ZombieEntityModel() {
		this(0.0F, false);
	}

	protected ZombieEntityModel(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	public ZombieEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		super.setRotationAngles(f, g, h, i, j, k, entity);
		boolean bl = entity instanceof ZombieEntity && ((ZombieEntity)entity).hasArmsRaised();
		float l = MathHelper.sin(this.swingProgress * (float) Math.PI);
		float m = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
		this.armRight.roll = 0.0F;
		this.armLeft.roll = 0.0F;
		this.armRight.yaw = -(0.1F - l * 0.6F);
		this.armLeft.yaw = 0.1F - l * 0.6F;
		float n = (float) -Math.PI / (bl ? 1.5F : 2.25F);
		this.armRight.pitch = n;
		this.armLeft.pitch = n;
		this.armRight.pitch += l * 1.2F - m * 0.4F;
		this.armLeft.pitch += l * 1.2F - m * 0.4F;
		this.armRight.roll = this.armRight.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
		this.armLeft.roll = this.armLeft.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
		this.armRight.pitch = this.armRight.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
		this.armLeft.pitch = this.armLeft.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
	}
}
