package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ArmadilloAnimations {
	public static final Animation ROLLING = Animation.Builder.create(0.5F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 5.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(0.0F, 6.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 6.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, -1.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"tail",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"tail",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(17.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-72.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2917F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, 6.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, 7.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 5.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1875F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, -3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createTranslationalVector(1.0F, 3.0F, -6.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 5.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1875F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, -3.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createTranslationalVector(-1.0F, 3.0F, -6.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-27.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createRotationalVector(-32.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-85.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 5.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1875F, AnimationHelper.createTranslationalVector(-0.5F, 11.5F, 0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(-1.0F, 9.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(-1.0F, 9.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, 3.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-12.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createRotationalVector(-35.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-85.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 5.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1875F, AnimationHelper.createTranslationalVector(0.5F, 11.5F, 0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(1.0F, 9.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(1.0F, 9.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, 3.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"cube",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4167F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4583F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"cube",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(0.0F, 7.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 7.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.6F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation IDLE = Animation.Builder.create(1.4583F)
		.looping()
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 4.6F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.2917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 6.81F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -4.6F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -6.89F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, -0.2F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.0F, -0.2F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"tail",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-9.17F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createRotationalVector(-8.24F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2917F, AnimationHelper.createRotationalVector(-20.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4583F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2917F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -0.18F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5417F, AnimationHelper.createRotationalVector(-20.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7083F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4583F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.25F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5417F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -0.18F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.25F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2917F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5417F, AnimationHelper.createRotationalVector(-20.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7083F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4583F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.25F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5417F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -0.18F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.25F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2917F, AnimationHelper.createRotationalVector(-20.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4583F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2917F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -0.18F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation SCARED = Animation.Builder.create(2.5F)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-70.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.15F, AnimationHelper.createRotationalVector(-65.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 45.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.15F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 45.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3F, AnimationHelper.createRotationalVector(-0.8639F, -1.4959F, -39.1287F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.6F, AnimationHelper.createRotationalVector(-0.8639F, -1.4959F, -39.1287F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.85F, AnimationHelper.createRotationalVector(-70.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 7.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.05F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 4.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.15F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 4.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.35F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 2.1F, 1.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6F, AnimationHelper.createTranslationalVector(0.0F, 1.1F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createTranslationalVector(0.0F, 1.1F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.75F, AnimationHelper.createTranslationalVector(0.0F, 1.1F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.95F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, 5.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, 7.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.15F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, 8.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, 5.2F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE, new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, -2.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE, new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, -2.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.8333F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.95F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5833F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, 2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, -2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.75F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.95F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, 3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.15F, AnimationHelper.createTranslationalVector(-1.0F, 3.0F, 4.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6667F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.8333F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.95F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, 2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, -2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.75F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.95F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, 3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.15F, AnimationHelper.createTranslationalVector(1.0F, 3.0F, 4.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"cube",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.35F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.05F, AnimationHelper.createRotationalVector(-17.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.15F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3F, AnimationHelper.createRotationalVector(12.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"cube",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.35F, AnimationHelper.createTranslationalVector(0.0F, 1.6F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.5F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.05F, AnimationHelper.createTranslationalVector(0.0F, 1.2F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.15F, AnimationHelper.createTranslationalVector(0.0F, 1.7F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3F, AnimationHelper.createTranslationalVector(0.0F, 1.3F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation UNROLLING = Animation.Builder.create(1.5F)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.1F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.15F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.65F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.85F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.95F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.05F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 5.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.05F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.15F, AnimationHelper.createTranslationalVector(0.0F, 2.1F, 1.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 1.03F, 0.13F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4F, AnimationHelper.createTranslationalVector(0.0F, 1.03F, 0.13F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.65F, AnimationHelper.createTranslationalVector(0.0F, 1.03F, 0.13F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createTranslationalVector(0.0F, 1.1F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 4.1F, 2.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.85F, AnimationHelper.createTranslationalVector(0.0F, 5.1F, 3.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9F, AnimationHelper.createTranslationalVector(0.0F, 0.1F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.95F, AnimationHelper.createTranslationalVector(0.0F, 0.9F, -0.8F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.05F, AnimationHelper.createTranslationalVector(0.0F, 0.9F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createTranslationalVector(0.0F, 2.6F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.15F, AnimationHelper.createTranslationalVector(0.0F, 2.4F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.2F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.2F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.1F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 30.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.45F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.1F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3F, AnimationHelper.createTranslationalVector(-1.0F, 3.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.45F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(1.1F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.45F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_hind_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.1F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3F, AnimationHelper.createTranslationalVector(1.0F, 3.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.35F, AnimationHelper.createTranslationalVector(1.0F, 3.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.45F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.05F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.55F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6F, AnimationHelper.createRotationalVector(-92.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.1F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.3F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 30.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.4F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.45F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.05F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, 2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.55F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, 2.63F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.1F, AnimationHelper.createTranslationalVector(-1.0F, 2.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(-1.0F, 7.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3F, AnimationHelper.createTranslationalVector(-1.0F, 3.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.45F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.05F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.55F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6F, AnimationHelper.createRotationalVector(-87.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.1F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.3F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.4F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.45F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_front_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, 2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.05F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, 2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.15F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.55F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, 1.88F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, 2.67F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.1F, AnimationHelper.createTranslationalVector(1.0F, 2.0F, 2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(1.0F, 8.0F, 2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(1.06F, 5.06F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.3F, AnimationHelper.createTranslationalVector(1.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.4F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.45F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"cube",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.05F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.15F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createRotationalVector(-17.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.85F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.95F, AnimationHelper.createRotationalVector(12.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.05F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"cube",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.05F, AnimationHelper.createTranslationalVector(0.0F, 1.6F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.15F, AnimationHelper.createTranslationalVector(0.0F, 0.5F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.7F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 1.2F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.85F, AnimationHelper.createTranslationalVector(0.0F, 1.7F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.95F, AnimationHelper.createTranslationalVector(0.0F, 1.3F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.05F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(0.0F, 5.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(1.1F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 5.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.4F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
}
