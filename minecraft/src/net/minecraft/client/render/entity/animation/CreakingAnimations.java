package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreakingAnimations {
	public static final Animation WALKING = Animation.Builder.create(1.5F)
		.looping()
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE, new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE, new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4167F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9167F, AnimationHelper.createRotationalVector(-10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0833F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(17.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.4167F, AnimationHelper.createRotationalVector(-10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.9167F, AnimationHelper.createRotationalVector(17.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0833F, AnimationHelper.createRotationalVector(30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(-30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(40.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6667F, AnimationHelper.createRotationalVector(60.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.8333F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, -3.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(40.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(60.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, -3.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, -3.5F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"upper_body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6667F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.8333F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0833F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"upper_body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation ATTACKING = Animation.Builder.create(0.375F)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(21.1693F, 37.7555F, 122.3122F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(-60.349F, -31.3213F, -16.4846F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-54.3924F, -22.7992F, -27.3049F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2917F, AnimationHelper.createRotationalVector(-53.0231F, -26.8304F, -30.5649F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(15.0F, 0.0F, -10.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(22.4229F, 1.9113F, -14.6211F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(38.9206F, -9.562F, -4.6084F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2917F, AnimationHelper.createRotationalVector(51.4206F, -9.562F, -4.6084F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"upper_body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-12.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.125F, AnimationHelper.createRotationalVector(11.1575F, 0.2125F, -2.2036F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.2917F, AnimationHelper.createRotationalVector(25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation INVULNERABLE = Animation.Builder.create(0.2917F)
		.addBoneAnimation(
			"upper_body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.0833F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"upper_body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.0833F, AnimationHelper.createRotationalVector(17.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.0833F, AnimationHelper.createRotationalVector(20.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
}
