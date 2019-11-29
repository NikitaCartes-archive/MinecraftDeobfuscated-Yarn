package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity> extends AnimalModel<T> {
	protected final ModelPart leftBackLeg;
	protected final ModelPart rightBackLeg;
	protected final ModelPart leftFrontLeg;
	protected final ModelPart rightFrontLeg;
	protected final ModelPart upperTail;
	protected final ModelPart lowerTail;
	protected final ModelPart head;
	protected final ModelPart torso;
	protected int animationState = 1;

	public OcelotEntityModel(float scale) {
		super(true, 10.0F, 4.0F);
		this.head = new ModelPart(this);
		this.head.addCuboid("main", -2.5F, -2.0F, -3.0F, 5, 4, 5, scale, 0, 0);
		this.head.addCuboid("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, scale, 0, 24);
		this.head.addCuboid("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, scale, 0, 10);
		this.head.addCuboid("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, scale, 6, 10);
		this.head.setPivot(0.0F, 15.0F, -9.0F);
		this.torso = new ModelPart(this, 20, 0);
		this.torso.addCuboid(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, scale);
		this.torso.setPivot(0.0F, 12.0F, -10.0F);
		this.upperTail = new ModelPart(this, 0, 15);
		this.upperTail.addCuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, scale);
		this.upperTail.pitch = 0.9F;
		this.upperTail.setPivot(0.0F, 15.0F, 8.0F);
		this.lowerTail = new ModelPart(this, 4, 15);
		this.lowerTail.addCuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, scale);
		this.lowerTail.setPivot(0.0F, 20.0F, 14.0F);
		this.leftBackLeg = new ModelPart(this, 8, 13);
		this.leftBackLeg.addCuboid(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, scale);
		this.leftBackLeg.setPivot(1.1F, 18.0F, 5.0F);
		this.rightBackLeg = new ModelPart(this, 8, 13);
		this.rightBackLeg.addCuboid(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, scale);
		this.rightBackLeg.setPivot(-1.1F, 18.0F, 5.0F);
		this.leftFrontLeg = new ModelPart(this, 40, 0);
		this.leftFrontLeg.addCuboid(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, scale);
		this.leftFrontLeg.setPivot(1.2F, 14.1F, -5.0F);
		this.rightFrontLeg = new ModelPart(this, 40, 0);
		this.rightFrontLeg.addCuboid(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, scale);
		this.rightFrontLeg.setPivot(-1.2F, 14.1F, -5.0F);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.leftBackLeg, this.rightBackLeg, this.leftFrontLeg, this.rightFrontLeg, this.upperTail, this.lowerTail);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		if (this.animationState != 3) {
			this.torso.pitch = (float) (Math.PI / 2);
			if (this.animationState == 2) {
				this.leftBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				this.rightBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 0.3F) * limbDistance;
				this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI + 0.3F) * limbDistance;
				this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
				this.lowerTail.pitch = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(limbAngle) * limbDistance;
			} else {
				this.leftBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				this.rightBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
				this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
				this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				if (this.animationState == 1) {
					this.lowerTail.pitch = 1.7278761F + (float) (Math.PI / 4) * MathHelper.cos(limbAngle) * limbDistance;
				} else {
					this.lowerTail.pitch = 1.7278761F + 0.47123894F * MathHelper.cos(limbAngle) * limbDistance;
				}
			}
		}
	}

	@Override
	public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
		this.torso.pivotY = 12.0F;
		this.torso.pivotZ = -10.0F;
		this.head.pivotY = 15.0F;
		this.head.pivotZ = -9.0F;
		this.upperTail.pivotY = 15.0F;
		this.upperTail.pivotZ = 8.0F;
		this.lowerTail.pivotY = 20.0F;
		this.lowerTail.pivotZ = 14.0F;
		this.leftFrontLeg.pivotY = 14.1F;
		this.leftFrontLeg.pivotZ = -5.0F;
		this.rightFrontLeg.pivotY = 14.1F;
		this.rightFrontLeg.pivotZ = -5.0F;
		this.leftBackLeg.pivotY = 18.0F;
		this.leftBackLeg.pivotZ = 5.0F;
		this.rightBackLeg.pivotY = 18.0F;
		this.rightBackLeg.pivotZ = 5.0F;
		this.upperTail.pitch = 0.9F;
		if (entity.isInSneakingPose()) {
			this.torso.pivotY++;
			this.head.pivotY += 2.0F;
			this.upperTail.pivotY++;
			this.lowerTail.pivotY += -4.0F;
			this.lowerTail.pivotZ += 2.0F;
			this.upperTail.pitch = (float) (Math.PI / 2);
			this.lowerTail.pitch = (float) (Math.PI / 2);
			this.animationState = 0;
		} else if (entity.isSprinting()) {
			this.lowerTail.pivotY = this.upperTail.pivotY;
			this.lowerTail.pivotZ += 2.0F;
			this.upperTail.pitch = (float) (Math.PI / 2);
			this.lowerTail.pitch = (float) (Math.PI / 2);
			this.animationState = 2;
		} else {
			this.animationState = 1;
		}
	}
}
