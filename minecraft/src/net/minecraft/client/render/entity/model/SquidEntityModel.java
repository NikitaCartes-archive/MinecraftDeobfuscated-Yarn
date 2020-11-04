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

@Environment(EnvType.CLIENT)
public class SquidEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart[] tentacles = new ModelPart[8];
	private final ModelPart field_27513;

	public SquidEntityModel(ModelPart modelPart) {
		this.field_27513 = modelPart;
		Arrays.setAll(this.tentacles, i -> modelPart.method_32086(method_32056(i)));
	}

	private static String method_32056(int i) {
		return "tentacle" + i;
	}

	public static class_5607 method_32055() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		int i = -16;
		lv2.method_32117(
			"body", class_5606.method_32108().method_32101(0, 0).method_32097(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F), class_5603.method_32090(0.0F, 8.0F, 0.0F)
		);
		int j = 8;
		class_5606 lv3 = class_5606.method_32108().method_32101(48, 0).method_32097(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);

		for (int k = 0; k < 8; k++) {
			double d = (double)k * Math.PI * 2.0 / 8.0;
			float f = (float)Math.cos(d) * 5.0F;
			float g = 15.0F;
			float h = (float)Math.sin(d) * 5.0F;
			d = (double)k * Math.PI * -2.0 / 8.0 + (Math.PI / 2);
			float l = (float)d;
			lv2.method_32117(method_32056(k), lv3, class_5603.method_32091(f, 15.0F, h, 0.0F, l, 0.0F));
		}

		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		for (ModelPart modelPart : this.tentacles) {
			modelPart.pitch = animationProgress;
		}
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27513;
	}
}
