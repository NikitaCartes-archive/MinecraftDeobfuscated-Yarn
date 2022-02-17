package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_7019;

@Environment(EnvType.CLIENT)
public class Animations {
	public static final Animation field_36957 = Animation.Builder.create(6.68F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40909(0.0F, 0.0F, -22.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.2F, class_7019.method_40909(0.0F, 0.0F, -7.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.68F, class_7019.method_40909(0.0F, 0.0F, 10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.8F, class_7019.method_40909(0.0F, 0.0F, 10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40909(0.0F, 0.0F, 10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40909(0.0F, 0.0F, 10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.76F, class_7019.method_40909(25.0F, 0.0F, -7.5F), Transformation.Interpolations.field_36950),
				new Keyframe(3.92F, class_7019.method_40909(35.0F, 0.0F, -7.5F), Transformation.Interpolations.field_36950),
				new Keyframe(4.08F, class_7019.method_40909(25.0F, 0.0F, -7.5F), Transformation.Interpolations.field_36950),
				new Keyframe(4.44F, class_7019.method_40909(47.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.56F, class_7019.method_40909(47.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.68F, class_7019.method_40909(47.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40909(70.0F, 0.0F, 2.5F), Transformation.Interpolations.field_36950),
				new Keyframe(5.8F, class_7019.method_40909(70.0F, 0.0F, 2.5F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, -63.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40903(0.0F, -56.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.2F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.68F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.8F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.16F, class_7019.method_40903(0.0F, -27.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.76F, class_7019.method_40903(0.0F, -14.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.92F, class_7019.method_40903(0.0F, -11.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.08F, class_7019.method_40903(0.0F, -14.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.44F, class_7019.method_40903(0.0F, -6.0F, -3.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.56F, class_7019.method_40903(0.0F, -4.0F, -3.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.68F, class_7019.method_40903(0.0F, -6.0F, -3.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40903(0.0F, -3.0F, -4.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.8F, class_7019.method_40903(0.0F, -3.0F, -4.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.92F, class_7019.method_40909(0.74F, 0.0F, -40.38F), Transformation.Interpolations.field_36950),
				new Keyframe(1.16F, class_7019.method_40909(-67.5F, 0.0F, -2.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40909(-67.5F, 0.0F, -2.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.32F, class_7019.method_40909(-47.5F, 0.0F, -2.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.4F, class_7019.method_40909(-67.5F, 0.0F, -2.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.68F, class_7019.method_40909(-67.5F, 0.0F, 15.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.76F, class_7019.method_40909(-67.5F, 0.0F, -5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.84F, class_7019.method_40909(-52.5F, 0.0F, -5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.92F, class_7019.method_40909(-67.5F, 0.0F, -5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.64F, class_7019.method_40909(-17.5F, 0.0F, -10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.76F, class_7019.method_40909(70.0F, 0.0F, 12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(4.04F, class_7019.method_40909(70.0F, 0.0F, 12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(4.12F, class_7019.method_40909(80.0F, 0.0F, 12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(4.24F, class_7019.method_40909(70.0F, 0.0F, 12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40909(77.5F, 0.0F, -2.5F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40903(-8.0F, -11.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.92F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40903(0.0F, 0.47F, -0.95F), Transformation.Interpolations.field_36950),
				new Keyframe(1.32F, class_7019.method_40903(0.0F, 0.47F, -0.95F), Transformation.Interpolations.field_36950),
				new Keyframe(1.4F, class_7019.method_40903(0.0F, 0.47F, -0.95F), Transformation.Interpolations.field_36950),
				new Keyframe(1.68F, class_7019.method_40903(0.0F, 1.0F, -2.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.76F, class_7019.method_40903(0.0F, 1.0F, -2.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.84F, class_7019.method_40903(0.0F, 1.0F, -2.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.92F, class_7019.method_40903(0.0F, 1.0F, -2.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.64F, class_7019.method_40903(0.0F, -2.0F, -2.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.76F, class_7019.method_40903(0.0F, -4.0F, 1.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.04F, class_7019.method_40903(0.0F, -1.0F, 1.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.12F, class_7019.method_40903(0.0F, -1.0F, 1.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.24F, class_7019.method_40903(0.0F, -1.0F, 1.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40903(0.0F, -1.0F, 1.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.36F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.56F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.8F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.36F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.56F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.8F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.36F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.56F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.8F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.36F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.56F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.8F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.2F, class_7019.method_40909(-152.5F, 2.5F, 7.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.68F, class_7019.method_40909(-180.0F, 12.5F, -10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.8F, class_7019.method_40909(-90.0F, 12.5F, -10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40909(-90.0F, 12.5F, -10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40909(-90.0F, 12.5F, -10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.08F, class_7019.method_40909(-95.0F, 12.5F, -10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.24F, class_7019.method_40909(-83.93F, 3.93F, 5.71F), Transformation.Interpolations.field_36950),
				new Keyframe(3.36F, class_7019.method_40909(-80.0F, 7.5F, 17.5F), Transformation.Interpolations.field_36950),
				new Keyframe(3.76F, class_7019.method_40909(-67.5F, 2.5F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.08F, class_7019.method_40909(-67.5F, 2.5F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.44F, class_7019.method_40909(-55.0F, 2.5F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.56F, class_7019.method_40909(-60.0F, 2.5F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.68F, class_7019.method_40909(-55.0F, 2.5F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40909(-67.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.56F, class_7019.method_40909(-50.45F, 0.0F, 2.69F), Transformation.Interpolations.field_36950),
				new Keyframe(6.08F, class_7019.method_40909(-62.72F, 0.0F, 4.3F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.2F, class_7019.method_40903(0.0F, -21.0F, 9.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.68F, class_7019.method_40903(2.0F, -2.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.8F, class_7019.method_40903(2.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40903(2.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40903(2.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.08F, class_7019.method_40903(2.0F, -2.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.24F, class_7019.method_40903(2.0F, 2.71F, 3.86F), Transformation.Interpolations.field_36950),
				new Keyframe(3.36F, class_7019.method_40903(2.0F, 1.0F, 5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.76F, class_7019.method_40903(2.0F, 3.0F, 3.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.08F, class_7019.method_40903(2.0F, 3.0F, 3.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.44F, class_7019.method_40903(2.67F, 4.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.56F, class_7019.method_40903(2.67F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.68F, class_7019.method_40903(2.67F, 4.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40903(0.67F, 3.0F, 4.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.12F, class_7019.method_40909(-167.5F, -17.5F, -7.5F), Transformation.Interpolations.field_36950),
				new Keyframe(0.6F, class_7019.method_40909(-167.5F, -17.5F, -7.5F), Transformation.Interpolations.field_36950),
				new Keyframe(0.88F, class_7019.method_40909(-175.0F, -17.5F, 15.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.16F, class_7019.method_40909(-190.0F, -17.5F, 5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.28F, class_7019.method_40909(-90.0F, -5.0F, 5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.68F, class_7019.method_40909(-90.0F, -17.5F, -12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.8F, class_7019.method_40909(-90.0F, -17.5F, -12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40909(-90.0F, -17.5F, -12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40909(-90.0F, -17.5F, -12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(3.04F, class_7019.method_40909(-81.29F, -10.64F, -14.21F), Transformation.Interpolations.field_36950),
				new Keyframe(3.16F, class_7019.method_40909(-83.5F, -5.5F, -15.5F), Transformation.Interpolations.field_36950),
				new Keyframe(3.76F, class_7019.method_40909(-62.5F, -7.5F, 5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.92F, class_7019.method_40909(-58.75F, -3.75F, 5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.08F, class_7019.method_40909(-55.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.44F, class_7019.method_40909(-52.5F, 0.0F, 5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.56F, class_7019.method_40909(-50.0F, 0.0F, 5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.68F, class_7019.method_40909(-52.5F, 0.0F, 5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40909(-72.5F, -2.5F, 5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.56F, class_7019.method_40909(-57.5F, -4.54F, 2.99F), Transformation.Interpolations.field_36950),
				new Keyframe(6.08F, class_7019.method_40909(-70.99F, -5.77F, 1.78F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.12F, class_7019.method_40903(0.0F, -8.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.6F, class_7019.method_40903(0.0F, -8.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.88F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.2F, class_7019.method_40903(-2.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.68F, class_7019.method_40903(-4.0F, 3.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.8F, class_7019.method_40903(-4.0F, 3.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40903(-4.0F, 3.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40903(-4.0F, 3.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.04F, class_7019.method_40903(-3.23F, 5.7F, 4.97F), Transformation.Interpolations.field_36950),
				new Keyframe(3.16F, class_7019.method_40903(-1.49F, 2.22F, 5.25F), Transformation.Interpolations.field_36950),
				new Keyframe(3.76F, class_7019.method_40903(-1.14F, 1.71F, 1.86F), Transformation.Interpolations.field_36950),
				new Keyframe(3.92F, class_7019.method_40903(-1.14F, 1.21F, 3.86F), Transformation.Interpolations.field_36950),
				new Keyframe(4.08F, class_7019.method_40903(-1.14F, 2.71F, 4.86F), Transformation.Interpolations.field_36950),
				new Keyframe(4.44F, class_7019.method_40903(-1.0F, 1.0F, 3.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.56F, class_7019.method_40903(0.0F, 1.0F, 1.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.68F, class_7019.method_40903(0.0F, 1.0F, 3.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40903(-2.0F, 0.0F, 4.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.36F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.32F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.48F, class_7019.method_40909(55.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.6F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.8F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, -63.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40903(0.0F, -56.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.2F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.68F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.8F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.36F, class_7019.method_40903(0.0F, -22.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.76F, class_7019.method_40903(0.0F, -12.28F, 2.48F), Transformation.Interpolations.field_36950),
				new Keyframe(3.92F, class_7019.method_40903(0.0F, -9.28F, 2.48F), Transformation.Interpolations.field_36950),
				new Keyframe(4.08F, class_7019.method_40903(0.0F, -12.28F, 2.48F), Transformation.Interpolations.field_36950),
				new Keyframe(4.32F, class_7019.method_40903(0.0F, -4.14F, 4.14F), Transformation.Interpolations.field_36950),
				new Keyframe(4.48F, class_7019.method_40903(0.0F, -0.57F, -8.43F), Transformation.Interpolations.field_36950),
				new Keyframe(4.6F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.8F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.36F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.84F, class_7019.method_40909(20.0F, 0.0F, -17.5F), Transformation.Interpolations.field_36950),
				new Keyframe(4.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.68F, class_7019.method_40909(20.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.84F, class_7019.method_40909(10.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.8F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, -63.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.52F, class_7019.method_40903(0.0F, -56.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.2F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.68F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.8F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40903(0.0F, -32.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.36F, class_7019.method_40903(0.0F, -22.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.84F, class_7019.method_40903(-4.0F, 2.0F, -7.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.0F, class_7019.method_40903(-4.0F, 0.0F, -5.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.68F, class_7019.method_40903(-4.0F, 0.0F, -9.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.84F, class_7019.method_40903(-2.0F, 2.0F, -3.5F), Transformation.Interpolations.field_36950),
				new Keyframe(5.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(5.8F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(6.64F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.build();
	public static final Animation field_36958 = Animation.Builder.create(5.0F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.25F, class_7019.method_40909(4.13441F, 0.94736F, 1.2694F), Transformation.Interpolations.field_36950),
				new Keyframe(0.5F, class_7019.method_40909(50.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.7083F, class_7019.method_40909(54.45407F, -13.53935F, -18.14183F), Transformation.Interpolations.field_36950),
				new Keyframe(1.0417F, class_7019.method_40909(59.46442F, -10.8885F, 35.7954F), Transformation.Interpolations.field_36950),
				new Keyframe(1.3333F, class_7019.method_40909(82.28261F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.625F, class_7019.method_40909(53.23606F, 10.04715F, -29.72932F), Transformation.Interpolations.field_36950),
				new Keyframe(2.2083F, class_7019.method_40909(-17.71739F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.5417F, class_7019.method_40909(112.28261F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.6667F, class_7019.method_40909(116.06889F, 5.11581F, -24.50117F), Transformation.Interpolations.field_36950),
				new Keyframe(2.8333F, class_7019.method_40909(121.56244F, -4.17248F, 19.57737F), Transformation.Interpolations.field_36950),
				new Keyframe(3.0417F, class_7019.method_40909(138.5689F, 5.11581F, -24.50117F), Transformation.Interpolations.field_36950),
				new Keyframe(3.25F, class_7019.method_40909(144.06244F, -4.17248F, 19.57737F), Transformation.Interpolations.field_36950),
				new Keyframe(3.375F, class_7019.method_40909(147.28261F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.625F, class_7019.method_40909(147.28261F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.875F, class_7019.method_40909(134.36221F, 8.81113F, -8.90172F), Transformation.Interpolations.field_36950),
				new Keyframe(4.0417F, class_7019.method_40909(132.05966F, -8.35927F, 9.70506F), Transformation.Interpolations.field_36950),
				new Keyframe(4.25F, class_7019.method_40909(134.36221F, 8.81113F, -8.90172F), Transformation.Interpolations.field_36950),
				new Keyframe(4.5F, class_7019.method_40909(147.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36949)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.5F, class_7019.method_40903(0.0F, -16.48454F, -6.5784F), Transformation.Interpolations.field_36950),
				new Keyframe(0.7083F, class_7019.method_40903(0.0F, -16.48454F, -6.5784F), Transformation.Interpolations.field_36950),
				new Keyframe(1.0417F, class_7019.method_40903(0.0F, -16.97F, -7.11F), Transformation.Interpolations.field_36950),
				new Keyframe(1.625F, class_7019.method_40903(0.0F, -13.97F, -7.11F), Transformation.Interpolations.field_36950),
				new Keyframe(2.2083F, class_7019.method_40903(0.0F, -11.48454F, -0.5784F), Transformation.Interpolations.field_36950),
				new Keyframe(2.5417F, class_7019.method_40903(0.0F, -16.48454F, -6.5784F), Transformation.Interpolations.field_36950),
				new Keyframe(2.6667F, class_7019.method_40903(0.0F, -20.27F, -5.42F), Transformation.Interpolations.field_36950),
				new Keyframe(3.375F, class_7019.method_40903(0.0F, -21.48454F, -5.5784F), Transformation.Interpolations.field_36950),
				new Keyframe(4.0417F, class_7019.method_40903(0.0F, -22.48454F, -5.5784F), Transformation.Interpolations.field_36950),
				new Keyframe(4.5F, class_7019.method_40903(0.0F, -40.0F, -8.0F), Transformation.Interpolations.field_36949)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.6667F, class_7019.method_40909(12.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.2083F, class_7019.method_40909(12.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.75F, class_7019.method_40909(45.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.375F, class_7019.method_40909(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.5417F, class_7019.method_40909(67.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.375F, class_7019.method_40909(67.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.375F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36949)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.5F, class_7019.method_40909(-101.8036F, -21.29587F, 30.61478F), Transformation.Interpolations.field_36950),
				new Keyframe(0.7083F, class_7019.method_40909(-101.8036F, -21.29587F, 30.61478F), Transformation.Interpolations.field_36950),
				new Keyframe(1.0F, class_7019.method_40909(48.7585F, -17.61941F, 9.9865F), Transformation.Interpolations.field_36950),
				new Keyframe(1.1667F, class_7019.method_40909(48.7585F, -17.61941F, 9.9865F), Transformation.Interpolations.field_36950),
				new Keyframe(1.4583F, class_7019.method_40909(-101.8036F, -21.29587F, 30.61478F), Transformation.Interpolations.field_36950),
				new Keyframe(1.75F, class_7019.method_40909(-89.04994F, -4.19657F, -1.47845F), Transformation.Interpolations.field_36950),
				new Keyframe(2.2083F, class_7019.method_40909(-158.30728F, 3.7152F, -1.52352F), Transformation.Interpolations.field_36950),
				new Keyframe(2.5417F, class_7019.method_40909(-89.04994F, -4.19657F, -1.47845F), Transformation.Interpolations.field_36950),
				new Keyframe(4.375F, class_7019.method_40909(-120.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.7083F, class_7019.method_40903(2.22F, 0.0F, 0.86F), Transformation.Interpolations.field_36950),
				new Keyframe(1.0F, class_7019.method_40903(3.12F, 0.0F, 4.29F), Transformation.Interpolations.field_36950),
				new Keyframe(2.2083F, class_7019.method_40903(1.0F, 0.0F, 4.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.375F, class_7019.method_40903(0.0F, 0.0F, 4.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.2917F, class_7019.method_40909(-63.89288F, -0.52011F, 2.09491F), Transformation.Interpolations.field_36950),
				new Keyframe(0.5F, class_7019.method_40909(-63.89288F, -0.52011F, 2.09491F), Transformation.Interpolations.field_36950),
				new Keyframe(0.7083F, class_7019.method_40909(-62.87857F, 15.15061F, 9.97445F), Transformation.Interpolations.field_36950),
				new Keyframe(0.9167F, class_7019.method_40909(-86.93642F, 17.45026F, 4.05284F), Transformation.Interpolations.field_36950),
				new Keyframe(1.1667F, class_7019.method_40909(-86.93642F, 17.45026F, 4.05284F), Transformation.Interpolations.field_36950),
				new Keyframe(1.4583F, class_7019.method_40909(-86.93642F, 17.45026F, 4.05284F), Transformation.Interpolations.field_36950),
				new Keyframe(1.6667F, class_7019.method_40909(63.0984F, 8.83573F, -8.71284F), Transformation.Interpolations.field_36950),
				new Keyframe(1.8333F, class_7019.method_40909(35.5984F, 8.83573F, -8.71284F), Transformation.Interpolations.field_36950),
				new Keyframe(2.2083F, class_7019.method_40909(-153.27473F, -0.02953F, 3.5235F), Transformation.Interpolations.field_36950),
				new Keyframe(2.5417F, class_7019.method_40909(-87.07754F, -0.02625F, 3.132F), Transformation.Interpolations.field_36950),
				new Keyframe(4.375F, class_7019.method_40909(-120.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36949)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.5F, class_7019.method_40903(-0.28F, 5.0F, 10.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.7083F, class_7019.method_40903(-1.51F, 4.35F, 4.33F), Transformation.Interpolations.field_36950),
				new Keyframe(0.9167F, class_7019.method_40903(-0.6F, 3.61F, 4.63F), Transformation.Interpolations.field_36950),
				new Keyframe(1.1667F, class_7019.method_40903(-0.6F, 3.61F, 0.63F), Transformation.Interpolations.field_36950),
				new Keyframe(1.6667F, class_7019.method_40903(-2.85F, -0.1F, 3.33F), Transformation.Interpolations.field_36950),
				new Keyframe(2.2083F, class_7019.method_40903(-1.0F, 0.0F, 4.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.375F, class_7019.method_40903(0.0F, 0.0F, 4.0F), Transformation.Interpolations.field_36949)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.5F, class_7019.method_40909(113.27F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.7083F, class_7019.method_40909(113.27F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.3333F, class_7019.method_40909(113.27F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.5833F, class_7019.method_40909(182.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.8333F, class_7019.method_40909(120.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.0833F, class_7019.method_40909(182.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2917F, class_7019.method_40909(120.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.5F, class_7019.method_40909(90.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36949)
			)
		)
		.addBoneAnimation(
			"right_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.5F, class_7019.method_40903(0.0F, -13.98F, -2.37F), Transformation.Interpolations.field_36950),
				new Keyframe(0.7083F, class_7019.method_40903(0.0F, -13.98F, -2.37F), Transformation.Interpolations.field_36950),
				new Keyframe(3.3333F, class_7019.method_40903(0.0F, -13.98F, -2.37F), Transformation.Interpolations.field_36950),
				new Keyframe(3.5833F, class_7019.method_40903(0.0F, -7.0F, -3.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.8333F, class_7019.method_40903(0.0F, -9.0F, -3.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.0833F, class_7019.method_40903(0.0F, -16.71F, -3.69F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2917F, class_7019.method_40903(0.0F, -28.0F, -5.0F), Transformation.Interpolations.field_36949)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.5F, class_7019.method_40909(114.98F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.7083F, class_7019.method_40909(114.98F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.3333F, class_7019.method_40909(114.98F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.5833F, class_7019.method_40909(90.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.8333F, class_7019.method_40909(172.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.0833F, class_7019.method_40909(90.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2917F, class_7019.method_40909(197.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.5F, class_7019.method_40909(90.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36949)
			)
		)
		.addBoneAnimation(
			"left_leg",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.5F, class_7019.method_40903(0.0F, -14.01F, -2.35F), Transformation.Interpolations.field_36950),
				new Keyframe(0.7083F, class_7019.method_40903(0.0F, -14.01F, -2.35F), Transformation.Interpolations.field_36950),
				new Keyframe(3.3333F, class_7019.method_40903(0.0F, -14.01F, -2.35F), Transformation.Interpolations.field_36950),
				new Keyframe(3.5833F, class_7019.method_40903(0.0F, -5.0F, -4.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.8333F, class_7019.method_40903(0.0F, -7.0F, -4.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.0833F, class_7019.method_40903(0.0F, -15.5F, -3.76F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2917F, class_7019.method_40903(0.0F, -28.0F, -5.0F), Transformation.Interpolations.field_36949)
			)
		)
		.build();
	public static final Animation field_36959 = Animation.Builder.create(4.2F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40909(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.6F, class_7019.method_40909(32.5F, 0.0F, -7.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.84F, class_7019.method_40909(38.33F, 0.0F, 2.99F), Transformation.Interpolations.field_36950),
				new Keyframe(2.08F, class_7019.method_40909(40.97F, 0.0F, -4.3F), Transformation.Interpolations.field_36950),
				new Keyframe(2.36F, class_7019.method_40909(44.41F, 0.0F, 6.29F), Transformation.Interpolations.field_36950),
				new Keyframe(3.0F, class_7019.method_40909(47.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40903(0.0F, -1.0F, 3.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.6F, class_7019.method_40903(0.0F, -3.0F, -6.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.0F, class_7019.method_40903(0.0F, -3.0F, -6.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40909(-32.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.6F, class_7019.method_40909(-32.5F, 0.0F, -27.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.8F, class_7019.method_40909(-32.5F, 0.0F, 26.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.04F, class_7019.method_40909(-32.5F, 0.0F, -27.5F), Transformation.Interpolations.field_36950),
				new Keyframe(2.44F, class_7019.method_40909(-32.5F, 0.0F, 26.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.84F, class_7019.method_40909(-5.0F, 0.0F, -12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.6F, class_7019.method_40903(0.0F, -2.0F, -6.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.2F, class_7019.method_40903(0.0F, -2.0F, -6.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.48F, class_7019.method_40903(0.0F, -2.0F, -6.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.76F, class_7019.method_40909(0.0F, 0.0F, -10.85F), Transformation.Interpolations.field_36950),
				new Keyframe(2.08F, class_7019.method_40909(0.0F, 0.0F, 12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(2.4F, class_7019.method_40909(0.0F, 0.0F, -10.85F), Transformation.Interpolations.field_36950),
				new Keyframe(2.72F, class_7019.method_40909(0.0F, 0.0F, 12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(3.0F, class_7019.method_40909(0.0F, 0.0F, -10.85F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_ear",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.76F, class_7019.method_40909(0.0F, 0.0F, -15.85F), Transformation.Interpolations.field_36950),
				new Keyframe(2.08F, class_7019.method_40909(0.0F, 0.0F, 12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(2.4F, class_7019.method_40909(0.0F, 0.0F, -15.85F), Transformation.Interpolations.field_36950),
				new Keyframe(2.72F, class_7019.method_40909(0.0F, 0.0F, 12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(3.0F, class_7019.method_40909(0.0F, 0.0F, -15.85F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.72F, class_7019.method_40909(-120.0F, 0.0F, -20.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40909(-77.5F, 3.75F, 15.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.48F, class_7019.method_40909(67.5F, -32.5F, 20.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.48F, class_7019.method_40909(37.5F, -32.5F, 25.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40909(27.6F, -17.1F, 32.5F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.72F, class_7019.method_40903(3.0F, -2.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.48F, class_7019.method_40903(4.0F, -2.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.48F, class_7019.method_40903(4.0F, -2.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.72F, class_7019.method_40909(-125.0F, 0.0F, 20.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40909(-76.25F, -17.5F, -7.5F), Transformation.Interpolations.field_36950),
				new Keyframe(1.48F, class_7019.method_40909(62.5F, 42.5F, -12.5F), Transformation.Interpolations.field_36950),
				new Keyframe(2.48F, class_7019.method_40909(37.5F, 27.5F, -27.5F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40909(25.0F, 18.4F, -30.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.72F, class_7019.method_40903(-3.0F, -2.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.48F, class_7019.method_40903(-4.0F, -2.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.48F, class_7019.method_40903(-4.0F, -2.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(4.2F, class_7019.method_40903(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.build();
	public static final Animation field_36960 = Animation.Builder.create(4.16F)
		.addBoneAnimation(
			"body",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.56F, class_7019.method_40909(17.5F, 32.5F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.96F, class_7019.method_40909(0.0F, 32.5F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.2F, class_7019.method_40909(10.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.8F, class_7019.method_40909(10.0F, -30.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.32F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"head",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.68F, class_7019.method_40909(0.0F, 40.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.96F, class_7019.method_40909(-22.5F, 40.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.24F, class_7019.method_40909(0.0F, 20.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.52F, class_7019.method_40909(-35.0F, 20.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(1.76F, class_7019.method_40909(0.0F, 20.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.28F, class_7019.method_40909(0.0F, -20.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.88F, class_7019.method_40909(0.0F, -20.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.32F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"right_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.96F, class_7019.method_40909(17.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.2F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.76F, class_7019.method_40909(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.32F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.addBoneAnimation(
			"left_arm",
			new Transformation(
				Transformation.Targets.ROTATE,
				new Keyframe(0.0F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(0.96F, class_7019.method_40909(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.2F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(2.76F, class_7019.method_40909(17.5F, 0.0F, 0.0F), Transformation.Interpolations.field_36950),
				new Keyframe(3.32F, class_7019.method_40909(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_36950)
			)
		)
		.build();
}
