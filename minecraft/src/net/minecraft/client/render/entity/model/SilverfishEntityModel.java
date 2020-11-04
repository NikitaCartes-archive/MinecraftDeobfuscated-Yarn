package net.minecraft.client.render.entity.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SilverfishEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart field_27497;
	private final ModelPart[] body = new ModelPart[7];
	private final ModelPart[] scales = new ModelPart[3];
	private static final int[][] segmentLocations = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
	private static final int[][] segmentSizes = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

	public SilverfishEntityModel(ModelPart modelPart) {
		this.field_27497 = modelPart;
		Arrays.setAll(this.body, i -> modelPart.method_32086(method_32045(i)));
		Arrays.setAll(this.scales, i -> modelPart.method_32086(method_32043(i)));
	}

	private static String method_32043(int i) {
		return "layer" + i;
	}

	private static String method_32045(int i) {
		return "segment" + i;
	}

	public static class_5607 method_32042() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		float[] fs = new float[7];
		float f = -3.5F;

		for (int i = 0; i < 7; i++) {
			lv2.method_32117(
				method_32045(i),
				class_5606.method_32108()
					.method_32101(segmentSizes[i][0], segmentSizes[i][1])
					.method_32097(
						(float)segmentLocations[i][0] * -0.5F,
						0.0F,
						(float)segmentLocations[i][2] * -0.5F,
						(float)segmentLocations[i][0],
						(float)segmentLocations[i][1],
						(float)segmentLocations[i][2]
					),
				class_5603.method_32090(0.0F, (float)(24 - segmentLocations[i][1]), f)
			);
			fs[i] = f;
			if (i < 6) {
				f += (float)(segmentLocations[i][2] + segmentLocations[i + 1][2]) * 0.5F;
			}
		}

		lv2.method_32117(
			method_32043(0),
			class_5606.method_32108().method_32101(20, 0).method_32097(-5.0F, 0.0F, (float)segmentLocations[2][2] * -0.5F, 10.0F, 8.0F, (float)segmentLocations[2][2]),
			class_5603.method_32090(0.0F, 16.0F, fs[2])
		);
		lv2.method_32117(
			method_32043(1),
			class_5606.method_32108().method_32101(20, 11).method_32097(-3.0F, 0.0F, (float)segmentLocations[4][2] * -0.5F, 6.0F, 4.0F, (float)segmentLocations[4][2]),
			class_5603.method_32090(0.0F, 20.0F, fs[4])
		);
		lv2.method_32117(
			method_32043(2),
			class_5606.method_32108().method_32101(20, 18).method_32097(-3.0F, 0.0F, (float)segmentLocations[4][2] * -0.5F, 6.0F, 5.0F, (float)segmentLocations[1][2]),
			class_5603.method_32090(0.0F, 19.0F, fs[1])
		);
		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27497;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		for (int i = 0; i < this.body.length; i++) {
			this.body[i].yaw = MathHelper.cos(animationProgress * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.05F * (float)(1 + Math.abs(i - 2));
			this.body[i].pivotX = MathHelper.sin(animationProgress * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * (float)Math.abs(i - 2);
		}

		this.scales[0].yaw = this.body[2].yaw;
		this.scales[1].yaw = this.body[4].yaw;
		this.scales[1].pivotX = this.body[4].pivotX;
		this.scales[2].yaw = this.body[1].yaw;
		this.scales[2].pivotX = this.body[1].pivotX;
	}
}
