package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity> extends AnimalModel<T> {
	protected ModelPart head = new ModelPart(this, 0, 0);
	protected ModelPart torso;
	protected ModelPart backRightLeg;
	protected ModelPart backLeftLeg;
	protected ModelPart frontRightLeg;
	protected ModelPart frontLeftLeg;

	public QuadrupedEntityModel(
		int legHeight,
		float scale,
		boolean headScaled,
		float childHeadYOffset,
		float childHeadZOffset,
		float invertedChildHeadScale,
		float invertedChildBodyScale,
		int childBodyYOffset
	) {
		super(headScaled, childHeadYOffset, childHeadZOffset, invertedChildHeadScale, invertedChildBodyScale, (float)childBodyYOffset);
		this.head.addCuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, scale);
		this.head.setPivot(0.0F, (float)(18 - legHeight), -6.0F);
		this.torso = new ModelPart(this, 28, 8);
		this.torso.addCuboid(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, scale);
		this.torso.setPivot(0.0F, (float)(17 - legHeight), 2.0F);
		this.backRightLeg = new ModelPart(this, 0, 16);
		this.backRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)legHeight, 4.0F, scale);
		this.backRightLeg.setPivot(-3.0F, (float)(24 - legHeight), 7.0F);
		this.backLeftLeg = new ModelPart(this, 0, 16);
		this.backLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)legHeight, 4.0F, scale);
		this.backLeftLeg.setPivot(3.0F, (float)(24 - legHeight), 7.0F);
		this.frontRightLeg = new ModelPart(this, 0, 16);
		this.frontRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)legHeight, 4.0F, scale);
		this.frontRightLeg.setPivot(-3.0F, (float)(24 - legHeight), -5.0F);
		this.frontLeftLeg = new ModelPart(this, 0, 16);
		this.frontLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)legHeight, 4.0F, scale);
		this.frontLeftLeg.setPivot(3.0F, (float)(24 - legHeight), -5.0F);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.backRightLeg, this.backLeftLeg, this.frontRightLeg, this.frontLeftLeg);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.torso.pitch = (float) (Math.PI / 2);
		this.backRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		this.backLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.frontRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.frontLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}
}
