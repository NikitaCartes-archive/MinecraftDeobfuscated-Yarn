package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class class_3968<T extends HostileEntity> extends BipedEntityModel<T> {
	protected class_3968(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	public void method_17791(T hostileEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(hostileEntity, f, g, h, i, j, k);
		boolean bl = this.method_17790(hostileEntity);
		float l = MathHelper.sin(this.swingProgress * (float) Math.PI);
		float m = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
		this.field_3401.roll = 0.0F;
		this.field_3390.roll = 0.0F;
		this.field_3401.yaw = -(0.1F - l * 0.6F);
		this.field_3390.yaw = 0.1F - l * 0.6F;
		float n = (float) -Math.PI / (bl ? 1.5F : 2.25F);
		this.field_3401.pitch = n;
		this.field_3390.pitch = n;
		this.field_3401.pitch += l * 1.2F - m * 0.4F;
		this.field_3390.pitch += l * 1.2F - m * 0.4F;
		this.field_3401.roll = this.field_3401.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
		this.field_3390.roll = this.field_3390.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
		this.field_3401.pitch = this.field_3401.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
		this.field_3390.pitch = this.field_3390.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
	}

	public abstract boolean method_17790(T hostileEntity);
}
