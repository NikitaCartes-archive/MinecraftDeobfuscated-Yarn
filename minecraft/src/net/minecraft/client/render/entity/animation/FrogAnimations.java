package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FrogAnimations {
	public static final Animation CROAKING = Animation.Builder.create(3.0F)
		.addBoneAnimation(
			"croaking_body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.9583F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"croaking_body",
			new Transformation(
				Transformation.Targets.SCALE,
				new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0, 0.0, 0.0), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createScalingVector(0.0, 0.0, 0.0), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4167F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4583F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5417F, AnimationHelper.createScalingVector(1.3F, 2.1F, 1.6F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.625F, AnimationHelper.createScalingVector(1.3F, 2.1F, 1.6F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7083F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(2.25F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3333F, AnimationHelper.createScalingVector(1.3F, 2.1F, 1.6F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.4167F, AnimationHelper.createScalingVector(1.3F, 2.1F, 1.6F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5833F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6667F, AnimationHelper.createScalingVector(1.3F, 2.1F, 1.6F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.875F, AnimationHelper.createScalingVector(1.3F, 2.1F, 1.6F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.9583F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0F, AnimationHelper.createScalingVector(0.0, 0.0, 0.0), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation WALKING = Animation.Builder.create(1.25F)
		.looping()
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2917F, AnimationHelper.createRotationalVector(7.5F, -2.67F, -7.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.625F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7917F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.125F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2917F, AnimationHelper.createTranslationalVector(-0.5F, -0.25F, -0.13F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.625F, AnimationHelper.createTranslationalVector(-0.5F, 0.1F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.5F, 1.0F, -0.11F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, -2.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4583F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.625F, AnimationHelper.createRotationalVector(0.0F, 5.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(7.5F, 2.33F, 7.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.5F, 0.1F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2917F, AnimationHelper.createTranslationalVector(-0.5F, 1.0F, 0.12F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.625F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.5F, -0.25F, -0.13F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.5F, 0.1F, 2.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2917F, AnimationHelper.createRotationalVector(45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.625F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, 1.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, 1.06F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, 1.2F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-33.75F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.0417F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(-33.75F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 1.14F, 0.11F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.125F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, 0.95F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 1.14F, 0.11F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 5.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2917F, AnimationHelper.createRotationalVector(-7.5F, 0.33F, 7.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.625F, AnimationHelper.createRotationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(-7.5F, 0.33F, -7.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 5.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation LONG_JUMPING = Animation.Builder.create(0.5F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-56.14F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-56.14F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-56.14F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-56.14F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation USING_TONGUE = Animation.Builder.create(0.5F)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.0833F, AnimationHelper.createRotationalVector(-60.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4167F, AnimationHelper.createRotationalVector(-60.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.SCALE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.998F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4167F, AnimationHelper.createRotationalVector(0.998F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"tongue",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4167F, AnimationHelper.createRotationalVector(-18.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"tongue",
			new Transformation(
				Transformation.Targets.SCALE,
				new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createScalingVector(0.5, 1.0, 5.0), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4167F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation SWIMMING = Animation.Builder.create(1.04167F)
		.looping()
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createRotationalVector(-10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(90.0F, 22.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createRotationalVector(45.0F, 22.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createRotationalVector(-22.5F, -22.5F, -22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.875F, AnimationHelper.createRotationalVector(-45.0F, -22.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createRotationalVector(90.0F, 22.5F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.64F, 2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(0.0F, -0.64F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, -0.27F, -1.14F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.0F, -1.45F, 0.43F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createTranslationalVector(0.0F, -0.64F, 2.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(90.0F, -22.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createRotationalVector(45.0F, -22.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createRotationalVector(-22.5F, 22.5F, 22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.875F, AnimationHelper.createRotationalVector(-45.0F, 22.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(22.5F, 0.0F, -22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createRotationalVector(90.0F, -22.5F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.64F, 2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(0.0F, -0.64F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, -0.27F, -1.14F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.0F, -1.45F, 0.43F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createTranslationalVector(0.0F, -0.64F, 2.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createRotationalVector(67.5F, -45.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7917F, AnimationHelper.createRotationalVector(90.0F, 45.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-2.5F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(-2.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(1.0F, -2.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(0.58F, 0.0F, -2.83F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(-2.5F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createTranslationalVector(-2.5F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createRotationalVector(67.5F, 45.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7917F, AnimationHelper.createRotationalVector(90.0F, -45.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(2.5F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(2.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(-1.0F, -2.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(-0.58F, 0.0F, -2.83F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(2.5F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createTranslationalVector(2.5F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
	public static final Animation IDLING_IN_WATER = Animation.Builder.create(3.0F)
		.looping()
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.625F, AnimationHelper.createRotationalVector(-10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -45.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -22.5F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-1.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2083F, AnimationHelper.createTranslationalVector(-1.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createTranslationalVector(-1.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 45.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 22.5F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2083F, AnimationHelper.createTranslationalVector(1.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(22.5F, -22.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(22.5F, -22.5F, -45.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(22.5F, -22.5F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(22.5F, 22.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(22.5F, 22.5F, 45.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(22.5F, 22.5F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
}
