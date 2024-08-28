package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.animation.ArmadilloAnimations;
import net.minecraft.client.render.entity.state.ArmadilloEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ArmadilloEntityModel extends EntityModel<ArmadilloEntityRenderState> {
	public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.6F);
	private static final float field_47860 = 25.0F;
	private static final float field_47861 = 22.5F;
	private static final float field_47862 = 16.5F;
	private static final float field_47863 = 2.5F;
	private static final String HEAD_CUBE = "head_cube";
	private static final String RIGHT_EAR_CUBE = "right_ear_cube";
	private static final String LEFT_EAR_CUBE = "left_ear_cube";
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart cube;
	private final ModelPart head;
	private final ModelPart tail;

	public ArmadilloEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.body = modelPart.getChild(EntityModelPartNames.BODY);
		this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.head = this.body.getChild(EntityModelPartNames.HEAD);
		this.tail = this.body.getChild(EntityModelPartNames.TAIL);
		this.cube = modelPart.getChild(EntityModelPartNames.CUBE);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create()
				.uv(0, 20)
				.cuboid(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new Dilation(0.3F))
				.uv(0, 40)
				.cuboid(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 21.0F, 4.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.TAIL,
			ModelPartBuilder.create().uv(44, 53).cuboid(-0.5F, -0.0865F, 0.0933F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)),
			ModelTransform.of(0.0F, -3.0F, 1.0F, 0.5061F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -2.0F, -11.0F));
		modelPartData3.addChild(
			"head_cube",
			ModelPartBuilder.create().uv(43, 15).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, -1.0F, 0.0F));
		modelPartData4.addChild(
			"right_ear_cube",
			ModelPartBuilder.create().uv(43, 10).cuboid(-2.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)),
			ModelTransform.of(-0.5F, 0.0F, -0.6F, 0.1886F, -0.3864F, -0.0718F)
		);
		ModelPartData modelPartData5 = modelPartData3.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create(), ModelTransform.pivot(1.0F, -2.0F, 0.0F));
		modelPartData5.addChild(
			"left_ear_cube",
			ModelPartBuilder.create().uv(47, 10).cuboid(0.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)),
			ModelTransform.of(0.5F, 1.0F, -0.6F, 0.1886F, 0.3864F, 0.0718F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().uv(51, 31).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-2.0F, 21.0F, 4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().uv(42, 31).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.pivot(2.0F, 21.0F, 4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().uv(51, 43).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-2.0F, 21.0F, -4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().uv(42, 43).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.pivot(2.0F, 21.0F, -4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.CUBE,
			ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -10.0F, -6.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 24.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(ArmadilloEntityRenderState armadilloEntityRenderState) {
		super.setAngles(armadilloEntityRenderState);
		if (armadilloEntityRenderState.rolledUp) {
			this.body.hidden = true;
			this.leftHindLeg.visible = false;
			this.rightHindLeg.visible = false;
			this.tail.visible = false;
			this.cube.visible = true;
		} else {
			this.body.hidden = false;
			this.leftHindLeg.visible = true;
			this.rightHindLeg.visible = true;
			this.tail.visible = true;
			this.cube.visible = false;
			this.head.pitch = MathHelper.clamp(armadilloEntityRenderState.pitch, -22.5F, 25.0F) * (float) (Math.PI / 180.0);
			this.head.yaw = MathHelper.clamp(armadilloEntityRenderState.yawDegrees, -32.5F, 32.5F) * (float) (Math.PI / 180.0);
		}

		this.animateWalking(ArmadilloAnimations.IDLE, armadilloEntityRenderState.limbFrequency, armadilloEntityRenderState.limbAmplitudeMultiplier, 16.5F, 2.5F);
		this.animate(armadilloEntityRenderState.unrollingAnimationState, ArmadilloAnimations.UNROLLING, armadilloEntityRenderState.age, 1.0F);
		this.animate(armadilloEntityRenderState.rollingAnimationState, ArmadilloAnimations.ROLLING, armadilloEntityRenderState.age, 1.0F);
		this.animate(armadilloEntityRenderState.scaredAnimationState, ArmadilloAnimations.SCARED, armadilloEntityRenderState.age, 1.0F);
	}
}
