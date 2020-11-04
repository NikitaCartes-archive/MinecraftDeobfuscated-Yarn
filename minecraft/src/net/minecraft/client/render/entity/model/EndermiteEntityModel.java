package net.minecraft.client.render.entity.model;

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
public class EndermiteEntityModel<T extends Entity> extends class_5597<T> {
	private static final int[][] field_3366 = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
	private static final int[][] field_3369 = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
	private final ModelPart field_27413;
	private final ModelPart[] field_3368;

	public EndermiteEntityModel(ModelPart modelPart) {
		this.field_27413 = modelPart;
		this.field_3368 = new ModelPart[4];

		for (int i = 0; i < 4; i++) {
			this.field_3368[i] = modelPart.method_32086(method_31997(i));
		}
	}

	private static String method_31997(int i) {
		return "segment" + i;
	}

	public static class_5607 method_31996() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		float f = -3.5F;

		for (int i = 0; i < 4; i++) {
			lv2.method_32117(
				method_31997(i),
				class_5606.method_32108()
					.method_32101(field_3369[i][0], field_3369[i][1])
					.method_32097(
						(float)field_3366[i][0] * -0.5F, 0.0F, (float)field_3366[i][2] * -0.5F, (float)field_3366[i][0], (float)field_3366[i][1], (float)field_3366[i][2]
					),
				class_5603.method_32090(0.0F, (float)(24 - field_3366[i][1]), f)
			);
			if (i < 3) {
				f += (float)(field_3366[i][2] + field_3366[i + 1][2]) * 0.5F;
			}
		}

		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27413;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		for (int i = 0; i < this.field_3368.length; i++) {
			this.field_3368[i].yaw = MathHelper.cos(animationProgress * 0.9F + (float)i * 0.15F * (float) Math.PI)
				* (float) Math.PI
				* 0.01F
				* (float)(1 + Math.abs(i - 2));
			this.field_3368[i].pivotX = MathHelper.sin(animationProgress * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.1F * (float)Math.abs(i - 2);
		}
	}
}
