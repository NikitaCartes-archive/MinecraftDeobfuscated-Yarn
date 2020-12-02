package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity> extends AnimalModel<T> {
	protected final ModelPart leftHindLeg;
	protected final ModelPart rightHindLeg;
	protected final ModelPart leftFrontLeg;
	protected final ModelPart rightFrontLeg;
	protected final ModelPart upperTail;
	protected final ModelPart lowerTail;
	protected final ModelPart head;
	protected final ModelPart body;
	protected int animationState = 1;

	public OcelotEntityModel(ModelPart root) {
		super(true, 10.0F, 4.0F);
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.upperTail = root.getChild("tail1");
		this.lowerTail = root.getChild("tail2");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
	}

	public static ModelData getModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			"head",
			ModelPartBuilder.create()
				.cuboid("main", -2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, dilation)
				.cuboid("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, dilation, 0, 24)
				.cuboid("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, dilation, 0, 10)
				.cuboid("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, dilation, 6, 10),
			ModelTransform.pivot(0.0F, 15.0F, -9.0F)
		);
		modelPartData.addChild(
			"body",
			ModelPartBuilder.create().uv(20, 0).cuboid(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, dilation),
			ModelTransform.of(0.0F, 12.0F, -10.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"tail1", ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, dilation), ModelTransform.of(0.0F, 15.0F, 8.0F, 0.9F, 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"tail2", ModelPartBuilder.create().uv(4, 15).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, dilation), ModelTransform.pivot(0.0F, 20.0F, 14.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(8, 13).cuboid(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, dilation);
		modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(1.1F, 18.0F, 5.0F));
		modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-1.1F, 18.0F, 5.0F));
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(40, 0).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, dilation);
		modelPartData.addChild("left_front_leg", modelPartBuilder2, ModelTransform.pivot(1.2F, 14.1F, -5.0F));
		modelPartData.addChild("right_front_leg", modelPartBuilder2, ModelTransform.pivot(-1.2F, 14.1F, -5.0F));
		return modelData;
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.body, this.leftHindLeg, this.rightHindLeg, this.leftFrontLeg, this.rightFrontLeg, this.upperTail, this.lowerTail);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		if (this.animationState != 3) {
			this.body.pitch = (float) (Math.PI / 2);
			if (this.animationState == 2) {
				this.leftHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				this.rightHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 0.3F) * limbDistance;
				this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI + 0.3F) * limbDistance;
				this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
				this.lowerTail.pitch = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(limbAngle) * limbDistance;
			} else {
				this.leftHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				this.rightHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
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
		this.body.pivotY = 12.0F;
		this.body.pivotZ = -10.0F;
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
		this.leftHindLeg.pivotY = 18.0F;
		this.leftHindLeg.pivotZ = 5.0F;
		this.rightHindLeg.pivotY = 18.0F;
		this.rightHindLeg.pivotZ = 5.0F;
		this.upperTail.pitch = 0.9F;
		if (entity.isInSneakingPose()) {
			this.body.pivotY++;
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
