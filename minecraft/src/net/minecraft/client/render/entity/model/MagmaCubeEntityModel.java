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
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MagmaCubeEntityModel<T extends SlimeEntity> extends class_5597<T> {
	private final ModelPart field_27441;
	private final ModelPart[] field_3427 = new ModelPart[8];

	public MagmaCubeEntityModel(ModelPart modelPart) {
		this.field_27441 = modelPart;
		Arrays.setAll(this.field_3427, i -> modelPart.method_32086(method_32015(i)));
	}

	private static String method_32015(int i) {
		return "cube" + i;
	}

	public static class_5607 method_32014() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();

		for (int i = 0; i < 8; i++) {
			int j = 0;
			int k = i;
			if (i == 2) {
				j = 24;
				k = 10;
			} else if (i == 3) {
				j = 24;
				k = 19;
			}

			lv2.method_32117(
				method_32015(i), class_5606.method_32108().method_32101(j, k).method_32097(-4.0F, (float)(16 + i), -4.0F, 8.0F, 1.0F, 8.0F), class_5603.field_27701
			);
		}

		lv2.method_32117("inside_cube", class_5606.method_32108().method_32101(0, 16).method_32097(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F), class_5603.field_27701);
		return class_5607.method_32110(lv, 64, 32);
	}

	public void setAngles(T slimeEntity, float f, float g, float h, float i, float j) {
	}

	public void animateModel(T slimeEntity, float f, float g, float h) {
		float i = MathHelper.lerp(h, slimeEntity.lastStretch, slimeEntity.stretch);
		if (i < 0.0F) {
			i = 0.0F;
		}

		for (int j = 0; j < this.field_3427.length; j++) {
			this.field_3427[j].pivotY = (float)(-(4 - j)) * i * 1.7F;
		}
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27441;
	}
}
