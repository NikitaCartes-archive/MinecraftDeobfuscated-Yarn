package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CamelAnimations {
	public static final Animation WALKING = Animation.Builder.create(1.5F)
		.looping()
		.addBoneAnimation(
			"root",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.125F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-20.4F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.375F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-20.4F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.21F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0833F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.375F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, -0.21F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.625F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.625F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"tail",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(15.94102F, -8.42106F, 20.94102F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(15.94102F, 8.42106F, -20.94102F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(15.94102F, -8.42106F, 20.94102F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
	public static final Animation SITTING_TRANSITION = Animation.Builder.create(2.0F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3F, AnimationHelper.createRotationalVector(30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(24.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8F, AnimationHelper.createTranslationalVector(0.0F, -6.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, -19.9F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-90.0F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 11.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 11.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.7F, AnimationHelper.createTranslationalVector(0.0F, -8.4F, 11.4F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, -20.6F, 12.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-90.0F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 11.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 11.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.7F, AnimationHelper.createTranslationalVector(0.0F, -8.4F, 11.4F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, -20.6F, 12.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.7F, AnimationHelper.createRotationalVector(-15.0F, -3.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9F, AnimationHelper.createRotationalVector(-65.0F, -9.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-90.0F, -15.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.7F, AnimationHelper.createTranslationalVector(1.0F, -0.62F, 0.25F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9F, AnimationHelper.createTranslationalVector(0.5F, -11.25F, 2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(1.0F, -20.5F, 5.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.7F, AnimationHelper.createRotationalVector(-15.0F, 3.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9F, AnimationHelper.createRotationalVector(-65.0F, 9.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-90.0F, 15.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.7F, AnimationHelper.createTranslationalVector(-1.0F, -0.62F, 0.25F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9F, AnimationHelper.createTranslationalVector(-0.5F, -11.25F, 2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(-1.0F, -20.5F, 5.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createRotationalVector(-27.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-21.25F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"tail",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.7F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9F, AnimationHelper.createRotationalVector(80.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation SITTING = Animation.Builder.create(1.0F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -19.9F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -19.9F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-90.0F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -20.6F, 12.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -20.6F, 12.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-90.0F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -20.6F, 12.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -20.6F, 12.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, -15.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-90.0F, -15.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(1.0F, -20.5F, 5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(1.0F, -20.5F, 5.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 15.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-90.0F, 15.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-1.0F, -20.5F, 5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(-1.0F, -20.5F, 5.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"tail",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation STANDING_TRANSITION = Animation.Builder.create(2.6F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createRotationalVector(-17.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(-17.83F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.3F, AnimationHelper.createRotationalVector(-5.83F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -19.9F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createTranslationalVector(0.0F, -19.9F, -3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4F, AnimationHelper.createTranslationalVector(0.0F, -12.76F, -4.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createTranslationalVector(0.0F, -10.1F, -4.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.3F, AnimationHelper.createTranslationalVector(0.0F, -2.9F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-90.0F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createRotationalVector(-49.06F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(-22.5F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3F, AnimationHelper.createRotationalVector(-25.0F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -20.6F, 12.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -20.6F, 8.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createTranslationalVector(0.0F, -7.14F, 4.42F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8F, AnimationHelper.createTranslationalVector(0.0F, -1.27F, -1.33F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3F, AnimationHelper.createTranslationalVector(0.0F, -1.27F, -0.33F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-90.0F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createRotationalVector(-49.06F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(-22.5F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3F, AnimationHelper.createRotationalVector(-25.0F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -20.6F, 12.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -20.6F, 8.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createTranslationalVector(0.0F, -7.14F, 4.42F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8F, AnimationHelper.createTranslationalVector(0.0F, -1.27F, -1.33F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3F, AnimationHelper.createTranslationalVector(0.0F, -1.27F, -0.33F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, -15.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createRotationalVector(-60.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.2F, AnimationHelper.createRotationalVector(30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(1.0F, -20.5F, 5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3F, AnimationHelper.createTranslationalVector(-2.0F, -20.5F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6F, AnimationHelper.createTranslationalVector(-2.0F, -20.5F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createTranslationalVector(-2.0F, -10.5F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(-2.0F, -0.4F, -3.9F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9F, AnimationHelper.createTranslationalVector(-2.0F, -4.3F, -9.8F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.2F, AnimationHelper.createTranslationalVector(-1.0F, -2.5F, -5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 15.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createRotationalVector(-60.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.2F, AnimationHelper.createRotationalVector(30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-1.0F, -20.5F, 5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3F, AnimationHelper.createTranslationalVector(2.0F, -20.5F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6F, AnimationHelper.createTranslationalVector(2.0F, -20.5F, 3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createTranslationalVector(2.0F, -10.5F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(2.0F, -0.4F, -3.9F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9F, AnimationHelper.createTranslationalVector(2.0F, -4.3F, -9.8F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.2F, AnimationHelper.createTranslationalVector(1.0F, -2.5F, -5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.8F, AnimationHelper.createRotationalVector(55.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(65.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.4F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"tail",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4F, AnimationHelper.createRotationalVector(55.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9F, AnimationHelper.createRotationalVector(55.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(17.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.6F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation DASHING = Animation.Builder.create(0.5F)
		.looping()
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"tail",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(67.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(112.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(67.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(112.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(67.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(44.97272F, 1.76749F, -1.76833F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(44.97272F, 1.76749F, -1.76833F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(44.97272F, 1.76749F, -1.76833F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(44.97272F, -1.76749F, 1.76833F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(44.97272F, -1.76749F, 1.76833F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, -67.5F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, -67.5F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 67.5F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 67.5F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation IDLING = Animation.Builder.create(4.0F)
		.addBoneAnimation(
			"tail",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(4.98107F, 0.43523F, -4.98107F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(4.9872F, -0.29424F, 3.36745F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.0F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -45.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.625F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -45.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.875F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -45.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 45.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.625F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 45.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.875F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 45.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
}
