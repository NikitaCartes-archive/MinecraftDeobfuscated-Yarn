package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WardenAnimations {
	public static final Animation EMERGING = Animation.Builder.create(6.68F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -22.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.68F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.76F, AnimationHelper.createRotationalVector(25.0F, 0.0F, -7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.92F, AnimationHelper.createRotationalVector(35.0F, 0.0F, -7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.08F, AnimationHelper.createRotationalVector(25.0F, 0.0F, -7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.44F, AnimationHelper.createRotationalVector(47.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.56F, AnimationHelper.createRotationalVector(47.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.68F, AnimationHelper.createRotationalVector(47.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createRotationalVector(70.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.8F, AnimationHelper.createRotationalVector(70.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -63.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createTranslationalVector(0.0F, -56.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.68F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.16F, AnimationHelper.createTranslationalVector(0.0F, -27.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.76F, AnimationHelper.createTranslationalVector(0.0F, -14.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.92F, AnimationHelper.createTranslationalVector(0.0F, -11.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.08F, AnimationHelper.createTranslationalVector(0.0F, -14.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.44F, AnimationHelper.createTranslationalVector(0.0F, -6.0F, -3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.56F, AnimationHelper.createTranslationalVector(0.0F, -4.0F, -3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.68F, AnimationHelper.createTranslationalVector(0.0F, -6.0F, -3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createTranslationalVector(0.0F, -3.0F, -4.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.8F, AnimationHelper.createTranslationalVector(0.0F, -3.0F, -4.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.92F, AnimationHelper.createRotationalVector(0.74F, 0.0F, -40.38F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.16F, AnimationHelper.createRotationalVector(-67.5F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createRotationalVector(-67.5F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.32F, AnimationHelper.createRotationalVector(-47.5F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.4F, AnimationHelper.createRotationalVector(-67.5F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.68F, AnimationHelper.createRotationalVector(-67.5F, 0.0F, 15.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.76F, AnimationHelper.createRotationalVector(-67.5F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.84F, AnimationHelper.createRotationalVector(-52.5F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.92F, AnimationHelper.createRotationalVector(-67.5F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.64F, AnimationHelper.createRotationalVector(-17.5F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.76F, AnimationHelper.createRotationalVector(70.0F, 0.0F, 12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.04F, AnimationHelper.createRotationalVector(70.0F, 0.0F, 12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.12F, AnimationHelper.createRotationalVector(80.0F, 0.0F, 12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.24F, AnimationHelper.createRotationalVector(70.0F, 0.0F, 12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createRotationalVector(77.5F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createTranslationalVector(-8.0F, -11.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.92F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createTranslationalVector(0.0F, 0.47F, -0.95F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.32F, AnimationHelper.createTranslationalVector(0.0F, 0.47F, -0.95F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.4F, AnimationHelper.createTranslationalVector(0.0F, 0.47F, -0.95F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.68F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.76F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.84F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.92F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.64F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, -2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.76F, AnimationHelper.createTranslationalVector(0.0F, -4.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.04F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.12F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.24F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.36F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.56F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.8F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.36F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.56F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.8F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.36F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.56F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.8F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.36F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.56F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.8F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2F, AnimationHelper.createRotationalVector(-152.5F, 2.5F, 7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.68F, AnimationHelper.createRotationalVector(-180.0F, 12.5F, -10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(-90.0F, 12.5F, -10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createRotationalVector(-90.0F, 12.5F, -10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createRotationalVector(-90.0F, 12.5F, -10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.08F, AnimationHelper.createRotationalVector(-95.0F, 12.5F, -10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.24F, AnimationHelper.createRotationalVector(-83.93F, 3.93F, 5.71F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.36F, AnimationHelper.createRotationalVector(-80.0F, 7.5F, 17.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.76F, AnimationHelper.createRotationalVector(-67.5F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.08F, AnimationHelper.createRotationalVector(-67.5F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.44F, AnimationHelper.createRotationalVector(-55.0F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.56F, AnimationHelper.createRotationalVector(-60.0F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.68F, AnimationHelper.createRotationalVector(-55.0F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createRotationalVector(-67.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.56F, AnimationHelper.createRotationalVector(-50.45F, 0.0F, 2.69F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.08F, AnimationHelper.createRotationalVector(-62.72F, 0.0F, 4.3F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(0.0F, -21.0F, 9.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.68F, AnimationHelper.createTranslationalVector(2.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createTranslationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createTranslationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createTranslationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.08F, AnimationHelper.createTranslationalVector(2.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.24F, AnimationHelper.createTranslationalVector(2.0F, 2.71F, 3.86F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.36F, AnimationHelper.createTranslationalVector(2.0F, 1.0F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.76F, AnimationHelper.createTranslationalVector(2.0F, 3.0F, 3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.08F, AnimationHelper.createTranslationalVector(2.0F, 3.0F, 3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.44F, AnimationHelper.createTranslationalVector(2.67F, 4.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.56F, AnimationHelper.createTranslationalVector(2.67F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.68F, AnimationHelper.createTranslationalVector(2.67F, 4.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createTranslationalVector(0.67F, 3.0F, 4.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.12F, AnimationHelper.createRotationalVector(-167.5F, -17.5F, -7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6F, AnimationHelper.createRotationalVector(-167.5F, -17.5F, -7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.88F, AnimationHelper.createRotationalVector(-175.0F, -17.5F, 15.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.16F, AnimationHelper.createRotationalVector(-190.0F, -17.5F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.28F, AnimationHelper.createRotationalVector(-90.0F, -5.0F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.68F, AnimationHelper.createRotationalVector(-90.0F, -17.5F, -12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(-90.0F, -17.5F, -12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createRotationalVector(-90.0F, -17.5F, -12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createRotationalVector(-90.0F, -17.5F, -12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.04F, AnimationHelper.createRotationalVector(-81.29F, -10.64F, -14.21F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.16F, AnimationHelper.createRotationalVector(-83.5F, -5.5F, -15.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.76F, AnimationHelper.createRotationalVector(-62.5F, -7.5F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.92F, AnimationHelper.createRotationalVector(-58.75F, -3.75F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.08F, AnimationHelper.createRotationalVector(-55.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.44F, AnimationHelper.createRotationalVector(-52.5F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.56F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.68F, AnimationHelper.createRotationalVector(-52.5F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createRotationalVector(-72.5F, -2.5F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.56F, AnimationHelper.createRotationalVector(-57.5F, -4.54F, 2.99F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.08F, AnimationHelper.createRotationalVector(-70.99F, -5.77F, 1.78F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.12F, AnimationHelper.createTranslationalVector(0.0F, -8.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6F, AnimationHelper.createTranslationalVector(0.0F, -8.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.88F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(-2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.68F, AnimationHelper.createTranslationalVector(-4.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createTranslationalVector(-4.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createTranslationalVector(-4.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createTranslationalVector(-4.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.04F, AnimationHelper.createTranslationalVector(-3.23F, 5.7F, 4.97F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.16F, AnimationHelper.createTranslationalVector(-1.49F, 2.22F, 5.25F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.76F, AnimationHelper.createTranslationalVector(-1.14F, 1.71F, 1.86F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.92F, AnimationHelper.createTranslationalVector(-1.14F, 1.21F, 3.86F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.08F, AnimationHelper.createTranslationalVector(-1.14F, 2.71F, 4.86F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.44F, AnimationHelper.createTranslationalVector(-1.0F, 1.0F, 3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.56F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.68F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createTranslationalVector(-2.0F, 0.0F, 4.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.36F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.32F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.48F, AnimationHelper.createRotationalVector(55.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.6F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.8F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -63.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createTranslationalVector(0.0F, -56.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.68F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.36F, AnimationHelper.createTranslationalVector(0.0F, -22.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.76F, AnimationHelper.createTranslationalVector(0.0F, -12.28F, 2.48F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.92F, AnimationHelper.createTranslationalVector(0.0F, -9.28F, 2.48F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.08F, AnimationHelper.createTranslationalVector(0.0F, -12.28F, 2.48F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.32F, AnimationHelper.createTranslationalVector(0.0F, -4.14F, 4.14F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.48F, AnimationHelper.createTranslationalVector(0.0F, -0.57F, -8.43F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.6F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.8F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.36F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.84F, AnimationHelper.createRotationalVector(20.0F, 0.0F, -17.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.68F, AnimationHelper.createRotationalVector(20.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.84F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.8F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -63.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.52F, AnimationHelper.createTranslationalVector(0.0F, -56.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.68F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createTranslationalVector(0.0F, -32.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.36F, AnimationHelper.createTranslationalVector(0.0F, -22.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.84F, AnimationHelper.createTranslationalVector(-4.0F, 2.0F, -7.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.0F, AnimationHelper.createTranslationalVector(-4.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.68F, AnimationHelper.createTranslationalVector(-4.0F, 0.0F, -9.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.84F, AnimationHelper.createTranslationalVector(-2.0F, 2.0F, -3.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(5.8F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(6.64F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
	public static final Animation DIGGING = Animation.Builder.create(5.0F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(4.13441F, 0.94736F, 1.2694F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createRotationalVector(54.45407F, -13.53935F, -18.14183F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createRotationalVector(59.46442F, -10.8885F, 35.7954F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(82.28261F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.625F, AnimationHelper.createRotationalVector(53.23606F, 10.04715F, -29.72932F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2083F, AnimationHelper.createRotationalVector(-17.71739F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5417F, AnimationHelper.createRotationalVector(112.28261F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.6667F, AnimationHelper.createRotationalVector(116.06889F, 5.11581F, -24.50117F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.8333F, AnimationHelper.createRotationalVector(121.56244F, -4.17248F, 19.57737F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0417F, AnimationHelper.createRotationalVector(138.5689F, 5.11581F, -24.50117F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.25F, AnimationHelper.createRotationalVector(144.06244F, -4.17248F, 19.57737F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.375F, AnimationHelper.createRotationalVector(147.28261F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.625F, AnimationHelper.createRotationalVector(147.28261F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.875F, AnimationHelper.createRotationalVector(134.36221F, 8.81113F, -8.90172F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.0417F, AnimationHelper.createRotationalVector(132.05966F, -8.35927F, 9.70506F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.25F, AnimationHelper.createRotationalVector(134.36221F, 8.81113F, -8.90172F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.5F, AnimationHelper.createRotationalVector(147.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -16.48454F, -6.5784F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(0.0F, -16.48454F, -6.5784F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0417F, AnimationHelper.createTranslationalVector(0.0F, -16.97F, -7.11F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.625F, AnimationHelper.createTranslationalVector(0.0F, -13.97F, -7.11F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2083F, AnimationHelper.createTranslationalVector(0.0F, -11.48454F, -0.5784F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5417F, AnimationHelper.createTranslationalVector(0.0F, -16.48454F, -6.5784F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.6667F, AnimationHelper.createTranslationalVector(0.0F, -20.27F, -5.42F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.375F, AnimationHelper.createTranslationalVector(0.0F, -21.48454F, -5.5784F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.0417F, AnimationHelper.createTranslationalVector(0.0F, -22.48454F, -5.5784F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.5F, AnimationHelper.createTranslationalVector(0.0F, -40.0F, -8.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.6667F, AnimationHelper.createRotationalVector(12.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.2083F, AnimationHelper.createRotationalVector(12.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.375F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5417F, AnimationHelper.createRotationalVector(67.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.375F, AnimationHelper.createRotationalVector(67.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.375F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-101.8036F, -21.29587F, 30.61478F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createRotationalVector(-101.8036F, -21.29587F, 30.61478F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(48.7585F, -17.61941F, 9.9865F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(48.7585F, -17.61941F, 9.9865F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.4583F, AnimationHelper.createRotationalVector(-101.8036F, -21.29587F, 30.61478F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(-89.04994F, -4.19657F, -1.47845F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2083F, AnimationHelper.createRotationalVector(-158.30728F, 3.7152F, -1.52352F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5417F, AnimationHelper.createRotationalVector(-89.04994F, -4.19657F, -1.47845F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.375F, AnimationHelper.createRotationalVector(-120.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(2.22F, 0.0F, 0.86F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(3.12F, 0.0F, 4.29F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2083F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 4.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.375F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 4.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.2917F, AnimationHelper.createRotationalVector(-63.89288F, -0.52011F, 2.09491F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(-63.89288F, -0.52011F, 2.09491F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createRotationalVector(-62.87857F, 15.15061F, 9.97445F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9167F, AnimationHelper.createRotationalVector(-86.93642F, 17.45026F, 4.05284F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(-86.93642F, 17.45026F, 4.05284F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.4583F, AnimationHelper.createRotationalVector(-86.93642F, 17.45026F, 4.05284F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.6667F, AnimationHelper.createRotationalVector(63.0984F, 8.83573F, -8.71284F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8333F, AnimationHelper.createRotationalVector(35.5984F, 8.83573F, -8.71284F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2083F, AnimationHelper.createRotationalVector(-153.27473F, -0.02953F, 3.5235F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5417F, AnimationHelper.createRotationalVector(-87.07754F, -0.02625F, 3.132F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.375F, AnimationHelper.createRotationalVector(-120.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(-0.28F, 5.0F, 10.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(-1.51F, 4.35F, 4.33F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.9167F, AnimationHelper.createTranslationalVector(-0.6F, 3.61F, 4.63F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(-0.6F, 3.61F, 0.63F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.6667F, AnimationHelper.createTranslationalVector(-2.85F, -0.1F, 3.33F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2083F, AnimationHelper.createTranslationalVector(-1.0F, 0.0F, 4.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.375F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 4.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(113.27F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createRotationalVector(113.27F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.3333F, AnimationHelper.createRotationalVector(113.27F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.5833F, AnimationHelper.createRotationalVector(182.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.8333F, AnimationHelper.createRotationalVector(120.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.0833F, AnimationHelper.createRotationalVector(182.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2917F, AnimationHelper.createRotationalVector(120.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.5F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -13.98F, -2.37F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(0.0F, -13.98F, -2.37F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.3333F, AnimationHelper.createTranslationalVector(0.0F, -13.98F, -2.37F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.5833F, AnimationHelper.createTranslationalVector(0.0F, -7.0F, -3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.8333F, AnimationHelper.createTranslationalVector(0.0F, -9.0F, -3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.0833F, AnimationHelper.createTranslationalVector(0.0F, -16.71F, -3.69F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2917F, AnimationHelper.createTranslationalVector(0.0F, -28.0F, -5.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createRotationalVector(114.98F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createRotationalVector(114.98F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.3333F, AnimationHelper.createRotationalVector(114.98F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.5833F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.8333F, AnimationHelper.createRotationalVector(172.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.0833F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2917F, AnimationHelper.createRotationalVector(197.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.5F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -14.01F, -2.35F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(0.0F, -14.01F, -2.35F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.3333F, AnimationHelper.createTranslationalVector(0.0F, -14.01F, -2.35F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.5833F, AnimationHelper.createTranslationalVector(0.0F, -5.0F, -4.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.8333F, AnimationHelper.createTranslationalVector(0.0F, -7.0F, -4.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.0833F, AnimationHelper.createTranslationalVector(0.0F, -15.5F, -3.76F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2917F, AnimationHelper.createTranslationalVector(0.0F, -28.0F, -5.0F), Transformation.Interpolations.LINEAR)
			)
		)
		.build();
	public static final Animation ROARING = Animation.Builder.create(4.2F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.6F, AnimationHelper.createRotationalVector(32.5F, 0.0F, -7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.84F, AnimationHelper.createRotationalVector(38.33F, 0.0F, 2.99F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.08F, AnimationHelper.createRotationalVector(40.97F, 0.0F, -4.3F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.36F, AnimationHelper.createRotationalVector(44.41F, 0.0F, 6.29F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(47.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.6F, AnimationHelper.createTranslationalVector(0.0F, -3.0F, -6.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, -3.0F, -6.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createRotationalVector(-32.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.6F, AnimationHelper.createRotationalVector(-32.5F, 0.0F, -27.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8F, AnimationHelper.createRotationalVector(-32.5F, 0.0F, 26.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.04F, AnimationHelper.createRotationalVector(-32.5F, 0.0F, -27.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.44F, AnimationHelper.createRotationalVector(-32.5F, 0.0F, 26.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.84F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, -12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.6F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, -6.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, -6.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.48F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, -6.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.76F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.85F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.08F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.4F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.85F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.72F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.85F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.76F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -15.85F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.08F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.4F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -15.85F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.72F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -15.85F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.72F, AnimationHelper.createRotationalVector(-120.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createRotationalVector(-77.5F, 3.75F, 15.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.48F, AnimationHelper.createRotationalVector(67.5F, -32.5F, 20.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.48F, AnimationHelper.createRotationalVector(37.5F, -32.5F, 25.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createRotationalVector(27.6F, -17.1F, 32.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.72F, AnimationHelper.createTranslationalVector(3.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.48F, AnimationHelper.createTranslationalVector(4.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.48F, AnimationHelper.createTranslationalVector(4.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.72F, AnimationHelper.createRotationalVector(-125.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createRotationalVector(-76.25F, -17.5F, -7.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.48F, AnimationHelper.createRotationalVector(62.5F, 42.5F, -12.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.48F, AnimationHelper.createRotationalVector(37.5F, 27.5F, -27.5F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createRotationalVector(25.0F, 18.4F, -30.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.72F, AnimationHelper.createTranslationalVector(-3.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.48F, AnimationHelper.createTranslationalVector(-4.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.48F, AnimationHelper.createTranslationalVector(-4.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(4.2F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
	public static final Animation SNIFFING = Animation.Builder.create(4.16F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.56F, AnimationHelper.createRotationalVector(17.5F, 32.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.96F, AnimationHelper.createRotationalVector(0.0F, 32.5F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.8F, AnimationHelper.createRotationalVector(10.0F, -30.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.32F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.68F, AnimationHelper.createRotationalVector(0.0F, 40.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.96F, AnimationHelper.createRotationalVector(-22.5F, 40.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.24F, AnimationHelper.createRotationalVector(0.0F, 20.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.52F, AnimationHelper.createRotationalVector(-35.0F, 20.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.76F, AnimationHelper.createRotationalVector(0.0F, 20.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.28F, AnimationHelper.createRotationalVector(0.0F, -20.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.88F, AnimationHelper.createRotationalVector(0.0F, -20.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.32F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.96F, AnimationHelper.createRotationalVector(17.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.76F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.32F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.96F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.2F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.76F, AnimationHelper.createRotationalVector(17.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.32F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
	public static final Animation ATTACKING = Animation.Builder.create(0.33333F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.0417F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.2083F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.0417F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.2083F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, -2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.0417F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(-30.17493F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.0417F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, -2.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.0417F, AnimationHelper.createRotationalVector(-120.36119F, 40.78947F, -20.94102F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-90.0F, -45.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.0417F, AnimationHelper.createTranslationalVector(4.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.0417F, AnimationHelper.createRotationalVector(-120.36119F, -40.78947F, 20.94102F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(-61.1632F, 42.85882F, 11.52421F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.0417F, AnimationHelper.createTranslationalVector(-4.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
	public static final Animation CHARGING_SONIC_BOOM = Animation.Builder.create(3.0F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0833F, AnimationHelper.createRotationalVector(47.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.625F, AnimationHelper.createRotationalVector(55.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-32.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.4583F, AnimationHelper.createRotationalVector(-32.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.875F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0833F, AnimationHelper.createTranslationalVector(0.0F, -3.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.625F, AnimationHelper.createTranslationalVector(0.0F, -4.0F, -1.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.9167F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.875F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_ribcage",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.875F, AnimationHelper.createRotationalVector(0.0F, 125.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 125.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_ribcage",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.5417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.875F, AnimationHelper.createRotationalVector(0.0F, -125.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, -125.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(67.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.75F, AnimationHelper.createRotationalVector(80.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.9167F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.7083F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.875F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.9167F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -3.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.875F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.875F, AnimationHelper.createRotationalVector(-42.28659F, -32.69813F, -5.00825F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(-29.83757F, -35.39626F, -45.28089F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(-29.83757F, -35.39626F, -45.28089F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.6667F, AnimationHelper.createRotationalVector(-72.28659F, -32.69813F, -5.00825F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8333F, AnimationHelper.createRotationalVector(35.26439F, -30.0F, 35.26439F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.9167F, AnimationHelper.createRotationalVector(73.75484F, -13.0931F, 19.20518F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(73.75484F, -13.0931F, 19.20518F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.75F, AnimationHelper.createRotationalVector(58.20713F, -21.1064F, 28.7261F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8333F, AnimationHelper.createTranslationalVector(3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.75F, AnimationHelper.createTranslationalVector(3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(0.875F, AnimationHelper.createRotationalVector(-33.80694F, 32.31058F, 6.87997F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.1667F, AnimationHelper.createRotationalVector(-17.87827F, 34.62115F, 49.02433F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(-17.87827F, 34.62115F, 49.02433F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.6667F, AnimationHelper.createRotationalVector(-51.30694F, 32.31058F, 6.87997F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8333F, AnimationHelper.createRotationalVector(35.26439F, 30.0F, -35.26439F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.9167F, AnimationHelper.createRotationalVector(73.75484F, 13.0931F, -19.20518F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(73.75484F, 13.0931F, -19.20518F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.75F, AnimationHelper.createRotationalVector(58.20713F, 21.1064F, -28.7261F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(1.8333F, AnimationHelper.createTranslationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(2.75F, AnimationHelper.createTranslationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
				new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			)
		)
		.build();
}
