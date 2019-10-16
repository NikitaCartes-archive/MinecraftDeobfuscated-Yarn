package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity> extends AnimalModel<T> {
	protected final ModelPart frontLegLeft;
	protected final ModelPart frontLegRight;
	protected final ModelPart backLegLeft;
	protected final ModelPart backLegRight;
	protected final ModelPart tail1;
	protected final ModelPart tail2;
	protected final ModelPart head;
	protected final ModelPart body;
	protected int animationState = 1;

	public OcelotEntityModel(float f) {
		super(true, 10.0F, 4.0F);
		this.head = new ModelPart(this);
		this.head.addCuboid("main", -2.5F, -2.0F, -3.0F, 5, 4, 5, f, 0, 0);
		this.head.addCuboid("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, f, 0, 24);
		this.head.addCuboid("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, f, 0, 10);
		this.head.addCuboid("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, f, 6, 10);
		this.head.setPivot(0.0F, 15.0F, -9.0F);
		this.body = new ModelPart(this, 20, 0);
		this.body.addCuboid(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, f);
		this.body.setPivot(0.0F, 12.0F, -10.0F);
		this.tail1 = new ModelPart(this, 0, 15);
		this.tail1.addCuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, f);
		this.tail1.pitch = 0.9F;
		this.tail1.setPivot(0.0F, 15.0F, 8.0F);
		this.tail2 = new ModelPart(this, 4, 15);
		this.tail2.addCuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, f);
		this.tail2.setPivot(0.0F, 20.0F, 14.0F);
		this.frontLegLeft = new ModelPart(this, 8, 13);
		this.frontLegLeft.addCuboid(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, f);
		this.frontLegLeft.setPivot(1.1F, 18.0F, 5.0F);
		this.frontLegRight = new ModelPart(this, 8, 13);
		this.frontLegRight.addCuboid(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, f);
		this.frontLegRight.setPivot(-1.1F, 18.0F, 5.0F);
		this.backLegLeft = new ModelPart(this, 40, 0);
		this.backLegLeft.addCuboid(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, f);
		this.backLegLeft.setPivot(1.2F, 14.1F, -5.0F);
		this.backLegRight = new ModelPart(this, 40, 0);
		this.backLegRight.addCuboid(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, f);
		this.backLegRight.setPivot(-1.2F, 14.1F, -5.0F);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.body, this.frontLegLeft, this.frontLegRight, this.backLegLeft, this.backLegRight, this.tail1, this.tail2);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		if (this.animationState != 3) {
			this.body.pitch = (float) (Math.PI / 2);
			if (this.animationState == 2) {
				this.frontLegLeft.pitch = MathHelper.cos(f * 0.6662F) * g;
				this.frontLegRight.pitch = MathHelper.cos(f * 0.6662F + 0.3F) * g;
				this.backLegLeft.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI + 0.3F) * g;
				this.backLegRight.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * g;
				this.tail2.pitch = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(f) * g;
			} else {
				this.frontLegLeft.pitch = MathHelper.cos(f * 0.6662F) * g;
				this.frontLegRight.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * g;
				this.backLegLeft.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * g;
				this.backLegRight.pitch = MathHelper.cos(f * 0.6662F) * g;
				if (this.animationState == 1) {
					this.tail2.pitch = 1.7278761F + (float) (Math.PI / 4) * MathHelper.cos(f) * g;
				} else {
					this.tail2.pitch = 1.7278761F + 0.47123894F * MathHelper.cos(f) * g;
				}
			}
		}
	}

	@Override
	public void animateModel(T entity, float f, float g, float h) {
		this.body.pivotY = 12.0F;
		this.body.pivotZ = -10.0F;
		this.head.pivotY = 15.0F;
		this.head.pivotZ = -9.0F;
		this.tail1.pivotY = 15.0F;
		this.tail1.pivotZ = 8.0F;
		this.tail2.pivotY = 20.0F;
		this.tail2.pivotZ = 14.0F;
		this.backLegLeft.pivotY = 14.1F;
		this.backLegLeft.pivotZ = -5.0F;
		this.backLegRight.pivotY = 14.1F;
		this.backLegRight.pivotZ = -5.0F;
		this.frontLegLeft.pivotY = 18.0F;
		this.frontLegLeft.pivotZ = 5.0F;
		this.frontLegRight.pivotY = 18.0F;
		this.frontLegRight.pivotZ = 5.0F;
		this.tail1.pitch = 0.9F;
		if (entity.isInSneakingPose()) {
			this.body.pivotY++;
			this.head.pivotY += 2.0F;
			this.tail1.pivotY++;
			this.tail2.pivotY += -4.0F;
			this.tail2.pivotZ += 2.0F;
			this.tail1.pitch = (float) (Math.PI / 2);
			this.tail2.pitch = (float) (Math.PI / 2);
			this.animationState = 0;
		} else if (entity.isSprinting()) {
			this.tail2.pivotY = this.tail1.pivotY;
			this.tail2.pivotZ += 2.0F;
			this.tail1.pitch = (float) (Math.PI / 2);
			this.tail2.pitch = (float) (Math.PI / 2);
			this.animationState = 2;
		} else {
			this.animationState = 1;
		}
	}
}
