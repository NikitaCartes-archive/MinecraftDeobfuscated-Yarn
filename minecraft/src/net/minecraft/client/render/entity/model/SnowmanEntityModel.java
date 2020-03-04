package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SnowmanEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart middleSnowball;
	private final ModelPart bottomSnowball;
	private final ModelPart topSnowball;
	private final ModelPart leftArm;
	private final ModelPart rightArm;

	public SnowmanEntityModel() {
		float f = 4.0F;
		float g = 0.0F;
		this.topSnowball = new ModelPart(this, 0, 0).setTextureSize(64, 64);
		this.topSnowball.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, -0.5F);
		this.topSnowball.setPivot(0.0F, 4.0F, 0.0F);
		this.leftArm = new ModelPart(this, 32, 0).setTextureSize(64, 64);
		this.leftArm.addCuboid(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, -0.5F);
		this.leftArm.setPivot(0.0F, 6.0F, 0.0F);
		this.rightArm = new ModelPart(this, 32, 0).setTextureSize(64, 64);
		this.rightArm.addCuboid(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, -0.5F);
		this.rightArm.setPivot(0.0F, 6.0F, 0.0F);
		this.middleSnowball = new ModelPart(this, 0, 16).setTextureSize(64, 64);
		this.middleSnowball.addCuboid(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, -0.5F);
		this.middleSnowball.setPivot(0.0F, 13.0F, 0.0F);
		this.bottomSnowball = new ModelPart(this, 0, 36).setTextureSize(64, 64);
		this.bottomSnowball.addCuboid(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, -0.5F);
		this.bottomSnowball.setPivot(0.0F, 24.0F, 0.0F);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
		this.topSnowball.yaw = headYaw * (float) (Math.PI / 180.0);
		this.topSnowball.pitch = headPitch * (float) (Math.PI / 180.0);
		this.middleSnowball.yaw = headYaw * (float) (Math.PI / 180.0) * 0.25F;
		float f = MathHelper.sin(this.middleSnowball.yaw);
		float g = MathHelper.cos(this.middleSnowball.yaw);
		this.leftArm.roll = 1.0F;
		this.rightArm.roll = -1.0F;
		this.leftArm.yaw = 0.0F + this.middleSnowball.yaw;
		this.rightArm.yaw = (float) Math.PI + this.middleSnowball.yaw;
		this.leftArm.pivotX = g * 5.0F;
		this.leftArm.pivotZ = -f * 5.0F;
		this.rightArm.pivotX = -g * 5.0F;
		this.rightArm.pivotZ = f * 5.0F;
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.middleSnowball, this.bottomSnowball, this.topSnowball, this.leftArm, this.rightArm);
	}

	public ModelPart getTopSnowball() {
		return this.topSnowball;
	}
}
