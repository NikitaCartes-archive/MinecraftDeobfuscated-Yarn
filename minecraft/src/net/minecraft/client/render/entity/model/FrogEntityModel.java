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
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.FrogAnimations;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class FrogEntityModel<T extends FrogEntity> extends SinglePartEntityModel<T> {
	private static final Vec3f field_37918 = new Vec3f();
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart eyes;
	private final ModelPart tongue;
	private final ModelPart leftArm;
	private final ModelPart rightArm;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart croakingBody;

	public FrogEntityModel(ModelPart root) {
		this.root = root.getChild(EntityModelPartNames.ROOT);
		this.body = this.root.getChild(EntityModelPartNames.BODY);
		this.head = this.body.getChild(EntityModelPartNames.HEAD);
		this.eyes = this.head.getChild(EntityModelPartNames.EYES);
		this.tongue = this.body.getChild(EntityModelPartNames.TONGUE);
		this.leftArm = this.body.getChild(EntityModelPartNames.LEFT_ARM);
		this.rightArm = this.body.getChild(EntityModelPartNames.RIGHT_ARM);
		this.leftLeg = this.root.getChild(EntityModelPartNames.LEFT_LEG);
		this.rightLeg = this.root.getChild(EntityModelPartNames.RIGHT_LEG);
		this.croakingBody = this.body.getChild(EntityModelPartNames.CROAKING_BODY);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(3, 1).cuboid(-3.5F, -2.0F, -8.0F, 7.0F, 3.0F, 9.0F).uv(23, 22).cuboid(-3.5F, -1.0F, -8.0F, 7.0F, 0.0F, 9.0F),
			ModelTransform.pivot(0.0F, -2.0F, 4.0F)
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(23, 13).cuboid(-3.5F, -1.0F, -7.0F, 7.0F, 0.0F, 9.0F).uv(0, 13).cuboid(-3.5F, -2.0F, -7.0F, 7.0F, 3.0F, 9.0F),
			ModelTransform.pivot(0.0F, -2.0F, -1.0F)
		);
		ModelPartData modelPartData5 = modelPartData4.addChild(EntityModelPartNames.EYES, ModelPartBuilder.create(), ModelTransform.pivot(-0.5F, 0.0F, 2.0F));
		modelPartData5.addChild(
			EntityModelPartNames.RIGHT_EYE, ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(-1.5F, -3.0F, -6.5F)
		);
		modelPartData5.addChild(
			EntityModelPartNames.LEFT_EYE, ModelPartBuilder.create().uv(0, 5).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(2.5F, -3.0F, -6.5F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.CROAKING_BODY,
			ModelPartBuilder.create().uv(26, 5).cuboid(-3.5F, -0.1F, -2.9F, 7.0F, 2.0F, 3.0F, new Dilation(-0.1F)),
			ModelTransform.pivot(0.0F, -1.0F, -5.0F)
		);
		ModelPartData modelPartData6 = modelPartData3.addChild(
			EntityModelPartNames.TONGUE, ModelPartBuilder.create().uv(17, 13).cuboid(-2.0F, 0.0F, -7.1F, 4.0F, 0.0F, 7.0F), ModelTransform.pivot(0.0F, -1.01F, 1.0F)
		);
		ModelPartData modelPartData7 = modelPartData3.addChild(
			EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(0, 32).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F), ModelTransform.pivot(4.0F, -1.0F, -6.5F)
		);
		modelPartData7.addChild(
			EntityModelPartNames.LEFT_HAND, ModelPartBuilder.create().uv(18, 40).cuboid(-4.0F, 0.01F, -4.0F, 8.0F, 0.0F, 8.0F), ModelTransform.pivot(0.0F, 3.0F, -1.0F)
		);
		ModelPartData modelPartData8 = modelPartData3.addChild(
			EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(0, 38).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F), ModelTransform.pivot(-4.0F, -1.0F, -6.5F)
		);
		modelPartData8.addChild(
			EntityModelPartNames.RIGHT_HAND, ModelPartBuilder.create().uv(2, 40).cuboid(-4.0F, 0.01F, -5.0F, 8.0F, 0.0F, 8.0F), ModelTransform.pivot(0.0F, 3.0F, 0.0F)
		);
		ModelPartData modelPartData9 = modelPartData2.addChild(
			EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(14, 25).cuboid(-1.0F, 0.0F, -2.0F, 3.0F, 3.0F, 4.0F), ModelTransform.pivot(3.5F, -3.0F, 4.0F)
		);
		modelPartData9.addChild(
			EntityModelPartNames.LEFT_FOOT, ModelPartBuilder.create().uv(2, 32).cuboid(-4.0F, 0.01F, -4.0F, 8.0F, 0.0F, 8.0F), ModelTransform.pivot(2.0F, 3.0F, 0.0F)
		);
		ModelPartData modelPartData10 = modelPartData2.addChild(
			EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 25).cuboid(-2.0F, 0.0F, -2.0F, 3.0F, 3.0F, 4.0F), ModelTransform.pivot(-3.5F, -3.0F, 4.0F)
		);
		modelPartData10.addChild(
			EntityModelPartNames.RIGHT_FOOT, ModelPartBuilder.create().uv(18, 32).cuboid(-4.0F, 0.01F, -4.0F, 8.0F, 0.0F, 8.0F), ModelTransform.pivot(-2.0F, 3.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 48, 48);
	}

	public void setAngles(T frogEntity, float f, float g, float h, float i, float j) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		long l = Util.getMeasuringTimeMs();
		this.runAnimation(frogEntity.longJumpingAnimationState, FrogAnimations.LONG_JUMPING, l);
		this.runAnimation(frogEntity.croakingAnimationState, FrogAnimations.CROAKING, l);
		this.runAnimation(frogEntity.usingTongueAnimationState, FrogAnimations.USING_TONGUE, l);
		this.runAnimation(frogEntity.walkingAnimationState, FrogAnimations.WALKING, l);
		this.runAnimation(frogEntity.swimmingAnimationState, FrogAnimations.SWIMMING, l);
		this.runAnimation(frogEntity.idlingInWaterAnimationState, FrogAnimations.IDLING_IN_WATER, l);
		this.croakingBody.visible = frogEntity.croakingAnimationState.isRunning();
	}

	private void runAnimation(AnimationState animationState, Animation animation, long time) {
		animationState.run(state -> AnimationHelper.animate(this, animation, time - state.getStartTime(), 1.0F, field_37918));
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
