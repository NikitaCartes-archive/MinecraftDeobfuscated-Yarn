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
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ParrotEntityModel extends EntityModel<ParrotEntityRenderState> {
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
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;

	public ParrotEntityModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.tail = root.getChild(EntityModelPartNames.TAIL);
		this.leftWing = root.getChild(EntityModelPartNames.LEFT_WING);
		this.rightWing = root.getChild(EntityModelPartNames.RIGHT_WING);
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.leftLeg = root.getChild(EntityModelPartNames.LEFT_LEG);
		this.rightLeg = root.getChild(EntityModelPartNames.RIGHT_LEG);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(2, 8).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
			ModelTransform.of(0.0F, 16.5F, -3.0F, 0.4937F, 0.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.TAIL,
			ModelPartBuilder.create().uv(22, 1).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F),
			ModelTransform.of(0.0F, 21.07F, 1.16F, 1.015F, 0.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_WING,
			ModelPartBuilder.create().uv(19, 8).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F),
			ModelTransform.of(1.5F, 16.94F, -2.76F, -0.6981F, (float) -Math.PI, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_WING,
			ModelPartBuilder.create().uv(19, 8).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F),
			ModelTransform.of(-1.5F, 16.94F, -2.76F, -0.6981F, (float) -Math.PI, 0.0F)
		);
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(2, 2).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F), ModelTransform.pivot(0.0F, 15.69F, -2.76F)
		);
		modelPartData2.addChild("head2", ModelPartBuilder.create().uv(10, 0).cuboid(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F), ModelTransform.pivot(0.0F, -2.0F, -1.0F));
		modelPartData2.addChild("beak1", ModelPartBuilder.create().uv(11, 7).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F), ModelTransform.pivot(0.0F, -0.5F, -1.5F));
		modelPartData2.addChild("beak2", ModelPartBuilder.create().uv(16, 7).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F), ModelTransform.pivot(0.0F, -1.75F, -2.45F));
		modelPartData2.addChild(
			"feather", ModelPartBuilder.create().uv(2, 18).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F), ModelTransform.of(0.0F, -2.15F, 0.15F, -0.2214F, 0.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(14, 18).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		modelPartData.addChild(EntityModelPartNames.LEFT_LEG, modelPartBuilder, ModelTransform.of(1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, modelPartBuilder, ModelTransform.of(-1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(ParrotEntityRenderState parrotEntityRenderState) {
		this.animateModel(parrotEntityRenderState.parrotPose);
		float f = parrotEntityRenderState.limbFrequency;
		float g = parrotEntityRenderState.limbAmplitudeMultiplier;
		this.setAngles(
			parrotEntityRenderState.parrotPose,
			parrotEntityRenderState.age,
			f,
			g,
			parrotEntityRenderState.flapAngle,
			parrotEntityRenderState.yawDegrees,
			parrotEntityRenderState.pitch
		);
	}

	public void poseOnShoulder(
		MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float limbAngle, float limbDistance, float headYaw, float headPitch, float age
	) {
		this.animateModel(ParrotEntityModel.Pose.ON_SHOULDER);
		this.setAngles(ParrotEntityModel.Pose.ON_SHOULDER, age, limbAngle, limbDistance, 0.0F, headYaw, headPitch);
		this.root.render(matrices, vertexConsumer, light, overlay);
	}

	private void setAngles(ParrotEntityModel.Pose pose, float age, float limbAngle, float limbDistance, float flapAngle, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		switch (pose) {
			case STANDING:
				this.leftLeg.pitch = this.leftLeg.pitch + MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
				this.rightLeg.pitch = this.rightLeg.pitch + MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
			case FLYING:
			case ON_SHOULDER:
			default:
				float h = flapAngle * 0.3F;
				this.head.pivotY += h;
				this.tail.pitch = this.tail.pitch + MathHelper.cos(limbAngle * 0.6662F) * 0.3F * limbDistance;
				this.tail.pivotY += h;
				this.body.pivotY += h;
				this.leftWing.roll = -0.0873F - flapAngle;
				this.leftWing.pivotY += h;
				this.rightWing.roll = 0.0873F + flapAngle;
				this.rightWing.pivotY += h;
				this.leftLeg.pivotY += h;
				this.rightLeg.pivotY += h;
			case SITTING:
				break;
			case PARTY:
				float f = MathHelper.cos(age);
				float g = MathHelper.sin(age);
				this.head.pivotX += f;
				this.head.pivotY += g;
				this.head.pitch = 0.0F;
				this.head.yaw = 0.0F;
				this.head.roll = MathHelper.sin(age) * 0.4F;
				this.body.pivotX += f;
				this.body.pivotY += g;
				this.leftWing.roll = -0.0873F - flapAngle;
				this.leftWing.pivotX += f;
				this.leftWing.pivotY += g;
				this.rightWing.roll = 0.0873F + flapAngle;
				this.rightWing.pivotX += f;
				this.rightWing.pivotY += g;
				this.tail.pivotX += f;
				this.tail.pivotY += g;
		}
	}

	private void animateModel(ParrotEntityModel.Pose pose) {
		this.body.resetTransform();
		this.head.resetTransform();
		this.tail.resetTransform();
		this.rightWing.resetTransform();
		this.leftWing.resetTransform();
		this.leftLeg.resetTransform();
		this.rightLeg.resetTransform();
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
				this.head.pivotY++;
				this.tail.pitch += (float) (Math.PI / 6);
				this.tail.pivotY++;
				this.body.pivotY++;
				this.leftWing.roll = -0.0873F;
				this.leftWing.pivotY++;
				this.rightWing.roll = 0.0873F;
				this.rightWing.pivotY++;
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

	public static ParrotEntityModel.Pose getPose(ParrotEntity parrot) {
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
