package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SnifferAnimations {
	public static final Animation BABY_GROWTH = Animation.Builder.create(0.0F)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.SCALE, new Keyframe(0.0F, AnimationHelper.createScalingVector(1.2F, 1.2F, 1.2F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE, new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation SCENTING = Animation.Builder.create(8.0F)
		.looping()
		.addBoneAnimation(
			"nose",
			new Transformation(
				Transformation.Targets.SCALE,
				new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5417F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createScalingVector(1.0, 0.5, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createScalingVector(1.0, 2.5, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9167F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0, 3.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(1.125F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation SNIFFING = Animation.Builder.create(1.0F)
		.addBoneAnimation(
			"nose",
			new Transformation(
				Transformation.Targets.SCALE,
				new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0, 0.7F, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0, 3.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0, 3.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createScalingVector(1.0, 4.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.875F, AnimationHelper.createRotationalVector(-20.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation WALKING = Animation.Builder.create(2.0F)
		.looping()
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 3.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_mid_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-7.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-7.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_mid_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 2.67F, -0.67F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9167F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 2.67F, -0.67F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 2.22F, 0.78F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_mid_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_mid_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9167F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 2.22F, 0.78F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 2.22F, 0.78F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(1.0F, 0.0F, -2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-1.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(1.0F, 0.0F, 2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-1.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(1.0F, 0.0F, -2.5F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.375F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(9.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.875F, AnimationHelper.createRotationalVector(-1.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(7.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
	public static final Animation SEARCHING = Animation.Builder.create(2.0F)
		.looping()
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 3.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_mid_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-7.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-7.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_mid_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 2.67F, -0.67F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9167F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 2.67F, -0.67F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 2.22F, 0.78F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_mid_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_mid_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9167F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 2.22F, 0.78F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 2.22F, 0.78F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(1.25F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(33.61503F, 11.46526F, 9.803F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.875F, AnimationHelper.createRotationalVector(34.71128F, 17.67415F, 14.15251F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.125F, AnimationHelper.createRotationalVector(37.21128F, -17.67415F, -14.15251F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.875F, AnimationHelper.createRotationalVector(38.30529F, -21.62827F, -17.40292F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -15.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -15.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -15.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -15.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 15.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 15.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 15.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 15.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"nose",
			new Transformation(
				Transformation.Targets.SCALE,
				new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0, 1.5, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.2083F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createScalingVector(1.0, 2.5, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.625F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9167F, AnimationHelper.createScalingVector(1.0, 2.5, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0833F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2917F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(1.3333F, AnimationHelper.createScalingVector(1.0, 2.5, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(1.625F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(1.6667F, AnimationHelper.createScalingVector(1.0, 3.5, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8333F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC),
				new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
	public static final Animation DIGGING = Animation.Builder.create(8.0F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(1.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.5F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(4.5F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(5.6667F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(5.8333F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(6.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, -7.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.SCALE,
				new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5417F, AnimationHelper.createScalingVector(1.04F, 0.98F, 1.02F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.4167F, AnimationHelper.createRotationalVector(-10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.875F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.0833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(47.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.6667F, AnimationHelper.createRotationalVector(38.44F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.875F, AnimationHelper.createRotationalVector(10.95951F, 13.57454F, -14.93501F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.2083F, AnimationHelper.createRotationalVector(47.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.5833F, AnimationHelper.createRotationalVector(55.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.7917F, AnimationHelper.createRotationalVector(4.2932F, -16.187F, 10.90042F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.125F, AnimationHelper.createRotationalVector(47.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.4167F, AnimationHelper.createRotationalVector(54.71135F, 7.98009F, -5.56662F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.5F, AnimationHelper.createRotationalVector(55.72895F, -6.77684F, 4.46197F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.5833F, AnimationHelper.createRotationalVector(54.71135F, 7.98009F, -5.56662F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.6667F, AnimationHelper.createRotationalVector(55.72895F, -6.77684F, 4.46197F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.75F, AnimationHelper.createRotationalVector(54.71135F, 7.98009F, -5.56662F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.8333F, AnimationHelper.createRotationalVector(55.72895F, -6.77684F, 4.46197F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createRotationalVector(65.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.75F, AnimationHelper.createRotationalVector(65.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.9167F, AnimationHelper.createRotationalVector(-32.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.625F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.375F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.875F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0833F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.2917F, AnimationHelper.createTranslationalVector(0.0F, 6.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.2083F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(4.125F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(5.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(5.75F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(6.0F, AnimationHelper.createTranslationalVector(0.0F, 1.5F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(6.25F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -50.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(5.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(6.0833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -65.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(6.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 50.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 30.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(5.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 30.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(6.0833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 65.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(6.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 30.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2917F, AnimationHelper.createTranslationalVector(-2.0F, -0.75F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.375F, AnimationHelper.createTranslationalVector(-4.0F, -5.5F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_mid_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_mid_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(-2.0F, -0.75F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4167F, AnimationHelper.createTranslationalVector(-4.0F, -5.5F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4167F, AnimationHelper.createTranslationalVector(-2.0F, -0.75F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(-4.0F, -5.5F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -90.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2917F, AnimationHelper.createTranslationalVector(2.0F, -0.75F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.375F, AnimationHelper.createTranslationalVector(4.0F, -5.5F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_mid_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -90.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_mid_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(2.0F, -0.75F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4167F, AnimationHelper.createTranslationalVector(4.0F, -5.5F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -90.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4167F, AnimationHelper.createTranslationalVector(2.0F, -0.75F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(4.0F, -5.5F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation RISING = Animation.Builder.create(3.0F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, -7.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, -7.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3333F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.375F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -5.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 30.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 30.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 5.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-4.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(6.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_mid_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_mid_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(-4.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(6.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(-4.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(6.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -90.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(4.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(-6.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_mid_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -90.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_mid_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(4.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(-6.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -90.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(4.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(-6.0F, -5.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
	public static final Animation field_42872 = Animation.Builder.create(4.0F)
		.addBoneAnimation(
			"bone",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-98.91F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.9583F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.7083F, AnimationHelper.createRotationalVector(-68.28F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"bone",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 20.0F, 17.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 25.19F, 20.37F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.9583F, AnimationHelper.createTranslationalVector(0.0F, 20.0F, 17.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.7083F, AnimationHelper.createTranslationalVector(0.0F, 17.06F, 11.25F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.8333F, AnimationHelper.createTranslationalVector(0.0F, 9.85F, 2.2F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.9583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.SCALE,
				new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9583F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(2.9167F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0F, AnimationHelper.createScalingVector(1.05F, 0.95F, 1.05F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0833F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2917F, AnimationHelper.createRotationalVector(17.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9583F, AnimationHelper.createRotationalVector(-10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.75F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.9167F, AnimationHelper.createRotationalVector(-30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0417F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 7.0F, 19.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 7.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9583F, AnimationHelper.createTranslationalVector(0.0F, 7.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.75F, AnimationHelper.createTranslationalVector(0.0F, 7.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.9583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -90.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.25F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.75F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_mid_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.625F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.875F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.125F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.375F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.625F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_mid_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.25F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.25F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.75F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_mid_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.625F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.875F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.125F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.375F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.625F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_mid_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.25F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation FEELING_HAPPY = Animation.Builder.create(2.0F)
		.looping()
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-32.00206F, 19.3546F, -11.70092F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-32.00206F, -19.3546F, 11.70092F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -67.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -67.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 67.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 67.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
}
