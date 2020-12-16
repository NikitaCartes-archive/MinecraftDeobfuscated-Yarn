package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity> extends TintableAnimalModel<T> {
	/**
	 * The main bone used to animate the head. Contains {@link #realHead} as one of its children.
	 */
	private final ModelPart head;
	private final ModelPart realHead;
	private final ModelPart torso;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	/**
	 * The main bone used to animate the tail. Contains {@link #realTail} as one of its children.
	 */
	private final ModelPart tail;
	private final ModelPart realTail;
	private final ModelPart neck;

	public WolfEntityModel(ModelPart root) {
		this.head = root.getChild("head");
		this.realHead = this.head.getChild("real_head");
		this.torso = root.getChild("body");
		this.neck = root.getChild("upper_body");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.tail = root.getChild("tail");
		this.realTail = this.tail.getChild("real_tail");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = 13.5F;
		ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, 13.5F, -7.0F));
		modelPartData2.addChild(
			"real_head",
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F)
				.uv(16, 14)
				.cuboid(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F)
				.uv(16, 14)
				.cuboid(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F)
				.uv(0, 10)
				.cuboid(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F),
			ModelTransform.NONE
		);
		modelPartData.addChild(
			"body",
			ModelPartBuilder.create().uv(18, 14).cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F),
			ModelTransform.of(0.0F, 14.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"upper_body",
			ModelPartBuilder.create().uv(21, 0).cuboid(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F),
			ModelTransform.of(-1.0F, 14.0F, -3.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 18).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F);
		modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-2.5F, 16.0F, 7.0F));
		modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(0.5F, 16.0F, 7.0F));
		modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-2.5F, 16.0F, -4.0F));
		modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(0.5F, 16.0F, -4.0F));
		ModelPartData modelPartData3 = modelPartData.addChild(
			"tail", ModelPartBuilder.create(), ModelTransform.of(-1.0F, 12.0F, 8.0F, (float) (Math.PI / 5), 0.0F, 0.0F)
		);
		modelPartData3.addChild("real_tail", ModelPartBuilder.create().uv(9, 18).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail, this.neck);
	}

	public void animateModel(T wolfEntity, float f, float g, float h) {
		if (wolfEntity.hasAngerTime()) {
			this.tail.yaw = 0.0F;
		} else {
			this.tail.yaw = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		if (wolfEntity.isInSittingPose()) {
			this.neck.setPivot(-1.0F, 16.0F, -3.0F);
			this.neck.pitch = (float) (Math.PI * 2.0 / 5.0);
			this.neck.yaw = 0.0F;
			this.torso.setPivot(0.0F, 18.0F, 0.0F);
			this.torso.pitch = (float) (Math.PI / 4);
			this.tail.setPivot(-1.0F, 21.0F, 6.0F);
			this.rightHindLeg.setPivot(-2.5F, 22.7F, 2.0F);
			this.rightHindLeg.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.leftHindLeg.setPivot(0.5F, 22.7F, 2.0F);
			this.leftHindLeg.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.rightFrontLeg.pitch = 5.811947F;
			this.rightFrontLeg.setPivot(-2.49F, 17.0F, -4.0F);
			this.leftFrontLeg.pitch = 5.811947F;
			this.leftFrontLeg.setPivot(0.51F, 17.0F, -4.0F);
		} else {
			this.torso.setPivot(0.0F, 14.0F, 2.0F);
			this.torso.pitch = (float) (Math.PI / 2);
			this.neck.setPivot(-1.0F, 14.0F, -3.0F);
			this.neck.pitch = this.torso.pitch;
			this.tail.setPivot(-1.0F, 12.0F, 8.0F);
			this.rightHindLeg.setPivot(-2.5F, 16.0F, 7.0F);
			this.leftHindLeg.setPivot(0.5F, 16.0F, 7.0F);
			this.rightFrontLeg.setPivot(-2.5F, 16.0F, -4.0F);
			this.leftFrontLeg.setPivot(0.5F, 16.0F, -4.0F);
			this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
			this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		this.realHead.roll = wolfEntity.getBegAnimationProgress(h) + wolfEntity.getShakeAnimationProgress(h, 0.0F);
		this.neck.roll = wolfEntity.getShakeAnimationProgress(h, -0.08F);
		this.torso.roll = wolfEntity.getShakeAnimationProgress(h, -0.16F);
		this.realTail.roll = wolfEntity.getShakeAnimationProgress(h, -0.2F);
	}

	public void setAngles(T wolfEntity, float f, float g, float h, float i, float j) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.tail.pitch = h;
	}
}
