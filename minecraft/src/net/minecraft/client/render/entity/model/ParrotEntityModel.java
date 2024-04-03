package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ParrotEntityModel extends SinglePartEntityModel<ParrotEntity> {
	/**
	 * The key of the feather model part, whose value is {@value}.
	 */
	private static final String FEATHER = "feather";
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart leftWing;
	private final ModelPart rightWing;
	private final ModelPart head;
	private final ModelPart feather;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;

	public ParrotEntityModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.tail = root.getChild(EntityModelPartNames.TAIL);
		this.leftWing = root.getChild(EntityModelPartNames.LEFT_WING);
		this.rightWing = root.getChild(EntityModelPartNames.RIGHT_WING);
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.feather = this.head.getChild("feather");
		this.leftLeg = root.getChild(EntityModelPartNames.LEFT_LEG);
		this.rightLeg = root.getChild(EntityModelPartNames.RIGHT_LEG);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(2, 8).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), ModelTransform.pivot(0.0F, 16.5F, -3.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(22, 1).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 21.07F, 1.16F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(19, 8).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(1.5F, 16.94F, -2.76F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_WING,
			ModelPartBuilder.create().uv(19, 8).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F),
			ModelTransform.pivot(-1.5F, 16.94F, -2.76F)
		);
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(2, 2).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F), ModelTransform.pivot(0.0F, 15.69F, -2.76F)
		);
		modelPartData2.addChild("head2", ModelPartBuilder.create().uv(10, 0).cuboid(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F), ModelTransform.pivot(0.0F, -2.0F, -1.0F));
		modelPartData2.addChild("beak1", ModelPartBuilder.create().uv(11, 7).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F), ModelTransform.pivot(0.0F, -0.5F, -1.5F));
		modelPartData2.addChild("beak2", ModelPartBuilder.create().uv(16, 7).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F), ModelTransform.pivot(0.0F, -1.75F, -2.45F));
		modelPartData2.addChild(
			"feather", ModelPartBuilder.create().uv(2, 18).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F), ModelTransform.pivot(0.0F, -2.15F, 0.15F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(14, 18).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		modelPartData.addChild(EntityModelPartNames.LEFT_LEG, modelPartBuilder, ModelTransform.pivot(1.0F, 22.0F, -1.05F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, modelPartBuilder, ModelTransform.pivot(-1.0F, 22.0F, -1.05F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(ParrotEntity parrotEntity, float f, float g, float h, float i, float j) {
		this.setAngles(getPose(parrotEntity), parrotEntity.age, f, g, h, i, j);
	}

	public void animateModel(ParrotEntity parrotEntity, float f, float g, float h) {
		this.animateModel(getPose(parrotEntity));
	}

	public void poseOnShoulder(
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		int light,
		int overlay,
		float limbAngle,
		float limbDistance,
		float headYaw,
		float headPitch,
		int danceAngle
	) {
		this.animateModel(ParrotEntityModel.Pose.ON_SHOULDER);
		this.setAngles(ParrotEntityModel.Pose.ON_SHOULDER, danceAngle, limbAngle, limbDistance, 0.0F, headYaw, headPitch);
		this.root.render(matrices, vertexConsumer, light, overlay);
	}

	private void setAngles(ParrotEntityModel.Pose pose, int danceAngle, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.roll = 0.0F;
		this.head.pivotX = 0.0F;
		this.body.pivotX = 0.0F;
		this.tail.pivotX = 0.0F;
		this.rightWing.pivotX = -1.5F;
		this.leftWing.pivotX = 1.5F;
		switch (pose) {
			case STANDING:
				this.leftLeg.pitch = this.leftLeg.pitch + MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
				this.rightLeg.pitch = this.rightLeg.pitch + MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
			case FLYING:
			case ON_SHOULDER:
			default:
				float h = age * 0.3F;
				this.head.pivotY = 15.69F + h;
				this.tail.pitch = 1.015F + MathHelper.cos(limbAngle * 0.6662F) * 0.3F * limbDistance;
				this.tail.pivotY = 21.07F + h;
				this.body.pivotY = 16.5F + h;
				this.leftWing.roll = -0.0873F - age;
				this.leftWing.pivotY = 16.94F + h;
				this.rightWing.roll = 0.0873F + age;
				this.rightWing.pivotY = 16.94F + h;
				this.leftLeg.pivotY = 22.0F + h;
				this.rightLeg.pivotY = 22.0F + h;
			case SITTING:
				break;
			case PARTY:
				float f = MathHelper.cos((float)danceAngle);
				float g = MathHelper.sin((float)danceAngle);
				this.head.pivotX = f;
				this.head.pivotY = 15.69F + g;
				this.head.pitch = 0.0F;
				this.head.yaw = 0.0F;
				this.head.roll = MathHelper.sin((float)danceAngle) * 0.4F;
				this.body.pivotX = f;
				this.body.pivotY = 16.5F + g;
				this.leftWing.roll = -0.0873F - age;
				this.leftWing.pivotX = 1.5F + f;
				this.leftWing.pivotY = 16.94F + g;
				this.rightWing.roll = 0.0873F + age;
				this.rightWing.pivotX = -1.5F + f;
				this.rightWing.pivotY = 16.94F + g;
				this.tail.pivotX = f;
				this.tail.pivotY = 21.07F + g;
		}
	}

	private void animateModel(ParrotEntityModel.Pose pose) {
		this.feather.pitch = -0.2214F;
		this.body.pitch = 0.4937F;
		this.leftWing.pitch = -0.6981F;
		this.leftWing.yaw = (float) -Math.PI;
		this.rightWing.pitch = -0.6981F;
		this.rightWing.yaw = (float) -Math.PI;
		this.leftLeg.pitch = -0.0299F;
		this.rightLeg.pitch = -0.0299F;
		this.leftLeg.pivotY = 22.0F;
		this.rightLeg.pivotY = 22.0F;
		this.leftLeg.roll = 0.0F;
		this.rightLeg.roll = 0.0F;
		switch (pose) {
			case FLYING:
				this.leftLeg.pitch += (float) (Math.PI * 2.0 / 9.0);
				this.rightLeg.pitch += (float) (Math.PI * 2.0 / 9.0);
			case STANDING:
			case ON_SHOULDER:
			default:
				break;
			case SITTING:
				float f = 1.9F;
				this.head.pivotY = 17.59F;
				this.tail.pitch = 1.5388988F;
				this.tail.pivotY = 22.97F;
				this.body.pivotY = 18.4F;
				this.leftWing.roll = -0.0873F;
				this.leftWing.pivotY = 18.84F;
				this.rightWing.roll = 0.0873F;
				this.rightWing.pivotY = 18.84F;
				this.leftLeg.pivotY++;
				this.rightLeg.pivotY++;
				this.leftLeg.pitch++;
				this.rightLeg.pitch++;
				break;
			case PARTY:
				this.leftLeg.roll = (float) (-Math.PI / 9);
				this.rightLeg.roll = (float) (Math.PI / 9);
		}
	}

	private static ParrotEntityModel.Pose getPose(ParrotEntity parrot) {
		if (parrot.isSongPlaying()) {
			return ParrotEntityModel.Pose.PARTY;
		} else if (parrot.isInSittingPose()) {
			return ParrotEntityModel.Pose.SITTING;
		} else {
			return parrot.isInAir() ? ParrotEntityModel.Pose.FLYING : ParrotEntityModel.Pose.STANDING;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Pose {
		FLYING,
		STANDING,
		SITTING,
		PARTY,
		ON_SHOULDER;
	}
}
