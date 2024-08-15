package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WolfEntityModel extends EntityModel<WolfEntityRenderState> {
	public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(Set.of("head"));
	/**
	 * The key of the real head model part, whose value is {@value}.
	 */
	private static final String REAL_HEAD = "real_head";
	/**
	 * The key of the upper body model part, whose value is {@value}.
	 */
	private static final String UPPER_BODY = "upper_body";
	/**
	 * The key of the real tail model part, whose value is {@value}.
	 */
	private static final String REAL_TAIL = "real_tail";
	private final ModelPart root;
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
	private static final int field_32580 = 8;

	public WolfEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.realHead = this.head.getChild("real_head");
		this.torso = root.getChild(EntityModelPartNames.BODY);
		this.neck = root.getChild("upper_body");
		this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
		this.tail = root.getChild(EntityModelPartNames.TAIL);
		this.realTail = this.tail.getChild("real_tail");
	}

	public static ModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = 13.5F;
		ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, 13.5F, -7.0F));
		modelPartData2.addChild(
			"real_head",
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, dilation)
				.uv(16, 14)
				.cuboid(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, dilation)
				.uv(16, 14)
				.cuboid(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, dilation)
				.uv(0, 10)
				.cuboid(-0.5F, -0.001F, -5.0F, 3.0F, 3.0F, 4.0F, dilation),
			ModelTransform.NONE
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(18, 14).cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, dilation),
			ModelTransform.of(0.0F, 14.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"upper_body",
			ModelPartBuilder.create().uv(21, 0).cuboid(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, dilation),
			ModelTransform.of(-1.0F, 14.0F, -3.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 18).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, dilation);
		modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-2.5F, 16.0F, 7.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(0.5F, 16.0F, 7.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-2.5F, 16.0F, -4.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(0.5F, 16.0F, -4.0F));
		ModelPartData modelPartData3 = modelPartData.addChild(
			EntityModelPartNames.TAIL, ModelPartBuilder.create(), ModelTransform.of(-1.0F, 12.0F, 8.0F, (float) (Math.PI / 5), 0.0F, 0.0F)
		);
		modelPartData3.addChild("real_tail", ModelPartBuilder.create().uv(9, 18).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, dilation), ModelTransform.NONE);
		return modelData;
	}

	public void setAngles(WolfEntityRenderState wolfEntityRenderState) {
		this.torso.resetTransform();
		this.neck.resetTransform();
		this.tail.resetTransform();
		this.rightHindLeg.resetTransform();
		this.leftHindLeg.resetTransform();
		this.rightFrontLeg.resetTransform();
		this.leftFrontLeg.resetTransform();
		float f = wolfEntityRenderState.limbFrequency;
		float g = wolfEntityRenderState.limbAmplitudeMultiplier;
		if (wolfEntityRenderState.angerTime) {
			this.tail.yaw = 0.0F;
		} else {
			this.tail.yaw = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		if (wolfEntityRenderState.inSittingPose) {
			float h = wolfEntityRenderState.ageScale;
			this.neck.pivotY += 2.0F * h;
			this.neck.pitch = (float) (Math.PI * 2.0 / 5.0);
			this.neck.yaw = 0.0F;
			this.torso.pivotY += 4.0F * h;
			this.torso.pivotZ -= 2.0F * h;
			this.torso.pitch = (float) (Math.PI / 4);
			this.tail.pivotY += 9.0F * h;
			this.tail.pivotZ -= 2.0F * h;
			this.rightHindLeg.pivotY += 6.7F * h;
			this.rightHindLeg.pivotZ -= 5.0F * h;
			this.rightHindLeg.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.leftHindLeg.pivotY += 6.7F * h;
			this.leftHindLeg.pivotZ -= 5.0F * h;
			this.leftHindLeg.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.rightFrontLeg.pitch = 5.811947F;
			this.rightFrontLeg.pivotX += 0.01F * h;
			this.rightFrontLeg.pivotY += 1.0F * h;
			this.leftFrontLeg.pitch = 5.811947F;
			this.leftFrontLeg.pivotX -= 0.01F * h;
			this.leftFrontLeg.pivotY += 1.0F * h;
		} else {
			this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
			this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		this.realHead.roll = wolfEntityRenderState.begAnimationProgress + wolfEntityRenderState.getRoll(0.0F);
		this.neck.roll = wolfEntityRenderState.getRoll(-0.08F);
		this.torso.roll = wolfEntityRenderState.getRoll(-0.16F);
		this.realTail.roll = wolfEntityRenderState.getRoll(-0.2F);
		this.head.pitch = wolfEntityRenderState.pitch * (float) (Math.PI / 180.0);
		this.head.yaw = wolfEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		this.tail.pitch = wolfEntityRenderState.tailAngle;
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
