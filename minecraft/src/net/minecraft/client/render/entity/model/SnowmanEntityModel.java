package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SnowmanEntityModel<T extends Entity> extends class_4595<T> {
	private final ModelPart field_3567;
	private final ModelPart field_3569;
	private final ModelPart field_3568;
	private final ModelPart field_3566;
	private final ModelPart field_3565;

	public SnowmanEntityModel() {
		float f = 4.0F;
		float g = 0.0F;
		this.field_3568 = new ModelPart(this, 0, 0).setTextureSize(64, 64);
		this.field_3568.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, -0.5F);
		this.field_3568.setPivot(0.0F, 4.0F, 0.0F);
		this.field_3566 = new ModelPart(this, 32, 0).setTextureSize(64, 64);
		this.field_3566.addCuboid(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, -0.5F);
		this.field_3566.setPivot(0.0F, 6.0F, 0.0F);
		this.field_3565 = new ModelPart(this, 32, 0).setTextureSize(64, 64);
		this.field_3565.addCuboid(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, -0.5F);
		this.field_3565.setPivot(0.0F, 6.0F, 0.0F);
		this.field_3567 = new ModelPart(this, 0, 16).setTextureSize(64, 64);
		this.field_3567.addCuboid(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, -0.5F);
		this.field_3567.setPivot(0.0F, 13.0F, 0.0F);
		this.field_3569 = new ModelPart(this, 0, 36).setTextureSize(64, 64);
		this.field_3569.addCuboid(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, -0.5F);
		this.field_3569.setPivot(0.0F, 24.0F, 0.0F);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_3568.yaw = i * (float) (Math.PI / 180.0);
		this.field_3568.pitch = j * (float) (Math.PI / 180.0);
		this.field_3567.yaw = i * (float) (Math.PI / 180.0) * 0.25F;
		float l = MathHelper.sin(this.field_3567.yaw);
		float m = MathHelper.cos(this.field_3567.yaw);
		this.field_3566.roll = 1.0F;
		this.field_3565.roll = -1.0F;
		this.field_3566.yaw = 0.0F + this.field_3567.yaw;
		this.field_3565.yaw = (float) Math.PI + this.field_3567.yaw;
		this.field_3566.pivotX = m * 5.0F;
		this.field_3566.pivotZ = -l * 5.0F;
		this.field_3565.pivotX = -m * 5.0F;
		this.field_3565.pivotZ = l * 5.0F;
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_3567, this.field_3569, this.field_3568, this.field_3566, this.field_3565);
	}

	public ModelPart method_2834() {
		return this.field_3568;
	}
}
